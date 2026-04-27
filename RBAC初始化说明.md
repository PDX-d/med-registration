# RBAC 权限初始化使用指南

## 📋 前置条件

1. ✅ 确保数据库中已创建RBAC相关表（sys_role, sys_permission, sys_user_role, sys_role_permission）
2. ✅ 确保已执行 `src/main/resources/MySQL/sql.sql` 中的初始化数据脚本
3. ✅ 确保Redis服务正在运行
4. ✅ 确保应用配置文件（application.yaml）中Redis配置正确

## 🚀 快速开始

### 方式一：一键完整初始化（推荐）

在IDEA中打开 `RbacInitTest.java`，运行 `fullInit()` 方法：

```java
@Test
```

这个方法会按顺序执行以下步骤：
1. 为ADMIN角色分配管理权限
2. 为DOCTOR角色分配医生权限
3. 为USER角色分配用户权限
4. 将所有角色权限加载到Redis
5. 为用户分配角色（示例）


### 方式二：分步执行

如果只想执行部分操作，可以单独运行以下方法：

#### 1️⃣ 为角色分配权限

```java
// 为ADMIN角色分配所有管理权限
@Test

// 为DOCTOR角色分配医生权限
@Test

// 为USER角色分配用户权限
@Test
```

#### 2️⃣ 将权限加载到Redis

```java
// 从数据库读取角色权限关系，写入Redis
@Test
```

#### 3️⃣ 查看Redis中的权限数据

```java
// 打印所有角色的权限信息
@Test
```

#### 4️⃣ 为用户分配角色

```java
// 示例：给用户ID=1分配ADMIN角色
@Test
```

#### 5️⃣ 清空Redis缓存（调试用）

```java
// 清空所有角色的权限数据
@Test
```

## 📊 权限说明

### ADMIN（管理员）权限
- 部门管理：view, add, update, delete
- 医生管理：view, register, delete
- 排班管理：view, add, update, delete
- 轮播图管理：view, add, update, delete
- 公告管理：view, add, update, delete

### DOCTOR（医生）权限
- 排班查看：schedule:view
- 预约查看：appointment:view
- 患者查看：patient:view

### USER（普通用户）权限
- 部门查看：department:view
- 医生查看：doctor:view
- 排班查看：schedule:view
- 预约管理：create, view, cancel, pay

## 🔍 Redis数据结构

权限数据存储在Redis的Set结构中：

```
Key格式: role:perms:{roleCode}
类型: Set
示例:
  - role:perms:ADMIN -> [department:view, department:add, ...]
  - role:perms:DOCTOR -> [schedule:view, appointment:view, ...]
  - role:perms:USER -> [department:view, doctor:view, ...]
```

## ⚠️ 注意事项

1. **首次使用前**必须先执行SQL脚本初始化数据库数据
2. **每次修改权限后**需要重新运行 `initRolePermissionsToRedis()` 更新Redis缓存
3. **生产环境**建议通过管理界面动态配置权限，而不是直接修改数据库
4. **测试环境**可以直接运行测试类进行初始化

## 🐛 常见问题

### Q1: 提示"数据库中没有角色数据"
**A:** 请先执行 `sql.sql` 中的INSERT语句，或运行 `assignPermissionsToAdminRole()` 等方法会自动插入数据

### Q2: Redis中没有权限数据
**A:** 请确保先运行 `initRolePermissionsToRedis()` 方法将数据库中的权限加载到Redis

### Q3: 权限验证不生效
**A:** 检查以下几点：
- Redis中是否有对应的权限数据（运行 `printAllRolePermissions()` 查看）
- LoginInterceptor是否正确配置
- 接口是否添加了 `@RequirePermission` 注解

## 📝 下一步

完成权限初始化后，你需要：
1. 在Controller接口上添加 `@RequirePermission` 注解
2. 修改登录逻辑，查询用户角色并存入Redis
3. 测试权限控制是否正常工作

祝使用愉快！🎉
