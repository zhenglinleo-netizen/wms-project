<template>
  <div style="padding: 20px;">
    <el-button @click="testSimilarMaterials" type="primary">测试相似辅料推荐</el-button>
    <el-button @click="clearLogs" type="warning">清除日志</el-button>
    
    <div style="margin-top: 20px;">
      <el-text type="primary" size="large">测试结果</el-text>
      <el-divider></el-divider>
      
      <div v-if="similarMaterials.length > 0">
        <el-row :gutter="20">
          <el-col v-for="item in similarMaterials" :key="item.id" :xs="24" :sm="12" :md="8">
            <el-card shadow="hover" style="margin-bottom: 20px;">
              <div style="text-align: center;">
                <el-image 
                  v-if="item.image" 
                  :src="item.image" 
                  fit="cover" 
                  style="width: 100%; height: 150px; border-radius: 4px; margin-bottom: 10px;"
                >
                  <template #error>
                    <div style="width: 100%; height: 150px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 4px;">
                      <el-empty description="暂无图片" :image-size="60" />
                    </div>
                  </template>
                </el-image>
                <el-empty v-else description="暂无图片" :image-size="60" style="margin-bottom: 10px;" />
                <el-text :truncate="{ rows: 1 }" :strong="true" style="display: block; margin-bottom: 10px;">
                  {{ item.productName }}
                </el-text>
                <el-text type="info">相似度: {{ (item.similarity * 100).toFixed(0) }}%</el-text>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <el-empty v-else description="暂无数据" :image-size="100" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { recommendProducts } from '@/api/product'
import { processImageUrl } from '@/utils/imageProcessor'

const similarMaterials = ref([])

const testSimilarMaterials = async () => {
  try {
    console.log('开始测试相似辅料推荐...')
    // 使用一个已知的辅料ID进行测试
    const productId = 1 // 替换为实际存在的辅料ID
    console.log('测试的辅料ID:', productId)
    
    const res = await recommendProducts(productId)
    console.log('后端返回的推荐结果:', res)
    
    if (res.code === 200 && res.data) {
      console.log('推荐辅料数据:', res.data)
      
      // 处理推荐辅料的图片URL
      const processedMaterials = res.data.map((prod) => {
        console.log('处理前的辅料数据:', prod)
        
        // 处理图片URL
        let imageUrl = ''
        if (prod.image) {
          imageUrl = processImageUrl(prod.image)
          console.log('使用prod.image:', prod.image, '处理后:', imageUrl)
        } else if (prod.imageUrl) {
          imageUrl = processImageUrl(prod.imageUrl)
          console.log('使用prod.imageUrl:', prod.imageUrl, '处理后:', imageUrl)
        } else if (prod.images) {
          try {
            const images = JSON.parse(prod.images)
            if (Array.isArray(images) && images.length > 0) {
              imageUrl = processImageUrl(images[0])
              console.log('使用prod.images[0]:', images[0], '处理后:', imageUrl)
            }
          } catch (e) {
            console.error('解析images失败:', e)
          }
        }
        
        console.log('处理后的辅料数据:', {
          ...prod,
          image: imageUrl
        })
        
        return {
          ...prod,
          image: imageUrl
        }
      })
      
      console.log('最终处理后的推荐辅料数据:', processedMaterials)
      similarMaterials.value = processedMaterials
    }
  } catch (error) {
    console.error('测试失败:', error)
  }
}

const clearLogs = () => {
  console.clear()
  similarMaterials.value = []
}
</script>

<style scoped>

</style>