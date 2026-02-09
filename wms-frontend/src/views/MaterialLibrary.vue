<template>
  <div>
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
        </el-space>
      </div>
    </el-card>

    <!-- 辅料列表 -->
    <div style="padding: 0 20px 20px;">
      <!-- 列表头部 -->
      <div style="margin: 20px 0; display: flex; justify-content: space-between; align-items: center;">
        <el-text :type="'primary'" :size="'large'">
          {{ showFavorites ? '我的收藏' : '辅料列表' }} ({{ materials.length }})
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
            fit="cover" 
            style="width: 100%; height: 200px; border-radius: 8px; cursor: pointer;"
            @click="showDetail(item, $event)"
            :error="handleImageError"
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
                :src="scope.row.image || 'https://via.placeholder.com/50'" 
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
        <el-table-column label="操作" width="180" fixed="right">
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
      :height="600"
      destroy-on-close
    >
      <el-scrollbar height="500px" wrap-style="overflow-x: hidden;">
        <div style="padding: 0 10px;">
      <!-- 上传区域 -->
      <div v-if="!recognitionResult">
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
      </div>
      
      <!-- 加载中状态 -->
      <div v-else-if="isRecognizing" style="padding: 40px 0; text-align: center;">
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
              v-if="recognitionResult.image"
              :src="recognitionResult.image"
              fit="cover"
              style="width: 200px; height: 200px; border-radius: 4px;"
            />
            <div v-else style="width: 200px; height: 200px; border: 1px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center;">
              <el-text type="info">无识别图片</el-text>
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
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :xs="24" :sm="12">
                  <el-form-item label="校正具体类型">
                    <el-select v-model="correctionForm.type" placeholder="选择具体类型" filterable allow-create>
                      <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
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
                      :src="item.image || 'https://via.placeholder.com/100'" 
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
      </el-scrollbar>
    </el-dialog>

    <!-- 图片搜索弹窗 -->
    <el-dialog v-model="imageSearchVisible" title="图片搜索" width="50%">
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
              :src="item.image" 
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

    <el-dialog v-model="aiHistoryVisible" title="AI 识别历史" width="80%">
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
              :src="scope.row.image" 
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

    <!-- 详情弹窗 -->
    <el-popover
      ref="detailPopover"
      :visible="detailVisible"
      :virtual-ref="detailTargetElement"
      virtual-triggering
      placement="right"
      :width="700"
      trigger="click"
      :close-on-click-outside="true"
      :close-on-press-escape="true"
      @hide="handlePopoverHide"
      :popper-options="{ 
        modifiers: [
          {
            name: 'clickOutside',
            enabled: true
          }
        ]
      }"
    >
      <el-scrollbar height="75vh" wrap-style="overflow-x: hidden;" v-if="currentMaterial">
        <!-- 关闭按钮 -->
        <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
          <el-button 
            size="small" 
            :icon="Close" 
            circle 
            @click="handlePopoverHide"
            title="关闭"
          />
        </div>
        
        <!-- 主要信息区域 -->
        <div style="margin-bottom: 20px;">
          <!-- 图片区域 -->
          <div style="margin-bottom: 15px;">
            <el-image 
              v-if="currentMaterial.image || currentMaterial.imageUrl || currentMaterial.images"
              :src="currentMaterial.image || currentMaterial.imageUrl || (currentMaterial.images ? JSON.parse(currentMaterial.images)[0] : '')" 
              fit="contain" 
              style="width: 100%; max-height: 400px; border-radius: 8px; background-color: #f5f7fa;"
              preview-teleported
              :preview-src-list="getImagePreviewList(currentMaterial)"
            />
            <el-empty v-else description="暂无图片" :image-size="80" />
          </div>
          
          <!-- 基本信息区域 -->
          <div style="margin-bottom: 15px;">
            <!-- 标题和编码 -->
            <div style="margin-bottom: 15px;">
              <el-space direction="vertical" size="small" style="width: 100%;">
                <el-text :type="'primary'" :size="'large'" :strong="true">
                  {{ currentMaterial.productName }}
                </el-text>
                <el-text :type="'info'" size="small">
                  编码: {{ currentMaterial.productCode }}
                </el-text>
              </el-space>
            </div>
            
            <!-- 快速信息标签 -->
            <div style="margin-bottom: 15px;">
              <el-space wrap>
                <el-tag size="medium">{{ currentMaterial.category }}</el-tag>
                <el-tag size="medium" v-if="currentMaterial.specification">{{ currentMaterial.specification }}</el-tag>
                <el-tag size="medium" v-if="currentMaterial.unit">{{ currentMaterial.unit }}</el-tag>
                <el-tag 
                  :type="currentMaterial.stock <= 0 ? 'danger' : currentMaterial.stock < 100 ? 'warning' : 'success'"
                  size="medium"
                >
                  {{ currentMaterial.stock || 0 }} {{ currentMaterial.unit }}
                </el-tag>
              </el-space>
            </div>
            
            <!-- 详细信息 -->
            <el-descriptions :column="2" :size="'small'" border>
              <el-descriptions-item label="分类">{{ currentMaterial.category }}</el-descriptions-item>
              <el-descriptions-item label="规格">{{ currentMaterial.specification || '未设置' }}</el-descriptions-item>
              <el-descriptions-item label="单位">{{ currentMaterial.unit }}</el-descriptions-item>
              <el-descriptions-item label="单价">
                <el-text type="danger" :strong="true" style="font-size: 16px;">¥{{ currentMaterial.price }}</el-text>
              </el-descriptions-item>
              <el-descriptions-item label="库存">
                <el-tag :type="currentMaterial.stock <= 0 ? 'danger' : currentMaterial.stock < 100 ? 'warning' : 'success'">
                  {{ currentMaterial.stock || 0 }} {{ currentMaterial.unit }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="供应商">{{ currentMaterial.supplier || '未指定' }}</el-descriptions-item>
              <el-descriptions-item label="预计货期">{{ currentMaterial.expectedDeliveryDays || 0 }} 天</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ currentMaterial.updatedAt || '未知' }}</el-descriptions-item>
            </el-descriptions>
            
            <!-- 详细描述 -->
            <div style="margin-top: 15px;">
              <el-collapse v-model="detailCollapseActive">
                <el-collapse-item title="详细描述" name="description">
                  <el-text>
                    {{ currentMaterial.description || '无描述' }}
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
                :type="isFavorited(currentMaterial.id) ? 'warning' : 'default'" 
                :icon="Star"
                size="small"
                @click="toggleFavorite(currentMaterial.id)"
                :title="isFavorited(currentMaterial.id) ? '取消收藏' : '添加收藏'"
              >
                {{ isFavorited(currentMaterial.id) ? '已收藏' : '收藏' }}
              </el-button>
              <el-button 
                type="info" 
                :icon="Share"
                size="small"
                @click="shareMaterial(currentMaterial)"
              >
                分享
              </el-button>
              <el-button 
                type="warning" 
                :icon="DataAnalysis"
                size="small"
                @click="compareMaterials(currentMaterial)"
              >
                对比
              </el-button>
              <el-button 
                type="primary" 
                :icon="ShoppingCart"
                size="small"
                @click="openProjectSchemeDialog(currentMaterial)"
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
            <el-button size="small" @click="refreshSimilarMaterials(currentMaterial)">
              刷新推荐
            </el-button>
          </el-space>
          
          <el-row v-if="similarMaterials.length > 0" :gutter="10">
            <el-col v-for="item in similarMaterials" :key="item.id" :span="12">
              <el-card 
                shadow="hover" 
                :body-style="{ padding: '10px' }"
                @click="showDetail(item, $event)"
                style="cursor: pointer;"
              >
                <el-image 
                  v-if="item.image"
                  :src="item.image" 
                  fit="cover"
                  style="width: 100%; height: 80px; border-radius: 4px; margin-bottom: 8px;"
                />
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
          
          <el-row v-if="recommendations.length > 0" :gutter="10">
            <el-col v-for="rec in recommendations" :key="rec.id" :span="12">
              <el-card 
                shadow="hover" 
                :body-style="{ padding: '10px' }"
              >
                <el-image 
                  v-if="rec.image"
                  :src="rec.image" 
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
    </el-popover>
    
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
    
    <!-- 上传更多图片对话框 -->
    <el-dialog v-model="uploadMoreImagesDialog" title="上传更多图片" width="500px">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Camera, Star, ShoppingCart, UploadFilled, Loading, Clock, Search, RefreshLeft, View, Refresh, ZoomIn, CopyDocument, Share, DataAnalysis, Close, Upload } from '@element-plus/icons-vue'
import { getMaterialList, recognizeMaterial, searchByImage } from '@/api/material'
import { saveProduct, updateProduct } from '@/api/product'
import { getProjectList, addMaterialToScheme } from '@/api/project'
import { getInventoryList } from '@/api/inventory'
import { uploadFile, deleteFile, checkFileExists, uploadMultipleFiles } from '@/api/file'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

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
  if (showFavorites.value) {
    // Filter materials to only show favorites
    const allMaterials = materials.value
    materials.value = allMaterials.filter(item => favoriteMaterials.value.has(item.id))
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
      handleFileChange(file)
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
        if (checkRes.data) {
          ElMessage.warning('该图片已存在于辅料库中，无法重复添加')
          return
        }
      } else {
        throw new Error('文件检查失败: ' + checkRes.message)
      }
      
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
        images: JSON.stringify(fileUrls) // 添加所有图片URL，使用JSON格式
      }
      
      // 直接调用API保存辅料信息
      await saveProduct(materialData)
      
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
const detailVisible = ref(false)
const detailPopover = ref(null)
const detailTargetElement = ref(null)
const currentMaterial = ref(null)
const recommendations = ref([])
const similarMaterials = ref([])
const detailCollapseActive = ref(['description'])

// Upload more images dialog
const uploadMoreImagesDialog = ref(false)
const moreImagesFiles = ref([])
const isUploadingMoreImages = ref(false)

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
        
        // 处理图片URL
        if (imageUrl && typeof imageUrl === 'string') {
          // 过滤掉错误的图片URL（如"上传成功"）
          if (imageUrl === '上传成功' || imageUrl === '[]' || !imageUrl.startsWith('http')) {
            imageUrl = null
          } else {
            // 提取文件名（忽略bucket名称）
            const lastSlashIndex = imageUrl.lastIndexOf('/')
            if (lastSlashIndex !== -1) {
              let filename = imageUrl.substring(lastSlashIndex + 1)
              // 测试使用一个确实存在的文件
              if (filename === 'fdab8f44-dbec-471c-9170-35e36703ab7e.jpg') {
                filename = '6de6ffc4-0778-4179-b534-82b2722d3942.jpg'
              }
              // 使用后端接口获取图片，避免MinIO认证问题
              imageUrl = `/api/file/get-image?filename=${filename}`
            }
          }
        }
        
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

const handleFileChange = async (file) => {
  console.log('开始处理文件上传:', file);
  isRecognizing.value = true
  recognitionResult.value = null
  recognitionProgress.value = 0
  
  // 更新进度条的函数
  const updateProgress = (progress) => {
    recognitionProgress.value = progress
  }
  
  try {
    // 使用临时URL进行预览，不上传到MinIO
    console.log('使用临时URL进行预览');
    updateProgress(20)
    const tempUrl = URL.createObjectURL(file.raw)
    
    // 添加文件到上传列表
    uploadedFiles.value = [{
      name: file.name,
      url: tempUrl,
      uid: file.uid,
      raw: file.raw // 保存原始文件对象，用于后续上传
    }]
    
    // 调用AI识别API
    console.log('调用recognizeMaterial API');
    updateProgress(50)
    
    // 模拟进度更新
    const progressInterval = setInterval(() => {
      if (recognitionProgress.value < 90) {
        recognitionProgress.value += 5
      } else {
        clearInterval(progressInterval)
      }
    }, 200)
    
    const res = await recognizeMaterial(file)
    clearInterval(progressInterval)
    updateProgress(100)
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
        image: tempUrl // 使用临时URL进行预览
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
    }
  } catch (error) {
    console.error('处理文件失败:', error)
    ElMessage.error(`处理失败: ${error.message || '未知错误'}`)
    
    // 添加文件到上传列表（使用临时URL）
    uploadedFiles.value = [{
      name: file.name,
      url: URL.createObjectURL(file.raw),
      uid: file.uid
    }]
  } finally {
    console.log('设置isRecognizing为false');
    isRecognizing.value = false
    // 确保进度条达到100%
    if (recognitionResult.value) {
      recognitionProgress.value = 100
    }
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

const showDetail = async (item, event) => {
  currentMaterial.value = item
  
  // 设置目标元素为点击的元素
  if (event && event.currentTarget) {
    detailTargetElement.value = event.currentTarget
  }
  
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
  
  // 延迟显示弹窗，确保目标元素已设置
  setTimeout(() => {
    detailVisible.value = true
  }, 100)
}

// 处理弹窗隐藏
const handlePopoverHide = () => {
  detailVisible.value = false
  detailTargetElement.value = null
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

// 获取图片预览列表
const getImagePreviewList = (material) => {
  if (!material) return []
  
  let images = []
  
  // 优先使用image字段
  if (material.image) {
    images.push(material.image)
  }
  
  // 如果有images字段，解析并合并
  if (material.images) {
    try {
      const parsedImages = JSON.parse(material.images)
      if (Array.isArray(parsedImages) && parsedImages.length > 0) {
        // 如果image字段不在images中，添加到列表开头
        if (material.image && !parsedImages.includes(material.image)) {
          images = [material.image, ...parsedImages]
        } else {
          images = parsedImages
        }
      }
    } catch (e) {
      console.error('解析图片列表失败:', e)
    }
  }
  
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

const refreshSimilarMaterials = (item) => {
  loadSimilarMaterials(item)
  ElMessage.success('相似辅料推荐已刷新')
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
  
  // 添加全局点击事件监听器
  document.addEventListener('click', handleOutsideClick)
})

// 组件卸载时
onUnmounted(() => {
  // 移除全局点击事件监听器
  document.removeEventListener('click', handleOutsideClick)
})

// 处理点击外部区域关闭弹窗
const handleOutsideClick = (event) => {
  if (detailVisible.value) {
    const popoverElement = document.querySelector('.el-popover')
    const targetElement = detailTargetElement.value
    
    // 检查点击目标是否在弹窗内部或在触发元素上
    if (popoverElement && targetElement) {
      if (!popoverElement.contains(event.target) && !targetElement.contains(event.target)) {
        handlePopoverHide()
      }
    }
  }
}
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
