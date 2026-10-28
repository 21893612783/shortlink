package com.guanyu.shortlink.mapper;

import com.guanyu.shortlink.domain.po.LinkStat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

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

    @Insert("""
        INSERT INTO link_stat (short_code, stat_date, pv)
        VALUES (#{code}, #{date}, 1)
        ON DUPLICATE KEY UPDATE pv = pv + 1
        """)
    void incrPv(@Param("code") String code, @Param("date") LocalDate date);

    @Insert("""
        INSERT INTO link_stat (short_code, stat_date, uv)
        VALUES (#{code}, #{date}, 1)
        ON DUPLICATE KEY UPDATE uv = uv + 1
        """)
    void incrUv(@Param("code") String code, @Param("date") LocalDate date);

}
