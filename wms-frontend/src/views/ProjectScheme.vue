<template>
  <div class="project-scheme">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>项目方案管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAddProject">新建项目</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="filters" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="项目名称/客户" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 项目列表 -->
      <el-table 
        :data="projectList" 
        border 
        style="width: 100%" 
        v-loading="loading"
        :expanded-row-keys="expandedRowKeys"
        @expand-change="handleExpandChange"
      >
        <el-table-column type="expand">
          <template #default="props">
            <div class="scheme-list">
              <h4>设计方案 ({{ props.row.schemes?.length || 0 }})</h4>
              <el-table :data="props.row.schemes || []" size="small" border>
                <el-table-column prop="schemeName" label="方案名称" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getSchemeStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="关联辅料数" width="100">
                  <template #default="{ row }">
                    {{ row.materials?.length || 0 }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template #default="{ row }">
                    <el-button link type="primary" size="small" @click="handleSchemeDetail(row)">查看详情</el-button>
                    <el-button link type="danger" size="small" @click="handleDeleteScheme(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div style="margin-top: 10px">
                <el-button type="primary" size="small" :icon="Plus" @click="handleAddScheme(props.row)">添加新方案</el-button>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="周期" width="220">
          <template #default="{ row }">
            {{ row.startDate }} ~ {{ row.endDate }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 项目编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px" @submit.prevent="handleSubmit">
        <el-form-item label="项目名称" required>
          <el-input v-model="form.projectName" />
        </el-form-item>
        <el-form-item label="客户名称" :required="userStore.user?.role === 'admin'">
          <el-input v-model="form.clientName" :readonly="userStore.user?.role !== 'admin'" :placeholder="userStore.user?.role === 'admin' ? '请输入客户名称' : ''" />
        </el-form-item>
        <el-form-item label="起止日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态" v-if="form.id">
          <el-select v-model="form.status">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 方案添加弹窗 -->
    <el-dialog v-model="schemeDialogVisible" title="添加设计方案" width="400px">
      <el-form :model="schemeForm" label-width="80px" @submit.prevent="submitScheme">
        <el-form-item label="方案名称" required>
          <el-input v-model="schemeForm.schemeName" placeholder="如：方案A-简约风" />        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="schemeForm.description" type="textarea" />        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="schemeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitScheme" :loading="submitting">确定</el-button>      </template>
    </el-dialog>

    <!-- 方案详情弹窗 -->
    <el-dialog v-model="schemeDetailDialogVisible" title="方案详情" width="800px" append-to-body>
      <div v-if="currentScheme" class="scheme-detail">
        <h3 style="margin-bottom: 5px;">{{ currentScheme.schemeName }}</h3>
        <p class="scheme-status">状态：<el-tag :type="getSchemeStatusType(currentScheme.status)">{{ currentScheme.status }}</el-tag></p>
        <p class="scheme-note" v-if="currentScheme.note">{{ currentScheme.note }}</p>
        
        <el-divider content-position="left" style="margin: 10px 0;">辅料列表</el-divider>
        
        <!-- 卡片式辅料列表 -->
        <div v-if="currentScheme.materials && currentScheme.materials.length > 0" class="material-card-list">
          <el-card v-for="(material, index) in currentScheme.materials" :key="index" class="material-card">
            <div class="card-content-wrapper">
              <!-- 辅料图片 -->
              <div class="material-image">
                <el-image 
                  :src="material.image || 'https://via.placeholder.com/120x120?text=No+Image'" 
                  fit="cover" 
                  style="width: 120px; height: 120px; border-radius: 8px;"
                  @error="(e) => { e.target.src = 'https://via.placeholder.com/120x120?text=No+Image' }"
                />
              </div>
              <div class="material-content">
                <div class="card-header-content">
                  <span class="material-name">{{ material.productName }}</span>
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="handleRemoveMaterialFromScheme(index)"
                    :disabled="currentScheme.status === '已提交'"
                  >删除</el-button>
                </div>
                <div class="material-info">
                    <div class="info-row">
                      <div class="info-item">
                        <span class="label">编码：</span>
                        <span class="value">{{ material.productCode }}</span>
                      </div>
                      <div class="info-item">
                        <span class="label">分类：</span>
                        <span class="value">{{ getCategoryName(material.category) }}</span>
                      </div>
                      <div class="info-item">
                        <span class="label">规格：</span>
                        <span class="value">{{ material.specification || '-' }}</span>
                      </div>
                      <div class="info-item">
                        <span class="label">单位：</span>
                        <span class="value">{{ material.unit }}</span>
                      </div>
                    </div>
                    <div class="info-row">
                      <div class="info-item">
                        <span class="label">单价：</span>
                        <span class="value price">¥{{ material.price }}</span>
                      </div>
                      <div class="info-item quantity-control">
                        <span class="label">数量：</span>
                        <el-input-number 
                          v-model="material.quantity" 
                          :min="1" 
                          :step="1" 
                          :integer="true"
                          @change="handleUpdateQuantity(material)"
                          :disabled="currentScheme.status === '已提交'"
                          style="width: 120px"
                        />
                      </div>
                    </div>
                  </div>
              </div>
            </div>
          </el-card>
        </div>
        
        <el-empty v-else description="暂无辅料" :image-size="100" />
      </div>
      <template #footer>
        <el-button @click="schemeDetailDialogVisible = false">关闭</el-button>
        <el-button type="warning" @click="handleFinalizeScheme" :disabled="!currentScheme?.materials || currentScheme.materials.length === 0" v-if="currentScheme?.status !== '已确定' && currentScheme?.status !== '已提交'">定版方案</el-button>
        <el-button type="primary" @click="handleSubmitRequirement" v-if="currentScheme?.status === '已确定'">提交采购需求</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getProjectList, createProject, updateProject, deleteProject, addScheme, updateScheme, getSchemeListByProjectId, deleteScheme, getSchemeItemListBySchemeId, updateSchemeItem, deleteSchemeItem } from '@/api/project'
import { getCategoryList } from '@/api/category'
import { createRequirementFromScheme, submitRequirementForAudit } from '@/api/requirement'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const projectList = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const schemeDialogVisible = ref(false)
const currentProject = ref(null)

// 保存当前展开的行ID
const expandedRowKeys = ref([])

// 分类列表，用于将分类ID转换为分类名称
const categoryList = ref([])
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categoryList.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 获取当前用户信息
const userStore = useUserStore()

const filters = reactive({
  keyword: '',
  status: '',
  userId: userStore.user?.id || 1 // 默认用户ID为1
})

// 监听用户变化，更新userId
const updateUserId = () => {
  filters.userId = userStore.user?.userId || 1
}

const form = reactive({
  id: null,
  projectName: '',
  clientName: '',
  description: '',
  status: '未开始',
  startDate: '',
  endDate: ''
})

const dateRange = computed({
  get: () => [form.startDate, form.endDate],
  set: (val) => {
    if (val) {
      form.startDate = val[0]
      form.endDate = val[1]
    } else {
      form.startDate = ''
      form.endDate = ''
    }
  }
})

const schemeForm = reactive({
  schemeName: '',
  description: ''
})

const dialogTitle = computed(() => form.id ? '编辑项目' : '新建项目')

const loadData = async () => {
  loading.value = true
  try {
    // 更新当前用户ID
    updateUserId()
    const res = await getProjectList(filters)
    if (res.code === 200) {
      projectList.value = res.data
      
      // 为每个方案获取辅料数量
      for (const project of projectList.value) {
        if (project.schemes) {
          // 过滤掉无效的方案（名称为空或ID无效的方案）
          project.schemes = project.schemes.filter(scheme => 
            scheme && scheme.schemeName && scheme.id && scheme.id !== 'null' && scheme.id !== 'undefined'
          )
          
          for (const scheme of project.schemes) {
            try {
              // 检查scheme.id是否为有效数值，避免传递null或无效值
              if (scheme.id && scheme.id !== 'null' && scheme.id !== 'undefined') {
                const schemeItemsRes = await getSchemeItemListBySchemeId(scheme.id)
                if (schemeItemsRes.code === 200) {
                  scheme.materials = schemeItemsRes.data
                } else {
                  scheme.materials = []
                }
              } else {
                scheme.materials = []
              }
            } catch (error) {
              console.error(`获取方案${scheme.id}的辅料列表失败:`, error)
              scheme.materials = []
            }
          }
        }
      }
      
      // 自动展开所有行
      expandedRowKeys.value = projectList.value.map(project => project.id)
    }
  } catch (error) {
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.status = ''
  loadData()
}

const handleAddProject = async () => {
  // 确保用户信息已加载
  if (!userStore.user) {
    await userStore.initUser()
  }
  
  // 立即填充客户名称，确保表单显示时就有值
  const clientNameValue = userStore.user?.role === 'admin' ? '' : (userStore.user?.company || '未设置')
  
  Object.assign(form, {
    id: null,
    projectName: '',
    clientName: clientNameValue,
    description: '',
    status: '未开始',
    startDate: '',
    endDate: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  // 非管理员用户的客户名称设置为当前用户所属公司，管理员可以编辑原有客户名称
  if (userStore.user?.role !== 'admin') {
    form.clientName = userStore.user?.company || '未设置'
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该项目吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await deleteProject(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  })
}

const handleSubmit = async () => {
  if (!form.projectName) {
    ElMessage.warning('请填写项目名称')
    return
  }
  
  // 管理员需要填写客户名称
  if (userStore.user?.role === 'admin' && !form.clientName) {
    ElMessage.warning('请填写客户名称')
    return
  }
  
  submitting.value = true
  try {
    const api = form.id ? updateProject : createProject
    const projectData = form.id ? form : { ...form, userId: userStore.user?.id || 1 }
    const res = await api(projectData)
    if (res.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// Scheme Logic
const handleAddScheme = (project) => {
  currentProject.value = project
  schemeForm.schemeName = ''
  schemeForm.description = ''
  schemeDialogVisible.value = true
}

const submitScheme = async () => {
  if (!schemeForm.schemeName) {
    ElMessage.warning('请输入方案名称')
    return
  }
  
  // 检查是否已经存在同名方案
  const hasSameNameScheme = currentProject.value.schemes?.some(scheme => scheme.schemeName === schemeForm.schemeName)
  if (hasSameNameScheme) {
    ElMessage.warning('已存在同名设计方案，请更换名称')
    return
  }
  
  submitting.value = true
  try {
    const res = await addScheme(currentProject.value.id, schemeForm)
    if (res.code === 200) {
      ElMessage.success('方案添加成功')
      
      // 如果项目状态是'未开始'，则更新为'进行中'
      if (currentProject.value.status === '未开始') {
        await updateProject({
          id: currentProject.value.id,
          status: '进行中'
        })
      }
      
      schemeDialogVisible.value = false
      loadData() // Reload to show new scheme and updated status
    }
  } catch (error) {
    ElMessage.error('添加失败')
  } finally {
    submitting.value = false
  }
}

// 方案详情
const schemeDetailDialogVisible = ref(false)
const currentScheme = ref(null)

const handleSchemeDetail = async (scheme) => {
  // 创建scheme对象的深拷贝，避免直接修改projectList中的数据导致表格重新渲染
  currentScheme.value = JSON.parse(JSON.stringify(scheme))
  // 获取方案辅料列表
  try {
    // 检查scheme.id是否为有效数值，避免传递null或无效值
    if (scheme.id && scheme.id !== 'null' && scheme.id !== 'undefined') {
      const res = await getSchemeItemListBySchemeId(scheme.id)
      if (res.code === 200) {
        currentScheme.value.materials = res.data
      } else {
        currentScheme.value.materials = []
      }
    } else {
      currentScheme.value.materials = []
    }
  } catch (error) {
    currentScheme.value.materials = []
    console.error('获取辅料列表失败:', error)
  }
  schemeDetailDialogVisible.value = true
}

// 更新辅料数量
const handleUpdateQuantity = async (item) => {
  try {
    const res = await updateSchemeItem({
      id: item.id,
      schemeId: item.schemeId,
      materialId: item.materialId,
      quantity: item.quantity,
      unit: item.unit,
      purpose: item.purpose,
      remark: item.remark,
      sortOrder: item.sortOrder
    })
    if (res.code === 200) {
      ElMessage.success('数量更新成功')
      // 更新当前方案详情中的辅料列表
      currentScheme.value.materials = currentScheme.value.materials.map(material => {
        if (material.id === item.id) {
          return { ...material, quantity: item.quantity }
        }
        return material
      })
      
      // 更新项目列表中对应方案的辅料列表
      projectList.value.forEach(project => {
        if (project.schemes) {
          project.schemes.forEach(scheme => {
            if (scheme.id === currentScheme.value.id) {
              scheme.materials = scheme.materials || []
              const materialIndex = scheme.materials.findIndex(m => m.id === item.id)
              if (materialIndex !== -1) {
                scheme.materials[materialIndex].quantity = item.quantity
              }
            }
          })
        }
      })
    } else {
      ElMessage.error('数量更新失败')
    }
  } catch (error) {
    console.error('更新数量失败:', error)
    ElMessage.error('数量更新失败')
  }
}

// 定版方案
const handleFinalizeScheme = async () => {
  try {
    // 确保currentScheme.value存在且包含必要的字段
    if (!currentScheme.value) {
      ElMessage.error('方案数据不存在')
      return
    }
    
    // 检查方案是否已经定版或已提交
    if (currentScheme.value.status === '已确定') {
      ElMessage.warning('该方案已成功定版，无法重复定版')
      return
    }
    
    // 检查方案是否已经提交采购需求
    if (currentScheme.value.status === '已提交') {
      ElMessage.warning('该方案已提交采购需求，无法重复定版')
      return
    }
    
    // 检查方案中是否有辅料
    if (!currentScheme.value.materials || currentScheme.value.materials.length === 0) {
      ElMessage.warning('方案中没有辅料，无法定版')
      return
    }
    
    // 构建更新数据，只包含必要的字段
    const updateData = {
      id: currentScheme.value.id,
      status: '已确定'
    }
    
    // 只在字段存在时添加到更新数据中
    if (currentScheme.value.schemeCode) updateData.schemeCode = currentScheme.value.schemeCode
    if (currentScheme.value.schemeName) updateData.schemeName = currentScheme.value.schemeName
    if (currentScheme.value.projectId) updateData.projectId = currentScheme.value.projectId
    if (currentScheme.value.version) updateData.version = currentScheme.value.version
    if (currentScheme.value.description) updateData.description = currentScheme.value.description
    if (currentScheme.value.creatorId) updateData.creatorId = currentScheme.value.creatorId
    
    const res = await updateScheme(updateData)
    if (res.code === 200) {
        ElMessage.success('方案已成功定版')
        // 更新当前方案状态
        currentScheme.value.status = '已确定'
        
        // 更新方案列表中的状态，不需要重新加载整个数据
        projectList.value.forEach(project => {
          if (project.schemes) {
            project.schemes.forEach(scheme => {
              if (scheme.id === currentScheme.value.id) {
                scheme.status = '已确定'
              }
            })
          }
        })
      } else {
        ElMessage.error('方案定版失败')
      }
  } catch (error) {
    console.error('定版方案失败:', error)
    ElMessage.error('方案定版失败')
  }
}

// 提交采购需求
const handleSubmitRequirement = async () => {
  try {
    // 首先检查是否有辅料
    if (!currentScheme.value.materials || currentScheme.value.materials.length === 0) {
      ElMessage.warning('方案中没有辅料，无法提交采购需求')
      return
    }
    
    // 调用从方案创建采购需求的API
    const res = await createRequirementFromScheme(currentScheme.value.id)
    if (res.code === 200) {
      ElMessage.success('采购需求已成功创建，请在采购需求管理中提交审核')
      
      // 更新方案状态为'已提交'
      try {
        const updateData = {
          id: currentScheme.value.id,
          status: '已提交'
        }
        
        // 只在字段存在时添加到更新数据中
        if (currentScheme.value.schemeCode) updateData.schemeCode = currentScheme.value.schemeCode
        if (currentScheme.value.schemeName) updateData.schemeName = currentScheme.value.schemeName
        if (currentScheme.value.projectId) updateData.projectId = currentScheme.value.projectId
        if (currentScheme.value.version) updateData.version = currentScheme.value.version
        if (currentScheme.value.description) updateData.description = currentScheme.value.description
        if (currentScheme.value.creatorId) updateData.creatorId = currentScheme.value.creatorId
        
        const updateRes = await updateScheme(updateData)
        if (updateRes.code === 200) {
          // 更新当前方案状态
          currentScheme.value.status = '已提交'
          
          // 更新方案列表中的状态
          projectList.value.forEach(project => {
            if (project.schemes) {
              project.schemes.forEach(scheme => {
                if (scheme.id === currentScheme.value.id) {
                  scheme.status = '已提交'
                }
              })
            }
          })
        }
      } catch (error) {
        console.error('更新方案状态失败:', error)
        // 不影响主流程，继续执行
      }
      
      schemeDetailDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '采购需求提交失败')
    }
  } catch (error) {
      // 显示更详细的错误信息
      if (error.response && error.response.data && error.response.data.message) {
        ElMessage.error('提交采购需求失败: ' + error.response.data.message)
      } else {
        ElMessage.error('提交采购需求失败: ' + (error.message || '未知错误'))
      }
    }
}

const handleDeleteScheme = async (scheme) => {
  try {
    const res = await deleteScheme(scheme.id)
    if (res.code === 200) {
      ElMessage.success('方案删除成功')
      // 更新本地数据，不需要重新加载整个列表
      projectList.value.forEach(project => {
        if (project.schemes) {
          project.schemes = project.schemes.filter(s => s.id !== scheme.id)
        }
      })
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 从方案中移除辅料
const handleRemoveMaterialFromScheme = async (index) => {
  if (currentScheme.value?.materials) {
    const item = currentScheme.value.materials[index]
    
    // 检查方案状态，已确定状态需要二次确认
    if (currentScheme.value.status === '已确定') {
      try {
        await ElMessageBox.confirm(
          '当前方案状态为已确定，确定要删除该辅料吗？',
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 用户确认后执行删除
        const res = await deleteSchemeItem(item.id)
        if (res.code === 200) {
          // 更新当前方案详情中的辅料列表
          currentScheme.value.materials.splice(index, 1)
          ElMessage.success('辅料已移除')
          
          // 更新项目列表中对应方案的辅料列表
          projectList.value.forEach(project => {
            if (project.schemes) {
              project.schemes.forEach(scheme => {
                if (scheme.id === currentScheme.value.id) {
                  scheme.materials = scheme.materials || []
                  scheme.materials = scheme.materials.filter(m => m.id !== item.id)
                }
              })
            }
          })
        } else {
          ElMessage.error('辅料移除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除辅料失败:', error)
          ElMessage.error('辅料移除失败')
        }
      }
    } else {
      // 非已确定状态，直接删除
      try {
        const res = await deleteSchemeItem(item.id)
        if (res.code === 200) {
          // 更新当前方案详情中的辅料列表
          currentScheme.value.materials.splice(index, 1)
          ElMessage.success('辅料已移除')
          
          // 更新项目列表中对应方案的辅料列表
          projectList.value.forEach(project => {
            if (project.schemes) {
              project.schemes.forEach(scheme => {
                if (scheme.id === currentScheme.value.id) {
                  scheme.materials = scheme.materials || []
                  scheme.materials = scheme.materials.filter(m => m.id !== item.id)
                }
              })
            }
          })
        } else {
          ElMessage.error('辅料移除失败')
        }
      } catch (error) {
        console.error('删除辅料失败:', error)
        ElMessage.error('辅料移除失败')
      }
    }
  }
}

const getStatusType = (status) => {
  const map = {
    '未开始': 'info',
    '进行中': 'primary',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

const getSchemeStatusType = (status) => {
  const map = {
    '待确定': 'info',
    '已确定': 'success',
    '已提交': 'warning'
  }
  return map[status] || 'info'
}

// 处理展开行变化
const handleExpandChange = (row, expandedRows) => {
  // 将展开的行ID保存到数组中
  expandedRowKeys.value = expandedRows.map(item => item.id)
}

// 将分类ID转换为分类名称
const getCategoryName = (categoryId) => {
  if (!categoryId) return '未分类'
  // 将categoryId转换为数字，因为从后端返回的是字符串类型的category_id
  const id = Number(categoryId)
  const category = categoryList.value.find(cat => cat.id === id)
  return category?.categoryName || categoryId
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.project-scheme {
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
.scheme-list {
  padding: 10px 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

/* 方案详情弹窗样式 */
.scheme-detail {
  padding: 5px;
}

.scheme-status {
  margin-bottom: 10px;
}

.scheme-note {
  margin-bottom: 15px;
  color: #606266;
}

/* 卡片式辅料列表样式 */
.material-card-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.material-card {
  margin-bottom: 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.material-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 卡片内容布局 */
.card-content-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 0 15px;
}

/* 辅料图片样式 */
.material-image {
  flex-shrink: 0;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 辅料内容样式 */
.material-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 0;
}

.card-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  padding-bottom: 4px;
  border-bottom: 1px solid #ebeef5;
}

.material-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
}

.material-info {
  padding: 0;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 0;
  align-items: center;
}

.info-item {
  display: flex;
  align-items: center;
  flex: 0 0 auto;
  min-width: 120px;
  flex-wrap: nowrap;
  gap: 5px;
}

.info-item .label {
  font-weight: 600;
  margin-right: 2px;
  color: #409eff;
  width: auto;
  flex-shrink: 0;
  text-align: right;
  font-size: 14px;
}

/* 数量控件在info-item中的样式调整 */
.info-item.quantity-control {
  min-width: 180px;
  flex-wrap: nowrap;
  gap: 8px;
}

/* 单价控件样式 */
.info-item:nth-child(5) {
  min-width: 120px;
}

.info-item .value {
  color: #303133;
  word-break: break-all;
  flex: 0 0 auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
  font-weight: 500;
}

.info-item .value.price {
  font-weight: bold;
  color: #f56c6c;
  font-size: 14px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: transparent;
  padding: 0;
  border-radius: 0;
  border: none;
  justify-content: flex-start;
}

.quantity-control .label {
  font-weight: 600;
  color: #409eff;
  margin: 0;
  flex-shrink: 0;
  font-size: 14px;
  width: auto;
  text-align: right;
}

/* 调整数量输入框大小 */
.quantity-control :deep(.el-input-number) {
  width: 120px;
  font-size: 14px;
}

.quantity-control :deep(.el-input__wrapper) {
  padding: 4px 11px;
}

.quantity-control :deep(.el-input__inner) {
  font-size: 14px;
  padding: 0;
}

.quantity-control :deep(.el-input-number__decrease),
.quantity-control :deep(.el-input-number__increase) {
  padding: 0 8px;
  height: auto;
}

.quantity-control :deep(.el-input-number__decrease:hover),
.quantity-control :deep(.el-input-number__increase:hover) {
  color: #409eff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-content-wrapper {
    flex-direction: column;
    align-items: center;
  }
  
  .material-image {
    width: 100px;
    height: 100px;
  }
  
  .material-content {
    width: 100%;
  }
  
  .info-row {
    gap: 15px;
  }
  
  .info-item {
    min-width: 100px;
  }
}
</style>
