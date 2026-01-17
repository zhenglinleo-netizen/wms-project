import request from '@/utils/request'

export const login = (username, password) => {
  return request({
    url: '/user/login',
    method: 'post',
    data: { username, password }
  })
}

export const register = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export const changePassword = (data) => {
  return request({
    url: '/user/change-password',
    method: 'post',
    data
  })
}

export const getUserList = () => {
  return request({
    url: '/user/list',
    method: 'get'
  })
}

export const saveUser = (data) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

export const getCurrentUser = () => {
  return request({
    url: '/user/current',
    method: 'get'
  })
}




