package com.guanyu.shortlink.service.impl;

import com.guanyu.shortlink.common.Base62;
import com.guanyu.shortlink.domain.po.Link;
import com.guanyu.shortlink.mapper.LinkMapper;
import com.guanyu.shortlink.service.ILinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 短链接表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements ILinkService {


    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "shortlink:code:";
    private static final String EMPTY_VALUE = "__NULL__";

    private final DefaultRedisScript<Long> unlockScript;

    @Override
    public String create(String originUrl) {
        if (originUrl == null || originUrl.isBlank()) {
            throw new IllegalArgumentException("originUrl 不能为空");
        }
        if (!originUrl.startsWith("http://") && !originUrl.startsWith("https://")) {
            throw new IllegalArgumentException("只支持 http/https 链接");
        }

        Link link = new Link();
        link.setOriginUrl(originUrl);
        link.setGroupId("default");
        link.setEnable(1);

        // 先插入，MySQL 自增生成 id
        this.save(link);

        // 用 id 生成短码
        String code = Base62.encode(link.getId());
        link.setShortCode(code);

        // 回填短码
        this.updateById(link);

        log.info("创建短链 id={} code={} url={}", link.getId(), code, originUrl);
        return code;
    }

    @Override
    public String getOriginUrl(String code) {
        String cacheKey = CACHE_PREFIX + code;

        // 1. 查缓存
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            if (EMPTY_VALUE.equals(cached)) {
                throw new IllegalArgumentException("短链接不存在");
            }
            log.info("命中缓存 code={}", code);
            return cached;
        }

        // 2. 缓存未命中，尝试加锁防击穿
        String lockKey = "shortlink:lock:" + code;
        String lockValue = UUID.randomUUID().toString();

        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, 5, TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(locked)) {
            // 没抢到锁：最多等 250ms，期间反复尝试读缓存
            log.info("未获取到锁，等待重试 code={}", code);

            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null && !EMPTY_VALUE.equals(cached)) {
                    log.info("等待后命中缓存 code={} 第{}次", code, i + 1);
                    return cached;
                }
            }
            throw new IllegalArgumentException("系统繁忙，请稍后重试");
        }

        try {
            // 双重检查：可能在等锁期间别人已经写好了
            cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                if (EMPTY_VALUE.equals(cached)) {
                    throw new IllegalArgumentException("短链接不存在");
                }
                return cached;
            }

            // 3. 查库
            Link link = this.lambdaQuery()
                    .eq(Link::getShortCode, code)
                    .eq(Link::getEnable, 1)
                    .one();

            boolean expired = link != null
                    && link.getExpireTime() != null
                    && link.getExpireTime().isBefore(LocalDateTime.now());

            if (link == null || expired) {
                redisTemplate.opsForValue()
                        .set(cacheKey, EMPTY_VALUE, 60, TimeUnit.SECONDS);
                throw new IllegalArgumentException("短链接不存在或已过期");
            }

            // 4. 写缓存
            long ttl = 3600 + ThreadLocalRandom.current().nextInt(600);
            redisTemplate.opsForValue()
                    .set(cacheKey, link.getOriginUrl(), ttl, TimeUnit.SECONDS);

            log.info("查库并写缓存 code={} -> {}", code, link.getOriginUrl());
            return link.getOriginUrl();

        } finally {
            // 5. 释放锁（Lua 保证原子 + 校验 value）
            redisTemplate.execute(unlockScript,
                    List.of(lockKey), lockValue);
        }
    }
}
