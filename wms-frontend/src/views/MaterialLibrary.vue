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
                  <el-option v-for="option in categoryOptions" :key="option.value" :label="option.label" :value="option.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="材质">
                <el-select v-model="filters.material" placeholder="全部" clearable style="width: 100%;">
                  <el-option v-for="option in materialOptions" :key="option.value" :label="option.label" :value="option.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="颜色">
                <el-select v-model="filters.color" placeholder="全部" clearable style="width: 100%;">
                  <el-option v-for="option in colorOptions" :key="option.value" :label="option.label" :value="option.value" />
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
            <el-col :xs="24" :sm="24" :md="24" :lg="24">
              <div style="display: flex; justify-content: center; margin-top: 20px;">
                <el-button type="primary" @click="handleSearch" :icon="Search">
                  确定筛选
                </el-button>
              </div>
            </el-col>
          </el-row>
        </el-collapse-item>
      </el-collapse>
      
      <!-- 智能功能按钮 -->
      <div style="margin-top: 20px;">
        <el-space size="medium">
          <el-button type="info" :icon="Search" @click="openImageSearch" round>
            图片搜索
          </el-button>
          <el-button type="warning" :icon="Clock" @click="openSearchHistory" round>
            搜索历史
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
            {{ showRecommendations ? '推荐列表' : showFavorites ? '我的收藏' : '辅料列表' }} ({{ materials.length }})
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
                :src="scope.row.image || 'https://via.placeholder.com/50'"
                loading="lazy"
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
      
      <!-- 分页组件 -->
      <div class="pagination-container" style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[8]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 图片搜索弹窗 -->
    <el-dialog v-model="imageSearchVisible" title="图片搜索" width="50%" append-to-body>
      <div style="margin-bottom: 20px;">
        <el-text type="info" size="small">
          💡 支持以图搜图、相似搜索，上传清晰的辅料图片以获得最佳搜索效果
        </el-text>
      </div>
      <el-upload
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleImageSearchFileChange"
        :on-remove="handleImageSearchFileRemove"
        :file-list="imageSearchFiles"
        :multiple="false"
        accept="image/*"
        :max-size="10485760"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽图片到此处或 <em>点击上传</em>
        </div>
      </el-upload>
      
      <el-divider v-if="searchImageResult">搜索结果</el-divider>
      
      <el-row :gutter="16" v-if="searchImageResult && searchImageResult.filter(item => item.similarity > 0.65).length">
        <el-col :span="8" v-for="item in searchImageResult.filter(item => item.similarity > 0.65)" :key="item.id">
          <el-card shadow="hover">
            <el-image 
              v-if="item.image"
              :src="item.image"
              loading="lazy"
              fit="contain"
              style="width: 100%; height: 200px; background-color: #f5f7fa; border-radius: 8px;"
            />
            <div v-else style="width: 100%; height: 200px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 8px;">
              <el-empty description="暂无图片" :image-size="80" />
            </div>
            <el-divider />
            <el-space direction="vertical" style="width: 100%">
              <el-text truncated>{{ item.productName }}</el-text>
              <el-text type="danger" tag="b">¥{{ item.price }}</el-text>
              <el-text type="success" size="small">相似度: {{ (item.similarity * 100).toFixed(0) }}%</el-text>
            </el-space>
            <div style="margin-top: 12px; display: flex; justify-content: flex-end; gap: 8px;">
              <el-popover
                placement="top"
                :width="350"
                trigger="click"
              >
                <template #reference>
                  <el-button type="info" size="small" :icon="View">
                    详情
                  </el-button>
                </template>
                <div style="padding: 15px;">
                  <el-image 
                    :src="item.image || 'https://via.placeholder.com/100'"
                    fit="cover" 
                    style="width: 100%; height: 150px; border-radius: 8px; margin-bottom: 15px;"
                  />
                  <el-text style="font-weight: 500; font-size: 16px;">{{ item.productName }}</el-text>
                  <el-text type="danger" style="display: block; margin-top: 10px; font-size: 15px;">
                    ¥{{ item.price }}
                  </el-text>
                  <el-text size="small" style="display: block; margin-top: 8px;">
                    相似度: {{ (item.similarity * 100).toFixed(0) }}%
                  </el-text>
                  <div style="margin-top: 12px; padding-top: 12px; border-top: 1px solid #e0e0e0;">
                    <el-space direction="vertical" size="6">
                      <el-text size="small">
                        <span style="font-weight: 500;">分类:</span> {{ item.category || '未分类' }}
                      </el-text>
                      <el-text size="small">
                        <span style="font-weight: 500;">材质:</span> {{ item.material || '未知' }}
                      </el-text>
                      <el-text size="small">
                        <span style="font-weight: 500;">颜色:</span> {{ item.color || '未知' }}
                      </el-text>
                      <el-text size="small">
                        <span style="font-weight: 500;">库存:</span> {{ item.stock || 0 }} {{ item.unit || '件' }}
                      </el-text>
                      <el-text size="small">
                        <span style="font-weight: 500;">供应商:</span> {{ item.supplier || '未知' }}
                      </el-text>
                    </el-space>
                  </div>
                </div>
              </el-popover>
              <el-button type="primary" size="small" :icon="ShoppingCart" @click="openProjectSchemeDialog(item)">
                加入项目方案
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else-if="!isSearching && searchImageResult" description="暂无相似度大于65%的辅料" :image-size="80" />
      
      <el-empty v-else-if="!isSearching" description="暂无搜索结果" :image-size="80" />
      
      <el-space v-if="isSearching" justify="center" style="width: 100%">
        <el-icon class="is-loading"><Loading /></el-icon>
        <el-text>正在搜索相似图片...</el-text>
      </el-space>
    </el-dialog>

    <!-- 搜索历史对话框 -->
    <el-dialog v-model="searchHistoryVisible" title="图片搜索历史" width="70%" append-to-body>
      <div style="margin-bottom: 15px; text-align: right;">
        <el-button type="danger" size="small" @click="clearSearchHistory">
          清除历史
        </el-button>
      </div>
      <el-table :data="searchHistory" style="width: 100%">
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image 
              v-if="scope.row.thumbnail" 
              :src="scope.row.thumbnail" 
              fit="cover" 
              style="width: 60px; height: 60px; border-radius: 4px;"
            >
              <template #error>
                <div style="width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 4px;">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else style="width: 60px; height: 60px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa; border-radius: 4px;">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="timestamp" label="搜索时间" width="180" />
        <el-table-column prop="fileName" label="图片名称" min-width="200" />
        <el-table-column prop="resultCount" label="搜索结果数" width="120" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="retrySearch(scope.row)">重新搜索</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="searchHistory.length === 0" description="暂无搜索历史数据" :image-size="80" />
    </el-dialog>


    
    <!-- 添加辅料对话框 -->
    <el-dialog v-model="addMaterialDialog" title="添加新辅料" width="600px" append-to-body>
      <el-form :model="addMaterialForm" :rules="addMaterialRules" ref="addMaterialFormRef" label-width="100px">
        <el-form-item label="辅料编码">
          <el-input v-model="addMaterialForm.productCode" placeholder="请输入辅料编码" />
        </el-form-item>
        <el-form-item label="辅料名称">
          <el-input v-model="addMaterialForm.productName" placeholder="请输入辅料名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="addMaterialForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="面料" value="面料" />
            <el-option label="辅料" value="辅料" />
            <el-option label="扣件" value="扣件" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体类型">
          <el-select v-model="addMaterialForm.type" placeholder="请选择具体类型" style="width: 100%" filterable allow-create default-first-option>
            <el-option v-for="type in typeOptions" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>
        <el-form-item label="风格">
          <el-select v-model="addMaterialForm.style" placeholder="请选择风格" style="width: 100%" filterable allow-create default-first-option>
            <el-option v-for="style in styleOptions" :key="style" :label="style" :value="style" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="addMaterialForm.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="addMaterialForm.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="addMaterialForm.price" :precision="2" :min="0" style="width: 100%" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="预计货期">
          <el-input-number v-model="addMaterialForm.expectedDeliveryDays" :min="0" style="width: 100%" placeholder="请输入预计货期（天）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="addMaterialForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="addMaterialForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addMaterialDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAddMaterial">确定添加</el-button>
      </template>
    </el-dialog>

    <!-- 选择项目方案对话框 - 移至 MainLayout.vue 中 -->

    
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
          <el-select v-model="editForm.style" placeholder="请选择风格" style="width: 100%" filterable allow-create default-first-option>
            <el-option v-for="style in styleOptions" :key="style" :label="style" :value="style" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体类型">
          <el-select v-model="editForm.type" placeholder="请选择具体类型" style="width: 100%" filterable allow-create default-first-option>
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
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Star, ShoppingCart, UploadFilled, Loading, Clock, Search, RefreshLeft, View, Refresh, ZoomIn, CopyDocument, Share, Close, Upload, Edit, Check } from '@element-plus/icons-vue'
import { getMaterialList, searchByImage } from '@/api/material'
import { saveProduct, updateProduct, recommendProducts, deleteProduct, collaborativeRecommend, recordBehavior } from '@/api/product'
import { getProjectList, addMaterialToScheme } from '@/api/project'

import { uploadFile, deleteFile, checkFileExists, uploadMultipleFiles } from '@/api/file'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import ProjectSchemeDialog from '../components/ProjectSchemeDialog.vue'
import { useUserStore } from '@/stores/user'
import { useMaterialStore } from '@/stores/material'

const userStore = useUserStore()
const materialStore = useMaterialStore()
const route = useRoute()

const materials = ref([])
const loading = ref(false)
const sortBy = ref('default')
const viewMode = ref('grid')

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const allMaterials = ref([])
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

// 动态筛选选项
const categoryOptions = ref([])
const materialOptions = ref([])
const colorOptions = ref([])

// 收藏视图切换
const showFavorites = ref(false)

// 推荐视图切换
const showRecommendations = ref(false)

// 确保默认显示辅料列表
console.log('默认显示辅料列表，showFavorites:', showFavorites.value, 'showRecommendations:', showRecommendations.value)

// 监听路由变化，确保每次进入页面都刷新数据
watch(() => route.path, (newPath) => {
  console.log('路由路径变化:', newPath)
  if (newPath === '/material-library') {
    console.log('触发加载辅料数据')
    loadMaterials()
  }
})

// 同时监听路由完整路径，确保各种情况下都能触发
watch(() => route.fullPath, (newFullPath) => {
  console.log('路由完整路径变化:', newFullPath)
  if (newFullPath.includes('/material-library')) {
    console.log('触发加载辅料数据（完整路径）')
    loadMaterials()
  }
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
const toggleFavorite = async (materialId) => {
  if (favoriteMaterials.value.has(materialId)) {
    favoriteMaterials.value.delete(materialId)
    ElMessage.success('已取消收藏')
  } else {
    favoriteMaterials.value.add(materialId)
    ElMessage.success('已添加到收藏')
    if (userStore.user?.id) {
      try {
        await recordBehavior(userStore.user.id, materialId, 'favorite')
      } catch (e) {
        console.warn('记录收藏行为失败:', e)
      }
    }
  }
  
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
    const filteredMaterials = allMaterials.value.filter(item => favoriteMaterials.value.has(item.id))
    allMaterials.value = filteredMaterials
    total.value = allMaterials.value.length
    currentPage.value = 1
    updatePagination()
  } else {
    // Reload all materials
    loadMaterials()
  }
}

// Toggle recommendations view
const toggleRecommendationsView = async () => {
  showRecommendations.value = !showRecommendations.value
  showFavorites.value = false
  
  console.log('推荐按钮点击 - 当前用户信息:', userStore.user)
  console.log('推荐按钮点击 - userStore.user?.id:', userStore.user?.id)
  
  if (showRecommendations.value) {
    if (!userStore.user?.id) {
      console.warn('用户 ID 不存在，检查是否已登录')
      ElMessage.warning('请先登录以获取个性化推荐')
      showRecommendations.value = false
      loadMaterials()
      return
    }
    
    try {
      ElMessage.info('正在生成个性化推荐，请稍候...')
      const res = await collaborativeRecommend(userStore.user.id)
      if (res && res.data && res.data.length > 0) {
        // 处理推荐数据的图片字段
        const recommendedData = res.data.map(item => {
          // 处理图片字段
          let imageUrl = item.imageUrl || item.image
          
          // 优先处理 images 字段（JSON 格式的图片数组）
          if (item.images) {
            try {
              let imagesStr = item.images
              if (typeof imagesStr === 'string' && imagesStr.startsWith('"') && imagesStr.endsWith('"')) {
                imagesStr = imagesStr.substring(1, imagesStr.length - 1)
              }
              const images = JSON.parse(imagesStr)
              if (Array.isArray(images) && images.length > 0) {
                imageUrl = images[0]
              }
            } catch (e) {
              console.error('解析图片列表失败:', e)
            }
          }
          
          // 处理图片 URL
          if (imageUrl && typeof imageUrl === 'string') {
            if (imageUrl === '上传成功' || imageUrl === '[]') {
              imageUrl = null
            } else if (imageUrl.startsWith('http')) {
              const lastSlashIndex = imageUrl.lastIndexOf('/')
              if (lastSlashIndex !== -1) {
                const filename = imageUrl.substring(lastSlashIndex + 1)
                imageUrl = `/file/get-image?filename=${filename}`
              }
            } else if (imageUrl && !imageUrl.startsWith('/file/get-image')) {
              imageUrl = `/file/get-image?filename=${imageUrl}`
            }
          }
          
          console.log('推荐辅料名称:', item.productName);
          console.log('推荐 - 处理后的图片 URL:', imageUrl);
          
          return {
            ...item,
            image: imageUrl
          }
        })
        
        allMaterials.value = recommendedData
        total.value = allMaterials.value.length
        currentPage.value = 1
        updatePagination()
        ElMessage.success(`为您推荐 ${recommendedData.length} 个辅料`)
      } else {
        ElMessage.info('暂无推荐数据，系统为您推荐热门辅料')
        loadMaterials()
      }
    } catch (error) {
      console.error('获取推荐失败:', error)
      ElMessage.warning('推荐服务暂时不可用，显示全部辅料')
      loadMaterials()
    }
  } else {
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
watch(sortBy, () => {
  sortMaterials()
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



// Image Search
const imageSearchVisible = ref(false)
const isSearching = ref(false)
const searchImageResult = ref(null)
const imageSearchFiles = ref([])

// Search History
const searchHistoryVisible = ref(false)
const searchHistory = ref([])

// Detail
const detailPopover = ref(null)




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
  console.log('=== 开始加载辅料数据 ===')
  console.log('当前时间:', new Date().toLocaleString())
  loading.value = true
  try {
    // 获取辅料列表数据
    console.log('准备发送请求获取辅料列表')
    console.log('调用getMaterialList()')
    const res = await getMaterialList()
    console.log('收到响应:', res)
    console.log('响应类型:', typeof res)
    console.log('响应是否为空:', res === null || res === undefined)
    
    // 检查响应数据结构
    if (!res) {
      console.error('响应为空')
      ElMessage.error('加载辅料列表失败: 响应为空')
      // 即使响应为空，也要设置空数组，确保页面显示正常
      materials.value = []
      allMaterials.value = []
      total.value = 0
      return
    }
    
    if (res.code === 200) {
      let materialData = res.data || []
      console.log('原始辅料数据:', materialData)
      console.log('原始辅料数据数量:', materialData.length)
      console.log('辅料状态分布:', materialData.map(item => item.status))
      
      // 只显示已上架的辅料（status === 1）
      console.log('原始辅料数据数量:', materialData.length)
      console.log('辅料状态分布:', materialData.map(item => item.status))
      
      // 过滤出已上架的辅料（状态为1），同时处理状态值为null或undefined的情况
      console.log('原始辅料数据状态分布:', materialData.map(item => ({ id: item.id, status: item.status, productName: item.productName })))
      const activeMaterials = materialData.filter(item => {
        // 确保status字段存在且为数字1
        return item.status === 1
      })
      console.log('已上架辅料数量:', activeMaterials.length)
      console.log('已上架辅料详情:', activeMaterials.map(item => ({ id: item.id, status: item.status, productName: item.productName })))
      
      // 如果没有状态为1的辅料，尝试显示所有非删除状态的辅料（状态不为-1）
      let finalMaterials = activeMaterials
      if (finalMaterials.length === 0) {
        console.log('没有状态为1的辅料，尝试显示所有非删除状态的辅料')
        finalMaterials = materialData.filter(item => item.status !== -1)
        console.log('非删除状态辅料数量:', finalMaterials.length)
        console.log('非删除状态辅料详情:', finalMaterials.map(item => ({ id: item.id, status: item.status, productName: item.productName })))
      }
      
      // 处理辅料数据
      const processedMaterials = finalMaterials.map(item => {
        
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
          stock: item.stock || 0 // 使用产品自身的库存数据，默认值为0
        }
      })
      
      // 应用筛选条件
      let filteredMaterials = processedMaterials
      
      // 关键字搜索
      if (filters.keyword) {
        const keyword = filters.keyword.toLowerCase()
        filteredMaterials = filteredMaterials.filter(item => 
          (item.productName?.toLowerCase().includes(keyword) || 
           item.productCode?.toLowerCase().includes(keyword) || 
           item.description?.toLowerCase().includes(keyword))
        )
      }
      
      // 分类筛选
      if (filters.category) {
        filteredMaterials = filteredMaterials.filter(item => item.category === filters.category)
      }
      
      // 材质筛选
      if (filters.material) {
        filteredMaterials = filteredMaterials.filter(item => item.material === filters.material)
      }
      
      // 颜色筛选
      if (filters.color) {
        filteredMaterials = filteredMaterials.filter(item => item.color === filters.color)
      }
      
      // 价格范围筛选
      if (filters.minPrice !== '' && filters.minPrice !== null) {
        filteredMaterials = filteredMaterials.filter(item => item.price >= filters.minPrice)
      }
      if (filters.maxPrice !== '' && filters.maxPrice !== null) {
        filteredMaterials = filteredMaterials.filter(item => item.price <= filters.maxPrice)
      }
      
      // 库存筛选
      if (filters.inStock !== null) {
        if (filters.inStock) {
          filteredMaterials = filteredMaterials.filter(item => item.stock > 0)
        } else {
          filteredMaterials = filteredMaterials.filter(item => item.stock <= 0)
        }
      }
      
      // 存储筛选后的数据用于分页
      allMaterials.value = filteredMaterials
      total.value = allMaterials.value.length
      currentPage.value = 1
      console.log('调用updatePagination前 - allMaterials:', allMaterials.value)
      console.log('调用updatePagination前 - allMaterials.length:', allMaterials.value.length)
      console.log('调用updatePagination前 - total:', total.value)
      console.log('调用updatePagination前 - currentPage:', currentPage.value)
      // 使用updatePagination()处理分页
      updatePagination()
      console.log('调用updatePagination后 - materials:', materials.value)
      console.log('调用updatePagination后 - materials.length:', materials.value.length)
      // 强制更新页面
      console.log('强制更新页面')
      materials.value = [...materials.value]
      
      // 提取唯一的分类、材质、颜色选项（基于筛选前的所有数据）
      extractFilterOptions(processedMaterials)
      
      // 调试打印辅料数据
      console.log('加载的辅料数据:', processedMaterials)
      console.log('图片URL示例:', processedMaterials.map(item => ({ 
        name: item.productName, 
        image: item.image 
      })))
      console.log('总辅料数量:', total.value)
    } else {
      console.error('响应代码不是200:', res)
      ElMessage.error('加载辅料列表失败: ' + (res.message || '未知错误'))
      // 即使响应失败，也要设置空数组，确保页面显示正常
      materials.value = []
      allMaterials.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载辅料列表失败:', error)
    console.error('错误详情:', error)
    // 友好的错误提示
    if (error.message.includes('Network Error')) {
      ElMessage.error('网络连接失败，请检查后端服务是否运行')
    } else {
      ElMessage.error('加载辅料列表失败: ' + (error.message || '网络错误'))
    }
    // 即使出错，也要设置空数组，确保页面显示正常
    materials.value = []
    allMaterials.value = []
    total.value = 0
  } finally {
    loading.value = false
    console.log('加载辅料数据完成')
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

// 提取筛选选项
const extractFilterOptions = (materials) => {
  // 提取唯一的分类
  const categories = new Set()
  // 提取唯一的材质
  const materialsSet = new Set()
  // 提取唯一的颜色
  const colors = new Set()
  
  materials.forEach(item => {
    if (item.category) categories.add(item.category)
    if (item.material) materialsSet.add(item.material)
    if (item.color) colors.add(item.color)
  })
  
  // 更新选项
  categoryOptions.value = Array.from(categories).map(cat => ({ label: cat, value: cat }))
  materialOptions.value = Array.from(materialsSet).map(mat => ({ label: mat, value: mat }))
  colorOptions.value = Array.from(colors).map(col => ({ label: col, value: col }))
  
  console.log('提取的分类选项:', categoryOptions.value)
  console.log('提取的材质选项:', materialOptions.value)
  console.log('提取的颜色选项:', colorOptions.value)
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

const openImageSearch = () => {
  searchImageResult.value = null
  imageSearchVisible.value = true
}

const openSearchHistory = () => {
  loadSearchHistory()
  searchHistoryVisible.value = true
}

const loadSearchHistory = () => {
  try {
    const history = localStorage.getItem('imageSearchHistory')
    if (history) {
      searchHistory.value = JSON.parse(history)
    } else {
      searchHistory.value = []
    }
  } catch (error) {
    console.error('加载搜索历史失败:', error)
    searchHistory.value = []
  }
}

const saveSearchHistory = (fileName, resultCount, thumbnail) => {
  try {
    // 加载现有历史
    loadSearchHistory()
    
    // 创建新的历史记录
    const newHistory = {
      id: Date.now(),
      fileName,
      resultCount,
      timestamp: new Date().toLocaleString(),
      thumbnail
    }
    
    // 添加到历史记录开头
    searchHistory.value.unshift(newHistory)
    
    // 限制最多保存8条记录
    if (searchHistory.value.length > 8) {
      searchHistory.value = searchHistory.value.slice(0, 8)
    }
    
    // 保存到localStorage
    localStorage.setItem('imageSearchHistory', JSON.stringify(searchHistory.value))
  } catch (error) {
    console.error('保存搜索历史失败:', error)
  }
}

const retrySearch = (historyItem) => {
  // 由于搜索历史中只保存了文件名，没有保存实际的图片文件
  // 所以这里提示用户需要重新上传图片
  ElMessage.info('请重新上传图片以执行搜索')
  searchHistoryVisible.value = false
  openImageSearch()
}

const clearSearchHistory = () => {
  ElMessageBox.confirm('确定要清除所有搜索历史吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清空搜索历史
    searchHistory.value = []
    // 清除localStorage中的数据
    localStorage.removeItem('imageSearchHistory')
    ElMessage.success('搜索历史已清除')
  }).catch(() => {
    // 取消清除
  })
}

const handleImageSearchFileRemove = (file, fileList) => {
  console.log('移除图片搜索文件:', file);
  imageSearchFiles.value = fileList.map(f => ({
    name: f.name,
    url: f.url,
    uid: f.uid,
    raw: f.raw
  }))
  searchImageResult.value = null
}

const handleImageSearchFileChange = async (file, fileList) => {
  console.log('=== 图片搜索文件变更事件触发 ===');
  console.log('文件信息:', file);
  console.log('文件列表:', fileList);
  
  // 更新文件列表
  imageSearchFiles.value = fileList.map(f => ({
    name: f.name,
    url: URL.createObjectURL(f.raw),
    uid: f.uid,
    raw: f.raw
  }))
  
  console.log('更新后的文件列表:', imageSearchFiles.value);
  
  isSearching.value = true
  searchImageResult.value = null
  
  try {
    console.log('开始图片搜索处理');
    
    if (!file || !file.raw) {
      throw new Error('文件格式错误，缺少原始文件对象')
    }
    
    console.log('文件验证通过，开始调用API');
    
    // 调用真实的图片搜索 API（不再需要单独上传到 MinIO）
    const res = await searchByImage(file, 10, 0.6) // limit=10, threshold=0.6
    
    console.log('API调用完成，响应结果:', res);
    if (res.code === 200) {
      console.log('图片搜索成功，结果数量:', res.data ? res.data.length : 0);
      
      // 处理搜索结果，添加相似度格式化和图片URL处理
      searchImageResult.value = (res.data || [])
        .filter(item => item.status === 1) // 只显示已上架的辅料
        .map(item => {
        
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
        
        // 使用processImageUrl工具函数处理图片URL
        const processedImageUrl = processImageUrl(imageUrl)
        
        console.log('辅料名称:', item.productName);
        console.log('原始图片URL:', imageUrl);
        console.log('处理后的图片URL:', processedImageUrl);
        
        return {
          ...item,
          image: processedImageUrl, // 确保图片字段可用
          stock: item.stock || 0,
          similarityFormatted: item.similarity ? (item.similarity * 100).toFixed(1) + '%' : 'N/A'
        }
      })
      
      // 计算实际显示的搜索结果数量（相似度 > 0.65）
      const actualResultCount = searchImageResult.value.filter(item => item.similarity > 0.65).length
      
      if (actualResultCount === 0) {
        ElMessage.info('未找到相似辅料，请尝试其他图片')
        // 保存搜索历史（无缩略图）
        saveSearchHistory(file.name, actualResultCount, null)
      } else {
        ElMessage.success(`找到 ${actualResultCount} 个相似辅料`)
        // 生成并保存缩略图
        generateThumbnail(file.raw)
          .then(thumbnail => {
            // 保存搜索历史
            saveSearchHistory(file.name, actualResultCount, thumbnail)
          })
          .catch(error => {
            console.error('生成缩略图失败:', error)
            // 保存搜索历史（无缩略图）
            saveSearchHistory(file.name, actualResultCount, null)
          })
      }
    } else {
      throw new Error(res.message || '搜索失败')
    }
  } catch (error) {
    console.error('图片搜索失败:', error)
    ElMessage.error('图片搜索失败：' + (error.message || '未知错误'))
  } finally {
    isSearching.value = false
  }
}

const showDetail = async (item, event) => {
  // 使用产品自身的库存数据
  item.stock = item.stock || 0
  
  // 记录浏览行为
  if (userStore.user?.id) {
    try {
      await recordBehavior(userStore.user.id, item.id, 'browse')
      console.log('浏览行为已记录:', userStore.user.id, item.id)
    } catch (e) {
      console.warn('记录浏览行为失败:', e)
    }
  }
  
  // 显示对话框（使用 materialStore）
  materialStore.showDetail(item)
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

// 处理图片URL的工具函数
const processImageUrl = (imageUrl) => {
  if (!imageUrl || typeof imageUrl !== 'string') {
    return null
  }
  
  // 过滤掉错误的图片URL
  if (imageUrl === '上传成功' || imageUrl === '[]') {
    return null
  }
  
  // 处理HTTP开头的URL
  if (imageUrl.startsWith('http')) {
    // 提取文件名（忽略bucket名称）
    const lastSlashIndex = imageUrl.lastIndexOf('/')
    if (lastSlashIndex !== -1) {
      const filename = imageUrl.substring(lastSlashIndex + 1)
      // 使用后端接口获取图片，避免MinIO认证问题
      return `/file/get-image?filename=${filename}`
    }
  } else if (!imageUrl.startsWith('/file/get-image')) {
    // 如果不是http开头也不是/file/get-image格式，直接使用文件名
    return `/file/get-image?filename=${imageUrl}`
  }
  
  // 保留已经是/file/get-image格式的URL
  return imageUrl
}

// 生成图片缩略图的函数
const generateThumbnail = (file) => {
  return new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    const img = new Image()
    
    img.onload = () => {
      // 设置缩略图尺寸
      const maxWidth = 100
      const maxHeight = 100
      let width = img.width
      let height = img.height
      
      // 计算缩放比例
      if (width > height) {
        if (width > maxWidth) {
          height *= maxWidth / width
          width = maxWidth
        }
      } else {
        if (height > maxHeight) {
          width *= maxHeight / height
          height = maxHeight
        }
      }
      
      // 设置canvas尺寸
      canvas.width = width
      canvas.height = height
      
      // 绘制缩略图
      ctx.drawImage(img, 0, 0, width, height)
      
      // 转换为base64格式
      const thumbnailBase64 = canvas.toDataURL('image/jpeg', 0.7)
      resolve(thumbnailBase64)
    }
    
    img.onerror = () => {
      reject(new Error('图片加载失败'))
    }
    
    // 读取文件并设置图片源
    const reader = new FileReader()
    reader.onload = (e) => {
      img.src = e.target.result
    }
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    reader.readAsDataURL(file)
  })
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





// 添加到项目方案 - 状态已移至 material store 管理

// 打开选择项目方案对话框
const openProjectSchemeDialog = (item) => {
  materialStore.showProjectSchemeDialog(item)
}

// 处理添加成功
const handleAddSuccess = async () => {
  // 记录添加方案行为
  if (userStore.user?.id && materialStore.currentMaterialForProject) {
    try {
      await recordBehavior(userStore.user.id, materialStore.currentMaterialForProject.id, 'add_to_scheme')
    } catch (e) {
      console.warn('记录添加方案行为失败:', e)
    }
  }
  materialStore.hideProjectSchemeDialog()
}


// 分页方法
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  updatePagination()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  updatePagination()
}

// 更新分页数据
const updatePagination = () => {
  console.log('updatePagination - allMaterials.length:', allMaterials.value.length)
  console.log('updatePagination - currentPage:', currentPage.value)
  console.log('updatePagination - pageSize:', pageSize.value)
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  console.log('updatePagination - start:', start)
  console.log('updatePagination - end:', end)
  materials.value = allMaterials.value.slice(start, end)
  console.log('updatePagination - materials.length:', materials.value.length)
  console.log('updatePagination - materials:', materials.value)
}

onMounted(async () => {
  console.log('=== 组件挂载开始 ===')
  console.log('当前路由路径:', window.location.pathname)
  console.log('当前路由完整路径:', window.location.href)
  
  console.log('1. 加载风格选项')
  loadStyleOptions()
  console.log('2. 加载类型选项')
  loadTypeOptions()
  console.log('3. 加载收藏列表')
  loadFavorites()
  
  console.log('4. 重置筛选条件')
  resetFilters()
  console.log('5. 开始加载辅料数据')
  // 直接调用loadMaterials()，不使用try-catch，确保错误能够被正确捕获和处理
  loadMaterials()
  console.log('=== 组件挂载完成 ===')
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
