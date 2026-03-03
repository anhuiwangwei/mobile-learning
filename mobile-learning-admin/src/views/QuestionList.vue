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
            <a-tag :color="record.questionType === 'multiple' ? 'purple' : (record.questionType === 'single' ? 'blue' : 'orange')">
              {{ record.questionType === 'multiple' ? '多选题' : (record.questionType === 'single' ? '单选题' : '判断题') }}
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
      </a-table>
    </a-card>

    <a-modal v-model:open="modalVisible" :title="isEdit ? '编辑题目' : '添加题目'" @ok="handleSubmit" width="700px">
      <a-form :model="form" :label-col="{ span: 4 }">
        <a-form-item label="题型" required>
          <a-radio-group v-model:value="form.questionType" @change="onQuestionTypeChange">
            <a-radio value="single">单选题</a-radio>
            <a-radio value="multiple">多选题</a-radio>
            <a-radio value="judge">判断题</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="是否为多选" required v-if="form.questionType === 'single'">
          <a-switch v-model:checked="form.isMultiple" checked-children="多选" un-checked-children="单选" />
        </a-form-item>
        <a-form-item label="题目内容" required>
          <a-textarea v-model:value="form.questionContent" :rows="2" placeholder="请输入题目内容" />
        </a-form-item>
        <a-form-item label="选项" required v-if="form.questionType !== 'judge'">
          <div v-for="(option, index) in form.optionList" :key="index" style="display: flex; align-items: center; margin-bottom: 8px;">
            <span style="margin-right: 8px;">{{ String.fromCharCode(65 + index) }}.</span>
            <a-input v-model:value="option.text" placeholder="请输入选项内容" style="flex: 1; margin-right: 8px;" />
            <a-button type="text" danger @click="removeOption(index)" :disabled="form.optionList.length <= 2">
              <template #icon><DeleteOutlined /></template>
            </a-button>
          </div>
          <a-button type="dashed" block @click="addOption" style="margin-top: 8px;">
            <template #icon><PlusOutlined /></template>
            添加选项
          </a-button>
        </a-form-item>
        <a-form-item label="正确答案" required>
          <template v-if="form.questionType === 'judge'">
            <a-radio-group v-model:value="form.answer">
              <a-radio value="true">正确</a-radio>
              <a-radio value="false">错误</a-radio>
            </a-radio-group>
          </template>
          <template v-else-if="form.isMultiple">
            <a-select v-model:value="form.answer" mode="multiple" placeholder="选择正确答案" style="width: 100%;">
              <a-select-option v-for="(option, index) in form.optionList" :key="index" :value="String.fromCharCode(65 + index)">
                {{ String.fromCharCode(65 + index) }}. {{ option.text }}
              </a-select-option>
            </a-select>
          </template>
          <template v-else>
            <a-select v-model:value="form.answer" placeholder="选择正确答案">
              <a-select-option v-for="(option, index) in form.optionList" :key="index" :value="String.fromCharCode(65 + index)">
                {{ String.fromCharCode(65 + index) }}. {{ option.text }}
              </a-select-option>
            </a-select>
          </template>
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
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const paperId = route.params.id
const paperName = ref('')

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '排序', dataIndex: 'questionOrder', key: 'questionOrder', width: 60 },
  { title: '题型', key: 'questionType', width: 100 },
  { title: '题目内容', key: 'questionContent' },
  { title: '分值', dataIndex: 'score', key: 'score', width: 60 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 150 }
]

const loading = ref(false)
const dataSource = ref([])
const modalVisible = ref(false)
const isEdit = ref(false)

const createForm = () => ({
  id: null,
  paperId,
  questionType: 'single',
  questionContent: '',
  options: '',
  answer: '',
  analysis: '',
  score: 5,
  difficulty: 1,
  questionOrder: 1,
  isMultiple: false,
  optionList: [{ text: '' }, { text: '' }, { text: '' }, { text: '' }]
})

const form = reactive(createForm())

const onQuestionTypeChange = () => {
  if (form.questionType === 'judge') {
    form.isMultiple = false
    form.answer = ''
  } else if (form.questionType === 'multiple') {
    form.isMultiple = true
  } else {
    form.isMultiple = false
  }
}

const addOption = () => {
  form.optionList.push({ text: '' })
}

const removeOption = (index) => {
  form.optionList.splice(index, 1)
}

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

const parseOptions = (optionsStr) => {
  if (!optionsStr) return [{ text: '' }, { text: '' }, { text: '' }, { text: '' }]
  const opts = optionsStr.split('|').map(opt => ({ text: opt.replace(/^[A-Z]\.\s*/, '') }))
  return opts.length >= 2 ? opts : [{ text: '' }, { text: '' }]
}

const buildOptions = (optionList) => {
  return optionList.map((opt, idx) => `${String.fromCharCode(65 + idx)}. ${opt.text}`).join('|')
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, createForm())
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  const parsedOptions = parseOptions(record.options)
  Object.assign(form, {
    ...record,
    isMultiple: record.isMultiple === 1,
    optionList: parsedOptions
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  if (!form.questionContent) { message.error('请填写题目内容'); return }
  if (form.questionType !== 'judge') {
    const validOptions = form.optionList.filter(o => o.text.trim())
    if (validOptions.length < 2) { message.error('请至少填写2个选项'); return }
    form.options = buildOptions(form.optionList)
  }
  if (!form.answer) { message.error('请选择正确答案'); return }
  if (form.questionType === 'single' || form.questionType === 'multiple') {
    form.questionType = form.questionType === 'multiple' ? 'multiple' : (form.isMultiple ? 'multiple' : 'single')
  }
  try {
    const submitData = {
      ...form,
      isMultiple: form.isMultiple ? 1 : 0,
      answer: Array.isArray(form.answer) ? form.answer.join(',') : form.answer
    }
    if (isEdit.value) { await examApi.updateQuestion(submitData); message.success('更新成功') }
    else { await examApi.addQuestion(submitData); message.success('添加成功') }
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
