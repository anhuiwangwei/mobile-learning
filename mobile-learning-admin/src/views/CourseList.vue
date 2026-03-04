<template>
  <div class="page-container">
    <a-card>
      <a-form
        layout="inline"
        class="search-form"
      >
        <a-form-item label="课程名称">
          <a-input
            v-model:value="searchForm.courseName"
            placeholder="请输入课程名称"
          />
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            @click="handleSearch"
          >
            查询
          </a-button>
          <a-button
            style="margin-left: 8px"
            @click="handleReset"
          >
            重置
          </a-button>
        </a-form-item>
      </a-form>

      <div class="table-operations">
        <a-button
          type="primary"
          @click="handleAdd"
        >
          添加课程
        </a-button>
      </div>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'coverImage'">
            <img
              v-if="record.coverImage"
              :src="record.coverImage"
              style="width: 80px; height: 45px; object-fit: cover; border-radius: 4px;"
            >
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'difficulty'">
            <a-rate
              :value="record.difficulty"
              disabled
            />
          </template>
          <template v-if="column.key === 'isOrderLearning'">
            <a-tag :color="record.isOrderLearning === 1 ? 'green' : 'default'">
              {{ record.isOrderLearning === 1 ? '是' : '否' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-switch
              :checked="record.status === 1"
              @change="(checked) => handleStatusChange(record, checked)"
            />
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a @click="handleChapters(record)">章节管理</a>
            <a-divider type="vertical" />
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除该课程吗？"
              @confirm="handleDelete(record)"
            >
              <a style="color: red">删除</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑课程' : '添加课程'"
      width="700px"
      @ok="handleSubmit"
    >
      <a-form
        :model="form"
        :label-col="{ span: 5 }"
        :rules="formRules"
      >
        <a-form-item
          label="课程名称"
          name="courseName"
          required
        >
          <a-input
            v-model:value="form.courseName"
            placeholder="请输入课程名称"
          />
        </a-form-item>

        <a-form-item
          label="课程描述"
          name="courseDesc"
          required
        >
          <a-textarea
            v-model:value="form.courseDesc"
            :rows="3"
            placeholder="请输入课程描述"
          />
        </a-form-item>

        <!-- 封面图片上传 -->
        <a-form-item
          label="封面图片"
          name="coverImage"
          required
        >
          <ImageUpload
            v-model="form.coverImage"
            :max-size="10"
            accept="image/*"
          />
          <div
            v-if="form.coverImage"
            style="margin-top: 8px; font-size: 12px; color: #666"
          >
            图片地址：{{ form.coverImage }}
          </div>
        </a-form-item>

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

        <a-form-item
          v-else
          label="授课教师"
        >
          <a-input
            :value="currentTeacherInfo"
            disabled
          />
        </a-form-item>

        <a-form-item label="难度等级">
          <a-rate v-model:value="form.difficulty" />
        </a-form-item>

        <a-form-item
          label="翻页时长"
          name="pageTurnTime"
        >
          <a-input-number
            v-model:value="form.pageTurnTime"
            :min="0"
            :step="10"
            placeholder="秒，0 表示不限制"
          />
          <div style="margin-top: 8px; font-size: 12px; color: #999">
            设置学员学习每个小节的最小停留时间，0 表示不限制
          </div>
        </a-form-item>

        <a-form-item label="顺序学习">
          <a-switch
            v-model:checked="form.isOrderLearning"
            checked-children="开启"
            unchecked-children="关闭"
          />
          <div style="margin-top: 8px; font-size: 12px; color: #999">
            开启后学员必须按章节顺序学习
          </div>
        </a-form-item>

        <a-form-item label="状态">
          <a-switch
            v-model:checked="form.status"
            checked-children="上架"
            unchecked-children="下架"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { courseApi } from '@/api/course'
import { teacherApi } from '@/api/teacher'
import { formatDate } from '@/utils/format'
import ImageUpload from '@/components/Upload/ImageUpload.vue'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const isTeacher = ref(false)
const teacherList = ref([])
const currentTeacherInfo = ref('')

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '封面', key: 'coverImage', width: 100 },
  { title: '课程名称', dataIndex: 'courseName', key: 'courseName' },
  { title: '教师', dataIndex: 'teacherName', key: 'teacherName' },
  { title: '难度', key: 'difficulty', width: 150 },
  { title: '时长 (分钟)', dataIndex: 'duration', key: 'duration' },
  { title: '翻页时长 (秒)', dataIndex: 'pageTurnTime', key: 'pageTurnTime' },
  { title: '顺序学习', key: 'isOrderLearning', width: 100 },
  { title: '浏览量', dataIndex: 'viewCount', key: 'viewCount' },
  { title: '状态', key: 'status', width: 80 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 220 }
]

const searchForm = reactive({ courseName: '' })
const loading = ref(false)
const dataSource = ref([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const modalVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  courseName: '',
  courseDesc: '',
  coverImage: '',
  categoryId: null,
  teacherId: null,
  teacherName: '',
  difficulty: 1,
  pageTurnTime: 0,
  isOrderLearning: 0,
  status: true
})

// 表单验证规则
const formRules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  courseDesc: [
    { required: true, message: '请输入课程描述', trigger: 'blur' }
  ],
  coverImage: [
    { required: true, message: '请上传封面图片', trigger: 'change' }
  ]
}

const filterTeacherOption = (input, option) => {
  return option.children.some(child => child.children.includes(input))
}

const loadTeachers = async () => {
  try {
    const res = await teacherApi.list({ pageNum: 1, pageSize: 100, isDeleted: 0 })
    teacherList.value = res.data.records || []
  } catch (e) {
    console.error('加载教师列表失败', e)
  }
}

const loadCurrentTeacher = async () => {
  const role = userStore.userInfo?.role
  if (role !== 'teacher') {
    return
  }

  try {
    const res = await teacherApi.getCurrent()
    if (res.data) {
      isTeacher.value = true
      currentTeacherInfo.value = res.data.realName + ' (' + res.data.teacherNo + ')'
      form.teacherId = res.data.id
      form.teacherName = res.data.realName
    }
  } catch (e) {
    isTeacher.value = true
    message.warning('您尚未绑定教师信息，请联系管理员创建')
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await courseApi.list({ pageNum: pagination.current, pageSize: pagination.pageSize, ...searchForm })
    dataSource.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.courseName = ''; handleSearch() }
const handleTableChange = (pag) => { pagination.current = pag.current; loadData() }

const handleAdd = () => {
  isEdit.value = false
  if (isTeacher.value) {
    Object.assign(form, {
      id: null,
      courseName: '',
      courseDesc: '',
      coverImage: '',
      categoryId: null,
      teacherId: form.teacherId,
      teacherName: form.teacherName,
      difficulty: 1,
      pageTurnTime: 0,
      isOrderLearning: 0,
      status: true
    })
  } else {
    Object.assign(form, {
      id: null,
      courseName: '',
      courseDesc: '',
      coverImage: '',
      categoryId: null,
      teacherId: null,
      teacherName: '',
      difficulty: 1,
      pageTurnTime: 0,
      isOrderLearning: 0,
      status: true
    })
  }
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  Object.assign(form, {
    ...record,
    pageTurnTime: record.pageTurnTime || 0,
    isOrderLearning: record.isOrderLearning || 0,
    teacherId: record.teacherId,
    teacherName: record.teacherName,
    status: record.status === 1
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  if (isTeacher.value && !form.teacherId) {
    message.error('您尚未绑定教师信息，请联系管理员创建')
    return
  }

  if (!isTeacher.value && !form.teacherId) {
    message.error('请选择授课教师')
    return
  }

  if (!form.courseName) {
    message.error('请输入课程名称')
    return
  }

  if (!form.courseDesc) {
    message.error('请输入课程描述')
    return
  }

  if (!form.coverImage) {
    message.error('请上传封面图片')
    return
  }

  try {
    const selectedTeacher = teacherList.value.find(t => t.id === form.teacherId)
    const data = {
      ...form,
      teacherName: selectedTeacher ? selectedTeacher.realName : form.teacherName,
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
    modalVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleStatusChange = async (record, checked) => {
  await courseApi.update({ ...record, status: checked ? 1 : 0 })
  message.success(checked ? '已上架' : '已下架')
  loadData()
}

const handleChapters = (record) => { router.push({ name: 'CourseChapters', params: { id: record.id } }) }

const handleDelete = async (record) => {
  await courseApi.delete(record.id)
  message.success('删除成功')
  loadData()
}

onMounted(() => {
  loadCurrentTeacher()
  loadTeachers()
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.search-form, .table-operations { margin-bottom: 16px; }
</style>
