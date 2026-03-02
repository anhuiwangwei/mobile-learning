<template>
  <div class="page-container">
    <a-card>
      <a-form layout="inline" class="search-form">
        <a-form-item label="试卷名称">
          <a-input v-model:value="searchForm.paperName" placeholder="请输入试卷名称" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>

      <div class="table-operations">
        <a-button type="primary" @click="handleAdd">添加试卷</a-button>
      </div>

      <a-table :columns="columns" :data-source="dataSource" :loading="loading" :pagination="pagination" @change="handleTableChange" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-switch :checked="record.status === 1" @change="(checked) => handleStatusChange(record, checked)" />
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a @click="handleQuestions(record)">题目管理</a>
            <a-divider type="vertical" />
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该试卷吗？" @confirm="handleDelete(record)">
              <a style="color: red">删除</a>
            </a-popconfirm>
          </template>
        </template>
          <template v-if="column.key === 'action'">
            <a @click="handleQuestions(record)">题目管理</a>
            <a-divider type="vertical" />
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该试卷吗?" @confirm="handleDelete(record)">
              <a style="color: red">删除</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="modalVisible" :title="isEdit ? '编辑试卷' : '添加试卷'" @ok="handleSubmit">
      <a-form :model="form" :label-col="{ span: 6 }">
        <a-form-item label="试卷名称" required>
          <a-input v-model:value="form.paperName" placeholder="请输入试卷名称" />
        </a-form-item>
        <a-form-item label="总分">
          <a-input-number v-model:value="form.totalScore" :min="0" />
        </a-form-item>
        <a-form-item label="及格分数">
          <a-input-number v-model:value="form.passScore" :min="0" />
        </a-form-item>
        <a-form-item label="考试时长(分钟)">
          <a-input-number v-model:value="form.duration" :min="1" />
        </a-form-item>
        <a-form-item label="状态">
          <a-switch v-model:checked="form.status" checked-children="启用" unchecked-children="禁用" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { examApi } from '@/api/exam'
import { formatDate } from '@/utils/format'

const router = useRouter()
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '试卷名称', dataIndex: 'paperName', key: 'paperName' },
  { title: '总分', dataIndex: 'totalScore', key: 'totalScore' },
  { title: '及格分数', dataIndex: 'passScore', key: 'passScore' },
  { title: '时长 (分钟)', dataIndex: 'duration', key: 'duration' },
  { title: '题目数', dataIndex: 'questionCount', key: 'questionCount' },
  { title: '状态', key: 'status', width: 80 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 200 }
]

const searchForm = reactive({ paperName: '' })
const loading = ref(false)
const dataSource = ref([])
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const modalVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, paperName: '', totalScore: 100, passScore: 60, duration: 60, status: true })

const loadData = async () => {
  loading.value = true
  try {
    const res = await examApi.paperList({ pageNum: pagination.current, pageSize: pagination.pageSize, ...searchForm })
    dataSource.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.paperName = ''; handleSearch() }
const handleTableChange = (pag) => { pagination.current = pag.current; loadData() }

const handleAdd = () => { isEdit.value = false; Object.assign(form, { id: null, paperName: '', totalScore: 100, passScore: 60, duration: 60, status: true }); modalVisible.value = true }
const handleEdit = (record) => { isEdit.value = true; Object.assign(form, { ...record, status: record.status === 1 }); modalVisible.value = true }

const handleSubmit = async () => {
  if (!form.paperName) { message.error('请输入试卷名称'); return }
  try {
    const data = { ...form, status: form.status ? 1 : 0 }
    if (isEdit.value) { await examApi.updatePaper(data); message.success('更新成功') }
    else { await examApi.addPaper(data); message.success('添加成功') }
    modalVisible.value = false; loadData()
  } catch (e) { console.error(e) }
}

const handleStatusChange = async (record, checked) => {
  await examApi.updatePaper({ ...record, status: checked ? 1 : 0 })
  message.success(checked ? '已启用' : '已禁用')
  loadData()
}

const handleQuestions = (record) => { router.push({ name: 'ExamQuestions', params: { id: record.id } }) }
const handleDelete = async (record) => { await examApi.deletePaper(record.id); message.success('删除成功'); loadData() }

onMounted(() => { loadData() })
</script>

<style scoped>
.search-form, .table-operations { margin-bottom: 16px; }
</style>
