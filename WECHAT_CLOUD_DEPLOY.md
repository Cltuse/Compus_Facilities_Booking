# 微信云托管前端部署说明

当前仓库已经调整为适合 `Vue + Nginx + 微信云托管` 的前端容器部署方式。

## 当前部署结构

- 根目录 `Dockerfile`：用于构建 `frontend/` 并使用 `nginx:stable-alpine` 提供静态站点服务
- `frontend/nginx/default.conf.template`：处理 Vue Router `history` 刷新，并将 `/api`、`/files` 代理到 Spring Boot 服务
- `frontend/.env.production`：前端生产环境默认通过 `/api` 访问后端

## 微信云托管创建前端服务

在微信云托管控制台中创建一个新服务：

- 部署来源：Git 仓库
- Dockerfile 路径：`Dockerfile`
- 构建上下文：`/`
- 服务端口：`80`

推荐配置的环境变量：

- `BACKEND_UPSTREAM=https://你的-springboot-服务域名`

例如：

```text
BACKEND_UPSTREAM=https://facility-backend-12345-1300000000.ap-shanghai.run.tcloudbase.com
```

说明：

- 前端页面访问 `/`
- 前端接口访问 `/api/**`
- 图片和上传文件访问 `/files/**`
- Nginx 会把 `/api` 和 `/files` 自动转发到 `BACKEND_UPSTREAM`

## 本地联调

本地开发默认仍然使用 Vite 代理：

- `/api` -> `http://localhost:5681`
- `/files` -> `http://localhost:5681`

如果你想让前端直接请求某个独立后端地址，可以调整：

`frontend/.env.production`

```text
VITE_API_BASE_URL=/api
```

例如改为：

```text
VITE_API_BASE_URL=https://你的后端域名/api
```

当你使用上面的直连方式时，建议同步确认后端返回的文件地址也能被前端域名正确访问；当前仓库默认推荐继续使用 Nginx 反向代理方式。

## Vue Router history

Nginx 已内置：

```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

因此刷新 `/admin/dashboard`、`/user/facility/1` 这类路由时不会返回 404。
