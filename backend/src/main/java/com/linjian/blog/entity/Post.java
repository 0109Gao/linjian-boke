package com.linjian.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章实体
 */
@Data
@TableName("post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String slug;

    private String summary;

    private String content;

    private String cover;

    private Long categoryId;

    /** 状态：PUBLISHED=已发布, PENDING=待审核, DRAFT=草稿 */
    private String status;

    private Integer pinned;

    private Integer views;

    private Integer likeCount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 非数据库字段（联表查询用）
    @TableField(exist = false)
    private String authorName;

    @TableField(exist = false)
    private String authorAvatar;

    @TableField(exist = false)
    private String categoryName;
}
