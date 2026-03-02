# Android 移动端运行说明

## 环境要求

### 必需软件
1. **Android Studio**: [下载地址](https://developer.android.com/studio)
2. **JDK**: 1.8+ (Android Studio 自带)
3. **Android SDK**: API 24+ (通过 Android Studio 安装)

## 安装步骤

### 1. 安装 Android Studio

1. 下载并安装 Android Studio
2. 打开 Android Studio
3. 完成 SDK 安装向导

### 2. 创建模拟器

1. 打开 Android Studio
2. 点击 **Tools** → **Device Manager**
3. 点击 **Create Device**
4. 选择设备 (推荐 Pixel 6)
5. 选择系统镜像 (推荐 API 33)
6. 完成创建

### 3. 打开项目

1. 启动 Android Studio
2. 选择 **Open an Existing Project**
3. 选择目录：`/Volumes/txz/project/mobile-learning/MobileLearning`
4. 等待 Gradle 同步完成

### 4. 配置 API 地址

编辑 `MainApplication.java`:
```java
// 模拟器使用 10.0.2.2 访问本机
public static final String BASE_URL = "http://10.0.2.2:8080/";

// 真机调试使用电脑 IP
// public static final String BASE_URL = "http://192.168.x.x:8080/";
```

### 5. 运行应用

#### 方式一：Android Studio
1. 启动模拟器
2. 点击 **Run** 按钮 (绿色三角形)
3. 选择模拟器
4. 等待编译和安装

#### 方式二：Gradle 命令行
```bash
cd MobileLearning

# 编译 Debug 包
./gradlew assembleDebug

# APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk

# 安装到模拟器
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 测试账号

| 账号 | 密码 | 说明 |
|------|------|------|
| 13800138000 | 123456 | 测试用户 |

## 功能演示

1. **登录**: 使用测试账号登录
2. **浏览课程**: 查看课程列表
3. **课程详情**: 查看章节列表
4. **视频学习**: 播放视频，测试进度保存
5. **PDF 学习**: 翻页阅读，测试时长统计
6. **参加考试**: 答题并提交，查看成绩

## 常见问题

### 1. 网络请求失败

**原因**: API 地址配置错误

**解决**: 
- 模拟器使用 `http://10.0.2.2:8080/`
- 真机使用电脑 IP 地址
- 确保后端服务已启动

### 2. 清缓存失败

**解决**:
```bash
cd MobileLearning
./gradlew clean
```

### 3. Gradle 同步失败

**解决**:
- 检查网络连接
- 修改 gradle 镜像源
- 重启 Android Studio

---

## APK 安装

如果已编译好 APK，可以直接安装：

```bash
# 确保模拟器已启动
adb devices

# 安装 APK
adb install MobileLearning/app/build/outputs/apk/debug/app-debug.apk
```
