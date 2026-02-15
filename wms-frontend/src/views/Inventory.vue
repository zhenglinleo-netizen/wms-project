<template>
  <div class="inventory-container">
    <el-card>
      <template #header>
        <span>库存监控</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="辅料">
          <el-input v-model="searchForm.productName" placeholder="请输入辅料名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="productCode" label="辅料编码" width="150" align="center" />
        <el-table-column prop="productName" label="辅料名称" width="200" align="center" />
        <el-table-column prop="imageUrl" label="缩略图" width="120" align="center">
          <template #default="scope">
            <el-image 
              :src="scope.row.imageUrl || 'https://via.placeholder.com/60'" 
              fit="cover" 
              style="width: 60px; height: 60px; border-radius: 4px; margin: 0 auto; display: block;"
              :preview-src-list="[scope.row.imageUrl || 'https://via.placeholder.com/60']"
              preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column label="库存数量" width="120" align="center">
          <template #default="scope">
            <span style="font-weight: bold;">{{ scope.row.quantity || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template #default="scope">
            <div style="display: flex; align-items: center; justify-content: center; gap: 10px;">
              <el-input-number 
                v-model="scope.row.operationQuantity" 
                :min="0" 
                :precision="0" 
                style="width: 120px; flex-shrink: 0;"
              />
              <el-button 
                type="success" 
                size="small" 
                @click="addInventory(scope.row)"
              >
                增加
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="reduceInventory(scope.row)"
                :disabled="scope.row.operationQuantity > scope.row.quantity"
              >
                减少
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="avgPrice" label="平均单价" width="120" align="center">
          <template #default="scope">
            ¥{{ scope.row.avgPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryList, updateInventory } from '@/api/inventory'
import { getProductList } from '@/api/product'

const tableData = ref([])


const searchForm = reactive({
  productName: ''
})

const loadData = async () => {
  try {
    // 获取所有库存数据
    const inventoryRes = await getInventoryList()
    
    // 获取所有辅料数据，只显示状态为上架（status=1）的辅料
    const productRes = await getProductList()
    const allProducts = (productRes.data || []).filter(product => product.status === 1)
    const inventoryData = inventoryRes.data || []
    
    // 创建库存数据的映射，方便查询
    const inventoryMap = new Map()
    inventoryData.forEach(item => {
      // 使用产品编码或物料编码作为键，确保能正确匹配
      const productCode = item.productCode || item.materialCode || item.product_code || item.material_code
      if (productCode) {
        inventoryMap.set(productCode, item)
      }
    })
    
    // 合并所有辅料和库存数据
    let mergedData = allProducts.map(product => {
      // 查找对应库存数据
      const inventory = inventoryMap.get(product.productCode)
      
      // 处理图片URL
      let imageUrl = product.image || product.imageUrl || product.image_url || ''
      if (imageUrl && typeof imageUrl === 'string') {
        // 过滤掉错误的图片URL（如"上传成功"）
        if (imageUrl === '上传成功' || imageUrl === '[]' || !imageUrl.startsWith('http')) {
          imageUrl = ''
        } else {
          // 提取文件名（忽略bucket名称）
          const lastSlashIndex = imageUrl.lastIndexOf('/')
          if (lastSlashIndex !== -1) {
            const filename = imageUrl.substring(lastSlashIndex + 1)
            // 使用后端接口获取图片，避免MinIO认证问题
            imageUrl = `/api/file/get-image?filename=${filename}`
          }
        }
      }
      
      return {
        // 产品基本信息
        productCode: product.productCode,
        productName: product.productName,
        imageUrl: imageUrl,
        // 库存信息，默认值确保即使没有库存也能显示
        quantity: inventory?.quantity || inventory?.amount || inventory?.stock || inventory?.total || 0,
        avgPrice: inventory?.avgPrice || inventory?.price || 0,
        updateTime: inventory?.updateTime || inventory?.update_time || '',
        // 操作数量，用于存储要增减的库存数量，初始化为0
        operationQuantity: 0
      }
    })
    
    // 如果输入了商品名称，进行过滤
    if (searchForm.productName) {
      mergedData = mergedData.filter(item => 
        item.productName?.includes(searchForm.productName)
      )
    }
    
    tableData.value = mergedData
  } catch (error) {
    console.error('加载库存数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}



const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.warehouseId = null
  searchForm.productName = ''
  loadData()
}

// 增加库存数量
const addInventory = async (row) => {
  try {
    if (row.operationQuantity <= 0) {
      ElMessage.warning('请输入有效的增加数量')
      return
    }
    
    // 计算新的库存数量
    const newQuantity = row.quantity + Number(row.operationQuantity)
    
    // 调用API更新库存，确保传递正确的参数格式
    await updateInventory({
      productCode: row.productCode,
      materialCode: row.productCode, // 兼容物料编码
      quantity: newQuantity, // 确保是数字类型
      price: row.avgPrice // 传递平均单价
    })
    
    ElMessage.success('库存增加成功')
    // 刷新数据以确保一致性
    loadData()
  } catch (error) {
    console.error('增加库存失败:', error)
    ElMessage.error('库存增加失败')
  }
}

// 减少库存数量
const reduceInventory = async (row) => {
  try {
    if (row.operationQuantity <= 0) {
      ElMessage.warning('请输入有效的减少数量')
      return
    }
    
    if (row.operationQuantity > row.quantity) {
      ElMessage.warning('减少数量不能大于当前库存数量')
      return
    }
    
    // 计算新的库存数量
    const newQuantity = row.quantity - Number(row.operationQuantity)
    
    // 调用API更新库存，确保传递正确的参数格式
    await updateInventory({
      productCode: row.productCode,
      materialCode: row.productCode, // 兼容物料编码
      quantity: newQuantity, // 确保是数字类型
      price: row.avgPrice // 传递平均单价
    })
    
    ElMessage.success('库存减少成功')
    // 刷新数据以确保一致性
    loadData()
  } catch (error) {
    console.error('减少库存失败:', error)
    ElMessage.error('库存减少失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.inventory-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>




