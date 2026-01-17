<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="sidebar">
      <div class="logo">
        <el-icon class="logo-icon"><GoodsFilled /></el-icon>
        <h2>辅料采购平台</h2>
      </div>
      <el-menu
    :default-active="activeMenu"
    :default-openeds="openMenus"
    router
    background-color="transparent"
    text-color="#0d47a1"
    active-text-color="#1976d2"
    class="custom-menu"
    @select="handleMenuSelect"
    @open="handleMenuOpen"
  >
        <el-menu-item index="/dashboard">
          <el-icon class="menu-icon"><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        
        <el-menu-item index="/material-library">
          <el-icon class="menu-icon"><Search /></el-icon>
          <span>智能辅料库</span>
        </el-menu-item>
        
        <el-menu-item index="/project-scheme">
          <el-icon class="menu-icon"><Folder /></el-icon>
          <span>项目方案管理</span>
        </el-menu-item>
        
        <el-menu-item index="/requirement-management">
          <el-icon class="menu-icon"><List /></el-icon>
          <span>采购需求管理</span>
        </el-menu-item>
        
        <el-menu-item index="/purchase-management">
          <el-icon class="menu-icon"><ShoppingCart /></el-icon>
          <span>采购管理</span>
        </el-menu-item>
        
        <el-menu-item index="/inventory-management" v-if="userStore.user?.role === 'admin'">
          <el-icon class="menu-icon"><Box /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        
        <el-menu-item index="/material-management" v-if="userStore.user?.role === 'admin'">
          <el-icon class="menu-icon"><Box /></el-icon>
          <span>辅料管理</span>
        </el-menu-item>
        
        <el-sub-menu index="/supplier" v-if="userStore.user?.role === 'admin'">
          <template #title>
            <el-icon class="menu-icon"><User /></el-icon>
            <span>供应商管理</span>
          </template>
          <el-menu-item index="/supplier">
            <el-icon class="sub-menu-icon"><OfficeBuilding /></el-icon>
            <span>供应商库</span>
          </el-menu-item>

          <el-menu-item index="/supplier/relationship">
            <el-icon class="sub-menu-icon"><Link /></el-icon>
            <span>供需关系管理</span>
          </el-menu-item>

        </el-sub-menu>
        
        <el-menu-item index="/data-analysis">
          <el-icon class="menu-icon"><PieChart /></el-icon>
          <span>数据分析与报表</span>
        </el-menu-item>

        <el-sub-menu index="/system-management" v-if="userStore.user?.role === 'admin'">
          <template #title>
            <el-icon class="menu-icon"><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system-management">
            <el-icon class="sub-menu-icon"><Setting /></el-icon>
            <span>系统概览</span>
          </el-menu-item>
          <el-menu-item index="/category-management">
            <el-icon class="sub-menu-icon"><List /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/user">
            <el-icon class="sub-menu-icon"><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageName }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
        </div>
        <div class="header-right">
          <el-dropdown trigger="hover" class="user-dropdown">
            <div class="user-info">
              <el-avatar :size="32" :icon="UserFilled" class="user-avatar" />
              <span class="welcome-text">欢迎，<span class="username-highlight">{{ userStore.user?.realName || userStore.user?.username }}</span></span>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown-menu">
                <el-dropdown-item disabled class="user-details">
                  <div class="user-detail-item">
                    <span class="label">所属公司：</span>
                    <span class="value">{{ userStore.user?.company || '未设置' }}</span>
                  </div>
                  <div class="user-detail-item">
                    <span class="label">角色：</span>
                    <span class="value">{{ translateRole(userStore.user?.role) || '普通用户' }}</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleChangePassword" class="dropdown-action">
                  <el-icon><Edit /></el-icon>
                  <span>修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout" class="dropdown-action">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 修改密码弹窗 -->
      <el-dialog 
        v-model="changePasswordVisible" 
        title="修改密码" 
        width="450px"
        class="custom-dialog"
      >
        <el-form 
          :model="changePasswordForm" 
          :rules="changePasswordRules" 
          ref="changePasswordFormRef" 
          label-width="120px"
          class="change-password-form"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input 
              v-model="changePasswordForm.oldPassword" 
              type="password" 
              placeholder="请输入原密码"
              class="custom-input"
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="changePasswordForm.newPassword" 
              type="password" 
              placeholder="请输入新密码"
              class="custom-input"
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="changePasswordForm.confirmPassword" 
              type="password" 
              placeholder="请确认新密码"
              class="custom-input"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="changePasswordVisible = false" class="cancel-btn">取消</el-button>
            <el-button type="primary" @click="handleSavePassword" class="submit-btn">保存</el-button>
          </span>
        </template>
      </el-dialog>
      <el-main class="main-content">
        <transition name="fade" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { changePassword } from '@/api/user'
import { SwitchButton, OfficeBuilding, Link, GoodsFilled, UserFilled, Edit } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

// 当前页面名称
const currentPageName = computed(() => {
  const routeMap = {
    '/dashboard': '仪表盘',
    '/material-library': '智能辅料库',
    '/project-scheme': '项目方案管理',
    '/requirement-management': '采购需求管理',
    '/purchase-management': '采购管理',
    '/inventory-management': '库存管理',
    '/material-management': '辅料管理',
    '/supplier': '供应商库',
    '/supplier/relationship': '供需关系管理',
    '/data-analysis': '数据分析与报表',
    '/system-management': '系统概览',
    '/category-management': '分类管理',
    '/user': '用户管理'
  }
  return routeMap[route.path] || '首页'
})

// 修改密码相关
const changePasswordVisible = ref(false)
const changePasswordFormRef = ref()
const changePasswordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const changePasswordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== changePasswordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleChangePassword = () => {
  changePasswordVisible.value = true
}

const handleSavePassword = async () => {
  if (!changePasswordFormRef.value) return
  await changePasswordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const { oldPassword, newPassword } = changePasswordForm
        await changePassword({ oldPassword, newPassword })
        ElMessage.success('密码修改成功')
        changePasswordVisible.value = false
        // 重置表单
        Object.assign(changePasswordForm, {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        })
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      }
    }
  })
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    // 强制刷新页面，确保登录状态正确更新
    window.location.href = '/login'
  })
}

// 角色翻译
const translateRole = (role) => {
  const roleMap = {
    'admin': '管理员',
    'designer': '设计师',
    'buyer': '采购员',
    'member': '成员'
  }
  return roleMap[role] || '普通用户'
}

// 菜单状态管理
const openMenus = ref([])

// 处理菜单选择
const handleMenuSelect = (key, keyPath) => {
  // 当选择的是子菜单项时，保持父菜单展开
  // 当选择的是非父菜单项时，收起所有展开的父菜单
  if (keyPath.length === 1) {
    // 选择的是顶级菜单项，不是父菜单
    openMenus.value = []
  }
}

// 处理菜单展开
const handleMenuOpen = (key, keyPath) => {
  // 只保存当前展开的父菜单
  openMenus.value = [key]
}
</script>

<style scoped>
/* 全局布局 */
.layout-container {
  height: 100vh;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}

/* 侧边栏 */
.sidebar {
  background-color: #ffffff;
  overflow: hidden;
  border-radius: 0 24px 24px 0;
  box-shadow: 0 0 30px rgba(33, 150, 243, 0.12);
  border: 1px solid #e0f2fe;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar:hover {
  box-shadow: 0 0 40px rgba(33, 150, 243, 0.18);
}

/* Logo */
.logo {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  padding: 0 24px;
  border-radius: 0 24px 0 0;
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.3);
}

.logo-icon {
  font-size: 36px;
  color: white;
  margin-right: 16px;
  animation: logoFloat 3s ease-in-out infinite;
}

.logo h2 {
  color: white;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  letter-spacing: 0.5px;
}

/* 菜单 */
.custom-menu {
  border-right: none;
  margin-top: 24px;
  background: transparent !important;
}

/* 子菜单容器 */
:deep(.el-sub-menu .el-menu) {
  background: transparent !important;
}

.menu-icon {
  font-size: 20px;
  margin-right: 16px;
  transition: all 0.3s ease;
  color: #2196f3;
}

.sub-menu-icon {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s ease;
  color: #2196f3;
}

/* 菜单项 */
:deep(.el-menu-item) {
  margin: 8px 16px !important;
  border-radius: 16px !important;
  background: transparent !important;
  border: 2px solid transparent !important;
  transition: all 0.3s cubic-bezier(0.4, 0.2, 0.2, 1) !important;
  padding: 14px 24px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  color: #0d47a1 !important;
  position: relative !important;
  overflow: hidden !important;
}

:deep(.el-menu-item::before) {
  content: '' !important;
  position: absolute !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  border-radius: 16px !important;
  background: radial-gradient(circle at center, rgba(224, 242, 254, 0.6) 0%, rgba(191, 219, 254, 0.3) 30%, rgba(240, 249, 255, 0.1) 60%, rgba(240, 249, 255, 0) 100%) !important;
  opacity: 1 !important;
  transition: all 0.3s ease !important;
  z-index: 0 !important;
}

:deep(.el-menu-item > *),
:deep(.el-menu-item .menu-icon) {
  position: relative !important;
  z-index: 1 !important;
}

:deep(.el-menu-item:hover) {
  background: transparent !important;
  transform: translateX(8px) !important;
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.3) !important;
}

:deep(.el-menu-item:hover::before) {
  background: radial-gradient(circle at center, rgba(191, 219, 254, 0.3) 0%, rgba(33, 150, 243, 0.2) 30%, rgba(25, 118, 210, 0.08) 60%, rgba(25, 118, 210, 0) 100%) !important;
  opacity: 0.8 !important;
}

:deep(.el-menu-item:hover .menu-icon) {
  color: #1976d2 !important;
  transform: scale(1.1) !important;
}

:deep(.el-menu-item.is-active) {
  background: transparent !important;
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.35) !important;
  color: #1976d2 !important;
}

:deep(.el-menu-item.is-active::before) {
  background: radial-gradient(circle at center, rgba(191, 219, 254, 0.35) 0%, rgba(33, 150, 243, 0.25) 30%, rgba(25, 118, 210, 0.1) 60%, rgba(25, 118, 210, 0) 100%) !important;
  opacity: 0.85 !important;
}

:deep(.el-menu-item.is-active .menu-icon) {
  color: #1976d2 !important;
  transform: scale(1.1) !important;
}

/* 子菜单 */
:deep(.el-sub-menu .el-menu-item) {
  margin: 6px 16px 6px 48px !important;
  border-radius: 12px !important;
  background: transparent !important;
  border: 2px solid transparent !important;
  transition: all 0.3s cubic-bezier(0.4, 0.2, 0.2, 1) !important;
  padding: 12px 20px !important;
  font-size: 13px !important;
  font-weight: 500 !important;
  color: #0d47a1 !important;
  position: relative !important;
  overflow: hidden !important;
}

:deep(.el-sub-menu .el-menu-item::before) {
  content: '' !important;
  position: absolute !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  border-radius: 12px !important;
  background: radial-gradient(circle at center, rgba(224, 242, 254, 0.5) 0%, rgba(191, 219, 254, 0.25) 30%, rgba(240, 249, 255, 0.08) 60%, rgba(240, 249, 255, 0) 100%) !important;
  opacity: 1 !important;
  transition: all 0.3s ease !important;
  z-index: 0 !important;
}

:deep(.el-sub-menu .el-menu-item > *),
:deep(.el-sub-menu .el-menu-item .sub-menu-icon) {
  position: relative !important;
  z-index: 1 !important;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background: transparent !important;
  transform: translateX(5px) !important;
  box-shadow: 0 3px 12px rgba(33, 150, 243, 0.25) !important;
}

:deep(.el-sub-menu .el-menu-item:hover::before) {
  background: radial-gradient(circle at center, rgba(191, 219, 254, 0.25) 0%, rgba(33, 150, 243, 0.15) 30%, rgba(25, 118, 210, 0.06) 60%, rgba(25, 118, 210, 0) 100%) !important;
  opacity: 0.7 !important;
}

:deep(.el-sub-menu .el-menu-item.is-active) {
  background: transparent !important;
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.3) !important;
  color: #1976d2 !important;
}

:deep(.el-sub-menu .el-menu-item.is-active::before) {
  background: radial-gradient(circle at center, rgba(191, 219, 254, 0.3) 0%, rgba(33, 150, 243, 0.2) 30%, rgba(25, 118, 210, 0.08) 60%, rgba(25, 118, 210, 0) 100%) !important;
  opacity: 0.8 !important;
}

/* 子菜单标题 */
:deep(.el-sub-menu__title) {
  margin: 8px 16px !important;
  border-radius: 16px !important;
  background: transparent !important;
  border: 2px solid transparent !important;
  transition: all 0.3s cubic-bezier(0.4, 0.2, 0.2, 1) !important;
  padding: 14px 24px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  color: #0d47a1 !important;
  position: relative !important;
  overflow: hidden !important;
}

:deep(.el-sub-menu__title::before) {
  content: '' !important;
  position: absolute !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  border-radius: 16px !important;
  background: radial-gradient(circle at center, rgba(224, 242, 254, 0.6) 0%, rgba(191, 219, 254, 0.3) 30%, rgba(240, 249, 255, 0.1) 60%, rgba(240, 249, 255, 0) 100%) !important;
  opacity: 1 !important;
  transition: all 0.3s ease !important;
  z-index: 0 !important;
}

:deep(.el-sub-menu__title > *),
:deep(.el-sub-menu__title .menu-icon) {
  position: relative !important;
  z-index: 1 !important;
}

:deep(.el-sub-menu__title:hover),
:deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  background: transparent !important;
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.3) !important;
}

:deep(.el-sub-menu__title:hover::before),
:deep(.el-sub-menu.is-opened > .el-sub-menu__title::before) {
  background: radial-gradient(circle at center, rgba(191, 219, 254, 0.3) 0%, rgba(33, 150, 243, 0.2) 30%, rgba(25, 118, 210, 0.08) 60%, rgba(25, 118, 210, 0) 100%) !important;
  opacity: 0.8 !important;
}

:deep(.el-sub-menu__title:hover) {
  transform: translateX(8px) !important;
}

:deep(.el-sub-menu__title:hover .menu-icon),
:deep(.el-sub-menu.is-opened > .el-sub-menu__title .menu-icon) {
  color: #1976d2 !important;
  transform: scale(1.1) !important;
}

:deep(.el-sub-menu__title) {
  display: flex !important;
  align-items: center !important;
  position: relative !important;
}

:deep(.el-sub-menu__title > span) {
  display: flex !important;
  align-items: center !important;
  gap: 16px !important;
  flex: 1 !important;
}

:deep(.el-sub-menu__title .el-sub-menu__icon-arrow) {
  transition: all 0.3s ease !important;
  color: #2196f3 !important;
  margin-top: 0 !important;
  margin-left: 8px !important;
  transform: translateY(0) !important;
  flex-shrink: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  height: 16px !important;
  width: 16px !important;
}

:deep(.el-sub-menu__title:hover .el-sub-menu__icon-arrow),
:deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  color: #1976d2 !important;
  transform: rotate(180deg) scale(1.1) translateY(0) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

/* 顶部导航 */
.header {
  background: rgba(255, 255, 255, 0.95) !important;
  border-bottom: 2px solid #e0f2fe !important;
  display: flex !important;
  align-items: center !important;
  justify-content: space-between !important;
  padding: 0 36px !important;
  height: 76px !important;
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 24px rgba(33, 150, 243, 0.08);
  border-radius: 24px 24px 0 0;
  margin: 24px 24px 0 0;
  transition: all 0.3s ease;
}

.header:hover {
  box-shadow: 0 6px 32px rgba(33, 150, 243, 0.12);
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

/* 面包屑 */
.breadcrumb {
  font-size: 15px;
  font-weight: 500;
}

:deep(.el-breadcrumb__inner) {
  color: #0d47a1 !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  position: relative;
}

:deep(.el-breadcrumb__inner:hover) {
  color: #1976d2 !important;
  text-shadow: 0 1px 6px rgba(33, 150, 243, 0.3) !important;
}

:deep(.el-breadcrumb__separator) {
  color: #bbdefb !important;
  margin: 0 12px !important;
  font-weight: 400 !important;
}

/* 用户信息 */
.user-dropdown {
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-dropdown:hover {
  transform: translateY(-3px);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 24px;
  border-radius: 24px;
  background: #f0f9ff;
  border: 2px solid #e0f2fe;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #e0f2fe;
  border-color: #bbdefb;
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.2);
}

.user-avatar {
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.25);
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%) !important;
}

.user-info:hover .user-avatar {
  box-shadow: 0 8px 24px rgba(33, 150, 243, 0.35);
  transform: scale(1.1);
}

.welcome-text {
  font-size: 15px;
  color: #0d47a1;
  font-weight: 500;
}

.username-highlight {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  color: white;
  padding: 6px 16px;
  border-radius: 16px;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 3px 12px rgba(33, 150, 243, 0.35);
  transition: all 0.3s ease;
}

.user-info:hover .username-highlight {
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.45);
  transform: scale(1.05);
}

/* 下拉菜单 */
.custom-dropdown-menu {
  border-radius: 16px !important;
  border: 2px solid #e0f2fe !important;
  box-shadow: 0 12px 40px rgba(33, 150, 243, 0.15) !important;
  overflow: hidden !important;
  backdrop-filter: blur(16px) !important;
  background: rgba(255, 255, 255, 0.98) !important;
  animation: dropdownSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 0 !important;
  margin: 8px 0 !important;
}

/* 消除下拉菜单的直角边 */
:deep(.el-dropdown-menu) {
  border-radius: 16px !important;
  overflow: hidden !important;
  padding: 0 !important;
  margin: 0 !important;
}

/* 调整下拉菜单项的边距和圆角 */
:deep(.custom-dropdown-menu .el-dropdown-menu__item) {
  border-radius: 0 !important;
  margin: 0 !important;
  padding: 16px 24px !important;
  border: none !important;
}

/* 第一个下拉菜单项（如果不是user-details） */
:deep(.custom-dropdown-menu .el-dropdown-menu__item:first-child) {
  border-radius: 14px 14px 0 0 !important;
  margin-top: -2px !important;
  margin-left: -2px !important;
  margin-right: -2px !important;
}

/* 最后一个下拉菜单项 */
:deep(.custom-dropdown-menu .el-dropdown-menu__item:last-child) {
  border-radius: 0 0 14px 14px !important;
  margin-bottom: -2px !important;
  margin-left: -2px !important;
  margin-right: -2px !important;
}

.user-details {
  padding: 20px !important;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%) !important;
  border-bottom: 2px solid #e0f2fe !important;
  border-radius: 14px 14px 0 0 !important;
  margin: -2px -2px 0 -2px !important;
  display: flex !important;
  flex-direction: column !important;
  gap: 12px !important;
}

.user-detail-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  width: 100% !important;
  padding: 8px 0;
}

.user-detail-item .label {
  font-weight: 600;
  color: #1976d2;
  min-width: 90px;
  text-align: right;
  margin-right: 16px;
}

.user-detail-item .value {
  color: #0d47a1;
  flex: 1;
  font-weight: 500;
  text-align: left;
  padding-left: 16px;
  border-left: 2px solid #e0f2fe;
}

.dropdown-action {
  display: flex !important;
  align-items: center !important;
  gap: 12px !important;
  padding: 16px 24px !important;
  transition: all 0.3s ease !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  color: #0d47a1 !important;
}

/* 最后一个下拉菜单项 */
:deep(.custom-dropdown-menu .el-dropdown-menu__item:last-child) {
  border-radius: 0 0 14px 14px !important;
  margin: 0 -2px -2px -2px !important;
}

.dropdown-action:hover {
  background: #e0f2fe !important;
  transform: translateX(8px) !important;
  color: #1976d2 !important;
}

.dropdown-action:hover el-icon {
  color: #1976d2 !important;
}

/* 内容区域 */
.main-content {
  background: rgba(255, 255, 255, 0.95) !important;
  padding: 36px !important;
  margin: 0 24px 24px 0 !important;
  border-radius: 0 0 24px 0;
  backdrop-filter: blur(8px);
  box-shadow: 0 4px 24px rgba(33, 150, 243, 0.08);
  border: 1px solid #e0f2fe;
  transition: all 0.3s ease;
}

.main-content:hover {
  box-shadow: 0 6px 32px rgba(33, 150, 243, 0.12);
}

/* 弹窗 */
.custom-dialog {
  border-radius: 20px !important;
  overflow: hidden !important;
  box-shadow: 0 24px 72px rgba(33, 150, 243, 0.2) !important;
  border: 2px solid #e0f2fe !important;
  animation: dialogSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.custom-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%) !important;
  padding: 24px 32px !important;
  border-radius: 20px 20px 0 0 !important;
}

:deep(.custom-dialog .el-dialog__title) {
  color: white !important;
  font-size: 20px !important;
  font-weight: 600 !important;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

:deep(.custom-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: white !important;
  font-size: 24px !important;
  transition: all 0.3s ease !important;
}

:deep(.custom-dialog .el-dialog__headerbtn .el-dialog__close:hover) {
  color: rgba(255, 255, 255, 0.9) !important;
  transform: scale(1.2) !important;
}

.change-password-form {
  padding: 24px 32px;
}

.custom-input {
  border-radius: 12px !important;
  border: 2px solid #e0f2fe !important;
  transition: all 0.3s ease !important;
  height: 48px !important;
  font-size: 15px !important;
  background: #fefdff !important;
}

.custom-input:hover {
  border-color: #bbdefb !important;
  background: #ffffff !important;
  box-shadow: 0 0 0 4px rgba(33, 150, 243, 0.1) !important;
}

.custom-input:focus {
  border-color: #1976d2 !important;
  background: #ffffff !important;
  box-shadow: 0 0 0 4px rgba(33, 150, 243, 0.2) !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 20px 32px;
  background: #fefdff;
  border-top: 2px solid #e0f2fe;
  border-radius: 0 0 20px 20px;
}

.cancel-btn, .submit-btn {
  border-radius: 12px !important;
  padding: 12px 32px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
  border: 2px solid transparent !important;
}

.cancel-btn {
  background: #fefdff !important;
  border-color: #e0f2fe !important;
  color: #1976d2 !important;
}

.cancel-btn:hover {
  background: #e0f2fe !important;
  border-color: #bbdefb !important;
  color: #1976d2 !important;
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.2) !important;
}

.submit-btn {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%) !important;
  border: none !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.35) !important;
}

.submit-btn:hover {
  box-shadow: 0 8px 28px rgba(33, 150, 243, 0.45) !important;
  transform: translateY(-3px) !important;
}

/* 页面过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(24px);
}

/* 动画效果 */
@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-6px);
  }
}

@keyframes dropdownSlideIn {
  from {
    opacity: 0;
    transform: translateY(-16px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: scale(0.92) translateY(-24px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px !important;
  }
  
  .logo h2 {
    font-size: 16px;
  }
  
  .header {
    padding: 0 24px !important;
  }
  
  .main-content {
    padding: 24px !important;
  }
  
  :deep(.el-menu-item) {
    padding: 12px 20px !important;
  }
}
</style>
