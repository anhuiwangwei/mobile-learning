import request from './index'

export const authApi = {
  login: (data) => request.post('/admin/login', data),
  getInfo: () => request.get('/admin/info')
}
