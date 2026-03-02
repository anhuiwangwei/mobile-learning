# 文件上传功能 - 管理端

## 功能概述

管理端章节管理页面现已支持视频和 PDF 文件上传到后端服务器，而不仅仅是填写 URL。

---

## 主要功能

### 1. 文件上传组件 ✅
**位置**: `mobile-learning-admin/src/components/Upload/FileUpload.vue`

**特性**:
- ✅ 支持视频上传（MP4, AVI, MOV, WMV）
- ✅ 支持 PDF 上传
- ✅ 文件大小限制（默认 100MB）
- ✅ 上传进度显示
- ✅ 文件预览（视频可播放，PDF 显示图标）
- ✅ 自动分类存储（videos/pdfs/images/files）
- ✅ 按日期组织文件（yyyy/MM/dd）

### 2. 章节管理页面 ✅
**位置**: `mobile-learning-admin/src/views/ChapterList.vue`

**功能**:
- ✅ 每个课程可以添加多个章节
- ✅ 每个章节可以添加多个小节（视频/PDF）
- ✅ 支持上传视频文件
- ✅ 支持上传 PDF 文件
- ✅ 也支持直接填写 URL（备用方式）
- ✅ 每个章节可以绑定多个考试
- ✅ 考试从考试管理中选择
- ✅ 配置快进控制
- ✅ 配置按步骤学习
- ✅ 配置 PDF 阅读时长

### 3. 后端文件上传接口 ✅
**位置**: `mobile-learning-api/src/main/java/com/mobilelearning/controller/admin/FileController.java`

**接口**: `POST /admin/file/upload`

**功能**:
- ✅ 文件上传到本地服务器
- ✅ 自动创建目录
- ✅ 生成唯一文件名
- ✅ 按文件类型分类存储
- ✅ 按日期组织目录
- ✅ 返回文件访问 URL

### 4. 静态文件访问配置 ✅
**位置**: `mobile-learning-api/src/main/java/com/mobilelearning/config/WebConfig.java`

**功能**:
- ✅ 配置 videos 目录静态访问
- ✅ 配置 pdfs 目录静态访问
- ✅ 配置 images 目录静态访问
- ✅ 配置 files 目录静态访问

---

## 文件存储结构

```
mobile-learning-api/
└── uploads/
    ├── videos/
    │   └── 2026/
    │       └── 03/
    │           └── 01/
    │               └── uuid.mp4
    ├── pdfs/
    │   └── 2026/
    │       └── 03/
    │           └── 01/
    │               └── uuid.pdf
    ├── images/
    └── files/
```

**访问 URL**:
- 视频：`http://localhost:8080/static/videos/2026/03/01/uuid.mp4`
- PDF: `http://localhost:8080/static/pdfs/2026/03/01/uuid.pdf`

---

## 使用流程

### 1. 添加章节
1. 进入课程管理
2. 点击某个课程的"章节管理"
3. 点击"添加章节"
4. 输入章节名称和排序
5. 保存

### 2. 添加小节（视频）
1. 点击章节的"添加小节"
2. 选择类型为"📹 视频"
3. 点击上传区域选择视频文件
4. 等待上传完成（显示进度）
5. 填写时长（秒）
6. 配置快进控制
7. 配置按步骤学习
8. 保存

### 3. 添加小节（PDF）
1. 点击章节的"添加小节"
2. 选择类型为"📄 PDF"
3. 点击上传区域选择 PDF 文件
4. 等待上传完成
5. 填写页数
6. 配置 PDF 阅读时长（秒）
7. 保存

### 4. 绑定考试
1. 点击章节的"绑定考试"
2. 从下拉列表选择试卷
3. 设置考试顺序
4. 设置是否必须通过
5. 设置及格分数
6. 保存

---

## API 接口

### 文件上传接口
```
POST /admin/file/upload
Content-Type: multipart/form-data

参数:
- file: 文件对象

响应:
{
  "code": 200,
  "data": {
    "url": "/static/pdfs/2026/03/01/uuid.pdf",
    "filename": "original.pdf",
    "newFilename": "uuid.pdf",
    "size": "123456",
    "type": "pdfs"
  }
}
```

### 章节管理接口
```
GET    /admin/course/{id}/chapters      # 获取章节列表
POST   /admin/course/chapter            # 添加章节
PUT    /admin/course/chapter            # 更新章节
DELETE /admin/course/chapter/{id}       # 删除章节

GET    /admin/course/chapter/{id}/sections  # 获取小节列表
POST   /admin/course/section                # 添加小节
PUT    /admin/course/section                # 更新小节
DELETE /admin/course/section/{id}           # 删除小节
```

---

## 配置说明

### 后端配置 (application.yml)
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 500MB

file:
  upload:
    path: ./uploads
```

### 前端配置
无需额外配置，组件自动使用后端 API

---

## 文件限制

### 视频文件
- **支持格式**: MP4, AVI, MOV, WMV, FLV, MKV
- **最大大小**: 500MB
- **存储目录**: uploads/videos/yyyy/MM/dd/

### PDF 文件
- **支持格式**: PDF
- **最大大小**: 500MB
- **存储目录**: uploads/pdfs/yyyy/MM/dd/

### 图片文件
- **支持格式**: JPG, JPEG, PNG, GIF, WEBP, BMP
- **最大大小**: 500MB
- **存储目录**: uploads/images/yyyy/MM/dd/

---

## 测试验证

### 1. 测试文件上传
```bash
# 获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 上传测试文件
echo "test pdf content" > /tmp/test.pdf
curl -s -X POST http://localhost:8080/admin/file/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/tmp/test.pdf" | python3 -m json.tool
```

### 2. 访问上传的文件
```bash
# 访问上传的 PDF
curl http://localhost:8080/static/pdfs/2026/03/01/uuid.pdf
```

---

## 修改文件清单

### 后端 (3 个文件)
1. ✅ `controller/admin/FileController.java` - 文件上传控制器
2. ✅ `config/WebConfig.java` - 静态文件访问配置
3. ✅ `application.yml` - 文件上传配置

### 管理端 (3 个文件)
1. ✅ `components/Upload/FileUpload.vue` - 文件上传组件
2. ✅ `views/ChapterList.vue` - 章节管理页面
3. ✅ `api/course.js` - 添加文件上传 API

---

## 功能特点

### 1. 灵活的上传方式
- **上传文件**: 直接上传到服务器
- **填写 URL**: 也可以使用外部 URL

### 2. 智能文件管理
- **自动分类**: 根据文件类型自动分类
- **日期组织**: 按日期组织文件目录
- **唯一命名**: 使用 UUID 避免文件名冲突

### 3. 用户友好
- **进度显示**: 实时显示上传进度
- **文件预览**: 视频可播放，PDF 显示图标
- **类型验证**: 自动验证文件类型
- **大小限制**: 提示文件大小超限

---

## 完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
