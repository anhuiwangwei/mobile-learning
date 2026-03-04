<template>
  <div class="page-container">
    <a-card>
      <a-form
        layout="inline"
        class="search-form"
      >
        <a-form-item label="用户名">
          <a-input
            v-model:value="searchForm.username"
            placeholder="请输入用户名"
          />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input
            v-model:value="searchForm.phone"
            placeholder="请输入手机号"
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

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'green' : 'red'">
              {{ record.status === 1 ? '正常' : '禁用' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              v-if="record.status === 1"
              title="确定禁用该用户吗?"
              @confirm="handleDisable(record)"
            >
              <a style="color: orange">禁用</a>
            </a-popconfirm>
            <a-popconfirm
              v-else
              title="确定启用该用户吗?"
              @confirm="handleEnable(record)"
            >
              <a style="color: green">启用</a>
            </a-popconfirm>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除该用户吗？删除后不可恢复！"
              @confirm="handleDelete(record)"
            >
              <a style="color: red">删除</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="editModalVisible"
      title="编辑用户"
      @ok="handleEditSubmit"
    >
      <a-form
        :model="editForm"
        :label-col="{ span: 6 }"
      >
        <a-form-item label="用户名">
          <a-input
            v-model:value="editForm.username"
            disabled
          />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="editForm.realName" />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input v-model:value="editForm.phone" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="editForm.email" />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="editForm.nickname" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { userApi } from '@/api/user'
import { formatDate } from '@/utils/format'

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '姓名', dataIndex: 'realName', key: 'realName' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '状态', key: 'status', width: 100 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 200 }
]

const searchForm = reactive({
  username: '',
  phone: ''
})

const loading = ref(false)
const dataSource = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const editModalVisible = ref(false)
const editForm = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  nickname: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await userApi.list({
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

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.phone = ''
  handleSearch()
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

const handleEdit = (record) => {
  Object.assign(editForm, {
    id: record.id,
    username: record.username,
    realName: record.realName,
    phone: record.phone,
    email: record.email,
    nickname: record.nickname
  })
  editModalVisible.value = true
}

const handleEditSubmit = async () => {
  try {
    message.success('更新成功')
    editModalVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleDisable = async (record) => {
  await userApi.updateStatus(record.id, 0)
  message.success('禁用成功')
  loadData()
}

const handleEnable = async (record) => {
  await userApi.updateStatus(record.id, 1)
  message.success('启用成功')
  loadData()
}

const handleDelete = async (record) => {
  await userApi.delete(record.id)
  message.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}
</style>
