package com.linjian.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞实体
 */
@Data
@TableName("`like`")
public class Like {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 目标类型：POST=文章, COMMENT=评论 */
    private String targetType;

    private Long targetId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
