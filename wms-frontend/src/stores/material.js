import { defineStore } from 'pinia'

export const useMaterialStore = defineStore('material', {
  state: () => ({
    // 当前选中的辅料
    currentMaterial: null,
    // 辅料详情对话框显示状态
    detailDialogVisible: false,
    // 相似辅料推荐数据
    similarMaterials: [],
    // 推荐辅料数据
    recommendations: []
  }),
  
  actions: {
    // 显示辅料详情对话框
    showDetail(material) {
      this.currentMaterial = material
      this.detailDialogVisible = true
    },
    
    // 隐藏辅料详情对话框
    hideDetail() {
      this.detailDialogVisible = false
      // 可选：清空当前辅料数据
      // this.currentMaterial = null
    },
    
    // 设置相似辅料数据
    setSimilarMaterials(materials) {
      this.similarMaterials = materials
    },
    
    // 设置推荐辅料数据
    setRecommendations(materials) {
      this.recommendations = materials
    }
  }
})
