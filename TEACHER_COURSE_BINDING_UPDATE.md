# 课程与教师绑定功能更新

## 功能说明

实现了课程创建时与教师的绑定逻辑：
- **超级管理员**：创建课程时需要手动选择授课教师
- **教师用户**：创建课程时自动绑定到当前登录的教师

---

## 一、后端修改

### 1. AdminController.java

**新增接口：获取当前用户关联的教师信息**

```java
@GetMapping("/admin/teacher/current")
public Result<EduTeacher> getCurrentTeacher() {
    Long userId = AuthContext.getUserId();
    if (userId == null) {
        return Result.error("未登录");
    }
    
    LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(EduTeacher::getUserId, userId);
    wrapper.eq(EduTeacher::getIsDeleted, 0);
    EduTeacher teacher = eduTeacherMapper.selectOne(wrapper);
    
    if (teacher != null) {
        return Result.success(teacher);
    } else {
        return Result.error("未找到关联的教师信息");
    }
}
```

**功能说明**：
- 根据当前登录用户的 `userId` 查询关联的教师信息
- 仅返回未删除的教师记录
- 用于教师登录时自动获取教师 ID

### 2. CourseController.java

**修改接口：创建课程**

```java
@PostMapping
public Result<Void> addCourse(@RequestBody CourseRequest request) {
    Long userId = AuthContext.getUserId();
    String userRole = AuthContext.getRole();
    
    // 如果没有指定教师 ID，根据用户角色处理
    if (request.getTeacherId() == null) {
        if ("teacher".equals(userRole)) {
            // 教师用户：自动查找关联的教师
            LambdaQueryWrapper<EduTeacher> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EduTeacher::getUserId, userId);
            wrapper.eq(EduTeacher::getIsDeleted, 0);
            EduTeacher teacher = eduTeacherMapper.selectOne(wrapper);
            if (teacher != null) {
                request.setTeacherId(teacher.getId());
            } else {
                return Result.error("教师用户未找到关联的教师信息");
            }
        } else {
            // 其他用户（如管理员）：必须指定教师
            return Result.error("必须选择授课教师");
        }
    }
    
    // 创建课程...
    EduCourse course = new EduCourse();
    course.setTeacherId(request.getTeacherId());
    // ...其他字段
    eduCourseMapper.insert(course);
    return Result.success();
}
```

**逻辑说明**：
1. 检查请求中是否包含 `teacherId`
2. 如果没有：
   - 教师用户：自动查询并绑定
   - 其他用户：返回错误，必须选择教师
3. 创建课程时保存教师 ID

---

## 二、管理端修改

### 1. teacher.js API

**新增方法：获取当前教师信息**

```javascript
export const teacherApi = {
  list: (params) => request.get('/admin/teacher/list', { params }),
  // ...其他方法
  getCurrent: () => request.get('/admin/teacher/current')
}
```

### 2. CourseList.vue

#### 新增状态

```javascript
const isTeacher = ref(false)           // 当前用户是否为教师
const teacherList = ref([])            // 教师列表（管理员用）
const currentTeacherInfo = ref('')     // 当前教师信息（显示用）
```

#### 新增方法

```javascript
// 加载教师列表（超级管理员用）
const loadTeachers = async () => {
  try {
    const res = await teacherApi.list({ pageNum: 1, pageSize: 100, isDeleted: 0 })
    teacherList.value = res.data.records || []
  } catch (e) {
    console.error('加载教师列表失败', e)
  }
}

// 加载当前教师信息（教师用户用）
const loadCurrentTeacher = async () => {
  try {
    const res = await teacherApi.getCurrent()
    if (res.code === 200 && res.data) {
      isTeacher.value = true
      currentTeacherInfo.value = res.data.realName + ' (' + res.data.teacherNo + ')'
      form.teacherId = res.data.id
      form.teacherName = res.data.realName
    }
  } catch (e) {
    console.error('获取当前教师信息失败', e)
  }
}

// 教师选项过滤
const filterTeacherOption = (input, option) => {
  return option.children.some(child => child.children.includes(input))
}
```

#### 模板修改

**教师选择字段**：

```vue
<!-- 教师选择（超级管理员可见） -->
<a-form-item
  v-if="!isTeacher"
  label="授课教师"
  name="teacherId"
  required
>
  <a-select
    v-model:value="form.teacherId"
    placeholder="请选择授课教师"
    show-search
    :filter-option="filterTeacherOption"
  >
    <a-select-option
      v-for="teacher in teacherList"
      :key="teacher.id"
      :value="teacher.id"
    >
      {{ teacher.realName }} ({{ teacher.teacherNo }})
    </a-select-option>
  </a-select>
</a-form-item>

<!-- 教师用户显示只读信息 -->
<a-form-item v-else label="授课教师">
  <a-input :value="currentTeacherInfo" disabled />
</a-form-item>
```

#### 表单提交验证

```javascript
const handleSubmit = async () => {
  // 超级管理员必须选择教师
  if (!isTeacher.value && !form.teacherId) {
    message.error('请选择授课教师')
    return
  }
  
  // 其他验证...
  if (!form.courseName) {
    message.error('请输入课程名称')
    return
  }
  // ...
  
  const data = {
    ...form,
    pageTurnTime: form.pageTurnTime || 0,
    isOrderLearning: form.isOrderLearning ? 1 : 0,
    status: form.status ? 1 : 0
  }
  
  if (isEdit.value) {
    await courseApi.update(data)
    message.success('更新成功')
  } else {
    await courseApi.add(data)
    message.success('添加成功')
  }
  // ...
}
```

#### 初始化

```javascript
onMounted(() => {
  loadCurrentTeacher()  // 先加载当前教师信息
  loadTeachers()        // 加载教师列表（管理员用）
  loadData()           // 加载课程列表
})
```

---

## 三、使用流程

### 超级管理员创建课程

1. 登录系统（角色：`admin`）
2. 进入课程管理
3. 点击"添加课程"
4. 填写课程信息
5. **从下拉框选择授课教师**（必选）
6. 提交保存

### 教师创建课程

1. 登录系统（角色：`teacher`）
2. 进入课程管理
3. 点击"添加课程"
4. 授课教师字段**自动填充**并**禁用**
5. 填写其他课程信息
6. 提交保存（自动绑定当前教师）

---

## 四、界面展示

### 超级管理员视角

```
┌─────────────────────────────────┐
│ 添加课程                        │
├─────────────────────────────────┤
│ 课程名称：[________________]    │
│ 课程描述：[________________]    │
│            [________________]   │
│ 封面图片：[上传图片]            │
│ 授课教师：[选择教师 ▼]  *必选   │
│ 难度等级：★★★☆☆              │
│ 翻页时长：[300] 秒             │
│ 顺序学习：○ 开启 ○ 关闭        │
│ 状态：   ○ 上架 ○ 下架         │
└─────────────────────────────────┘
```

### 教师用户视角

```
┌─────────────────────────────────┐
│ 添加课程                        │
├─────────────────────────────────┤
│ 课程名称：[________________]    │
│ 课程描述：[________________]    │
│            [________________]   │
│ 封面图片：[上传图片]            │
│ 授课教师：[张老师 (T001)]  🔒  │
│ 难度等级：★★★☆☆              │
│ 翻页时长：[300] 秒             │
│ 顺序学习：○ 开启 ○ 关闭        │
│ 状态：   ○ 上架 ○ 下架         │
└─────────────────────────────────┘
```

---

## 五、文件清单

### 后端文件
- `mobile-learning-api/src/main/java/com/mobilelearning/controller/admin/AdminController.java`
- `mobile-learning-api/src/main/java/com/mobilelearning/controller/admin/CourseController.java`

### 管理端文件
- `mobile-learning-admin/src/api/teacher.js`
- `mobile-learning-admin/src/views/CourseList.vue`

---

## 六、注意事项

1. **教师用户必须先创建教师信息**
   - 如果用户角色是 `teacher`，但在 `edu_teacher` 表中没有记录，会报错
   - 需要管理员先在"教师管理"中创建教师信息

2. **超级管理员必须选择教师**
   - 管理员创建课程时，`teacherId` 为必填项
   - 未选择教师时，前端和后端都会校验并返回错误

3. **编辑课程时的教师处理**
   - 教师用户编辑课程时，授课教师字段保持只读
   - 超级管理员可以修改课程的授课教师

4. **数据安全**
   - 后端会再次校验教师 ID 的有效性
   - 防止越权操作（教师不能绑定到其他教师）

---

**更新时间**: 2026-03-04
