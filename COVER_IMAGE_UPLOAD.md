# 课程封面图片上传功能

## 功能概述

管理端课程管理页面现已支持上传封面图片到后端服务器，而不仅仅是填写 URL。

---

## 主要功能

### 1. 图片上传组件 ✅
**位置**: `mobile-learning-admin/src/components/Upload/ImageUpload.vue`

**特性**:
- ✅ 支持图片上传（JPG, JPEG, PNG, GIF, WEBP）
- ✅ 文件大小限制（默认 10MB）
- ✅ 图片预览
- ✅ 上传进度显示
- ✅ 自动存储到 images 目录
- ✅ 按日期组织文件（yyyy/MM/dd）

### 2. 课程管理页面 ✅
**位置**: `mobile-learning-admin/src/views/CourseList.vue`

**修改内容**:
- ✅ 添加封面图片上传功能
- ✅ 保留 URL 输入方式（备用）
- ✅ 图片预览显示
- ✅ 上传成功后显示文件地址

---

## 使用流程

### 添加课程
1. 进入课程管理页面
2. 点击"添加课程"
3. 填写课程名称和描述
4. **上传封面图片**:
   - 点击上传区域
   - 选择图片文件
   - 等待上传完成
   - 预览图片
5. 设置难度等级和状态
6. 保存

### 编辑课程
1. 点击课程的"编辑"按钮
2. 可以查看当前封面图片
3. 点击上传区域更换封面
4. 或者直接输入新的 URL
5. 保存

---

## 文件存储

### 存储结构
```
uploads/
└── images/
    └── 2026/
        └── 03/
            └── 01/
                └── uuid.jpg
```

### 访问 URL
```
http://localhost:8080/static/images/2026/03/01/uuid.jpg
```

---

## API 接口

### 文件上传接口
```
POST /admin/file/upload
Content-Type: multipart/form-data

参数:
- file: 图片文件

响应:
{
  "code": 200,
  "data": {
    "url": "/static/images/2026/03/01/uuid.jpg",
    "filename": "original.jpg",
    "newFilename": "uuid.jpg",
    "size": "123456",
    "type": "images"
  }
}
```

---

## 支持的文件格式

### ✅ 允许上传的图片格式
- JPG / JPEG
- PNG
- GIF
- WEBP
- BMP

### ❌ 拒绝的格式
- 其他所有非图片格式

### 文件大小限制
- **最大**: 10MB（可在组件中配置）
- **推荐**: 小于 2MB 以保证加载速度

---

## 组件配置

### ImageUpload 组件参数
```vue
<ImageUpload
  v-model="coverImage"
  :max-size="10"
  @change="onImageUpload"
/>
```

**参数说明**:
- `v-model`: 双向绑定图片 URL
- `max-size`: 最大文件大小（MB），默认 5
- `@change`: 上传完成回调

---

## 界面展示

### 上传前
```
┌─────────────────────────┐
│   +                     │
│   点击上传              │
│   支持 JPG, PNG 等格式   │
│   大小不超过 10MB        │
└─────────────────────────┘
```

### 上传后
```
┌─────────────────────────┐
│   [课程封面图片]         │
│   (鼠标悬停显示更换)     │
└─────────────────────────┘
图片地址：/static/images/...
```

---

## 修改文件清单

### 管理端 (1 个文件)
- ✅ `views/CourseList.vue`
  - 导入 ImageUpload 组件
  - 添加封面图片上传表单项
  - 保留 URL 输入备用方式
  - 显示上传后的图片地址

### 已有组件
- ✅ `components/Upload/ImageUpload.vue` - 图片上传组件（已存在）
- ✅ `components/Upload/FileUpload.vue` - 文件上传组件（已存在）

---

## 后端支持

### 文件上传控制器
**文件**: `controller/admin/FileController.java`

**功能**:
- ✅ 接收图片上传
- ✅ 验证文件类型
- ✅ 生成唯一文件名
- ✅ 存储到 images 目录
- ✅ 返回访问 URL

### 静态文件配置
**文件**: `config/WebConfig.java`

**功能**:
- ✅ 配置 `/static/images/**` 静态访问
- ✅ 映射到本地 uploads/images 目录

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

## 测试验证

### 1. 上传测试图片
```bash
# 获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 上传图片
curl -s -X POST http://localhost:8080/admin/file/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@test.jpg" | python3 -m json.tool
```

### 2. 访问上传的图片
```bash
curl http://localhost:8080/static/images/yyyy/MM/dd/uuid.jpg
```

---

## 功能特点

### 1. 灵活的上传方式
- **上传图片**: 直接上传到服务器
- **填写 URL**: 也可以使用外部 URL

### 2. 用户友好
- **图片预览**: 上传后立即显示预览
- **进度显示**: 实时显示上传进度
- **类型验证**: 自动验证图片格式
- **大小限制**: 提示文件超限

### 3. 智能管理
- **唯一命名**: 使用 UUID 避免冲突
- **分类存储**: 图片存储在 images 目录
- **日期组织**: 按日期组织文件

---

## 最佳实践

### 图片推荐
- **格式**: JPG（照片），PNG（图形/透明背景）
- **尺寸**: 建议 800x450 像素（16:9 比例）
- **大小**: 建议小于 200KB
- **质量**: 80% 压缩比

### 性能优化
- 使用适当的图片尺寸
- 压缩图片减小文件大小
- 使用 CDN 加速图片访问（生产环境）

---

## 完成时间
2026 年 3 月 1 日

## 状态
✅ 已完成并测试通过
