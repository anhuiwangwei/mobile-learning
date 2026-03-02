#!/bin/bash
# 数据库配置脚本

echo "=== 移动学习平台 - 数据库配置 ==="
echo ""

# 检查 MySQL 是否运行
if ! pgrep -x "mysqld" > /dev/null; then
    echo "MySQL 未运行，正在启动..."
    mysql.server start
fi

echo ""
echo "请输入 MySQL root 密码 (如果未设置密码，直接回车):"
read -s MYSQL_PASSWORD

# 创建数据库
echo ""
echo "正在创建数据库..."
mysql -u root -p"$MYSQL_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS mobile_learning DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ 数据库创建成功"
else
    echo "✗ 数据库创建失败，请检查 MySQL 配置"
    exit 1
fi

# 导入数据
echo "正在导入数据..."
mysql -u root -p"$MYSQL_PASSWORD" mobile_learning < /Volumes/txz/project/mobile-learning/mobile-learning.sql 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✓ 数据导入成功"
else
    echo "✗ 数据导入失败"
    exit 1
fi

# 更新后端配置
echo ""
echo "正在更新后端数据库配置..."
read -p "是否更新 application.yml 中的数据库配置？(y/n): " UPDATE_CONFIG

if [ "$UPDATE_CONFIG" = "y" ]; then
    read -p "数据库用户名 [root]: " DB_USERNAME
    DB_USERNAME=${DB_USERNAME:-root}
    
    read -s -p "数据库密码：" DB_PASSWORD
    echo ""
    
    # 备份原配置
    cp /Volumes/txz/project/mobile-learning/mobile-learning-api/src/main/resources/application.yml \
       /Volumes/txz/project/mobile-learning/mobile-learning-api/src/main/resources/application.yml.bak
    
    # 更新配置
    sed -i.bak "s/username: .*/username: $DB_USERNAME/" /Volumes/txz/project/mobile-learning/mobile-learning-api/src/main/resources/application.yml
    sed -i.bak "s/password: .*/password: $DB_PASSWORD/" /Volumes/txz/project/mobile-learning/mobile-learning-api/src/main/resources/application.yml
    
    echo "✓ 配置已更新"
    echo "  备份文件：application.yml.bak"
fi

echo ""
echo "=== 数据库配置完成 ==="
echo ""
echo "下一步:"
echo "1. 启动 Redis: redis-server"
echo "2. 启动后端：cd mobile-learning-api && mvn spring-boot:run"
echo "3. 启动管理端：cd mobile-learning-admin && npm install && npm run dev"
echo ""
