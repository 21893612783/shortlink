package com.guanyu.shortlink.service.impl;

import com.guanyu.shortlink.domain.po.LinkStat;
import com.guanyu.shortlink.mapper.LinkStatMapper;
import com.guanyu.shortlink.service.ILinkStatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 访问统计表 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
@Service
public class LinkStatServiceImpl extends ServiceImpl<LinkStatMapper, LinkStat> implements ILinkStatService {

}
