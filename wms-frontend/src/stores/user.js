import { defineStore } from 'pinia'
import { ref, onMounted } from 'vue'
import { login, getCurrentUser } from '@/api/user'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)

  // 初始化用户信息
  const initUser = async () => {
    const token = sessionStorage.getItem('token')
    if (token) {
      try {
        const res = await getCurrentUser()
        if (res.code === 200) {
          user.value = res.data
          return true
        }
      } catch (error) {
        // 获取用户信息失败，清除token
        console.error('获取用户信息失败:', error)
        sessionStorage.removeItem('token')
        user.value = null
      }
    }
    return false
  }

  // 页面加载时初始化用户信息
  onMounted(() => {
    initUser()
  })

  const setUser = (userData) => {
    user.value = userData
  }

  const loginUser = async (username, password) => {
    try {
      console.log('调用登录API:', username, password)
      const res = await login(username, password)
      console.log('登录API返回:', res)
      if (res.code === 200) {
        console.log('登录成功，存储用户信息')
        user.value = {
          id: res.data.id,
          username: res.data.username,
          realName: res.data.realName,
          company: res.data.company,
          role: res.data.role,
          token: res.data.token
        }
        if (res.data.token) {
          console.log('存储token:', res.data.token)
          sessionStorage.setItem('token', res.data.token)
        }
        return true
      } else {
        console.log('登录失败，API返回错误:', res.message)
        throw new Error(res.message || '登录失败')
      }
    } catch (error) {
      console.error('登录异常:', error)
      throw error
    }
  }

  const logout = () => {
    user.value = null
    sessionStorage.removeItem('token')
    router.push('/login')
  }

  return {
    user,
    setUser,
    loginUser,
    logout
  }
})




