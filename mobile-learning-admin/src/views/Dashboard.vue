<template>
  <div class="dashboard">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="用户总数"
            :value="stats.userCount"
            :value-style="{ color: '#3f8600' }"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="课程总数"
            :value="stats.courseCount"
            :value-style="{ color: '#1890ff' }"
          >
            <template #prefix>
              <BookOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="教师总数"
            :value="stats.teacherCount"
            :value-style="{ color: '#cf1322' }"
          >
            <template #prefix>
              <TeamOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="考试总数"
            :value="stats.examCount"
            :value-style="{ color: '#faad14' }"
          >
            <template #prefix>
              <FileTextOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="12">
        <a-card title="学习数据">
          <a-descriptions bordered :column="2">
            <a-descriptions-item label="今日学习人数">{{ stats.todayLearners || 0 }}</a-descriptions-item>
            <a-descriptions-item label="总学习时长">{{ stats.totalStudyTime || 0 }}分钟</a-descriptions-item>
            <a-descriptions-item label="完成课程数">{{ stats.completedCourses || 0 }}</a-descriptions-item>
            <a-descriptions-item label="通过考试数">{{ stats.passedExams || 0 }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="课程数据">
          <a-descriptions bordered :column="2">
            <a-descriptions-item label="上架课程">{{ stats.onShelfCourses || 0 }}</a-descriptions-item>
            <a-descriptions-item label="下架课程">{{ stats.offShelfCourses || 0 }}</a-descriptions-item>
            <a-descriptions-item label="总章节数">{{ stats.totalChapters || 0 }}</a-descriptions-item>
            <a-descriptions-item label="总小节数">{{ stats.totalSections || 0 }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UserOutlined, BookOutlined, TeamOutlined, FileTextOutlined } from '@ant-design/icons-vue'
import axios from 'axios'

const stats = ref({
  userCount: 0,
  courseCount: 0,
  teacherCount: 0,
  examCount: 0,
  todayLearners: 0,
  totalStudyTime: 0,
  completedCourses: 0,
  passedExams: 0,
  onShelfCourses: 0,
  offShelfCourses: 0,
  totalChapters: 0,
  totalSections: 0
})

const loadStats = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('/admin/stats/dashboard', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    if (res.data.code === 200) {
      stats.value = { ...stats.value, ...res.data.data }
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 24px;
}

.ant-card {
  margin-bottom: 16px;
}
</style>
