# 课程和教师管理更新

## 更新概述

1. **课程管理**: 移除 URL 输入，必须上传封面图片，课程描述和封面必填
2. **教师管理**: 新增头像上传字段（非必填）

---

## 修改内容

### 1. 管理端 - 课程管理 ✅

**文件**: `mobile-learning-admin/src/views/CourseList.vue`

**修改**:
- ✅ 移除封面图片 URL 输入框
- ✅ 课程描述设为必填项
- ✅ 封面图片设为必填项（通过表单验证）
- ✅ 添加表单验证规则

**验证规则**:
```javascript
const formRules = {
  courseName: [{ required: true, message: '请输入课程名称' }],
  courseDesc: [{ required: true, message: '请输入课程描述' }],
  coverImage: [{ required: true, message: '请上传封面图片' }]
}
```

---

### 2. 管理端 - 教师管理 ✅

**文件**: `mobile-learning-admin/src/views/TeacherList.vue`

**修改**:
- ✅ 表格新增头像列
- ✅ 表单新增头像上传组件
- ✅ 头像为非必填项

**头像上传**:
```vue
<a-form-item label="头像">
  <ImageUpload v-model="form.avatar" :max-size="5" accept="image/*" />
</a-form-item>
```

---

### 3. 后端 - EduTeacher 实体 ✅

**文件**: `entity/EduTeacher.java`

**新增字段**:
```java
private String avatar;  // 教师头像
```

---

### 4. 后端 - TeacherRequest DTO ✅

**文件**: `dto/TeacherRequest.java`

**新增字段**:
```java
private String avatar;  // 教师头像
```

---

### 5. 后端 - Service 层 ✅

**文件**: `service/impl/EduTeacherServiceImpl.java`

**修改**:
- `addTeacher` 方法：添加 avatar 字段设置
- `updateTeacher` 方法：添加 avatar 字段更新

---

### 6. 后端 - Controller 层 ✅

**文件**: `controller/admin/AdminController.java`

**修改**:
- 添加教师时设置 avatar
- 更新教师时更新 avatar
- 恢复教师时恢复 avatar

---

### 7. 数据库脚本 ✅

**文件**: `mobile-learning.sql`

**修改 edu_teacher 表**:
```sql
CREATE TABLE edu_teacher (
    ...
    teacher_no  VARCHAR(50)  NOT NULL UNIQUE COMMENT '工号',
    avatar      VARCHAR(255) COMMENT '教师头像',  -- 新增
    intro       TEXT COMMENT '教师简介',
    ...
);

-- 更新现有表
ALTER TABLE edu_teacher 
ADD COLUMN IF NOT EXISTS avatar VARCHAR(255) COMMENT '教师头像' 
AFTER teacher_no;
```

---

## 功能说明

### 课程管理

**添加/编辑课程**:
1. 课程名称（必填）
2. 课程描述（必填）✅ 新增验证
3. 封面图片（必填）✅ 必须上传，不能输入 URL
4. 难度等级（可选）
5. 状态（上架/下架）

**验证**:
- 未填写课程名称 → 提示"请输入课程名称"
- 未填写课程描述 → 提示"请输入课程描述"
- 未上传封面图片 → 提示"请上传封面图片"

---

### 教师管理

**添加/编辑教师**:
1. 姓名（必填）
2. 手机号（必填）
3. 工号（必填）
4. 头像（可选）✅ 新增
5. 简介（可选）

**头像显示**:
- 列表页显示圆形头像（40x40px）
- 未上传头像显示 "-"

---

## 文件清单

### 管理端 (2 个文件)
- ✅ `views/CourseList.vue` - 课程管理页面
- ✅ `views/TeacherList.vue` - 教师管理页面

### 后端 (4 个文件)
- ✅ `entity/EduTeacher.java` - 教师实体
- ✅ `dto/TeacherRequest.java` - 教师请求 DTO
- ✅ `service/impl/EduTeacherServiceImpl.java` - 教师服务
- ✅ `controller/admin/AdminController.java` - 管理端控制器

### 数据库 (1 个文件)
- ✅ `mobile-learning.sql` - 数据库脚本

---

## 测试验证

### 课程管理测试
1. ✅ 尝试不填课程名称保存 → 提示验证错误
2. ✅ 尝试不填课程描述保存 → 提示验证错误
3. ✅ 尝试不上传封面保存 → 提示验证错误
4. ✅ 上传封面后保存 → 成功

### 教师管理测试
1. ✅ 添加教师不上传头像 → 成功（非必填）
2. ✅ 添加教师上传头像 → 成功
3. ✅ 列表显示头像 → 正常显示

---

## 完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
