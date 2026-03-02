# 移动学习平台 - 运行指南

## 环境要求

### 必需软件
- **JDK**: 1.8+ (推荐 JDK 17)
- **Maven**: 3.6+
- **Node.js**: 16+ (推荐 v22)
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Android Studio**: Arctic Fox+ (移动端开发用)

---

## 快速开始

### 1. 数据库配置

```bash
# 登录 MySQL
mysql -u root -p

# 执行 SQL 脚本
source /Volumes/txz/project/mobile-learning/mobile-learning.sql
```

**修改数据库配置** (mobile-learning-api/src/main/resources/application.yml):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mobile_learning?...
    username: root
    password: your_password  # 修改为你的 MySQL 密码
```

---

### 2. 启动后端服务

```bash
cd mobile-learning-api

# 方式一：使用 Maven
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/mobile-learning-api-1.0.0.jar
```

**访问测试**: http://localhost:8080

**管理端登录**: 
- 账号：admin
- 密码：admin123

---

### 3. 启动管理端

```bash
cd mobile-learning-admin

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

**访问地址**: http://localhost:8081

**管理端功能**:
- ✅ 仪表盘统计
- ✅ 用户管理（禁用/启用/删除/编辑）
- ✅ 教师管理（添加/编辑/删除）
- ✅ 课程管理（CRUD）
- ✅ 章节管理（CRUD + 绑定考试）
- ✅ 小节管理（视频/PDF 配置）
- ✅ 试卷管理（CRUD）
- ✅ 题目管理（单选/判断）

---

### 4. 移动端（Android）

```bash
cd MobileLearning

# 方式一：使用 Gradle
./gradlew assembleDebug

# 方式二：在 Android Studio 中打开项目运行
```

**APK 输出位置**: `MobileLearning/app/build/outputs/apk/debug/app-debug.apk`

**测试账号**:
- 手机号：13800138000
- 密码：123456

---

## 端口配置

| 服务 | 默认端口 | 配置文件 |
|------|---------|---------|
| 后端 API | 8080 | mobile-learning-api/src/main/resources/application.yml |
| 管理端 | 8081 | mobile-learning-admin/vite.config.js |
| MySQL | 3306 | application.yml |
| Redis | 6379 | application.yml |

---

## 常见问题

### 1. 后端启动失败

**错误**: `Access denied for user 'root'@'localhost'`

**解决**: 修改 application.yml 中的数据库密码

**错误**: `Redis connection failed`

**解决**: 
```bash
# 启动 Redis
redis-server
```

### 2. 管理端无法访问后端

**检查**: vite.config.js 中的代理配置
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

### 3. 移动端网络请求失败

**解决**: 
1. 确保后端服务已启动
2. 修改 MainApplication.java 中的 BASE_URL
3. Android 模拟器使用 `10.0.2.2` 访问本机

---

## 功能测试清单

### 后端 API 测试

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 登录 | POST | /admin/login | ✅ |
| 用户列表 | GET | /admin/user/list | ✅ |
| 教师列表 | GET | /admin/teacher/list | ✅ |
| 课程列表 | GET | /admin/course/list | ✅ |
| 试卷列表 | GET | /admin/exam/paper/list | ✅ |
| 统计 | GET | /admin/stats/dashboard | ✅ |

### 管理端功能测试

| 功能模块 | 状态 |
|---------|------|
| 登录认证 | ✅ |
| 仪表盘统计 | ✅ |
| 用户管理 | ✅ |
| 教师管理 | ✅ |
| 课程管理 | ✅ |
| 章节管理 | ✅ |
| 小节管理 | ✅ |
| 试卷管理 | ✅ |
| 题目管理 | ✅ |

### 移动端功能测试

| 功能模块 | 状态 |
|---------|------|
| 用户登录 | ✅ |
| 用户注册 | ✅ |
| 课程列表 | 🔄 |
| 视频学习 | 🔄 |
| PDF 学习 | 🔄 |
| 在线考试 | 🔄 |

*🔄 = 基础框架已搭建，需进一步完善*

---

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis Plus 3.5.3
- MySQL 8.0
- Redis 6.x
- JWT 0.11.5

### 管理端
- Vue 3.3.4
- Ant Design Vue 4.1.0
- Vite 4.4.9
- Axios 1.4.0

### 移动端
- Android SDK API 24+
- Java 1.8
- Retrofit 2.9.0
- OkHttp 4.11.0

---

## 项目结构

```
mobile-learning/
├── mobile-learning-api/          # 后端
│   ├── src/main/java/
│   │   └── com/mobilelearning/
│   │       ├── controller/       # 控制器 (7 个)
│   │       ├── service/          # 服务层 (9 个接口 + 实现)
│   │       ├── mapper/           # 数据访问 (11 个)
│   │       ├── entity/           # 实体类 (11 个)
│   │       ├── dto/              # 数据传输对象 (11 个)
│   │       ├── config/           # 配置类 (5 个)
│   │       └── common/           # 公共组件 (4 个)
│   └── src/main/resources/
│       ├── application.yml
│       └── mapper/
│
├── mobile-learning-admin/        # 管理端
│   ├── src/
│   │   ├── api/                  # API 接口 (8 个)
│   │   ├── views/                # 页面组件 (9 个)
│   │   ├── router/               # 路由配置
│   │   ├── store/                # 状态管理
│   │   └── components/           # 公共组件
│   └── package.json
│
├── MobileLearning/               # 移动端
│   └── app/src/main/
│       ├── java/com/mobilelearning/
│       │   ├── api/              # API 接口 (4 个)
│       │   ├── bean/             # 实体类 (7 个)
│       │   ├── ui/activity/      # Activity(7 个)
│       │   └── utils/            # 工具类 (4 个)
│       └── res/layout/           # 布局文件 (8 个)
│
└── mobile-learning.sql           # 数据库脚本
```

---

## 下一步完善

### 管理端
- [ ] 实现课程封面图片上传
- [ ] 实现教师选择器组件
- [ ] 实现分类选择器组件
- [ ] 完善个人资料修改
- [ ] 添加批量导入导出功能

### 移动端
- [ ] 完善课程列表页
- [ ] 实现视频播放器
- [ ] 实现 PDF 阅读器
- [ ] 完善考试功能
- [ ] 添加个人中心页
- [ ] 实现学习进度跟踪

### 后端
- [ ] 文件上传功能完善
- [ ] 添加操作日志
- [ ] 完善缓存机制
- [ ] 添加单元测试
- [ ] 集成 Swagger 文档

---

## 联系与支持

如有问题，请查看 AGENTS.md 文件获取开发规范。
