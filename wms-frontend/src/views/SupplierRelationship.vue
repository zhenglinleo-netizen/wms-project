<template>
  <div class="supplier-relationship">
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="filter-form">
        <el-form-item label="辅料名称">
          <el-input v-model="searchForm.materialName" placeholder="输入辅料名称搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="handleAddRelationship">
            <el-icon><Plus /></el-icon>
            添加关系
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 辅料卡片列表 -->
    <el-row :gutter="20" class="material-card-list">
      <el-col :span="6" v-for="material in groupedMaterials" :key="material.id" style="margin-bottom: 20px;">
        <el-card :body-style="{ padding: '16px' }" class="material-card" shadow="hover">
          <div class="card-inner">
            <div class="image-wrapper">
              <el-image 
                :src="material.thumbnail || ''"
                fit="cover" 
                class="material-image"
                :alt="material.productName"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon class="image-icon"><Picture /></el-icon>
                    <div class="image-text">图片未存储</div>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="card-content">
              <div class="material-title" :title="material.productName">{{ material.productName }}</div>
              <div class="material-meta">
                <span class="material-code">编码: {{ material.productCode }}</span>
                <span class="supplier-count">供应商: {{ getMaterialSupplierCount(material.id) }}家</span>
                <div class="supplier-preview" v-if="getMaterialSupplierCount(material.id) > 0">
                  <span v-for="(supplier, index) in getMaterialSuppliersPreview(material.id)" :key="supplier.id" class="supplier-name">
                    {{ supplier.supplierName }}
                    <span v-if="index < getMaterialSuppliersPreview(material.id).length - 1">、</span>
                  </span>
                  <span v-if="getMaterialSupplierCount(material.id) > getMaterialSuppliersPreview(material.id).length">等</span>
                </div>
              </div>
              <div class="material-actions">
                <el-button type="primary" size="small" @click.stop="handleAddRelationshipToMaterial(material.id)">
                  <el-icon><Plus /></el-icon>
                  关联供应商
                </el-button>
                <el-button size="small" :icon="Link" type="info" @click="showMaterialRelationships(material.id)" />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 供应商关系弹窗 -->
    <el-dialog v-model="relationshipDialogVisible" :title="currentMaterialName + ' - 供应商关系'" width="70%" append-to-body>
      <div v-if="currentMaterialRelationships.length === 0" class="no-relationships">
        <el-empty description="暂无关联供应商" :image-size="80" />
        <el-button type="primary" style="margin-top: 20px;" @click="handleAddRelationshipToMaterial(currentMaterialId)">
          <el-icon><Plus /></el-icon>
          关联供应商
        </el-button>
      </div>
      <div v-else class="relationship-list">
        <el-card 
          v-for="relationship in currentMaterialRelationships" 
          :key="relationship.id" 
          class="relationship-card"
          shadow="hover"
        >
          <div class="relationship-content">
            <div class="relationship-header">
              <div class="supplier-info">
                <div class="supplier-name">{{ getSupplierName(relationship.supplierId) }}</div>
                <div class="supplier-code">编码: {{ getSupplierCode(relationship.supplierId) }}</div>
              </div>
              <div class="relationship-actions">
                <el-button type="primary" size="small" @click="handleEditRelationship(relationship)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteRelationship(relationship)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </div>
            <div class="relationship-details">
              <div class="detail-item">
                <span class="label">价格:</span>
                <span class="value">{{ relationship.price }}</span>
              </div>
              <div class="detail-item">
                <span class="label">最小起订量:</span>
                <span class="value">{{ relationship.minOrderQuantity }}</span>
              </div>
              <div class="detail-item">
                <span class="label">交货周期:</span>
                <span class="value">{{ relationship.leadTime }}天</span>
              </div>
              <div class="detail-item">
                <span class="label">质量评级:</span>
                <div class="rating">
                  <el-rate v-model="relationship.qualityRating" disabled :show-score="false" :texts="[]" />
                </div>
              </div>
              <div class="detail-item" v-if="relationship.remark">
                <span class="label">备注:</span>
                <span class="value">{{ relationship.remark }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-dialog>

    <!-- 添加/编辑关系弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加供应商-辅料关系' : '编辑供应商-辅料关系'"
      width="500px"
      append-to-body
    >
      <el-form :model="relationshipForm" :rules="relationshipRules" ref="relationshipFormRef" label-width="100px">
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="relationshipForm.supplierId" placeholder="请选择供应商">
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.supplierName"
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="辅料" prop="materialId">
          <el-select v-model="relationshipForm.materialId" placeholder="请选择辅料">
            <el-option
              v-for="material in materials"
              :key="material.id"
              :label="material.productName"
              :value="material.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number 
            v-model="relationshipForm.price" 
            :min="0" 
            :step="0.01" 
            :precision="2"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="最小起订量" prop="minOrderQuantity">
          <el-input-number 
            v-model="relationshipForm.minOrderQuantity" 
            :min="1" 
            :step="1"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="交货周期" prop="leadTime">
          <el-input-number 
            v-model="relationshipForm.leadTime" 
            :min="1" 
            :step="1"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="质量评级" prop="qualityRating">
          <el-rate v-model="relationshipForm.qualityRating" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="relationshipForm.remark" type="textarea" placeholder="请输入备注" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveRelationship">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Link, Picture } from '@element-plus/icons-vue'
import { getSupplierProducts, createSupplierProduct, updateSupplierProduct, deleteSupplierProduct, getSuppliers, getMaterials } from '@/api/supplier'

// 数据
const relationships = ref([])
const suppliers = ref([])
const materials = ref([])
const searchForm = reactive({
  materialName: ''
})

// 关系弹窗相关
const relationshipDialogVisible = ref(false)
const currentMaterialId = ref('')
const currentMaterialName = ref('')
const currentMaterialRelationships = ref([])



// 弹窗相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const relationshipForm = reactive({
  id: '',
  supplierId: '',
  materialId: '',
  price: 0,
  minOrderQuantity: 1,
  leadTime: 7,
  qualityRating: 3,
  remark: ''
})
const relationshipFormRef = ref()
const relationshipRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'blur' }],
  materialId: [{ required: true, message: '请选择辅料', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }, { type: 'number', min: 0, message: '价格必须大于等于0', trigger: 'blur' }]
}



// 计算属性：按辅料分组
const groupedMaterials = computed(() => {
  let result = materials.value
  if (searchForm.materialName) {
    result = result.filter(material => 
      material.productName.toLowerCase().includes(searchForm.materialName.toLowerCase())
    )
  }
  return result
})

// 生命周期
onMounted(() => {
  loadSuppliers()
  loadMaterials()
  loadRelationships()
})

// 加载数据
const loadSuppliers = async () => {
  try {
    const response = await getSuppliers()
    suppliers.value = response.data
  } catch (error) {
    ElMessage.error('加载供应商失败')
  }
}

const loadMaterials = async () => {
  try {
    const response = await getMaterials()
    materials.value = response.data.map(item => {
      let imageUrl = item.imageUrl
      // 过滤掉错误的图片URL（如"上传成功"）
      if (!imageUrl || imageUrl === '上传成功' || imageUrl === '[]' || typeof imageUrl !== 'string' || !imageUrl.startsWith('http')) {
        imageUrl = null
      } else {
        // 提取文件名（忽略bucket名称）
        const lastSlashIndex = imageUrl.lastIndexOf('/')
        if (lastSlashIndex !== -1) {
          let filename = imageUrl.substring(lastSlashIndex + 1)
          // 使用后端接口获取图片，避免MinIO认证问题
          imageUrl = `/api/file/get-image?filename=${filename}`
        }
      }
      return {
        ...item,
        thumbnail: imageUrl // 确保thumbnail字段可用
      }
    })
  } catch (error) {
    console.error('加载辅料失败:', error)
    ElMessage.error('加载辅料失败')
  }
}

const loadRelationships = async () => {
  try {
    const response = await getSupplierProducts()
    // 转换后端字段名到前端字段名
    relationships.value = response.data.map(item => ({
      id: item.id,
      supplierId: item.supplierId,
      materialId: item.productId,
      price: item.price,
      minOrderQuantity: 1, // 设置默认值
      leadTime: 7, // 设置默认值
      qualityRating: 3, // 设置默认值
      remark: '' // 设置默认值
    }))
  } catch (error) {
    ElMessage.error('加载关系数据失败')
  }
}

// 搜索和重置
const handleSearch = () => {
  // 搜索逻辑在computed属性中处理
}

const handleReset = () => {
  searchForm.materialName = ''
}

// 辅助函数：获取材料名称
const getMaterialName = (materialId) => {
  const material = materials.value.find(m => m.id === materialId)
  return material ? material.productName : '未知辅料'
}

// 辅助函数：获取材料编码
const getMaterialCode = (materialId) => {
  const material = materials.value.find(m => m.id === materialId)
  return material ? material.productCode : '未知编码'
}

// 辅助函数：获取材料缩略图
const getMaterialThumbnail = (materialId) => {
  const material = materials.value.find(m => m.id === materialId)
  return material ? material.thumbnail : ''
}

// 辅助函数：获取供应商名称
const getSupplierName = (supplierId) => {
  const supplier = suppliers.value.find(s => s.id === supplierId)
  return supplier ? supplier.supplierName : '未知供应商'
}

// 辅助函数：获取供应商编码
const getSupplierCode = (supplierId) => {
  const supplier = suppliers.value.find(s => s.id === supplierId)
  return supplier ? supplier.supplierCode : '未知编码'
}

// 辅助函数：获取辅料的供应商数量
const getMaterialSupplierCount = (materialId) => {
  return relationships.value.filter(r => r.materialId === materialId).length
}

// 辅助函数：获取辅料的所有供应商关系
const getMaterialSuppliers = (materialId) => {
  return relationships.value.filter(r => r.materialId === materialId)
}

// 辅助函数：获取辅料的供应商预览（最多2个）
const getMaterialSuppliersPreview = (materialId) => {
  const materialSuppliers = relationships.value.filter(r => r.materialId === materialId)
  return materialSuppliers.slice(0, 2).map(relationship => {
    const supplier = suppliers.value.find(s => s.id === relationship.supplierId)
    return supplier || { id: relationship.supplierId, supplierName: '未知供应商' }
  })
}

// 弹窗操作
const handleAddRelationship = () => {
  dialogType.value = 'add'
  Object.assign(relationshipForm, {
    id: '',
    supplierId: '',
    materialId: '',
    price: 0,
    minOrderQuantity: 1,
    leadTime: 7,
    qualityRating: 3,
    remark: ''
  })
  dialogVisible.value = true
}

// 为指定辅料添加关系
const handleAddRelationshipToMaterial = (materialId) => {
  dialogType.value = 'add'
  Object.assign(relationshipForm, {
    id: '',
    supplierId: '',
    materialId: materialId,
    price: 0,
    minOrderQuantity: 1,
    leadTime: 7,
    qualityRating: 3,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEditRelationship = (row) => {
  dialogType.value = 'edit'
  Object.assign(relationshipForm, {
    id: row.id,
    supplierId: row.supplierId,
    materialId: row.materialId,
    price: row.price,
    minOrderQuantity: row.minOrderQuantity,
    leadTime: row.leadTime,
    qualityRating: row.qualityRating,
    remark: row.remark
  })
  dialogVisible.value = true
}

const handleSaveRelationship = async () => {
  if (!relationshipFormRef.value) return
  await relationshipFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 转换前端数据结构为后端期望的格式
        const relationshipData = {
          id: relationshipForm.id,
          supplierId: relationshipForm.supplierId,
          productId: relationshipForm.materialId,
          price: relationshipForm.price,
          status: 1 // 设置默认状态为激活
        }
        
        if (dialogType.value === 'add') {
          await createSupplierProduct(relationshipData)
          ElMessage.success('添加成功')
        } else {
          await updateSupplierProduct(relationshipData)
          ElMessage.success('编辑成功')
        }
        dialogVisible.value = false
        loadRelationships()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '编辑失败')
      }
    }
  })
}

const handleDeleteRelationship = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条关系吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteSupplierProduct(row.id)
    ElMessage.success('删除成功')
    
    // 重新加载关系数据
    await loadRelationships()
    
    // 更新当前显示的关系列表
    currentMaterialRelationships.value = relationships.value.filter(r => r.materialId === currentMaterialId.value)
  } catch (error) {
    // 忽略取消操作的错误
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 显示辅料的供应商关系
const showMaterialRelationships = (materialId) => {
  currentMaterialId.value = materialId
  const material = materials.value.find(m => m.id === materialId)
  currentMaterialName.value = material ? material.productName : '未知辅料'
  currentMaterialRelationships.value = relationships.value.filter(r => r.materialId === materialId)
  relationshipDialogVisible.value = true
}


</script>

<style scoped>
.supplier-relationship {
  padding: 10px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  align-items: center;
  gap: 10px;
}

.material-card-list {
  margin-top: 20px;
}

.material-card {
  transition: all 0.3s ease;
  border-radius: 8px !important;
  height: 200px;
  overflow: hidden !important;
}

.material-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-inner {
  display: flex;
  height: 100%;
  gap: 12px;
}

.image-wrapper {
  width: 80px;
  height: 100%;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 4px;
}

.material-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.material-card:hover .material-image {
  transform: scale(1.05);
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.image-icon {
  font-size: 24px;
  color: #909399;
  margin-bottom: 8px;
}

.image-text {
  font-size: 12px;
  color: #909399;
  text-align: center;
  padding: 0 8px;
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.material-title {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #303133;
}

.material-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
}

.material-code {
  color: #606266;
}

.supplier-count {
  color: #1976d2;
  font-weight: 500;
}

.supplier-preview {
  font-size: 12px;
  color: #606266;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
}

.material-actions {
  display: flex;
  gap: 8px;
  margin-top: 2px;
}

.material-actions :deep(.el-button) {
  font-size: 12px;
  padding: 4px 10px;
}

.supplier-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 80px;
  display: inline-block;
  vertical-align: middle;
}

.relationship-card {
  transition: all 0.3s ease;
  border-radius: 6px !important;
  overflow: hidden !important;
}

.relationship-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.relationship-content {
  padding: 12px;
}

.relationship-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.relationship-header .supplier-info {
  flex: 1;
}

.relationship-header .supplier-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.relationship-header .supplier-code {
  font-size: 12px;
  color: #606266;
}

.relationship-actions {
  display: flex;
  gap: 6px;
}

.relationship-actions :deep(.el-button) {
  font-size: 12px;
  padding: 4px 10px;
}

.relationship-details {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.detail-item .label {
  color: #606266;
  flex-shrink: 0;
}

.detail-item .value {
  color: #303133;
  font-weight: 500;
}

.rating {
  display: flex;
  align-items: center;
}

.rating :deep(.el-rate__icon) {
  font-size: 14px;
}

.no-relationships {
  text-align: center;
  padding: 40px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-form {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .image-wrapper {
    height: 140px;
  }
  
  .card-content {
    padding: 12px;
  }
  
  .material-title {
    font-size: 14px;
  }
}
</style>