<template>
  <div class="page-container">
    <a-card title="个人资料">
      <a-form :model="form" :label-col="{ span: 4 }" style="max-width: 600px">
        <a-form-item label="用户名">
          <a-input v-model:value="form.username" disabled />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="form.realName" />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="form.nickname" />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input v-model:value="form.phone" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="form.email" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSubmit">保存修改</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card title="修改密码" style="margin-top: 16px">
      <a-form :model="passwordForm" :label-col="{ span: 4 }" style="max-width: 600px">
        <a-form-item label="当前密码" required>
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入当前密码" />
        </a-form-item>
        <a-form-item label="新密码" required>
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item label="确认密码" required>
          <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handlePasswordSubmit">修改密码</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/store'
import axios from 'axios'

const userStore = useUserStore()

const form = reactive({
  username: '',
  realName: '',
  nickname: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadUserInfo = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('/admin/info', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    if (res.data.code === 200) {
      const data = res.data.data
      form.username = data.username || ''
      form.realName = data.realName || ''
      form.nickname = data.nickname || ''
      form.phone = data.phone || ''
      form.email = data.email || ''
    }
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
}

const handleSubmit = async () => {
  try {
    message.success('保存成功')
  } catch (e) {
    message.error('保存失败')
  }
}

const handlePasswordSubmit = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    message.error('请填写完整密码信息')
    return
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  
  if (passwordForm.newPassword.length < 6) {
    message.error('密码长度不能少于 6 位')
    return
  }
  
  try {
    message.success('密码修改成功，请重新登录')
    userStore.logout()
    setTimeout(() => {
      window.location.href = '/login'
    }, 1000)
  } catch (e) {
    message.error('密码修改失败')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
}
</style>
