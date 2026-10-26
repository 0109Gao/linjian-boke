package com.linjian.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linjian.blog.common.BusinessException;
import com.linjian.blog.dto.LikeRequest;
import com.linjian.blog.entity.Favorite;
import com.linjian.blog.entity.Like;
import com.linjian.blog.entity.Post;
import com.linjian.blog.mapper.FavoriteMapper;
import com.linjian.blog.mapper.LikeMapper;
import com.linjian.blog.mapper.PostMapper;
import com.linjian.blog.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞/收藏服务（幂等：重复操作自动取消）
 */
@Service
@RequiredArgsConstructor
public class InteractionService {

    private final LikeMapper likeMapper;
    private final FavoriteMapper favoriteMapper;
    private final PostMapper postMapper;

    /**
     * 点赞/取消点赞（幂等）。返回是否已点赞。
     */
    @Transactional
    public boolean toggleLike(LikeRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Like existing = likeMapper.selectOne(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, userId)
                .eq(Like::getTargetType, request.getTargetType())
                .eq(Like::getTargetId, request.getTargetId()));
        if (existing != null) {
            likeMapper.deleteById(existing.getId());
            // 文章点赞数 -1
            if ("POST".equals(request.getTargetType())) {
                updatePostLikeCount(request.getTargetId(), -1);
            }
            return false;
        }
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(request.getTargetType());
        like.setTargetId(request.getTargetId());
        likeMapper.insert(like);
        if ("POST".equals(request.getTargetType())) {
            updatePostLikeCount(request.getTargetId(), 1);
        }
        return true;
    }

    /**
     * 收藏/取消收藏（幂等）。返回是否已收藏。
     */
    @Transactional
    public boolean toggleFavorite(Long postId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        Favorite existing = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId));
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            return false;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        favoriteMapper.insert(favorite);
        return true;
    }

    /**
     * 当前用户是否已点赞。
     */
    public boolean isLiked(String targetType, Long targetId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return false;
        }
        return likeMapper.selectCount(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId)) > 0;
    }

    /**
     * 当前用户是否已收藏。
     */
    public boolean isFavorited(Long postId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return false;
        }
        return favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId)) > 0;
    }

    private void updatePostLikeCount(Long postId, int delta) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            post.setLikeCount(Math.max(0, post.getLikeCount() + delta));
            postMapper.updateById(post);
        }
    }
}
