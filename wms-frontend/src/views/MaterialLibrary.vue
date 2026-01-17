<template>
  <div class="material-library">
    <!-- 搜索与筛选栏 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="名称/描述/编号" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.category" placeholder="全部" clearable style="width: 120px">
            <el-option label="面料" value="面料" />
            <el-option label="辅料" value="辅料" />
            <el-option label="扣件" value="扣件" />
          </el-select>
        </el-form-item>
        <el-form-item label="材质">
          <el-select v-model="filters.material" placeholder="全部" clearable style="width: 120px">
            <el-option label="棉" value="棉" />
            <el-option label="麻" value="麻" />
            <el-option label="丝" value="丝" />
            <el-option label="毛" value="毛" />
            <el-option label="涤纶" value="涤纶" />
          </el-select>
        </el-form-item>
        <el-form-item label="颜色">
          <el-select v-model="filters.color" placeholder="全部" clearable style="width: 120px">
            <el-option label="红色" value="红色" />
            <el-option label="蓝色" value="蓝色" />
            <el-option label="绿色" value="绿色" />
            <el-option label="黄色" value="黄色" />
            <el-option label="黑色" value="黑色" />
            <el-option label="白色" value="白色" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="filters.minPrice" placeholder="最低" style="width: 100px" :min="0" />
          <span style="margin: 0 5px">-</span>
          <el-input-number v-model="filters.maxPrice" placeholder="最高" style="width: 100px" :min="0" />
        </el-form-item>
        <el-form-item label="库存">
          <el-select v-model="filters.inStock" placeholder="全部" clearable style="width: 100px">
            <el-option label="有库存" :value="true" />
            <el-option label="无库存" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="warning" :icon="Camera" @click="openAIRecognition">AI 智能识别</el-button>
          <el-button type="info" :icon="Search" @click="openImageSearch">图片搜索</el-button>
          <el-button type="success" :icon="Clock" @click="openAIHistory">识别历史</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 辅料列表 -->
    <el-row :gutter="20" class="material-list">
      <el-col :span="6" v-for="item in materials" :key="item.id" style="margin-bottom: 20px;">
        <el-card :body-style="{ padding: '0px' }" class="material-card" shadow="hover">
          <div class="image-wrapper" @click="showDetail(item)">
            <el-image :src="item.image || 'https://via.placeholder.com/200'" fit="cover" class="image" />
            <div class="hover-overlay">
              <el-button type="primary" size="small" @click.stop="showDetail(item)">查看详情</el-button>
            </div>
          </div>
          <div class="card-content">
            <div class="material-title" :title="item.productName">{{ item.productName }}</div>
            <div class="material-meta">
              <span class="category">{{ item.category || '未分类' }}</span>
              <span class="price">¥{{ item.price }} / {{ item.unit }}</span>
            </div>
            <div class="material-actions">
              <el-button size="small" :icon="Star" circle :type="isFavorited(item.id) ? 'warning' : ''" @click="toggleFavorite(item.id)" />
              <el-button size="small" :icon="ShoppingCart" type="primary" plain circle @click="addToProject(item)" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI 识别弹窗 -->
    <el-dialog v-model="aiDialogVisible" title="AI 智能辅料识别" width="50%">
      <div class="ai-container">
        <div class="upload-section">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
          </el-upload>
        </div>
        
        <div v-if="recognitionResult" class="result-section">
          <el-divider>识别结果 (置信度: {{ recognitionResult.confidence * 100 }}%)</el-divider>
          <el-descriptions border :column="1">
            <el-descriptions-item label="类别">{{ recognitionResult.category }}</el-descriptions-item>
            <el-descriptions-item label="具体类型">{{ recognitionResult.type || '未识别' }}</el-descriptions-item>
            <el-descriptions-item label="材质">{{ recognitionResult.material }}</el-descriptions-item>
            <el-descriptions-item label="颜色">{{ recognitionResult.color }}</el-descriptions-item>
            <el-descriptions-item label="风格">{{ recognitionResult.style || '未识别' }}</el-descriptions-item>
          </el-descriptions>

          <el-divider>人工校正</el-divider>
          <el-form :model="correctionForm" label-width="100px">
            <el-form-item label="校正类别">
              <el-select v-model="correctionForm.category" placeholder="选择类别">
                <el-option label="面料" value="面料" />
                <el-option label="辅料" value="辅料" />
                <el-option label="扣件" value="扣件" />
              </el-select>
            </el-form-item>
            <el-form-item label="校正具体类型">
              <el-select v-model="correctionForm.type" placeholder="选择具体类型" filterable allow-create>
                <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
              </el-select>
            </el-form-item>
            <el-form-item label="校正材质">
              <el-select v-model="correctionForm.material" placeholder="选择材质">
                <el-option label="棉" value="棉" />
                <el-option label="麻" value="麻" />
                <el-option label="丝" value="丝" />
                <el-option label="毛" value="毛" />
                <el-option label="涤纶" value="涤纶" />
                <el-option label="混纺" value="混纺" />
              </el-select>
            </el-form-item>
            <el-form-item label="校正颜色">
              <el-select v-model="correctionForm.color" placeholder="选择颜色">
                <el-option label="红色" value="红色" />
                <el-option label="蓝色" value="蓝色" />
                <el-option label="绿色" value="绿色" />
                <el-option label="黄色" value="黄色" />
                <el-option label="黑色" value="黑色" />
                <el-option label="白色" value="白色" />
                <el-option label="灰色" value="灰色" />
              </el-select>
            </el-form-item>
            <el-form-item label="校正风格">
              <el-select v-model="correctionForm.style" placeholder="选择风格" filterable allow-create>
                <el-option v-for="style in styleOptions" :key="style" :label="style" :value="style" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveCorrection">保存校正结果</el-button>
            </el-form-item>
          </el-form>
          
          <!-- 添加辅料按钮 - 只有管理员可见 -->
          <div class="add-material-section" v-if="userStore.user?.role === 'admin'">
            <el-button type="success" @click="confirmAddMaterial">添加到辅料库</el-button>
          </div>
          
          <div class="similar-section" v-if="recognitionResult.similar && recognitionResult.similar.length">
            <h4>相似辅料推荐</h4>
            <el-table :data="recognitionResult.similar" size="small">
              <el-table-column prop="name" label="名称" />
              <el-table-column prop="price" label="价格" />
              <el-table-column prop="similarity" label="相似度">
                <template #default="scope">
                  {{ scope.row.similarity * 100 }}%
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        
        <div v-if="isRecognizing" class="recognizing">
          <el-icon class="is-loading"><Loading /></el-icon>
          <p>AI 正在分析图片特征...</p>
        </div>
      </div>
    </el-dialog>

    <!-- 图片搜索弹窗 -->
    <el-dialog v-model="imageSearchVisible" title="图片搜索" width="50%">
      <div class="image-search-container">
        <div class="upload-section">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleImageSearchFileChange"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽图片到此处或 <em>点击上传</em>
            </div>
          </el-upload>
        </div>
        
        <div v-if="searchImageResult" class="result-section">
          <el-divider>搜索结果</el-divider>
          <div class="search-results">
            <el-card v-for="item in searchImageResult" :key="item.id" class="result-card">
              <div class="result-content">
                <el-image :src="item.image" fit="cover" class="result-image" />
                <div class="result-info">
                  <div class="result-name">{{ item.productName }}</div>
                  <div class="result-price">¥{{ item.price }}</div>
                  <div class="result-similarity">相似度: {{ item.similarity * 100 }}%</div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
        
        <div v-if="isSearching" class="recognizing">
          <el-icon class="is-loading"><Loading /></el-icon>
          <p>正在搜索相似图片...</p>
        </div>
      </div>
    </el-dialog>

    <!-- AI 识别历史弹窗 -->
    <el-dialog v-model="aiHistoryVisible" title="AI 识别历史" width="80%">
      <div class="history-container">
        <el-table v-loading="false" :data="recognitionHistory" style="width: 100%">
          <el-table-column prop="timestamp" label="识别时间" width="180" />
          <el-table-column prop="category" label="类别" width="100" />
          <el-table-column prop="type" label="具体类型" width="120" />
          <el-table-column prop="material" label="材质" width="100" />
          <el-table-column prop="color" label="颜色" width="100" />
          <el-table-column prop="style" label="风格" width="120" />
          <el-table-column prop="confidence" label="置信度" width="100">
            <template #default="scope">
              {{ (scope.row.confidence * 100).toFixed(1) }}%
            </template>
          </el-table-column>
          <el-table-column prop="image" label="图片预览" width="100">
            <template #default="scope">
              <el-image :src="scope.row.image" fit="cover" style="width: 50px; height: 50px; border-radius: 4px;" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button type="danger" size="small" @click="deleteRecognitionHistory(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div v-if="recognitionHistory.length === 0" class="empty-history">
          <el-empty description="暂无识别历史数据" :image-size="80" />
        </div>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="辅料详情" width="60%">
      <div v-if="currentMaterial" class="detail-container">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-image :src="currentMaterial.image || 'https://via.placeholder.com/300'" fit="cover" class="detail-image" />
          </el-col>
          <el-col :span="14">
            <h3>{{ currentMaterial.productName }}</h3>
            <p class="code">编码: {{ currentMaterial.productCode }}</p>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="分类">{{ currentMaterial.category }}</el-descriptions-item>
              <el-descriptions-item label="规格">{{ currentMaterial.specification }}</el-descriptions-item>
              <el-descriptions-item label="单位">{{ currentMaterial.unit }}</el-descriptions-item>
              <el-descriptions-item label="单价">¥{{ currentMaterial.price }}</el-descriptions-item>
              <el-descriptions-item label="预计货期">{{ currentMaterial.expectedDeliveryDays || 0 }} 天</el-descriptions-item>
              <el-descriptions-item label="库存">
                <span :class="{ 'low-stock': currentMaterial.stock < 100, 'out-of-stock': currentMaterial.stock <= 0 }">
                  {{ currentMaterial.stock || 0 }} {{ currentMaterial.unit }}
                </span>
              </el-descriptions-item>
              <el-descriptions-item label="供应商">{{ currentMaterial.supplier || '未指定' }}</el-descriptions-item>
              <el-descriptions-item label="描述">{{ currentMaterial.description }}</el-descriptions-item>
            </el-descriptions>
            <div class="detail-actions" style="margin-top: 20px;">
              <el-button type="primary" :icon="ShoppingCart" @click="openProjectSchemeDialog(currentMaterial)">加入项目方案</el-button>
              <el-button :icon="Star" :type="isFavorited(currentMaterial.id) ? 'warning' : ''" @click="toggleFavorite(currentMaterial.id)">
                {{ isFavorited(currentMaterial.id) ? '已收藏' : '收藏' }}
              </el-button>
            </div>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">相似辅料 (基于特征向量)</el-divider>
        <div class="similar-materials">
           <div v-if="similarMaterials.length > 0" class="similar-list">
             <el-card v-for="item in similarMaterials" :key="item.id" shadow="hover" class="similar-card">
               <div class="similar-content">
                 <el-image :src="item.image" class="similar-image" @click="showDetail(item)" />
                 <div class="similar-info">
                   <div class="similar-name" @click="showDetail(item)">{{ item.productName }}</div>
                   <div class="similar-similarity">相似度: {{ (item.similarity * 100).toFixed(1) }}%</div>
                   <div class="similar-price">¥{{ item.price }}</div>
                 </div>
                 <el-button type="primary" size="small" circle :icon="ShoppingCart" @click="openProjectSchemeDialog(item)" />
               </div>
             </el-card>
           </div>
           <el-empty v-else description="暂无相似辅料数据" :image-size="60"></el-empty>
        </div>

        <el-divider content-position="left">你可能需要 (协同过滤推荐)</el-divider>
        <div class="recommendations">
           <div v-if="recommendations.length > 0" class="recommendation-list">
             <el-card v-for="rec in recommendations" :key="rec.id" shadow="hover" class="rec-card">
               <div class="rec-content">
                 <el-image :src="rec.image" class="rec-image" />
                 <div class="rec-info">
                   <div class="rec-name">{{ rec.productName }}</div>
                   <div class="rec-reason"><el-tag size="small" type="success">{{ rec.reason }}</el-tag></div>
                   <div class="rec-price">¥{{ rec.price }}</div>
                 </div>
                 <el-button type="primary" size="small" circle :icon="ShoppingCart" @click="openProjectSchemeDialog(rec)" />
               </div>
             </el-card>
           </div>
           <el-empty v-else description="暂无推荐数据" :image-size="60"></el-empty>
        </div>
      </div>
    </el-dialog>
    
    <!-- 添加辅料对话框 -->
    <el-dialog v-model="addMaterialDialog" title="添加新辅料" width="600px">
      <el-form :model="addMaterialForm" :rules="addMaterialRules" ref="addMaterialFormRef" label-width="100px">
        <el-form-item label="辅料编码">
          <el-input v-model="addMaterialForm.productCode" placeholder="请输入辅料编码" />          </el-form-item>
        <el-form-item label="辅料名称">
          <el-input v-model="addMaterialForm.productName" placeholder="请输入辅料名称" />          </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="addMaterialForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="面料" value="面料" />            <el-option label="辅料" value="辅料" />            <el-option label="扣件" value="扣件" />          </el-select>
        </el-form-item>
        <el-form-item label="具体类型">
          <el-select v-model="addMaterialForm.type" placeholder="请选择具体类型" style="width: 100%" filterable allow-create>
            <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />          </el-select>
        </el-form-item>
        <el-form-item label="风格">
          <el-select v-model="addMaterialForm.style" placeholder="请选择风格" style="width: 100%" filterable allow-create>
            <el-option v-for="style in styleOptions" :key="style" :label="style" :value="style" />          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="addMaterialForm.specification" placeholder="请输入规格" />          </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="addMaterialForm.unit" placeholder="请输入单位" />          </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="addMaterialForm.price" :precision="2" :min="0" style="width: 100%" placeholder="请输入单价" />          </el-form-item>
        <el-form-item label="预计货期">
          <el-input-number v-model="addMaterialForm.expectedDeliveryDays" :min="0" style="width: 100%" placeholder="请输入预计货期（天）" />          </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="addMaterialForm.description" type="textarea" :rows="3" placeholder="请输入描述" />          </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="addMaterialForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addMaterialDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAddMaterial">确定添加</el-button>      </template>
    </el-dialog>

    <!-- 选择项目方案对话框 -->
    <el-dialog v-model="projectSchemeDialogVisible" title="选择项目方案" width="500px">
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
        <el-button type="primary" @click="addToProject">确定添加</el-button>      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Camera, Star, ShoppingCart, UploadFilled, Loading, Clock } from '@element-plus/icons-vue'
import { getMaterialList, recognizeMaterial, searchByImage } from '@/api/material'
import { saveProduct } from '@/api/product'
import { getProjectList, addMaterialToScheme } from '@/api/project'
import { getInventoryList } from '@/api/inventory'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const materials = ref([])
const loading = ref(false)
const filters = reactive({
  keyword: '',
  category: '',
  material: '',
  color: '',
  minPrice: '',
  maxPrice: '',
  supplier: '',
  inStock: null
})

// Favorite functionality
const favoriteMaterials = ref(new Set())

// Load favorites from localStorage (mock persistence)
const loadFavorites = () => {
  const savedFavorites = localStorage.getItem('materialFavorites')
  if (savedFavorites) {
    favoriteMaterials.value = new Set(JSON.parse(savedFavorites))
  }
}

// Toggle favorite status
const toggleFavorite = (materialId) => {
  if (favoriteMaterials.value.has(materialId)) {
    favoriteMaterials.value.delete(materialId)
    ElMessage.success('已取消收藏')
  } else {
    favoriteMaterials.value.add(materialId)
    ElMessage.success('已添加到收藏')
  }
  
  // Save favorites to localStorage (mock persistence)
  localStorage.setItem('materialFavorites', JSON.stringify([...favoriteMaterials.value]))
}

// Check if material is favorited
const isFavorited = (materialId) => {
  return favoriteMaterials.value.has(materialId)
}

// AI Recognition
const aiDialogVisible = ref(false)
const isRecognizing = ref(false)
const recognitionResult = ref(null)
const correctionForm = reactive({
  category: '',
  type: '',
  material: '',
  color: '',
  style: ''
})

// 风格选项（支持动态添加和持久化）
const styleOptions = ref([])

// 具体类型选项（支持动态添加和持久化）
const typeOptions = ref([])

// 加载具体类型选项（从localStorage）
const loadTypeOptions = () => {
  const savedTypes = localStorage.getItem('materialTypes')
  if (savedTypes) {
    typeOptions.value = JSON.parse(savedTypes)
  } else {
    // 默认具体类型选项
    typeOptions.value = ['拉链', '纽扣', '线', '衬里', '花边', '标签']
  }
}

// 保存具体类型选项到localStorage
const saveTypeOptions = () => {
  localStorage.setItem('materialTypes', JSON.stringify(typeOptions.value))
}

// 加载风格选项（从localStorage）
const loadStyleOptions = () => {
  const savedStyles = localStorage.getItem('materialStyles')
  if (savedStyles) {
    styleOptions.value = JSON.parse(savedStyles)
  } else {
    // 默认风格选项
    styleOptions.value = ['复古', '现代', '简约', '华丽', '休闲', '商务']
  }
}

// 保存风格选项到localStorage
const saveStyleOptions = () => {
  localStorage.setItem('materialStyles', JSON.stringify(styleOptions.value))
}

// AI Recognition History
const aiHistoryVisible = ref(false)
const recognitionHistory = ref([])

// Load recognition history from localStorage (mock persistence)
const loadRecognitionHistory = () => {
  const savedHistory = localStorage.getItem('aiRecognitionHistory')
  if (savedHistory) {
    recognitionHistory.value = JSON.parse(savedHistory)
  }
}

// Save recognition to history
const saveToHistory = (result) => {
  if (!result) return
  
  const historyItem = {
    id: Date.now(),
    timestamp: new Date().toLocaleString(),
    category: result.category,
    type: result.type || '未识别',
    material: result.material,
    color: result.color,
    style: result.style || '未识别',
    confidence: result.confidence,
    image: result.image || 'https://via.placeholder.com/100'
  }
  
  recognitionHistory.value.unshift(historyItem)
  
  // Save to localStorage (mock persistence)
  localStorage.setItem('aiRecognitionHistory', JSON.stringify(recognitionHistory.value))
}

// 删除识别历史
const deleteRecognitionHistory = (id) => {
  // 从数组中删除指定id的历史记录
  recognitionHistory.value = recognitionHistory.value.filter(item => item.id !== id)
  // 保存到localStorage
  localStorage.setItem('aiRecognitionHistory', JSON.stringify(recognitionHistory.value))
  ElMessage.success('识别历史已删除')
}

const saveCorrection = () => {
  if (!recognitionResult.value) return
  
  // Update recognition result with corrected values
  recognitionResult.value = {
    ...recognitionResult.value,
    category: correctionForm.category || recognitionResult.value.category,
    type: correctionForm.type || recognitionResult.value.type,
    material: correctionForm.material || recognitionResult.value.material,
    color: correctionForm.color || recognitionResult.value.color,
    style: correctionForm.style || recognitionResult.value.style
  }
  
  // 检查并添加新风格到选项列表
  if (correctionForm.style && !styleOptions.value.includes(correctionForm.style)) {
    styleOptions.value.push(correctionForm.style)
    saveStyleOptions()
  }
  
  // 检查并添加新具体类型到选项列表
  if (correctionForm.type && !typeOptions.value.includes(correctionForm.type)) {
    typeOptions.value.push(correctionForm.type)
    saveTypeOptions()
  }
  
  ElMessage.success('校正结果已保存')
}

// 生成唯一的辅料编码
const generateProductCode = () => {
  // 生成格式：M + 年月日 + 4位随机数
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  return `M${year}${month}${day}${random}`
}

// 确认添加辅料
const confirmAddMaterial = () => {
  if (!recognitionResult.value) return
  
  // 询问用户是否添加新辅料
  ElMessageBox.confirm(
    '是否将校正后的辅料添加到辅料管理中？',
    '确认添加',
    {
      confirmButtonText: '添加',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 初始化添加辅料表单，使用校正后的结果
    addMaterialForm.productCode = generateProductCode() // 自动生成辅料编码
    addMaterialForm.productName = `${recognitionResult.value.color} ${recognitionResult.value.material} ${recognitionResult.value.type || recognitionResult.value.category}`
    addMaterialForm.category = recognitionResult.value.category
    addMaterialForm.type = recognitionResult.value.type || ''
    addMaterialForm.style = '' // 初始化风格字段
    addMaterialForm.specification = ''
    addMaterialForm.unit = '件'
    addMaterialForm.price = 0
    addMaterialForm.description = `${recognitionResult.value.color} ${recognitionResult.value.material} ${recognitionResult.value.type || recognitionResult.value.category}`
    addMaterialForm.status = 1
    
    // 打开添加辅料对话框
    addMaterialDialog.value = true
  }).catch(() => {
    // 用户取消，不执行任何操作
  })
}

// Image Search
const imageSearchVisible = ref(false)
const isSearching = ref(false)
const searchImageResult = ref(null)

// Detail
const detailVisible = ref(false)
const currentMaterial = ref(null)
const recommendations = ref([])
const similarMaterials = ref([])

const loadMaterials = async () => {
  loading.value = true
  try {
    // 获取辅料列表数据
    const res = await getMaterialList(filters)
    if (res.code === 200) {
      const materialData = res.data || []
      
      // 获取库存数据
      const inventoryRes = await getInventoryList()
      const inventoryData = inventoryRes.data || []
      
      // 创建库存数据的映射，使用产品编码作为键
      const inventoryMap = new Map()
      inventoryData.forEach(inv => {
        const productCode = inv.productCode || inv.materialCode || inv.product_code || inv.material_code
        if (productCode) {
          inventoryMap.set(productCode, inv)
        }
      })
      
      // 合并辅料数据和库存数据
      materials.value = materialData.map(item => {
        const inventory = inventoryMap.get(item.productCode)
        return {
          ...item,
          stock: inventory?.quantity || 0 // 使用真实库存数据，默认值为0
        }
      })
    }
  } catch (error) {
    console.error('加载辅料列表失败:', error)
    ElMessage.error('加载辅料列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadMaterials()
}

const resetFilters = () => {
  filters.keyword = ''
  filters.category = ''
  filters.material = ''
  filters.color = ''
  filters.minPrice = ''
  filters.maxPrice = ''
  filters.supplier = ''
  filters.inStock = null
  loadMaterials()
}

const openAIRecognition = () => {
  recognitionResult.value = null
  isRecognizing.value = false
  aiDialogVisible.value = true
}

// 添加辅料对话框
const addMaterialDialog = ref(false)
const addMaterialFormRef = ref(null)
const addMaterialForm = reactive({
  productCode: '',
  productName: '',
  category: '',
  type: '',
  style: '',
  specification: '',
  unit: '件',
  price: 0,
  expectedDeliveryDays: 0,
  description: '',
  status: 1
})

// 添加辅料表单验证规则
const addMaterialRules = {
  productCode: [{ required: true, message: '请输入辅料编码', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入辅料名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const handleFileChange = async (file) => {
  console.log('开始处理文件上传:', file);
  isRecognizing.value = true
  recognitionResult.value = null
  try {
    console.log('调用recognizeMaterial API');
    const res = await recognizeMaterial(file)
    console.log('API调用返回结果:', res);
    if (res.code === 200) {
      recognitionResult.value = res.data
      // Auto-fill correction form with recognition results
      correctionForm.category = res.data.category
      correctionForm.type = res.data.type
      correctionForm.material = res.data.material
      correctionForm.color = res.data.color
      correctionForm.style = res.data.style || ''
      
      // Save to recognition history
      saveToHistory(res.data)
    }
  } catch (error) {
    console.error('AI识别错误:', error)
    ElMessage.error(`识别失败: ${error.message || '未知错误'}`)
  } finally {
    console.log('设置isRecognizing为false');
    isRecognizing.value = false
  }
}

// 提交添加辅料
const submitAddMaterial = async () => {
  // 权限检查：只有管理员可以添加新辅料
  if (userStore.user?.role !== 'admin') {
    ElMessage.warning('您没有权限添加新辅料')
    addMaterialDialog.value = false
    return
  }
  
  if (!addMaterialFormRef.value) return
  
  await addMaterialFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await saveProduct(addMaterialForm)
        ElMessage.success('辅料添加成功')
        addMaterialDialog.value = false
        // 刷新辅料列表
        loadMaterials()
      } catch (error) {
        ElMessage.error('辅料添加失败')
      }
    }
  })
}

const openAIHistory = () => {
  loadRecognitionHistory()
  aiHistoryVisible.value = true
}

const openImageSearch = () => {
  searchImageResult.value = null
  imageSearchVisible.value = true
}

const handleImageSearchFileChange = async (file) => {
  isSearching.value = true
  searchImageResult.value = null
  try {
    const res = await searchByImage(file)
    if (res.code === 200) {
      searchImageResult.value = res.data
    }
  } catch (error) {
    ElMessage.error('图片搜索失败')
  } finally {
    isSearching.value = false
  }
}

const loadRecommendations = (item) => {
  // Mock collaborative filtering recommendations
  // In a real app, this would call an API like /api/recommendations?productId=...
  recommendations.value = [
    {
      id: 101,
      productName: '推荐搭配：' + (item.category === '面料' ? '同色系纽扣' : '配套里布'),
      price: 15.5,
      image: 'https://via.placeholder.com/150',
      reason: '95%的用户同时也购买了此商品'
    },
    {
      id: 102,
      productName: '推荐搭配：' + (item.category === '面料' ? '缝纫线' : '粘合衬'),
      price: 5.0,
      image: 'https://via.placeholder.com/150',
      reason: '经常一起使用的辅料'
    }
  ]
}

const showDetail = async (item) => {
  currentMaterial.value = item
  
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
      currentMaterial.value.stock = inventory.quantity || 0
    } else {
      currentMaterial.value.stock = 0
    }
  } catch (error) {
    console.error('获取库存数据失败:', error)
    // 如果获取失败，使用默认值0
    currentMaterial.value.stock = 0
  }
  
  loadRecommendations(item)
  loadSimilarMaterials(item)
  detailVisible.value = true
}

const loadSimilarMaterials = (item) => {
  // Mock similar materials based on feature vectors
  // In a real app, this would call an API like /api/materials/similar?productId=...
  similarMaterials.value = [
    {
      id: item.id + 10,
      productName: item.productName + ' (相似款1)',
      price: item.price + Math.random() * 10 - 5,
      image: item.image || 'https://via.placeholder.com/150',
      similarity: 0.92 - Math.random() * 0.1
    },
    {
      id: item.id + 11,
      productName: item.productName + ' (相似款2)',
      price: item.price + Math.random() * 10 - 5,
      image: item.image || 'https://via.placeholder.com/150',
      similarity: 0.85 - Math.random() * 0.1
    },
    {
      id: item.id + 12,
      productName: item.productName + ' (相似款3)',
      price: item.price + Math.random() * 10 - 5,
      image: item.image || 'https://via.placeholder.com/150',
      similarity: 0.80 - Math.random() * 0.1
    }
  ]
}

// 添加到项目方案
const projectSchemeDialogVisible = ref(false)
const selectedProject = ref(null)
const selectedScheme = ref(null)
const projectList = ref([])
const currentMaterialForProject = ref(null)

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

// 选择项目时，更新可用方案列表
const handleProjectChange = (projectId) => {
  selectedScheme.value = null
  // 这里可以根据projectId加载对应方案
}

// 将辅料添加到项目方案
const addToProject = async () => {
  if (!selectedProject.value || !selectedScheme.value || !currentMaterialForProject.value) {
    ElMessage.warning('请选择项目和方案')
    return
  }
  
  // 检查方案状态，防止将辅料添加到已确定的方案中
  const selectedSchemeObj = selectedProject.value.schemes.find(scheme => scheme.id === selectedScheme.value)
  if (selectedSchemeObj && selectedSchemeObj.status === '已确定') {
    ElMessage.warning('该方案已确定，无法添加新辅料')
    return
  }
  
  try {
    // 调用API将辅料添加到方案
    const res = await addMaterialToScheme(selectedScheme.value, {
      materialId: currentMaterialForProject.value.id,
      productCode: currentMaterialForProject.value.productCode,
      productName: currentMaterialForProject.value.productName,
      category: currentMaterialForProject.value.category,
      specification: currentMaterialForProject.value.specification,
      unit: currentMaterialForProject.value.unit,
      price: currentMaterialForProject.value.price,
      image: currentMaterialForProject.value.image || '',
      quantity: 1 // 默认添加1个
    })
    
    if (res.code === 200) {
      ElMessage.success(`已将 ${currentMaterialForProject.value.productName} 加入方案`)
      projectSchemeDialogVisible.value = false
      selectedProject.value = null
      selectedScheme.value = null
      currentMaterialForProject.value = null
    }
  } catch (error) {
    ElMessage.error('添加失败，请重试')
  }
}

onMounted(() => {
  loadStyleOptions()
  loadTypeOptions()
  loadMaterials()
  loadRecognitionHistory()
  loadFavorites()
})
</script>

<style scoped>
.material-library {
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.material-card {
  transition: all 0.3s;
}
.material-card:hover {
  transform: translateY(-5px);
}
.image-wrapper {
  position: relative;
  height: 200px;
  overflow: hidden;
}
.image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}
.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}
.image-wrapper:hover .hover-overlay {
  opacity: 1;
}
.card-content {
  padding: 14px;
}
.material-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.material-meta {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 13px;
  margin-bottom: 10px;
}
.price {
  color: #f56c6c;
  font-weight: bold;
}
.material-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.ai-container {
  padding: 10px;
}
.recognizing {
  text-align: center;
  margin-top: 20px;
}
.add-material-section {
  margin-top: 20px;
  text-align: center;
}
.detail-image {
  width: 100%;
  border-radius: 4px;
}
.code {
  color: #909399;
  margin-bottom: 20px;
}
.low-stock {
  color: #e6a23c;
  font-weight: bold;
}
.out-of-stock {
  color: #f56c6c;
  font-weight: bold;
}
.image-search-container {
  padding: 10px;
}
.search-results {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}
.result-card {
  margin-bottom: 0;
}
.result-content {
  display: flex;
  gap: 10px;
}
.result-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}
.result-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.result-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.result-price {
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 3px;
}
.result-similarity {
  font-size: 12px;
  color: #67c23a;
}
.similar-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}
.similar-card {
  margin-bottom: 0;
}
.similar-content {
  display: flex;
  align-items: center;
  gap: 10px;
}
.similar-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  cursor: pointer;
}
.similar-info {
  flex: 1;
  overflow: hidden;
}
.similar-name {
  font-size: 13px;
  font-weight: bold;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}
.similar-similarity {
  font-size: 12px;
  color: #67c23a;
  margin-bottom: 2px;
}
.similar-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 13px;
}

.recommendation-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}
.rec-card {
  margin-bottom: 0;
}
.rec-content {
  display: flex;
  align-items: center;
  gap: 10px;
}
.rec-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}
.rec-info {
  flex: 1;
  overflow: hidden;
}
.rec-name {
  font-size: 13px;
  font-weight: bold;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rec-reason {
  margin-bottom: 4px;
}
.rec-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 13px;
}

.history-container {
  padding: 10px;
}

.empty-history {
  text-align: center;
  padding: 40px 0;
}
</style>
