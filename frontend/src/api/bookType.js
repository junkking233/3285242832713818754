import request from './request'

// 分页查询图书类型
export function queryBookTypesByPage(params) {
  return request.get('/api/bookType/queryBookTypesByPage', { params })
}

// 查询全部图书类型
export function listAllBookTypes() {
  return request.get('/api/bookType/listAll')
}

// 添加图书类型
export function addBookType(data) {
  return request.post('/api/bookType/addBookType', data)
}

// 更新图书类型
export function updateBookType(data) {
  return request.put('/api/bookType/updateBookType', data)
}

// 删除图书类型
export function deleteBookType(data) {
  return request.delete('/api/bookType/deleteBookType', { data })
}

// 批量删除图书类型
export function batchDeleteBookTypes(data) {
  return request.delete('/api/bookType/batchDelete', { data })
}
