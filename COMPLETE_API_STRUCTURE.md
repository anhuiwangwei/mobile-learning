# 完整的课程 - 章节 - 小节 API 结构

## 数据结构

```
课程 (Course)
├── 章节 1 (Chapter)
│   ├── 小节 1 (Section - video)
│   ├── 小节 2 (Section - video)
│   └── 小节 3 (Section - video)
├── 章节 2 (Chapter)
│   ├── 小节 1 (Section - video)
│   └── 小节 2 (Section - video)
└── 章节 3 (Chapter)
    └── 小节 1 (Section - pdf)
```

## 数据库验证

**课程**: Java 基础入门 (ID: 1)

**章节和小节**:
1. 第一章：Java 基础语法
   - 1.1 Java 环境搭建 (video)
   - 1.2 第一个 Java 程序 (video)
   - 1.3 Java 基本数据类型 (video)
2. 第二章：面向对象编程
   - 2.1 类和对象 (video)
   - 2.2 封装与构造方法 (video)
3. 第三章：Java 核心类库
   - 3.1 常用 API 介绍 (pdf)

✅ 1 个课程，3 个章节，6 个小节（5 个视频 + 1 个 PDF）

---

## API 接口列表

### 1. 获取课程列表
```
GET /api/course/list
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "courseName": "Java 基础入门",
      "courseDesc": "Java 编程入门课程...",
      "coverImage": "https://picsum.photos/800/450",
      "teacherId": 1,
      "difficulty": 1,
      "duration": 600,
      "status": 1
    }
  ]
}
```

---

### 2. 获取课程详情（包含所有章节和小节）
```
GET /api/course/{id}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "course": {
      "id": 1,
      "courseName": "Java 基础入门",
      "courseDesc": "Java 编程入门课程...",
      "coverImage": "https://picsum.photos/800/450",
      "teacherId": 1,
      "difficulty": 1,
      "duration": 600,
      "status": 1
    },
    "chapters": [
      {
        "id": 1,
        "courseId": 1,
        "chapterName": "第一章：Java 基础语法",
        "chapterOrder": 1,
        "sections": [
          {
            "id": 1,
            "chapterId": 1,
            "sectionName": "1.1 Java 环境搭建",
            "sectionType": "video",
            "contentUrl": "/static/videos/java-env.mp4",
            "duration": 600,
            "isAllowSeek": 1,
            "isStepLearning": 1,
            "isFree": 1
          },
          {
            "id": 2,
            "sectionName": "1.2 第一个 Java 程序",
            "sectionType": "video",
            "contentUrl": "/static/videos/hello-java.mp4",
            "duration": 480
          },
          {
            "id": 3,
            "sectionName": "1.3 Java 基本数据类型",
            "sectionType": "video",
            "contentUrl": "/static/videos/data-types.mp4",
            "duration": 720
          }
        ]
      },
      {
        "id": 2,
        "chapterName": "第二章：面向对象编程",
        "sections": [
          {
            "id": 4,
            "sectionName": "2.1 类和对象",
            "sectionType": "video"
          },
          {
            "id": 5,
            "sectionName": "2.2 封装与构造方法",
            "sectionType": "video"
          }
        ]
      },
      {
        "id": 3,
        "chapterName": "第三章：Java 核心类库",
        "sections": [
          {
            "id": 6,
            "sectionName": "3.1 常用 API 介绍",
            "sectionType": "pdf",
            "contentUrl": "/static/pdfs/java-api.pdf",
            "duration": 50,
            "pdfReadTime": 300
          }
        ]
      }
    ]
  }
}
```

---

### 3. 获取课程的章节列表（单独接口）
```
GET /api/course/{courseId}/chapters
```

**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "courseId": 1,
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
        }
      ]
    }
  ]
}
```

---

### 4. 获取小节详情
```
GET /api/course/section/{id}
```

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "chapterId": 1,
    "sectionName": "1.1 Java 环境搭建",
    "sectionType": "video",
    "contentUrl": "/static/videos/java-env.mp4",
    "duration": 600,
    "isAllowSeek": 1,
    "isStepLearning": 1,
    "isFree": 1
  }
}
```

---

## Android 端使用示例

### 1. 获取课程列表
```java
CourseApi api = RetrofitUtil.create(CourseApi.class);
api.getCourseList(null, 1).enqueue(new Callback<BaseResponse<List<Course>>>() {
    @Override
    public void onResponse(Call<BaseResponse<List<Course>>> call, 
                           Response<BaseResponse<List<Course>>> response) {
        if (response.isSuccessful()) {
            List<Course> courses = response.body().getData();
            // 显示课程列表
        }
    }
});
```

### 2. 点击课程，获取课程详情（所有章节和小节）
```java
CourseApi api = RetrofitUtil.create(CourseApi.class);
api.getCourseDetail(courseId).enqueue(new Callback<BaseResponse<Map<String, Object>>>() {
    @Override
    public void onResponse(Call<BaseResponse<Map<String, Object>>> call, 
                           Response<BaseResponse<Map<String, Object>>> response) {
        if (response.isSuccessful()) {
            Map<String, Object> data = response.body().getData();
            Course course = (Course) data.get("course");
            List<Chapter> chapters = (List<Chapter>) data.get("chapters");
            
            // chapters 包含所有章节，每个章节包含 sections（小节列表）
            for (Chapter chapter : chapters) {
                List<Section> sections = chapter.getSections();
                // sections 包含该章节的所有小节
            }
        }
    }
});
```

### 3. 单独获取章节列表（如果不需要课程信息）
```java
CourseApi api = RetrofitUtil.create(CourseApi.class);
api.getChapters(courseId).enqueue(new Callback<BaseResponse<List<Chapter>>>() {
    @Override
    public void onResponse(Call<BaseResponse<List<Chapter>>> call, 
                           Response<BaseResponse<List<Chapter>>> response) {
        if (response.isSuccessful()) {
            List<Chapter> chapters = response.body().getData();
            // chapters 包含所有章节，每个章节包含 sections
        }
    }
});
```

---

## 后端代码修复

### 修复文件
**文件**: `CourseApiController.java`

**修复内容**:
1. `getCourseDetail` 方法 - 使用 `ChapterWithSectionsDTO` 返回章节和小节
2. `getChapters` 方法 - 使用 `ChapterWithSectionsDTO` 返回章节和小节

### 关键代码
```java
@GetMapping("/{id}")
public Result<Map<String, Object>> getCourseDetail(@PathVariable Long id) {
    // 1. 查询课程
    EduCourse course = eduCourseMapper.selectById(id);
    
    // 2. 查询所有章节
    List<EduChapter> chapters = eduChapterMapper.selectList(chapterWrapper);
    
    // 3. 为每个章节查询小节
    List<ChapterWithSectionsDTO> chaptersWithSections = new ArrayList<>();
    for (EduChapter chapter : chapters) {
        ChapterWithSectionsDTO dto = new ChapterWithSectionsDTO();
        // 设置章节属性...
        
        // 查询该章节的小节
        List<EduSection> sections = eduSectionMapper.selectList(sectionWrapper);
        dto.setSections(sections);  // ✅ 关键：将小节赋值
        
        chaptersWithSections.add(dto);
    }
    
    // 4. 返回包含小节的章节列表
    Map<String, Object> result = new HashMap<>();
    result.put("course", course);
    result.put("chapters", chaptersWithSections);  // ✅ 包含小节
    return Result.success(result);
}
```

---

## 测试命令

### 1. 登录获取 Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"13800138000","password":"123456"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)
```

### 2. 获取课程列表
```bash
curl -s "http://localhost:8080/api/course/list" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

### 3. 获取课程详情（所有章节和小节）
```bash
curl -s "http://localhost:8080/api/course/1" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

### 4. 获取章节列表
```bash
curl -s "http://localhost:8080/api/course/1/chapters" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

---

## 修复完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过

## 数据结构验证
✅ 1 个课程
✅ 3 个章节
✅ 6 个小节（5 个视频 + 1 个 PDF）
✅ 每个章节都有小节
✅ API 返回数据包含完整的章节和小节
