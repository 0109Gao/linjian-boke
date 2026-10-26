package com.linjian.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linjian.blog.common.Result;
import com.linjian.blog.entity.Post;
import com.linjian.blog.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索接口
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PostMapper postMapper;

    @GetMapping
    public Result<List<Post>> search(@RequestParam String q) {
        List<Post> posts = postMapper.selectList(new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, "PUBLISHED")
                .and(w -> w.like(Post::getTitle, q)
                        .or().like(Post::getSummary, q)
                        .or().like(Post::getContent, q))
                .orderByDesc(Post::getCreatedAt));
        return Result.success(posts);
    }
}
