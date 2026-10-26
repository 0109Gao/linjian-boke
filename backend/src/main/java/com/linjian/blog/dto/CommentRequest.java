package com.linjian.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论请求
 */
@Data
public class CommentRequest {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论过长")
    private String content;

    /** 父评论 ID（盖楼回复时传） */
    private Long parentId;
}
