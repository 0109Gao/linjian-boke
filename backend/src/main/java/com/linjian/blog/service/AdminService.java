package com.linjian.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linjian.blog.common.BusinessException;
import com.linjian.blog.entity.*;
import com.linjian.blog.mapper.*;
import com.linjian.blog.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台服务。所有方法均要求站长（ADMIN）角色，权限校验在 Controller 层由 Security 完成，
 * 此处再做一次业务层兜底校验（双保险）。
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final SiteConfigMapper siteConfigMapper;

    private void requireAdmin() {
        User current = UserContext.get();
        if (current == null || !"ADMIN".equals(current.getRole())) {
            throw new BusinessException(403, "无权限操作");
        }
    }

    // ============================================================
    // 仪表盘统计
    // ============================================================

    /**
     * 仪表盘统计数据。
     */
    public Map<String, Object> dashboard() {
        requireAdmin();
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.selectCount(null));
        data.put("postCount", postMapper.selectCount(null));
        data.put("commentCount", commentMapper.selectCount(null));
        data.put("categoryCount", categoryMapper.selectCount(null));
        data.put("tagCount", tagMapper.selectCount(null));
        data.put("pendingPostCount", postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getStatus, "PENDING")));
        data.put("pendingCommentCount", commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, "PENDING")));
        // 最近发布的文章
        List<Post> recentPosts = postMapper.selectList(new LambdaQueryWrapper<Post>()
                .orderByDesc(Post::getCreatedAt).last("LIMIT 5"));
        for (Post p : recentPosts) {
            User u = userMapper.selectById(p.getUserId());
            if (u != null) {
                p.setAuthorName(u.getNickname() != null ? u.getNickname() : u.getUsername());
            }
            if (p.getCategoryId() != null) {
                Category c = categoryMapper.selectById(p.getCategoryId());
                if (c != null) {
                    p.setCategoryName(c.getName());
                }
            }
        }
        data.put("recentPosts", recentPosts);
        // 最近评论
        List<Comment> recentComments = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreatedAt).last("LIMIT 5"));
        for (Comment c : recentComments) {
            User u = userMapper.selectById(c.getUserId());
            if (u != null) {
                c.setAuthorName(u.getNickname() != null ? u.getNickname() : u.getUsername());
            }
        }
        data.put("recentComments", recentComments);
        return data;
    }

    // ============================================================
    // 文章管理
    // ============================================================

    /**
     * 文章全量分页列表（含各状态），支持关键字搜索。
     */
    public Page<Post> listPosts(int page, int size, String status, String keyword) {
        requireAdmin();
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Post::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Post::getTitle, keyword).or().like(Post::getSummary, keyword));
        }
        wrapper.orderByDesc(Post::getCreatedAt);
        Page<Post> result = postMapper.selectPage(new Page<>(page, size), wrapper);
        // 填充作者名和分类名
        for (Post p : result.getRecords()) {
            User u = userMapper.selectById(p.getUserId());
            if (u != null) {
                p.setAuthorName(u.getNickname() != null ? u.getNickname() : u.getUsername());
            }
            if (p.getCategoryId() != null) {
                Category c = categoryMapper.selectById(p.getCategoryId());
                if (c != null) {
                    p.setCategoryName(c.getName());
                }
            }
        }
        return result;
    }

    /**
     * 站长强制删除文章（不受归属限制）。
     */
    @Transactional
    public void deletePost(Long id) {
        requireAdmin();
        postMapper.deleteById(id);
    }

    /**
     * 置顶/取消置顶。
     */
    public Post togglePin(Long id, Integer pinned) {
        requireAdmin();
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        post.setPinned(pinned != null && pinned == 1 ? 1 : 0);
        postMapper.updateById(post);
        return post;
    }

    // ============================================================
    // 评论管理
    // ============================================================

    /**
     * 评论全量分页列表。
     */
    public Page<Comment> listComments(int page, int size, String status) {
        requireAdmin();
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Comment::getStatus, status);
        }
        wrapper.orderByDesc(Comment::getCreatedAt);
        Page<Comment> result = commentMapper.selectPage(new Page<>(page, size), wrapper);
        for (Comment c : result.getRecords()) {
            User u = userMapper.selectById(c.getUserId());
            if (u != null) {
                c.setAuthorName(u.getNickname() != null ? u.getNickname() : u.getUsername());
            }
        }
        return result;
    }

    /**
     * 站长强制删除评论。
     */
    public void deleteComment(Long id) {
        requireAdmin();
        commentMapper.deleteById(id);
    }

    // ============================================================
    // 用户管理
    // ============================================================

    /**
     * 用户分页列表（密码脱敏）。
     */
    public Page<User> listUsers(int page, int size, String keyword) {
        requireAdmin();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getEmail, keyword));
        }
        wrapper.orderByDesc(User::getCreatedAt);
        Page<User> result = userMapper.selectPage(new Page<>(page, size), wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    /**
     * 禁用/启用用户。
     */
    public User toggleUserStatus(Long id, Integer status) {
        requireAdmin();
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 不允许禁用自己
        User current = UserContext.get();
        if (current != null && current.getId().equals(id)) {
            throw new BusinessException("不能禁用自己的账号");
        }
        user.setStatus(status != null && status == 1 ? 1 : 0);
        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    /**
     * 修改用户角色（MEMBER/ADMIN）。
     */
    public User updateUserRole(Long id, String role) {
        requireAdmin();
        if (!"ADMIN".equals(role) && !"MEMBER".equals(role)) {
            throw new BusinessException("无效的角色");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    // ============================================================
    // 分类管理
    // ============================================================

    public List<Category> listCategories() {
        requireAdmin();
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }

    public Category saveCategory(Category category) {
        requireAdmin();
        if (category.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
        return category;
    }

    @Transactional
    public void deleteCategory(Long id) {
        requireAdmin();
        // 检查该分类下是否有文章
        Long count = postMapper.selectCount(new LambdaQueryWrapper<Post>().eq(Post::getCategoryId, id));
        if (count != null && count > 0) {
            throw new BusinessException("该分类下存在文章，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    // ============================================================
    // 标签管理
    // ============================================================

    public List<Tag> listTags() {
        requireAdmin();
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getId));
    }

    public Tag saveTag(Tag tag) {
        requireAdmin();
        if (tag.getId() == null) {
            tagMapper.insert(tag);
        } else {
            tagMapper.updateById(tag);
        }
        return tag;
    }

    public void deleteTag(Long id) {
        requireAdmin();
        tagMapper.deleteById(id);
    }

    // ============================================================
    // 站点配置管理
    // ============================================================

    public List<SiteConfig> listSiteConfigs() {
        requireAdmin();
        return siteConfigMapper.selectList(null);
    }

    public SiteConfig saveSiteConfig(SiteConfig config) {
        requireAdmin();
        if (config.getId() == null) {
            siteConfigMapper.insert(config);
        } else {
            siteConfigMapper.updateById(config);
        }
        return config;
    }
}
