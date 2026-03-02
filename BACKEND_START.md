# 后端服务运行说明

## 方式一：使用启动脚本（推荐）

```bash
cd /Volumes/txz/project/mobile-learning
./start-backend.sh
# 输入 MySQL root 密码后自动配置并启动
```

## 方式二：手动配置

### 1. 编辑配置文件

编辑 `mobile-learning-api/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mobile_learning?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root          # 你的 MySQL 用户名
    password: yourpassword  # 你的 MySQL 密码
```

### 2. 导入数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS mobile_learning DEFAULT CHARACTER SET utf8mb4;

# 使用数据库
USE mobile_learning;

# 导入数据
source /Volumes/txz/project/mobile-learning/mobile-learning.sql;

# 查看表
SHOW TABLES;
```

### 3. 启动后端

```bash
cd /Volumes/txz/project/mobile-learning/mobile-learning-api

# 方式一：Maven 运行
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/mobile-learning-api-1.0.0.jar
```

## 测试后端

启动后测试登录接口：

```bash
curl -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

预期响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGc...",
    "userId": 1,
    "username": "admin",
    "role": "admin"
  }
}
```

## 常见问题

### 1. Access denied for user 'root'@'localhost'

**解决**: 检查 application.yml 中的用户名密码是否正确

### 2. 端口 8080 被占用

**解决**: 
```bash
lsof -ti:8080 | xargs kill -9
```

### 3. Redis 连接失败

**解决**:
```bash
redis-server
```

---

## 快速启动（如果 MySQL 无密码）

```bash
cd /Volumes/txz/project/mobile-learning/mobile-learning-api

# 更新配置（使用空密码）
sed -i.bak 's/password: .*/password: /' src/main/resources/application.yml

# 导入数据
mysql -u root mobile_learning < ../../mobile-learning.sql

# 启动
mvn spring-boot:run
```
