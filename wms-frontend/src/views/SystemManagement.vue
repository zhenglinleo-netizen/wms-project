<template>
  <div class="system-management">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon size="28"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon size="28"><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.enabledUsers }}</div>
              <div class="stat-label">启用用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon size="28"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingUsers }}</div>
              <div class="stat-label">待审核用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon size="28"><CloseBold /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.disabledUsers }}</div>
              <div class="stat-label">禁用用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="panel-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>待审核用户</span>
          <div>
            <el-button type="primary" link @click="goUserManage">前往用户管理</el-button>
          </div>
        </div>
      </template>
      <el-table :data="pendingList" v-loading="loading" size="small" border>
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="realName" label="真实姓名" width="160" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'success'">
              {{ roleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="approve(row)">审核通过</el-button>
            <el-button type="warning" size="small" @click="reject(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && pendingList.length === 0" description="暂无待审核用户" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, CircleCheck, Warning, CloseBold } from '@element-plus/icons-vue'
import { getUserList, updateUser } from '@/api/user'

const router = useRouter()
const loading = ref(false)

const stats = ref({
  totalUsers: 0,
  enabledUsers: 0,
  pendingUsers: 0,
  disabledUsers: 0
})

const pendingList = ref([])

const roleName = (role) => {
  if (role === 'admin') return '管理员'
  if (role === 'designer') return '设计师'
  if (role === 'buyer') return '采购员'
  return '普通用户'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList()
    const list = res.data || []
    stats.value.totalUsers = list.length
    stats.value.enabledUsers = list.filter(u => u.status === 1).length
    stats.value.pendingUsers = list.filter(u => u.status === 0).length
    stats.value.disabledUsers = list.filter(u => u.status === 2).length
    pendingList.value = list.filter(u => u.status === 0)
  } catch (error) {
    ElMessage.error('加载系统数据失败')
  } finally {
    loading.value = false
  }
}

const approve = async (row) => {
  try {
    await updateUser({ id: row.id, status: 1 })
    ElMessage.success('已通过审核')
    loadData()
  } catch (e) {
    ElMessage.error('审核失败')
  }
}

const reject = async (row) => {
  try {
    await updateUser({ id: row.id, status: 2 })
    ElMessage.success('已驳回用户')
    loadData()
  } catch (e) {
    ElMessage.error('驳回失败')
  }
}

const goUserManage = () => {
  router.push('/user')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.system-management {
  padding: 20px;
}
.stat-card {
  transition: all 0.3s;
}
.stat-card:hover {
  transform: translateY(-4px);
}
.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.stat-content .stat-value {
  font-size: 22px;
  font-weight: bold;
}
.stat-content .stat-label {
  color: #909399;
  font-size: 12px;
}
.panel-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
