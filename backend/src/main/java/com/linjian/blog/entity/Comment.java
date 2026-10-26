package com.linjian.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private Long userId;

    /** 父评论 ID（NULL=顶级评论） */
    private Long parentId;

    private String content;

    /** 状态：APPROVED=通过, PENDING=待审核, SPAM=垃圾 */
    private String status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 非数据库字段
    @TableField(exist = false)
    private String authorName;

    @TableField(exist = false)
    private String authorAvatar;
}
