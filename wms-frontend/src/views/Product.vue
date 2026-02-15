<template>
  <div class="product-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>辅料管理</span>
          <el-button type="primary" @click="handleAdd">新增辅料</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="辅料编码">
          <el-input v-model="searchForm.productCode" placeholder="请输入辅料编码" clearable />
        </el-form-item>
        <el-form-item label="辅料名称">
          <el-input v-model="searchForm.productName" placeholder="请输入辅料名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="上架" value="1" />
            <el-option label="待审核" value="2" />
            <el-option label="下架" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="productCode" label="辅料编码" width="150" />
        <el-table-column prop="productName" label="辅料名称" width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="type" label="具体类型" width="120" />
        <el-table-column prop="style" label="风格" width="120" />
        <el-table-column prop="specification" label="规格" width="150" />
        <el-table-column prop="stock" label="库存数量" width="120">
          <template #default="{ row }">
            <span :style="{ color: row.stock <= 0 ? 'red' : row.stock < 100 ? 'orange' : '' }">
              {{ row.stock || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status !== 1" type="success" size="small" @click="handleStatusChange(row, 1)">上架</el-button>
            <el-button v-if="row.status !== 0" type="warning" size="small" @click="handleStatusChange(row, 0)">下架</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" append-to-body>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="辅料编码" prop="productCode">
          <el-input v-model="form.productCode" :disabled="isEdit" />        </el-form-item>
        <el-form-item label="辅料名称" prop="productName">
          <el-input v-model="form.productName" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="c in categoryList"
              :key="c.id"
              :label="c.categoryName"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="风格">
          <el-select v-model="form.style" placeholder="请选择风格" style="width: 100%" filterable allow-create>
            <el-option label="复古" value="复古" />
            <el-option label="现代" value="现代" />
            <el-option label="简约" value="简约" />
            <el-option label="华丽" value="华丽" />
            <el-option label="休闲" value="休闲" />
            <el-option label="商务" value="商务" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体类型">
          <el-select v-model="form.type" placeholder="请选择具体类型" style="width: 100%" filterable allow-create>
            <el-option label="拉链" value="拉链" />
            <el-option label="纽扣" value="纽扣" />
            <el-option label="线" value="线" />
            <el-option label="衬里" value="衬里" />
            <el-option label="花边" value="花边" />
            <el-option label="标签" value="标签" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.specification" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, searchProducts, saveProduct, updateProduct, deleteProduct } from '@/api/product'
import { getCategoryList } from '@/api/category'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增辅料')
const isEdit = ref(false)
const formRef = ref()

const searchForm = reactive({
  productCode: '',
  productName: '',
  status: ''
})

const form = reactive({
  id: null,
  productCode: '',
  productName: '',
  category: '',
  categoryId: null,
  style: '',
  type: '',
  specification: '',
  unit: '件',
  price: 0,
  description: '',
  status: 1
})

const rules = {
  productCode: [{ required: true, message: '请输入辅料编码', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入辅料名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const categoryList = ref([])
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryList.value = res.data || []
  } catch (error) {
    // ignore
  }
}

const loadData = async () => {
  try {
    let res
    
    // 构建搜索参数，包括状态
    const searchParams = {
      productCode: searchForm.productCode,
      productName: searchForm.productName
    }
    
    // 只有当status有值时才添加到搜索参数中
    if (searchForm.status !== '') {
      searchParams.status = searchForm.status
    }
    
    if (searchForm.productCode || searchForm.productName || searchForm.status !== '') {
      res = await searchProducts(searchParams)
    } else {
      res = await getProductList()
    }
    
    let data = res.data || []
    
    // 简化数据处理，直接使用数据库返回的字段
    data = data.map(item => ({
      ...item,
      // 确保分类字段存在
      category: item.category || item.categoryName || '',
      // 直接使用数据库返回的type和style字段
      type: item.type || '',
      style: item.style || '',
      // 确保库存数量字段存在，处理不同可能的字段名
      stock: item.stock || item.quantity || 0
    }))
    
    tableData.value = data
  } catch (error) {
    console.error('加载辅料数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.productCode = ''
  searchForm.productName = ''
  searchForm.status = ''
  loadData()
}

const handleAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增辅料'
    resetForm()
    dialogVisible.value = true
  }

  const handleEdit = (row) => {
    isEdit.value = true
    dialogTitle.value = '编辑辅料'
    Object.assign(form, row)
    if (row.category && categoryList.value.length) {
      const cat = categoryList.value.find(c => c.categoryName === row.category)
      form.categoryId = cat ? cat.id : null
    }
    dialogVisible.value = true
  }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1:
      return '上架'
    case 2:
      return '待审核'
    case 0:
    default:
      return '下架'
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'info'
    case 0:
    default:
      return 'danger'
  }
}

// 处理状态变更
const handleStatusChange = async (row, newStatus) => {
  try {
    // 当状态为待审核且用户点击上架操作时，检查单价是否大于0
    if (row.status === 2 && newStatus === 1) {
      if (!row.price || row.price <= 0) {
        ElMessage.error('待审核的辅料上架前必须设置非零单价')
        return
      }
    }
    
    const updatedProduct = {
      ...row,
      status: newStatus
    }
    await updateProduct(updatedProduct)
    ElMessage.success(`已${getStatusText(newStatus)}`)
    loadData()
  } catch (error) {
    console.error('状态变更失败:', error)
    ElMessage.error('状态变更失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 当状态为待审核且设置了单价时，自动变更为上架状态
        if (form.status === 2 && form.price > 0) {
          form.status = 1
        }
        
        if (isEdit.value) {
          await updateProduct(form)
          ElMessage.success('更新成功')
        } else {
          await saveProduct(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      }
    }
  })
}

const resetForm = () => {
  form.id = null
  form.productCode = ''
  form.productName = ''
  form.category = ''
  form.categoryId = null
  form.style = ''
  form.type = ''
  form.specification = ''
  form.unit = '件'
  form.price = 0
  form.description = ''
  // 不再设置status字段，状态通过操作按钮控制
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.product-container {
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
</style>




