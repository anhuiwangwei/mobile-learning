# AGENTS.md - 移动学习平台开发规范

## 项目概述

这是一个基于 Android 平台的移动学习平台，包含三个子项目：
- **后端 API**: Spring Boot + MySQL + Redis
- **管理端**: Vue.js + Ant Design Vue
- **移动端**: Android (Java)

---

## 1. Build/Lint/Test Commands

### 后端 (mobile-learning-api)

```bash
# 进入后端目录
cd mobile-learning-api

# 编译打包
mvn clean package -DskipTests

# 运行项目
mvn spring-boot:run

# 运行测试
mvn test

# 运行单个测试类
mvn test -Dtest=UserServiceTest

# 运行单个测试方法
mvn test -Dtest=UserServiceTest#testLogin

# Maven 清理
mvn clean

# 代码格式化 (需要在 pom.xml 中配置 spotless)
mvn spotless:format
```

### 管理端 (mobile-learning-admin)

```bash
# 进入管理端目录
cd mobile-learning-admin

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 构建生产版本
npm run build

# 代码检查
npm run lint

# 代码检查并自动修复
npm run lint -- --fix

# 运行单元测试
npm run test
```

### 移动端 (MobileLearning)

```bash
# 进入移动端目录
cd MobileLearning

# 使用 Gradle 编译
./gradlew assembleDebug

# 使用 Gradle 运行测试
./gradlew test

# 运行单个测试
./gradlew test --tests="com.mobilelearning.api.UserApiTest"

# 清理构建
./gradlew clean

# 查看依赖树
./gradlew dependencies
```

---

## 2. Code Style Guidelines

### 2.1 Java 后端规范

#### 命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 类名 | UpperCamelCase | `UserService`, `CourseController` |
| 方法名 | lowerCamelCase | `getUserById`, `saveCourse` |
| 变量名 | lowerCamelCase | `userName`, `courseList` |
| 常量 | UPPER_SNAKE_CASE | `MAX_COUNT`, `DEFAULT_STATUS` |
| 包名 | lowercase | `com.mobilelearning.controller` |

#### Import 规范

```java
// 顺序：静态导入 → java → javax → 项目内部包
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.mobilelearning.entity.User;
import com.mobilelearning.service.UserService;
```

#### 代码格式化

```java
// 方法参数超过3个时换行
public Result<User> updateUser(
    Long id,
    String nickname,
    String email,
    String phone
) {
    // code
}

// 链式调用适当换行
User user = userService.getById(id)
    .setNickname(nickname)
    .setEmail(email);
```

#### 类型使用

- 使用 `Long` 而非 `long` 作为 ID 类型
- 使用 `Integer` 而非 `int` 作为可空数字类型
- 使用 `List<T>` 而非 `ArrayList<T>` 作为返回类型
- 使用接口类型声明变量：`List<User> users = new ArrayList<>()`

#### 异常处理

```java
// 使用全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}

// 不捕获异常，让其向上传播
// 仅在必要时捕获特定异常
try {
    userService.deleteById(id);
} catch (Exception e) {
    log.error("删除用户失败: {}", id, e);
    return Result.error("删除失败");
}
```

#### Controller 规范

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PostMapping
    public Result<Void> createUser(@RequestBody @Valid UserDTO userDTO) {
        userService.save(userDTO);
        return Result.success();
    }
}
```

---

### 2.2 Vue.js 管理端规范

#### 组件命名

```vue
<!-- 使用 PascalCase 命名组件 -->
<script setup>
import UserList from './UserList.vue'
import CourseEdit from './CourseEdit.vue'
</script>

<!-- 模板中使用 kebab-case -->
<UserList />
<CourseEdit />
```

#### Import 规范

```javascript
// 顺序：Vue 核心 → 第三方组件 → 项目组件 → 工具函数
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import UserModal from './UserModal.vue'
import { formatDate, debounce } from '@/utils'
```

#### 组件结构

```vue
<template>
  <div class="user-list">
    <!-- 搜索区域 -->
    <a-card>
      <a-form layout="inline">
        <a-form-item label="用户名">
          <a-input v-model:value="searchForm.username" />
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 表格区域 -->
    <a-table :columns="columns" :data-source="dataSource">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleDelete(record)">删除</a>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { userApi } from '@/api'

// 定义响应式数据
const searchForm = reactive({ username: '' })
const dataSource = ref([])

// 定义列配置
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '操作', key: 'action' }
]

// 定义方法
const handleEdit = (record) => {
  // 编辑逻辑
}
</script>

<style scoped>
.user-list {
  padding: 16px;
}
</style>
```

#### API 调用规范

```javascript
// api/user.js
import request from '@/api'

export const userApi = {
  list: (params) => request.get('/admin/user/list', { params }),
  getById: (id) => request.get(`/admin/user/${id}`),
  create: (data) => request.post('/admin/user', data),
  update: (data) => request.put('/admin/user', data),
  delete: (id) => request.delete(`/admin/user/${id}`)
}
```

---

### 2.3 Android 移动端规范

#### 包名与类名

```java
// 包名使用小写
package com.mobilelearning.ui.activity
package com.mobilelearning.api

// 类名使用 PascalCase
public class MainActivity
public class UserApi
public class CourseAdapter
```

#### 变量命名

```java
// 普通变量：lowerCamelCase
private String userName;
private int userAge;
private List<Course> courseList;

// 常量：UPPER_SNAKE_CASE
public static final int REQUEST_CODE_LOGIN = 1001;
private static final String TAG = "MainActivity";

// 控件变量：m + UpperCamelCase (Hungarian notation 可选)
private TextView mTitleTextView;
private Button mSubmitButton;
```

#### 资源文件命名

```
# 布局文件：activity_/fragment_ + 功能名 + .xml
activity_main.xml
fragment_course_list.xml
item_course.xml

# 控件ID：功能_类型
@+id/tv_title
@+id/btn_submit
@+id/et_username

# 字符串资源：模块_类型_描述
<string name="login_title">登录</string>
<string name="course_name">课程名称</string>
```

#### 网络请求规范

```java
// 使用 Retrofit + OkHttp
public interface UserApi {
    @POST("/api/user/login")
    Call<ResponseBody<User>> login(@Body LoginRequest request);
}

// 调用时处理回调
userApi.login(request, new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        // 处理成功
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        // 处理失败
    }
});
```

---

## 3. 数据库设计规范

### 表命名规范

- 使用下划线命名法
- 表名前缀：
  - `sys_`: 系统相关表
  - `edu_`: 教育相关表
  - `exam_`: 考试相关表

### 字段规范

```sql
-- 主键
id BIGINT PRIMARY KEY AUTO_INCREMENT

-- 时间字段
create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

-- 状态字段（通用）
status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用, 1正常'

-- 删除标记
is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0否, 1是'

-- 外键命名：表名_id
teacher_id BIGINT
course_id BIGINT
chapter_id BIGINT
```

---

## 4. Git 提交规范

### 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档更新 |
| style | 代码格式 |
| refactor | 重构 |
| test | 测试相关 |
| chore | 构建/辅助工具 |

### 示例

```
feat(user): 添加用户登录功能

- 实现手机号+密码登录
- 添加 token 缓存
- 添加登录状态检查

Closes #123
```

---

## 5. 错误处理规范

### 后端错误响应

```java
// 成功响应
Result.success(data)
Result.success()

// 错误响应
Result.error(400, "参数错误")
Result.error("操作失败")
```

### 前端错误处理

```javascript
// 统一处理 API 错误
request.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      message.error(error.response.data.message || '请求失败')
    } else {
      message.error('网络错误')
    }
    return Promise.reject(error)
  }
)
```

---

## 6. 安全规范

### 敏感信息

- 禁止在代码中硬编码密钥、密码
- 使用配置文件或环境变量
- 密码必须加密存储（MD5 + Salt 或 BCrypt）

### 接口安全

- 敏感接口需要登录认证
- 使用 JWT Token 认证
- 接口参数进行校验

---

## 7. 项目特定规则

### 教师管理

- 教师删除采用假删除 (is_deleted=1)
- 重新添加时通过手机号+姓名匹配恢复
- 教师课程保留

### 章节管理

- 章节必须包含视频、PDF 或考试之一
- 禁止空章节

### 学习进度

- 视频：支持快进控制 (is_allow_seek)
- 视频：支持按步骤学习 (is_step_learning)
- PDF：阅读时长需达到最低要求 (pdf_read_time)

### 考试

- 支持单选题和判断题
- 考试完成后显示答案和解析

---

## 8. 目录结构总览

```
mobile-learning/
├── mobile-learning-api/          # Spring Boot 后端
│   ├── src/main/java/com/mobilelearning/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── config/
│   └── src/main/resources/
│       ├── mapper/
│       └── application.yml
│
├── mobile-learning-admin/        # Vue.js 管理端
│   ├── src/
│   │   ├── api/
│   │   ├── views/
│   │   ├── components/
│   │   └── router/
│   └── package.json
│
├── MobileLearning/               # Android 移动端
│   └── app/src/main/
│       ├── java/com/mobilelearning/
│       │   ├── api/
│       │   ├── ui/
│       │   ├── bean/
│       │   └── utils/
│       └── res/
│
└── mobile-learning.sql           # 数据库脚本
```
