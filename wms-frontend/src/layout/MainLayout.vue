<template>
  <el-container class="layout-container" @click="handleClickOutside">
    <!-- 移动端菜单展开按钮 -->
    <div class="mobile-menu-toggle" @click="toggleSidebar" @click.stop>
      <el-icon><ArrowLeft :class="{ 'rotate-180': isSidebarCollapsed }"></ArrowLeft></el-icon>
    </div>
    
    <el-aside width="240px" class="sidebar" :class="{ 'sidebar-collapsed': isSidebarCollapsed }" @click.stop>
      <div class="logo" :class="{ 'logo-collapsed': isSidebarCollapsed }">
        <el-icon class="logo-icon"><GoodsFilled /></el-icon>
        <h2 v-if="!isSidebarCollapsed">辅料采购平台</h2>
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
    @close="handleMenuClose"
  >
        <!-- 菜单项渲染 -->
        <template v-for="item in menuItems" :key="item.index">
          <!-- 有子菜单的菜单项 -->
          <el-sub-menu :index="item.index" v-if="item.children && (!item.requireAdmin || userStore.user?.role === 'admin')">
            <template #title>
              <el-icon class="menu-icon"><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item 
              v-for="child in item.children" 
              :key="child.index" 
              :index="child.index"
            >
              <el-icon class="sub-menu-icon"><component :is="child.icon" /></el-icon>
              <span>{{ child.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 无子菜单的菜单项 -->
          <el-menu-item 
            :index="item.index" 
            v-else-if="!item.requireAdmin || userStore.user?.role === 'admin'"
          >
            <el-icon class="menu-icon"><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
      <div class="sidebar-toggle" @click="toggleSidebar">
        <el-icon :class="{ 'rotate-180': isSidebarCollapsed }"><ArrowLeft /></el-icon>
      </div>
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
        append-to-body
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
      
      <!-- 辅料详情对话框 -->
      <el-dialog
        v-model="materialStore.detailDialogVisible"
        :title="materialStore.currentMaterial?.productName || '辅料详情'"
        :width="'55%'"
        :max-width="'1000px'"
        :close-on-click-modal="true"
        :close-on-press-escape="true"
        @close="materialStore.hideDetail()"
      >
        <el-scrollbar height="75vh" wrap-style="overflow-x: hidden;" v-if="materialStore.currentMaterial">
          <!-- 主要信息区域 -->
          <div style="margin-bottom: 20px;">
            <!-- 图片区域 -->
            <div style="margin-bottom: 15px;">
              <el-image 
                v-if="materialStore.currentMaterial.image || materialStore.currentMaterial.imageUrl || materialStore.currentMaterial.images"
                :src="getProcessedImageUrl(materialStore.currentMaterial)"
                loading="lazy"
                fit="contain" 
                style="width: 100%; max-height: 400px; border-radius: 8px; background-color: #f5f7fa;"
                preview-teleported
                :preview-src-list="getImagePreviewList(materialStore.currentMaterial)"
              >
                <template #error>
                  <div style="width: 100%; height: 400px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 8px;">
                    <el-empty description="图片加载失败" :image-size="80" />
                  </div>
                </template>
              </el-image>
              <el-empty v-else description="暂无图片" :image-size="80" />
            </div>
            
            <!-- 基本信息区域 -->
            <div style="margin-bottom: 15px;">
              <!-- 标题和编码 -->
              <div style="margin-bottom: 15px;">
                <el-space direction="vertical" size="small" style="width: 100%;">
                  <el-text :type="'primary'" :size="'large'" :strong="true">
                    {{ materialStore.currentMaterial.productName }}
                  </el-text>
                  <el-text :type="'info'" size="small">
                    编码: {{ materialStore.currentMaterial.productCode }}
                  </el-text>
                </el-space>
              </div>
              
              <!-- 快速信息标签 -->
              <div style="margin-bottom: 15px;">
                <el-space wrap>
                  <el-tag>{{ materialStore.currentMaterial.category }}</el-tag>
                  <el-tag v-if="materialStore.currentMaterial.specification">{{ materialStore.currentMaterial.specification }}</el-tag>
                  <el-tag v-if="materialStore.currentMaterial.unit">{{ materialStore.currentMaterial.unit }}</el-tag>
                  <el-tag 
                    :type="materialStore.currentMaterial.stock <= 0 ? 'danger' : materialStore.currentMaterial.stock < 100 ? 'warning' : 'success'"
                  >
                    {{ materialStore.currentMaterial.stock || 0 }} {{ materialStore.currentMaterial.unit }}
                  </el-tag>
                </el-space>
              </div>
              
              <!-- 详细信息 -->
              <el-descriptions :column="2" :size="'small'" border>
                <el-descriptions-item label="分类">{{ materialStore.currentMaterial.category }}</el-descriptions-item>
                <el-descriptions-item label="规格">{{ materialStore.currentMaterial.specification || '未设置' }}</el-descriptions-item>
                <el-descriptions-item label="单位">{{ materialStore.currentMaterial.unit }}</el-descriptions-item>
                <el-descriptions-item label="单价">
                  <el-text type="danger" :strong="true" style="font-size: 16px;">¥{{ materialStore.currentMaterial.price }}</el-text>
                </el-descriptions-item>
                <el-descriptions-item label="库存">
                  <el-tag :type="materialStore.currentMaterial.stock <= 0 ? 'danger' : materialStore.currentMaterial.stock < 100 ? 'warning' : 'success'">
                    {{ materialStore.currentMaterial.stock || 0 }} {{ materialStore.currentMaterial.unit }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="供应商">{{ materialStore.currentMaterial.supplier || '未指定' }}</el-descriptions-item>
                <el-descriptions-item label="预计货期">{{ materialStore.currentMaterial.expectedDeliveryDays || 0 }} 天</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ materialStore.currentMaterial.updatedAt || '未知' }}</el-descriptions-item>
              </el-descriptions>
              
              <!-- 详细描述 -->
              <div style="margin-top: 15px;">
                <el-collapse v-model="detailCollapseActive">
                  <el-collapse-item title="详细描述" name="description">
                    <el-text>
                      {{ materialStore.currentMaterial.description || '无描述' }}
                    </el-text>
                  </el-collapse-item>
                </el-collapse>
              </div>
              
              <!-- 操作按钮 -->
              <div style="margin-top: 15px; display: flex; justify-content: flex-end; gap: 8px; flex-wrap: wrap;">
                <el-button 
                  type="success" 
                  :icon="Upload"
                  size="small"
                  @click="openUploadMoreImagesDialog"
                  title="上传更多图片"
                >
                  上传更多图片
                </el-button>
                <el-button 
                  :type="isFavorited(materialStore.currentMaterial.id) ? 'warning' : 'default'" 
                  :icon="Star"
                  size="small"
                  @click="toggleFavorite(materialStore.currentMaterial.id)"
                  :title="isFavorited(materialStore.currentMaterial.id) ? '取消收藏' : '添加收藏'"
                >
                  {{ isFavorited(materialStore.currentMaterial.id) ? '已收藏' : '收藏' }}
                </el-button>
                <el-button 
                  type="info" 
                  :icon="Share"
                  size="small"
                  @click="shareMaterial(materialStore.currentMaterial)"
                >
                  分享
                </el-button>
                <el-button 
                  type="warning" 
                  :icon="DataAnalysis"
                  size="small"
                  @click="compareMaterials(materialStore.currentMaterial)"
                >
                  对比
                </el-button>
                <el-button 
                  type="primary" 
                  :icon="ShoppingCart"
                  size="small"
                  @click="openProjectSchemeDialog(materialStore.currentMaterial)"
                >
                  加入项目方案
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 相似辅料推荐 -->
          <div style="margin-bottom: 20px;">
            <el-space justify="space-between" style="width: 100%; margin-bottom: 10px;">
              <el-text type="primary" size="large">相似辅料推荐</el-text>
              <el-button size="small" @click="refreshSimilarMaterials(materialStore.currentMaterial)">
                刷新推荐
              </el-button>
            </el-space>
            
            <el-row v-if="materialStore.similarMaterials.length > 0" :gutter="10">
              <el-col v-for="item in materialStore.similarMaterials" :key="item.id" :span="12">
                <el-card 
                  shadow="hover" 
                  :body-style="{ padding: '10px' }"
                  @click="showDetail(item, false)"
                  style="cursor: pointer;"
                >
                  <el-image 
                    v-if="getProcessedImageUrl(item)"
                    v-lazy="getProcessedImageUrl(item)"
                    fit="cover"
                    style="width: 100%; height: 80px; border-radius: 4px; margin-bottom: 8px;"
                    @click.stop
                  >
                    <template #error>
                      <div style="width: 100%; height: 80px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 4px;">
                        <el-empty description="暂无图片" :image-size="40" />
                      </div>
                    </template>
                  </el-image>
                  <el-empty v-else description="暂无图片" :image-size="40" style="margin-bottom: 8px;" />
                  <el-text :truncate="{ rows: 1 }" :strong="true" style="display: block; margin-bottom: 6px; font-size: 13px;">
                    {{ item.productName }}
                  </el-text>
                  <el-progress 
                    :percentage="item.similarity * 100" 
                    :format="() => `${(item.similarity * 100).toFixed(0)}%`" 
                    :size="'small'" 
                    style="margin-bottom: 6px;"
                  />
                  <el-text type="danger" :strong="true" style="font-size: 14px;">
                    ¥{{ item.price.toFixed(2) }}
                  </el-text>
                  <div style="margin-top: 8px; display: flex; justify-content: flex-end; gap: 6px;">
                    <el-button 
                      size="small" 
                      type="primary" 
                      circle 
                      :icon="ShoppingCart"
                      @click.stop="openProjectSchemeDialog(item)"
                      title="加入项目"
                    />
                    <el-button 
                      size="small" 
                      circle 
                      :icon="Star"
                      :type="isFavorited(item.id) ? 'warning' : ''"
                      @click.stop="toggleFavorite(item.id)"
                      title="收藏"
                    />
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-else description="暂无相似辅料数据" :image-size="60" />
          </div>
          
          <!-- 推荐辅料 -->
          <div>
            <el-text type="primary" size="large" style="display: block; margin-bottom: 10px;">你可能需要</el-text>
            
            <el-row v-if="materialStore.recommendations.length > 0" :gutter="10">
              <el-col v-for="rec in materialStore.recommendations" :key="rec.id" :span="12">
                <el-card 
                  shadow="hover" 
                  :body-style="{ padding: '10px' }"
                >
                  <el-image 
                    v-if="rec.image"
                    v-lazy="rec.image"
                    fit="cover"
                    style="width: 100%; height: 80px; border-radius: 4px; margin-bottom: 8px;"
                  />
                  <el-empty v-else description="暂无图片" :image-size="40" style="margin-bottom: 8px;" />
                  <el-text :truncate="{ rows: 1 }" :strong="true" style="display: block; margin-bottom: 6px; font-size: 13px;">
                    {{ rec.productName }}
                  </el-text>
                  <el-tag size="small" type="success" style="margin-bottom: 6px;">{{ rec.reason }}</el-tag>
                  <el-text type="danger" :strong="true" style="font-size: 14px;">
                    ¥{{ rec.price.toFixed(2) }}
                  </el-text>
                  <div style="margin-top: 8px; display: flex; justify-content: flex-end; gap: 6px;">
                    <el-button 
                      size="small" 
                      type="primary" 
                      circle 
                      :icon="ShoppingCart"
                      @click="openProjectSchemeDialog(rec)"
                      title="加入项目"
                    />
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-empty v-else description="暂无推荐数据" :image-size="60" />
          </div>
        </el-scrollbar>
      </el-dialog>
      
      <!-- 上传更多图片对话框 -->
      <el-dialog v-model="uploadMoreImagesDialog" title="上传更多图片" width="500px" append-to-body>
        <div style="padding: 20px 0;">
          <el-upload
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleMoreImagesFileChange"
            :on-remove="handleMoreImagesFileRemove"
            :file-list="moreImagesFiles"
            list-type="picture"
            multiple
            :limit="5"
            :disabled="isUploadingMoreImages"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              拖拽图片到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 JPG、PNG、BMP 格式，文件大小不超过 10MB，最多上传 5 张图片
              </div>
            </template>
          </el-upload>
        </div>
        
        <template #footer>
          <el-button @click="uploadMoreImagesDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleMoreImagesUpload"
            :loading="isUploadingMoreImages"
          >
            确定上传
          </el-button>
        </template>
      </el-dialog>
      
      <!-- 选择项目方案对话框 -->
      <el-dialog v-model="projectSchemeDialogVisible" title="选择项目方案" width="500px" append-to-body>
        <el-form :model="{}" label-width="80px">
          <el-form-item label="选择项目" required>
            <el-select v-model="selectedProject" placeholder="请选择项目" style="width: 100%" @change="handleProjectChange">
              <el-option v-for="project in projectList" :key="project.id" :label="project.projectName" :value="project" />
            </el-select>
          </el-form-item>
          <el-form-item label="选择方案" required>
            <el-select v-model="selectedScheme" placeholder="请选择方案" style="width: 100%" :disabled="!selectedProject">
              <el-option 
                v-for="scheme in selectedProject?.schemes || []" 
                :key="scheme.id" 
                :label="`${scheme.schemeName} (${scheme.status})`" 
                :value="scheme.id" 
                :disabled="scheme.status === '已确定'"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="projectSchemeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addToProject">确定添加</el-button>
        </template>
      </el-dialog>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMaterialStore } from '@/stores/material'
import { ElMessageBox, ElMessage } from 'element-plus'
import { changePassword } from '@/api/user'
import { getMaterialList, recognizeMaterial, searchByImage } from '@/api/material'
import { saveProduct, updateProduct, recommendProducts } from '@/api/product'
import { getProjectList, addMaterialToScheme } from '@/api/project'
import { getInventoryList } from '@/api/inventory'
import { uploadFile, deleteFile, checkFileExists, uploadMultipleFiles } from '@/api/file'
import { SwitchButton, OfficeBuilding, Link, GoodsFilled, UserFilled, Edit, ArrowLeft, Search, Odometer, Folder, List, ShoppingCart, Box, PieChart, Setting, Upload, UploadFilled, Star, Share, DataAnalysis } from '@element-plus/icons-vue'
import { processImageUrl, getProcessedImageUrl, getImagePreviewList } from '@/utils/imageProcessor'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const materialStore = useMaterialStore()

// 辅料详情折叠面板状态
const detailCollapseActive = ref(['description'])

// 收藏功能
const favoriteMaterials = ref(new Set())

// 上传更多图片对话框
const uploadMoreImagesDialog = ref(false)
const moreImagesFiles = ref([])
const isUploadingMoreImages = ref(false)

// 项目方案对话框
const projectSchemeDialogVisible = ref(false)
const selectedProject = ref(null)
const selectedScheme = ref(null)
const projectList = ref([])
const currentMaterialForProject = ref(null)

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

// 菜单项数据
const menuItems = [
  {
    index: '/dashboard',
    icon: Odometer,
    title: '仪表盘'
  },
  {
    index: '/material-library',
    icon: Search,
    title: '智能辅料库'
  },
  {
    index: '/project-scheme',
    icon: Folder,
    title: '项目方案管理'
  },
  {
    index: '/requirement-management',
    icon: List,
    title: '采购需求管理'
  },
  {
    index: '/purchase-management',
    icon: ShoppingCart,
    title: '采购管理'
  },
  {
    index: '/inventory-management',
    icon: Box,
    title: '库存管理',
    requireAdmin: true
  },
  {
    index: '/material-management',
    icon: GoodsFilled,
    title: '辅料管理',
    requireAdmin: true
  },
  {
    index: '/supplier',
    icon: UserFilled,
    title: '供应商管理',
    requireAdmin: true,
    children: [
      {
        index: '/supplier',
        icon: OfficeBuilding,
        title: '供应商库'
      },
      {
        index: '/supplier/relationship',
        icon: Link,
        title: '供需关系管理'
      }
    ]
  },
  {
    index: '/data-analysis',
    icon: PieChart,
    title: '数据分析与报表'
  },
  {
    index: '/system-management',
    icon: Setting,
    title: '系统管理',
    requireAdmin: true,
    children: [
      {
        index: '/system-management',
        icon: Setting,
        title: '系统概览'
      },
      {
        index: '/category-management',
        icon: List,
        title: '分类管理'
      },
      {
        index: '/user',
        icon: UserFilled,
        title: '用户管理'
      }
    ]
  }
]



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

// 侧边栏状态管理
const isSidebarCollapsed = ref(false)

// 菜单状态管理
const openMenus = ref([])

// 加载菜单状态
const loadMenuState = () => {
  // 从localStorage加载侧边栏状态
  const collapsed = localStorage.getItem('sidebarCollapsed')
  if (collapsed !== null) {
    isSidebarCollapsed.value = JSON.parse(collapsed)
  }
  
  // 从localStorage加载菜单展开状态
  const savedOpenMenus = localStorage.getItem('openMenus')
  if (savedOpenMenus) {
    try {
      openMenus.value = JSON.parse(savedOpenMenus)
    } catch (e) {
      console.error('Failed to parse openMenus from localStorage:', e)
    }
  }
}

// 保存菜单状态
const saveMenuState = () => {
  localStorage.setItem('sidebarCollapsed', JSON.stringify(isSidebarCollapsed.value))
  localStorage.setItem('openMenus', JSON.stringify(openMenus.value))
}

// 切换侧边栏
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
  saveMenuState()
}

// 处理菜单选择
const handleMenuSelect = (key, keyPath) => {
  // 当选择的是子菜单项时，保持父菜单展开
  // 当选择的是非父菜单项时，收起所有展开的父菜单
  if (keyPath.length === 1) {
    // 选择的是顶级菜单项，不是父菜单
    openMenus.value = []
  }
  saveMenuState()
}

// 处理菜单展开
const handleMenuOpen = (key, keyPath) => {
  // 只保存当前展开的父菜单
  openMenus.value = [key]
  saveMenuState()
}

// 处理菜单收起
const handleMenuClose = (key, keyPath) => {
  openMenus.value = openMenus.value.filter(menuKey => menuKey !== key)
  saveMenuState()
}

// 点击外部区域关闭菜单
const handleClickOutside = (event) => {
  // 点击外部区域关闭所有展开的菜单
  if (openMenus.value.length > 0) {
    openMenus.value = []
    saveMenuState()
  }
}

// 页面加载时执行
onMounted(() => {
  // 加载菜单状态
  loadMenuState()
  // 监听路由变化
  router.afterEach(() => {
    // 路由变化时可以执行一些操作
  })
  
  // 加载收藏数据
  loadFavorites()
})

// 加载收藏数据
const loadFavorites = () => {
  const savedFavorites = localStorage.getItem('materialFavorites')
  if (savedFavorites) {
    favoriteMaterials.value = new Set(JSON.parse(savedFavorites))
  }
}

// 检查是否收藏
const isFavorited = (materialId) => {
  return favoriteMaterials.value.has(materialId)
}

// 切换收藏状态
const toggleFavorite = (materialId) => {
  if (favoriteMaterials.value.has(materialId)) {
    favoriteMaterials.value.delete(materialId)
    ElMessage.success('已取消收藏')
  } else {
    favoriteMaterials.value.add(materialId)
    ElMessage.success('已添加到收藏')
  }
  
  // 保存到localStorage
  localStorage.setItem('materialFavorites', JSON.stringify([...favoriteMaterials.value]))
}



// 加载推荐辅料
const loadRecommendations = (item) => {
  // Mock collaborative filtering recommendations
  // In a real app, this would call an API like /api/recommendations?productId=...
  const recommendations = [
    {
      id: 101,
      productName: '推荐搭配：' + (item.category === '面料' ? '同色系纽扣' : '配套里布'),
      price: 15.5,
      image: '',
      reason: '95%的用户同时也购买了此商品'
    },
    {
      id: 102,
      productName: '推荐搭配：' + (item.category === '面料' ? '缝纫线' : '粘合衬'),
      price: 5.0,
      image: '',
      reason: '经常一起使用的辅料'
    }
  ]
  materialStore.setRecommendations(recommendations)
}

// 加载相似辅料
const loadSimilarMaterials = async (item) => {
  try {
    const res = await recommendProducts(item.id)
    
    if (res.code === 200 && res.data) {
      // Map backend products to frontend format
      const similarMaterials = res.data.map((prod) => {
        // Process image URL using the same logic as main materials
        const processedImage = getProcessedImageUrl(prod)
        
        const processedMaterial = {
          ...prod,
          image: processedImage, // Use the same image processing logic
          similarity: prod.similarity // Use real similarity score from backend
        }
        
        return processedMaterial
      })
      
      materialStore.setSimilarMaterials(similarMaterials)
    } else {
      materialStore.setSimilarMaterials([])
    }
  } catch (error) {
    console.error('加载相似辅料失败:', error)
    materialStore.setSimilarMaterials([])
  }
}

// 刷新相似辅料推荐
const refreshSimilarMaterials = (item) => {
  loadSimilarMaterials(item)
  ElMessage.success('相似辅料推荐已刷新')
}

// 显示辅料详情
const showDetail = async (item, reloadRecommendations = true) => {
  // 获取真实库存数据
  try {
    const inventoryRes = await getInventoryList()
    const inventoryData = inventoryRes.data || []
    const inventoryMap = new Map()
    
    inventoryData.forEach(inv => {
      // 使用产品编码或物料编码作为键，确保能正确匹配
      const productCode = inv.productCode || inv.materialCode || inv.product_code || inv.material_code
      if (productCode) {
        inventoryMap.set(productCode, inv)
      }
    })
    
    // 更新当前物料的库存数据
    const inventory = inventoryMap.get(item.productCode)
    if (inventory) {
      item.stock = inventory.quantity || 0
    } else {
      item.stock = 0
    }
  } catch (error) {
    console.error('获取库存数据失败:', error)
    // 如果获取失败，使用默认值0
    item.stock = 0
  }
  
  if (reloadRecommendations) {
    loadRecommendations(item)
    loadSimilarMaterials(item)
  }
  
  // 显示对话框
  materialStore.showDetail(item)
}

// 分享辅料
const shareMaterial = (material) => {
  if (navigator.share) {
    navigator.share({
      title: material.productName,
      text: `查看辅料: ${material.productName} - ¥${material.price}`,
      url: window.location.origin + '/#/material-library'
    })
  } else {
    // Fallback for browsers that don't support Web Share API
    const shareText = `查看辅料: ${material.productName} - ¥${material.price}\n编码: ${material.productCode}\n链接: ${window.location.origin + '/#/material-library'}`
    navigator.clipboard.writeText(shareText)
      .then(() => {
        ElMessage.success('分享信息已复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动分享')
      })
  }
}

// 对比辅料
const compareMaterials = (material) => {
  ElMessage.info('材料对比功能开发中...')
}

// 打开上传更多图片对话框
const openUploadMoreImagesDialog = () => {
  moreImagesFiles.value = []
  uploadMoreImagesDialog.value = true
}

// 处理更多图片文件变化
const handleMoreImagesFileChange = (file, fileList) => {
  moreImagesFiles.value = fileList
}

// 处理更多图片文件移除
const handleMoreImagesFileRemove = (file, fileList) => {
  moreImagesFiles.value = fileList
}

// 处理更多图片上传
const handleMoreImagesUpload = async () => {
  if (moreImagesFiles.value.length === 0) {
    ElMessage.warning('请先选择要上传的图片')
    return
  }
  
  isUploadingMoreImages.value = true
  
  try {
    // 上传多张图片
    const uploadedFiles = await uploadMultipleFiles(moreImagesFiles.value)
    
    // 处理上传结果
    if (uploadedFiles && uploadedFiles.length > 0) {
      ElMessage.success(`成功上传 ${uploadedFiles.length} 张图片`)
      
      // 可选：更新当前物料的图片信息
      if (materialStore.currentMaterial) {
        // 这里可以根据实际需求更新物料的图片信息
        // 例如：materialStore.currentMaterial.images = [...]
      }
      
      // 关闭对话框
      uploadMoreImagesDialog.value = false
    } else {
      ElMessage.error('图片上传失败，请重试')
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    ElMessage.error('上传图片失败: ' + (error.message || '未知错误'))
  } finally {
    isUploadingMoreImages.value = false
  }
}

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

// 打开选择项目方案对话框
const openProjectSchemeDialog = (item) => {
  currentMaterialForProject.value = item
  loadProjects()
  projectSchemeDialogVisible.value = true
}

// 处理项目选择变化
const handleProjectChange = () => {
  selectedScheme.value = null
}

// 添加辅料到项目方案
const addToProject = async () => {
  if (!selectedProject.value || !selectedScheme.value || !currentMaterialForProject.value) {
    ElMessage.warning('请选择项目和方案')
    return
  }
  
  try {
    const result = await addMaterialToScheme({
      projectId: selectedProject.value.id,
      schemeId: selectedScheme.value,
      materialId: currentMaterialForProject.value.id
    })
    
    if (result.code === 200) {
      ElMessage.success('添加成功')
      projectSchemeDialogVisible.value = false
      // 可选：刷新项目方案列表
    } else {
      ElMessage.error(result.message || '添加失败')
    }
  } catch (error) {
    console.error('添加辅料到项目方案失败:', error)
    ElMessage.error('添加失败: ' + (error.message || '未知错误'))
  }
}
</script>

<style scoped>
/* 全局布局 */
.layout-container {
  height: 100vh;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}

/* 移动端菜单展开按钮 - 默认隐藏 */
.mobile-menu-toggle {
  display: none !important;
}

/* 侧边栏 */
.sidebar {
  background-color: #ffffff;
  overflow: hidden;
  border-radius: 0 24px 24px 0;
  box-shadow: 0 0 30px rgba(33, 150, 243, 0.12);
  border: 1px solid #e0f2fe;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.sidebar:hover {
  box-shadow: 0 0 40px rgba(33, 150, 243, 0.18);
}

/* 侧边栏折叠状态 */
.sidebar.sidebar-collapsed {
  width: 80px !important;
}

/* 菜单搜索 */
.menu-header {
  padding: 0 16px 16px;
}

.menu-search {
  border-radius: 12px !important;
  border: 2px solid #e0f2fe !important;
  transition: all 0.3s ease !important;
}

.menu-search:focus {
  border-color: #2196f3 !important;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.2) !important;
}

/* 侧边栏切换按钮 */
.sidebar-toggle {
  position: absolute;
  top: 50%;
  right: -10px;
  transform: translateY(-50%);
  width: 20px;
  height: 40px;
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  border-radius: 0 10px 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
  transition: all 0.3s ease;
  z-index: 10;
}

.sidebar-toggle:hover {
  background: linear-gradient(135deg, #1565c0 0%, #1e88e5 100%);
  box-shadow: 0 6px 16px rgba(33, 150, 243, 0.4);
  transform: translateY(-50%) scale(1.1);
}

.sidebar-toggle el-icon {
  color: white;
  font-size: 16px;
  transition: all 0.3s ease;
}

.rotate-180 {
  transform: rotate(180deg);
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
  transition: all 0.3s ease;
}

.logo-collapsed {
  height: 72px;
  justify-content: center;
  padding: 0;
}

.logo-icon {
  font-size: 36px;
  color: white;
  margin-right: 16px;
  animation: logoFloat 3s ease-in-out infinite;
  transition: all 0.3s ease;
}

.sidebar-collapsed .logo-icon {
  margin-right: 0;
  font-size: 32px;
}

.logo h2 {
  color: white;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  animation: titleSlideIn 0.5s ease-out;
}

/* Logo 浮动动画 */
@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

/* 标题滑入动画 */
@keyframes titleSlideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 菜单 */
.custom-menu {
  border-right: none;
  margin-top: 24px;
  background: transparent !important;
  max-height: calc(100vh - 160px);
  overflow-y: auto;
}

/* 滚动条样式 */
.custom-menu::-webkit-scrollbar {
  width: 6px;
}

.custom-menu::-webkit-scrollbar-track {
  background: #f0f9ff;
  border-radius: 3px;
}

.custom-menu::-webkit-scrollbar-thumb {
  background: #bbdefb;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.custom-menu::-webkit-scrollbar-thumb:hover {
  background: #90caf9;
}

/* 子菜单容器 */
:deep(.el-sub-menu .el-menu) {
  background: transparent !important;
  animation: submenuSlideIn 0.3s ease-out;
}

/* 子菜单滑入动画 */
@keyframes submenuSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
  position: relative;
  z-index: 1;
}

.sub-menu-icon {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s ease;
  color: #2196f3;
  position: relative;
  z-index: 1;
}

/* 侧边栏折叠状态下的样式 */
.sidebar.sidebar-collapsed :deep(.el-menu-item) {
  justify-content: center !important;
  padding: 14px !important;
  margin: 8px 8px !important;
}

.sidebar.sidebar-collapsed :deep(.el-menu-item span) {
  display: none !important;
}

.sidebar.sidebar-collapsed :deep(.el-menu-item .menu-icon) {
  margin-right: 0 !important;
  font-size: 24px !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu__title) {
  justify-content: center !important;
  padding: 14px !important;
  margin: 8px 8px !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu__title span) {
  display: none !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu__title .menu-icon) {
  margin-right: 0 !important;
  font-size: 24px !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu__title .el-sub-menu__icon-arrow) {
  display: none !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu .el-menu) {
  position: absolute !important;
  left: 100% !important;
  top: 0 !important;
  min-width: 180px !important;
  background: white !important;
  border-radius: 0 16px 16px 0 !important;
  box-shadow: 0 0 20px rgba(33, 150, 243, 0.15) !important;
  border: 1px solid #e0f2fe !important;
  z-index: 100 !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu .el-menu-item) {
  justify-content: flex-start !important;
  padding: 12px 16px !important;
  margin: 4px 8px !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu .el-menu-item span) {
  display: inline !important;
}

.sidebar.sidebar-collapsed :deep(.el-sub-menu .el-menu-item .sub-menu-icon) {
  margin-right: 12px !important;
  font-size: 18px !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .sidebar {
    width: 200px !important;
  }
  
  .sidebar.sidebar-collapsed {
    width: 70px !important;
  }
  
  .logo h2 {
    font-size: 18px !important;
  }
  
  .logo-icon {
    font-size: 30px !important;
  }
  
  .sidebar.sidebar-collapsed .logo-icon {
    font-size: 28px !important;
  }
  
  :deep(.el-menu-item) {
    padding: 12px 20px !important;
    font-size: 13px !important;
  }
  
  :deep(.el-sub-menu__title) {
    padding: 12px 20px !important;
    font-size: 13px !important;
  }
  
  .menu-icon {
    font-size: 18px !important;
    margin-right: 12px !important;
  }
  
  .sub-menu-icon {
    font-size: 16px !important;
    margin-right: 10px !important;
  }
}

@media (max-width: 992px) {
  .sidebar {
    width: 70px !important;
  }
  
  .sidebar :deep(.el-menu-item span) {
    display: none !important;
  }
  
  .sidebar :deep(.el-menu-item) {
    justify-content: center !important;
    padding: 14px !important;
    margin: 8px 8px !important;
  }
  
  .sidebar :deep(.el-menu-item .menu-icon) {
    margin-right: 0 !important;
    font-size: 24px !important;
  }
  
  .sidebar :deep(.el-sub-menu__title) {
    justify-content: center !important;
    padding: 14px !important;
    margin: 8px 8px !important;
  }
  
  .sidebar :deep(.el-sub-menu__title span) {
    display: none !important;
  }
  
  .sidebar :deep(.el-sub-menu__title .menu-icon) {
    margin-right: 0 !important;
    font-size: 24px !important;
  }
  
  .sidebar :deep(.el-sub-menu__title .el-sub-menu__icon-arrow) {
    display: none !important;
  }
  
  .sidebar :deep(.el-sub-menu .el-menu) {
    position: absolute !important;
    left: 100% !important;
    top: 0 !important;
    min-width: 160px !important;
  }
  
  .sidebar :deep(.el-sub-menu .el-menu-item) {
    justify-content: flex-start !important;
    padding: 12px 16px !important;
    margin: 4px 8px !important;
  }
  
  .sidebar :deep(.el-sub-menu .el-menu-item span) {
    display: inline !important;
  }
  
  .sidebar :deep(.el-sub-menu .el-menu-item .sub-menu-icon) {
    margin-right: 12px !important;
    font-size: 18px !important;
  }
  
  .logo h2 {
    display: none !important;
  }
  
  .logo {
    justify-content: center !important;
    padding: 0 !important;
  }
  
  .logo-icon {
    margin-right: 0 !important;
  }
  
  .menu-header {
    display: none !important;
  }
}

@media (max-width: 768px) {
  .layout-container {
    flex-direction: column !important;
  }
  
  .sidebar {
    position: fixed !important;
    left: 0 !important;
    top: 0 !important;
    height: 100vh !important;
    z-index: 1000 !important;
    border-radius: 0 !important;
    transform: translateX(0) !important;
    transition: transform 0.3s ease !important;
  }
  
  .sidebar.sidebar-collapsed {
    transform: translateX(-100%) !important;
  }
  
  .sidebar-toggle {
    right: 10px !important;
  }
  
  .el-main {
    margin-left: 0 !important;
  }
  
  .header {
    margin-left: 0 !important;
  }
  
  /* 移动端菜单展开按钮 */
  .mobile-menu-toggle {
    display: block !important;
    position: fixed !important;
    left: 10px !important;
    top: 10px !important;
    z-index: 1001 !important;
    background: white !important;
    border: 2px solid #e0f2fe !important;
    border-radius: 50% !important;
    width: 48px !important;
    height: 48px !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    box-shadow: 0 4px 16px rgba(33, 150, 243, 0.3) !important;
    cursor: pointer !important;
    transition: all 0.3s ease !important;
  }
  
  .mobile-menu-toggle:hover {
    background: #f0f9ff !important;
    transform: scale(1.1) !important;
  }
}

/* 菜单展开/收起动画 */
:deep(.el-sub-menu .el-menu-item) {
  animation: menuItemSlideIn 0.3s ease-out;
}

@keyframes menuItemSlideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 菜单项点击效果 */
:deep(.el-menu-item) {
  cursor: pointer;
  position: relative;
}

:deep(.el-menu-item:active) {
  transform: translateX(8px) scale(0.98) !important;
}

:deep(.el-sub-menu__title:active) {
  transform: translateX(8px) scale(0.98) !important;
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
  background: rgba(255, 255, 255, 0.6) !important;
  backdrop-filter: blur(12px) !important;
  -webkit-backdrop-filter: blur(12px) !important;
  border: 1px solid rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.08) !important;
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
  background: rgba(255, 255, 255, 0.6) !important;
  backdrop-filter: blur(12px) !important;
  -webkit-backdrop-filter: blur(12px) !important;
  border: 1px solid rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.08) !important;
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
  background: rgba(255, 255, 255, 0.6) !important;
  backdrop-filter: blur(12px) !important;
  -webkit-backdrop-filter: blur(12px) !important;
  border: 1px solid rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.08) !important;
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
