# 移动学习平台 - 运行说明

## 环境要求

### 必需软件
- **JDK**: 1.8+ (推荐 JDK 17)
- **Maven**: 3.6+
- **Node.js**: 16+ (推荐 v22)
- **MySQL**: 8.0+
- **Redis**: 6.0+

---

## 数据库配置

### 1. 创建数据库和用户

```sql
-- 登录 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mobile_learning 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建用户（如果需要）
CREATE USER IF NOT EXISTS 'mobile'@'localhost' IDENTIFIED BY 'mobile123';
GRANT ALL PRIVILEGES ON mobile_learning.* TO 'mobile'@'localhost';
FLUSH PRIVILEGES;

-- 使用 root 用户（开发环境）
-- 确保 root 用户可以访问
```

### 2. 导入数据

```bash
# 进入项目目录
cd /Volumes/txz/project/mobile-learning

# 导入 SQL 脚本
mysql -u root -p mobile_learning < mobile-learning.sql
# 或
mysql -u mobile -p mobile_learning < mobile-learning.sql
```

### 3. 配置后端数据库连接

编辑 `mobile-learning-api/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mobile_learning?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root        # 修改为你的数据库用户名
    password: yourpassword # 修改为你的数据库密码
```

---

## 启动服务

### 1. 启动 Redis

```bash
redis-server
# 或
redis-server --daemonize yes
```

### 2. 启动后端 API

```bash
cd mobile-learning-api

# 方式一：Maven 运行
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/mobile-learning-api-1.0.0.jar
```

**访问测试**: http://localhost:8080

### 3. 启动管理端

```bash
cd mobile-learning-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

**访问地址**: http://localhost:8081

### 4. 启动移动端

```bash
cd MobileLearning

# 方式一：Gradle 编译
./gradlew assembleDebug

# 方式二：在 Android Studio 中打开项目并运行
```

**APK 位置**: `MobileLearning/app/build/outputs/apk/debug/app-debug.apk`

---

## 测试账号

| 角色 | 账号 | 密码 | 入口 |
|------|------|------|------|
| 管理员 | admin | admin123 | 管理端 |
| 测试用户 | 13800138000 | 123456 | 移动端 |

---

## 常见问题

### 1. 后端启动失败 - 数据库连接错误

**错误**: `Access denied for user 'root'@'localhost'`

**解决**: 
- 检查 application.yml 中的数据库用户名密码
- 确认 MySQL 服务已启动
- 确认数据库已创建并导入数据

### 2. 后端启动失败 - Redis 连接错误

**错误**: `Cannot get Jedis connection`

**解决**: 
```bash
redis-server
```

### 3. 管理端无法访问后端

**检查**: 
- 确认后端服务已启动 (http://localhost:8080)
- 检查 vite.config.js 中的代理配置

### 4. 移动端网络请求失败

**解决**:
- 修改 `MainApplication.java` 中的 `BASE_URL`
- Android 模拟器使用 `10.0.2.2` 访问本机
- 真机调试使用电脑 IP 地址

---

## 端口配置

| 服务 | 默认端口 | 配置文件 |
|------|---------|---------|
| 后端 API | 8080 | application.yml |
| 管理端 | 8081 | vite.config.js |
| MySQL | 3306 | - |
| Redis | 6379 | - |

---

## 功能演示流程

### 管理端演示

1. **登录**: http://localhost:8081
   - 账号：admin
   - 密码：admin123

2. **查看仪表盘**: 显示统计数据

3. **用户管理**: 查看用户列表，禁用/启用用户

4. **教师管理**: 添加教师，编辑教师信息

5. **课程管理**: 
   - 创建课程
   - 添加章节
   - 添加小节（视频/PDF）
   - 配置快进控制/按步骤学习

6. **考试管理**:
   - 创建试卷
   - 添加题目（单选/判断）
   - 配置答案解析
   - 章节绑定考试

### 移动端演示

1. **登录/注册**: 使用测试账号登录

2. **浏览课程**: 查看课程列表

3. **课程学习**:
   - 查看课程详情
   - 学习视频（进度保存）
   - 学习 PDF（时长判定）

4. **参加考试**:
   - 开始考试
   - 答题（单选/判断）
   - 提交试卷
   - 查看成绩和解析

---

**文档更新时间**: 2026 年 3 月 1 日
