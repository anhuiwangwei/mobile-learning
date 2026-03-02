<template>
  <div class="page-container">
    <a-card>
      <a-form layout="inline" class="search-form">
        <a-form-item label="工号">
          <a-input v-model:value="searchForm.teacherNo" placeholder="请输入工号" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>

      <div class="table-operations">
        <a-button type="primary" @click="handleAdd">添加教师</a-button>
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
          <template v-if="column.key === 'avatar'">
            <img v-if="record.avatar" :src="record.avatar" style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover;" />
            <span v-else>-</span>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除该教师吗？课程将保留"
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
      :title="isEdit ? '编辑教师' : '添加教师'"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form :model="form" :label-col="{ span: 6 }">
        <a-form-item label="姓名" required>
          <a-input v-model:value="form.realName" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item label="手机号" required>
          <a-input v-model:value="form.phone" placeholder="请输入手机号" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="工号" required>
          <a-input v-model:value="form.teacherNo" placeholder="请输入工号" />
        </a-form-item>
        <a-form-item label="头像">
          <ImageUpload v-model="form.avatar" :max-size="5" accept="image/*" />
          <div v-if="form.avatar" style="margin-top: 8px; font-size: 12px; color: #666">
            头像地址：{{ form.avatar }}
          </div>
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model:value="form.intro" :rows="3" placeholder="请输入教师简介" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { teacherApi } from '@/api/teacher'
import { formatDate } from '@/utils/format'
import ImageUpload from '@/components/Upload/ImageUpload.vue'

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '头像', key: 'avatar', width: 80 },
  { title: '工号', dataIndex: 'teacherNo', key: 'teacherNo' },
  { title: '姓名', dataIndex: 'realName', key: 'realName' },
  { title: '简介', dataIndex: 'intro', key: 'intro', ellipsis: true },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 150 }
]

const searchForm = reactive({ teacherNo: '' })
const loading = ref(false)
const dataSource = ref([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const modalVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  realName: '',
  phone: '',
  teacherNo: '',
  avatar: '',
  intro: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await teacherApi.list({
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    dataSource.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.teacherNo = ''; handleSearch() }
const handleTableChange = (pag) => { pagination.current = pag.current; loadData() }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, realName: '', phone: '', teacherNo: '', title: '', specialty: '', intro: '' })
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  Object.assign(form, record)
  modalVisible.value = true
}

const handleSubmit = async () => {
  if (!form.realName || !form.phone || !form.teacherNo) {
    message.error('请填写必填项')
    return
  }
  try {
    if (isEdit.value) {
      await teacherApi.update(form)
      message.success('更新成功')
    } else {
      await teacherApi.add(form)
      message.success('添加成功')
    }
    modalVisible.value = false
    loadData()
  } catch (e) { console.error(e) }
}

const handleCancel = () => { modalVisible.value = false }
const handleDelete = async (record) => {
  await teacherApi.delete(record.id)
  message.success('删除成功')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
.search-form, .table-operations { margin-bottom: 16px; }
</style>
