package com.linjian.blog.dto;

import lombok.Data;

/**
 * 点赞/收藏请求
 */
@Data
public class LikeRequest {

    /** 目标类型：POST=文章, COMMENT=评论 */
    private String targetType;

    private Long targetId;
}
