<template>
  <div class="page-container">
    <a-card>
      <a-form layout="inline" class="search-form">
        <a-form-item label="课程名称">
          <a-input v-model:value="searchForm.courseName" placeholder="请输入课程名称" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>

      <div class="table-operations">
        <a-button type="primary" @click="handleAdd">添加课程</a-button>
      </div>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'coverImage'">
            <img v-if="record.coverImage" :src="record.coverImage" style="width: 80px; height: 45px; object-fit: cover; border-radius: 4px;" />
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'difficulty'">
            <a-rate :value="record.difficulty" disabled />
          </template>
          <template v-if="column.key === 'status'">
            <a-switch :checked="record.status === 1" @change="(checked) => handleStatusChange(record, checked)" />
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a @click="handleChapters(record)">章节管理</a>
            <a-divider type="vertical" />
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该课程吗？" @confirm="handleDelete(record)">
              <a style="color: red">删除</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="modalVisible" :title="isEdit ? '编辑课程' : '添加课程'" @ok="handleSubmit" width="700px">
      <a-form :model="form" :label-col="{ span: 5 }" :rules="formRules">
        <a-form-item label="课程名称" name="courseName" required>
          <a-input v-model:value="form.courseName" placeholder="请输入课程名称" />
        </a-form-item>
        
        <a-form-item label="课程描述" name="courseDesc" required>
          <a-textarea v-model:value="form.courseDesc" :rows="3" placeholder="请输入课程描述" />
        </a-form-item>
        
        <!-- 封面图片上传 -->
        <a-form-item label="封面图片" name="coverImage" required>
          <ImageUpload v-model="form.coverImage" :max-size="10" accept="image/*" />
          <div v-if="form.coverImage" style="margin-top: 8px; font-size: 12px; color: #666">
            图片地址：{{ form.coverImage }}
          </div>
        </a-form-item>
        
        <a-form-item label="难度等级">
          <a-rate v-model:value="form.difficulty" />
        </a-form-item>
        
        <a-form-item label="状态">
          <a-switch v-model:checked="form.status" checked-children="上架" unchecked-children="下架" />
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
import { formatDate } from '@/utils/format'
import ImageUpload from '@/components/Upload/ImageUpload.vue'

const router = useRouter()

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '封面', key: 'coverImage', width: 100 },
  { title: '课程名称', dataIndex: 'courseName', key: 'courseName' },
  { title: '难度', key: 'difficulty', width: 150 },
  { title: '时长 (分钟)', dataIndex: 'duration', key: 'duration' },
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
const form = reactive({ id: null, courseName: '', courseDesc: '', coverImage: '', difficulty: 1, status: true })

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
  Object.assign(form, { id: null, courseName: '', courseDesc: '', coverImage: '', difficulty: 1, status: true })
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  Object.assign(form, { ...record, status: record.status === 1 })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    const data = { ...form, status: form.status ? 1 : 0 }
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

onMounted(() => { loadData() })
</script>

<style scoped>
.page-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.search-form, .table-operations { margin-bottom: 16px; }
</style>
