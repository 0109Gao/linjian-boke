package com.linjian.blog.controller;

import com.linjian.blog.common.BusinessException;
import com.linjian.blog.common.Result;
import com.linjian.blog.dto.LoginRequest;
import com.linjian.blog.dto.LoginResponse;
import com.linjian.blog.dto.RegisterRequest;
import com.linjian.blog.dto.UpdateProfileRequest;
import com.linjian.blog.entity.User;
import com.linjian.blog.security.UserContext;
import com.linjian.blog.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 上传目录（与 WebConfig 静态资源映射保持一致） */
    @Value("${blog.upload-dir:uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_IMAGE_TYPES =
            Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @GetMapping("/me")
    public Result<User> me() {
        User user = UserContext.get();
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    /**
     * 编辑个人资料（昵称、简介、性别、头像）。
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        return Result.success(authService.updateProfile(userId, request));
    }

    /**
     * 上传头像，返回可访问的 URL 路径。
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        if (file == null || file.isEmpty()) {
            return Result.error(400, "请选择要上传的文件");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            return Result.error(400, "仅支持 JPG/PNG/GIF/WebP 格式的图片");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error(400, "图片大小不能超过 2MB");
        }

        try {
            // 按日期分目录存储，避免单目录文件过多
            String dateDir = LocalDate.now().toString().replace("-", "/");
            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path dir = Paths.get(uploadDir, "avatar", dateDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target.toAbsolutePath().toFile());

            // 返回相对 URL，前端经 /uploads 代理访问
            String url = "/uploads/avatar/" + dateDir + "/" + filename;
            return Result.success(url);
        } catch (IOException e) {
            throw new BusinessException("头像上传失败：" + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".png";
        }
        String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return List.of(".jpg", ".jpeg", ".png", ".gif", ".webp").contains(ext) ? ext : ".png";
    }
}
