package com.linjian.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linjian.blog.common.Result;
import com.linjian.blog.dto.PostRequest;
import com.linjian.blog.entity.Post;
import com.linjian.blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文章接口
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public Result<Page<Post>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return Result.success(postService.listPublished(page, size, categoryId, keyword));
    }

    @GetMapping("/mine")
    public Result<java.util.List<Post>> mine() {
        return Result.success(postService.listMine());
    }

    @GetMapping("/{id}")
    public Result<Post> detail(@PathVariable Long id) {
        return Result.success(postService.getDetail(id));
    }

    @PostMapping
    public Result<Post> create(@Valid @RequestBody PostRequest request) {
        return Result.success(postService.create(request));
    }

    @PutMapping("/{id}")
    public Result<Post> update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return Result.success(postService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return Result.success();
    }
}
