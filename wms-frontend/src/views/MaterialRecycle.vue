<template>
  <div class="recycle-container">
    <el-card shadow="hover" :body-style="{ padding: '20px' }">
      <template #header>
        <div class="card-header">
          <el-text :type="'primary'" :size="'large'" style="font-weight: bold;">
            🗑️ 辅料回收站
          </el-text>
          <el-button 
            type="danger" 
            :icon="Delete" 
            @click="handleCleanupRecycle"
            :loading="cleaningUp"
            :disabled="deletedMaterials.length === 0"
          >
            清空回收站
          </el-button>
        </div>
      </template>

      <!-- 回收站统计信息 -->
      <div class="recycle-stats" style="margin-bottom: 20px;">
        <el-space size="medium">
          <el-statistic :value="deletedMaterials.length" title="已删除辅料数量" />
          <el-statistic :value="autoCleanupDays" title="自动清理天数" />
          <el-statistic :value="estimatedCleanupDate" title="下次清理时间" />
        </el-space>
      </div>

      <!-- 操作说明 -->
      <el-alert
        title="操作说明"
        type="info"
        :closable="false"
        style="margin-bottom: 20px;"
      >
        <template #default>
          <ul style="margin: 0; padding-left: 20px;">
            <li>🟢 <strong>恢复</strong>：将辅料恢复到正常状态，可在辅料库中重新查看</li>
            <li>🔴 <strong>永久删除</strong>：彻底删除辅料，不可恢复，同时删除相关图片</li>
            <li>🗑️ <strong>清空回收站</strong>：批量永久删除所有已删除的辅料</li>
            <li>⏰ <strong>自动清理</strong>：系统会定期清理回收站中超过30天的辅料</li>
          </ul>
        </template>
      </el-alert>

      <!-- 已删除辅料列表 -->
      <div class="recycle-list">
        <!-- 加载中状态 -->
        <el-skeleton v-if="loading" :rows="8" animated style="margin: 20px 0;">
          <template #template>
            <el-skeleton-item variant="image" style="width: 100%; height: 120px;" />
            <el-skeleton-item variant="p" style="width: 80%;" />
            <el-skeleton-item variant="text" style="width: 60%;" />
            <el-skeleton-item variant="text" style="width: 40%;" />
          </template>
        </el-skeleton>

        <!-- 空状态 -->
        <el-empty 
          v-else-if="deletedMaterials.length === 0" 
          description="回收站为空" 
          style="margin: 100px 0;"
        >
          <el-button type="primary" @click="$router.push('/material-library')">
            返回辅料库
          </el-button>
        </el-empty>

        <!-- 辅料列表 -->
        <el-table 
          v-else 
          :data="deletedMaterials" 
          style="width: 100%" 
          border 
          stripe
        >
          <el-table-column prop="productCode" label="辅料编码" width="150" />
          <el-table-column prop="productName" label="辅料名称" min-width="200">
            <template #default="scope">
              <el-space>
                <el-image 
                  v-if="getProcessedImageUrl(scope.row)"
                  :src="getProcessedImageUrl(scope.row)"
                  fit="cover" 
                  style="width: 40px; height: 40px; border-radius: 4px;"
                />
                <el-text :truncate="{ rows: 1 }">
                  {{ scope.row.productName }}
                </el-text>
              </el-space>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="price" label="价格" width="120">
            <template #default="scope">
              <el-text type="danger">¥{{ scope.row.price }} / {{ scope.row.unit }}</el-text>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag type="danger" effect="dark">已删除</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="updateTime" label="删除时间" width="200">
            <template #default="scope">
              {{ formatDate(scope.row.updateTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-space size="small">
                <el-button 
                  :icon="Refresh" 
                  type="success" 
                  size="small"
                  @click="handleRestoreMaterial(scope.row.id)"
                  :loading="restoringIds.includes(scope.row.id)"
                >
                  恢复
                </el-button>
                <el-button 
                  :icon="Delete" 
                  type="danger" 
                  size="small"
                  @click="handlePermanentDelete(scope.row.id, scope.row.productName)"
                  :loading="deletingIds.includes(scope.row.id)"
                >
                  永久删除
                </el-button>
              </el-space>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页组件 -->
        <div class="pagination-container" style="margin-top: 20px; text-align: right;">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Delete, Refresh, Warning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getProcessedImageUrl } from '@/utils/imageProcessor'

const router = useRouter()

// 状态管理
const deletedMaterials = ref([])
const loading = ref(false)
const cleaningUp = ref(false)
const restoringIds = ref([])
const deletingIds = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 自动清理配置
const autoCleanupDays = ref(30)

// 计算下次清理时间
const estimatedCleanupDate = computed(() => {
  const today = new Date()
  const nextCleanup = new Date(today)
  nextCleanup.setDate(today.getDate() + autoCleanupDays.value)
  return nextCleanup.toLocaleDateString('zh-CN')
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 加载已删除的辅料
const loadDeletedMaterials = async () => {
  loading.value = true
  try {
    const { default: request } = await import('@/utils/request')
    const response = await request({
      url: '/product/deleted',
      method: 'get'
    })
    
    if (response.code === 200) {
      deletedMaterials.value = response.data
      total.value = response.data.length
    } else {
      ElMessage.error('加载回收站数据失败')
    }
  } catch (error) {
    console.error('加载回收站数据失败:', error)
    ElMessage.error('加载回收站数据失败')
  } finally {
    loading.value = false
  }
}

// 恢复辅料
const handleRestoreMaterial = async (id) => {
  try {
    restoringIds.value.push(id)
    
    const { default: request } = await import('@/utils/request')
    const response = await request({
      url: `/product/restore/${id}`,
      method: 'put'
    })
    
    if (response.code === 200) {
      ElMessage.success('辅料恢复成功')
      loadDeletedMaterials()
    } else {
      ElMessage.error('辅料恢复失败')
    }
  } catch (error) {
    console.error('恢复辅料失败:', error)
    ElMessage.error('辅料恢复失败')
  } finally {
    restoringIds.value = restoringIds.value.filter(item => item !== id)
  }
}

// 永久删除辅料
const handlePermanentDelete = async (id, productName) => {
  try {
    await ElMessageBox.confirm(
      `确定要永久删除辅料 "${productName}" 吗？此操作不可恢复，同时会删除相关图片文件。`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deletingIds.value.push(id)
    
    const { default: request } = await import('@/utils/request')
    const response = await request({
      url: `/product/permanent/${id}`,
      method: 'delete'
    })
    
    if (response.code === 200) {
      ElMessage.success('辅料永久删除成功')
      loadDeletedMaterials()
    } else {
      ElMessage.error('辅料永久删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('永久删除辅料失败:', error)
      ElMessage.error('辅料永久删除失败')
    }
  } finally {
    deletingIds.value = deletingIds.value.filter(item => item !== id)
  }
}

// 清空回收站
const handleCleanupRecycle = async () => {
  if (deletedMaterials.value.length === 0) {
    ElMessage.info('回收站为空，无需清空')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要清空回收站吗？此操作将永久删除所有 ${deletedMaterials.value.length} 个已删除的辅料，不可恢复。`,
      '危险操作',
      {
        confirmButtonText: '确定清空',
        cancelButtonText: '取消',
        type: 'danger'
      }
    )
    
    cleaningUp.value = true
    
    // 批量永久删除所有已删除的辅料
    const { default: request } = await import('@/utils/request')
    const deletePromises = deletedMaterials.value.map(material => {
      return request({
        url: `/product/permanent/${material.id}`,
        method: 'delete'
      })
    })
    
    await Promise.all(deletePromises)
    
    ElMessage.success('回收站清空成功')
    loadDeletedMaterials()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空回收站失败:', error)
      ElMessage.error('清空回收站失败')
    }
  } finally {
    cleaningUp.value = false
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadDeletedMaterials()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadDeletedMaterials()
}

// 初始化
onMounted(() => {
  loadDeletedMaterials()
})
</script>

<style scoped>
.recycle-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recycle-stats {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .recycle-stats {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>