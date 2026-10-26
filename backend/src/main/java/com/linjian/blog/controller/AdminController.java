package com.linjian.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linjian.blog.common.Result;
import com.linjian.blog.entity.*;
import com.linjian.blog.service.AdminService;
import com.linjian.blog.service.CommentService;
import com.linjian.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台接口（均需 ADMIN 角色，由 Security 的 /api/admin/** 拦截）
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PostService postService;
    private final CommentService commentService;

    // ============================================================
    // 仪表盘
    // ============================================================

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(adminService.dashboard());
    }

    // ============================================================
    // 文章管理
    // ============================================================

    @GetMapping("/posts")
    public Result<Page<Post>> posts(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String keyword) {
        return Result.success(adminService.listPosts(page, size, status, keyword));
    }

    @GetMapping("/posts/pending")
    public Result<List<Post>> pendingPosts() {
        return Result.success(postService.listPending());
    }

    @PutMapping("/posts/{id}/audit")
    public Result<Post> auditPost(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(postService.audit(id, body.get("status")));
    }

    @PutMapping("/posts/{id}/pin")
    public Result<Post> togglePin(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer pinned = body.get("pinned") == null ? 0 : Integer.parseInt(body.get("pinned").toString());
        return Result.success(adminService.togglePin(id, pinned));
    }

    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return Result.success();
    }

    // ============================================================
    // 评论管理
    // ============================================================

    @GetMapping("/comments")
    public Result<Page<Comment>> comments(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String status) {
        return Result.success(adminService.listComments(page, size, status));
    }

    @GetMapping("/comments/pending")
    public Result<List<Comment>> pendingComments() {
        return Result.success(commentService.listPending());
    }

    @PutMapping("/comments/{id}/audit")
    public Result<Void> auditComment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        commentService.audit(id, body.get("status"));
        return Result.success();
    }

    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        adminService.deleteComment(id);
        return Result.success();
    }

    // ============================================================
    // 用户管理
    // ============================================================

    @GetMapping("/users")
    public Result<Page<User>> users(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String keyword) {
        return Result.success(adminService.listUsers(page, size, keyword));
    }

    @PutMapping("/users/{id}/status")
    public Result<User> toggleUserStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = body.get("status") == null ? 0 : Integer.parseInt(body.get("status").toString());
        return Result.success(adminService.toggleUserStatus(id, status));
    }

    @PutMapping("/users/{id}/role")
    public Result<User> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(adminService.updateUserRole(id, body.get("role")));
    }

    // ============================================================
    // 分类管理
    // ============================================================

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(adminService.listCategories());
    }

    @PostMapping("/categories")
    public Result<Category> saveCategory(@RequestBody Category category) {
        return Result.success(adminService.saveCategory(category));
    }

    @PutMapping("/categories")
    public Result<Category> updateCategory(@RequestBody Category category) {
        return Result.success(adminService.saveCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return Result.success();
    }

    // ============================================================
    // 标签管理
    // ============================================================

    @GetMapping("/tags")
    public Result<List<Tag>> tags() {
        return Result.success(adminService.listTags());
    }

    @PostMapping("/tags")
    public Result<Tag> saveTag(@RequestBody Tag tag) {
        return Result.success(adminService.saveTag(tag));
    }

    @PutMapping("/tags")
    public Result<Tag> updateTag(@RequestBody Tag tag) {
        return Result.success(adminService.saveTag(tag));
    }

    @DeleteMapping("/tags/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        adminService.deleteTag(id);
        return Result.success();
    }

    // ============================================================
    // 站点配置
    // ============================================================

    @GetMapping("/configs")
    public Result<List<SiteConfig>> configs() {
        return Result.success(adminService.listSiteConfigs());
    }

    @PutMapping("/configs")
    public Result<SiteConfig> saveConfig(@RequestBody SiteConfig config) {
        return Result.success(adminService.saveSiteConfig(config));
    }
}
