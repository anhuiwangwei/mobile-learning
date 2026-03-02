# 管理端时间格式优化

## 优化概述

已将管理端所有页面的创建时间字段统一使用 dayjs 进行格式化显示。

---

## 修改内容

### 1. 创建时间格式化工具 ✅

**文件**: `src/utils/format.js`

**功能**:
- `formatDate(date, format)` - 格式化日期时间（默认：YYYY-MM-DD HH:mm:ss）
- `formatDateOnly(date)` - 仅日期部分（YYYY-MM-DD）
- `formatTimeOnly(date)` - 仅时间部分（HH:mm:ss）
- `formatRelative(date)` - 相对时间（如：几分钟前）
- `formatDuration(seconds)` - 时长格式化（如：1 小时 30 分）

---

### 2. 优化的页面 ✅

#### 用户管理页面
- ✅ 导入 `formatDate` 工具
- ✅ 创建时间列宽度调整为 180px
- ✅ 使用 `{{ formatDate(record.createTime) }}` 显示

#### 教师管理页面
- ✅ 导入 `formatDate` 工具
- ✅ 创建时间列宽度调整为 180px
- ✅ 使用 `{{ formatDate(record.createTime) }}` 显示

#### 课程管理页面
- ✅ 导入 `formatDate` 工具
- ✅ 创建时间列宽度调整为 180px
- ✅ 使用 `{{ formatDate(record.createTime) }}` 显示

#### 试卷管理页面
- ✅ 导入 `formatDate` 工具
- ✅ 创建时间列宽度调整为 180px
- ✅ 使用 `{{ formatDate(record.createTime) }}` 显示

#### 题目管理页面
- ✅ 导入 `formatDate` 工具
- ✅ 创建时间列宽度调整为 180px
- ✅ 使用 `{{ formatDate(record.createTime) }}` 显示

---

## 显示效果

### 优化前
```
2026-03-01T08:25:27.000+00:00
2026-03-01T08:25:27.000+00:00
```

### 优化后
```
2026-03-01 16:25:27
2026-03-01 16:25:27
```

---

## 可用的格式化选项

### 在组件中使用

```javascript
import { formatDate, formatDateOnly, formatTimeOnly, formatRelative } from '@/utils/format'

// 完整日期时间
formatDate(date) // 2026-03-01 16:25:27

// 仅日期
formatDateOnly(date) // 2026-03-01

// 仅时间
formatTimeOnly(date) // 16:25:27

// 相对时间
formatRelative(date) // 3 小时前

// 自定义格式
formatDate(date, 'YYYY-MM-DD') // 2026-03-01
formatDate(date, 'MM/DD HH:mm') // 03/01 16:25
```

---

## 常用格式模板

| 模板 | 示例输出 |
|------|---------|
| `YYYY-MM-DD HH:mm:ss` | 2026-03-01 16:25:27 |
| `YYYY-MM-DD HH:mm` | 2026-03-01 16:25 |
| `YYYY-MM-DD` | 2026-03-01 |
| `MM/DD HH:mm` | 03/01 16:25 |
| `MM-DD HH:mm` | 03-01 16:25 |
| `HH:mm:ss` | 16:25:27 |
| `HH:mm` | 16:25 |

---

## 修改文件清单

### 工具类 (1 个)
- ✅ `src/utils/format.js` - 新建

### 页面组件 (5 个)
- ✅ `src/views/UserList.vue`
- ✅ `src/views/TeacherList.vue`
- ✅ `src/views/CourseList.vue`
- ✅ `src/views/ExamPaperList.vue`
- ✅ `src/views/QuestionList.vue`

---

## 完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
