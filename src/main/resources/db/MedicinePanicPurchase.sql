/*
 Navicat Premium Data Transfer

 Source Server         : 0 localhost(127.0.0.1)
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : 127.0.0.1:3306
 Source Schema         : medicinepanicpurchase

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 26/07/2023 10:38:36
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_medicine
-- ----------------------------
DROP TABLE IF EXISTS `tb_medicine`;
CREATE TABLE `tb_medicine`
(
    `id`          bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pharmacy_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '药房id',
    `title`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '药品标题',
    `sub_title`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题(详细介绍)',
    `pay_value`   bigint(0) UNSIGNED NOT NULL COMMENT '支付金额，单位是分。例如200代表2元',
    `type`        tinyint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '0,普通药品；1,抢购药品',
    `create_time` timestamp(0)                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time` timestamp(0)                                                  NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_medicine
-- ----------------------------
INSERT INTO `tb_medicine`
VALUES (1, 1, '连花清瘟', '按袋销售，紧缺药品！', 1100, 1, '2023-05-30 09:59:31', '2023-08-27 19:59:59');
INSERT INTO `tb_medicine`
VALUES (2, 1, '健胃消食片', '按盒销售', 2000, 0, '2023-05-30 10:00:24', '2023-07-26 16:25:24');
INSERT INTO `tb_medicine`
VALUES (3, 2, '阿司匹林', '按盒销售', 1500, 0, '2023-05-30 10:00:51', '2023-06-19 20:47:51');
INSERT INTO `tb_medicine`
VALUES (4, 2, '布洛芬', '按袋销售，紧缺药品！', 1800, 1, '2023-05-30 10:06:57', '2023-05-30 10:07:54');
INSERT INTO `tb_medicine`
VALUES (5, 1, '小柴胡颗粒', '按带销售', 800, 0, '2023-05-30 10:07:43', '2023-05-30 10:07:43');

-- ----------------------------
-- Table structure for tb_medicine_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_medicine_order`;
CREATE TABLE `tb_medicine_order`
(
    `id`           bigint(0) NOT NULL COMMENT '主键',
    `purchaser_id` bigint(0) UNSIGNED NOT NULL COMMENT '下单的购药者id',
    `medicine_id`  bigint(0) UNSIGNED NOT NULL COMMENT '购买的药品id',
    `status`       tinyint(0) UNSIGNED NOT NULL DEFAULT 1 COMMENT '订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款',
    `create_time`  timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '下单时间',
    `update_time`  timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_medicine_order
-- ----------------------------
INSERT INTO `tb_medicine_order`
VALUES (9471403615059969, 1, 1, 1, '2023-05-30 10:13:49', '2023-08-27 20:13:49');
INSERT INTO `tb_medicine_order`
VALUES (9471403615064327, 1, 2, 1, '2023-05-30 10:14:29', '2023-07-26 16:26:29');
INSERT INTO `tb_medicine_order`
VALUES (9471403615068290, 1, 4, 1, '2023-05-30 10:14:48', '2023-05-30 10:14:48');
INSERT INTO `tb_medicine_order`
VALUES (9471403615074702, 2, 4, 1, '2023-05-30 10:15:01', '2023-06-19 20:47:01');

-- ----------------------------
-- Table structure for tb_panic_purchase_medicine
-- ----------------------------
DROP TABLE IF EXISTS `tb_panic_purchase_medicine`;
CREATE TABLE `tb_panic_purchase_medicine`
(
    `medicine_id` bigint(0) UNSIGNED NOT NULL COMMENT '关联的限量药品的id',
    `stock`       int(0) NOT NULL COMMENT '库存',
    `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `begin_time`  timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '生效时间',
    `end_time`    timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '失效时间',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`medicine_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '抢购限量药品表，与限量药品是一对一关系' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_panic_purchase_medicine
-- ----------------------------
INSERT INTO `tb_panic_purchase_medicine`
VALUES (1, 500, '2023-05-30 10:15:41', '2023-05-30 10:15:41', '2023-12-30 10:15:41', '2023-07-26 16:28:54');
INSERT INTO `tb_panic_purchase_medicine`
VALUES (4, 800, '2023-05-30 10:15:47', '2023-05-30 10:15:47', '2023-12-30 10:15:47', '2023-06-19 20:56:59');

-- ----------------------------
-- Table structure for tb_pharmacy
-- ----------------------------
DROP TABLE IF EXISTS `tb_pharmacy`;
CREATE TABLE `tb_pharmacy`
(
    `id`          bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '药房名称',
    `type_id`     bigint(0) UNSIGNED NOT NULL COMMENT '药房类型的id',
    `area`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区域，例如人民医院A区',
    `x`           double UNSIGNED NOT NULL COMMENT '经度',
    `y`           double UNSIGNED NOT NULL COMMENT '纬度',
    `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP (0) COMMENT '创建时间',
    `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP (0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `foreign_key_type`(`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_pharmacy
-- ----------------------------
INSERT INTO `tb_pharmacy`
VALUES (1, 'A区中药房', 1, '人民医院A区', 120.140192, 30.312154, '2023-05-30 10:17:46', '2023-05-30 10:20:26');
INSERT INTO `tb_pharmacy`
VALUES (2, 'B区中药房', 1, '人民医院B区', 120.139863, 30.316078, '2023-05-30 10:18:03', '2023-05-30 10:19:30');
INSERT INTO `tb_pharmacy`
VALUES (3, 'A区西药房', 2, '人民医院A区', 120.140388, 30.312147, '2023-05-30 10:18:26', '2023-05-30 10:20:59');
INSERT INTO `tb_pharmacy`
VALUES (4, 'H区西药房', 2, '人民医院H区', 120.142396, 30.309486, '2023-05-30 10:18:47', '2023-05-30 10:21:21');

-- ----------------------------
-- Table structure for tb_pharmacy_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_pharmacy_type`;
CREATE TABLE `tb_pharmacy_type`
(
    `id`          bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '药房类型名称',
    `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP (0) COMMENT '创建时间',
    `update_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP (0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_pharmacy_type
-- ----------------------------
INSERT INTO `tb_pharmacy_type`
VALUES (1, '中药房', '2023-05-30 10:22:27', '2023-05-30 10:22:27');
INSERT INTO `tb_pharmacy_type`
VALUES (2, '西药房', '2023-05-30 10:22:32', '2023-05-30 10:22:32');

-- ----------------------------
-- Table structure for tb_purchaser
-- ----------------------------
DROP TABLE IF EXISTS `tb_purchaser`;
CREATE TABLE `tb_purchaser`
(
    `id`          bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `phone`       varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号码',
    `nick_name`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '昵称，默认是购药者id',
    `create_time` timestamp(0)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time` timestamp(0)                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uniqe_key_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_purchaser
-- ----------------------------
INSERT INTO `tb_purchaser`
VALUES (1, '18620345985', '购药者测试1', '2023-05-30 10:23:01', '2023-05-30 10:23:44');
INSERT INTO `tb_purchaser`
VALUES (2, '18620345211', 'ZYL测试', '2023-05-30 10:23:19', '2023-06-19 20:50:38');
INSERT INTO `tb_purchaser`
VALUES (3, '18620345216', '余导师测试', '2023-05-30 10:23:35', '2023-07-26 16:28:48');
INSERT INTO `tb_purchaser`
VALUES (4, '18888888888', '购药者测试2', '2023-05-30 10:24:05', '2023-07-26 16:29:05');
INSERT INTO `tb_purchaser`
VALUES (5, '18866666666', '张三', '2023-05-30 10:24:13', '2023-08-27 20:11:13');

-- ----------------------------
-- Table structure for tb_purchaser_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_purchaser_info`;
CREATE TABLE `tb_purchaser_info`
(
    `purchaser_id` bigint(0) UNSIGNED NOT NULL COMMENT '主键，购药者id',
    `introduce`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人介绍等信息，不超过128个字符',
    `gender`       tinyint(0) UNSIGNED NULL DEFAULT 0 COMMENT '性别，0：男，1：女',
    `create_time`  timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
    `update_time`  timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP (0) COMMENT '更新时间',
    PRIMARY KEY (`purchaser_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of tb_purchaser_info
-- ----------------------------
INSERT INTO `tb_purchaser_info`
VALUES (1, '购药者测试1购药者测试1购药者测试1购药者测试1购药者测试1购药者测试1购药者测试1', 1, '2023-05-30 10:24:22',
        '2023-05-30 10:25:08');
INSERT INTO `tb_purchaser_info`
VALUES (2, 'ZYL测试ZYL测试ZYL测试ZYL测试ZYL测试ZYL测试', 0, '2023-05-30 10:24:23', '2023-06-19 20:50:59');
INSERT INTO `tb_purchaser_info`
VALUES (3, '余导师测试余导师测试余导师测试余导师测试余导师测试', 0, '2023-05-30 10:24:24', '2023-07-26 16:28:53');
INSERT INTO `tb_purchaser_info`
VALUES (4, '购药者测试2购药者测试2购药者测试2购药者测试2购药者测试2购药者测试2购药者测试2', 0, '2023-05-30 10:24:25',
        '2023-07-26 16:29:30');
INSERT INTO `tb_purchaser_info`
VALUES (5, '张三张三张三张三张三张三张三张三张三张三张三张三张三张三张三', 1, '2023-05-30 10:25:18',
        '2023-08-27 20:25:34');

SET
FOREIGN_KEY_CHECKS = 1;
