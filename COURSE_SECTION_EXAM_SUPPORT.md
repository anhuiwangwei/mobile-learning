# 课程小节支持考试类型

## 功能概述

现在一个课程下可以有多个章节，每个章节下可以有多个小节，小节类型支持：
- 📹 **视频** (video)
- 📄 **PDF** (pdf)
- 📝 **考试** (exam)

**小节排列示例**: 视频 → 视频 → 考试 → PDF → 考试

---

## 修改内容

### 1. 数据库修改 ✅

**表**: `edu_section`

**新增字段**:
```sql
ALTER TABLE edu_section 
ADD COLUMN exam_id BIGINT DEFAULT NULL COMMENT '关联试卷 ID(考试类型用)' 
AFTER is_free;
```

---

### 2. 后端实体类 ✅

**文件**: `EduSection.java`

**新增字段**:
```java
private Long examId;  // 如果是考试类型，关联 exam_paper 表
```

---

### 3. 后端 DTO ✅

**文件**: `SectionRequest.java`

**新增字段**:
```java
private Long examId;  // 考试类型的试卷 ID
```

---

### 4. 后端 Controller ✅

**文件**: `CourseController.java`

**修改**:
- `addSection` 方法：添加考试类型校验
- `updateSection` 方法：添加 examId 字段更新

**校验规则**:
- 考试类型必须有 examId
- 视频类型必须有 contentUrl
- PDF 类型必须有 contentUrl

---

### 5. 管理端章节管理页面 ✅

**文件**: `ChapterList.vue`

**修改**:
- 小节类型选择新增"考试"选项
- 选择考试类型时显示试卷选择下拉框
- 选择视频/PDF 类型时显示文件上传组件
- 列表显示优化（不同颜色区分类型）

**小节类型显示**:
- 📹 视频 - 蓝色标签
- 📄 PDF - 绿色标签
- 📝 考试 - 橙色标签

---

## 使用流程

### 添加章节和小节

1. **进入课程管理** → 点击某个课程的"章节管理"
2. **添加章节** → 输入章节名称
3. **添加小节** → 点击章节的"添加小节"
4. **选择小节类型**:
   - 📹 **视频**: 上传视频文件
   - 📄 **PDF**: 上传 PDF 文件
   - 📝 **考试**: 选择已有试卷
5. **配置其他选项**:
   - 时长/页数
   - PDF 阅读时长（仅 PDF）
   - 允许快进（仅视频）
   - 按步骤学习
   - 免费试看
   - 排序
   - 状态

### 示例：创建混合小节

```
章节：第一章 Java 基础
├── 📹 1.1 Java 环境搭建 (视频)
├── 📹 1.2 第一个 Java 程序 (视频)
├── 📝 1.3 基础测试 (考试)
├── 📄 1.4 语法手册 (PDF)
└── 📝 1.5 章节综合测试 (考试)
```

---

## 数据结构

### 课程 - 章节 - 小节关系

```
Course (课程)
├── Chapter 1 (章节 1)
│   ├── Section 1 (视频)
│   ├── Section 2 (视频)
│   ├── Section 3 (考试) → 关联 ExamPaper
│   ├── Section 4 (PDF)
│   └── Section 5 (考试) → 关联 ExamPaper
├── Chapter 2 (章节 2)
│   ├── Section 1 (视频)
│   └── Section 2 (PDF)
└── Chapter 3 (章节 3)
    ├── Section 1 (考试) → 关联 ExamPaper
    └── Section 2 (PDF)
```

---

## 字段说明

### 小节类型 (section_type)

| 值 | 说明 | 必填字段 |
|----|------|---------|
| video | 视频 | contentUrl (视频 URL) |
| pdf | PDF | contentUrl (PDF URL), pdfReadTime |
| exam | 考试 | examId (关联试卷 ID) |

### 视频特有字段

- `is_allow_seek`: 是否允许快进 (0 否/1 是)
- `duration`: 时长（秒）

### PDF 特有字段

- `pdf_read_time`: 最低阅读时长（秒）
- `duration`: 页数

### 考试特有字段

- `exam_id`: 关联试卷 ID

---

## 修改文件清单

### 后端 (3 个文件)
- ✅ `entity/EduSection.java`
- ✅ `dto/SectionRequest.java`
- ✅ `controller/admin/CourseController.java`

### 管理端 (1 个文件)
- ✅ `views/ChapterList.vue`

### 数据库 (1 个文件)
- ✅ `mobile-learning.sql` (已添加 exam_id 字段)

---

## 服务状态

| 服务 | 状态 |
|------|------|
| 后端 API | ✅ 运行中 |
| 管理端 | ✅ 运行中 |

---

## 完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
