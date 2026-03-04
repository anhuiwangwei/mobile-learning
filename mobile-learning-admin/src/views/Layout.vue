<template>
  <a-layout class="layout-container">
    <a-layout-header class="header">
      <div class="logo">
        移动学习平台
      </div>
      <a-menu
        v-model:selected-keys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :items="menuItems"
        @click="handleMenuClick"
      />
      <div class="user-info">
        <a-dropdown>
          <a class="ant-dropdown-link">
            {{ userStore.userInfo.realName || userStore.userInfo.username }}
            <DownOutlined />
          </a>
          <template #overlay>
            <a-menu>
              <a-menu-item
                key="profile"
                @click="handleProfile"
              >
                个人资料
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item
                key="logout"
                @click="handleLogout"
              >
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </a-layout-header>
    <a-layout-content class="content">
      <router-view />
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/store'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const selectedKeys = ref([route.name])

const menuItems = [
  { key: 'Dashboard', label: '首页' },
  { key: 'Users', label: '用户管理' },
  { key: 'Teachers', label: '教师管理' },
  { key: 'Courses', label: '课程管理' },
  { key: 'Exams', label: '考试管理' }
]

const handleMenuClick = ({ key }) => {
  router.push({ name: key })
}

const handleProfile = () => {
  router.push({ name: 'Profile' })
}

const handleLogout = () => {
  userStore.logout()
  message.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.logo {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  margin-right: 40px;
}

.user-info {
  margin-left: auto;
  color: #fff;
}

.content {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
