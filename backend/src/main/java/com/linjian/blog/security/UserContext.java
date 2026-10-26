package com.linjian.blog.security;

import com.linjian.blog.entity.User;

/**
 * 当前登录用户上下文（ThreadLocal）
 */
public class UserContext {

    private static final ThreadLocal<User> HOLDER = new ThreadLocal<>();

    public static void set(User user) {
        HOLDER.set(user);
    }

    public static User get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        User user = HOLDER.get();
        return user != null ? user.getId() : null;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
