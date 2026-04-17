# 校园公共设施预约管理系统 - 系统说明文档

## 1. 系统概述

### 1.1 项目简介

本系统是一个面向校园场景的公共设施预约与运维管理平台，服务对象主要包括管理员、普通用户和维护人员。系统支持公共设施浏览、预约申请、预约审批、签到核验、维护记录、通知公告、违规处理、黑名单管理、操作日志审计等完整业务流程。

项目采用前后端分离架构：

- 后端基于 Spring Boot 3 + Spring Data JPA + Spring Security + JWT。
- 前端基于 Vue 3 + Vite + Element Plus。
- 数据库使用 MySQL，字符集为 `utf8mb4`。

系统当前业务语义中已将传统“实验室设备预约”扩展为“校园公共设施预约”。因此，旧文档中的 `equipment / equipment_category`，在当前代码与数据库实现中实际对应为：

- `equipment` -> `facility`
- `equipment_category` -> `facility_category`

本文档以下章节会同时保留业务口径与实际表名映射，避免维护时产生歧义。

### 1.2 建设目标

系统的主要目标包括：

- 提供统一的校园公共设施预约入口。
- 提供预约审核、签到核验、爽约识别、违规处理等闭环管理能力。
- 支持设施维护、故障统计和状态追踪。
- 为管理员提供规则配置、公告发布、黑名单管理与操作审计能力。
- 为后续推荐、天气、热度统计等扩展功能预留基础数据结构。

### 1.3 适用读者

本文档面向以下角色：

- 项目维护者
- 新加入项目的后端/前端开发人员
- 测试与实施人员
- 需要了解系统结构与数据模型的技术文档读者

---

## 2. 功能模块详解

### 2.1 用户与认证模块

功能说明：

- 用户注册、登录、退出
- JWT 令牌签发与鉴权
- 个人资料维护
- 修改密码
- 管理员创建/修改/删除用户

业务特点：

- 登录接口位于 `/api/user/login`
- 认证凭证为 `Authorization: Bearer <token>`
- 当前系统角色包括 `ADMIN`、`MAINTAINER`、`USER/TEACHER/STUDENT`
- 旧数据中的明文密码已通过登录兼容逻辑支持自动迁移为 BCrypt 哈希

### 2.2 设施管理模块

功能说明：

- 设施基本信息管理
- 设施分类管理
- 设施图片上传
- 设施状态维护
- 设施检索与分页展示

业务特点：

- 当前代码中设施实体为 `Facility`
- 设施状态与预约、维护模块联动
- 管理员可维护设施档案，普通用户仅浏览和预约

### 2.3 预约管理模块

功能说明：

- 用户创建预约
- 管理员审批/拒绝预约
- 用户取消预约
- 管理员或用户完成签到/签退流程
- 预约二维码/核销码校验
- 预约统计与趋势分析

业务特点：

- 预约流程受到规则配置模块约束
- 系统支持时间冲突校验
- 系统支持自动识别爽约并生成违规记录
- 预约状态、签到状态分别独立维护

### 2.4 维护管理模块

功能说明：

- 维护任务创建、修改、完成
- 维护人员信息记录
- 维护成本、结果、起止时间跟踪
- 设施故障排行、维护类型分布、平均维护时长统计

业务特点：

- 维护记录与设施状态联动
- 维护开始前或进行中会影响设施可预约状态
- 已完成维护会尝试恢复设施状态为可用

### 2.5 通知公告模块

功能说明：

- 管理员发布公告
- 公告草稿/已发布/定时发布状态管理
- 用户查看公告列表与详情

业务特点：

- 公告实体为 `Notice`
- 支持发布人、发布时间、定时发布时间字段

### 2.6 规则配置模块

功能说明：

- 管理员配置预约规则
- 支持全局规则和分类级规则
- 支持预约时长、提前预约、取消截止、开放时间、审批要求等设置

业务特点：

- 分类规则优先于全局规则
- 是预约校验链路中的核心依赖模块

### 2.7 反馈与违规管理模块

反馈模块：

- 用户提交建议、投诉、咨询
- 管理员回复反馈并更新处理状态

违规模块：

- 管理员或系统记录违规行为
- 审批违规记录
- 影响用户信用分与违规次数
- 可与黑名单模块联动

### 2.8 黑名单与审计日志模块

黑名单模块：

- 管理员手动拉黑用户
- 支持永久拉黑、定期拉黑、手动移除
- 支持状态过滤与分页查询

操作日志模块：

- 记录管理员/维护人员关键操作
- 记录操作人、操作类型、目标对象、详情、IP、时间
- 为系统审计、追责、问题定位提供依据

### 2.9 推荐、天气与扩展能力模块

从现有代码可见，系统已具备以下扩展模块或半成品能力：

- 用户推荐与协同过滤
- 设施热度评分
- 天气查询
- 城市代码表

说明：

- 这些能力不是当前核心预约流程的主链路，但已在代码层面形成实体或服务，可以视为可扩展子系统。

---

## 3. 技术架构说明

### 3.1 总体架构

系统采用典型前后端分离架构：

1. 前端 Vue 应用负责页面渲染、交互、路由控制、API 调用。
2. 后端 Spring Boot 提供 RESTful API、业务逻辑、权限校验、数据持久化。
3. MySQL 负责结构化业务数据存储。
4. JWT 用于无状态认证。

文字架构图：

```text
浏览器 / 前端 Vue
    |
    | HTTP / JSON / Bearer Token
    v
Spring Boot REST API
    |-- Controller
    |-- Service
    |-- Repository (JPA)
    |-- Security (JWT / RBAC)
    v
MySQL (utf8mb4)
```

### 3.2 后端技术栈

| 技术 | 版本/组件 | 用途 |
|------|-----------|------|
| Java | 21 | 后端开发语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Data JPA | Hibernate | ORM 与持久化 |
| Spring Security | Boot Starter | 认证与授权 |
| JWT | `jjwt` 0.12.5 | 令牌签发与解析 |
| MySQL | 5.7/8.x 兼容 | 关系型数据库 |
| Maven | 项目构建工具 | 构建与依赖管理 |
| Lombok | - | 简化实体/POJO 代码 |

### 3.3 前端技术栈

| 技术 | 版本/组件 | 用途 |
|------|-----------|------|
| Vue | 3.5.x | 前端框架 |
| Vite | 7.2.x | 构建与开发服务器 |
| Vue Router | 4.6.x | 路由管理 |
| Element Plus | 2.11.x | UI 组件库 |
| Axios | 1.13.x | HTTP 客户端 |
| ECharts | 6.x | 统计图表 |

### 3.4 后端分层

后端按职责大致分为：

- `controller`：API 接口入口，负责请求接收和返回封装
- `service`：业务逻辑处理
- `repository`：数据库访问
- `entity`：JPA 实体映射
- `config`：安全、跨域、全局配置
- `security`：JWT 认证过滤器、当前用户上下文
- `dto`：登录、注册、更新等输入输出对象
- `common/util`：公共响应与工具类

### 3.5 前端结构

前端按职责大致分为：

- `views`：页面组件
- `layouts`：管理端/用户端/维护端整体布局
- `components`：通用组件
- `router`：路由与守卫
- `api`：接口封装
- `utils`：认证与请求封装

### 3.6 认证与授权机制

当前系统认证流程：

1. 用户通过用户名和密码调用 `/api/user/login`
2. 后端校验用户信息并签发 JWT
3. 前端将 token 存入本地存储
4. 后续请求通过 `Authorization` 头携带 JWT
5. 后端 `JwtAuthenticationFilter` 解析 token 并构造认证上下文
6. `Spring Security + @PreAuthorize` 实现角色控制

角色控制方式：

- 路由层：前端根据 `user.role` 做体验级跳转控制
- 接口层：后端通过 Spring Security 做强制权限校验

### 3.7 目录结构

#### 后端目录

```text
backend/
├─ pom.xml
├─ src/main/java/com/facility/booking
│  ├─ annotation
│  ├─ common
│  ├─ config
│  ├─ controller
│  ├─ dto
│  ├─ entity
│  ├─ repository
│  ├─ security
│  ├─ service
│  └─ util
└─ src/main/resources
   ├─ application.properties
   ├─ application-dev.properties
   ├─ application-prod.properties
   └─ sql/
```

#### 前端目录

```text
frontend/
├─ package.json
├─ src
│  ├─ api
│  ├─ components
│  ├─ layouts
│  ├─ router
│  ├─ utils
│  └─ views
│     ├─ admin
│     ├─ user
│     └─ maintainer
```

---

## 4. 数据库设计

### 4.1 设计原则

数据库设计遵循以下原则：

- 以 JPA 实体映射为主，结合 SQL 备份校验
- 字符集统一使用 `utf8mb4`
- 核心业务表使用自增主键
- 大多数关联采用“逻辑外键字段 + 业务层关联查询”的方式，而非所有表都显式声明 JPA `@ManyToOne`
- 对于原文档未明确、但可从代码与业务逻辑推出的字段，文档中标注“（根据业务逻辑推断）”

### 4.2 表命名映射说明

为兼容原开发文档中的旧术语，需特别说明：

| 业务口径 | 当前实际实现 |
|------|------|
| equipment | facility |
| equipment_category | facility_category |

因此下文的“设备表”“设备分类表”对应当前项目真实表：

- `facility`
- `facility_category`

### 4.3 表间关系说明

主要关系如下：

- `reservation.user_id` 关联 `user.id`
- `reservation.facility_id` 关联 `facility.id`
- `reservation.verified_by` 关联 `user.id`
- `maintenance.facility_id` 关联 `facility.id`
- `maintenance.maintainer_id` 关联 `user.id`
- `notice.publisher_id` 关联 `user.id`
- `feedback.user_id` 关联 `user.id`
- `feedback.reply_by` 关联 `user.id`
- `violation_record.user_id` 关联 `user.id`
- `violation_record.reservation_id` 关联 `reservation.id`
- `violation_record.reported_by` 关联 `user.id`
- `blacklist.user_id` 关联 `user.id`
- `blacklist.operator_id` 关联 `user.id`
- `operation_log.operator_id` 关联 `user.id`
- `rule_config.category_id` 关联 `facility_category.id`

文字关系图：

```text
user
 ├─< reservation >─ facility
 ├─< maintenance >─ facility
 ├─< notice
 ├─< feedback
 ├─< violation_record >─ reservation
 ├─< blacklist
 └─< operation_log

facility_category
 └─< rule_config
```

### 4.4 用户表 `user`

表说明：

用户表是整个系统的身份中心，保存登录账号、密码、角色、联系方式、状态、信用分等信息。该表同时为预约、维护、公告、反馈、黑名单、违规记录和操作日志等模块提供用户主数据支撑。系统的 JWT 认证、角色授权、个人资料管理都依赖此表。`credit_score` 与 `violation_count` 用于信用治理与违规联动，体现了系统的业务约束能力。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 用户唯一标识 |
| username | VARCHAR(50) | 否 | 无 | UNIQUE | 登录用户名 |
| password | VARCHAR(100) | 否 | 无 | - | 登录密码，当前应保存 BCrypt 哈希 |
| real_name | VARCHAR(50) | 否 | 无 | - | 真实姓名 |
| role | VARCHAR(20) | 否 | 无 | - | 用户角色 |
| phone | VARCHAR(20) | 是 | NULL | - | 手机号 |
| email | VARCHAR(100) | 是 | NULL | 业务上建议唯一 | 邮箱地址 |
| avatar | VARCHAR(500) | 是 | NULL | - | 头像 URL |
| status | VARCHAR(20) | 是 | ACTIVE | - | 用户状态 |
| credit_score | INT | 是 | 100 | - | 用户信用分 |
| violation_count | INT | 是 | 0 | - | 用户违规次数 |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |

枚举说明：

- `role`
  - `ADMIN`：管理员
  - `MAINTAINER`：维护人员
  - `USER`：普通用户
  - `TEACHER`：教师用户
  - `STUDENT`：学生用户
- `status`
  - `ACTIVE`：启用
  - `INACTIVE`：停用

### 4.5 设备表 `equipment`（实际表名：`facility`）

表说明：

该表用于保存校园公共设施主档案。虽然原开发文档使用“equipment”术语，但当前代码已统一为 `facility`。表中记录了设施名称、型号、分类、位置、状态、购置日期、价格、图片和描述等信息。该表既是前台设施展示的来源，也是预约与维护流程中的关键主表。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 设施唯一标识 |
| name | VARCHAR(100) | 否 | 无 | - | 设施名称 |
| model | VARCHAR(100) | 是 | NULL | - | 型号/规格 |
| category | VARCHAR(50) | 是 | NULL | - | 分类名称，当前为字符串冗余字段 |
| location | VARCHAR(200) | 是 | NULL | - | 所在位置 |
| status | VARCHAR(20) | 是 | AVAILABLE | - | 设施状态 |
| description | TEXT | 是 | NULL | - | 设施描述 |
| purchase_date | DATE | 是 | NULL | - | 购置日期 |
| price | DECIMAL(10,2) | 是 | NULL | - | 采购价格 |
| image_url | VARCHAR(500) | 是 | `/files/facility/default-facility.svg` | - | 展示图片地址 |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |

枚举说明：

- `status`
  - `AVAILABLE`：可预约/可用
  - `MAINTENANCE`：维护中
  - `DAMAGED`：损坏或不可用

说明：

- `category` 当前不是严格外键，而是分类名称字符串。（根据代码实现确认）
- 若后续做强一致模型，建议改为 `category_id` 外键。

### 4.6 设备分类表 `equipment_category`（实际表名：`facility_category`）

表说明：

该表用于维护设施分类信息，支持后台分类排序、启停和描述维护。规则配置模块中的分类级预约规则通过此表关联。当前项目中设施主表并未直接保存 `category_id`，而是保留了分类名称字符串，因此该表更多承担“分类定义”和“规则适配基准”的角色。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 分类唯一标识 |
| category_name | VARCHAR(100) | 否 | 无 | UNIQUE | 分类名称 |
| description | TEXT | 是 | NULL | - | 分类描述 |
| sort_order | INT | 是 | 0 | - | 排序值 |
| status | VARCHAR(20) | 是 | ACTIVE | - | 分类状态 |
| created_time | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_time | DATETIME | 是 | 当前时间 | - | 更新时间 |

枚举说明：

- `status`
  - `ACTIVE`：启用
  - `INACTIVE`：停用（根据业务逻辑推断）

### 4.7 预约表 `reservation`

表说明：

预约表是系统最核心的业务表之一，承载用户与设施之间的预约关系。其记录了预约时间段、预约用途、审批状态、签到状态、核销信息、审批备注以及审计时间戳。预约流程、规则校验、时间冲突检测、签到签退、爽约识别、违规处理等关键业务都围绕此表展开。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 预约唯一标识 |
| facility_id | BIGINT | 否 | 无 | FK（业务关联） | 关联设施 ID |
| user_id | BIGINT | 否 | 无 | FK（业务关联） | 关联用户 ID |
| start_time | DATETIME | 否 | 无 | - | 预约开始时间 |
| end_time | DATETIME | 否 | 无 | - | 预约结束时间 |
| purpose | TEXT | 是 | NULL | - | 预约用途说明 |
| status | VARCHAR(20) | 是 | PENDING | - | 预约状态 |
| admin_remark | TEXT | 是 | NULL | - | 管理员审批备注 |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |
| checkin_time | DATETIME | 是 | NULL | - | 实际签到时间 |
| checkout_time | DATETIME | 是 | NULL | - | 实际签退时间 |
| checkin_status | VARCHAR(20) | 是 | NOT_CHECKED | - | 签到状态 |
| verification_code | VARCHAR(64) | 是 | NULL | UNIQUE（业务建议） | 核销码/二维码内容 |
| verified_by | BIGINT | 是 | NULL | FK（业务关联） | 核验操作人 ID |
| verified_time | DATETIME | 是 | NULL | - | 核验时间 |

枚举说明：

- `status`
  - `PENDING`：待审批
  - `APPROVED`：已通过
  - `REJECTED`：已拒绝
  - `COMPLETED`：已完成
  - `CANCELLED`：已取消
- `checkin_status`
  - `NOT_CHECKED`：未签到
  - `CHECKED_IN`：已签到
  - `CHECKED_OUT`：已签退
  - `MISSED`：爽约

### 4.8 维护表 `maintenance`

表说明：

维护表记录设施维护任务的全过程，包括维护类型、描述、维护人员、起止时间、维护状态、成本和结果。系统利用该表完成设施状态联动、维护统计分析和故障排行。对于维护人员工作台而言，该表是核心业务对象。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 维护记录唯一标识 |
| facility_id | BIGINT | 否 | 无 | FK（业务关联） | 关联设施 ID |
| maintainer_id | BIGINT | 是 | NULL | FK（业务关联） | 维护人员用户 ID |
| maintenance_type | VARCHAR(50) | 是 | NULL | - | 维护类型 |
| description | TEXT | 是 | NULL | - | 维护问题/任务描述 |
| cost | DECIMAL(10,2) | 是 | NULL | - | 维护成本 |
| maintainer | VARCHAR(50) | 是 | NULL | - | 维护人员姓名冗余字段 |
| start_time | DATETIME | 是 | NULL | - | 开始时间 |
| end_time | DATETIME | 是 | NULL | - | 结束时间 |
| status | VARCHAR(20) | 是 | PENDING | - | 维护状态 |
| result | TEXT | 是 | NULL | - | 维护结果 |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |

枚举说明：

- `maintenance_type`
  - `ROUTINE`：常规维护
  - `REPAIR`：故障维修
  - `UPGRADE`：设备升级
  - `OTHER`：其他（根据业务逻辑推断）
- `status`
  - `PENDING`：待处理
  - `IN_PROGRESS`：进行中
  - `COMPLETED`：已完成
  - `CANCELLED`：已取消（根据业务逻辑推断）

### 4.9 公告表 `notice`

表说明：

公告表保存系统公告内容及发布信息，是管理员向用户传达停机通知、规则更新、维护安排、系统消息的主要渠道。支持草稿、已发布、定时发布等状态，具备较强的运维支撑属性。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 公告唯一标识 |
| title | VARCHAR(200) | 否 | 无 | - | 公告标题 |
| content | TEXT | 否 | 无 | - | 公告正文 |
| publisher_id | BIGINT | 否 | 无 | FK（业务关联） | 发布人用户 ID |
| publisher_name | VARCHAR(50) | 是 | NULL | - | 发布人姓名冗余 |
| publish_time | DATETIME | 是 | 当前时间 | - | 发布时间 |
| status | VARCHAR(20) | 是 | PUBLISHED | - | 公告状态 |
| scheduled_time | DATETIME | 是 | NULL | - | 定时发布时间 |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |

枚举说明：

- `status`
  - `DRAFT`：草稿
  - `PUBLISHED`：已发布
  - `SCHEDULED`：定时发布

### 4.10 反馈表 `feedback`

表说明：

反馈表用于收集用户对系统和设施使用过程中的建议、投诉和咨询，同时支持管理员回复。该表是用户体验治理的重要组成部分。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 反馈唯一标识 |
| user_id | BIGINT | 否 | 无 | FK（业务关联） | 提交人用户 ID |
| title | VARCHAR(200) | 否 | 无 | - | 反馈标题 |
| content | TEXT | 否 | 无 | - | 反馈正文 |
| type | VARCHAR(20) | 是 | SUGGESTION | - | 反馈类型 |
| status | VARCHAR(20) | 是 | PENDING | - | 处理状态 |
| reply | TEXT | 是 | NULL | - | 回复内容 |
| reply_time | DATETIME | 是 | NULL | - | 回复时间 |
| reply_by | BIGINT | 是 | NULL | FK（业务关联） | 回复人用户 ID |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |

枚举说明：

- `type`
  - `SUGGESTION`：建议
  - `COMPLAINT`：投诉
  - `QUESTION`：咨询
- `status`
  - `PENDING`：待处理
  - `PROCESSED`：已处理

### 4.11 规则配置表 `rule_config`

表说明：

规则配置表定义预约策略，是预约业务规则引擎的简化实现。支持配置最短/最长预约时长、提前预约限制、当日预约限制、每日预约次数、活跃预约上限、取消截止、审批要求、开放时段等。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 规则唯一标识 |
| category_id | BIGINT | 是 | NULL | FK（业务关联） | 分类 ID，NULL 表示全局规则 |
| min_duration_minutes | INT | 否 | 30 | - | 最短预约时长（分钟） |
| max_duration_minutes | INT | 否 | 120 | - | 最长预约时长（分钟） |
| advance_days_max | INT | 否 | 7 | - | 最早可提前预约的天数上限 |
| advance_cutoff_minutes | INT | 否 | 60 | - | 开始前至少提前多少分钟预约 |
| allow_same_day_booking | BOOLEAN | 否 | true | - | 是否允许当日预约 |
| max_bookings_per_day | INT | 否 | 2 | - | 每日预约次数上限 |
| max_active_bookings | INT | 否 | 3 | - | 活跃预约上限 |
| cancel_deadline_minutes | INT | 否 | 30 | - | 取消截止时间（距开始前分钟数） |
| need_approval | BOOLEAN | 否 | false | - | 是否需要管理员审批 |
| open_time | TIME | 否 | 无 | - | 开放开始时间 |
| close_time | TIME | 否 | 无 | - | 开放结束时间 |
| is_active | BOOLEAN | 否 | true | - | 是否当前生效 |
| time_slot_minutes | INT | 否 | 30 | - | 时间粒度/预约时间槽大小 |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |

### 4.12 违规记录表 `violation_record`

表说明：

违规记录表承载用户违规行为的登记、审核与追踪，是信用分、黑名单、爽约惩戒等机制的基础数据来源。系统既支持管理员手工登记违规，也支持系统自动识别爽约后生成违规记录。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 违规记录唯一标识 |
| user_id | BIGINT | 否 | 无 | FK（业务关联） | 违规用户 ID |
| reservation_id | BIGINT | 是 | NULL | FK（业务关联） | 关联预约 ID |
| violation_type | VARCHAR(50) | 否 | 无 | - | 违规类型 |
| description | TEXT | 是 | NULL | - | 违规描述 |
| penalty_points | INT | 是 | 0 | - | 扣分值 |
| status | VARCHAR(20) | 是 | PENDING | - | 处理状态 |
| remark | TEXT | 是 | NULL | - | 审批备注 |
| reported_by | BIGINT | 是 | NULL | FK（业务关联） | 上报人 ID |
| reported_time | DATETIME | 是 | NULL | - | 上报时间 |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |
| updated_at | DATETIME | 是 | 当前时间 | - | 更新时间 |

枚举说明：

- `status`
  - `PENDING`：待处理
  - `PROCESSED`：已处理
  - `REJECTED`：已拒绝

### 4.13 黑名单表 `blacklist`

表说明：

黑名单表用于限制高风险或严重违规用户继续使用系统资源。根据 SQL 备份可知，该表对 `user` 与 `operator` 都设置了外键约束，是当前数据库中关系约束较明确的业务表之一。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 黑名单记录 ID |
| user_id | BIGINT | 否 | 无 | FK -> user.id | 被拉黑用户 ID |
| reason | VARCHAR(255) | 否 | 无 | - | 拉黑原因 |
| start_time | DATETIME | 否 | 无 | - | 拉黑开始时间 |
| end_time | DATETIME | 是 | NULL | - | 拉黑结束时间，NULL 表示永久 |
| status | VARCHAR(20) | 是 | ACTIVE | - | 黑名单状态 |
| operator_id | BIGINT | 是 | NULL | FK -> user.id | 操作管理员 ID |
| created_at | DATETIME | 是 | CURRENT_TIMESTAMP | - | 创建时间 |
| updated_at | DATETIME | 是 | CURRENT_TIMESTAMP | - | 更新时间 |

枚举说明：

- `status`
  - `ACTIVE`：生效中
  - `EXPIRED`：已过期
  - `REMOVED`：手动移除

### 4.14 操作日志表 `operation_log`

表说明：

操作日志表用于记录后台关键行为，是系统安全审计与问题追踪的重要依据。日志通常由注解 + 切面或统一拦截逻辑写入。

| 字段名 | 数据类型 | 允许空 | 默认值 | 约束 | 说明 |
|------|------|----|------|------|------|
| id | BIGINT | 否 | 自增 | PK, AUTO_INCREMENT | 日志记录 ID |
| operator_id | BIGINT | 是 | NULL | FK（业务关联） | 操作人 ID |
| operator_name | VARCHAR(50) | 是 | NULL | - | 操作人姓名冗余 |
| operation_type | VARCHAR(50) | 否 | 无 | INDEX（建议） | 操作类型 |
| target_id | BIGINT | 是 | NULL | - | 目标业务对象 ID |
| detail | TEXT | 是 | NULL | - | 操作明细 |
| ip_address | VARCHAR(50) | 是 | NULL | - | 操作来源 IP |
| created_at | DATETIME | 是 | 当前时间 | - | 创建时间 |

### 4.15 索引与性能建议

结合现有代码与 SQL 脚本，建议重点关注以下索引：

- `reservation(facility_id, start_time, end_time, status)`：冲突检测、时间查询
- `reservation(user_id, status)`：用户活跃预约统计
- `reservation(created_at)`：趋势统计
- `maintenance(facility_id, created_at, status)`：维护统计与排行
- `feedback(user_id, status, created_at)`：反馈分页
- `notice(status, publish_time)`：公告发布列表
- `operation_log(operator_id, operation_type, created_at)`：日志检索
- `blacklist(user_id, status)`：黑名单生效查询

---

## 5. 接口概览

### 5.1 统一响应格式

系统接口统一使用 `Result<T>` 返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 5.2 主要接口分组

| 模块 | 路径前缀 | 说明 |
|------|------|------|
| 用户与认证 | `/api/user` | 登录、注册、用户管理、改密、资料维护 |
| 设施管理 | `/api/facility` | 设施列表、详情、创建、更新、状态维护 |
| 分类管理 | `/api/facility-category` | 分类维护、启停、分页 |
| 预约管理 | `/api/reservation` | 创建预约、审批、取消、签到核验、统计 |
| 维护管理 | `/api/maintenance` | 维护任务、统计、汇总 |
| 公告管理 | `/api/notice` | 公告发布与查询 |
| 用户端聚合接口 | `/api/user-client` | 用户侧规则、反馈、违规记录 |
| 管理端聚合接口 | `/api/admin` | 规则、黑名单、操作日志、仪表盘统计 |
| 违规管理 | `/api/violation` | 违规记录、审批、统计 |
| 文件上传 | `/api/file` | 图片/头像上传 |

### 5.3 关键接口示例

#### 登录

`POST /api/user/login`

请求：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "role": "ADMIN",
      "status": "ACTIVE"
    }
  }
}
```

#### 创建预约

`POST /api/reservation`

请求：

```json
{
  "facilityId": 12,
  "userId": 5,
  "startTime": "2026-04-18 10:00:00",
  "endTime": "2026-04-18 12:00:00",
  "purpose": "课程讨论"
}
```

#### 审批预约

`PUT /api/reservation/{id}/approve`

请求：

```json
{
  "adminRemark": "审批通过"
}
```

### 5.4 权限特征

- 登录、注册接口匿名可访问
- 管理员接口通常要求 `ADMIN`
- 维护端接口通常要求 `MAINTAINER`
- 普通用户只能访问自己的个人数据和用户端功能

---

## 6. 各模块业务流程

### 6.1 用户登录流程

```text
输入用户名密码
  -> 调用 /api/user/login
  -> 校验用户名
  -> 校验密码（兼容旧明文并自动迁移为 BCrypt）
  -> 签发 JWT
  -> 前端保存 token 与用户信息
  -> 按角色跳转首页
```

### 6.2 预约申请与审核流程

```text
用户选择设施与时间
  -> 前端提交预约申请
  -> 后端校验时间格式、时间冲突、规则配置
  -> 写入 reservation 表，状态为 PENDING 或直接 APPROVED（取决于规则）
  -> 管理员查看待审核列表
  -> 审批通过 / 拒绝
  -> 用户查看预约结果
```

### 6.3 签到核验流程

```text
已审批预约
  -> 用户获取核销码 / 二维码
  -> 到场后由管理员扫码核验
  -> 更新 checkin_status = CHECKED_IN
  -> 使用结束后再次核验
  -> 更新 checkin_status = CHECKED_OUT
```

### 6.4 爽约识别流程

```text
定时任务扫描已批准但未签到的预约
  -> 判断是否超过允许签到时间窗口
  -> 标记 checkin_status = MISSED
  -> 更新预约状态为 COMPLETED
  -> 自动生成 violation_record
  -> 后续可能影响信用分或黑名单
```

### 6.5 设施维护流程

```text
管理员/维护人员创建维护任务
  -> 维护记录写入 maintenance
  -> 设施状态改为 MAINTENANCE
  -> 维护人员处理任务
  -> 填写结果、费用、结束时间
  -> 维护任务完成
  -> 设施状态恢复为 AVAILABLE（若适用）
```

### 6.6 黑名单处理流程

```text
发现严重违规用户
  -> 管理员创建黑名单记录
  -> 设置生效时间与结束时间
  -> 用户进入限制状态
  -> 到期自动过期或管理员手动移除
```

### 6.7 反馈处理流程

```text
用户提交反馈
  -> 进入待处理状态
  -> 管理员查看反馈列表
  -> 回复反馈并更新状态为已处理
  -> 用户在个人反馈页面查看回复
```

### 6.8 预约审核流程图（文字版）

```text
开始
  -> 用户提交预约
  -> 是否符合预约规则？
      -> 否：返回错误，结束
      -> 是：继续
  -> 是否需要审批？
      -> 否：状态设为 APPROVED，结束
      -> 是：状态设为 PENDING
  -> 管理员审核
      -> 通过：状态改为 APPROVED
      -> 拒绝：状态改为 REJECTED，并写入 adminRemark
结束
```

---

## 7. 角色权限矩阵

说明：

- 原任务要求至少区分“管理员 vs 普通用户”。
- 当前系统代码中还实际存在“维护人员”角色，因此下表按真实实现扩展。

| 功能 | 管理员 ADMIN | 维护人员 MAINTAINER | 普通用户 USER/TEACHER/STUDENT |
|------|------|------|------|
| 登录系统 | 是 | 是 | 是 |
| 注册账号 | 可创建其他用户 / 用户自助注册 | 否 | 是 |
| 查看设施列表 | 是 | 是 | 是 |
| 新增/修改/删除设施 | 是 | 部分查看 | 否 |
| 管理设施分类 | 是 | 否 | 否 |
| 提交预约 | 可代操作（部分场景） | 否 | 是 |
| 查看自己的预约 | 是 | 否 | 是 |
| 查看所有预约 | 是 | 部分 | 否 |
| 审核预约 | 是 | 否 | 否 |
| 取消预约 | 是 | 否 | 仅自己的预约 |
| 核验签到/签退 | 是 | 视业务授权 | 否 |
| 创建维护任务 | 是 | 是 | 否 |
| 完成维护任务 | 是 | 是 | 否 |
| 发布公告 | 是 | 否 | 否 |
| 配置预约规则 | 是 | 否 | 否 |
| 处理反馈 | 是 | 否 | 否 |
| 记录/审批违规 | 是 | 可上报 | 否 |
| 管理黑名单 | 是 | 否 | 否 |
| 查看操作日志 | 是 | 否 | 否 |

---

## 8. 部署

### 8.1 环境要求

| 组件 | 建议版本 |
|------|------|
| JDK | 21 |
| Maven | 3.9+ |
| Node.js | 20+ / 22+ |
| MySQL | 5.7+ 或 8.x |

### 8.2 数据库准备

数据库名称：

- 业务库：`campus_facility_booking`
- 提供的备份文件：`campus_facility_booking-10-backup.sql`

字符集：

- `utf8mb4`

导入示例：

```bash
mysql -u root -p campus_facility_booking < campus_facility_booking-10-backup.sql
```

### 8.3 后端配置

核心配置文件：

- [application.properties](/E:/Stdio/JetBrains/Intellij/Programs/Java/Thesis/Compus_Facilities_Booking/backend/src/main/resources/application.properties)
- [application-dev.properties](/E:/Stdio/JetBrains/Intellij/Programs/Java/Thesis/Compus_Facilities_Booking/backend/src/main/resources/application-dev.properties)
- `application-prod.properties`（生产环境）

开发环境关键配置：

| 配置项 | 默认值 | 说明 |
|------|------|------|
| `server.port` | `5681` | 后端端口 |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/campus_facility_booking...` | 数据库连接 |
| `spring.datasource.username` | `root` | 数据库用户名 |
| `spring.datasource.password` | `123456` | 数据库密码 |
| `spring.jpa.hibernate.ddl-auto` | `update` | 开发环境自动更新表结构 |
| `spring.jpa.show-sql` | `true` | 开发环境显示 SQL |
| `jwt.expiration-millis` | `86400000` | token 有效期 24 小时 |

说明：

- 生产环境建议关闭 `ddl-auto=update`
- 生产环境建议关闭 `show-sql=true`
- 生产环境建议通过环境变量传入 `JWT_SECRET` 和数据库连接信息

### 8.4 启动后端

```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

默认访问：

- `http://localhost:5681`

### 8.5 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认访问：

- `http://localhost:3000`

### 8.6 前后端联调说明

- 前端通过 Axios 调用后端 `/api/**` 接口
- 请求头自动携带 `Authorization: Bearer <token>`
- 未登录或 token 过期时，前端路由守卫会跳转到登录页

### 8.7 生产部署建议

- 前端构建：

```bash
cd frontend
npm run build
```

- 后端打包：

```bash
cd backend
mvn clean package
java -jar target/facility-management-1.0.0.jar
```

- 建议配套：
  - Nginx 反向代理
  - HTTPS
  - 独立文件存储目录
  - 数据库备份与日志轮转

---

## 9. 附录

### 9.1 初始账户

原开发文档给出的初始账户如下：

| 用户名 | 密码 | 角色 | 说明 |
|------|------|------|------|
| `admin` | `123456` | ADMIN | 管理员账户 |
| `user1` | `123456` | USER | 普通测试账户 |

说明：

- 如果数据库中的旧密码为明文，当前登录逻辑会在首次成功登录后自动迁移为 BCrypt 哈希。
- 生产环境应立即修改默认口令。

### 9.2 常见状态与枚举汇总

#### 用户角色

- `ADMIN`
- `MAINTAINER`
- `USER`
- `TEACHER`
- `STUDENT`

#### 设施状态

- `AVAILABLE`
- `MAINTENANCE`
- `DAMAGED`

#### 预约状态

- `PENDING`
- `APPROVED`
- `REJECTED`
- `COMPLETED`
- `CANCELLED`

#### 签到状态

- `NOT_CHECKED`
- `CHECKED_IN`
- `CHECKED_OUT`
- `MISSED`

#### 维护状态

- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELLED`

#### 公告状态

- `DRAFT`
- `PUBLISHED`
- `SCHEDULED`

#### 黑名单状态

- `ACTIVE`
- `EXPIRED`
- `REMOVED`

### 9.3 生成说明

本说明文档基于以下信息整理：

- `开发文档.md`
- `campus_facility_booking-10-backup.sql`
- 当前后端实体类、控制器、路由与配置文件

其中以下内容带有“根据业务逻辑推断”性质：

- 部分未在 SQL 中完整约束但可从实体和控制器推导出的关系
- 个别状态枚举的边界取值
- 部分数据库约束建议与索引建议

### 9.4 修订历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-04-17 | 根据现有代码、数据库备份与开发文档生成系统说明文档 |

