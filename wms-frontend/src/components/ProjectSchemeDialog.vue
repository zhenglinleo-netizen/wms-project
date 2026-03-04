<template>
  <el-dialog v-model="dialogVisible" title="选择项目方案" width="500px" center>
    <el-form :model="{}" label-width="80px">
      <el-form-item label="选择项目" required>
        <el-select v-model="selectedProject" placeholder="请选择项目" style="width: 100%" @change="handleProjectChange">
          <el-option v-for="project in projectList" :key="project.id" :label="project.projectName" :value="project.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="选择方案" required>
        <el-select v-model="selectedScheme" placeholder="请选择方案" style="width: 100%" :disabled="!selectedProject">
          <el-option 
            v-for="scheme in getSelectedProjectSchemes()" 
            :key="scheme.id" 
            :label="`${scheme.schemeName} (${scheme.status})`" 
            :value="scheme.id" 
            :disabled="scheme.status === '已确定'"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="addToProject">确定添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getProjectList, getSchemeListByProjectId, addMaterialToScheme } from '@/api/project'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  material: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'add-success'])

const dialogVisible = ref(props.visible)
const selectedProject = ref(null)
const selectedScheme = ref(null)
const projectList = ref([])

// 监听 visible 属性变化
watch(() => props.visible, (newValue) => {
  dialogVisible.value = newValue
})

// 监听 dialogVisible 变化，通知父组件
watch(dialogVisible, (newValue) => {
  emit('update:visible', newValue)
})

// 加载项目列表
const loadProjects = async () => {
  try {
    // 获取当前登录用户ID
    const userId = userStore.user?.id || 1
    const res = await getProjectList({ userId })
    if (res.code === 200) {
      projectList.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载项目列表失败')
  }
}

// 获取当前选择项目的方案列表
const getSelectedProjectSchemes = () => {
  if (!selectedProject.value) return []
  const project = projectList.value.find(p => p.id === selectedProject.value)
  return project?.schemes || []
}

// 处理项目选择变化
const handleProjectChange = async () => {
  selectedScheme.value = null
  if (selectedProject.value) {
    try {
      const res = await getSchemeListByProjectId(selectedProject.value)
      if (res.code === 200) {
        // 找到对应的项目并更新其方案列表
        const project = projectList.value.find(p => p.id === selectedProject.value)
        if (project) {
          project.schemes = res.data
        }
      }
    } catch (error) {
      console.error('加载方案列表失败:', error)
      ElMessage.error('加载方案列表失败')
    }
  }
}

// 添加辅料到项目方案
const addToProject = async () => {
  if (!selectedProject.value || !selectedScheme.value || !props.material) {
    ElMessage.warning('请选择项目和方案')
    return
  }
  
  try {
    const result = await addMaterialToScheme(selectedScheme.value, {
      materialId: props.material.id,
      productCode: props.material.productCode,
      productName: props.material.productName,
      category: props.material.category,
      specification: props.material.specification,
      unit: props.material.unit,
      price: props.material.price,
      image: props.material.image || '',
      quantity: 1 // 默认添加1个
    })
    
    if (result.code === 200) {
      ElMessage.success(`已将 ${props.material.productName} 加入方案`)
      dialogVisible.value = false
      selectedProject.value = null
      selectedScheme.value = null
      emit('add-success')
    } else {
      ElMessage.error(result.message || '添加失败')
    }
  } catch (error) {
    console.error('添加辅料到项目方案失败:', error)
    ElMessage.error('添加失败: ' + (error.message || '未知错误'))
  }
}

// 当对话框显示时加载项目列表
watch(dialogVisible, async (newValue) => {
  if (newValue) {
    await loadProjects()
  }
})
</script>

<style scoped>
/* 组件样式 */
</style>