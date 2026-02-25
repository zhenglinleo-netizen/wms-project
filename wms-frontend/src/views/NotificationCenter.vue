<template>
  <div class="notification-center">
    <el-card class="notification-card">
      <template #header>
        <div class="card-header">
          <span class="title">消息中心</span>
          <div class="header-actions">
            <el-button 
              v-if="unreadCount > 0" 
              type="primary" 
              size="small" 
              @click="markAllAsRead"
            >
              全部标为已读
            </el-button>
            <el-button 
              v-if="selectedNotifications.length > 0" 
              type="danger" 
              size="small" 
              @click="batchDelete"
            >
              批量删除
            </el-button>
            <el-button 
              v-if="selectedNotifications.length > 0" 
              type="success" 
              size="small" 
              @click="batchMarkAsRead"
            >
              批量标为已读
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选和搜索 -->
      <div class="filter-section">
        <el-row :gutter="20" align="middle">
          <el-col :span="8">
            <el-select 
              v-model="filterType" 
              placeholder="按类型筛选" 
              clearable 
              class="w-full"
            >
              <el-option label="物流通知" value="logistics" />
              <el-option label="活动通知" value="activity" />
              <el-option label="过期通知" value="expiry" />
              <el-option label="系统通知" value="system" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-select 
              v-model="filterStatus" 
              placeholder="按状态筛选" 
              clearable 
              class="w-full"
            >
              <el-option label="未读" value="0" />
              <el-option label="已读" value="1" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索通知"
              class="w-full"
              prefix-icon="el-icon-search"
              @keyup.enter="handleSearch"
            />
          </el-col>
        </el-row>
      </div>

      <!-- 分类标签页 -->
      <div class="category-tabs">
        <el-tabs v-model="activeCategory" @tab-click="handleCategoryChange">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="系统通知" name="system" />
          <el-tab-pane label="业务通知" name="business" />
          <el-tab-pane label="未读消息" name="unread" />
        </el-tabs>
      </div>

      <!-- 通知列表 -->
      <div class="notification-list">
        <el-empty v-if="filteredNotifications.length === 0" description="暂无通知" />
        
        <el-checkbox-group v-model="selectedNotifications" v-else>
          <div
            v-for="notice in filteredNotifications"
            :key="notice.id"
            class="notification-item"
            :class="{ 'unread': notice.isRead === 0 }"
          >
            <el-checkbox :label="notice.id" class="notification-checkbox" />
            <div class="notification-content" @click="showNoticeDetail(notice)">
              <div class="notification-header">
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
        </el-checkbox-group>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="filteredNotifications.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalNotifications"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 通知详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentNotice?.title || '通知详情'"
      width="600px"
    >
      <div v-if="currentNotice" class="notice-detail">
        <div class="detail-header">
          <el-tag :type="getTypeTagType(currentNotice.type)">
            {{ getTypeLabel(currentNotice.type) }}
          </el-tag>
          <span class="detail-time">{{ formatTime(currentNotice.createTime) }}</span>
        </div>
        <div class="detail-content">
          {{ currentNotice.content }}
        </div>
        <div class="detail-footer" v-if="currentNotice.isRead === 0">
          <el-button type="primary" @click="markAsRead(currentNotice.id)">
            标记为已读
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, markAsRead as markAsReadApi, markAllAsRead as markAllAsReadApi, deleteNotice as deleteNoticeApi } from '@/api/notice'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userId = computed(() => userStore.user?.id || 0)

// 通知数据
const notices = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalNotifications = ref(0)
const unreadCount = ref(0)

// 筛选条件
const filterType = ref('')
const filterStatus = ref('')
const searchKeyword = ref('')
const activeCategory = ref('all')

// 批量操作
const selectedNotifications = ref([])

// 详情弹窗
const detailDialogVisible = ref(false)
const currentNotice = ref(null)

// 加载通知列表
const loadNotifications = async () => {
  try {
    const response = await getNoticeList(userId.value, pageSize.value * currentPage.value)
    if (response.code === 200) {
      notices.value = response.data || []
      totalNotifications.value = response.total || 0
      unreadCount.value = notices.value.filter(notice => notice.isRead === 0).length
    }
  } catch (error) {
    ElMessage.error('获取通知失败')
    console.error(error)
  }
}

// 筛选后的通知
const filteredNotifications = computed(() => {
  let result = [...notices.value]
  
  // 按分类筛选
  if (activeCategory.value !== 'all') {
    if (activeCategory.value === 'system') {
      result = result.filter(notice => notice.type === 'system')
    } else if (activeCategory.value === 'business') {
      result = result.filter(notice => notice.type !== 'system')
    } else if (activeCategory.value === 'unread') {
      result = result.filter(notice => notice.isRead === 0)
    }
  }
  
  // 按类型筛选
  if (filterType.value) {
    result = result.filter(notice => notice.type === filterType.value)
  }
  
  // 按状态筛选
  if (filterStatus.value !== '') {
    result = result.filter(notice => notice.isRead === parseInt(filterStatus.value))
  }
  
  // 搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(notice => 
      notice.title.toLowerCase().includes(keyword) || 
      notice.content.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 标记已读
const markAsRead = async (noticeId) => {
  try {
    const response = await markAsReadApi(userId.value, noticeId)
    if (response.code === 200) {
      ElMessage.success('标记已读成功')
      loadNotifications()
    } else {
      ElMessage.error(response.message || '标记已读失败')
    }
  } catch (error) {
    ElMessage.error('标记已读失败')
    console.error(error)
  }
}

// 全部标记已读
const markAllAsRead = async () => {
  try {
    const response = await markAllAsReadApi(userId.value)
    if (response.code === 200) {
      ElMessage.success('全部标记已读成功')
      loadNotifications()
    } else {
      ElMessage.error(response.message || '标记已读失败')
    }
  } catch (error) {
    ElMessage.error('标记已读失败')
    console.error(error)
  }
}

// 删除通知
const deleteNotice = async (noticeId) => {
  try {
    const response = await deleteNoticeApi(noticeId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadNotifications()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error('删除失败')
    console.error(error)
  }
}

// 批量删除
const batchDelete = async () => {
  if (selectedNotifications.value.length === 0) {
    ElMessage.warning('请选择要删除的通知')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要删除选中的通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 批量删除逻辑
    for (const noticeId of selectedNotifications.value) {
      await deleteNoticeApi(noticeId)
    }
    
    ElMessage.success('批量删除成功')
    selectedNotifications.value = []
    loadNotifications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error(error)
    }
  }
}

// 批量标记已读
const batchMarkAsRead = async () => {
  if (selectedNotifications.value.length === 0) {
    ElMessage.warning('请选择要标记已读的通知')
    return
  }
  
  try {
    for (const noticeId of selectedNotifications.value) {
      await markAsReadApi(userId.value, noticeId)
    }
    
    ElMessage.success('批量标记已读成功')
    selectedNotifications.value = []
    loadNotifications()
  } catch (error) {
    ElMessage.error('批量标记已读失败')
    console.error(error)
  }
}

// 显示通知详情
const showNoticeDetail = (notice) => {
  currentNotice.value = notice
  detailDialogVisible.value = true
  
  // 自动标记为已读
  if (notice.isRead === 0) {
    markAsRead(notice.id)
  }
}

// 分类切换
const handleCategoryChange = () => {
  selectedNotifications.value = []
}

// 搜索
const handleSearch = () => {
  selectedNotifications.value = []
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadNotifications()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadNotifications()
}

// 类型标签
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

// 时间格式化
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

// 页面加载时获取通知
onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notification-center {
  padding: 20px;
  min-height: calc(100vh - 160px);
}

.notification-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-section {
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.category-tabs {
  margin-bottom: 20px;
}

.notification-list {
  margin-bottom: 20px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s;
  background-color: #ffffff;
}

.notification-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-color: #dcdfe6;
}

.notification-item.unread {
  background-color: #f0f9ff;
  border-left: 4px solid #409eff;
}

.notification-checkbox {
  margin-right: 12px;
  margin-top: 2px;
}

.notification-content {
  flex: 1;
  cursor: pointer;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-title {
  display: flex;
  align-items: center;
  font-weight: 500;
  color: #303133;
  flex: 1;
}

.notification-time {
  font-size: 12px;
  color: #909399;
  margin-left: 16px;
}

.notification-body {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.notification-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 16px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.notice-detail {
  padding: 16px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.detail-time {
  font-size: 14px;
  color: #909399;
}

.detail-content {
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 24px;
}

.detail-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

@media (max-width: 768px) {
  .notification-center {
    padding: 10px;
  }
  
  .filter-section {
    padding: 12px;
  }
  
  .notification-item {
    padding: 12px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>