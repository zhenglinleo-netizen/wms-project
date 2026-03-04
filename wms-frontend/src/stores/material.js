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
    recommendations: [],
    // 项目方案对话框显示状态
    projectSchemeDialogVisible: false,
    // 当前要添加到项目方案的辅料
    currentMaterialForProject: null
  }),
  
  actions: {
    // 显示辅料详情对话框
    showDetail(material) {
      this.currentMaterial = material
      this.detailDialogVisible = true
      
      // 自动加载推荐内容
      if (material && material.id) {
        // 导入需要的函数
        import('@/api/product').then(({ recommendProducts }) => {
          // 加载相似辅料
          recommendProducts(material.id).then(res => {
            if (res.code === 200 && res.data) {
              // 导入图片处理函数
              import('@/utils/imageProcessor').then(({ getProcessedImageUrl }) => {
                // 处理推荐辅料的图片URL
                const similarMaterials = res.data.map((prod) => {
                  const processedImage = getProcessedImageUrl(prod)
                  return {
                    ...prod,
                    image: processedImage,
                    similarity: prod.similarity
                  }
                })
                this.setSimilarMaterials(similarMaterials)
              })
            } else {
              this.setSimilarMaterials([])
            }
          }).catch(error => {
            console.error('加载相似辅料失败:', error)
            this.setSimilarMaterials([])
          })
        })
      }
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
    },
    
    // 显示项目方案对话框
    showProjectSchemeDialog(material) {
      this.currentMaterialForProject = material
      this.projectSchemeDialogVisible = true
    },
    
    // 隐藏项目方案对话框
    hideProjectSchemeDialog() {
      this.projectSchemeDialogVisible = false
      this.currentMaterialForProject = null
    }
  }
})
