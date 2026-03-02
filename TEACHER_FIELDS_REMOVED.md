# 移除教师职称和专业方向字段

## 修改概述
已从后端和管理端移除教师表的 `title`（职称）和 `specialty`（专业方向）字段。

---

## 修改内容

### 1. 后端实体类 ✅
**文件**: `EduTeacher.java`

**移除字段**:
- ❌ `private String title;`
- ❌ `private String specialty;`

**保留字段**:
- ✅ `private String teacherNo;` 工号
- ✅ `private String intro;` 简介

---

### 2. 后端 DTO ✅
**文件**: `TeacherRequest.java`

**移除字段**:
- ❌ `private String title;`
- ❌ `private String specialty;`

---

### 3. 后端服务实现 ✅
**文件**: `EduTeacherServiceImpl.java`

**修改**:
- `addTeacher` 方法：移除 title 和 specialty 设置
- `updateTeacher` 方法：移除 title 和 specialty 更新

---

### 4. 管理端教师列表 ✅
**文件**: `TeacherList.vue`

**表格列修改**:
```javascript
// 移除前
const columns = [
  { title: '职称', dataIndex: 'title', key: 'title' },
  { title: '专业方向', dataIndex: 'specialty', key: 'specialty' }
]

// 移除后
const columns = [
  { title: '简介', dataIndex: 'intro', key: 'intro', ellipsis: true }
]
```

**表单修改**:
```vue
<!-- 移除 -->
<a-form-item label="职称">
  <a-input v-model:value="form.title" />
</a-form-item>
<a-form-item label="专业方向">
  <a-input v-model:value="form.specialty" />
</a-form-item>
```

---

### 5. 教师选择器组件 ✅
**文件**: `TeacherSelect.vue`

**修改**:
```vue
<!-- 修改前 -->
{{ teacher.teacherNo }} - {{ teacher.realName }} ({{ teacher.title || '无职称' }})

<!-- 修改后 -->
{{ teacher.teacherNo }} - {{ teacher.realName }}
```

---

### 6. 数据库脚本 ✅
**文件**: `mobile-learning.sql`

**修改 edu_teacher 表**:
```sql
-- 移除
title VARCHAR(50) COMMENT '职称',
specialty VARCHAR(100) COMMENT '专业方向',

-- 保留
teacher_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工号',
intro TEXT COMMENT '教师简介',
```

---

## 现有教师信息字段

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 关联用户 ID |
| teacher_no | VARCHAR(50) | 工号 |
| intro | TEXT | 教师简介 |
| is_deleted | TINYINT | 是否删除 |

---

## 管理端功能

### 添加教师
1. 姓名（必填）
2. 手机号（必填）
3. 工号（必填）
4. 简介（可选）

### 编辑教师
1. 工号
2. 简介

---

## 修改文件清单

### 后端 (3 个文件)
- ✅ `entity/EduTeacher.java`
- ✅ `dto/TeacherRequest.java`
- ✅ `service/impl/EduTeacherServiceImpl.java`

### 管理端 (2 个文件)
- ✅ `views/TeacherList.vue`
- ✅ `components/Select/TeacherSelect.vue`

### 数据库 (1 个文件)
- ✅ `mobile-learning.sql`

---

## 完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成
