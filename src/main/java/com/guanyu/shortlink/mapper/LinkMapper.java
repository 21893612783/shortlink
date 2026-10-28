package com.guanyu.shortlink.mapper;

import com.guanyu.shortlink.domain.po.Link;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 短链接表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
@Mapper
public interface LinkMapper extends BaseMapper<Link> {

}
