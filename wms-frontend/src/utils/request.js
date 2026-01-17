import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      // 权限不足
      if (res.code === 403) {
        ElMessage.warning(res.message || '权限不足')
        return Promise.reject(new Error(res.message || '权限不足'))
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    // HTTP 状态码错误处理
    if (error.response) {
      if (error.response.status === 403) {
        ElMessage.warning('权限不足，需要管理员权限')
        return Promise.reject(error)
      } else if (error.response.status === 401) {
        ElMessage.warning('未登录或登录已过期，请重新登录')
        // 可以在这里跳转到登录页
        return Promise.reject(error)
      }
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
