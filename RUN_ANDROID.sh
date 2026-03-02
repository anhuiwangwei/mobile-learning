#!/bin/bash
# Android 移动端运行脚本

echo "======================================"
echo "   移动学习平台 - Android 运行脚本"
echo "======================================"
echo ""

# 配置环境变量
export ANDROID_HOME=~/Library/Android/sdk
export ANDROID_SDK_ROOT=~/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator

cd /Volumes/txz/project/mobile-learning/MobileLearning

echo "1. 启动 Android Studio"
echo "   open -a 'Android Studio' ."
echo ""
echo "2. 或者使用命令行编译"
echo "   gradle assembleDebug"
echo ""
echo "3. 启动模拟器（在 Android Studio 中）"
echo "   Tools → Device Manager → 启动模拟器"
echo ""
echo "4. 运行应用"
echo "   点击 Run 按钮 或 gradle installDebug"
echo ""
echo "======================================"
echo "推荐方式：使用 Android Studio"
echo "======================================"
echo ""
echo "步骤:"
echo "1. 打开 Android Studio"
echo "2. File → Open → 选择 MobileLearning 目录"
echo "3. 等待 Gradle 同步完成"
echo "4. Tools → Device Manager → 创建模拟器"
echo "5. 启动模拟器"
echo "6. 点击 Run 按钮运行应用"
echo ""
echo "测试账号:"
echo "  手机号：13800138000"
echo "  密码：123456"
echo ""
echo "详细文档：MobileLearning/RUN_ON_ANDROID.md"
echo ""

# 自动打开 Android Studio
open -a "Android Studio" .
