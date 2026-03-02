<template>
  <div class="page-container">
    <a-card>
      <a-page-header title="题目管理" :sub-title="`试卷: ${paperName}`" @back="() => router.push('/exams')">
        <template #extra>
          <a-button type="primary" @click="handleAdd">添加题目</a-button>
        </template>
      </a-page-header>

      <a-table :columns="columns" :data-source="dataSource" :loading="loading" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'questionType'">
            <a-tag :color="record.questionType === 'single' ? 'blue' : 'orange'">
              {{ record.questionType === 'single' ? '单选题' : '判断题' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'questionContent'">
            <span style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
              {{ record.questionContent }}
            </span>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该题目吗？" @confirm="handleDelete(record.id)">
              <a style="color: red">删除</a>
            </a-popconfirm>
          </template>
        </template>
          <template v-if="column.key === 'questionContent'">
            <span style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
              {{ record.questionContent }}
            </span>
          </template>
          <template v-if="column.key === 'action'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除该题目吗?" @confirm="handleDelete(record.id)">
              <a style="color: red">删除</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="modalVisible" :title="isEdit ? '编辑题目' : '添加题目'" @ok="handleSubmit" width="700px">
      <a-form :model="form" :label-col="{ span: 4 }">
        <a-form-item label="题型" required>
          <a-radio-group v-model:value="form.questionType">
            <a-radio value="single">单选题</a-radio>
            <a-radio value="judge">判断题</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="题目内容" required>
          <a-textarea v-model:value="form.questionContent" :rows="2" placeholder="请输入题目内容" />
        </a-form-item>
        <a-form-item label="选项" required v-if="form.questionType === 'single'">
          <a-input v-model:value="form.options" placeholder='格式: ["A. 选项1", "B. 选项2", "C. 选项3", "D. 选项4"]' />
        </a-form-item>
        <a-form-item label="正确答案" required>
          <a-input v-model:value="form.answer" :placeholder="form.questionType === 'single' ? '如: A' : '如: true 或 false'" />
        </a-form-item>
        <a-form-item label="答案解析">
          <a-textarea v-model:value="form.analysis" :rows="2" placeholder="请输入答案解析" />
        </a-form-item>
        <a-form-item label="分值">
          <a-input-number v-model:value="form.score" :min="1" />
        </a-form-item>
        <a-form-item label="难度">
          <a-rate v-model:value="form.difficulty" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.questionOrder" :min="1" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { examApi } from '@/api/exam'
import { formatDate } from '@/utils/format'

const router = useRouter()
const route = useRoute()
const paperId = route.params.id
const paperName = ref('')

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '排序', dataIndex: 'questionOrder', key: 'questionOrder', width: 60 },
  { title: '题型', key: 'questionType', width: 80 },
  { title: '题目内容', key: 'questionContent' },
  { title: '分值', dataIndex: 'score', key: 'score', width: 60 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 150 }
]

const loading = ref(false)
const dataSource = ref([])
const modalVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, paperId, questionType: 'single', questionContent: '', options: '', answer: '', analysis: '', score: 5, difficulty: 1, questionOrder: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await examApi.getQuestions(paperId)
    dataSource.value = res.data
    if (dataSource.value.length > 0) {
      const paperRes = await examApi.getPaper(paperId)
      paperName.value = paperRes.data.paperName
    }
  } finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, paperId, questionType: 'single', questionContent: '', options: '', answer: '', analysis: '', score: 5, difficulty: 1, questionOrder: dataSource.value.length + 1 })
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  Object.assign(form, record)
  modalVisible.value = true
}

const handleSubmit = async () => {
  if (!form.questionContent || !form.answer) { message.error('请填写必填项'); return }
  try {
    if (isEdit.value) { await examApi.updateQuestion(form); message.success('更新成功') }
    else { await examApi.addQuestion(form); message.success('添加成功') }
    modalVisible.value = false; loadData()
  } catch (e) { console.error(e) }
}

const handleDelete = async (id) => {
  await examApi.deleteQuestion(id)
  message.success('删除成功')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
</style>
