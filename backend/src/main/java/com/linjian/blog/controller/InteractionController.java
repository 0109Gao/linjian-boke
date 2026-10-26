package com.linjian.blog.controller;

import com.linjian.blog.common.Result;
import com.linjian.blog.dto.LikeRequest;
import com.linjian.blog.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 点赞/收藏接口
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping("/likes")
    public Result<Map<String, Object>> like(@RequestBody LikeRequest request) {
        boolean liked = interactionService.toggleLike(request);
        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        return Result.success(data);
    }

    @GetMapping("/likes/status")
    public Result<Map<String, Object>> likeStatus(@RequestParam String targetType,
                                                  @RequestParam Long targetId) {
        Map<String, Object> data = new HashMap<>();
        data.put("liked", interactionService.isLiked(targetType, targetId));
        return Result.success(data);
    }

    @PostMapping("/favorites/{postId}")
    public Result<Map<String, Object>> favorite(@PathVariable Long postId) {
        boolean favorited = interactionService.toggleFavorite(postId);
        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        return Result.success(data);
    }

    @GetMapping("/favorites/{postId}/status")
    public Result<Map<String, Object>> favoriteStatus(@PathVariable Long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("favorited", interactionService.isFavorited(postId));
        return Result.success(data);
    }
}
