package com.linjian.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("`user`")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String nickname;

    private String avatar;

    private String bio;

    /** 性别：MALE=男, FEMALE=女, SECRET=保密 */
    private String gender;

    /** 角色：ADMIN=站长, MEMBER=会员 */
    private String role;

    /** 状态：1=正常, 0=禁用 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
