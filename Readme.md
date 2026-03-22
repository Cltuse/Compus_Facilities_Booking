# 校园公共设施预约管理系统说明文档

## 1. 项目概述

### 1.1 项目背景
校园公共设施预约管理系统是为高校设计的智能化设施管理平台，旨在解决校园内各类公共设施（如会议室、实验室、体育馆等）的预约管理问题，提高设施使用效率，优化资源配置。

### 1.2 项目目标
- 实现校园设施的统一预约管理
- 提供便捷的在线预约服务
- 建立完善的信用评价体系
- 支持多角色权限管理
- 实现数据统计与分析功能

### 1.3 技术架构
- **后端框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **持久层**: Spring Data JPA
- **Java版本**: Java 21
- **构建工具**: Maven
- **服务器端口**: 5681

## 2. 系统功能模块

### 2.1 用户管理模块
#### 功能特性
- 用户注册与登录
- 多角色支持（学生、教师、管理员）
- 个人信息管理
- 信用评分系统
- 头像上传功能

#### 用户角色定义
- **STUDENT**: 学生用户，基础预约权限
- **TEACHER**: 教师用户，扩展预约权限
- **ADMIN**: 管理员，系统管理权限
- **USER**: 通用用户角色

#### 信用评分机制
- 初始信用分：100分
- 爽约扣分：每次扣10分
- 违规扣分：根据违规类型扣5-20分
- 信用分低于60分限制预约

### 2.2 设施管理模块
#### 设施分类
- 会议室
- 实验室
- 体育馆
- 自习室
- 研讨室
- 琴房
- 充电桩

#### 设施状态管理
- **AVAILABLE**: 可用状态
- **IN_USE**: 使用中
- **MAINTENANCE**: 维护中
- **DAMAGED**: 损坏待修

#### 设施信息管理
- 基本信息维护（名称、型号、位置）
- 设施图片管理
- 采购信息管理
- 使用说明配置

### 2.3 预约管理模块
#### 预约流程
1. 用户选择设施和时间段
2. 系统检查可用性和规则
3. 提交预约申请
4. 管理员审核（如需）
5. 预约成功通知
6. 现场签到使用
7. 使用完成后签退

#### 预约状态
- **PENDING**: 待审核
- **APPROVED**: 已批准
- **REJECTED**: 已拒绝
- **COMPLETED**: 已完成
- **CANCELLED**: 已取消

#### 签到签退机制
- 预约开始前15分钟可签到
- 预约结束后30分钟内须签退
- 生成唯一验证码用于身份验证
- 支持管理员手动签到签退

### 2.4 信用与违规管理
#### 违规类型
- 爽约未到
- 超时占用
- 损坏设施
- 恶意预约
- 未按时签退

#### 处罚措施
- 信用分扣分
- 限制预约权限
- 加入黑名单
- 取消预约资格

#### 黑名单管理
- 支持临时拉黑和永久拉黑
- 可设置拉黑时长
- 支持手动解除黑名单
- 记录违规原因

### 2.5 通知公告模块
#### 通知类型
- 预约成功通知
- 预约审核结果
- 预约提醒
- 违规通知
- 系统公告

#### 通知方式
- 系统内消息
- 邮件通知（扩展）
- 短信通知（扩展）

### 2.6 数据统计模块
#### 统计维度
- 设施使用频率
- 用户预约行为
- 时间段分析
- 违规统计
- 信用分布

#### 报表功能
- 日报表
- 周报表
- 月报表
- 年度统计
- 研讨室
- 琴房
- 充电桩

#### 设施状态管理
- **AVAILABLE**: 可用状态
- **IN_USE**: 使用中
- **MAINTENANCE**: 维护中
- **DAMAGED**: 损坏待修

#### 设施信息管理
- 基本信息维护（名称、型号、位置）
- 设施图片管理
- 采购信息管理
- 使用说明配置

### 2.3 预约管理模块
#### 预约流程
1. 用户选择设施和时间段
2. 系统检查可用性和规则
3. 提交预约申请
4. 管理员审核（如需）
5. 预约成功通知
6. 现场签到使用
7. 使用完成后签退

#### 预约状态
- **PENDING**: 待审核
- **APPROVED**: 已批准
- **REJECTED**: 已拒绝
- **COMPLETED**: 已完成
- **CANCELLED**: 已取消

#### 签到签退机制
- 预约开始前15分钟可签到
- 预约结束后30分钟内须签退
- 生成唯一验证码用于身份验证
- 支持管理员手动签到签退

### 2.4 信用与违规管理
#### 违规类型
- 爽约未到
- 超时占用
- 损坏设施
- 恶意预约
- 未按时签退

#### 处罚措施
- 信用分扣分
- 限制预约权限
- 加入黑名单
- 取消预约资格

#### 黑名单管理
- 支持临时拉黑和永久拉黑
- 可设置拉黑时长
- 支持手动解除黑名单
- 记录违规原因

### 2.5 通知公告模块
#### 通知类型
- 预约成功通知
- 预约审核结果
- 预约提醒
- 违规通知
- 系统公告

#### 通知方式
- 系统内消息
- 邮件通知（扩展）
- 短信通知（扩展）

### 2.6 数据统计模块
#### 统计维度
- 设施使用频率
- 用户预约行为
- 时间段分析
- 违规统计
- 信用分布

#### 报表功能
- 日报表
- 周报表
- 月报表
- 年度统计

## 3. 数据库设计

### 3.1 核心数据表

#### 用户表 (user)
```sql
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `avatar` varchar(500) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'ACTIVE',
  `credit_score` int(11) DEFAULT '100',
  `violation_count` int(11) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 设施表 (facility)
```sql
CREATE TABLE `facility` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `model` varchar(100) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'AVAILABLE',
  `description` text,
  `purchase_date` date DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 预约表 (reservation)
```sql
CREATE TABLE `reservation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `facility_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `purpose` text,
  `status` varchar(20) DEFAULT 'PENDING',
  `admin_remark` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `checkin_time` datetime DEFAULT NULL,
  `checkout_time` datetime DEFAULT NULL,
  `checkin_status` varchar(20) DEFAULT 'NOT_CHECKED',
  `verification_code` varchar(64) DEFAULT NULL,
  `verified_by` bigint(20) DEFAULT NULL,
  `verified_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_facility_id` (`facility_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 黑名单表 (blacklist)
```sql
CREATE TABLE `blacklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `reason` varchar(255) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT 'ACTIVE',
  `operator_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_blacklist_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.2 业务规则配置表

#### 规则配置表 (rule_config)
```sql
CREATE TABLE `rule_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_key` varchar(100) NOT NULL,
  `rule_value` varchar(500) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `rule_type` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_key` (`rule_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 4. API接口设计

### 4.1 用户管理接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | /api/user/login | 用户登录 | 用户名、密码 |
| POST | /api/user/register | 用户注册 | 用户信息 |
| GET | /api/user/list | 获取用户列表 | 分页参数 |
| GET | /api/user/{id} | 获取用户详情 | 用户ID |
| PUT | /api/user/{id} | 更新用户信息 | 用户ID、用户信息 |
| POST | /api/user/{id}/change-password | 修改密码 | 用户ID、新旧密码 |
| DELETE | /api/user/{id} | 删除用户 | 用户ID |

### 4.2 设施管理接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | /api/facility/list | 获取设施列表 | 分页参数、筛选条件 |
| GET | /api/facility/available | 获取可用设施 | 时间段参数 |
| GET | /api/facility/{id} | 获取设施详情 | 设施ID |
| POST | /api/facility | 创建设施 | 设施信息 |
| PUT | /api/facility/{id} | 更新设施信息 | 设施ID、设施信息 |
| PUT | /api/facility/{id}/status | 更新设施状态 | 设施ID、状态 |
| DELETE | /api/facility/{id} | 删除设施 | 设施ID |
| GET | /api/facility/search | 搜索设施 | 关键词、分类 |

### 4.3 预约管理接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | /api/reservation/list | 获取预约列表 | 分页参数、状态筛选 |
| GET | /api/reservation/user/{userId} | 获取用户预约 | 用户ID |
| GET | /api/reservation/facility/{facilityId} | 获取设施预约 | 设施ID |
| GET | /api/reservation/pending | 获取待审核预约 | 无 |
| GET | /api/reservation/{id} | 获取预约详情 | 预约ID |
| POST | /api/reservation | 创建预约 | 预约信息 |
| PUT | /api/reservation/{id} | 更新预约 | 预约ID、预约信息 |
| PUT | /api/reservation/{id}/approve | 审核通过 | 预约ID、审核备注 |
| PUT | /api/reservation/{id}/reject | 拒绝预约 | 预约ID、拒绝原因 |
| PUT | /api/reservation/{id}/cancel | 取消预约 | 预约ID |
| PUT | /api/reservation/{id}/checkin | 签到 | 预约ID、验证码 |
| PUT | /api/reservation/{id}/checkout | 签退 | 预约ID |

### 4.4 信用管理接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | /api/credit/user/{userId} | 获取用户信用信息 | 用户ID |
| PUT | /api/credit/user/{userId}/deduct | 扣除信用分 | 用户ID、扣分原因 |
| PUT | /api/credit/user/{userId}/restore | 恢复信用分 | 用户ID、恢复分数 |
| GET | /api/credit/blacklist | 获取黑名单列表 | 分页参数 |
| POST | /api/credit/blacklist | 加入黑名单 | 用户ID、拉黑信息 |
| PUT | /api/credit/blacklist/{id}/remove | 移除黑名单 | 黑名单记录ID |

### 4.5 通知公告接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | /api/notice/list | 获取通知列表 | 分页参数、状态筛选 |
| GET | /api/notice/published | 获取已发布通知 | 无 |
| GET | /api/notice/{id} | 获取通知详情 | 通知ID |
| POST | /api/notice | 创建通知 | 通知信息 |
| PUT | /api/notice/{id} | 更新通知 | 通知ID、通知信息 |
| PUT | /api/notice/{id}/publish | 发布通知 | 通知ID |
| PUT | /api/notice/{id}/unpublish | 下线通知 | 通知ID |
| DELETE | /api/notice/{id} | 删除通知 | 通知ID |

### 4.6 统计报表接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | /api/statistics/usage | 使用统计 | 时间范围、设施类型 |
| GET | /api/statistics/violations | 违规统计 | 时间范围、违规类型 |
| GET | /api/statistics/popularity | 热门设施统计 | 时间范围、top数量 |
| GET | /api/statistics/credit-distribution | 信用分布统计 | 无 |
| GET | /api/statistics/user-behavior | 用户行为分析 | 用户ID、时间范围 |
| GET | /api/statistics/facility-utilization | 设施利用率 | 设施ID、时间范围 |
| GET | /api/statistics/export | 导出报表 | 报表类型、时间范围 |

### 4.7 文件上传接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | /api/file/upload | 文件上传 | 文件、文件类型 |
| GET | /api/file/{filename} | 获取文件 | 文件名 |
| DELETE | /api/file/{filename} | 删除文件 | 文件名 |

### 4.8 系统配置接口
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | /api/config/list | 获取配置列表 | 配置类型 |
| GET | /api/config/{key} | 获取配置项 | 配置键 |
| PUT | /api/config/{key} | 更新配置 | 配置键、配置值 |
| POST | /api/config | 创建配置 | 配置信息 |
| DELETE | /api/config/{key} | 删除配置 | 配置键 |

### 4.9 统一响应格式
所有API接口均采用统一的响应格式：
```json
{
  "code": 200,           // 响应状态码
  "message": "success",  // 响应消息
  "data": {},           // 响应数据
  "timestamp": "2024-01-01T12:00:00" // 响应时间
}
```

### 4.10 错误码定义
- `200` - 操作成功
- `400` - 请求参数错误
- `401` - 未授权访问
- `403` - 权限不足
- `404` - 资源不存在
- `409` - 业务冲突（如时间冲突）
- `422` - 业务验证失败
- `500` - 服务器内部错误
- `503` - 服务不可用








## 5. 业务规则与算法

### 5.1 预约冲突检测算法
```java
// 检查时间冲突
private boolean hasTimeConflict(Long facilityId, LocalDateTime startTime, 
                                LocalDateTime endTime, Long excludeReservationId) {
    List<Reservation> conflicts = reservationRepository.findConflicts(
        facilityId, startTime, endTime, 
        Arrays.asList("APPROVED", "COMPLETED"), excludeReservationId);
    return !conflicts.isEmpty();
}
```

### 5.2 信用分计算算法
```java
// 信用分评估
public void updateCreditScore(Long userId, String violationType) {
    User user = userRepository.findById(userId).orElse(null);
    if (user != null) {
        int deduction = getDeductionPoints(violationType);
        int newScore = Math.max(0, user.getCreditScore() - deduction);
        user.setCreditScore(newScore);
        user.setViolationCount(user.getViolationCount() + 1);
        userRepository.save(user);
    }
}
```

### 5.3 验证码生成算法
```java
// 生成唯一验证码
private String generateVerificationCode() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    String random = String.valueOf(Math.random() * 10000);
    String combined = timestamp + random;
    
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(combined.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString().substring(0, 8).toUpperCase();
    } catch (NoSuchAlgorithmException e) {
        return String.valueOf((int)(Math.random() * 100000000));
    }
}
```

## 6. 系统特色功能

### 6.1 智能预约推荐
- 基于用户历史行为推荐合适时间段
- 考虑设施使用频率和用户偏好
- 避开高峰时段，提高预约成功率

### 6.2 信用评价体系
- 多维度信用评估
- 动态信用调整机制
- 信用分与预约权限挂钩

### 6.3 违规自动检测
- 系统自动识别爽约行为
- 超时占用自动记录
- 违规行为自动扣分

### 6.4 灵活规则配置
- 支持动态调整预约规则
- 不同设施类型可设置不同规则
- 支持节假日和特殊时段配置

### 6.5 数据统计分析
- 多维度数据统计
- 可视化图表展示
- 支持导出报表

## 7. 部署与配置

### 7.1 环境要求
- Java 21或更高版本
- MySQL 8.0或更高版本
- Maven 3.6或更高版本
- 8GB以上内存

### 7.2 配置文件
application.properties关键配置：
```properties
# 服务器端口
server.port=5681

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/campus_facility_booking
spring.datasource.username=root
spring.datasource.password=123456

# 文件上传配置
file.upload-dir=files
file.base-url=http://localhost:5681/files
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
```

### 7.3 部署步骤
1. 创建MySQL数据库
2. 导入数据库脚本
3. 修改配置文件中的数据库连接信息
4. 编译打包项目
5. 启动应用

## 8. 安全设计

### 8.1 数据安全
- 敏感信息加密存储
- SQL注入防护
- XSS攻击防护
- 数据备份机制

### 8.2 访问控制
- 基于角色的权限控制
- API接口权限验证
- 操作日志记录

### 8.3 业务安全
- 预约频次限制
- 异常行为检测
- 恶意预约防护

## 9. 性能优化

### 9.1 数据库优化
- 合理设计索引
- 查询优化
- 连接池配置

### 9.2 缓存策略
- 热点数据缓存
- 查询结果缓存
- 配置信息缓存

### 9.3 并发处理
- 乐观锁机制
- 分布式锁支持
- 队列处理机制

## 10. 扩展功能

### 10.1 第三方集成
- 天气预报集成
- IP定位服务
- 消息推送服务

### 10.2 移动端支持
- RESTful API设计
- 响应式数据格式
- 移动端适配

### 10.3 数据分析
- 用户行为分析
- 设施使用分析
- 预测性维护

## 11. 项目结构说明

### 11.1 后端项目结构
```
backend/
├── pom.xml                                    # Maven项目配置文件
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── campus/
│   │   │           └── facilities/
│   │   │               ├── CampusFacilitiesApplication.java  # Spring Boot启动类
│   │   │               ├── common/          # 公共工具类
│   │   │               │   ├── Result.java  # 统一响应结果封装
│   │   │               │   ├── PageResult.java  # 分页结果封装
│   │   │               │   └── Constants.java   # 系统常量定义
│   │   │               ├── config/          # 配置类
│   │   │               │   ├── CorsConfig.java    # 跨域配置
│   │   │               │   ├── SecurityConfig.java  # 安全配置
│   │   │               │   └── WebConfig.java     # Web配置
│   │   │               ├── controller/      # 控制器层
│   │   │               │   ├── UserController.java      # 用户管理接口
│   │   │               │   ├── FacilityController.java  # 设施管理接口
│   │   │               │   ├── ReservationController.java # 预约管理接口
│   │   │               │   ├── CreditController.java    # 信用管理接口
│   │   │               │   ├── NoticeController.java    # 通知公告接口
│   │   │               │   ├── StatisticsController.java # 统计报表接口
│   │   │               │   └── FileController.java      # 文件上传接口
│   │   │               ├── entity/          # 实体类
│   │   │               │   ├── User.java        # 用户实体
│   │   │               │   ├── Facility.java    # 设施实体
│   │   │               │   ├── Reservation.java # 预约实体
│   │   │               │   ├── Blacklist.java   # 黑名单实体
│   │   │               │   ├── Notice.java      # 通知公告实体
│   │   │               │   └── RuleConfig.java  # 规则配置实体
│   │   │               ├── repository/      # 数据访问层
│   │   │               │   ├── UserRepository.java      # 用户数据访问
│   │   │               │   ├── FacilityRepository.java  # 设施数据访问
│   │   │               │   ├── ReservationRepository.java # 预约数据访问
│   │   │               │   ├── BlacklistRepository.java # 黑名单数据访问
│   │   │               │   └── NoticeRepository.java    # 通知公告数据访问
│   │   │               ├── service/         # 业务逻辑层
│   │   │               │   ├── UserService.java       # 用户业务逻辑
│   │   │               │   ├── FacilityService.java   # 设施业务逻辑
│   │   │               │   ├── ReservationService.java # 预约业务逻辑
│   │   │               │   ├── CreditService.java     # 信用业务逻辑
│   │   │               │   ├── NoticeService.java     # 通知公告业务逻辑
│   │   │               │   └── StatisticsService.java # 统计报表业务逻辑
│   │   │               ├── dto/             # 数据传输对象
│   │   │               │   ├── UserDTO.java       # 用户数据传输对象
│   │   │               │   ├── FacilityDTO.java   # 设施数据传输对象
│   │   │               │   ├── ReservationDTO.java # 预约数据传输对象
│   │   │               │   └── StatisticsDTO.java # 统计数据传输对象
│   │   │               ├── exception/       # 异常处理
│   │   │               │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │               │   ├── BusinessException.java     # 业务异常
│   │   │               │   └── ErrorCode.java            # 错误码定义
│   │   │               ├── util/            # 工具类
│   │   │               │   ├── JwtUtil.java       # JWT工具类
│   │   │               │   ├── PasswordUtil.java  # 密码工具类
│   │   │               │   ├── ValidationUtil.java # 验证工具类
│   │   │               │   └── DateUtil.java     # 日期工具类
│   │   │               └── annotation/      # 自定义注解
│   │   │                   ├── RequireLogin.java    # 登录验证注解
│   │   │                   └── RequirePermission.java # 权限验证注解
│   │   └── resources/
│   │       ├── application.properties     # 应用配置文件
│   │       ├── application-dev.properties  # 开发环境配置
│   │       ├── application-prod.properties # 生产环境配置
│   │       ├── logback-spring.xml        # 日志配置
│   │       └── static/                    # 静态资源目录
│   │           ├── images/                # 图片资源
│   │           ├── files/                 # 上传文件
│   │           └── templates/             # 模板文件
│   └── test/                                # 测试代码
│       ├── java/                            # 单元测试
│       └── resources/                       # 测试配置
└── target/                                  # Maven编译输出目录
```

### 11.2 前端项目结构
```
frontend/
├── package.json                              # npm项目配置文件
├── vite.config.js                           # Vite构建配置文件
├── index.html                               # 应用入口HTML文件
├── .env                                     # 环境变量配置
├── .env.development                         # 开发环境变量
├── .env.production                          # 生产环境变量
├── public/                                  # 公共静态资源目录
│   ├── favicon.ico                          # 网站图标
│   ├── logo.png                             # Logo图片
│   └── assets/                              # 公共资源
├── src/
│   ├── main.js                              # Vue应用入口文件
│   ├── App.vue                              # 根组件
│   ├── api/                                 # API接口封装
│   │   ├── user.js                          # 用户相关API
│   │   ├── facility.js                      # 设施相关API
│   │   ├── reservation.js                   # 预约相关API
│   │   ├── credit.js                        # 信用相关API
│   │   ├── notice.js                        # 通知相关API
│   │   ├── statistics.js                    # 统计相关API
│   │   └── file.js                          # 文件上传API
│   ├── assets/                              # 静态资源文件
│   │   ├── images/                          # 图片资源
│   │   ├── icons/                           # 图标资源
│   │   ├── styles/                          # 样式文件
│   │   │   ├── global.css                   # 全局样式
│   │   │   ├── variables.css                # CSS变量
│   │   │   └── element-ui.css               # Element Plus样式覆盖
│   │   └── fonts/                           # 字体文件
│   ├── components/                          # 公共组件
│   │   ├── Header.vue                       # 顶部导航组件
│   │   ├── Sidebar.vue                      # 侧边栏组件
│   │   ├── Footer.vue                       # 底部组件
│   │   ├── Breadcrumb.vue                   # 面包屑导航
│   │   ├── SearchBox.vue                    # 搜索框组件
│   │   ├── Pagination.vue                   # 分页组件
│   │   ├── UploadImage.vue                  # 图片上传组件
│   │   ├── DateRangePicker.vue              # 日期范围选择器
│   │   ├── StatusBadge.vue                  # 状态标签组件
│   │   ├── LoadingSpinner.vue               # 加载动画组件
│   │   └── ModalDialog.vue                  # 模态对话框组件
│   ├── layouts/                             # 布局组件
│   │   ├── AdminLayout.vue                  # 管理员布局
│   │   ├── UserLayout.vue                   # 用户布局
│   │   └── AuthLayout.vue                   # 认证布局（登录注册）
│   ├── router/                              # 路由配置
│   │   ├── index.js                         # 路由主配置
│   │   ├── guards.js                        # 路由守卫
│   │   ├── admin.js                         # 管理员路由
│   │   ├── user.js                          # 用户路由
│   │   └── common.js                        # 公共路由
│   ├── store/                               # 状态管理
│   │   ├── index.js                         # Vuex主配置
│   │   ├── modules/                         # 状态模块
│   │   │   ├── user.js                      # 用户状态
│   │   │   ├── facility.js                  # 设施状态
│   │   │   ├── reservation.js               # 预约状态
│   │   │   ├── notice.js                    # 通知状态
│   │   │   └── app.js                       # 应用状态
│   │   └── getters.js                       # 状态获取器
│   ├── utils/                               # 工具函数
│   │   ├── request.js                       # Axios请求封装
│   │   ├── auth.js                          # 认证工具
│   │   ├── validate.js                      # 验证工具
│   │   ├── date.js                          # 日期工具
│   │   ├── format.js                        # 格式化工具
│   │   ├── constants.js                     # 常量定义
│   │   └── storage.js                       # 本地存储工具
│   ├── directives/                          # 自定义指令
│   │   ├── permission.js                    # 权限指令
│   │   ├── copy.js                          # 复制指令
│   │   └── loading.js                       # 加载指令
│   ├── filters/                             # 全局过滤器
│   │   ├── date.js                          # 日期过滤器
│   │   ├── status.js                        # 状态过滤器
│   │   └── currency.js                      # 货币过滤器
│   └── views/                               # 页面组件
│       ├── Login.vue                        # 登录页面
│       ├── Register.vue                     # 注册页面
│       ├── ForgotPassword.vue               # 忘记密码页面
│       ├── admin/                           # 管理员页面
│       │   ├── Dashboard.vue                # 管理员仪表盘
│       │   ├── UserManagement.vue            # 用户管理
│       │   ├── FacilityManagement.vue        # 设施管理
│       │   ├── ReservationManagement.vue     # 预约管理
│       │   ├── CreditManagement.vue          # 信用管理
│       │   ├── BlacklistManagement.vue       # 黑名单管理
│       │   ├── NoticeManagement.vue         # 通知公告管理
│       │   ├── Statistics.vue               # 统计分析
│       │   ├── SystemConfig.vue             # 系统配置
│       │   └── Profile.vue                  # 个人中心
│       ├── user/                            # 用户页面
│       │   ├── Welcome.vue                  # 欢迎页面
│       │   ├── FacilityList.vue            # 设施列表
│       │   ├── FacilityDetail.vue          # 设施详情
│       │   ├── MyReservations.vue           # 我的预约
│       │   ├── NewReservation.vue           # 新建预约
│       │   ├── MyCredit.vue                 # 我的信用
│       │   ├── NoticeList.vue               # 通知公告
│       │   └── Profile.vue                  # 个人中心
│       └── error/                           # 错误页面
│           ├── 404.vue                      # 404页面
│           ├── 403.vue                      # 403页面
│           └── 500.vue                      # 500页面
├── dist/                                    # 构建输出目录
├── node_modules/                            # 依赖包目录
├── tests/                                   # 测试文件
│   ├── unit/                               # 单元测试
│   └── e2e/                                # 端到端测试
└── docs/                                    # 项目文档
```

### 11.3 模块功能说明

#### 后端核心模块
- **Controller层**：处理HTTP请求，参数验证，返回响应结果
- **Service层**：实现业务逻辑，处理复杂的业务流程
- **Repository层**：数据访问层，负责数据库操作
- **Entity层**：实体类，映射数据库表结构
- **DTO层**：数据传输对象，用于前后端数据交互
- **Exception层**：统一异常处理，错误码管理

#### 前端核心模块
- **API层**：封装后端接口调用，统一错误处理
- **Views层**：页面组件，负责页面展示和用户交互
- **Components层**：公共组件，提高代码复用性
- **Store层**：状态管理，统一管理应用状态
- **Router层**：路由配置，页面跳转控制
- **Utils层**：工具函数，提供常用功能封装

### 11.4 技术架构特点
- **前后端分离**：独立开发、独立部署，提高开发效率
- **模块化设计**：功能模块化，便于维护和扩展
- **组件化开发**：前端组件化，提高代码复用性
- **统一异常处理**：全局异常捕获，友好的错误提示
- **统一响应格式**：标准化的API响应格式
- **权限控制**：基于角色的访问控制，确保系统安全






## 12. 维护与监控

### 12.1 系统监控
- 应用性能监控
- 数据库性能监控
- 服务器资源监控

### 12.2 日志管理
- 操作日志记录
- 异常日志收集
- 审计日志保存

### 12.3 备份策略
- 数据库定期备份
- 文件数据备份
- 配置信息备份

## 13. 总结

校园公共设施预约管理系统是一个功能完善、设计合理的现代化管理平台。 系统采用主流的Java技术栈，具有良好的可扩展性和维护性。
通过智能化的预约管理、完善的信用评价体系和灵活的规则配置，能够有效提升校园设施的管理效率和使用体验。

系统的核心优势包括：
- 完整的业务流程覆盖
- 灵活的规则配置机制
- 智能的冲突检测算法
- 完善的信用管理体系
- 丰富的数据统计功能

该系统为高校设施管理提供了现代化的解决方案，能够有效解决传统管理方式中的效率低下、资源浪费等问题，是智慧校园建设的重要组成部分。