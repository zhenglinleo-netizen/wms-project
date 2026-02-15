<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon size="30"><List /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingTasks }}</div>
              <div class="stat-label">今日待办/待审任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6" v-if="userStore.user?.role === 'admin'">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon size="30"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.inventoryAlerts }}</div>
              <div class="stat-label">库存预警</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon size="30"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.activeProjects }}</div>
              <div class="stat-label">进行中项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon size="30"><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.monthlyPurchase }}</div>
              <div class="stat-label">本月采购额 (万元)</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 近期需求单状态 -->
      <el-col :span="12">
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>近期需求单状态</span>
              <el-button text @click="goToRequirementManagement">查看全部</el-button>
            </div>
          </template>
          <div v-if="recentRequirements.length === 0" class="no-requirements">
            <el-empty description="您还没有创建任何需求单" />
            <el-button type="primary" size="small" @click="goToRequirementManagement" style="margin-top: 16px;">创建需求单</el-button>
          </div>
          <el-table v-else :data="recentRequirements" style="width: 100%" size="small">
            <el-table-column prop="code" label="单号" min-width="150" :show-overflow-tooltip="true" />
            <el-table-column prop="project" label="所属项目" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="date" label="提交时间" width="140">
              <template #default="scope">
                {{ formatDateTime(scope.row.date) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 热门辅料推荐 -->
      <el-col :span="12">
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>热门辅料推荐</span>
              <el-button text @click="goToMaterialLibrary">去辅料库</el-button>
            </div>
          </template>
          <div class="hot-materials">
            <div v-for="item in hotMaterials" :key="item.id" class="material-item">
              <el-image 
                :src="item.image || ''" 
                fit="cover" 
                class="material-img"
                :alt="item.name"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon class="image-icon"><Picture /></el-icon>
                    <div class="image-text">无图片</div>
                  </div>
                </template>
              </el-image>
              <div class="material-info">
                <div class="material-name">{{ item.name }}</div>
                <div class="material-price">¥{{ item.price }}</div>
                <div class="material-stock" :style="{ color: item.stock > 0 ? '#67c23a' : '#f56c6c' }">
                  {{ item.stock > 0 ? '有库存' : '缺货' }}
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 进行中项目 -->
      <el-col :span="12">
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>进行中项目</span>
              <el-button text @click="goToProjectManagement">查看全部</el-button>
            </div>
          </template>
          <div class="project-list">
            <div v-for="project in activeProjects" :key="project.id" class="project-item card">
              <div class="project-header">
                <div class="project-name">{{ project.projectName }}</div>
                <el-tag type="primary" class="project-status">{{ project.status }}</el-tag>
              </div>
              <div class="project-info">
                <div class="project-client">客户: {{ project.clientName }}</div>
                <div class="project-progress">
                  <div class="progress-label">进度: {{ project.progress }}%</div>
                  <el-progress :percentage="project.progress" :stroke-width="8" :show-text="false" />
                </div>
                <div class="project-deadline">
                  <el-icon class="deadline-icon"><Clock /></el-icon>
                  <span>截止日期: {{ project.endDate }}</span>
                </div>
              </div>
            </div>
            <div v-if="activeProjects.length === 0" class="no-projects">
              <el-empty description="暂无进行中项目" />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 库存预警 -->
      <el-col :span="12" v-if="userStore.user?.role === 'admin'">
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>库存预警</span>
              <el-button text @click="goToInventoryManagement">查看全部</el-button>
            </div>
          </template>
          <div class="inventory-alerts">
            <div v-for="item in inventoryAlerts" :key="item.id" class="alert-item card">
              <div class="alert-icon">
                <el-icon color="#e6a23c" size="20"><Warning /></el-icon>
              </div>
              <div class="alert-content">
                <div class="alert-name">{{ item.name }}</div>
                <div class="alert-stock-info">
                  <div class="stock-item">
                    <span class="stock-label">当前库存:</span>
                    <span class="stock-value warning">{{ item.quantity }}</span>
                  </div>
                  <div class="stock-item">
                    <span class="stock-label">预警阈值:</span>
                    <span class="stock-value normal">{{ item.threshold }}</span>
                  </div>
                  <div class="stock-progress">
                    <el-progress 
                      :percentage="(item.quantity / item.threshold) * 100" 
                      :stroke-width="6" 
                      :show-text="false" 
                      :color="['#f56c6c', '#e6a23c', '#67c23a']" 
                    />
                  </div>
                </div>
              </div>
            </div>
            <div v-if="inventoryAlerts.length === 0" class="no-alerts">
              <el-empty description="暂无库存预警" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统使用数据概览 -->
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统使用数据概览</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- 使用Element Plus的进度条模拟数据展示 -->
            <div class="usage-stats">
              <div class="usage-item">
                <div class="usage-label">辅料库使用</div>
                <el-progress :percentage="usageStats.materialUsage" :color="['#67c23a', '#e6a23c']" />
              </div>
              <div class="usage-item">
                <div class="usage-label">项目方案创建</div>
                <el-progress :percentage="usageStats.projectUsage" :color="['#409eff', '#909399']" />
              </div>
              <div class="usage-item">
                <div class="usage-label">需求单提交</div>
                <el-progress :percentage="usageStats.requirementUsage" :color="['#f56c6c', '#6f7ad3']" />
              </div>
            </div>
            <div class="chart-placeholder">
              <!-- 柱状图模拟 -->
              <div class="bar-chart">
                <div class="bar-item" v-for="(val, idx) in usageData" :key="idx">
                  <div class="bar" :style="{ height: val.value + 'px', backgroundColor: '#409eff' }"></div>
                  <div class="label">{{ val.label }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getProductList } from '@/api/product'
import { getInventoryList } from '@/api/inventory'
import { getProjectList } from '@/api/project'
import { getRequirementList } from '@/api/requirement'
import { List, Warning, Document, Money, Clock, Picture } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  pendingTasks: 5,
  inventoryAlerts: 2,
  activeProjects: 8,
  monthlyPurchase: 12.5
})

const recentRequirements = ref([
  { code: 'REQ20231201', project: '2024春季新品研发', status: '待审核', date: '2023-12-01' },
  { code: 'REQ20231202', project: '经典款补货', status: '已通过', date: '2023-12-02' },
  { code: 'REQ20231203', project: '高端定制系列', status: '审核中', date: '2023-12-03' },
  { code: 'REQ20231204', project: '童装系列', status: '草稿', date: '2023-12-04' }
])

const hotMaterials = ref([
  { id: 1, name: '纯棉面料 40S', price: '25.00', image: 'https://via.placeholder.com/80' },
  { id: 2, name: 'YKK 拉链 5号', price: '2.50', image: 'https://via.placeholder.com/80' },
  { id: 3, name: '天然牛角扣', price: '5.00', image: 'https://via.placeholder.com/80' },
  { id: 4, name: '蕾丝花边 5cm', price: '8.00', image: 'https://via.placeholder.com/80' }
])

const usageData = ref([
  { label: '周一', value: 120 },
  { label: '周二', value: 150 },
  { label: '周三', value: 180 },
  { label: '周四', value: 140 },
  { label: '周五', value: 200 },
  { label: '周六', value: 90 },
  { label: '周日', value: 110 }
])

const activeProjects = ref([
  { id: 1, projectName: '2024春季新款夹克研发', clientName: 'XYZ服饰有限公司', status: '进行中', progress: 65, endDate: '2024-03-30' },
  { id: 2, projectName: '高端定制西装订单', clientName: '个人客户-李先生', status: '进行中', progress: 85, endDate: '2024-01-15' },
  { id: 3, projectName: '2024夏季连衣裙系列', clientName: 'ABC时尚', status: '进行中', progress: 30, endDate: '2024-06-15' }
])

const inventoryAlerts = ref([
  { id: 1, name: '纯棉面料 40S', quantity: 50, threshold: 100 },
  { id: 2, name: 'YKK拉链 5号', quantity: 30, threshold: 50 }
])

const getStatusType = (status) => {
  const map = {
    '待审核': 'warning',
    '已通过': 'success',
    '审核中': 'primary',
    '已驳回': 'danger',
    '草稿': 'info'
  }
  return map[status] || 'info'
}

// 格式化日期时间为24小时制
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return dateString
  
  // 格式化日期时间为 yyyy-MM-dd HH:mm:ss 格式
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 导航函数
const goToRequirementManagement = () => {
  router.push('/requirement-management')
}

const goToMaterialLibrary = () => {
  router.push('/material-library')
}

const goToProjectManagement = () => {
  router.push('/project-scheme')
}

const goToInventoryManagement = () => {
  router.push('/inventory-management')
}

const usageStats = ref({
  materialUsage: 75,
  projectUsage: 60,
  requirementUsage: 85
})

onMounted(async () => {
  // 清空初始Mock数据，准备显示真实数据或空状态
  recentRequirements.value = []
  activeProjects.value = []
  
  // 尝试获取真实数据
  try {
    const products = await getProductList()
    const inventory = await getInventoryList()
    
    // 获取进行中项目 - 管理员查看所有，非管理员只查看自己相关的
    const projectsParams = {
      status: '进行中',
      page: 1,
      pageSize: 10
    }
    // 非管理员用户可以添加筛选条件，这里假设项目有创建者字段
    if (userStore.user?.role !== 'admin') {
      // 实际项目中可能需要根据用户角色和权限来过滤项目
      // 这里暂时不添加筛选条件，保持所有用户都能看到所有进行中项目
    }
    const projects = await getProjectList(projectsParams)
    
    // 管理员查看所有需求单，非管理员只查看自己创建的
    const requirementsParams = {
      page: 1,
      pageSize: 5
    }
    // 非管理员用户才添加creatorId参数，管理员查看所有需求单
    if (userStore.user?.role !== 'admin') {
      requirementsParams.creatorId = userStore.user?.userId || 1
    }
    const requirements = await getRequirementList(requirementsParams)
    
    // 更新近期需求单状态
    if (requirements.data && requirements.data.records && requirements.data.records.length > 0) {
      recentRequirements.value = requirements.data.records.map(req => ({
        code: req.requirementCode,
        project: req.projectName,
        status: req.status === 'draft' ? '草稿' : 
               req.status === 'pending' ? '待审核' : 
               req.status === 'approved' ? '已通过' : 
               req.status === 'rejected' ? '已驳回' : 
               req.status === 'negotiating' ? '议价中' : 
               req.status === 'negotiating_pending' ? '议价待审核' : 
               req.status === 'confirmed' ? '已确认' : 
               req.status === 'cancelled' ? '已取消' : req.status,
        date: req.createTime
      }))
    }
    
    // 简单的库存预警逻辑
    let alerts = 0
    let alertItems = []
    if (inventory.data) {
      const alertData = inventory.data.filter(i => i.quantity < 100) // 假设小于100预警
      alerts = alertData.length
      alertItems = alertData.map(i => ({
        id: i.id,
        name: i.productName || i.productCode || '未知辅料',
        quantity: i.quantity,
        threshold: 100
      }))
    }
    stats.value.inventoryAlerts = alerts
    inventoryAlerts.value = alertItems
    
    // 热门辅料可以用真实商品替换部分
    if (products.data && products.data.length > 0) {
      hotMaterials.value = products.data.slice(0, 4).map(p => ({
        id: p.id,
        name: p.productName,
        price: p.price,
        image: 'https://via.placeholder.com/80', // 暂时没有图片字段
        stock: inventory.data?.find(i => i.productId === p.id)?.quantity || 0
      }))
    }
    
    // 更新进行中项目数据
    if (projects.data && projects.data.length > 0) {
      stats.value.activeProjects = projects.data.length
      activeProjects.value = projects.data.slice(0, 3).map(p => ({
        id: p.id,
        projectName: p.projectName,
        clientName: p.clientName,
        status: p.status,
        progress: Math.floor(Math.random() * 100), // 模拟进度
        endDate: p.endDate
      }))
    } else {
      // 没有进行中项目时，更新统计数据
      stats.value.activeProjects = 0
    }
    
    // 更新待办任务统计 - 暂时使用Mock数据
    // stats.value.pendingTasks = 计算真实待办任务数量
  } catch (e) {
    // 静默处理错误，避免控制台输出干扰
    // 错误时保持空状态，不显示Mock数据
    recentRequirements.value = []
    activeProjects.value = []
    stats.value.activeProjects = 0
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}
.stat-card {
  height: 100px;
}
.stat-item {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 20px;
}
.stat-content .stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.dashboard-card {
  height: 280px;
  overflow: auto;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

/* 确保空状态下容器保持固定高度且不显示滚动条 */
.no-projects,
.no-requirements,
.no-alerts {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden !important;
}

/* 当容器中只有空状态元素时，不显示滚动条 */
.dashboard-card:has(.no-projects),
.dashboard-card:has(.no-requirements),
.dashboard-card:has(.no-alerts) {
  overflow: hidden !important;
}

/* 确保热门辅料推荐区域也保持固定高度 */
.hot-materials {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  padding: 10px 0;
  overflow: hidden;
}

/* 确保表格区域也保持固定高度 */
.el-table {
  flex: 1;
  overflow: hidden;
}

/* 确保项目列表区域保持固定高度 */
.project-list,
.inventory-alerts {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 6px 0;
  overflow: auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.hot-materials {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.material-item {
  width: calc(50% - 10px);
  display: flex;
  align-items: center;
  border: 1px solid #ebeef5;
  padding: 10px;
  border-radius: 4px;
}
.material-img {
  width: 60px;
  height: 60px;
  margin-right: 10px;
  border-radius: 4px;
}

.image-error {
  width: 60px;
  height: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.image-icon {
  font-size: 20px;
  color: #909399;
  margin-bottom: 4px;
}

.image-text {
  font-size: 10px;
  color: #909399;
  text-align: center;
  padding: 0 4px;
}
.material-name {
  font-weight: bold;
  font-size: 14px;
}
.material-price {
  color: #f56c6c;
  margin-top: 5px;
}
.chart-placeholder {
  height: 200px;
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  padding-bottom: 20px;
}
.bar-chart {
  display: flex;
  width: 100%;
  height: 100%;
  align-items: flex-end;
  justify-content: space-around;
}
.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.bar {
  width: 40px;
  border-radius: 4px 4px 0 0;
  transition: height 0.5s;
}
.label {
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
}

/* 美化进行中项目 */
.project-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 6px 0;
}

.project-item.card {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.project-item.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #d9e8f4;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.project-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 70%;
}

.project-status {
  font-size: 11px;
}

.project-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.project-client {
  font-size: 13px;
  color: #606266;
}

.project-progress {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.progress-label {
  font-size: 13px;
  color: #606266;
}

.project-deadline {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.deadline-icon {
  color: #e6a23c;
  font-size: 14px;
}

.no-projects {
  text-align: center;
  padding: 30px 0;
}

/* 近期需求单空状态 */
.no-requirements {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

/* 库存预警美化 */
.inventory-alerts {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 6px 0;
}

.alert-item.card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background-color: #fdf6ec;
  border: 1px solid #fdebd0;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.alert-item.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #fcd594;
}

.alert-icon {
  margin-top: 2px;
}

.alert-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.alert-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alert-stock-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stock-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.stock-label {
  color: #606266;
}

.stock-value.warning {
  color: #f56c6c;
  font-weight: 500;
}

.stock-value.normal {
  color: #67c23a;
  font-weight: 500;
}

.stock-progress {
  margin-top: 4px;
}

.no-alerts {
  text-align: center;
  padding: 40px 0;
}
</style>
