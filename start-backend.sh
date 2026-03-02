#!/bin/bash
# 后端服务启动脚本

echo "======================================"
echo "   移动学习平台 - 后端服务启动"
echo "======================================"
echo ""

# 检查 MySQL
echo "检查 MySQL 服务..."
if ! pgrep -f "mysqld" > /dev/null; then
    echo "MySQL 未运行，正在启动..."
    mysql.server start
else
    echo "✓ MySQL 已运行"
fi

# 检查 Redis
echo "检查 Redis 服务..."
if ! pgrep -f "redis-server" > /dev/null; then
    echo "Redis 未运行，正在启动..."
    redis-server --daemonize yes
else
    echo "✓ Redis 已运行"
fi

echo ""
echo "请输入 MySQL root 密码:"
read -s MYSQL_PASSWORD
echo ""

# 创建数据库
echo "创建数据库..."
mysql -u root -p"$MYSQL_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS mobile_learning DEFAULT CHARACTER SET utf8mb4;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ 数据库创建成功"
else
    echo "✗ 数据库创建失败"
    exit 1
fi

# 导入数据
echo "导入数据..."
mysql -u root -p"$MYSQL_PASSWORD" mobile_learning < /Volumes/txz/project/mobile-learning/mobile-learning.sql 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ 数据导入成功"
else
    echo "⚠️ 数据导入失败（可能已导入）"
fi

# 更新后端配置
echo "更新后端配置..."
cd /Volumes/txz/project/mobile-learning/mobile-learning-api

# 备份原配置
cp src/main/resources/application.yml src/main/resources/application.yml.bak.$(date +%Y%m%d_%H%M%S)

# 更新数据库配置
cat > src/main/resources/application.yml << EOF
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: mobile-learning-api

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mobile_learning?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&allowMultiQueries=true
    username: root
    password: $MYSQL_PASSWORD

  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 5000ms

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.mobilelearning.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

jwt:
  secret: mobile-learning-secret-key-2024
  expiration: 86400000

logging:
  level:
    com.mobilelearning: info
EOF

echo "✓ 配置已更新"
echo ""
echo "启动后端服务..."
echo "======================================"
echo ""

# 启动后端
mvn spring-boot:run
