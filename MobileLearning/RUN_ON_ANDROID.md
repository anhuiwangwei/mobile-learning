# 在 Android 设备上运行

## 方式一：使用 Android Studio（推荐）

### 1. 打开项目
1. 启动 Android Studio
2. 选择 **File** → **Open**
3. 选择目录：`/Volumes/txz/project/mobile-learning/MobileLearning`
4. 等待 Gradle 同步完成

### 2. 创建/启动模拟器
1. 点击 **Tools** → **Device Manager**
2. 点击 **Create Device**
3. 选择 **Pixel 6**
4. 选择系统镜像：**API 34 (Android 14)**
5. 完成创建
6. 点击 ▶️ 启动模拟器

### 3. 运行应用
1. 确保模拟器已启动
2. 点击工具栏的绿色 ▶️ Run 按钮
3. 选择模拟器
4. 等待编译和安装

### 4. 测试账号
- 手机号：13800138000
- 密码：123456

---

## 方式二：使用命令行

### 1. 配置环境变量
```bash
export ANDROID_HOME=~/Library/Android/sdk
export ANDROID_SDK_ROOT=~/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator
```

### 2. 创建模拟器
```bash
# 创建测试模拟器
avdmanager create avd -n test_avd -k "system-images;android-34;google_apis;arm64-v8a" -d pixel_6
```

### 3. 启动模拟器
```bash
emulator -avd test_avd
```

### 4. 编译 APK
```bash
cd /Volumes/txz/project/mobile-learning/MobileLearning
gradle assembleDebug
```

### 5. 安装到模拟器
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 6. 启动应用
```bash
adb shell am start -n com.mobilelearning/.ui.activity.LoginActivity
```

---

## 方式三：真机调试

### 1. 启用开发者选项
1. 打开 **设置** → **关于手机**
2. 连续点击 **版本号** 7 次
3. 返回设置，进入 **开发者选项**
4. 启用 **USB 调试**

### 2. 连接电脑
1. 使用 USB 线连接手机
2. 在手机上允许 USB 调试授权

### 3. 安装 APK
```bash
# 检查设备连接
adb devices

# 安装 APK
adb install MobileLearning/app/build/outputs/apk/debug/app-debug.apk
```

---

## 常见问题

### 1. Gradle 同步失败
- 检查网络连接
- 清理缓存：`gradle clean`
- 重启 Android Studio

### 2. 模拟器启动慢
- 确保启用了虚拟化（VT-x/AMD-V）
- 使用 ARM64 系统镜像
- 增加模拟器内存

### 3. 网络请求失败
- 修改 `MainApplication.java` 中的 `BASE_URL`
- 模拟器使用：`http://10.0.2.2:8080/`
- 真机使用电脑 IP：`http://192.168.x.x:8080/`

---

## 功能演示

1. **登录**：使用测试账号登录
2. **首页**：查看功能入口
3. **课程列表**：浏览所有课程
4. **课程详情**：查看章节列表
5. **视频学习**：播放视频，测试进度保存
6. **PDF 学习**：阅读 PDF，测试时长统计
7. **考试**：答题并提交，查看成绩和解析

---

**注意**: 确保后端服务已启动（http://localhost:8080）
