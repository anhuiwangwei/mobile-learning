import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/UserList.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'teachers',
        name: 'Teachers',
        component: () => import('@/views/TeacherList.vue'),
        meta: { title: '教师管理' }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('@/views/CourseList.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'courses/:id/chapters',
        name: 'CourseChapters',
        component: () => import('@/views/ChapterList.vue'),
        meta: { title: '章节管理' }
      },
      {
        path: 'exams',
        name: 'Exams',
        component: () => import('@/views/ExamPaperList.vue'),
        meta: { title: '考试管理' }
      },
      {
        path: 'exams/:id/questions',
        name: 'ExamQuestions',
        component: () => import('@/views/QuestionList.vue'),
        meta: { title: '题目管理' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile/index.vue'),
        meta: { title: '个人资料' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
