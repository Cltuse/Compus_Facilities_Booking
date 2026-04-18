# 微信云托管部署说明

## 1. 当前仓库的部署方式

本仓库已经调整为单容器部署：

- `frontend` 先执行 `npm run build`
- 构建产物会被复制到 Spring Boot 的 `static` 目录
- `backend` 再打包成一个可运行的 Jar
- 微信云托管只需要暴露一个端口即可访问前端页面和后端 API

容器启动后：

- 前端页面：`/`
- 后端接口：`/api/**`
- 上传文件：`/files/**`

## 2. 微信云托管控制台填写

在“部署发布”页面中：

- 选择方式：`绑定 GitHub 仓库`
- 代码仓库：选择 `Cltuse/Compus_Facilities_Booking`
- 分支：选择 `detached5`
- 端口：填写 `80`

如果高级设置中支持指定 Dockerfile 路径，填写：

- `Dockerfile`

如果支持指定构建上下文，填写：

- `/`

## 3. 必填环境变量

至少配置以下环境变量：

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL=jdbc:mysql://<你的MySQL地址>:3306/campus_facility_booking?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai`
- `DB_USERNAME=<数据库用户名>`
- `DB_PASSWORD=<数据库密码>`
- `JWT_SECRET=<改成你自己的高强度密钥>`

建议同时配置：

- `APP_CORS_ALLOWED_ORIGIN_PATTERNS=*`
- `FILE_UPLOAD_DIR=/app/files`
- `SERVER_PORT=80`

## 4. 数据库准备

先创建数据库：

- 数据库名：`campus_facility_booking`

再导入根目录下的备份文件：

- `campus_facility_booking-10-backup.sql`

生产环境建议确认数据库字符集为 `utf8mb4`。

## 5. 部署后的访问说明

部署成功后：

- 打开服务默认域名访问前端页面
- 前端会通过同域名下的 `/api` 调用后端
- 不再需要单独部署 Vite 开发服务器

## 6. 当前限制

- `files/` 目录会被打进镜像，适合携带初始图片资源
- 运行期新上传的文件仍保存在容器内，重新部署后可能丢失

如果后续你要长期在线使用，建议把上传文件迁移到对象存储，而不是继续放在容器本地目录。
