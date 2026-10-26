package com.linjian.blog.controller;

import com.linjian.blog.common.Result;
import com.linjian.blog.dto.CommentRequest;
import com.linjian.blog.dto.CommentVO;
import com.linjian.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论接口
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public Result<List<CommentVO>> list(@PathVariable Long postId) {
        return Result.success(commentService.listByPost(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public Result<CommentVO> create(@PathVariable Long postId,
                                    @Valid @RequestBody CommentRequest request) {
        return Result.success(commentService.create(postId, request));
    }
}
