import request from './index'

export const courseApi = {
  list: (params) => request.get('/admin/course/list', { params }),
  get: (id) => request.get(`/admin/course/${id}`),
  add: (data) => request.post('/admin/course', data),
  update: (data) => request.put('/admin/course', data),
  delete: (id) => request.delete(`/admin/course/${id}`),
  getChapters: (id) => request.get(`/admin/course/${id}/chapters`),
  addChapter: (data) => request.post('/admin/course/chapter', data),
  updateChapter: (data) => request.put('/admin/course/chapter', data),
  deleteChapter: (id) => request.delete(`/admin/course/chapter/${id}`),
  getSections: (chapterId) => request.get(`/admin/course/chapter/${chapterId}/sections`),
  addSection: (data) => request.post('/admin/course/section', data),
  updateSection: (data) => request.put('/admin/course/section', data),
  deleteSection: (id) => request.delete(`/admin/course/section/${id}`)
}

export const fileApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
