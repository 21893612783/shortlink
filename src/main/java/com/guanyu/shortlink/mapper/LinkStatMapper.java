package com.guanyu.shortlink.mapper;

import com.guanyu.shortlink.domain.po.LinkStat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 访问统计表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
@Mapper
public interface LinkStatMapper extends BaseMapper<LinkStat> {

}
