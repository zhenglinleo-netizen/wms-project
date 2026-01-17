<template>
  <div class="purchase-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>采购管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="filters" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="订单号/需求单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="待处理" value="pending" />
            <el-option label="采购中" value="purchasing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPurchaseList">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="purchaseList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="orderCode" label="订单号" min-width="180" />
        <el-table-column prop="requirementCode" label="需求单号" min-width="180" />
        <el-table-column prop="supplierName" label="供应商" min-width="150" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            {{ row.totalAmount ? '¥' + row.totalAmount.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPurchaseStatusType(row.status)">{{ getPurchaseStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" plain size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button v-if="userStore.user?.role !== 'admin'" type="warning" plain size="small" @click="handleUrge(row)">催促采购</el-button>
            <el-button v-if="userStore.user?.role === 'admin'" type="success" plain size="small" @click="handleUpdateStatus(row)">更新进度</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="采购订单详情" width="1000px" append-to-body>
      <div v-if="currentPurchase" class="purchase-detail">
        <el-row :gutter="20">
          <el-col :span="24">
            <div class="detail-header">
              <h3>{{ currentPurchase.orderCode }}</h3>
              <div class="status-tag">
                <el-tag :type="getPurchaseStatusType(currentPurchase.status)" size="large">{{ getPurchaseStatusLabel(currentPurchase.status) }}</el-tag>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="需求单号">{{ currentPurchase.requirementCode || '-' }}</el-descriptions-item>
              <el-descriptions-item label="供应商">{{ currentPurchase.supplierName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="订单金额">¥{{ currentPurchase.totalAmount ? currentPurchase.totalAmount.toFixed(2) : '0.00' }}</el-descriptions-item>
              <el-descriptions-item label="创建人">{{ currentPurchase.creatorName || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="创建时间">{{ formatDateTime(currentPurchase.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="订单日期">{{ formatDate(currentPurchase.orderDate) }}</el-descriptions-item>
              <el-descriptions-item label="交货日期">{{ formatDate(currentPurchase.deliveryDate) }}</el-descriptions-item>
              <el-descriptions-item label="备注">{{ currentPurchase.remark || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
          
          <el-col :span="24">
            <div style="margin-top: 20px;">
              <h4>采购明细</h4>
              <el-table :data="currentPurchase.items" border style="width: 100%">
                <el-table-column prop="materialName" label="辅料名称" width="150" />
                <el-table-column prop="materialCode" label="辅料编码" width="120" />
                <el-table-column prop="specification" label="规格" width="120" />
                <el-table-column prop="supplierName" label="供应商" min-width="120" />
                <el-table-column prop="quantity" label="数量" width="100" />
                <el-table-column prop="unit" label="单位" width="80" />
                <el-table-column prop="unitPrice" label="单价" width="100">
                  <template #default="{ row }">
                    ¥{{ row.unitPrice ? row.unitPrice.toFixed(2) : '0.00' }}
                  </template>
                </el-table-column>
                <el-table-column prop="totalPrice" label="总价" width="120">
                  <template #default="{ row }">
                    ¥{{ row.totalPrice ? row.totalPrice.toFixed(2) : '0.00' }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="采购状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getItemStatusType(row.status)" size="small">{{ getItemStatusLabel(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column v-if="userStore.user?.role === 'admin'" label="操作" width="200">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="handleUpdateItemStatus(row)" :disabled="!row.supplierId">更新状态</el-button>
                    <el-button type="success" size="small" @click="handleAssignSupplier(row)">分配供应商</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>

          <el-col :span="24">
            <div style="margin-top: 20px;">
              <h4>催促记录</h4>
              <el-table :data="urgeList" border style="width: 100%" v-if="urgeList.length > 0">
                <el-table-column prop="userName" label="催促人" width="100" />
                <el-table-column prop="urgeContent" label="催促内容" />
                <el-table-column prop="urgeTime" label="催促时间" width="180">
                  <template #default="{ row }">
                    {{ formatDateTime(row.urgeTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'pending' ? 'warning' : 'success'" size="small">
                      {{ row.status === 'pending' ? '待响应' : '已响应' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column v-if="userStore.user?.role === 'admin'" label="响应" width="250">
                  <template #default="{ row }">
                    <div v-if="row.status === 'responded'">
                      <div>{{ row.responseContent }}</div>
                      <div style="font-size: 12px; color: #909399;">{{ formatDateTime(row.responseTime) }}</div>
                    </div>
                    <el-button v-else type="primary" size="small" @click="handleRespondUrge(row)">响应</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无催促记录" :image-size="60" />
            </div>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statusVisible" title="更新采购状态" width="500px" append-to-body>
      <el-form :model="statusForm" :rules="statusRules" ref="statusFormRef" label-width="100px">
        <el-form-item label="采购状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择采购状态" style="width: 100%">
            <el-option label="待处理" value="pending" />
            <el-option label="采购中" value="purchasing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitStatus" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="itemStatusVisible" title="更新物料采购状态" width="500px" append-to-body>
      <el-form :model="itemStatusForm" label-width="120px">
        <el-form-item label="物料名称">
          <span>{{ itemStatusForm.materialName }}</span>
        </el-form-item>
        <el-form-item label="采购状态" prop="status">
          <el-select v-model="itemStatusForm.status" placeholder="请选择采购状态" style="width: 100%">
            <el-option label="待采购" value="pending" />
            <el-option label="采购中" value="purchasing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemStatusVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitItemStatus" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="urgeVisible" title="催促采购" width="500px" append-to-body>
      <el-form :model="urgeForm" :rules="urgeRules" ref="urgeFormRef" label-width="100px">
        <el-form-item label="催促内容" prop="urgeContent">
          <el-input
            v-model="urgeForm.urgeContent"
            type="textarea"
            placeholder="请输入催促内容"
            rows="4"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="urgeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitUrge" :loading="submitting">发送催促</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="respondVisible" title="响应催促" width="500px" append-to-body>
      <el-form :model="respondForm" :rules="respondRules" ref="respondFormRef" label-width="100px">
        <el-form-item label="响应内容" prop="responseContent">
          <el-input
            v-model="respondForm.responseContent"
            type="textarea"
            placeholder="请输入响应内容"
            rows="4"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="respondVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRespond" :loading="submitting">提交响应</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="supplierVisible" title="分配供应商" width="600px" append-to-body>
      <el-form :model="supplierForm" :rules="supplierRules" ref="supplierFormRef" label-width="100px">
        <el-form-item label="辅料名称">
          <span>{{ supplierForm.materialName }}</span>
        </el-form-item>
        <el-form-item label="当前供应商">
          <span>{{ supplierForm.currentSupplier || '-' }}</span>
        </el-form-item>
        <el-form-item label="分配供应商" prop="supplierId">
          <el-select v-model="supplierForm.supplierId" placeholder="请选择供应商" style="width: 100%">
            <el-option 
              v-for="supplier in supplierList" 
              :key="supplier.id" 
              :label="supplier.supplierName" 
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="supplierVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSupplier" :loading="submitting">确定分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { 
  getPurchaseOrderList, 
  getPurchaseOrderDetail, 
  updatePurchaseOrderStatus,
  updateOrderItemStatus,
  updateOrderItemSupplier,
  getUrgeListByOrderId,
  createUrge,
  respondToUrge
} from '@/api/purchaseOrder'
import { getSupplierList, getSuppliersByMaterialId } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const purchaseList = ref([])
const loading = ref(false)
const submitting = ref(false)

const filters = reactive({
  keyword: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const detailVisible = ref(false)
const currentPurchase = ref(null)
const urgeList = ref([])

const statusVisible = ref(false)
const statusFormRef = ref()
const statusForm = reactive({
  id: null,
  status: ''
})
const statusRules = {
  status: [{ required: true, message: '请选择采购状态', trigger: 'change' }]
}

const itemStatusVisible = ref(false)
const itemStatusForm = reactive({
  id: null,
  materialName: '',
  status: ''
})

const urgeVisible = ref(false)
const urgeFormRef = ref()
const urgeForm = reactive({
  orderId: null,
  urgeContent: ''
})
const urgeRules = {
  urgeContent: [{ required: true, message: '请输入催促内容', trigger: 'blur' }]
}

const respondVisible = ref(false)
const respondFormRef = ref()
const respondForm = reactive({
  urgeId: null,
  responseContent: ''
})
const respondRules = {
  responseContent: [{ required: true, message: '请输入响应内容', trigger: 'blur' }]
}

const supplierVisible = ref(false)
const supplierFormRef = ref()
const supplierList = ref([])
const supplierForm = reactive({
  id: null,
  materialName: '',
  currentSupplier: '',
  supplierId: null
})
const supplierRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }]
}

const loadPurchaseList = async () => {
  loading.value = true
  try {
    const params = {
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    
    if (userStore.user?.role !== 'admin' && userStore.user?.id) {
      params.creatorId = userStore.user.id
    }
    
    console.log('调用采购订单列表API，参数:', params)
    
    const res = await getPurchaseOrderList(params)
    console.log('采购订单列表API返回:', res)
    
    if (res.code === 200) {
      purchaseList.value = res.data.records || []
      pagination.total = res.data.total || 0
      console.log('采购订单列表:', purchaseList.value)
      console.log('总记录数:', pagination.total)
    } else {
      purchaseList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取采购订单列表失败:', error)
    ElMessage.error('获取采购订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewDetail = async (row) => {
  try {
    const res = await getPurchaseOrderDetail(row.id)
    if (res.code === 200) {
      currentPurchase.value = res.data
      
      const urgeRes = await getUrgeListByOrderId(row.id)
      if (urgeRes.code === 200) {
        urgeList.value = urgeRes.data || []
      }
      
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取采购详情失败:', error)
    ElMessage.error('获取采购详情失败')
  }
}

const handleUpdateStatus = (row) => {
  Object.assign(statusForm, {
    id: row.id,
    status: row.status
  })
  statusVisible.value = true
}

const handleSubmitStatus = async () => {
  if (!statusFormRef.value) return
  await statusFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await updatePurchaseOrderStatus(statusForm.id, statusForm.status)
        if (res.code === 200) {
          ElMessage.success('采购状态更新成功')
          statusVisible.value = false
          loadPurchaseList()
        }
      } catch (error) {
        console.error('更新采购状态失败:', error)
        ElMessage.error('更新采购状态失败')
      } finally {
        submitting.value = false
      }
    }
  })
}


const handleUpdateItemStatus = (row) => {
  if (!row.supplierId) {
    ElMessage.warning('请先分配供应商，再更新采购状态')
    return
  }
  Object.assign(itemStatusForm, {
    id: row.id,
    materialName: row.materialName,
    status: row.status
  })
  itemStatusVisible.value = true
}


const handleSubmitItemStatus = async () => {
  submitting.value = true
  try {
    const res = await updateOrderItemStatus(itemStatusForm.id, itemStatusForm.status)
    if (res.code === 200) {
      ElMessage.success('物料采购状态更新成功')
      itemStatusVisible.value = false
      handleViewDetail(currentPurchase.value)
    }
  } catch (error) {
    console.error('更新物料采购状态失败:', error)
    ElMessage.error('更新物料采购状态失败')
  } finally {
    submitting.value = false
  }
}

const handleUrge = (row) => {
  Object.assign(urgeForm, {
    orderId: row.id,
    urgeContent: ''
  })
  urgeVisible.value = true
}

const handleSubmitUrge = async () => {
  if (!urgeFormRef.value) return
  await urgeFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await createUrge(urgeForm.orderId, urgeForm.urgeContent)
        if (res.code === 200) {
          ElMessage.success('催促已发送')
          urgeVisible.value = false
        }
      } catch (error) {
        console.error('发送催促失败:', error)
        ElMessage.error('发送催促失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleRespondUrge = (row) => {
  Object.assign(respondForm, {
    urgeId: row.id,
    responseContent: ''
  })
  respondVisible.value = true
}

const handleSubmitRespond = async () => {
  if (!respondFormRef.value) return
  await respondFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await respondToUrge(respondForm.urgeId, respondForm.responseContent)
        if (res.code === 200) {
          ElMessage.success('响应已提交')
          respondVisible.value = false
          handleViewDetail(currentPurchase.value)
        }
      } catch (error) {
        console.error('提交响应失败:', error)
        ElMessage.error('提交响应失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const loadSuppliers = async (materialId = null) => {
  try {
    if (materialId) {
      // 加载可以提供该辅料的供应商
      const res = await getSuppliersByMaterialId(materialId)
      if (res.code === 200) {
        supplierList.value = res.data || []
      }
    } else {
      // 加载所有供应商
      const res = await getSupplierList()
      if (res.code === 200) {
        supplierList.value = res.data || []
      }
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  }
}

const handleAssignSupplier = async (row) => {
  await loadSuppliers(row.materialId)
  Object.assign(supplierForm, {
    id: row.id,
    materialId: row.materialId,
    materialName: row.materialName,
    currentSupplier: row.supplierName || '-',
    supplierId: null
  })
  supplierVisible.value = true
}

const handleSubmitSupplier = async () => {
  if (!supplierFormRef.value) return
  await supplierFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // Find the selected supplier to get the supplier name
        const selectedSupplier = supplierList.value.find(supplier => supplier.id === supplierForm.supplierId)
        if (!selectedSupplier) {
          ElMessage.error('请选择有效的供应商')
          return
        }
        
        const res = await updateOrderItemSupplier(supplierForm.id, supplierForm.supplierId, selectedSupplier.supplierName)
        if (res.code === 200) {
          ElMessage.success('供应商分配成功')
          supplierVisible.value = false
          handleViewDetail(currentPurchase.value)
        }
      } catch (error) {
        console.error('分配供应商失败:', error)
        ElMessage.error('分配供应商失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadPurchaseList()
}

const handleCurrentChange = (current) => {
  pagination.page = current
  loadPurchaseList()
}

const resetFilters = () => {
  Object.assign(filters, {
    keyword: '',
    status: ''
  })
  loadPurchaseList()
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const getPurchaseStatusType = (status) => {
  const map = {
    pending: 'info',
    purchasing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

const getPurchaseStatusLabel = (status) => {
  const map = {
    pending: '待处理',
    purchasing: '采购中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const getItemStatusType = (status) => {
  const map = {
    pending: 'info',
    purchasing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

const getItemStatusLabel = (status) => {
  const map = {
    pending: '待采购',
    purchasing: '采购中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

onMounted(() => {
  loadPurchaseList()
})
</script>

<style scoped>
.purchase-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status-tag {
  margin-right: 20px;
}

.purchase-detail {
  padding: 10px;
}
</style>
