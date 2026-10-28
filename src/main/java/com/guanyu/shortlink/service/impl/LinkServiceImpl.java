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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        // 1. 先查缓存
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            if (EMPTY_VALUE.equals(cached)) {
                throw new IllegalArgumentException("短链接不存在");
            }
            log.info("命中缓存 code={}", code);
            return cached;
        }

        // 2. 缓存未命中，查数据库
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

        // 3. 写回缓存，随机 TTL 防雪崩
        long ttl = 3600 + ThreadLocalRandom.current().nextInt(600);
        redisTemplate.opsForValue()
                .set(cacheKey, link.getOriginUrl(), ttl, TimeUnit.SECONDS);

        log.info("查库并写缓存 code={} -> {}", code, link.getOriginUrl());
        return link.getOriginUrl();
    }
}
