# 移动端功能修复说明

## 修复内容

### 1. 考试列表页面 ✅
- **新建**: `ExamListActivity.java`
- **布局**: `activity_exam_list.xml`
- **功能**: 
  - 显示所有可用考试列表
  - 支持下拉刷新
  - 点击进入考试详情
  - 空数据提示

### 2. 我的考试页面 ✅
- **新建**: `MyExamActivity.java`
- **布局**: `activity_my_exam.xml`
- **功能**:
  - 显示个人考试记录
  - 支持下拉刷新
  - 空数据提示（暂无考试记录）
  - 查看考试详情

### 3. 个人中心页面 ✅
- **新建**: `ProfileActivity.java`
- **布局**: `activity_profile.xml`
- **功能**:
  - 显示用户信息（用户名、姓名、手机号、角色）
  - 角色显示（管理员/教师/学生）
  - 编辑个人资料（待实现）
  - 修改密码（待实现）
  - 退出登录

### 4. 主页功能更新 ✅
- **更新**: `MainActivity.java`
- **功能**:
  - 考试列表按钮跳转到 ExamListActivity
  - 个人中心按钮跳转到 ProfileActivity
  - 侧边栏导航：考试记录 → MyExamActivity
  - 侧边栏导航：个人中心 → ProfileActivity

### 5. AndroidManifest 配置 ✅
- **更新**: `AndroidManifest.xml`
- **新增 Activity 声明**:
  - ExamListActivity
  - MyExamActivity
  - ProfileActivity

### 6. 登录功能优化 ✅
- **更新**: `LoginActivity.java`
- **功能**:
  - 保存用户信息到本地（username, realName, phone, role）
  - 个人中心可从本地读取用户信息

## 后端 API 调用

### 考试列表 API
```java
GET /api/exam/list
返回：考试列表（ExamPaper[]）
```

### 我的考试记录 API
```java
GET /api/exam/records
返回：考试记录列表（ExamRecord[]）
```

### 用户信息 API
```java
GET /api/auth/info
返回：用户信息（User）
```

## 测试步骤

1. **启动后端服务**
   ```bash
   cd mobile-learning-api
   mvn spring-boot:run
   ```

2. **在 Android Studio 中运行移动端**
   - 打开 MobileLearning 项目
   - 启动模拟器
   - 点击 Run

3. **测试功能**
   - 登录：使用 admin/admin123 或 13800138000/123456
   - 考试列表：点击"考试列表"按钮
   - 我的考试：侧边栏 → 我的考试
   - 个人中心：点击"个人中心"按钮或侧边栏 → 个人中心

## 文件清单

### Java 文件（新增 3 个）
- `ui/activity/ExamListActivity.java`
- `ui/activity/MyExamActivity.java`
- `ui/activity/ProfileActivity.java`

### 布局文件（新增 3 个）
- `layout/activity_exam_list.xml`
- `layout/activity_my_exam.xml`
- `layout/activity_profile.xml`

### 更新文件（3 个）
- `ui/activity/MainActivity.java`
- `ui/activity/LoginActivity.java`
- `AndroidManifest.xml`

## 界面预览

### 考试列表
- 标题栏：考试列表
- 列表：显示所有考试
- 空数据：显示"暂无考试"提示

### 我的考试
- 标题栏：我的考试
- 列表：显示考试记录（分数、状态、时间）
- 空数据：显示"暂无考试记录"提示

### 个人中心
- 用户信息卡片
  - 用户名
  - 姓名
  - 手机号
  - 角色
- 账户设置
  - 编辑个人资料
  - 修改密码
  - 退出登录

---

**修复完成时间**: 2026 年 3 月 1 日
**状态**: ✅ 已完成
