import request from './index'

export const categoryApi = {
  list: () => request.get('/admin/category/list'),
  getChildren: (parentId) => request.get(`/admin/category/children/${parentId}`),
  add: (data) => request.post('/admin/category', data),
  update: (data) => request.put('/admin/category', data),
  delete: (id) => request.delete(`/admin/category/${id}`)
}
