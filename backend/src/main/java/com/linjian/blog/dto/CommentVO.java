package com.linjian.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论树节点（响应）
 */
@Data
public class CommentVO {

    private Long id;

    private Long userId;

    private String authorName;

    private String authorAvatar;

    private String content;

    private String status;

    private LocalDateTime createdAt;

    /** 子评论（盖楼） */
    private List<CommentVO> children;
}
