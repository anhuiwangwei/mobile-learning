#!/bin/bash
# 项目运行状态检查脚本

echo "======================================"
echo "   移动学习平台 - 运行状态检查"
echo "======================================"
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查函数
check_service() {
    local name=$1
    local port=$2
    local process=$3
    
    if lsof -ti:$port > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $name 运行中 (端口:$port)"
        return 0
    else
        echo -e "${RED}✗${NC} $name 未运行 (端口:$port)"
        return 1
    fi
}

check_process() {
    local name=$1
    local pattern=$2
    
    if pgrep -f "$pattern" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $name 运行中"
        return 0
    else
        echo -e "${RED}✗${NC} $name 未运行"
        return 1
    fi
}

# 检查服务
echo "=== 服务状态 ==="
echo ""
check_process "MySQL" "mysqld"
check_process "Redis" "redis-server"
echo ""
check_service "后端 API" "8080" "mobile-learning-api"
check_service "管理端" "8081" "vite"
echo ""

# 检查文件
echo "=== 项目文件 ==="
echo ""
if [ -f "/Volumes/txz/project/mobile-learning/mobile-learning-api/pom.xml" ]; then
    echo -e "${GREEN}✓${NC} 后端项目存在"
    JAVA_COUNT=$(find /Volumes/txz/project/mobile-learning/mobile-learning-api -name "*.java" | wc -l)
    echo "  Java 文件数：$JAVA_COUNT"
else
    echo -e "${RED}✗${NC} 后端项目不存在"
fi

if [ -f "/Volumes/txz/project/mobile-learning/mobile-learning-admin/package.json" ]; then
    echo -e "${GREEN}✓${NC} 管理端项目存在"
    VUE_COUNT=$(find /Volumes/txz/project/mobile-learning/mobile-learning-admin -name "*.vue" | wc -l)
    JS_COUNT=$(find /Volumes/txz/project/mobile-learning/mobile-learning-admin -name "*.js" | wc -l)
    echo "  Vue 文件数：$VUE_COUNT"
    echo "  JS 文件数：$JS_COUNT"
else
    echo -e "${RED}✗${NC} 管理端项目不存在"
fi

if [ -f "/Volumes/txz/project/mobile-learning/MobileLearning/app/build.gradle" ]; then
    echo -e "${GREEN}✓${NC} 移动端项目存在"
    JAVA_COUNT=$(find /Volumes/txz/project/mobile-learning/MobileLearning -name "*.java" | wc -l)
    XML_COUNT=$(find /Volumes/txz/project/mobile-learning/MobileLearning -name "*.xml" | wc -l)
    echo "  Java 文件数：$JAVA_COUNT"
    echo "  XML 文件数：$XML_COUNT"
else
    echo -e "${RED}✗${NC} 移动端项目不存在"
fi
echo ""

# 检查数据库
echo "=== 数据库状态 ==="
echo ""
if pgrep -f "mysqld" > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} MySQL 服务运行中"
    # 检查数据库是否存在（需要密码）
    echo "  提示：请确认 mobile_learning 数据库已创建"
else
    echo -e "${RED}✗${NC} MySQL 服务未启动"
    echo "  启动命令：mysql.server start"
fi
echo ""

# 访问地址
echo "=== 访问地址 ==="
echo ""
if lsof -ti:8080 > /dev/null 2>&1; then
    echo "后端 API:  ${GREEN}http://localhost:8080${NC}"
    echo "  测试：curl http://localhost:8080/admin/login"
else
    echo "后端 API:  ${RED}未运行${NC}"
    echo "  启动：cd mobile-learning-api && mvn spring-boot:run"
fi
echo ""

if lsof -ti:8081 > /dev/null 2>&1; then
    echo "管理端：   ${GREEN}http://localhost:8081${NC}"
    echo "  账号：admin"
    echo "  密码：admin123"
else
    echo "管理端：   ${RED}未运行${NC}"
    echo "  启动：cd mobile-learning-admin && npm install && npm run dev"
fi
echo ""

echo "移动端：   ${YELLOW}需要 Android Studio 运行${NC}"
echo "  项目位置：MobileLearning/"
echo "  说明：查看 ANDROID_INSTRUCTIONS.md"
echo ""

# 测试账号
echo "=== 测试账号 ==="
echo ""
echo "管理员：admin / admin123 (管理端)"
echo "测试用户：13800138000 / 123456 (移动端)"
echo ""

echo "======================================"
echo "检查完成"
echo "======================================"
