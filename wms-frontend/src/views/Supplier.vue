<template>
  <div class="supplier-library">
    <!-- 只有当没有子路由时才显示供应商库内容 -->
    <template v-if="$route.path === '/supplier'">
      <!-- 搜索与筛选栏 -->
      <el-card class="filter-card">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="关键词">
            <el-input v-model="filters.keyword" placeholder="名称/编码/联系人" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadSupplierList">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="warning" :icon="Plus" @click="handleCreateSupplier">添加供应商</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 供应商列表 -->
      <el-row :gutter="20" class="supplier-list">
        <el-col :span="6" v-for="supplier in supplierList" :key="supplier.id" style="margin-bottom: 20px;">
          <el-card :body-style="{ padding: '0px' }" class="supplier-card" shadow="hover">
            <div class="card-content">
              <div class="supplier-header">
                <div class="supplier-title" :title="supplier.supplierName">{{ supplier.supplierName }}</div>
                <div class="supplier-code">{{ supplier.supplierCode }}</div>
              </div>
              <div class="supplier-info">
                <div class="info-item">
                  <span class="label">联系人:</span>
                  <span class="value">{{ supplier.contactPerson }}</span>
                </div>
                <div class="info-item">
                  <span class="label">电话:</span>
                  <span class="value">{{ supplier.phone }}</span>
                </div>
                <div class="info-item">
                  <span class="label">合作开始:</span>
                  <span class="value">{{ formatDate(supplier.cooperationStartTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">工艺匹配:</span>
                  <span class="value">{{ supplier.processModuleMatch || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">核心擅长:</span>
                  <span class="value">{{ supplier.coreExpertise || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">供应商角色:</span>
                  <span class="value">{{ supplier.supplierRole || '未设置' }}</span>
                </div>
              </div>
              <div class="supplier-footer">
                <div class="supplier-meta">
                  <el-tag :type="supplier.status === 1 ? 'success' : 'danger'">
                    {{ supplier.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                  <span class="rating">评分: {{ supplier.rating }}/5</span>
                </div>
                <div class="supplier-actions">
                  <el-button size="small" type="primary" @click="handleEditSupplier(supplier)">编辑</el-button>
                  <el-button size="small" type="danger" @click="handleDeleteSupplier(supplier)">删除</el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 添加/编辑供应商弹窗 -->
      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应商' : '添加供应商'" width="800px" append-to-body>
        <el-form :model="supplierForm" :rules="supplierRules" ref="supplierFormRef" label-width="120px">
          <el-form-item label="供应商名称" prop="supplierName">
            <el-input v-model="supplierForm.supplierName" placeholder="请输入供应商名称" />
          </el-form-item>
          <el-form-item label="供应商编码" prop="supplierCode" v-if="isEdit">
            <el-input v-model="supplierForm.supplierCode" placeholder="供应商编码" disabled />
          </el-form-item>
          <el-form-item label="供应商编码" v-else>
            <el-input v-model="supplierForm.supplierCode" placeholder="系统自动生成" disabled />
          </el-form-item>
          <el-form-item label="联系人" prop="contactPerson">
            <el-input v-model="supplierForm.contactPerson" placeholder="请输入联系人" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="supplierForm.phone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="电子邮箱" prop="email">
            <el-input v-model="supplierForm.email" placeholder="请输入电子邮箱" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="supplierForm.address" placeholder="请输入地址" type="textarea" rows="2" />
          </el-form-item>
          <el-form-item label="合作开始时间">
            <el-date-picker
              v-model="supplierForm.cooperationStartTime"
              type="date"
              placeholder="请选择合作开始时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="工艺模块匹配">
            <el-input v-model="supplierForm.processModuleMatch" placeholder="请输入工艺模块匹配" />
          </el-form-item>
          <el-form-item label="核心擅长工艺">
            <el-input v-model="supplierForm.coreExpertise" placeholder="请输入核心擅长工艺" />
          </el-form-item>
          <el-form-item label="不擅长/风险">
            <el-input v-model="supplierForm.weaknesses" placeholder="请输入不擅长/风险" />
          </el-form-item>
          <el-form-item label="供应商角色">
            <el-input v-model="supplierForm.supplierRole" placeholder="请输入供应商角色" />
          </el-form-item>
          <el-form-item label="评分">
            <el-slider v-model="supplierForm.rating" :min="1" :max="5" show-input />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="supplierForm.status" active-value="1" inactive-value="0" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="supplierForm.remark" placeholder="请输入备注" type="textarea" rows="3" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </template>
      </el-dialog>
    </template>
    
    <!-- 显示子路由内容 -->
    <router-view v-else />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getSupplierList,
  getSupplierDetail,
  createSupplier,
  updateSupplier,
  deleteSupplier
} from '@/api/supplier'

const supplierList = ref([])
const loading = ref(false)
const submitting = ref(false)
const filters = reactive({
  keyword: '',
  status: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const supplierFormRef = ref()

const supplierForm = reactive({
  id: null,
  supplierName: '',
  supplierCode: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  cooperationStartTime: null,
  processModuleMatch: '',
  coreExpertise: '',
  weaknesses: '',
  supplierRole: '',
  rating: 5,
  status: 1,
  remark: ''
})

const supplierRules = {
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

// 加载供应商列表
const loadSupplierList = async () => {
  loading.value = true
  try {
    const res = await getSupplierList(filters)
    if (res.code === 200) {
      supplierList.value = res.data || []
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.status = ''
  loadSupplierList()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 处理添加供应商
const handleCreateSupplier = () => {
  isEdit.value = false
  // 重置表单
  Object.assign(supplierForm, {
    id: null,
    supplierName: '',
    supplierCode: '',
    contactPerson: '',
    phone: '',
    email: '',
    address: '',
    cooperationStartTime: null,
    processModuleMatch: '',
    coreExpertise: '',
    weaknesses: '',
    supplierRole: '',
    rating: 5,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

// 处理编辑供应商
const handleEditSupplier = async (row) => {
  isEdit.value = true
  try {
    const res = await getSupplierDetail(row.id)
    if (res.code === 200) {
      Object.assign(supplierForm, res.data)
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取供应商详情失败:', error)
    ElMessage.error('获取供应商详情失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!supplierFormRef.value) return
  await supplierFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        let res
        if (isEdit.value) {
          res = await updateSupplier(supplierForm)
        } else {
          res = await createSupplier(supplierForm)
        }

        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '供应商更新成功' : '供应商添加成功')
          dialogVisible.value = false
          loadSupplierList()
        }
      } catch (error) {
        console.error(isEdit.value ? '更新供应商失败:' : '添加供应商失败:', error)
        ElMessage.error(isEdit.value ? '更新供应商失败' : '添加供应商失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 处理删除供应商
const handleDeleteSupplier = (supplier) => {
  ElMessageBox.confirm('确定要删除该供应商吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 二次确认
    ElMessageBox.confirm('此操作不可恢复，确定要删除吗？', '最终确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'danger'
    }).then(async () => {
      try {
        const res = await deleteSupplier(supplier.id)
        if (res.code === 200) {
          ElMessage.success('供应商删除成功')
          loadSupplierList()
        }
      } catch (error) {
        console.error('删除供应商失败:', error)
        ElMessage.error('删除供应商失败')
      }
    }).catch(() => {
      // 取消二次确认
    })
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  loadSupplierList()
})
</script>

<style scoped>
.supplier-library {
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.supplier-card {
  transition: all 0.3s;
  height: 100%;
}
.supplier-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}
.card-content {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.supplier-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}
.supplier-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.supplier-code {
  font-size: 12px;
  color: #909399;
  background-color: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}
.supplier-info {
  flex: 1;
  margin-bottom: 15px;
}
.info-item {
  margin-bottom: 8px;
  font-size: 13px;
}
.label {
  color: #909399;
  margin-right: 8px;
  min-width: 70px;
  display: inline-block;
}
.value {
  color: #303133;
  max-width: 150px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: top;
}
.supplier-footer {
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}
.supplier-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.rating {
  font-size: 12px;
  color: #606266;
}
.supplier-actions {
  display: flex;
  gap: 10px;
}
</style>