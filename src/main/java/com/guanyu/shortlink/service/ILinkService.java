package com.guanyu.shortlink.service;

import com.guanyu.shortlink.domain.po.Link;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 短链接表 服务类
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
public interface ILinkService extends IService<Link> {

    String create(String originUrl);

}
