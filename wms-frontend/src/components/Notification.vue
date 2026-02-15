<template>
  <div class="notification-container">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
      <el-button circle @click="showNotifications = true" type="primary" icon="el-icon-bell" />
    </el-badge>
    
    <el-drawer
      v-model="showNotifications"
      title="通知中心"
      direction="rtl"
      size="400px"
      :before-close="handleClose"
    >
      <div class="notification-header">
        <span>通知列表</span>
        <div>
          <el-button
            v-if="unreadCount > 0"
            type="text"
            size="small"
            @click="markAllAsRead"
          >
            全部标为已读
          </el-button>
        </div>
      </div>
      
      <div class="notification-content">
        <el-empty v-if="notices.length === 0" description="暂无通知" />
        
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="notification-item"
          :class="{ 'unread': notice.isRead === 0 }"
          @click="handleNoticeClick(notice)"
        >
          <div class="notification-header-item">
            <div class="notification-title">
              <el-tag
                :type="getTypeTagType(notice.type)"
                size="small"
                style="margin-right: 8px;"
              >
                {{ getTypeLabel(notice.type) }}
              </el-tag>
              <span>{{ notice.title }}</span>
            </div>
            <div class="notification-time">
              {{ formatTime(notice.createTime) }}
            </div>
          </div>
          
          <div class="notification-body">
            {{ notice.content }}
          </div>
          
          <div class="notification-actions">
            <el-button
              v-if="notice.isRead === 0"
              type="text"
              size="small"
              @click.stop="markAsRead(notice.id)"
            >
              标记已读
            </el-button>
            <el-button
              type="text"
              size="small"
              @click.stop="deleteNotice(notice.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="notification-footer">
        <el-button type="primary" @click="loadMore" v-if="notices.length >= 5">
          加载更多
        </el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { markAsRead as markAsReadApi, markAllAsRead as markAllAsReadApi, deleteNotice as deleteNoticeApi } from '@/api/notice'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  },
  notices: {
    type: Array,
    default: () => []
  },
  unreadCount: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['refresh', 'noticeClick'])

const showNotifications = ref(false)

const handleClose = () => {
  showNotifications.value = false
}

const handleNoticeClick = (notice) => {
  emit('noticeClick', notice)
  
  if (notice.isRead === 0) {
    markAsRead(notice.id)
  }
}

const markAsRead = async (noticeId) => {
  try {
    const response = await markAsReadApi(props.userId, noticeId)
    if (response.code === 200) {
      ElMessage.success('标记已读成功')
      emit('refresh')
    } else {
      ElMessage.error(response.message || '标记已读失败')
    }
  } catch (error) {
    ElMessage.error('标记已读失败')
    console.error(error)
  }
}

const markAllAsRead = async () => {
  try {
    const response = await markAllAsReadApi(props.userId)
    if (response.code === 200) {
      ElMessage.success('全部标记已读成功')
      emit('refresh')
    } else {
      ElMessage.error(response.message || '标记已读失败')
    }
  } catch (error) {
    ElMessage.error('标记已读失败')
    console.error(error)
  }
}

const deleteNotice = async (noticeId) => {
  try {
    const response = await deleteNoticeApi(noticeId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      emit('refresh')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error('删除失败')
    console.error(error)
  }
}

const loadMore = () => {
  emit('loadMore')
}

const getTypeLabel = (type) => {
  const typeMap = {
    logistics: '物流',
    activity: '活动',
    expiry: '过期',
    system: '系统'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type) => {
  const typeMap = {
    logistics: 'success',
    activity: 'warning',
    expiry: 'danger',
    system: 'info'
  }
  return typeMap[type] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < 7 * day) {
    return Math.floor(diff / day) + '天前'
  } else {
    return date.toLocaleDateString()
  }
}
</script>

<style scoped>
.notification-container {
  position: relative;
}

.notification-badge {
  margin-right: 20px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.notification-content {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.notification-item {
  padding: 12px;
  margin-bottom: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.notification-item:hover {
  background-color: #f5f5f5;
}

.notification-item.unread {
  background-color: #f0f9ff;
  border-left: 3px solid #409eff;
}

.notification-header-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-title {
  display: flex;
  align-items: center;
  font-weight: 500;
  flex: 1;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.notification-body {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
  line-height: 1.5;
}

.notification-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.notification-footer {
  margin-top: 10px;
  text-align: center;
}
</style>