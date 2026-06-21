import request from './request'

// 登录
export function login(data) {
  return request.post('/api/login', data)
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/api/user/info')
}

// 分页查询用户
export function queryUsersByPage(params) {
  return request.get('/api/user/queryUsersByPage', { params })
}

// 添加用户
export function addUser(data) {
  return request.post('/api/user/addUser', data)
}

// 更新用户
export function updateUser(data) {
  return request.put('/api/user/updateUser', data)
}

// 删除用户
export function deleteUser(data) {
  return request.delete('/api/user/deleteUser', { data })
}
