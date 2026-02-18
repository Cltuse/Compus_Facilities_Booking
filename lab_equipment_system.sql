/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80200 (8.2.0)
 Source Host           : localhost:3306
 Source Schema         : lab_equipment_system

 Target Server Type    : MySQL
 Target Server Version : 80200 (8.2.0)
 File Encoding         : 65001

 Date: 03/12/2025 09:57:26
*/

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `lab_equipment_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 使用数据库
USE `lab_equipment_system`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备名称',
  `model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备型号',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类别',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存放位置',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可用/IN_USE-使用中/MAINTENANCE-维护中/DAMAGED-损坏',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '设备描述',
  `purchase_date` date NULL DEFAULT NULL COMMENT '购买日期',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备图片',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '高速离心机', 'TGL-16M', '电子设备', '实验室A-101', 'IN_USE', '台式高速离心机，最大转速16000rpm', '2023-01-15', 25000.00, NULL, '2025-11-23 17:16:27', '2025-12-02 22:27:49');
INSERT INTO `equipment` VALUES (2, '显微镜', 'BX53', '实验仪器', '实验室A-102', 'AVAILABLE', '奥林巴斯生物显微镜', '2023-02-20', 45000.00, NULL, '2025-11-23 17:16:27', '2025-12-01 23:07:12');
INSERT INTO `equipment` VALUES (3, 'PCR仪', 'T100', '化学设备', '实验室B-201', 'IN_USE', '热循环仪，用于PCR扩增', '2023-03-10', 38000.00, NULL, '2025-11-23 17:16:27', '2025-12-02 09:13:45');
INSERT INTO `equipment` VALUES (4, '分光光度计', 'UV-2600', '网络设备', '实验室B-202', 'AVAILABLE', '紫外可见分光光度计', '2023-04-05', 52000.00, NULL, '2025-11-23 17:16:27', '2025-12-02 09:13:50');
INSERT INTO `equipment` VALUES (5, '超声波清洗机', 'KQ-500DE', '生物设备', '实验室C-301', 'MAINTENANCE', '数控超声波清洗器', '2022-12-01', 8000.00, NULL, '2025-11-23 17:16:27', '2025-12-02 09:13:38');
INSERT INTO `equipment` VALUES (6, '高性能离心机', 'Hettich 32R', '机械工具', 'A区101室', 'AVAILABLE', '德国原装进口高性能离心机，最高转速可达32000rpm，适用于生物样品分离和纯化工作', '2023-03-15', 45000.00, 'https://via.placeholder.com/300x200/409EFF/ffffff?text=离心机', '2025-12-02 09:13:58', '2025-12-02 09:14:38');
INSERT INTO `equipment` VALUES (7, 'PCR扩增仪', 'Bio-Rad C1000', '生物设备', 'B区205室', 'AVAILABLE', '美国Bio-Rad公司生产，支持多通道PCR反应，温度控制精确，适用于DNA扩增实验', '2023-05-20', 28000.00, 'https://via.placeholder.com/300x200/67C23A/ffffff?text=PCR仪', '2025-12-02 09:13:58', '2025-12-03 09:37:27');
INSERT INTO `equipment` VALUES (8, '紫外分光光度计', 'Shimadzu UV-2600', '光学设备', 'C区302室', 'AVAILABLE', '日本岛津生产，波长范围185-900nm，双光束设计，适用于化学成分分析和浓度测定', '2022-11-08', 78000.00, 'https://via.placeholder.com/300x200/E6A23C/ffffff?text=分光光度计', '2025-12-02 09:13:58', '2025-12-02 09:14:54');
INSERT INTO `equipment` VALUES (9, '恒温培养箱', 'Thermo Scientific 371', '物理设备', 'A区103室', 'MAINTENANCE', '美国Thermo Fisher公司生产，温度控制范围4-70℃，CO2浓度控制，适用于细胞培养和微生物培养', '2023-01-12', 15000.00, 'https://via.placeholder.com/300x200/F56C6C/ffffff?text=培养箱', '2025-12-02 09:13:58', '2025-12-02 09:15:00');
INSERT INTO `equipment` VALUES (10, '电子天平', 'Mettler-Toledo XPR205', '化学设备', 'B区201室', 'AVAILABLE', '瑞士梅特勒-托利多生产，精度可达0.01mg，最大称量220g，适用于精密称量实验', '2023-07-25', 12000.00, 'https://via.placeholder.com/300x200/909399/ffffff?text=电子天平', '2025-12-02 09:13:58', '2025-12-02 09:14:28');
INSERT INTO `equipment` VALUES (11, '液相色谱仪', 'Agilent 1260 Infinity', '化学设备', 'D区401室', 'AVAILABLE', '美国安捷伦公司生产，四元梯度泵，二极管阵列检测器，适用于复杂样品的分离和定量分析', '2022-09-30', 95001.00, 'https://via.placeholder.com/300x200/409EFF/ffffff?text=液相色谱仪', '2025-12-02 09:13:58', '2025-12-02 23:20:54');

-- ----------------------------
-- Table structure for equipment_category
-- ----------------------------
DROP TABLE IF EXISTS `equipment_category`;
CREATE TABLE `equipment_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类别名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '类别描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-禁用',
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `category_name`(`category_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备类别表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment_category
-- ----------------------------
INSERT INTO `equipment_category` VALUES (9, '计算机设备', '台式电脑、笔记本电脑、平板电脑等计算设备', 1, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (10, '实验仪器', '各类实验测量仪器设备和精密仪器', 2, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (11, '电子设备', '示波器、信号发生器、万用表等电子测量设备', 3, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (12, '机械工具', '各类机械操作工具、设备和机床', 4, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (13, '光学设备', '显微镜、望远镜、相机等光学仪器', 5, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (14, '测量设备', '长度、重量、温度、压力等测量设备', 6, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (15, '网络设备', '路由器、交换机、服务器等网络通信设备', 7, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (16, '化学设备', '实验用化学试剂容器、反应釜等设备', 8, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (17, '物理设备', '力学、电磁学、热学等物理实验设备', 9, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (18, '生物设备', '显微镜、培养箱、离心机等生物实验设备', 10, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');
INSERT INTO `equipment_category` VALUES (19, '其他设备', '其他类型的设备和器材', 11, 'ACTIVE', '2025-12-01 22:48:50', '2025-12-01 22:48:50');

-- ----------------------------
-- Table structure for maintenance
-- ----------------------------
DROP TABLE IF EXISTS `maintenance`;
CREATE TABLE `maintenance`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `equipment_id` bigint NOT NULL COMMENT '设备ID',
  `maintenance_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维护类型：ROUTINE-常规维护/REPAIR-故障维修/UPGRADE-设备升级',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '维护描述',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '维护费用',
  `maintainer` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维护人员',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'IN_PROGRESS' COMMENT '状态：IN_PROGRESS-进行中/COMPLETED-已完成',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '维护结果',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `equipment_id`(`equipment_id` ASC) USING BTREE,
  CONSTRAINT `maintenance_ibfk_1` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '维护记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of maintenance
-- ----------------------------
INSERT INTO `maintenance` VALUES (1, 5, 'REPAIR', '超声波发生器故障维修', 1500.00, '王工', '2025-11-20 09:00:00', '2025-11-22 17:00:00', 'COMPLETED', '更换超声波发生器，设备已恢复正常', '2025-11-23 17:16:27', '2025-11-23 17:16:27');
INSERT INTO `maintenance` VALUES (2, 3, 'ROUTINE', '定期保养维护', 500.00, '张工', '2025-11-23 09:00:00', NULL, 'IN_PROGRESS', NULL, '2025-11-23 17:16:27', '2025-11-23 17:16:27');
INSERT INTO `maintenance` VALUES (4, 1, 'ROUTINE', '定期检查离心机转子平衡性，清洁轴承，添加润滑油', 350.00, '技术员-王工', '2024-01-15 09:00:00', '2024-01-15 11:30:00', 'COMPLETED', '设备运行正常，转子平衡性良好', '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (5, 2, 'REPAIR', '显微镜光源系统故障，更换LED灯泡，校准光路系统', 1200.00, '维修工程师-李明', '2024-02-10 14:00:00', '2024-02-11 16:00:00', 'COMPLETED', '光源系统修复成功，图像清晰度恢复正常', '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (6, 3, 'UPGRADE', '升级PCR仪温度控制程序，优化升温曲线', 2500.00, '软件工程师-张华', '2024-03-05 09:00:00', '2024-03-07 17:00:00', 'COMPLETED', '软件升级完成，温度精度提升', '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (7, 4, 'ROUTINE', '校准波长准确性，清洁检测器，检查光源强度', 680.00, '技术员-陈工', '2024-04-12 10:00:00', '2024-04-12 14:00:00', 'COMPLETED', '波长校准完成，检测器灵敏度正常', '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (8, 5, 'REPAIR', '超声波发生器故障，更换换能器，修复控制电路', 1800.00, '维修工程师-赵强', '2024-05-08 13:30:00', '2024-05-09 15:30:00', 'COMPLETED', '换能器更换完成，超声波功率正常', '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (9, 1, 'ROUTINE', '检查安全门锁系统，测试紧急制动功能，更换减震垫', 450.00, '技术员-王工', '2024-06-15 09:30:00', '2024-06-15 12:00:00', 'COMPLETED', '安全系统检查通过，减震垫更换完成', '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (10, 2, 'ROUTINE', '清洁镜头系统，检查调焦机构，校准机械台', 320.00, '技术员-刘工', '2024-07-10 14:30:00', '2024-07-10 16:30:00', 'COMPLETED', '镜头清洁完毕，调焦顺畅', '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (11, 3, 'REPAIR', '温度传感器异常，更换温度探头，重新校准温度控制系统', 950.00, '维修工程师-李明', '2024-11-20 10:00:00', NULL, 'IN_PROGRESS', NULL, '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (12, 4, 'UPGRADE', '安装自动进样器模块，集成软件控制系统', 4500.00, '系统工程师-张华', '2024-11-25 09:00:00', NULL, 'IN_PROGRESS', NULL, '2025-12-02 14:56:26', '2025-12-02 14:56:26');
INSERT INTO `maintenance` VALUES (13, 5, 'ROUTINE', '清洁清洗槽，检查超声波功率输出，维护排水系统', 280.00, '技术员-陈工', '2024-12-01 13:00:00', NULL, 'IN_PROGRESS', NULL, '2025-12-02 14:56:26', '2025-12-02 14:56:26');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `publisher_id` bigint NOT NULL COMMENT '发布人ID',
  `publisher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布人姓名',
  `publish_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT-草稿/PUBLISHED-已发布',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id` ASC) USING BTREE,
  CONSTRAINT `notice_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '实验室设备管理系统上线通知', '尊敬的各位老师和同学：\n\n实验室设备预约与维护管理系统已正式上线，请大家使用新系统进行设备预约。系统功能包括：设备浏览、在线预约、维护申报等。\n\n感谢大家的支持！', 1, '系统管理员', '2025-11-23 17:16:27', 'PUBLISHED', '2025-11-23 17:16:27', '2025-11-23 17:16:27');
INSERT INTO `notice` VALUES (2, '本周设备维护计划', '本周将对以下设备进行定期维护：\n1. PCR仪（周三）\n2. 显微镜（周五）\n\n维护期间设备将暂停使用，请大家合理安排实验时间。', 1, '系统管理员', '2025-11-23 17:16:27', 'DRAFT', '2025-11-23 17:16:27', '2025-12-02 09:42:43');
INSERT INTO `notice` VALUES (4, '实验室安全管理制度更新通知', '各位师生：\n\n为进一步加强实验室安全管理，保障实验教学和科研工作的顺利进行，实验室管理委员会对现有安全管理制度进行了全面修订。新制度将于2025年1月1日正式实施。\n\n主要更新内容包括：\n1. 实验室准入管理流程优化\n2. 危险化学品管理规范细化\n3. 实验废弃物处理标准更新\n4. 应急预案处置流程完善\n\n请各位师生认真学习新制度，严格遵守实验室安全管理规定。如有疑问，请及时联系实验室管理员。\n\n实验室管理委员会\n2024年12月2日', 1, '系统管理员', '2024-12-02 09:45:00', 'PUBLISHED', '2025-12-02 09:45:04', '2025-12-02 09:48:54');
INSERT INTO `notice` VALUES (5, '新增高端设备采购公告', '尊敬的各位师生：\n\n为提升实验室科研水平和教学能力，学校近期采购了一批高端实验设备，现已完成安装调试并正式投入使用。\n\n新增设备清单：\n• 高分辨率液质联用仪（Waters Xevo G2-XS）- 用于复杂样品分析\n• 激光共聚焦显微镜（Zeiss LSM 880）- 用于细胞成像研究\n• 超高速离心机（Beckman Coulter Optima XE）- 用于生物大分子分离\n• 傅里叶变换红外光谱仪（Bruker Vertex 70）- 用于分子结构分析\n\n以上设备均已开放预约使用，请通过实验室管理系统提交预约申请。设备详细操作手册和培训安排请关注后续通知。\n\n设备管理部\n2024年12月1日', 1, '系统管理员', '2024-12-01 14:30:00', 'PUBLISHED', '2025-12-02 09:45:04', '2025-12-02 09:48:54');
INSERT INTO `notice` VALUES (6, '元旦假期实验室开放安排通知', '各位师生：\n\n根据学校校历安排，2025年元旦假期期间实验室开放安排如下：\n\n【放假时间】\n2024年12月31日（周二）至2025年1月2日（周四），共3天\n2025年1月3日（周五）正常上班\n\n【实验室开放安排】\n1. 常规实验室：12月31日、1月2日开放，1月1日关闭\n2. 精密仪器室：假期全天关闭，1月3日恢复开放\n3. 24小时实验室：假期正常开放\n4. 特殊设备室：根据预约情况酌情开放\n\n【注意事项】\n• 假期使用实验室需提前网上预约\n• 严格遵守实验室安全管理规定\n• 离开前务必关闭水源、电源、气源\n• 如遇紧急情况，请及时联系值班人员：138-XXXX-XXXX\n\n祝各位师生元旦快乐！\n\n实验室管理中心\n2024年12月2日', 1, '系统管理员', '2024-12-02 10:15:00', 'PUBLISHED', '2025-12-02 09:45:04', '2025-12-02 09:48:54');

-- ----------------------------
-- Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `equipment_id` bigint NOT NULL COMMENT '设备ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `purpose` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '使用目的',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审批/APPROVED-已通过/REJECTED-已拒绝/COMPLETED-已完成/CANCELLED-已取消',
  `admin_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '管理员备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `equipment_id`(`equipment_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservation
-- ----------------------------
INSERT INTO `reservation` VALUES (8, 11, 2, '2025-12-15 00:00:00', '2025-12-24 00:00:00', '3', 'COMPLETED', '', '2025-12-02 21:56:53', '2025-12-02 22:13:10');
INSERT INTO `reservation` VALUES (9, 1, 2, '2025-12-21 00:00:00', '2026-01-08 00:00:00', '32', 'REJECTED', '', '2025-12-02 21:59:22', '2025-12-02 22:26:00');
INSERT INTO `reservation` VALUES (10, 1, 2, '2025-12-15 00:00:00', '2025-12-24 00:00:00', '1', 'APPROVED', '', '2025-12-02 22:26:23', '2025-12-02 22:27:49');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：USER-普通用户/ADMIN-管理员',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用/INACTIVE-禁用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', '系统管理员', 'ADMIN', '13800138000', 'admin@lab.com', 'ACTIVE', '2025-11-23 17:16:27', '2025-12-02 23:28:18');
INSERT INTO `user` VALUES (2, 'user1', '123456', '张三', 'USER', '13800138001', 'zhangsan@lab.com', 'ACTIVE', '2025-11-23 17:16:27', '2025-12-02 22:30:03');
INSERT INTO `user` VALUES (3, 'user2', '123456', '李四', 'USER', '13800138002', 'lisi@lab.com', 'ACTIVE', '2025-11-23 17:16:27', '2025-11-23 17:16:27');
INSERT INTO `user` VALUES (5, 'user3', '123456', '123', 'USER', NULL, '2@qq.com', 'ACTIVE', '2025-12-01 22:15:44', '2025-12-01 22:15:44');

SET FOREIGN_KEY_CHECKS = 1;
