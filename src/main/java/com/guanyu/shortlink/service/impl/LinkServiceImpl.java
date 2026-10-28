package com.guanyu.shortlink.service.impl;

import com.guanyu.shortlink.common.Base62;
import com.guanyu.shortlink.domain.po.Link;
import com.guanyu.shortlink.mapper.LinkMapper;
import com.guanyu.shortlink.service.ILinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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


    private  final LinkMapper linkMapper;

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
        Link link = this.lambdaQuery()
                .eq(Link::getShortCode, code)
                .eq(Link::getEnable, 1)
                .one();

        if (link == null) {
            throw new IllegalArgumentException("短链接不存在");
        }
        if (link.getExpireTime() != null
                && link.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("短链接已过期");
        }

        log.info("跳转 code={} -> {}", code, link.getOriginUrl());
        return link.getOriginUrl();
    }
}
