package com.linjian.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 文章发布/编辑请求
 */
@Data
public class PostRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题过长")
    private String title;

    @Size(max = 500, message = "摘要过长")
    private String summary;

    private String content;

    private String cover;

    private Long categoryId;

    private List<Long> tagIds;
}
