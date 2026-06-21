import request from './request'

// 分页查询图书
export function queryBookInfosByPage(params) {
  return request.get('/api/book/queryBookInfosByPage', { params })
}

// 添加图书
export function addBookInfo(data) {
  return request.post('/api/book/addBookInfo', data)
}

// 更新图书
export function updateBookInfo(data) {
  return request.put('/api/book/updateBookInfo', data)
}

// 删除图书
export function deleteBookInfo(data) {
  return request.delete('/api/book/deleteBookInfo', { data })
}

// 借阅图书
export function borrowBook(data) {
  return request.post('/api/book/borrowBook', data)
}
