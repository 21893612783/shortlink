package com.guanyu.shortlink.domain.po;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短链接表
 * </p>
 *
 * @author author
 * @since 2026-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("link")
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String shortCode;

    /**
     * 原始长链接
     */
    private String originUrl;

    /**
     * 分组
     */
    private String groupId;

    /**
     * 过期时间，NULL永久
     */
    private LocalDateTime expireTime;

    /**
     * 1启用 0禁用
     */
    private Integer enable;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer delFlag;


}
