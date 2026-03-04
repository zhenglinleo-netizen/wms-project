<template>
  <router-view v-slot="{ Component }">
    <RouteTransition 
      :default-transition="'fade'"
      :duration="350"
      :route-transitions="routeTransitions"
      :custom-classes="customClasses"
    >
      <component :is="Component" />
    </RouteTransition>
  </router-view>
</template>

<script setup>
import RouteTransition from './components/RouteTransition.vue'

// 为特定路由配置不同的过渡动画
const routeTransitions = {
  // 主要页面使用更生动的动画
  Dashboard: 'bounce',
  Login: 'elastic',
  MaterialLibrary: 'slide-left',
  InventoryManagement: 'slide-right',
  MaterialManagement: 'slide-up',
  ProjectScheme: 'scale',
  RequirementManagement: 'slide-up',
  PurchaseManagement: 'slide-up',
  DataAnalysis: 'elastic',
  SystemManagement: 'fade',
  MaterialRecycle: 'slide-left',
  CategoryManagement: 'slide-right',
  User: 'scale',
  Supplier: 'slide-up',
  SupplierRelationship: 'slide-up',
  // 其他可能的路由
  TestSimilarMaterials: 'bounce',
  Warehouse: 'slide-up',
  Product: 'slide-up'
}

// 自定义动画类名（可选）
const customClasses = {
  enterActive: '',
  leaveActive: '',
  enterFrom: '',
  leaveTo: ''
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  width: 100%;
  height: 100vh;
}
</style>




