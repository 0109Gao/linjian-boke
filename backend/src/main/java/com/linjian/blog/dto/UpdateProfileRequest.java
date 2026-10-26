package com.linjian.blog.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑个人资料请求
 */
@Data
public class UpdateProfileRequest {

    /** 昵称 */
    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    private String nickname;

    /** 个人简介 */
    @Size(max = 500, message = "简介长度不能超过 500 个字符")
    private String bio;

    /** 性别：MALE=男, FEMALE=女, SECRET=保密 */
    private String gender;

    /** 头像 URL */
    @Size(max = 255, message = "头像地址长度不合法")
    private String avatar;
}
