# 林间博客 · 管理后台

林间博客的独立管理后台（Vue3 + Element Plus），复用博客的 Spring Boot 后端与账号体系，站长（ADMIN 角色）登录后管理博客的**文章、评论、用户、分类、标签、站点配置**。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + Vite + Vue Router + Pinia + Element Plus + Axios |
| 后端 | 复用博客 Spring Boot 3.x（`/api/admin/**` 接口） |
| 安全 | 复用博客 JWT 无状态认证 + ADMIN 角色校验 |

## 功能模块

| 模块 | 能力 |
|------|------|
| 仪表盘 | 文章/用户/评论/分类/标签统计卡片 + 待审核提醒 + 最近动态 |
| 文章管理 | 全量列表（含审核/草稿状态）、搜索、审核通过/驳回、置顶、删除 |
| 评论管理 | 全量评论、审核通过/标垃圾、删除 |
| 用户管理 | 用户列表、搜索、禁用/启用、升降角色（站长/会员） |
| 分类与标签 | 分类/标签增删改、排序 |
| 站点设置 | 站点名称、评论审核开关、域名配置 |

## 启动步骤

### 前提

1. 博客后端已启动在 `8080` 端口（见博客项目 `本地运行说明.md`）
2. MySQL 已初始化 `linjian_blog` 数据库

### 启动前端

```bash
cd admin
npm install   # 仅首次
npm run dev
```

访问 http://localhost:5176/

> Vite 已配 `/api` 代理到后端 `http://localhost:8080`

## 演示账号

| 账号 | 密码 | 角色 |
|------|------|------|
| GZP | 123456 | 站长（可进入后台） |
| gzp123 | 123456 | 会员（无后台权限） |

> 用 gzp123 登录会提示"该账号无管理权限"，这是权限隔离的体现。

## 目录结构

```
admin/
├── src/
│   ├── api/            # Axios 封装 + 接口定义
│   ├── router/         # 路由 + 路由守卫
│   ├── store/          # Pinia 用户状态
│   ├── layout/         # 后台布局（侧边栏 + 顶栏）
│   └── views/
│       ├── login/      # 登录页
│       ├── dashboard/  # 仪表盘
│       ├── post/       # 文章管理
│       ├── comment/    # 评论管理
│       ├── user/       # 用户管理
│       ├── category/   # 分类与标签
│       └── config/     # 站点设置
└── vite.config.js      # 端口 5176 + /api 代理
```

## 后端接口（新增/扩展）

在博客后端 `AdminController` + `AdminService` 中扩展了完整的 `/api/admin/**` 管理接口：

- `GET /api/admin/dashboard` — 仪表盘统计
- `GET/POST/PUT/DELETE /api/admin/posts` — 文章管理（含审核、置顶、删除）
- `GET/PUT/DELETE /api/admin/comments` — 评论管理（含审核、标垃圾、删除）
- `GET/PUT /api/admin/users` — 用户管理（禁用、升降角色）
- `GET/POST/PUT/DELETE /api/admin/categories` — 分类管理
- `GET/POST/PUT/DELETE /api/admin/tags` — 标签管理
- `GET/PUT /api/admin/configs` — 站点配置

所有接口均由 Spring Security 的 `/api/admin/**` 规则做 `hasRole("ADMIN")` 拦截，Service 层再做一次角色兜底校验（双保险）。
