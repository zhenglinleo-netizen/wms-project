import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'material-library',
        name: 'MaterialLibrary',
        component: () => import('@/views/MaterialLibrary.vue'),
        meta: { title: '智能辅料库' }
      },
      {
        path: 'project-scheme',
        name: 'ProjectScheme',
        component: () => import('@/views/ProjectScheme.vue'),
        meta: { title: '项目方案管理' }
      },
      {
        path: 'requirement-management',
        name: 'RequirementManagement',
        component: () => import('@/views/RequirementManagement.vue'),
        meta: { title: '采购需求管理' }
      },
      {
        path: 'purchase-management',
        name: 'PurchaseManagement',
        component: () => import('@/views/PurchaseManagement.vue'),
        meta: { title: '采购管理' }
      },
      {
        path: 'inventory-management',
        name: 'InventoryManagement',
        component: () => import('@/views/Inventory.vue'),
        meta: { title: '库存管理', requiresAdmin: true }
      },
      {
        path: 'data-analysis',
        name: 'DataAnalysis',
        component: () => import('@/views/DataAnalysis.vue'),
        meta: { title: '数据分析与报表' }
      },
      {
        path: 'system-management',
        name: 'SystemManagement',
        component: () => import('@/views/SystemManagement.vue'),
        meta: { title: '系统管理', requiresAdmin: true }
      },
      {
        path: 'material-management',
        name: 'MaterialManagement',
        component: () => import('@/views/Product.vue'),
        meta: { title: '辅料管理', requiresAdmin: true }
      },
      {
        path: 'category-management',
        name: 'CategoryManagement',
        component: () => import('@/views/Category.vue'),
        meta: { title: '分类管理', requiresAdmin: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理', requiresAdmin: true }
      },
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('@/views/Supplier.vue'),
        meta: { title: '供应商库' },
        children: [
          {
            path: 'relationship',
            name: 'SupplierRelationship',
            component: () => import('@/views/SupplierRelationship.vue'),
            meta: { title: '供需关系管理' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  // 动态导入useUserStore，避免在模块级别调用
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()
  
  // 检查是否已登录
  if (to.path !== '/login' && !userStore.user) {
    // 尝试从sessionStorage中恢复用户状态
    const token = sessionStorage.getItem('token')
    if (token) {
      try {
        // 尝试获取用户信息
        const { getCurrentUser } = await import('@/api/user')
        const res = await getCurrentUser()
        if (res.code === 200) {
          userStore.setUser(res.data)
          // 检查是否需要管理员权限
          if (to.meta.requiresAdmin && res.data.role !== 'admin') {
            next('/dashboard')
            return
          }
          next()
          return
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        sessionStorage.removeItem('token')
      }
    }
    next('/login')
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && userStore.user?.role !== 'admin') {
    next('/dashboard')
    return
  }
  
  next()
})

export default router
