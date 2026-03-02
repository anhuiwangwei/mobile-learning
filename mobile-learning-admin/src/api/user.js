import request from './index'

export const userApi = {
  list: (params) => request.get('/admin/user/list', { params }),
  updateStatus: (id, status) => request.put('/admin/user/status', null, { params: { id, status } }),
  delete: (id) => request.delete(`/admin/user/${id}`)
}
