# 移动端 API 章节中小节不显示 - 最终修复

## 问题描述
移动端调用 `/api/course/1/chapters` 接口，返回的章节数据中**没有 sections 字段**。

## 问题原因
**CourseApiController.java** 的 `getChapters` 方法：
- 查询了章节 ✅
- 查询了小节 ✅
- **但是没有将小节赋值给章节对象** ❌

### 修复前的代码
```java
@GetMapping("/{courseId}/chapters")
public Result<List<EduChapter>> getChapters(@PathVariable Long courseId) {
    List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
    
    for (EduChapter chapter : chapters) {
        // ❌ 查询了小节但没有赋值
        List<EduSection> sections = eduSectionMapper.selectList(sectionWrapper);
        // 没有 chapter.setSections(sections);
    }
    
    return Result.success(chapters);
}
```

## 解决方案

### 修复后的代码
```java
@GetMapping("/{courseId}/chapters")
public Result<List<ChapterWithSectionsDTO>> getChapters(@PathVariable Long courseId) {
    // 1. 查询所有章节
    List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
    
    // 2. 为每个章节查询小节
    List<ChapterWithSectionsDTO> result = new ArrayList<>();
    for (EduChapter chapter : chapters) {
        ChapterWithSectionsDTO dto = new ChapterWithSectionsDTO();
        dto.setId(chapter.getId());
        dto.setCourseId(chapter.getCourseId());
        dto.setChapterName(chapter.getChapterName());
        dto.setChapterOrder(chapter.getChapterOrder());
        dto.setCreateTime(chapter.getCreateTime());
        
        // ✅ 查询该章节的小节并赋值
        List<EduSection> sections = eduSectionMapper.selectList(sectionWrapper);
        dto.setSections(sections);
        
        result.add(dto);
    }
    
    return Result.success(result);
}
```

## 文件修改

### 修改文件 (1 个)
- `controller/api/CourseApiController.java`
  - 添加 `import java.util.ArrayList;`
  - 修改返回类型从 `List<EduChapter>` 到 `List<ChapterWithSectionsDTO>`
  - 为每个章节查询小节并赋值

## 测试验证

### 测试命令
```bash
# 1. 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"13800138000","password":"123456"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 2. 测试章节接口
curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/course/1/chapters | python3 -m json.tool
```

### 测试结果 ✅
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "chapterName": "第一章：Java 基础语法",
      "sections": [
        {
          "id": 1,
          "sectionName": "1.1 Java 环境搭建",
          "sectionType": "video",
          "contentUrl": "/static/videos/java-env.mp4",
          "isAllowSeek": 1,
          "isStepLearning": 1
        },
        {
          "id": 2,
          "sectionName": "1.2 第一个 Java 程序",
          "sectionType": "video"
        },
        {
          "id": 3,
          "sectionName": "1.3 Java 基本数据类型",
          "sectionType": "video"
        }
      ]
    }
  ]
}
```

## 完整的数据流程

```
移动端用户
    ↓
打开课程详情
    ↓
调用：GET /api/course/{courseId}/chapters
    ↓
后端：CourseApiController.getChapters()
    ↓
1. 查询章节 (WHERE course_id = ?)
2. 遍历每个章节
3. 查询小节 (WHERE chapter_id = ?)
4. 将小节添加到章节对象
5. 返回 ChapterWithSectionsDTO[]
    ↓
Android: 解析为 List<Chapter>
    ↓
ChapterExpandableAdapter 渲染
    ↓
显示：章节 + 小节 ✅
```

## 修复完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
