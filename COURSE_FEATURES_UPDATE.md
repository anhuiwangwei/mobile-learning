# 课程展示功能更新总结

## 更新内容

本次更新实现了教师发布课程功能，并在 App 端展示课程信息、学习进度和特殊标注。

---

## 一、数据库修改

### 1. 课程表 (edu_course)

新增字段：
- `page_turn_time` INT DEFAULT 0 - 翻页时长 (秒)，0 表示不限制
- `is_order_learning` TINYINT DEFAULT 0 - 是否顺序学习：0 否，1 是

### 2. 小节表 (edu_section)

新增字段：
- `is_no_drag` TINYINT DEFAULT 0 - 是否禁止拖动：0 否，1 是（仅视频有效）

---

## 二、后端修改

### 1. 实体类更新

**EduCourse.java**
- 添加 `pageTurnTime` 字段
- 添加 `isOrderLearning` 字段

**EduSection.java**
- 添加 `isNoDrag` 字段

### 2. DTO 更新

**CourseRequest.java**
- 添加 `pageTurnTime` 字段
- 添加 `isOrderLearning` 字段
- 添加 `teacherName` 字段（用于显示）

**SectionRequest.java**
- 添加 `isNoDrag` 字段

### 3. Controller 更新

**CourseController.java (管理端)**
- `addCourse()`: 添加新课程字段处理
- `updateCourse()`: 更新课程字段处理
- `addSection()`: 添加小节字段处理
- `updateSection()`: 更新小节字段处理

**CourseApiController.java (移动端 API)**
- `getCourseList()`: 返回课程列表时包含：
  - 课程基本信息
  - 学习进度（视频/PDF/考试数量及完成情况）
  - 教师姓名
- `getCourseProgress()`: 优化进度计算，支持考试类型

---

## 三、管理端修改

### 1. CourseList.vue

**列表显示**
- 新增"教师"列
- 新增"翻页时长"列
- 新增"顺序学习"列（标签显示）

**表单编辑**
- 添加"翻页时长"输入框（数字输入，单位秒）
- 添加"顺序学习"开关
- 添加教师姓名字段

### 2. ChapterList.vue

**列表显示**
- 小节类型显示图标：📹 视频、📄 PDF、📝 考试
- 新增"禁止拖动"列（标签显示）

**表单编辑**
- 添加"禁止拖动"开关（仅视频类型显示）
- 说明文字：启用后学生无法拖动视频进度条

---

## 四、移动端修改

### 1. Bean 更新

**Course.java**
- 添加 `teacherName` 字段
- 添加 `pageTurnTime` 字段
- 添加 `isOrderLearning` 字段
- 添加 `progress` 字段（Map 类型，包含学习进度信息）

**Section.java**
- 添加 `isNoDrag` 字段及 getter/setter

### 2. API 接口更新

**CourseApi.java**
- `getCourseList()`: 返回类型改为 `List<Map<String, Object>>`

### 3. 布局文件更新

**item_course.xml**
- 新增教师姓名显示
- 新增"顺序学习"标签（带图标 🔒）
- 新增"翻页时长"标签（带图标 ⏱）
- 新增学习进度显示（百分比）
- 新增小节类型图标统计（📹X 📄X 📝X）

### 4. Adapter 更新

**CourseAdapter.java**
- 更新 ViewHolder，添加新字段引用
- 更新 `getView()` 方法：
  - 显示教师姓名
  - 根据 `isOrderLearning` 显示/隐藏顺序学习标签
  - 根据 `pageTurnTime` 显示/隐藏翻页时长标签
  - 显示学习进度百分比
  - 统计并显示视频、PDF、考试数量及图标

### 5. Activity 更新

**CourseListActivity.java**
- 更新 `loadCourses()` 方法解析新的返回数据结构
- 从 Map 中提取 course、progress、teacherName 信息

---

## 五、特殊标注说明

### App 端显示规则

1. **翻页时长标注**
   - 条件：`pageTurnTime > 0`
   - 显示：`⏱ 翻页：X 秒`
   - 颜色：橙色 (#FF6B00)

2. **顺序学习标注**
   - 条件：`isOrderLearning == 1`
   - 显示：`🔒 顺序学习`
   - 颜色：橙色 (#FF6B00)

3. **小节类型图标**
   - 视频：📹
   - PDF：📄
   - 考试：📝
   - 显示格式：`📹3 📄2 📝1`

4. **视频禁止拖动标注**（管理端）
   - 条件：`isNoDrag == 1`
   - 显示：橙色标签"禁止拖动"

---

## 六、数据库迁移 SQL

```sql
-- 添加课程新字段
ALTER TABLE edu_course ADD COLUMN IF NOT EXISTS page_turn_time INT DEFAULT 0 COMMENT '翻页时长 (秒),0 表示不限制' AFTER duration;
ALTER TABLE edu_course ADD COLUMN IF NOT EXISTS is_order_learning TINYINT DEFAULT 0 COMMENT '是否顺序学习：0 否，1 是' AFTER page_turn_time;

-- 添加小节 is_no_drag 字段
ALTER TABLE edu_section ADD COLUMN IF NOT EXISTS is_no_drag TINYINT DEFAULT 0 COMMENT '是否禁止拖动：0 否，1 是 (仅视频有效)' AFTER is_allow_seek;
```

---

## 七、测试建议

1. **后端测试**
   - 创建课程，设置翻页时长和顺序学习选项
   - 创建视频小节，设置禁止拖动选项
   - 调用 `/api/course/list` 接口验证返回数据

2. **管理端测试**
   - 课程列表显示新字段
   - 课程表单可以编辑新字段
   - 章节管理显示小节类型图标
   - 视频小节可以设置禁止拖动

3. **移动端测试**
   - 课程列表显示教师姓名
   - 显示学习进度百分比
   - 显示视频/PDF/考试数量图标
   - 翻页时长和顺序学习标注正确显示

---

## 八、文件清单

### 后端文件
- `mobile-learning-api/src/main/java/com/mobilelearning/entity/EduCourse.java`
- `mobile-learning-api/src/main/java/com/mobilelearning/entity/EduSection.java`
- `mobile-learning-api/src/main/java/com/mobilelearning/dto/CourseRequest.java`
- `mobile-learning-api/src/main/java/com/mobilelearning/dto/SectionRequest.java`
- `mobile-learning-api/src/main/java/com/mobilelearning/controller/admin/CourseController.java`
- `mobile-learning-api/src/main/java/com/mobilelearning/controller/api/CourseApiController.java`

### 管理端文件
- `mobile-learning-admin/src/views/CourseList.vue`
- `mobile-learning-admin/src/views/ChapterList.vue`

### 移动端文件
- `MobileLearning/app/src/main/java/com/mobilelearning/bean/Course.java`
- `MobileLearning/app/src/main/java/com/mobilelearning/bean/Section.java`
- `MobileLearning/app/src/main/java/com/mobilelearning/api/CourseApi.java`
- `MobileLearning/app/src/main/java/com/mobilelearning/ui/adapter/CourseAdapter.java`
- `MobileLearning/app/src/main/java/com/mobilelearning/ui/activity/CourseListActivity.java`
- `MobileLearning/app/src/main/res/layout/item_course.xml`

### 数据库文件
- `mobile-learning.sql`

---

**更新时间**: 2026-03-04
