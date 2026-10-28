package com.guanyu.shortlink.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 访问统计表
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("link_stat")
public class LinkStat implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 短码
     */
    private String shortCode;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 浏览量
     */
    private Long pv;

    /**
     * 访客数
     */
    private Long uv;

    private LocalDateTime updateTime;


}
