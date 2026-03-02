import request from './index'

export const statsApi = {
  getDashboard: () => request.get('/admin/stats/dashboard'),
  getCourseStats: (courseId) => request.get(`/admin/stats/course/${courseId}`)
}
