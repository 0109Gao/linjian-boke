-- ============================================================
-- 林间博客 数据库建表脚本（MySQL 8.x）
-- 执行方式：mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS linjian_blog
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE linjian_blog;

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名（登录用）',
  `email`       VARCHAR(100) NOT NULL COMMENT '邮箱（唯一）',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
  `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
  `bio`         VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
  `gender`      VARCHAR(10)  DEFAULT NULL COMMENT '性别：MALE=男, FEMALE=女, SECRET=保密',
  `role`        VARCHAR(20)  NOT NULL DEFAULT 'MEMBER' COMMENT '角色：ADMIN=站长, MEMBER=会员',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 0=禁用',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=否, 1=是',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ------------------------------------------------------------
-- 2. 分类表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `category` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(50) NOT NULL COMMENT '分类名',
  `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- ------------------------------------------------------------
-- 3. 标签表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tag` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(50) NOT NULL COMMENT '标签名',
  `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ------------------------------------------------------------
-- 4. 文章表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `post` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT       NOT NULL COMMENT '作者 ID',
  `title`       VARCHAR(200) NOT NULL COMMENT '标题',
  `slug`        VARCHAR(200) NOT NULL COMMENT 'URL 友好标识（唯一）',
  `summary`     VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content`     LONGTEXT     COMMENT '正文（Markdown）',
  `cover`       VARCHAR(255) DEFAULT NULL COMMENT '封面图 URL',
  `category_id` BIGINT       DEFAULT NULL COMMENT '分类 ID',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PUBLISHED=已发布, PENDING=待审核, DRAFT=草稿',
  `pinned`      TINYINT      NOT NULL DEFAULT 0 COMMENT '置顶：0=否, 1=是',
  `views`       INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count`  INT          NOT NULL DEFAULT 0 COMMENT '点赞数（冗余，加速查询）',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`),
  KEY `idx_status_created` (`status`, `created_at`),
  KEY `idx_user` (`user_id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ------------------------------------------------------------
-- 5. 文章-标签关联表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `post_tag` (
  `post_id` BIGINT NOT NULL,
  `tag_id`  BIGINT NOT NULL,
  PRIMARY KEY (`post_id`, `tag_id`),
  KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- ------------------------------------------------------------
-- 6. 评论表（支持盖楼）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comment` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `post_id`    BIGINT       NOT NULL COMMENT '文章 ID',
  `user_id`    BIGINT       NOT NULL COMMENT '评论者 ID',
  `parent_id`  BIGINT       DEFAULT NULL COMMENT '父评论 ID（NULL=顶级评论）',
  `content`    TEXT         NOT NULL COMMENT '评论内容',
  `status`     VARCHAR(20)  NOT NULL DEFAULT 'APPROVED' COMMENT '状态：APPROVED=通过, PENDING=待审核, SPAM=垃圾',
  `deleted`    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post` (`post_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ------------------------------------------------------------
-- 7. 点赞表（幂等）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `like` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT      NOT NULL COMMENT '用户 ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型：POST=文章, COMMENT=评论',
  `target_id`   BIGINT      NOT NULL COMMENT '目标 ID',
  `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_like` (`user_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ------------------------------------------------------------
-- 8. 收藏表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `favorite` (
  `id`         BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`    BIGINT   NOT NULL COMMENT '用户 ID',
  `post_id`    BIGINT   NOT NULL COMMENT '文章 ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorite` (`user_id`, `post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ------------------------------------------------------------
-- 9. 订阅表（RSS）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `subscription` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `email`      VARCHAR(100) NOT NULL COMMENT '订阅邮箱',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅表';

-- ------------------------------------------------------------
-- 10. 站点配置表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `site_config` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(255) DEFAULT NULL COMMENT '配置值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点配置表';

-- ------------------------------------------------------------
-- 种子数据
-- ------------------------------------------------------------

-- 站点配置
INSERT INTO `site_config` (`config_key`, `config_value`) VALUES
  ('site_name', '林间博客'),
  ('comment_moderation', 'false'),
  ('domain', 'http://localhost:8080');

-- 分类
INSERT INTO `category` (`name`, `sort`) VALUES
  ('前端', 1), ('后端', 2), ('数据库', 3), ('随笔', 4);

-- 标签
INSERT INTO `tag` (`name`) VALUES
  ('Vue'), ('Spring Boot'), ('MySQL'), ('Java'), ('JavaScript');

-- 默认站长账号（密码明文 123456，启动时会被 BCrypt 加密；此处仅占位）
-- 实际第一个注册用户会被自动设为 ADMIN，此条可注释掉
-- INSERT INTO `user` (username, email, password, nickname, role) VALUES
--   ('GZP', 'admin@linjian.com', '$2a$10$placeholder', '站长', 'ADMIN');
