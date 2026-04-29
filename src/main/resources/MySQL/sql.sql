CREATE TABLE `banner`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     varchar(50)  NOT NULL COMMENT '管理员id',
    `title`       varchar(50)  DEFAULT NULL COMMENT '轮播图标题（可选，如“医院简介”）',
    `description` varchar(500) DEFAULT NULL COMMENT '轮播图描述（可选）',
    `image_url`   varchar(500) NOT NULL COMMENT '轮播图的OSS访问地址（核心字段）',
    `sort`        int          DEFAULT '0' COMMENT '排序号（数字越小，轮播位置越靠前）',
    `status`      tinyint(1)   DEFAULT '1' COMMENT '状态：0=禁用，1=启用（控制是否在前台显示）',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    PRIMARY KEY (`id`),
    KEY `idx_status_sort` (`status`, `sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '轮播图表';

CREATE TABLE `user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `phone`       VARCHAR(50)  NOT NULL COMMENT '登录账号',
    `passwordDTO`    VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `role`        VARCHAR(20)  NOT NULL COMMENT 'admin / doctor / user',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
    `create_time` DATETIME              DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_phone` (`phone`)
) COMMENT '登录表 - 只管账号密码验证';


CREATE TABLE `sys_role`
(
    `role_code` VARCHAR(20) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色中文名',
    PRIMARY KEY (`role_code`)
) COMMENT = '角色表';

INSERT INTO `sys_role` (`role_code`, `role_name`)
VALUES ('ADMIN', '管理员'),
       ('DOCTOR', '医生'),
       ('USER', '普通用户');



-- ============================
-- RBAC 五张核心表
-- ============================

-- 1. 用户表
CREATE TABLE sys_user
(
    id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone    VARCHAR(50)  NOT NULL COMMENT '手机号',
    passwordDTO VARCHAR(100) NOT NULL COMMENT '密码',
    status   TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (phone)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '用户表';

CREATE TABLE `user`
(
    `id`                BIGINT      NOT NULL COMMENT '与user_login.id一致',
    `name`              VARCHAR(50) NOT NULL COMMENT '姓名',
    `phone`             VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `avatar`            VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `gender`            TINYINT      DEFAULT 0 COMMENT '0未知 1男 2女',
    `id_card`           VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
    `address`           VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `allergy_history`   VARCHAR(500) DEFAULT NULL COMMENT '过敏史',
    `emergency_contact` VARCHAR(50)  DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '紧急联系电话',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) COMMENT = '用户表 ';

CREATE TABLE `doctor_info` (
                               `user_id` BIGINT NOT NULL COMMENT '关联user.id',
                               `department_id` BIGINT DEFAULT NULL COMMENT '科室ID',
                               `title` VARCHAR(50) DEFAULT NULL COMMENT '职称:主任医师/副主任医师/主治医师',
                               `specialty` VARCHAR(255) DEFAULT NULL COMMENT '擅长领域',
                               `introduction` TEXT DEFAULT NULL COMMENT '个人简介',
                               `registration_num` INT DEFAULT 0 COMMENT '挂号数',
                               PRIMARY KEY (`user_id`)
) COMMENT = '医生详情';

-- 2. 角色表
CREATE TABLE sys_role
(
    id        BIGINT      NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色标识',
    role_desc VARCHAR(100) DEFAULT NULL COMMENT '角色描述',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_name (role_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '角色表';

-- 3. 权限表
CREATE TABLE sys_permission
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    perm_name VARCHAR(50)  NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(100) NOT NULL COMMENT '权限标识 如 user:delete',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '权限表';

-- 4. 用户-角色关联表
CREATE TABLE sys_user_role
(
    id      BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '用户角色关联表';

-- 5. 角色-权限关联表
CREATE TABLE sys_role_permission
(
    id      BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    perm_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_id, perm_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '角色权限关联表';

-- ============================
-- RBAC 初始化数据
-- ============================

-- 1. 插入角色数据
INSERT INTO sys_role (role_name, role_desc) VALUES
('admin', '系统管理员，拥有所有管理权限'),
('doctor', '医生，可以查看排班和患者信息'),
('user', '普通用户，可以预约挂号');

-- 2. 插入权限数据
-- 部门管理权限
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('部门查看', 'department:view'),
('部门新增', 'department:add'),
('部门修改', 'department:update'),
('部门删除', 'department:delete');

-- 医生管理权限
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('医生查看', 'doctor:view'),
('医生注册', 'doctor:register'),
('医生删除', 'doctor:delete');

-- 排班管理权限
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('排班查看', 'schedule:view'),
('排班新增', 'schedule:add'),
('排班修改', 'schedule:update'),
('排班删除', 'schedule:delete');

-- 轮播图管理权限
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('轮播图查看', 'banner:view'),
('轮播图新增', 'banner:add'),
('轮播图修改', 'banner:update'),
('轮播图删除', 'banner:delete');

-- 公告管理权限
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('公告查看', 'anno:view'),
('公告新增', 'anno:add'),
('公告修改', 'anno:update'),
('公告删除', 'anno:delete');

-- 预约管理权限（医生）
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('预约查看', 'appointment:view'),
('患者查看', 'patient:view');

-- 用户预约权限
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('创建预约', 'appointment:create'),
('取消预约', 'appointment:cancel'),
('支付订单', 'appointment:pay');

-- 3. 为ADMIN角色分配所有管理权限
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_name = 'ADMIN'
AND p.perm_code IN (
    'department:view', 'department:add', 'department:update', 'department:delete',
    'doctor:view', 'doctor:register', 'doctor:delete',
    'schedule:view', 'schedule:add', 'schedule:update', 'schedule:delete',
    'banner:view', 'banner:add', 'banner:update', 'banner:delete',
    'anno:view', 'anno:add', 'anno:update', 'anno:delete'
);

-- 4. 为DOCTOR角色分配医生相关权限
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_name = 'DOCTOR'
AND p.perm_code IN (
    'schedule:view',
    'appointment:view',
    'patient:view'
);

-- 5. 为USER角色分配用户相关权限
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_name = 'USER'
AND p.perm_code IN (
    'department:view',
    'doctor:view',
    'schedule:view',
    'appointment:create',
    'appointment:view',
    'appointment:cancel',
    'appointment:pay'
);

-- ============================
-- 咨询文章模块表结构
-- ============================

-- 1. 咨询文章表
CREATE TABLE post
(
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title           VARCHAR(200) NOT NULL COMMENT '文章标题',
    content         TEXT         NOT NULL COMMENT '文章内容',
    cover_image     VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    user_id         BIGINT       NOT NULL COMMENT '作者ID',
    view_count      INT          DEFAULT 0 COMMENT '浏览量',
    like_count      INT          DEFAULT 0 COMMENT '点赞数',
    favorite_count  INT          DEFAULT 0 COMMENT '收藏数',
    comment_count   INT          DEFAULT 0 COMMENT '评论数',
    status          TINYINT      DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已删除',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '咨询文章表';

-- 2. 评论表
CREATE TABLE comment
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    post_id     BIGINT       NOT NULL COMMENT '文章ID',
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    content     VARCHAR(1000) NOT NULL COMMENT '评论内容',
    parent_id   BIGINT       DEFAULT NULL COMMENT '父评论ID（回复评论时使用）',
    like_count  INT          DEFAULT 0 COMMENT '点赞数',
    status      TINYINT      DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_post_id (post_id),
    KEY idx_user_id (user_id),
    KEY idx_parent_id (parent_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '评论表';

-- 3. 用户点赞表
CREATE TABLE user_like
(
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    post_id     BIGINT   NOT NULL COMMENT '文章ID',
    type        TINYINT  DEFAULT 1 COMMENT '类型：1-文章点赞 2-评论点赞',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_post_type (user_id, post_id, type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '用户点赞表';

-- 4. 用户收藏表
CREATE TABLE user_favorite
(
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    post_id     BIGINT   NOT NULL COMMENT '文章ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_post (user_id, post_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '用户收藏表';

-- ============================
-- 咨询文章模块权限数据
-- ============================

-- 插入咨询文章管理权限
INSERT INTO sys_permission (perm_name, perm_code) VALUES
('文章发布', 'consult:article:add'),
('文章编辑', 'consult:article:update'),
('文章删除', 'consult:article:delete');

-- 为ADMIN角色分配咨询文章管理权限
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_name = 'ADMIN'
AND p.perm_code IN (
    'consult:article:add',
    'consult:article:update',
    'consult:article:delete'
);





















