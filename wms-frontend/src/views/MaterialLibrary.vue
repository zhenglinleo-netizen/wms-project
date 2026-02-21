<template>
  <div>
    <!-- 首屏内容：立即加载 -->
    <el-image v-if="materials.length > 0 && materials[0].image" :src="materials[0].image" loading="eager" style="display: none;" />
    <!-- 搜索与筛选栏 -->
    <el-card shadow="hover" :body-style="{ padding: '20px' }">
      <!-- 主要搜索栏 -->
      <div>
        <el-input
          v-model="filters.keyword"
          placeholder="搜索名称/描述/编号..."
          clearable
          prefix-icon="Search"
          style="width: 400px; margin-right: 15px;"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch" :icon="Search" round>
          搜索
        </el-button>
        <el-button @click="resetFilters" :icon="RefreshLeft" round>
          重置
        </el-button>
      </div>
      
      <!-- 高级筛选 -->
      <el-collapse v-model="activeFilterTabs" style="margin-top: 20px;">
        <el-collapse-item title="筛选条件" name="filters">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="分类">
                <el-select v-model="filters.category" placeholder="全部" clearable style="width: 100%;">
                  <el-option label="面料" value="面料" />
                  <el-option label="辅料" value="辅料" />
                  <el-option label="扣件" value="扣件" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="材质">
                <el-select v-model="filters.material" placeholder="全部" clearable style="width: 100%;">
                  <el-option label="棉" value="棉" />
                  <el-option label="麻" value="麻" />
                  <el-option label="丝" value="丝" />
                  <el-option label="毛" value="毛" />
                  <el-option label="涤纶" value="涤纶" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="颜色">
                <el-select v-model="filters.color" placeholder="全部" clearable style="width: 100%;">
                  <el-option label="红色" value="红色" />
                  <el-option label="蓝色" value="蓝色" />
                  <el-option label="绿色" value="绿色" />
                  <el-option label="黄色" value="黄色" />
                  <el-option label="黑色" value="黑色" />
                  <el-option label="白色" value="白色" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="库存">
                <el-select v-model="filters.inStock" placeholder="全部" clearable style="width: 100%;">
                  <el-option label="有库存" :value="true" />
                  <el-option label="无库存" :value="false" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="24" :md="16" :lg="12">
              <el-form-item label="价格范围">
                <el-space style="width: 100%;">
                  <el-input-number v-model="filters.minPrice" placeholder="最低" :min="0" style="flex: 1;" />
                  <span style="margin: 0 10px;">至</span>
                  <el-input-number v-model="filters.maxPrice" placeholder="最高" :min="0" style="flex: 1;" />
                </el-space>
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
      </el-collapse>
      
      <!-- 智能功能按钮 -->
      <div style="margin-top: 20px;">
        <el-space size="medium">
          <el-button type="warning" :icon="Camera" @click="openAIRecognition" round>
            AI 智能识别
          </el-button>
          <el-button type="info" :icon="Search" @click="openImageSearch" round>
            图片搜索
          </el-button>
          <el-button type="success" :icon="Clock" @click="openAIHistory" round>
            识别历史
          </el-button>
          <el-button type="primary" :icon="Star" @click="toggleFavoritesView" round>
            {{ showFavorites ? '全部辅料' : '我的收藏' }}
          </el-button>
          <el-button type="danger" :icon="Star" @click="toggleRecommendationsView" round>
            {{ showRecommendations ? '全部辅料' : '推荐' }}
          </el-button>
        </el-space>
      </div>
    </el-card>

    <!-- 辅料列表 -->
    <div style="padding: 0 20px 20px;">
      <!-- 列表头部 -->
        <div style="margin: 20px 0; display: flex; justify-content: space-between; align-items: center;">
          <el-text :type="'primary'" :size="'large'">
            {{ showRecommendations ? '推荐辅料' : showFavorites ? '我的收藏' : '辅料列表' }} ({{ materials.length }})
          </el-text>
        <el-space>
          <el-select v-model="sortBy" placeholder="排序方式" size="small">
            <el-option label="默认" value="default" />
            <el-option label="价格从低到高" value="price-asc" />
            <el-option label="价格从高到低" value="price-desc" />
            <el-option label="库存从多到少" value="stock-desc" />
          </el-select>
          <el-select v-model="viewMode" placeholder="视图模式" size="small">
            <el-option label="网格视图" value="grid" />
            <el-option label="列表视图" value="list" />
          </el-select>
        </el-space>
      </div>

      <!-- 网格视图 -->
      <el-row v-if="viewMode === 'grid'" :gutter="24" style="margin: 20px 0;">
        <el-col v-for="item in materials" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6">
          <div style="padding: 12px;">
            <el-card 
              shadow="hover" 
              :body-style="{ padding: '15px' }"
            >
              <el-image 
                v-if="item.image"
                :src="item.image"
                loading="lazy"
                fit="cover" 
                style="width: 100%; height: 200px; border-radius: 8px; cursor: pointer;"
                @click="showDetail(item, $event)"
              >
                <template #error>
                  <div style="width: 100%; height: 200px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 8px;">
                    <el-empty description="暂无图片" :image-size="60" />
                  </div>
                </template>
              </el-image>
              <div v-else style="width: 100%; height: 200px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 8px; cursor: pointer;" @click="showDetail(item, $event)">
                <el-empty description="暂无图片" :image-size="60" />
              </div>
              <el-badge 
                v-if="item.stock === 0" 
                value="缺货" 
                type="danger" 
                :offset="[-10, 10]"
              />
              
              <div style="margin-top: 15px;">
                <el-space direction="vertical" size="small" style="width: 100%;">
                  <el-text :truncate="{ rows: 1 }" :type="'primary'" :strong="true" style="font-size: 16px;">
                    {{ item.productName }}
                  </el-text>
                  
                  <el-space size="small" style="flex-wrap: wrap;">
                    <el-tag size="small" effect="plain">{{ item.category || '未分类' }}</el-tag>
                    <el-tag size="small" effect="plain" v-if="item.material">{{ item.material }}</el-tag>
                    <el-tag size="small" effect="plain" v-if="item.color">{{ item.color }}</el-tag>
                  </el-space>
                  
                  <el-text :type="'danger'" :strong="true" style="font-size: 18px;">
                    ¥{{ item.price }} / {{ item.unit }}
                  </el-text>
                  
                  <el-space size="small" style="justify-content: space-between; width: 100%; margin-top: 10px;">
                    <el-text size="small" type="info">
                      库存: {{ item.stock || 0 }}
                    </el-text>
                    <el-text size="small">
                      {{ item.supplier || '未知供应商' }}
                    </el-text>
                  </el-space>
                </el-space>
              </div>
              
              <div style="margin-top: 15px; display: flex; justify-content: flex-end; gap: 10px;">
                <el-button 
                  :icon="Star" 
                  circle 
                  :type="isFavorited(item.id) ? 'warning' : ''"
                  @click="toggleFavorite(item.id)"
                  :title="isFavorited(item.id) ? '取消收藏' : '添加收藏'"
                />
                <el-button 
                  :icon="ShoppingCart" 
                  type="primary" 
                  circle 
                  @click="openProjectSchemeDialog(item)"
                  title="添加到项目"
                />
                <el-button 
                  :icon="View" 
                  type="info" 
                  circle 
                  @click="showDetail(item, $event)"
                  title="查看详情"
                />
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>

      <!-- 列表视图 -->
      <el-table v-else-if="viewMode === 'list'" :data="materials" style="width: 100%" border stripe>
        <el-table-column prop="productName" label="名称" min-width="200">
          <template #default="scope">
            <el-space>
              <el-image 
                v-lazy="scope.row.image || 'https://via.placeholder.com/50'"
                fit="cover" 
                style="width: 40px; height: 40px; border-radius: 4px;"
              />
              <el-text :truncate="{ rows: 1 }">
                {{ scope.row.productName }}
              </el-text>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="material" label="材质" width="100" />
        <el-table-column prop="color" label="颜色" width="100" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="scope">
            <el-text type="danger">{{ scope.row.price }} / {{ scope.row.unit }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="supplier" label="供应商" min-width="150" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-space size="small">
              <el-button 
                :icon="Star" 
                circle 
                :type="isFavorited(scope.row.id) ? 'warning' : ''"
                @click="toggleFavorite(scope.row.id)"
              />
              <el-button 
                :icon="ShoppingCart" 
                type="primary" 
                size="small"
                @click="openProjectSchemeDialog(scope.row)"
              >
                添加到项目
              </el-button>
              <el-button 
                :icon="View" 
                type="info" 
                size="small"
                @click="showDetail(scope.row, $event)"
              >
                详情
              </el-button>
              <el-button 
                :icon="Edit" 
                type="warning" 
                size="small"
                @click="handleEdit(scope.row)"
              >
                编辑
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty 
        v-if="materials.length === 0" 
        description="暂无辅料数据"
        style="margin: 100px 0;"
      >
        <el-button type="primary" @click="resetFilters(); loadMaterials();">
          重新加载
        </el-button>
      </el-empty>

      <!-- 加载中状态 -->
      <el-skeleton v-if="loading" :rows="8" animated style="margin: 20px 0;">
        <template #template>
          <el-skeleton-item variant="image" style="width: 100%; height: 200px;" />
          <el-skeleton-item variant="p" style="width: 80%;" />
          <el-skeleton-item variant="text" style="width: 60%;" />
          <el-skeleton-item variant="text" style="width: 40%;" />
        </template>
      </el-skeleton>
    </div>

    <!-- AI 识别弹窗 -->
    <el-dialog 
      v-model="aiDialogVisible" 
      title="AI 智能辅料识别" 
      width="60%"
      :before-close="handleAIDialogClose"
      destroy-on-close
      append-to-body
    >
      <div style="max-height: 500px; overflow-y: auto; overflow-x: hidden; padding: 0 10px;">
      <!-- 加载中状态 -->
      <div v-if="isRecognizing" style="padding: 40px 0; text-align: center;">
        <el-space direction="vertical" size="large">
          <!-- Element Plus 原生加载组件 -->
          <el-icon class="is-loading" style="font-size: 48px; color: #409EFF;"><Loading /></el-icon>
          
          <!-- 加载文字 -->
          <el-text size="large">AI 正在分析图片特征...</el-text>
          
          <!-- 提示文字 -->
          <el-text size="small" type="info">
            这可能需要几秒钟时间，请耐心等待
          </el-text>
          
          <!-- Element Plus 原生进度条 -->
          <div style="width: 300px;">
            <el-progress 
              :percentage="recognitionProgress" 
              :format="() => ''" 
              :stroke-width="10"
              :status="recognitionProgress >= 100 ? 'success' : ''"
            />
          </div>
        </el-space>
      </div>
      
      <!-- 上传区域 -->
      <div v-else-if="!recognitionResult">
        <el-upload
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :file-list="uploadedFiles"
          list-type="picture"
          :disabled="isRecognizing"
          :multiple="false"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip" style="margin-top: 10px;">
              <el-text size="small">
                支持 JPG、PNG、BMP 格式，文件大小不超过 10MB
              </el-text>
            </div>
          </template>
        </el-upload>
        
        <!-- AI 识别按钮 -->
        <div v-if="uploadedFiles.length > 0" style="margin-top: 20px; text-align: center;">
          <el-button 
            type="primary" 
            size="large"
            :icon="Camera"
            @click="startAIRecognition"
            :loading="isRecognizing"
            :disabled="isRecognizing"
          >
            🔍 开始 AI 识别
          </el-button>
          <el-text size="small" type="info" style="display: block; margin-top: 10px;">
            点击后 AI 将分析图片特征并识别辅料信息
          </el-text>
        </div>
      </div>
      
      <!-- 识别结果 -->
      <div v-else-if="recognitionResult">
        <!-- 识别结果头部 -->
        <div style="margin-bottom: 20px;">
          <el-space direction="vertical" size="small">
            <el-text type="primary" size="large">
              识别结果
            </el-text>
            <el-space>
              <el-tag :type="getConfidenceLevel(recognitionResult.confidence)" size="large">
                置信度: {{ (recognitionResult.confidence * 100).toFixed(1) }}%
              </el-tag>
              <el-button 
                size="small" 
                type="info" 
                :icon="Refresh" 
                @click="retryRecognition"
              >
                重新识别
              </el-button>
            </el-space>
          </el-space>
        </div>
        
        <!-- 识别图片展示 -->
        <div style="margin-bottom: 20px;">
          <el-text size="small" type="info">
            识别图片
          </el-text>
          <div style="margin-top: 10px;">
            <el-image
              v-if="recognitionResult?.image || uploadedFiles[0]?.url"
              :src="recognitionResult?.image || uploadedFiles[0]?.url"
              fit="cover"
              style="width: 200px; height: 200px; border-radius: 4px;"
              @error="handleImageError"
            >
              <template #error>
                <div style="width: 200px; height: 200px; border: 1px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa;">
                  <el-empty description="图片加载失败" :image-size="60" />
                </div>
              </template>
            </el-image>
            <div v-else style="width: 200px; height: 200px; border: 1px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa;">
              <el-empty description="无识别图片" :image-size="60" />
            </div>
          </div>
        </div>
        
        <!-- 识别结果标签 -->
        <div style="margin-bottom: 20px;">
          <el-space wrap>
            <el-tag size="large">{{ recognitionResult.category }}</el-tag>
            <el-tag size="large" v-if="recognitionResult.type">{{ recognitionResult.type }}</el-tag>
            <el-tag size="large" v-if="recognitionResult.material">{{ recognitionResult.material }}</el-tag>
            <el-tag size="large" v-if="recognitionResult.color">{{ recognitionResult.color }}</el-tag>
            <el-tag size="large" v-if="recognitionResult.auxiliaryName">{{ recognitionResult.auxiliaryName }}</el-tag>
          </el-space>
        </div>
        
        <!-- 上传额外图片 -->
        <div style="margin-bottom: 20px;">
          <el-text size="small" type="info">
            可上传更多图片（最多5张）
          </el-text>
          <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleAdditionalFileChange"
            :on-remove="handleAdditionalFileRemove"
            :file-list="additionalFiles"
            list-type="picture-card"
            :limit="5"
            :multiple="true"
          >
            <el-icon><UploadFilled /></el-icon>
            <div class="el-upload__text">
              添加图片
            </div>
          </el-upload>
        </div>
        
        <!-- 识别结果详情 -->
        <el-collapse v-model="activeResultTabs">
          <el-collapse-item title="详细信息" name="details">
            <el-descriptions border :column="1" label-width="120px">
              <el-descriptions-item label="类别">{{ recognitionResult.category }}</el-descriptions-item>
              <el-descriptions-item label="具体类型">{{ recognitionResult.type || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="材质">{{ recognitionResult.material || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="颜色">{{ recognitionResult.color || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="辅料名称">{{ recognitionResult.auxiliaryName || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="风格">{{ recognitionResult.style || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="辅料类别">{{ recognitionResult.auxiliaryCategory || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="工艺大类">{{ recognitionResult.processCategory || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="材料层">{{ recognitionResult.materialLayer || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="效果层">{{ recognitionResult.effectLayer || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="适用阶段">{{ recognitionResult.applicationStage || '未识别' }}</el-descriptions-item>
              <el-descriptions-item label="描述">{{ recognitionResult.description || '未识别' }}</el-descriptions-item>
            </el-descriptions>
          </el-collapse-item>
          
          <el-collapse-item title="人工校正" name="correction">
            <el-form :model="correctionForm" label-width="120px">
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12">
                  <el-form-item label="校正类别">
                    <el-select v-model="correctionForm.category" placeholder="选择类别">
                      <el-option label="面料" value="面料" />
                      <el-option label="辅料" value="辅料" />
                      <el-option label="扣件" value="扣件" />
                      <el-option v-if="recognitionResult?.category" :label="recognitionResult.category" :value="recognitionResult.category">
                        <template #default>
                          <div style="display: flex; justify-content: space-between; width: 100%;">
                            <span>{{ recognitionResult.category }}</span>
                            <span style="color: #999; font-size: 12px;">AI识别</span>
                          </div>
                        </template>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="12">
                  <el-form-item label="校正具体类型">
                    <el-select v-model="correctionForm.type" placeholder="选择具体类型" filterable allow-create>
                      <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
                      <el-option v-if="recognitionResult?.type" :label="recognitionResult.type" :value="recognitionResult.type">
                        <template #default>
                          <div style="display: flex; justify-content: space-between; width: 100%;">
                            <span>{{ recognitionResult.type }}</span>
                            <span style="color: #999; font-size: 12px;">AI识别</span>
                          </div>
                        </template>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="12">
                  <el-form-item label="校正材质">
                    <el-select v-model="correctionForm.material" placeholder="选择材质">
                      <el-option label="棉" value="棉" />
                      <el-option label="麻" value="麻" />
                      <el-option label="丝" value="丝" />
                      <el-option label="毛" value="毛" />
                      <el-option label="涤纶" value="涤纶" />
                      <el-option label="混纺" value="混纺" />
                      <el-option v-if="recognitionResult?.material" :label="recognitionResult.material" :value="recognitionResult.material">
                        <template #default>
                          <div style="display: flex; justify-content: space-between; width: 100%;">
                            <span>{{ recognitionResult.material }}</span>
                            <span style="color: #999; font-size: 12px;">AI识别</span>
                          </div>
                        </template>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="12">
                  <el-form-item label="校正颜色">
                    <el-select v-model="correctionForm.color" placeholder="选择颜色">
                      <el-option label="红色" value="红色" />
                      <el-option label="蓝色" value="蓝色" />
                      <el-option label="绿色" value="绿色" />
                      <el-option label="黄色" value="黄色" />
                      <el-option label="黑色" value="黑色" />
                      <el-option label="白色" value="白色" />
                      <el-option label="灰色" value="灰色" />
                      <el-option v-if="recognitionResult?.color" :label="recognitionResult.color" :value="recognitionResult.color">
                        <template #default>
                          <div style="display: flex; justify-content: space-between; width: 100%;">
                            <span>{{ recognitionResult.color }}</span>
                            <span style="color: #999; font-size: 12px;">AI识别</span>
                          </div>
                        </template>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24">
                  <el-form-item label="校正辅料名称">
                    <el-input v-model="correctionForm.auxiliaryName" placeholder="输入辅料名称" />
                  </el-form-item>
                </el-col>
                <el-col :xs="24">
                  <el-form-item label="校正风格">
                    <el-input v-model="correctionForm.style" placeholder="输入风格关键词，如：优雅、浪漫、精致" />
                  </el-form-item>
                </el-col>
                <el-col :xs="24">
                  <el-form-item label="校正描述">
                    <el-input v-model="correctionForm.description" type="textarea" :rows="4" placeholder="输入详细描述" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item style="margin-top: 20px;">
                <el-button type="primary" @click="saveCorrection">保存校正结果</el-button>
              </el-form-item>
            </el-form>
          </el-collapse-item>
          
          <el-collapse-item title="相似辅料推荐" name="similar" v-if="recognitionResult.similar && recognitionResult.similar.length">
            <el-card :body-style="{ padding: '15px' }">
              <el-row :gutter="15">
                <el-col :span="8" v-for="(item, index) in recognitionResult.similar" :key="index">
                  <el-card shadow="hover" :body-style="{ padding: '10px' }">
                    <el-image 
                      v-lazy="item.image || 'https://via.placeholder.com/100'"
                      fit="cover" 
                      style="width: 100%; height: 100px; border-radius: 4px; margin-bottom: 10px;"
                    />
                    <el-text :truncate="{ rows: 1 }" style="font-weight: 500;">
                      {{ item.name }}
                    </el-text>
                    <el-text type="danger" size="small" style="display: block; margin-top: 5px;">
                      ¥{{ item.price }}
                    </el-text>
                    <el-progress 
                      :percentage="item.similarity * 100" 
                      :format="() => `${(item.similarity * 100).toFixed(0)}%`" 
                      :size="'small'" 
                      style="margin-top: 10px;"
                    />
                  </el-card>
                </el-col>
              </el-row>
            </el-card>
          </el-collapse-item>
        </el-collapse>
        
        <!-- 识别结果操作 -->
        <div style="margin-top: 20px; display: flex; justify-content: flex-end; gap: 10px;">
          <el-button @click="handleFileRemove">重新上传</el-button>
          <el-button type="warning" @click="activeResultTabs = ['correction']">人工校正</el-button>
          <el-button 
            type="success" 
            @click="confirmAddMaterial"
          >
            添加到辅料库
          </el-button>
        </div>
      </div>
      </div>
    </el-dialog>

    <!-- 图片搜索弹窗 -->
    <el-dialog v-model="imageSearchVisible" title="图片搜索" width="50%" append-to-body>
      <el-upload
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
      
      <el-divider v-if="searchImageResult">搜索结果</el-divider>
      
      <el-row :gutter="16" v-if="searchImageResult">
        <el-col :span="8" v-for="item in searchImageResult" :key="item.id">
          <el-card shadow="hover">
            <el-image 
              v-if="item.image"
              v-lazy="item.image"
              fit="cover"
              style="width: 100%; height: 150px;"
            />
            <el-empty v-else description="暂无图片" :image-size="80" />
            <el-divider />
            <el-space direction="vertical" style="width: 100%">
              <el-text truncated>{{ item.productName }}</el-text>
              <el-text type="danger" tag="b">¥{{ item.price }}</el-text>
              <el-text type="success" size="small">相似度: {{ item.similarity * 100 }}%</el-text>
            </el-space>
          </el-card>
        </el-col>
      </el-row>
      
      <el-empty v-else-if="!isSearching" description="暂无搜索结果" :image-size="80" />
      
      <el-space v-if="isSearching" justify="center" style="width: 100%">
        <el-icon class="is-loading"><Loading /></el-icon>
        <el-text>正在搜索相似图片...</el-text>
      </el-space>
    </el-dialog>

    <el-dialog v-model="aiHistoryVisible" title="AI 识别历史" width="80%" append-to-body>
      <el-table :data="recognitionHistory" style="width: 100%">
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
            <el-image 
              v-if="scope.row.image"
              v-lazy="scope.row.image"
              fit="cover" 
              style="width: 50px; height: 50px; border-radius: 4px;" 
            />
            <el-empty v-else description="" :image-size="30" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="danger" size="small" @click="deleteRecognitionHistory(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="recognitionHistory.length === 0" description="暂无识别历史数据" :image-size="80" />
    </el-dialog>


    
    <!-- 添加辅料对话框 -->
    <el-dialog v-model="addMaterialDialog" title="添加新辅料" width="600px" append-to-body>
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
        <el-button type="primary" @click="addToProject">确定添加</el-button>      </template>
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

    <!-- 编辑辅料对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑辅料" width="600px" append-to-body>
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="辅料编码">
          <el-input v-model="editForm.productCode" disabled />
        </el-form-item>
        <el-form-item label="辅料名称" prop="productName">
          <el-input v-model="editForm.productName" placeholder="请输入辅料名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="editForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="面料" value="面料" />
            <el-option label="辅料" value="辅料" />
            <el-option label="扣件" value="扣件" />
          </el-select>
        </el-form-item>
        <el-form-item label="风格">
          <el-select v-model="editForm.style" placeholder="请选择风格" style="width: 100%" filterable allow-create>
            <el-option v-for="style in styleOptions" :key="style" :label="style" :value="style" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体类型">
          <el-select v-model="editForm.type" placeholder="请选择具体类型" style="width: 100%" filterable allow-create>
            <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="editForm.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="editForm.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="editForm.price" :precision="2" :min="0" style="width: 100%" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="预期货期" prop="expectedDeliveryDays">
          <el-input-number v-model="editForm.expectedDeliveryDays" :min="0" :max="999" style="width: 100%" placeholder="请输入预期货期（天）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Camera, Star, ShoppingCart, UploadFilled, Loading, Clock, Search, RefreshLeft, View, Refresh, ZoomIn, CopyDocument, Share, DataAnalysis, Close, Upload, Edit } from '@element-plus/icons-vue'
import { getMaterialList, recognizeMaterial, searchByImage } from '@/api/material'
import { saveProduct, updateProduct, recommendProducts } from '@/api/product'
import { getProjectList, addMaterialToScheme } from '@/api/project'
import { getInventoryList } from '@/api/inventory'
import { uploadFile, deleteFile, checkFileExists, uploadMultipleFiles } from '@/api/file'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useMaterialStore } from '@/stores/material'
import { aiRequest } from '@/api/material'

const userStore = useUserStore()
const materialStore = useMaterialStore()

const materials = ref([])
const loading = ref(false)
const sortBy = ref('default')
const viewMode = ref('grid')
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

// 筛选面板状态
const activeFilterTabs = ref(['filters'])

// 收藏视图切换
const showFavorites = ref(false)

// 推荐视图切换
const showRecommendations = ref(false)

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

// Toggle favorites view
const toggleFavoritesView = () => {
  showFavorites.value = !showFavorites.value
  showRecommendations.value = false // 确保推荐视图关闭
  if (showFavorites.value) {
    // Filter materials to only show favorites
    const allMaterials = materials.value
    materials.value = allMaterials.filter(item => favoriteMaterials.value.has(item.id))
  } else {
    // Reload all materials
    loadMaterials()
  }
}

// Toggle recommendations view
const toggleRecommendationsView = () => {
  showRecommendations.value = !showRecommendations.value
  showFavorites.value = false // 确保收藏视图关闭
  if (showRecommendations.value) {
    // 模拟个性化推荐（目前尚未实现真实的推荐算法）
    ElMessage.info('个性化推荐功能正在开发中，敬请期待！')
    // 暂时显示所有辅料
    loadMaterials()
  } else {
    // Reload all materials
    loadMaterials()
  }
}

// Sort materials
const sortMaterials = () => {
  if (!materials.value || materials.value.length === 0) return
  
  const sorted = [...materials.value]
  
  switch (sortBy.value) {
    case 'price-asc':
      sorted.sort((a, b) => (a.price || 0) - (b.price || 0))
      break
    case 'price-desc':
      sorted.sort((a, b) => (b.price || 0) - (a.price || 0))
      break
    case 'stock-desc':
      sorted.sort((a, b) => (b.stock || 0) - (a.stock || 0))
      break
    default:
      // Default sort (by id)
      sorted.sort((a, b) => a.id - b.id)
  }
  
  materials.value = sorted
}

// Watch for sort changes
import { watch } from 'vue'
watch(sortBy, () => {
  sortMaterials()
})

// AI Recognition
const aiDialogVisible = ref(false)
const activeTab = ref('result')
const isRecognizing = ref(false)
const recognitionResult = ref(null)
const uploadedFiles = ref([])
const additionalFiles = ref([])
const correctionForm = reactive({
  category: '',
  type: '',
  material: '',
  color: '',
  style: '',
  auxiliaryCategory: '',
  auxiliaryName: '',
  processCategory: '',
  materialLayer: '',
  effectLayer: '',
  applicationStage: ''
})

// AI Recognition new variables
const recognitionProgress = ref(0)
const activeResultTabs = ref(['details'])

// 处理文件移除
const handleFileRemove = async () => {
  try {
    // 如果有文件，从MinIO中删除
    if (uploadedFiles.value.length > 0) {
      const fileUrl = uploadedFiles.value[0].url
      if (fileUrl && !fileUrl.startsWith('blob:')) {
        console.log('从MinIO中删除文件:', fileUrl)
        await deleteFile(fileUrl)
        console.log('文件删除成功')
      }
    }
  } catch (error) {
    console.error('删除文件失败:', error)
    // 继续执行，不影响前端操作
  } finally {
    uploadedFiles.value = []
    additionalFiles.value = []
    recognitionResult.value = null
    recognitionProgress.value = 0
    // 重置校正表单
    Object.keys(correctionForm).forEach(key => {
      correctionForm[key] = ''
    })
  }
}

// 处理额外图片上传
const handleAdditionalFileChange = (file, fileList) => {
  console.log('处理额外文件上传:', file, fileList);
  
  // 过滤出有效的文件
  const validFiles = fileList.map(f => ({
    name: f.name,
    url: URL.createObjectURL(f.raw),
    uid: f.uid,
    raw: f.raw // 保存原始文件对象，用于后续上传
  }))
  
  additionalFiles.value = validFiles
}

// 处理额外图片删除
const handleAdditionalFileRemove = (file, fileList) => {
  console.log('处理额外文件删除:', file, fileList);
  
  // 过滤出剩余的文件
  const remainingFiles = fileList.map(f => ({
    name: f.name,
    url: f.url,
    uid: f.uid,
    raw: f.raw
  }))
  
  additionalFiles.value = remainingFiles
}

// Get confidence level tag type
const getConfidenceLevel = (confidence) => {
  if (confidence >= 0.8) return 'success'
  if (confidence >= 0.6) return 'warning'
  return 'danger'
}

// Retry recognition
const retryRecognition = () => {
  if (uploadedFiles.value.length > 0) {
    const file = uploadedFiles.value[0]
    console.log('重新识别，文件信息:', file);
    if (file && file.raw) {
      startAIRecognition()
    } else {
      console.error('重新识别失败：缺少原始文件对象');
      ElMessage.error('重新识别失败：缺少原始文件对象');
      isRecognizing.value = false;
    }
  }
}

// Handle AI dialog close
const handleAIDialogClose = () => {
  handleFileRemove()
  aiDialogVisible.value = false
}

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
    style: correctionForm.style || recognitionResult.value.style,
    auxiliaryCategory: correctionForm.auxiliaryCategory || recognitionResult.value.auxiliaryCategory,
    auxiliaryName: correctionForm.auxiliaryName || recognitionResult.value.auxiliaryName,
    processCategory: correctionForm.processCategory || recognitionResult.value.processCategory,
    materialLayer: correctionForm.materialLayer || recognitionResult.value.materialLayer,
    effectLayer: correctionForm.effectLayer || recognitionResult.value.effectLayer,
    applicationStage: correctionForm.applicationStage || recognitionResult.value.applicationStage
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
const confirmAddMaterial = async () => {
  if (!recognitionResult.value) return
  
  // 询问用户是否添加新辅料
  ElMessageBox.confirm(
    '是否将校正后的辅料添加到辅料管理中？\n\n注意：添加后需要管理员在"辅料管理"中审核并设置单价后才能上架到智能辅料库。',
    '确认添加',
    {
      confirmButtonText: '添加',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 检查是否有上传的文件
      if (uploadedFiles.value.length === 0 || !uploadedFiles.value[0].raw) {
        ElMessage.error('请先上传图片')
        return
      }
      
      // 检查文件是否已存在
      console.log('检查文件是否已存在');
      const checkRes = await checkFileExists(uploadedFiles.value[0].raw)
      console.log('文件检查返回结果:', checkRes);
      
      if (checkRes.code === 200) {
        if (checkRes.data.exists) {
          ElMessage.warning('该图片已存在于辅料库中，无法重复添加')
          return
        }
      } else {
        throw new Error('文件检查失败: ' + checkRes.message)
      }
      
      // 获取文件哈希值
      const fileHash = checkRes.data.fileHash
      console.log('文件哈希值:', fileHash);
      
      // 准备所有要上传的文件
      console.log('准备所有要上传的文件');
      const allFiles = [...uploadedFiles.value, ...additionalFiles.value]
      console.log('总文件数:', allFiles.length);
      
      // 上传文件到MinIO
      console.log('上传文件到MinIO');
      let fileUrls = []
      if (allFiles.length === 0) {
        ElMessage.error('请先上传图片')
        return
      } else if (allFiles.length === 1) {
        // 单个文件上传
        const uploadRes = await uploadFile(allFiles[0].raw)
        console.log('文件上传返回结果:', uploadRes);
        
        if (uploadRes.code !== 200) {
          throw new Error('文件上传失败: ' + uploadRes.message)
        }
        
        fileUrls = [uploadRes.data]
      } else {
        // 多个文件上传
        const files = allFiles.map(file => file.raw)
        const uploadRes = await uploadMultipleFiles(files)
        console.log('多文件上传返回结果:', uploadRes);
        
        if (uploadRes.code !== 200) {
          throw new Error('文件上传失败: ' + uploadRes.message)
        }
        
        fileUrls = uploadRes.data
      }
      
      const fileUrl = fileUrls[0] // 第一张图片作为主图
      
      // 构建辅料信息，使用人工校正后的结果
      const materialData = {
        productCode: generateProductCode(), // 自动生成辅料编码
        productName: correctionForm.auxiliaryName || `${correctionForm.type || correctionForm.category}--${correctionForm.material}--${correctionForm.color}`,
        category: correctionForm.category || recognitionResult.value.category,
        type: correctionForm.type || recognitionResult.value.type || '',
        style: correctionForm.style || recognitionResult.value.style || '',
        specification: '',
        unit: '件',
        price: 0,
        expectedDeliveryDays: 0,
        description: correctionForm.description || `${correctionForm.type || correctionForm.category}--${correctionForm.material}--${correctionForm.color}`,
        status: 2, // 设置为待审核状态，需要管理员审核
        imageUrl: fileUrl, // 添加主图片URL
        image: fileUrl, // 添加image字段，确保前端可以正常显示
        images: JSON.stringify(fileUrls), // 添加所有图片URL，使用JSON格式
        fileHash: fileHash // 添加文件哈希值
      }
      
      // 调试打印图片URL信息
      console.log('构建的辅料信息:');
      console.log('主图片URL:', fileUrl);
      console.log('所有图片URL:', fileUrls);
      
      // 直接调用API保存辅料信息
      const saveResult = await saveProduct(materialData)
      
      // 尝试进行向量化，但即使失败也不影响辅料添加
      try {
        if (saveResult.code === 200 && saveResult.data && saveResult.data.id) {
          const productId = saveResult.data.id
          console.log('辅料保存成功，ID:', productId);
          
          // 调用新的向量化端点
          console.log('调用向量化端点');
          
          // 创建FormData对象
          const vectorizeFormData = new FormData();
          vectorizeFormData.append('productId', productId);
          vectorizeFormData.append('file', uploadedFiles.value[0].raw);
          
          // 使用aiRequest实例发送请求，确保正确的baseURL和请求头
          console.log('使用aiRequest发送向量化请求');
          console.log('请求URL:', '/ai/vectorize');
          console.log('请求参数:', { productId, hasFile: !!uploadedFiles.value[0].raw });
          
          try {
            const vectorizeData = await aiRequest({
              url: '/ai/vectorize',
              method: 'post',
              data: vectorizeFormData
            });
            console.log('向量化返回结果:', vectorizeData);
          
            if (vectorizeData.code === 200) {
              console.log('向量化成功');
            } else {
              console.error('向量化失败:', vectorizeData.message);
              // 向量化失败，仅记录错误，不影响辅料添加
              ElMessage.warning('辅料添加成功，但向量化失败，相似辅料推荐功能可能受限');
            }
          } catch (vectorizeError) {
            console.error('向量化过程中发生错误:', vectorizeError);
            // 向量化失败，仅记录错误，不影响辅料添加
            ElMessage.warning('辅料添加成功，但向量化失败，相似辅料推荐功能可能受限');
          }
        }
      } catch (vectorizeOuterError) {
        console.error('向量化外层异常:', vectorizeOuterError);
        // 向量化失败，仅记录错误，不影响辅料添加
      }
      
      // 显示成功提示
      ElMessage.success('辅料已成功添加到待审核列表，请等待管理员审核')
      
      // 关闭AI识别对话框
      aiDialogVisible.value = false
      
      // 重置相关状态
      recognitionResult.value = null
      uploadedFiles.value = []
      additionalFiles.value = []
      recognitionProgress.value = 0
    } catch (error) {
      console.error('添加辅料失败:', error)
      ElMessage.error('添加辅料失败，请稍后重试')
    }
  }).catch(() => {
    // 用户取消，不执行任何操作
  })
}

// Image Search
const imageSearchVisible = ref(false)
const isSearching = ref(false)
const searchImageResult = ref(null)

// Detail
const detailPopover = ref(null)


// Upload more images dialog
const uploadMoreImagesDialog = ref(false)
const moreImagesFiles = ref([])
const isUploadingMoreImages = ref(false)

// Edit dialog
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  productCode: '',
  productName: '',
  category: '',
  style: '',
  type: '',
  specification: '',
  unit: '',
  price: 0,
  expectedDeliveryDays: null,
  description: ''
})

const editRules = {
  productName: [
    { required: true, message: '请输入辅料名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  expectedDeliveryDays: [
    { required: true, message: '请输入预期货期', trigger: 'blur' },
    { type: 'number', min: 0, message: '预期货期必须大于等于0', trigger: 'blur' }
  ]
}

const loadMaterials = async () => {
  loading.value = true
  try {
    // 获取辅料列表数据
    const res = await getMaterialList()
    if (res.code === 200) {
      let materialData = res.data || []
      
      // 只显示已上架的辅料（status = 1）
      materialData = materialData.filter(item => item.status === 1)
      
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
        
        // 处理图片字段
        let imageUrl = item.imageUrl || item.image
        
        // 优先处理images字段（JSON格式的图片数组）
        if (item.images) {
          try {
            // 处理可能的字符串包裹情况
            let imagesStr = item.images
            if (typeof imagesStr === 'string' && imagesStr.startsWith('"') && imagesStr.endsWith('"')) {
              imagesStr = imagesStr.substring(1, imagesStr.length - 1)
            }
            
            const images = JSON.parse(imagesStr)
            if (Array.isArray(images) && images.length > 0) {
              imageUrl = images[0] // 使用第一张图片作为主图
            }
          } catch (e) {
            console.error('解析图片列表失败:', e, '原始数据:', item.images)
          }
        }
        
        // 如果imageUrl仍然是"上传成功"，尝试从images字段获取
        if (imageUrl === '上传成功' && item.images) {
          try {
            // 处理可能的字符串包裹情况
            let imagesStr = item.images
            if (typeof imagesStr === 'string' && imagesStr.startsWith('"') && imagesStr.endsWith('"')) {
              imagesStr = imagesStr.substring(1, imagesStr.length - 1)
            }
            
            const images = JSON.parse(imagesStr)
            if (Array.isArray(images) && images.length > 0) {
              imageUrl = images[0] // 使用第一张图片作为主图
            }
          } catch (e) {
            console.error('解析图片列表失败:', e, '原始数据:', item.images)
          }
        }
        
        // 处理图片URL
        if (imageUrl && typeof imageUrl === 'string') {
          // 过滤掉错误的图片URL（如"上传成功"）
          if (imageUrl === '上传成功' || imageUrl === '[]') {
            imageUrl = null
          } else if (imageUrl.startsWith('http')) {
            // 提取文件名（忽略bucket名称）
            const lastSlashIndex = imageUrl.lastIndexOf('/')
            if (lastSlashIndex !== -1) {
              const filename = imageUrl.substring(lastSlashIndex + 1)
              // 使用后端接口获取图片，避免MinIO认证问题
              imageUrl = `/file/get-image?filename=${filename}`
            }
          } else if (!imageUrl.startsWith('/file/get-image')) {
            // 如果不是http开头也不是/file/get-image格式，直接使用文件名
            imageUrl = `/file/get-image?filename=${imageUrl}`
          }
          // 保留已经是/file/get-image格式的URL
        }
        
        // 调试打印图片URL
        console.log('辅料名称:', item.productName || item.material_name);
        console.log('处理后的图片URL:', imageUrl);
        
        return {
          ...item,
          image: imageUrl, // 确保图片字段可用
          stock: inventory?.quantity || 0 // 使用真实库存数据，默认值为0
        }
      })
      
      // 调试打印辅料数据
      console.log('加载的辅料数据:', materials.value)
      console.log('图片URL示例:', materials.value.map(item => ({ 
        name: item.productName, 
        image: item.image 
      })))
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

// 处理文件上传（只预览，不自动识别）
const handleFileChange = (file) => {
  console.log('开始处理文件上传:', file);
  console.log('文件对象结构:', {
    name: file.name,
    size: file.size,
    type: file.type,
    raw: file.raw,
    hasRaw: !!file.raw
  });
  
  recognitionResult.value = null
  recognitionProgress.value = 0
  
  try {
    // 使用临时URL进行预览，不上传到MinIO
    console.log('使用临时URL进行预览');
    const tempUrl = URL.createObjectURL(file.raw)
    
    // 添加文件到上传列表
    uploadedFiles.value = [{
      name: file.name,
      url: tempUrl,
      uid: file.uid,
      raw: file.raw // 保存原始文件对象，用于后续上传
    }]
    
    ElMessage.success('图片上传成功，请点击"开始 AI 识别"按钮进行分析')
  } catch (error) {
    console.error('处理文件失败:', error)
    ElMessage.error(`处理失败: ${error.message || '未知错误'}`)
  }
}

// 开始AI识别
const startAIRecognition = async () => {
  if (uploadedFiles.value.length === 0 || !uploadedFiles.value[0].raw) {
    ElMessage.warning('请先上传图片')
    return
  }
  
  const file = uploadedFiles.value[0]
  isRecognizing.value = true
  recognitionResult.value = null
  recognitionProgress.value = 0
  
  // 更新进度条的函数
  const updateProgress = (progress) => {
    // 确保进度值在0-100之间
    recognitionProgress.value = Math.min(100, Math.max(0, progress))
  }
  
  try {
    // 开始识别
    console.log('开始AI识别流程');
    updateProgress(0)
    
    // 模拟进度更新
    let currentProgress = 0
    const progressInterval = setInterval(() => {
      if (currentProgress < 90) {
        currentProgress += 2
        updateProgress(currentProgress)
      }
    }, 100)
    
    // 调用AI识别API
    console.log('调用recognizeMaterial API');
    
    console.log('准备调用recognizeMaterial函数');
    const res = await recognizeMaterial({ raw: file.raw, name: file.name, uid: file.uid })
    
    // 清除进度更新定时器
    clearInterval(progressInterval)
    
    // 平滑过渡到100%
    const finalProgressInterval = setInterval(() => {
      if (recognitionProgress.value < 100) {
        recognitionProgress.value += 5
      } else {
        clearInterval(finalProgressInterval)
      }
    }, 50)
    
    // 等待进度条达到100%
    await new Promise(resolve => {
      const checkProgress = setInterval(() => {
        if (recognitionProgress.value >= 100) {
          clearInterval(checkProgress)
          resolve()
        }
      }, 50)
    })
    
    console.log('API调用返回结果:', res);
    
    if (res.code === 200) {
      // 检查返回的数据是否为空
      if (!res.data) {
        ElMessage.warning('API返回数据为空，请重试')
        console.warn('API返回数据为空:', res)
        return
      }
      
      // 检查返回的数据是否有效
      const isDataValid = Object.values(res.data).some(value => 
        value !== '未识别' && value !== '' && 
        (!Array.isArray(value) || value.length > 0)
      )
      
      if (!isDataValid) {
        ElMessage.warning('未识别出辅料信息，请尝试上传清晰的图片')
        console.warn('API返回数据无效:', res.data)
      }
      
      // 保存识别结果，暂不包含图片URL
      const resultWithImage = {
        ...res.data,
        image: file.url // 使用临时URL进行预览
      }
      
      // 确保图片URL格式正确
      if (resultWithImage.image) {
        console.log('识别结果图片URL:', resultWithImage.image)
      }
      
      recognitionResult.value = resultWithImage
      // Auto-fill correction form with recognition results
      correctionForm.category = resultWithImage.category
      correctionForm.type = resultWithImage.type
      correctionForm.material = resultWithImage.material
      correctionForm.color = resultWithImage.color
      correctionForm.style = resultWithImage.style || ''
      correctionForm.auxiliaryCategory = resultWithImage.auxiliaryCategory || ''
      correctionForm.auxiliaryName = resultWithImage.auxiliaryName || ''
      correctionForm.processCategory = resultWithImage.processCategory || ''
      correctionForm.materialLayer = resultWithImage.materialLayer || ''
      correctionForm.effectLayer = resultWithImage.effectLayer || ''
      correctionForm.applicationStage = resultWithImage.applicationStage || ''
      correctionForm.description = resultWithImage.description || ''
      
      // Save to recognition history
      saveToHistory(resultWithImage)
      
      ElMessage.success('AI识别完成')
    }
  } catch (error) {
    console.error('AI识别失败:', error)
    ElMessage.error(`识别失败: ${error.message || '未知错误'}`)
  } finally {
    // 确保进度条达到100%
    if (recognitionResult.value) {
      recognitionProgress.value = 100
    }
    // 延迟设置isRecognizing为false，确保用户能看到完成状态
    setTimeout(() => {
      console.log('设置isRecognizing为false');
      isRecognizing.value = false
    }, 500)
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
    // 先上传文件到MinIO
    console.log('上传文件到MinIO');
    const uploadRes = await uploadFile(file.raw)
    console.log('文件上传返回结果:', uploadRes);
    
    if (uploadRes.code === 200) {
      const res = await searchByImage(file)
      if (res.code === 200) {
        searchImageResult.value = res.data
      }
    } else {
      throw new Error('文件上传失败: ' + uploadRes.message)
    }
  } catch (error) {
    console.error('图片搜索失败:', error)
    ElMessage.error('图片搜索失败: ' + (error.message || '未知错误'))
  } finally {
    isSearching.value = false
  }
}



const showDetail = async (item, event) => {
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
  
  // 显示对话框（使用materialStore）
  materialStore.showDetail(item)
}

// 打开上传更多图片对话框
const openUploadMoreImagesDialog = () => {
  moreImagesFiles.value = []
  uploadMoreImagesDialog.value = true
}

// 处理更多图片上传
const handleMoreImagesUpload = async () => {
  if (!currentMaterial.value) return
  
  if (moreImagesFiles.value.length === 0) {
    ElMessage.warning('请先选择要上传的图片')
    return
  }
  
  isUploadingMoreImages.value = true
  
  try {
    // 准备要上传的文件
    const files = moreImagesFiles.value.map(file => file.raw)
    
    // 上传文件到MinIO
    const uploadRes = await uploadMultipleFiles(files)
    
    if (uploadRes.code !== 200) {
      throw new Error('文件上传失败: ' + uploadRes.message)
    }
    
    const newFileUrls = uploadRes.data
    
    // 获取当前辅料的图片列表
    let currentImages = []
    if (currentMaterial.value.images) {
      try {
        currentImages = JSON.parse(currentMaterial.value.images)
      } catch (e) {
        currentImages = []
      }
    }
    
    // 合并新上传的图片URL
    const updatedImages = [...currentImages, ...newFileUrls]
    
    // 更新辅料信息
    const updatedMaterial = {
      ...currentMaterial.value,
      images: JSON.stringify(updatedImages)
    }
    
    // 更新辅料信息（使用updateProduct而不是saveProduct）
    await updateProduct(updatedMaterial)
    
    // 更新当前辅料数据
    currentMaterial.value = updatedMaterial
    
    // 显示成功提示
    ElMessage.success('图片上传成功')
    
    // 关闭对话框
    uploadMoreImagesDialog.value = false
  } catch (error) {
    console.error('上传更多图片失败:', error)
    ElMessage.error('上传更多图片失败，请稍后重试')
  } finally {
    isUploadingMoreImages.value = false
  }
}

// 处理更多图片文件选择
const handleMoreImagesFileChange = (file, fileList) => {
  moreImagesFiles.value = fileList
}

// 处理更多图片文件移除
const handleMoreImagesFileRemove = (file, fileList) => {
  moreImagesFiles.value = fileList
}

// 处理单个图片URL的函数
const processImageUrl = (url) => {
  if (!url || typeof url !== 'string') return null
  
  // 过滤掉错误的图片URL（如"上传成功"）
  if (url === '上传成功' || url === '[]') {
    return null
  } else if (url.startsWith('http')) {
    // 提取文件名（忽略bucket名称）
    const lastSlashIndex = url.lastIndexOf('/')
    if (lastSlashIndex !== -1) {
      const filename = url.substring(lastSlashIndex + 1)
      // 使用后端接口获取图片，避免MinIO认证问题
      return `/file/get-image?filename=${filename}`
    }
  } else if (!url.startsWith('/file/get-image')) {
    // 如果不是http开头也不是/file/get-image格式，直接使用文件名
    return `/file/get-image?filename=${url}`
  }
  // 保留已经是/file/get-image格式的URL
  return url
}

// 获取处理后的单个图片URL
const getProcessedImageUrl = (material) => {
  if (!material) return ''
  
  // 优先使用处理后的image字段
  if (material.image) {
    const processed = processImageUrl(material.image)
    if (processed) return processed
  }
  
  // 处理imageUrl字段
  if (material.imageUrl) {
    const processed = processImageUrl(material.imageUrl)
    if (processed) return processed
  }
  
  // 处理images字段
  if (material.images) {
    try {
      const parsedImages = JSON.parse(material.images)
      if (Array.isArray(parsedImages) && parsedImages.length > 0) {
        const processed = processImageUrl(parsedImages[0])
        if (processed) return processed
      }
    } catch (e) {
      console.error('解析图片列表失败:', e)
    }
  }
  
  return ''
}

// 获取图片预览列表
const getImagePreviewList = (material) => {
  if (!material) return []
  
  let images = []
  
  // 优先使用image字段
  if (material.image) {
    const processedUrl = processImageUrl(material.image)
    if (processedUrl) {
      images.push(processedUrl)
    }
  }
  
  // 如果有images字段，解析并合并
  if (material.images) {
    try {
      const parsedImages = JSON.parse(material.images)
      if (Array.isArray(parsedImages) && parsedImages.length > 0) {
        // 处理每个图片URL
        const processedImages = parsedImages.map(url => processImageUrl(url)).filter(Boolean)
        
        // 如果image字段不在images中，添加到列表开头
        if (material.image) {
          const processedImageUrl = processImageUrl(material.image)
          if (processedImageUrl && !processedImages.includes(processedImageUrl)) {
            images = [processedImageUrl, ...processedImages]
          } else {
            images = processedImages
          }
        } else {
          images = processedImages
        }
      }
    } catch (e) {
      console.error('解析图片列表失败:', e)
    }
  }
  
  // 调试打印处理后的图片URL
  console.log('辅料名称:', material.productName || material.material_name);
  console.log('处理后的图片预览列表:', images);
  
  return images
}

// 处理图片加载错误
const handleImageError = (e) => {
  console.error('图片加载失败:', e)
  // 错误处理逻辑已在template中通过error插槽实现
}

// 调试打印辅料数据
const debugMaterialData = () => {
  console.log('当前辅料数据:', materials.value)
  // 打印每个辅料的图片字段
  materials.value.forEach((item, index) => {
    console.log(`辅料${index + 1}:`, {
      id: item.id,
      productName: item.productName,
      image: item.image,
      imageUrl: item.imageUrl,
      images: item.images
    })
  })
}

// Edit dialog actions
const handleEdit = (row) => {
  // 填充编辑表单
  editForm.id = row.id
  editForm.productCode = row.productCode || row.material_code || ''
  editForm.productName = row.productName || row.material_name || ''
  editForm.category = row.category || ''
  editForm.style = row.style || ''
  editForm.type = row.type || ''
  editForm.specification = row.specification || ''
  editForm.unit = row.unit || ''
  editForm.price = row.price || 0
  editForm.expectedDeliveryDays = row.expectedDeliveryDays || null
  editForm.description = row.description || ''
  
  // 显示编辑对话框
  editDialogVisible.value = true
}

const submitEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 准备更新数据
        const updatedMaterial = {
          id: editForm.id,
          productCode: editForm.productCode,
          productName: editForm.productName,
          category: editForm.category,
          style: editForm.style,
          type: editForm.type,
          specification: editForm.specification,
          unit: editForm.unit,
          price: editForm.price,
          expectedDeliveryDays: editForm.expectedDeliveryDays,
          description: editForm.description
        }
        
        // 调用更新API
        await updateProduct(updatedMaterial)
        
        ElMessage.success('辅料信息更新成功')
        
        // 关闭对话框
        editDialogVisible.value = false
        
        // 刷新列表
        loadMaterials()
      } catch (error) {
        console.error('更新辅料失败:', error)
        ElMessage.error('更新辅料失败，请稍后重试')
      }
    }
  })
}

// Detail actions
const previewImage = (imageUrl) => {
  if (imageUrl) {
    window.open(imageUrl, '_blank')
  }
}

const copyImageLink = (imageUrl) => {
  if (imageUrl) {
    navigator.clipboard.writeText(imageUrl)
      .then(() => {
        ElMessage.success('图片链接已复制到剪贴板')
      })
      .catch(() => {
        ElMessage.error('复制失败，请手动复制')
      })
  }
}

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

const compareMaterials = (material) => {
  ElMessage.info('材料对比功能开发中...')
  // 这里可以实现材料对比功能，将当前材料添加到对比列表
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

// 组件卸载时
onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped>
/* 完全使用Element Plus原生组件，无CSS硬编码 */
</style>

<style>
/* 防止弹窗导致页面出现滚动条 */
body {
  overflow-x: hidden;
}
</style>
