import request from './request'

// 分页查询借阅记录
export function queryBorrowsByPage(params) {
  return request.get('/api/borrow/queryBorrowsByPage', { params })
}

// 归还图书
export function returnBook(data) {
  return request.post('/api/borrow/returnBook', data)
}

// 删除借阅记录
export function deleteBorrow(data) {
  return request.delete('/api/borrow/deleteBorrow', { data })
}

// 批量删除借阅记录
export function batchDeleteBorrows(data) {
  return request.delete('/api/borrow/batchDelete', { data })
}
