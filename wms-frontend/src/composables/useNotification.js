import { ref, onMounted, onUnmounted } from 'vue'
import { getLatestNotice } from '@/api/notice'

export function useNotification(userId) {
  const unreadCount = ref(0)
  const latestNotices = ref([])
  const pollingInterval = ref(null)
  const isPolling = ref(false)

  const fetchNotifications = async () => {
    try {
      const response = await getLatestNotice(userId)
      if (response.code === 200) {
        latestNotices.value = response.data.notices || []
        unreadCount.value = response.data.unreadCount || 0
      }
    } catch (error) {
      console.error('获取通知失败:', error)
    }
  }

  const startPolling = (interval = 30000) => {
    if (isPolling.value) {
      return
    }
    
    isPolling.value = true
    
    fetchNotifications()
    
    pollingInterval.value = setInterval(() => {
      fetchNotifications()
    }, interval)
  }

  const stopPolling = () => {
    if (pollingInterval.value) {
      clearInterval(pollingInterval.value)
      pollingInterval.value = null
      isPolling.value = false
    }
  }

  onMounted(() => {
    startPolling()
  })

  onUnmounted(() => {
    stopPolling()
  })

  return {
    unreadCount,
    latestNotices,
    isPolling,
    fetchNotifications,
    startPolling,
    stopPolling
  }
}