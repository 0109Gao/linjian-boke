package com.linjian.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linjian.blog.common.BusinessException;
import com.linjian.blog.dto.LoginRequest;
import com.linjian.blog.dto.LoginResponse;
import com.linjian.blog.dto.RegisterRequest;
import com.linjian.blog.dto.UpdateProfileRequest;
import com.linjian.blog.entity.User;
import com.linjian.blog.mapper.UserMapper;
import com.linjian.blog.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务：注册、登录
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 注册。第一个注册的用户自动成为站长（ADMIN）。
     */
    public LoginResponse register(RegisterRequest request) {
        // 校验邮箱/用户名是否已存在
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, request.getEmail())
                .or()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("邮箱或用户名已被注册");
        }

        // 首个用户 = 站长
        Long total = userMapper.selectCount(null);
        String role = (total == null || total == 0) ? "ADMIN" : "MEMBER";

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(role);
        user.setStatus(1);
        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, toUserInfo(user));
    }

    /**
     * 登录。支持用户名或邮箱。
     */
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getAccount())
                .or()
                .eq(User::getEmail, request.getAccount()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, toUserInfo(user));
    }

    /**
     * 编辑个人资料（昵称、简介、性别、头像）。
     */
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname().trim());
        }
        // 简介、性别、头像允许置空
        user.setBio(request.getBio());
        user.setGender(request.getGender());
        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        userMapper.updateById(user);

        // 重新查询，返回最新数据
        User updated = userMapper.selectById(userId);
        updated.setPassword(null); // 不返回密码
        return updated;
    }

    private LoginResponse.UserInfo toUserInfo(User user) {
        return new LoginResponse.UserInfo(
                user.getId(), user.getUsername(), user.getNickname(),
                user.getEmail(), user.getAvatar(), user.getBio(),
                user.getGender(), user.getRole());
    }
}
