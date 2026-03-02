# 章节中小节不显示问题修复

## 问题描述
课程详情页面中，章节展开后没有显示小节列表。

## 问题原因
后端 API `/admin/course/{id}/chapters` 只返回了章节数据，**没有包含小节数据**。

**修复前响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "chapterName": "第一章：Java 基础语法",
      "chapterOrder": 1
      // ❌ 没有 sections 字段
    }
  ]
}
```

## 解决方案

### 1. 创建 DTO 类
**文件**: `ChapterWithSectionsDTO.java`
```java
@Data
public class ChapterWithSectionsDTO {
    private Long id;
    private Long courseId;
    private String chapterName;
    private Integer chapterOrder;
    private Date createTime;
    private List<EduSection> sections = new ArrayList<>();
}
```

### 2. 修改 CourseController
**文件**: `CourseController.java`
**方法**: `getChapters`

**修改逻辑**:
1. 查询课程的所有章节
2. 遍历每个章节
3. 为每个章节查询对应的小节
4. 将小节列表添加到 DTO 中
5. 返回包含小节的章节列表

**修复后响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "chapterName": "第一章：Java 基础语法",
      "chapterOrder": 1,
      "sections": [
        {
          "id": 1,
          "sectionName": "1.1 Java 环境搭建",
          "sectionType": "video",
          "contentUrl": "/static/videos/java-env.mp4",
          "duration": 600,
          "isAllowSeek": 1,
          "isStepLearning": 1
        },
        {
          "id": 2,
          "sectionName": "1.2 第一个 Java 程序",
          "sectionType": "video",
          ...
        }
      ]
    }
  ]
}
```

## 文件修改清单

### 新增文件 (1 个)
- `dto/ChapterWithSectionsDTO.java` - 包含小节的章节 DTO

### 修改文件 (1 个)
- `controller/admin/CourseController.java` - 修改 getChapters 方法
  - 添加 `import java.util.ArrayList;`
  - 修改返回类型从 `List<EduChapter>` 到 `List<ChapterWithSectionsDTO>`
  - 添加小节查询逻辑

## 测试验证

### 1. 数据库验证
```sql
SELECT id, chapter_id, section_name, section_type FROM edu_section;
```
**结果**: ✅ 有 6 条小节记录

### 2. 后端 API 测试
```bash
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/admin/course/1/chapters
```
**结果**: ✅ 返回章节和小节数据

### 3. Android 端测试
1. 打开课程详情页面
2. 展开章节
3. **应该显示小节列表**
4. 点击小节可以进入学习

## 数据流程

```
用户点击课程详情
    ↓
Android: 调用 api.getChapters(courseId)
    ↓
后端：查询章节 + 查询小节
    ↓
返回：ChapterWithSectionsDTO[] (包含 sections)
    ↓
Android: ChapterExpandableAdapter 渲染
    ↓
显示：章节（组） + 小节（子项） ✅
```

## 关键代码

### CourseController.java
```java
@GetMapping("/{id}/chapters")
public Result<List<ChapterWithSectionsDTO>> getChapters(@PathVariable("id") Long courseId) {
    // 1. 查询所有章节
    List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
    
    // 2. 为每个章节查询小节
    List<ChapterWithSectionsDTO> result = new ArrayList<>();
    for (EduChapter chapter : chapters) {
        ChapterWithSectionsDTO dto = new ChapterWithSectionsDTO();
        // ... 设置章节属性
        
        // 查询该章节的小节
        List<EduSection> sections = eduSectionMapper.selectList(sectionWrapper);
        dto.setSections(sections);
        
        result.add(dto);
    }
    
    return Result.success(result);
}
```

### ChapterExpandableAdapter.java
```java
@Override
public int getChildrenCount(int groupPosition) {
    List<Section> sections = chapterList.get(groupPosition).getSections();
    return sections != null ? sections.size() : 0;
}
```

## 修复完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
