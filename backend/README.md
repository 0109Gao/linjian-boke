# 林间博客 - 后端（Spring Boot 3 + MySQL + MyBatis-Plus）

## 技术栈
- Spring Boot 3.2.5
- Spring Security + JWT（jjwt 0.12.5）
- MyBatis-Plus 3.5.6
- MySQL 8.x
- JDK 17

## 运行前准备

1. **安装 JDK 17+**、**Maven 3.8+**、**MySQL 8.x**
2. 初始化数据库：
   ```bash
   mysql -u root -p < ../sql/schema.sql
   ```
3. 修改 `src/main/resources/application.yml` 中的数据库账号密码：
   ```yaml
   spring:
     datasource:
       username: root
       password: 你的密码
   ```

## 启动

```bash
mvn spring-boot:run
```

启动后 API 地址：`http://localhost:8080/api`

## 核心接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /api/auth/register | 注册（首个账号=站长） |
| POST | /api/auth/login | 登录 |
| GET | /api/auth/me | 当前用户 |
| GET | /api/posts | 文章列表 |
| GET | /api/posts/{id} | 文章详情 |
| POST | /api/posts | 发文章 |
| PUT | /api/posts/{id} | 编辑文章 |
| DELETE | /api/posts/{id} | 删除文章 |
| GET | /api/posts/{id}/comments | 评论列表 |
| POST | /api/posts/{id}/comments | 发评论 |
| POST | /api/likes | 点赞 |
| POST | /api/favorites/{postId} | 收藏 |
| GET | /api/categories | 分类 |
| GET | /api/tags | 标签 |
| GET | /api/search?q= | 搜索 |

## 说明

- 登录后需在请求头带 `Authorization: Bearer <token>`
- 会员发文章默认 `PENDING`（待审核），站长直接 `PUBLISHED`
- 评论审核开关在 `site_config` 表的 `comment_moderation` 配置项
