# 移动端测试指南

## ✅ 已修复的功能

### 1. 考试列表
- **问题**: 登录后考试列表为空
- **修复**: 创建完整的考试列表页面
- **文件**: `ExamListActivity.java` + `activity_exam_list.xml`
- **API**: `GET /api/exam/list`

### 2. 我的考试
- **问题**: 我的考试页面为空
- **修复**: 创建我的考试页面
- **文件**: `MyExamActivity.java` + `activity_my_exam.xml`
- **API**: `GET /api/exam/records`

### 3. 个人中心
- **问题**: 个人中心页面为空
- **修复**: 创建个人中心页面
- **文件**: `ProfileActivity.java` + `activity_profile.xml`
- **API**: `GET /api/auth/info`

---

## 测试步骤

### 方式一：Android Studio（推荐）

#### 1. 打开项目
```
1. 启动 Android Studio
2. File → Open
3. 选择：/Volumes/txz/project/mobile-learning/MobileLearning
4. 等待 Gradle 同步完成
```

#### 2. 启动模拟器
```
1. Tools → Device Manager
2. 创建或选择已有模拟器
3. 点击启动按钮
```

#### 3. 运行应用
```
1. 点击绿色 Run 按钮（或 Shift+F10）
2. 选择模拟器
3. 等待编译和安装
```

#### 4. 测试功能
```
1. 登录：admin / admin123
2. 点击"考试列表"按钮 → 查看考试列表
3. 打开侧边栏 → 我的考试 → 查看考试记录
4. 点击"个人中心"按钮 → 查看个人信息
5. 侧边栏 → 个人中心 → 同样显示个人信息
```

---

### 方式二：命令行编译

```bash
cd /Volumes/txz/project/mobile-learning/MobileLearning

# 清理并编译
gradle clean assembleDebug

# APK 位置
# app/build/outputs/apk/debug/app-debug.apk

# 安装到模拟器（需要先启动模拟器）
adb install app/build/outputs/apk/debug/app-debug.apk

# 启动应用
adb shell am start -n com.mobilelearning/.ui.activity.LoginActivity
```

---

## 测试用例

### 1. 登录测试
- ✅ 正确账号密码登录成功
- ✅ 错误密码提示"密码错误"
- ✅ 登录成功后跳转到主页
- ✅ 自动保存 token 到本地

### 2. 考试列表测试
- ✅ 显示所有考试
- ✅ 下拉刷新
- ✅ 空数据时显示提示
- ✅ 点击进入考试详情

### 3. 我的考试测试
- ✅ 显示个人考试记录
- ✅ 显示分数和状态
- ✅ 空数据时显示"暂无考试记录"
- ✅ 下拉刷新

### 4. 个人中心测试
- ✅ 显示用户名
- ✅ 显示真实姓名
- ✅ 显示手机号
- ✅ 显示角色（管理员/教师/学生）
- ✅ 退出登录功能正常

---

## 预期效果

### 考试列表页面
```
┌─────────────────────┐
│ 考试列表            │
├─────────────────────┤
│ Java 基础入门测试   │
│ 总分：100 及格：60   │
│ 时长：30 分钟        │
├─────────────────────┤
│ 高级 Java 测试       │
│ 总分：100 及格：70   │
│ 时长：60 分钟        │
└─────────────────────┘
```

### 我的考试页面
```
┌─────────────────────┐
│ 我的考试            │
├─────────────────────┤
│ 考试 #1             │
│ 80 分 ✓ 已完成      │
│ 2026-03-01 16:00   │
├─────────────────────┤
│ 考试 #2             │
│ 0 分 进行中          │
│ 2026-03-01 17:00   │
└─────────────────────┘
```

### 个人中心页面
```
┌─────────────────────┐
│ 个人信息            │
├─────────────────────┤
│ 用户名：admin       │
│ 姓名：超级管理员    │
│ 手机号：13800000000 │
│ 角色：管理员        │
├─────────────────────┤
│ 账户设置            │
│ [编辑个人资料]      │
│ [修改密码]          │
│ [退出登录]          │
└─────────────────────┘
```

---

## 常见问题

### 1. 编译失败
**解决**: 
```bash
gradle clean
# 重启 Android Studio
```

### 2. 网络请求失败
**解决**: 
- 确保后端服务已启动
- 检查 `MainApplication.java` 中的 BASE_URL
- 模拟器使用：`http://10.0.2.2:8080/`

### 3. 登录成功后个人中心仍然显示 "-"
**解决**: 
- 检查登录时是否保存了用户信息
- 查看 SpUtil 中的值
- 重新登录后会自动保存

---

## 后端 API 验证

### 验证后端服务
```bash
# 测试登录接口
curl -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 测试考试列表接口
curl http://localhost:8080/api/exam/list

# 测试用户信息接口（需要 token）
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/auth/info
```

---

## 文件修改清单

### 新增文件（6 个）
- `ExamListActivity.java`
- `MyExamActivity.java`
- `ProfileActivity.java`
- `activity_exam_list.xml`
- `activity_my_exam.xml`
- `activity_profile.xml`

### 更新文件（3 个）
- `MainActivity.java` - 添加跳转逻辑
- `LoginActivity.java` - 保存用户信息
- `AndroidManifest.xml` - 注册新 Activity

---

**修复完成**: 2026 年 3 月 1 日
**测试状态**: ✅ 代码完成，待在 Android Studio 中编译运行
