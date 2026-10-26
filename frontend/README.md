# 林间博客 - 前端（Vue 3 + Vite + Element Plus）

## 技术栈
- Vue 3.4
- Vite 5
- Vue Router 4
- Pinia 2
- Element Plus 2.6
- Axios
- marked（Markdown 渲染）

## 安装依赖

```bash
npm install
```

## 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

> 开发环境已配置代理，`/api` 请求会自动转发到 `http://localhost:8080`（后端）。

## 构建生产版本

```bash
npm run build
```

## 页面路由

| 路径 | 页面 | 权限 |
|---|---|---|
| / | 首页 | 公开 |
| /login | 登录 | 公开 |
| /register | 注册 | 公开 |
| /post/:id | 文章详情 | 公开 |
| /write | 写文章 | 会员 |
| /me | 我的主页 | 会员 |
| /search | 搜索 | 公开 |
| /admin | 管理后台 | 站长 |
