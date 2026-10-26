package com.linjian.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linjian.blog.common.BusinessException;
import com.linjian.blog.dto.PostRequest;
import com.linjian.blog.entity.*;
import com.linjian.blog.mapper.*;
import com.linjian.blog.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 文章服务
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final PostTagMapper postTagMapper;

    /**
     * 公开文章列表（分页）。只返回 PUBLISHED 状态。
     */
    public Page<Post> listPublished(int page, int size, Long categoryId, String keyword) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, "PUBLISHED");
        if (categoryId != null) {
            wrapper.eq(Post::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Post::getTitle, keyword).or().like(Post::getSummary, keyword));
        }
        wrapper.orderByDesc(Post::getPinned).orderByDesc(Post::getCreatedAt);
        return postMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 文章详情。仅作者/站长可见非 PUBLISHED 状态。
     */
    public Post getDetail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        User current = UserContext.get();
        boolean isOwner = current != null && Objects.equals(current.getId(), post.getUserId());
        boolean isAdmin = current != null && "ADMIN".equals(current.getRole());
        if (!"PUBLISHED".equals(post.getStatus()) && !isOwner && !isAdmin) {
            throw new BusinessException(404, "文章不存在或未发布");
        }
        // 填充作者信息
        fillAuthor(post);
        // 浏览量 +1
        post.setViews(post.getViews() + 1);
        postMapper.updateById(post);
        return post;
    }

    /**
     * 发布文章。会员发布自动 PENDING，站长直接 PUBLISHED。
     */
    @Transactional
    public Post create(PostRequest request) {
        User current = UserContext.get();
        if (current == null) {
            throw new BusinessException(401, "请先登录");
        }

        Post post = new Post();
        post.setUserId(current.getId());
        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setCover(request.getCover());
        post.setCategoryId(request.getCategoryId());
        post.setSlug(generateSlug(request.getTitle()));
        post.setStatus("ADMIN".equals(current.getRole()) ? "PUBLISHED" : "PENDING");
        post.setPinned(0);
        post.setViews(0);
        post.setLikeCount(0);
        postMapper.insert(post);

        // 保存标签关联
        saveTags(post.getId(), request.getTagIds());

        return post;
    }

    /**
     * 编辑文章。仅作者本人或站长。
     */
    @Transactional
    public Post update(Long id, PostRequest request) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        checkOwnership(post);

        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setCover(request.getCover());
        post.setCategoryId(request.getCategoryId());
        // 会员编辑后需重新审核
        if (!"ADMIN".equals(UserContext.get().getRole())) {
            post.setStatus("PENDING");
        }
        postMapper.updateById(post);

        // 更新标签
        postTagMapper.delete(new LambdaQueryWrapper<PostTag>().eq(PostTag::getPostId, id));
        saveTags(id, request.getTagIds());

        return post;
    }

    /**
     * 删除文章。仅作者本人或站长。
     */
    @Transactional
    public void delete(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        checkOwnership(post);
        postMapper.deleteById(id);
        postTagMapper.delete(new LambdaQueryWrapper<PostTag>().eq(PostTag::getPostId, id));
    }

    /**
     * 站长审核文章（通过/驳回）。
     */
    public Post audit(Long id, String status) {
        if (!"ADMIN".equals(UserContext.get().getRole())) {
            throw new BusinessException(403, "无权限操作");
        }
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!"PUBLISHED".equals(status) && !"PENDING".equals(status)) {
            throw new BusinessException("无效的审核状态");
        }
        post.setStatus(status);
        postMapper.updateById(post);
        return post;
    }

    /**
     * 获取当前用户的文章列表（含待审核）。
     */
    public List<Post> listMine() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return postMapper.selectList(new LambdaQueryWrapper<Post>()
                .eq(Post::getUserId, userId)
                .orderByDesc(Post::getCreatedAt));
    }

    /**
     * 站长获取待审核文章列表。
     */
    public List<Post> listPending() {
        if (!"ADMIN".equals(UserContext.get().getRole())) {
            throw new BusinessException(403, "无权限操作");
        }
        return postMapper.selectList(new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, "PENDING")
                .orderByDesc(Post::getCreatedAt));
    }

    private void checkOwnership(Post post) {
        User current = UserContext.get();
        boolean isOwner = Objects.equals(current.getId(), post.getUserId());
        boolean isAdmin = "ADMIN".equals(current.getRole());
        if (!isOwner && !isAdmin) {
            throw new BusinessException(403, "无权限操作该文章");
        }
    }

    private void fillAuthor(Post post) {
        User author = userMapper.selectById(post.getUserId());
        if (author != null) {
            post.setAuthorName(author.getNickname() != null ? author.getNickname() : author.getUsername());
            post.setAuthorAvatar(author.getAvatar());
        }
        if (post.getCategoryId() != null) {
            Category category = categoryMapper.selectById(post.getCategoryId());
            if (category != null) {
                post.setCategoryName(category.getName());
            }
        }
    }

    private void saveTags(Long postId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds) {
            PostTag postTag = new PostTag();
            postTag.setPostId(postId);
            postTag.setTagId(tagId);
            postTagMapper.insert(postTag);
        }
    }

    private String generateSlug(String title) {
        // 只保留英文、数字、连字符；中文等字符会被移除
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-+|-+$", "");
        if (slug.isBlank()) {
            slug = "post";
        }
        return slug + "-" + System.currentTimeMillis() % 100000;
    }
}
