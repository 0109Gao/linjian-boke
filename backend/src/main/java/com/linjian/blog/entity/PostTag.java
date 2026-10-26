package com.linjian.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章-标签关联实体
 */
@Data
@TableName("post_tag")
public class PostTag {

    private Long postId;

    private Long tagId;
}
