import request from './index'

export const examApi = {
  paperList: (params) => request.get('/admin/exam/paper/list', { params }),
  getPaper: (id) => request.get(`/admin/exam/paper/${id}`),
  addPaper: (data) => request.post('/admin/exam/paper', data),
  updatePaper: (data) => request.put('/admin/exam/paper', data),
  deletePaper: (id) => request.delete(`/admin/exam/paper/${id}`),
  getQuestions: (paperId) => request.get('/admin/exam/question/list', { params: { paperId } }),
  addQuestion: (data) => request.post('/admin/exam/question', data),
  updateQuestion: (data) => request.put('/admin/exam/question', data),
  deleteQuestion: (id) => request.delete(`/admin/exam/question/${id}`),
  getChapterExams: (chapterId) => request.get(`/admin/exam/chapter/${chapterId}/exams`),
  bindExam: (data) => request.post('/admin/exam/chapter/exam', data),
  unbindExam: (id) => request.delete(`/admin/exam/chapter/exam/${id}`)
}
