# 林间博客

> 林间博客的完整交付版本：Vue 3 + Spring Boot 3 + MySQL 的前后端分离个人博客系统，附带独立管理后台。

---

## 一、项目概述

「林间博客」是一个可供网友使用的个人博客网站。访客可以浏览文章、搜索、按分类/标签浏览；注册会员后可以发布文章（需站长审核）、评论（支持盖楼回复）、点赞、收藏；站长拥有独立的管理后台，可管理文章、评论、用户、分类、标签与站点配置。

本版本采用前后端分离架构，前后台共用一个后端服务（端口 8080）与同一个数据库（`linjian_blog`）。

---

## 二、目录结构

```
林间博客最终版/
├── README.md              # 本文件
├── backend/               # 后端（Spring Boot 3.2.5 + MyBatis-Plus + JWT）
│   ├── src/               # 后端源码
│   ├── pom.xml            # Maven 配置
│   └── release/
│       └── linjian-blog-1.0.0.jar   # 可直接运行的打包成品
├── frontend/              # 博客前台（Vue 3 + Vite + Element Plus）
│   ├── src/               # 前台源码
│   ├── dist/              # 前台构建成品（可直接部署）
│   └── package.json
├── admin/                 # 管理后台（Vue 3 + Vite + Element Plus）
│   ├── src/               # 后台源码
│   ├── dist/              # 后台构建成品（可直接部署）
│   └── package.json
├── sql/
│   └── schema.sql         # 数据库建表脚本（含种子数据）
└── docs/                  # 项目文档
    ├── 技术方案.md
    ├── 本地运行说明.md
    ├── 面试准备.md
    └── vue-springboot-README.md
```

> 说明：`node_modules`、编译中间产物（`target/`）等可通过 `npm install` / `mvn package` 重新生成，未纳入交付，以减小体积。

---

## 三、技术栈

| 层   | 技术                                                               |
| --- | ---------------------------------------------------------------- |
| 后端  | Spring Boot 3.2.5、Spring Security、JWT、MyBatis-Plus 3.5.6、MySQL 8 |
| 前台  | Vue 3.4、Vite 5、Vue Router、Pinia、Element Plus、Axios、marked        |
| 后台  | Vue 3.4、Vite 5、Vue Router、Pinia、Element Plus、Axios               |
| 数据库 | MySQL（库名 `linjian_blog`）                                         |

---

## 四、快速启动

### 1. 初始化数据库（仅首次）

```bash
mysql --default-character-set=utf8mb4 -u root -p < sql/schema.sql
```

> 关键：必须加 `--default-character-set=utf8mb4`，否则中文种子数据会因字符集报错。

### 2. 启动后端

方式一（直接运行打包成品）：

```bash
java -jar backend/release/linjian-blog-1.0.0.jar --server.port=8080
```

方式二（源码运行）：

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

### 3. 启动前台

```bash
cd frontend
npm install
npm run dev
```

前台地址：http://localhost:5173

### 4. 启动管理后台

```bash
cd admin
npm install
npm run dev
```

后台地址：http://localhost:5176

> 三个前端工程均已配置 `/api` 与 `/uploads` 代理，指向后端 `http://localhost:8080`。

---

## 五、测试账号

| 角色      | 账号     | 密码     |
| ------- | ------ | ------ |
| 站长（管理员） | GZP    | 123456 |
| 会员      | gzp123 | 123456 |

站长可登录管理后台（5176 端口），会员访问管理接口会被拒绝（403 无权限），体现权限隔离。

---

## 六、主要功能

- 用户：注册、登录、编辑个人资料（昵称、性别、简介、头像上传）
- 文章：发布、审核、编辑、删除、置顶、分类/标签、浏览量统计
- 互动：评论盖楼、点赞、收藏（均幂等）
- 管理后台：仪表盘统计、文章/评论/用户/分类/标签/站点配置管理
- 权限：JWT 无状态认证，三级角色（访客 / 会员 / 站长），`/api/admin/**` 接口做 `ADMIN` 角色拦截

---

## 七、端口规划

| 服务     | 端口   |
| ------ | ---- |
| 后端 API | 8080 |
| 博客前台   | 5173 |
| 管理后台   | 5176 |

---

## 八、补充说明

1. 数据库连接信息可在 `backend/src/main/resources/application.yml` 中通过环境变量覆盖（`DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD`）。
2. 头像等上传文件默认保存到后端运行目录下的 `uploads/` 目录，通过 `/uploads/**` 静态资源映射访问。
3. 生产部署时，可将 `frontend/dist` 与 `admin/dist` 交由 Nginx 托管，并反向代理 `/api` 与 `/uploads` 到后端服务。

---

## 附：文档索引

- 技术架构与设计决策：见 `docs/技术方案.md`
- 本地环境的完整启动步骤与踩坑记录：见 `docs/本地运行说明.md`
- 面试要点梳理：见 `docs/面试准备.md`
