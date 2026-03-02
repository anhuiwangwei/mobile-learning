# 视频上传格式限制 - 仅支持 MP4

## 修改概述

已将管理端视频上传格式限制为**仅支持 MP4 格式**，其他格式（AVI, MOV, WMV 等）将被拒绝。

---

## 修改内容

### 1. 前端修改 ✅

**文件**: `mobile-learning-admin/src/components/Upload/FileUpload.vue`

#### 修改点 1: accept 属性
```javascript
// 修改前
const accept = computed(() => {
  if (props.type === 'video') {
    return 'video/*'  // ❌ 允许所有视频格式
  } else {
    return '.pdf'
  }
})

// 修改后
const accept = computed(() => {
  if (props.type === 'video') {
    return 'video/mp4,.mp4'  // ✅ 只允许 MP4
  } else {
    return '.pdf'
  }
})
```

#### 修改点 2: beforeUpload 验证
```javascript
// 修改前
if (props.type === 'video') {
  const videoTypes = ['video/mp4', 'video/avi', 'video/quicktime', 'video/x-msvideo']
  if (!videoTypes.includes(file.type) && !file.name.match(/\.(mp4|avi|mov|wmv)$/i)) {
    message.error('只能上传视频文件 (MP4, AVI, MOV, WMV)')
    return false
  }
}

// 修改后
if (props.type === 'video') {
  // 只允许 MP4 格式
  if (file.type !== 'video/mp4' && !file.name.match(/\.mp4$/i)) {
    message.error('只能上传 MP4 格式的视频文件')
    return false
  }
}
```

#### 修改点 3: 提示文字
```html
<!-- 修改前 -->
<div>支持 MP4, AVI, MOV 格式，大小不超过 100MB</div>

<!-- 修改后 -->
<div>支持 MP4 格式，大小不超过 100MB</div>
```

---

### 2. 后端修改 ✅

**文件**: `mobile-learning-api/src/main/java/com/mobilelearning/controller/admin/FileController.java`

#### 修改点 1: getFileType 方法
```java
// 修改前
} else if (extension.matches("\\.(mp4|avi|mov|wmv|flv|mkv)$")) {
    return "videos";

// 修改后
} else if (extension.matches("\\.(mp4)$")) {
    // 只允许 MP4 格式的视频
    return "videos";
```

#### 修改点 2: 添加文件类型验证方法
```java
/**
 * 验证文件类型是否允许上传
 */
private boolean isAllowedFileType(String extension) {
    if (extension == null) {
        return false;
    }
    extension = extension.toLowerCase();
    // 图片
    if (extension.matches("\\.(jpg|jpeg|png|gif|webp|bmp)$")) {
        return true;
    }
    // 视频（只允许 MP4）
    if (extension.matches("\\.(mp4)$")) {
        return true;
    }
    // PDF
    if (extension.matches("\\.(pdf)$")) {
        return true;
    }
    return false;
}
```

#### 修改点 3: upload 方法添加验证
```java
// 获取扩展名后添加验证
String extension = "";
if (originalFilename != null && originalFilename.contains(".")) {
    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
}

// 验证文件类型
if (!isAllowedFileType(extension)) {
    return Result.error("不支持的文件类型，视频只支持 MP4 格式，文档只支持 PDF 格式");
}
```

---

## 测试结果

### 测试 1: 上传 AVI 格式（失败）✅
```bash
$ curl -X POST http://localhost:8080/admin/file/upload \
  -F "file=@test.avi"

响应:
{
  "code": 500,
  "message": "不支持的文件类型，视频只支持 MP4 格式，文档只支持 PDF 格式"
}
```

### 测试 2: 上传 MP4 格式（成功）✅
```bash
$ curl -X POST http://localhost:8080/admin/file/upload \
  -F "file=@test.mp4"

响应:
{
  "code": 200,
  "data": {
    "type": "videos",
    "url": "/static/videos/2026/03/01/uuid.mp4"
  }
}
```

---

## 支持的格式

### ✅ 允许上传的格式
- **视频**: MP4 (.mp4)
- **文档**: PDF (.pdf)
- **图片**: JPG, JPEG, PNG, GIF, WEBP, BMP

### ❌ 拒绝上传的格式
- **视频**: AVI, MOV, WMV, FLV, MKV 等
- **其他**: 除上述格式外的所有文件

---

## 用户体验

### 前端提示
当用户尝试上传非 MP4 格式的视频时，会看到以下提示：

1. **文件选择阶段**: 文件选择对话框只显示 MP4 文件
2. **上传前验证**: "只能上传 MP4 格式的视频文件"
3. **大小限制**: "文件大小不能超过 100MB"

### 后端提示
如果绕过前端直接调用 API，后端会返回：
```json
{
  "code": 500,
  "message": "不支持的文件类型，视频只支持 MP4 格式，文档只支持 PDF 格式"
}
```

---

## 文件存储

上传的 MP4 文件将存储在：
```
uploads/videos/yyyy/MM/dd/uuid.mp4
```

访问 URL：
```
http://localhost:8080/static/videos/yyyy/MM/dd/uuid.mp4
```

---

## 修改文件清单

### 前端 (1 个文件)
- ✅ `mobile-learning-admin/src/components/Upload/FileUpload.vue`
  - 修改 accept 属性
  - 修改 beforeUpload 验证
  - 修改提示文字

### 后端 (1 个文件)
- ✅ `mobile-learning-api/src/main/java/com/mobilelearning/controller/admin/FileController.java`
  - 修改 getFileType 方法
  - 添加 isAllowedFileType 方法
  - 在 upload 方法中添加验证

---

## 配置说明

### 文件大小限制
在 `application.yml` 中配置：
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 500MB
```

### 上传路径
```yaml
file:
  upload:
    path: ./uploads
```

---

## 完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
