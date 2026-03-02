import request from './index'

export const teacherApi = {
  list: (params) => request.get('/admin/teacher/list', { params }),
  add: (data) => request.post('/admin/teacher', data),
  update: (data) => request.put('/admin/teacher', data),
  delete: (id) => request.delete(`/admin/teacher/${id}`)
}
