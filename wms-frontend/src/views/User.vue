<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理（支持审核）</span>
          <div style="display:flex;gap:10px;align-items:center;">
            <el-switch v-model="onlyPending" active-text="只看待审核" />
            <el-button type="primary" @click="handleAdd">新增用户</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredTableData" border style="width: 100%">
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'success'">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-tooltip
              v-if="row.role === 'admin' && adminCount === 1"
              content="系统至少保留一个管理员账户，无法删除"
              placement="top"
            >
              <span>
                <el-button type="danger" size="small" disabled>删除</el-button>
              </span>
            </el-tooltip>
            <el-button
              v-else
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >删除</el-button>
            <el-divider direction="vertical" />
            <el-button v-if="row.status === 0" type="success" size="small" @click="approveUser(row)">审核通过</el-button>
            <el-button v-if="row.status === 0" type="warning" size="small" @click="rejectUser(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio label="admin">管理员</el-radio>
            <el-radio label="user">普通用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, saveUser, updateUser, deleteUser } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const tableData = ref([])
const onlyPending = ref(false)
const filteredTableData = computed(() => {
  if (onlyPending.value) {
    return (tableData.value || []).filter(u => u.status === 0)
  }
  return tableData.value || []
})
const adminCount = computed(() => (tableData.value || []).filter(u => u.role === 'admin').length)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'user',
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadData = async () => {
  try {
    const res = await getUserList()
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const approveUser = async (row) => {
  try {
    await updateUser({ id: row.id, status: 1 })
    ElMessage.success('已通过审核')
    loadData()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

const rejectUser = async (row) => {
  try {
    await updateUser({ id: row.id, status: 2 })
    ElMessage.success('已驳回用户')
    loadData()
  } catch (error) {
    ElMessage.error('驳回失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          // 编辑时如果没有输入密码，则不更新密码
          if (!form.password) {
            const { password, ...updateData } = form
            await updateUser(updateData)
          } else {
            await updateUser(form)
          }
          ElMessage.success('更新成功')
        } else {
          await saveUser(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(error.message || (isEdit.value ? '更新失败' : '添加失败'))
      }
    }
  })
}

const resetForm = () => {
  form.id = null
  form.username = ''
  form.password = ''
  form.realName = ''
  form.phone = ''
  form.email = ''
  form.role = 'user'
  form.status = 1
}

onMounted(() => {
  // 检查是否为管理员
  if (userStore.user?.role !== 'admin') {
    ElMessage.warning('权限不足，只有管理员可以访问用户管理')
    router.push('/dashboard')
    return
  }
  loadData()
})
</script>

<style scoped>
.user-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
