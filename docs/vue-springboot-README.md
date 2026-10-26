# 林间博客

一个对标 CSDN 的多人博客系统，采用前后端分离架构。

- **前端**：Vue 3 + Vite + Element Plus + Pinia
- **后端**：Spring Boot 3 + Spring Security + JWT + MyBatis-Plus
- **数据库**：MySQL 8

## 功能特性

- 用户注册登录（JWT 无状态认证），三级权限（访客 / 会员 / 站长）
- 文章发布、编辑、删除，会员发文章需站长审核
- 评论盖楼（多级回复）
- 点赞、收藏（幂等设计，防重复）
- 分类、标签、全文搜索
- 管理员后台（文章审核、评论审核）

## 目录结构

```
linjian-blog-vue-springboot/
├── backend/          # Spring Boot 后端
├── frontend/         # Vue3 前端
├── sql/schema.sql    # 数据库建表 + 种子数据
├── 技术方案.md        # 完整技术方案文档
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x
- Node.js 18+

### 1. 初始化数据库

```bash
mysql -u root -p < sql/schema.sql
```

### 2. 启动后端

```bash
cd backend
# 修改 src/main/resources/application.yml 里的数据库账号密码
mvn spring-boot:run
```

后端启动在 http://localhost:8080

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动在 http://localhost:5173

## 使用说明

1. 打开 http://localhost:5173，注册第一个账号（自动成为站长 ADMIN）
2. 后续注册的账号为会员 MEMBER
3. 会员发文章 → 待审核 → 站长在「管理后台」审核通过后公开
4. 会员可评论、点赞、收藏、盖楼回复

## 核心接口

详见 `backend/README.md` 和 `技术方案.md`。

## 权限模型

| 角色 | 浏览 | 评论 | 点赞收藏 | 发文章 | 审核 | 后台 |
|---|---|---|---|---|---|---|
| 访客 | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| 会员 | ✅ | ✅ | ✅ | ✅（需审核） | ❌ | ❌ |
| 站长 | ✅ | ✅ | ✅ | ✅（直接发布） | ✅ | ✅ |
