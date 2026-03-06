<template>
  <div class="product-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>辅料管理</span>
          <el-space size="medium">
            <el-button v-if="userStore.user?.role === 'admin'" type="warning" :icon="Camera" @click="openAIRecognition" round>
              AI 智能识别
            </el-button>
            <el-button v-if="userStore.user?.role === 'admin'" type="success" :icon="Clock" @click="openAIHistory" round>
              识别历史
            </el-button>
            <el-button type="primary" @click="handleAdd">新增辅料</el-button>
          </el-space>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="辅料编码">
          <el-input v-model="searchForm.productCode" placeholder="请输入辅料编码" clearable />
        </el-form-item>
        <el-form-item label="辅料名称">
          <el-input v-model="searchForm.productName" placeholder="请输入辅料名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="全部" value="" />
            <el-option label="上架" value="1" />
            <el-option label="待审核" value="2" />
            <el-option label="下架" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="productCode" label="辅料编码" width="150" />
        <el-table-column prop="imageUrl" label="缩略图" width="120" align="center">
          <template #default="scope">
            <el-image 
              :src="scope.row.imageUrl || 'https://via.placeholder.com/60'" 
              fit="cover" 
              style="width: 60px; height: 60px; border-radius: 4px; margin: 0 auto; display: block;"
              :preview-src-list="[scope.row.imageUrl || 'https://via.placeholder.com/60']"
              preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="辅料名称" width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="type" label="具体类型" width="120" />
        <el-table-column prop="style" label="风格" width="120" />
        <el-table-column prop="specification" label="规格" width="150" />
        <el-table-column prop="stock" label="库存数量" width="120">
          <template #default="{ row }">
            <div>
              <span :style="{ color: row.stock <= 0 ? 'red' : row.stock < 50 ? 'red' : row.stock < 100 ? 'orange' : '' }">
                {{ row.stock || 0 }}
              </span>
              <el-tooltip v-if="row.stock < 50" content="库存预警：库存低于阈值" placement="top">
                <el-tag size="small" type="danger" style="margin-left: 5px;">预警</el-tag>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存操作" width="300" align="center">
          <template #default="scope">
            <div style="display: flex; align-items: center; justify-content: center; gap: 10px;">
              <el-input-number 
                v-model="scope.row.operationQuantity" 
                :min="0" 
                :precision="0" 
                style="width: 120px; flex-shrink: 0;"
              />
              <el-button 
                type="success" 
                size="small" 
                @click="addInventory(scope.row)"
              >
                增加
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="reduceInventory(scope.row)"
                :disabled="scope.row.operationQuantity > scope.row.stock"
              >
                减少
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleStatusChange(row, 1)" :disabled="row.status === 1">上架</el-button>
            <el-button type="warning" size="small" @click="handleStatusChange(row, 0)" :disabled="row.status === 0">下架</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :disabled="row.status !== 0">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
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
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" append-to-body>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="辅料编码" prop="productCode">
          <el-input v-model="form.productCode" :disabled="isEdit" />        </el-form-item>
        <el-form-item label="辅料名称" prop="productName">
          <el-input v-model="form.productName" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="c in categoryList"
              :key="c.id"
              :label="c.categoryName"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="风格">
          <el-select v-model="form.style" placeholder="请选择风格" style="width: 100%" filterable allow-create>
            <el-option label="复古" value="复古" />
            <el-option label="现代" value="现代" />
            <el-option label="简约" value="简约" />
            <el-option label="华丽" value="华丽" />
            <el-option label="休闲" value="休闲" />
            <el-option label="商务" value="商务" />
          </el-select>
        </el-form-item>
        <el-form-item label="具体类型">
          <el-select v-model="form.type" placeholder="请选择具体类型" style="width: 100%" filterable allow-create>
            <el-option label="拉链" value="拉链" />
            <el-option label="纽扣" value="纽扣" />
            <el-option label="线" value="线" />
            <el-option label="衬里" value="衬里" />
            <el-option label="花边" value="花边" />
            <el-option label="标签" value="标签" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.specification" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :precision="2" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预期货期" prop="expectedDeliveryDays">
          <el-input-number v-model="form.expectedDeliveryDays" :min="0" :max="999" style="width: 100%" placeholder="请输入预期货期（天）" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- AI 识别弹窗 -->
    <el-dialog 
      v-model="aiDialogVisible" 
      title="AI 智能辅料识别" 
      width="70%"
      :before-close="handleAIDialogClose"
      destroy-on-close
      append-to-body
      :custom-class="'ai-recognition-dialog'"
    >
      <div style="max-height: 600px; overflow-y: auto; overflow-x: hidden; padding: 0 10px;">
      <!-- 加载中状态 -->
      <div v-if="isRecognizing" style="padding: 40px 0; text-align: center;">
        <el-space direction="vertical" size="large">
          <!-- Element Plus 原生加载组件 -->
          <el-icon class="is-loading" style="font-size: 48px; color: #409EFF;"><LoadingIcon /></el-icon>
          
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
      <div v-else-if="!recognitionResult" class="upload-section">
        <el-card :body-style="{ padding: '30px', textAlign: 'center' }">
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
            :class="'material-uploader'"
          >
            <el-icon class="el-icon--upload" style="font-size: 48px; color: #409EFF;"><UploadFilled /></el-icon>
            <div class="el-upload__text" style="font-size: 16px; margin: 20px 0;">
              拖拽文件到此处或 <em style="color: #409EFF;">点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip" style="margin-top: 20px;">
                <el-text size="small" type="info">
                  📁 支持 JPG、PNG、BMP 格式<br>
                  📏 文件大小不超过 10MB<br>
                  🎯 建议上传清晰的辅料图片以获得最佳识别效果
                </el-text>
              </div>
            </template>
          </el-upload>
          
          <!-- AI 识别按钮 -->
          <div v-if="uploadedFiles.length > 0" style="margin-top: 30px;">
            <el-button 
              type="primary" 
              size="large"
              :icon="Camera"
              @click="startAIRecognition"
              :loading="isRecognizing"
              :disabled="isRecognizing"
              style="width: 200px; height: 50px; font-size: 16px;"
            >
              🔍 开始 AI 识别
            </el-button>
            <el-text size="small" type="info" style="display: block; margin-top: 15px;">
              点击后 AI 将分析图片特征并识别辅料信息，预计需要几秒钟时间
            </el-text>
          </div>
        </el-card>
      </div>
      
      <!-- 识别结果 -->
      <div v-else-if="recognitionResult" class="recognition-result">
        <!-- 识别结果头部 -->
        <el-card :body-style="{ padding: '20px' }" style="margin-bottom: 20px;">
          <el-space direction="vertical" size="small" style="width: 100%;">
            <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
              <el-text type="primary" size="large" style="font-weight: bold;">
                🎯 识别结果
              </el-text>
              <el-space>
                <el-tag :type="getConfidenceLevel(recognitionResult.confidence)" size="large" :effect="'dark'">
                  <el-icon style="margin-right: 5px;"><DataAnalysis /></el-icon>
                  置信度: {{ (recognitionResult.confidence * 100).toFixed(1) }}%
                </el-tag>
                <el-button 
                  size="small" 
                  type="info" 
                  :icon="Refresh" 
                  @click="retryRecognition"
                  :circle="false"
                >
                  重新识别
                </el-button>
              </el-space>
            </div>
          </el-space>
        </el-card>
        
        <!-- 识别图片展示 -->
        <el-card :body-style="{ padding: '20px' }" style="margin-bottom: 20px;">
          <el-text type="info" style="margin-bottom: 15px; display: block;">
            📷 识别图片
          </el-text>
          <div style="display: flex; justify-content: center;">
            <el-image
              v-if="recognitionResult?.image || uploadedFiles[0]?.url"
              :src="recognitionResult?.image || uploadedFiles[0]?.url"
              fit="cover"
              style="width: 240px; height: 240px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);"
            >
              <template #error>
                <div style="width: 240px; height: 240px; border: 2px dashed #d9d9d9; border-radius: 8px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa;">
                  <el-empty description="图片加载失败" :image-size="80" />
                </div>
              </template>
            </el-image>
            <div v-else style="width: 240px; height: 240px; border: 2px dashed #d9d9d9; border-radius: 8px; display: flex; align-items: center; justify-content: center; background-color: #f5f7fa;">
              <el-empty description="无识别图片" :image-size="80" />
            </div>
          </div>
        </el-card>
        
        <!-- 识别结果标签 -->
        <el-card :body-style="{ padding: '20px' }" style="margin-bottom: 20px;">
          <el-text type="info" style="margin-bottom: 15px; display: block;">
            🏷️ 识别标签
          </el-text>
          <el-space wrap :size="15">
            <el-tag size="large" :effect="'dark'" type="primary">{{ recognitionResult.category }}</el-tag>
            <el-tag size="large" :effect="'dark'" type="success" v-if="recognitionResult.type">{{ recognitionResult.type }}</el-tag>
            <el-tag size="large" :effect="'dark'" type="warning" v-if="recognitionResult.material">{{ recognitionResult.material }}</el-tag>
            <el-tag size="large" :effect="'dark'" type="info" v-if="recognitionResult.color">{{ recognitionResult.color }}</el-tag>
            <el-tag size="large" :effect="'dark'" type="danger" v-if="recognitionResult.auxiliaryName">{{ recognitionResult.auxiliaryName }}</el-tag>
          </el-space>
        </el-card>
        
        <!-- 上传额外图片 -->
        <el-card :body-style="{ padding: '20px' }" style="margin-bottom: 20px;">
          <el-text type="info" style="margin-bottom: 15px; display: block;">
            📚 可上传更多图片（最多5张）
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
            :class="'additional-image-uploader'"
          >
            <el-icon style="font-size: 24px; color: #409EFF;"><UploadFilled /></el-icon>
            <div class="el-upload__text" style="font-size: 14px; color: #606266;">
              添加图片
            </div>
            <template #tip>
              <div class="el-upload__tip" style="margin-top: 10px;">
                <el-text size="small" type="info">
                  💡 上传多张图片可提高识别准确性
                </el-text>
              </div>
            </template>
          </el-upload>
        </el-card>
        
        <!-- 识别结果详情 -->
        <el-collapse v-model="activeResultTabs" :class="'result-details-collapse'">
          <el-collapse-item title="📋 详细信息" name="details">
            <el-card :body-style="{ padding: '20px' }">
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
            </el-card>
          </el-collapse-item>
          
          <el-collapse-item title="✏️ 人工校正" name="correction">
            <el-card :body-style="{ padding: '20px' }">
              <el-form :model="correctionForm" label-width="120px" :class="'correction-form'">
                <el-row :gutter="20">
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="校正类别">
                      <el-select v-model="correctionForm.category" placeholder="选择类别" @change="updateAuxiliaryName" style="width: 100%;">
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
                      <el-select v-model="correctionForm.type" placeholder="选择具体类型" filterable allow-create @change="updateAuxiliaryName" style="width: 100%;">
                        <el-option label="拉链" value="拉链" />
                        <el-option label="纽扣" value="纽扣" />
                        <el-option label="线" value="线" />
                        <el-option label="衬里" value="衬里" />
                        <el-option label="花边" value="花边" />
                        <el-option label="标签" value="标签" />
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
                      <el-input v-model="correctionForm.material" placeholder="输入材质，多个关键字用分号间隔" @change="updateAuxiliaryName" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24" :sm="12">
                    <el-form-item label="校正颜色">
                      <el-input v-model="correctionForm.color" placeholder="输入颜色，多个关键字用分号间隔" @change="updateAuxiliaryName" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正辅料名称">
                      <el-input v-model="correctionForm.auxiliaryName" placeholder="输入辅料名称" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正风格">
                      <el-input v-model="correctionForm.style" placeholder="输入风格，多个关键字用分号间隔" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正辅料类别">
                      <el-input v-model="correctionForm.auxiliaryCategory" placeholder="输入辅料类别" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正工艺大类">
                      <el-input v-model="correctionForm.processCategory" placeholder="输入工艺大类，多个关键字用分号间隔" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正材料层">
                      <el-input v-model="correctionForm.materialLayer" placeholder="输入材料层" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正效果层">
                      <el-input v-model="correctionForm.effectLayer" placeholder="输入效果层" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正适用阶段">
                      <el-input v-model="correctionForm.applicationStage" placeholder="输入适用阶段" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                  <el-col :xs="24">
                    <el-form-item label="校正描述">
                      <el-input v-model="correctionForm.description" type="textarea" :rows="4" placeholder="输入详细描述" style="width: 100%;" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item style="margin-top: 20px;">
                  <el-button type="primary" @click="saveCorrection" style="width: 120px;">
                    <el-icon><Check /></el-icon>
                    保存校正结果
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-collapse-item>
        </el-collapse>
        
        <!-- 识别结果操作 -->
        <div style="margin-top: 30px; display: flex; justify-content: flex-end; gap: 15px; padding: 20px; background-color: #f8f9fa; border-radius: 8px;">
          <el-button @click="handleFileRemove" :icon="Close">
            重新上传
          </el-button>
          <el-button type="warning" @click="activeResultTabs = ['correction']" :icon="Edit">
            人工校正
          </el-button>
          <el-button 
            type="success" 
            @click="confirmAddMaterial"
            :icon="Camera"
            size="large"
            :loading="isAddingMaterial"
            :disabled="isAddingMaterial"
          >
            {{ isAddingMaterial ? '添加中...' : '添加到辅料库' }}
          </el-button>
        </div>
      </div>
      </div>
    </el-dialog>
    
    <!-- AI 识别历史 -->
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Camera, Clock, UploadFilled, Loading as LoadingIcon, Refresh, DataAnalysis, Close, Check, Edit } from '@element-plus/icons-vue'
import { getProductList, searchProducts, saveProduct, updateProduct, deleteProduct } from '@/api/product'
import { getCategoryList } from '@/api/category'
import { recognizeMaterial } from '@/api/material'
import { uploadFile, deleteFile, checkFileExists, uploadMultipleFiles } from '@/api/file'
import { aiRequest } from '@/api/material'

import { processImageUrl } from '@/utils/imageProcessor'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增辅料')
const isEdit = ref(false)
const formRef = ref()

// AI Recognition
const aiDialogVisible = ref(false)
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

// AI Recognition History
const aiHistoryVisible = ref(false)
const recognitionHistory = ref([])

// 存储所有辅料名称，用于唯一性验证
const allProductNames = ref([])

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const allData = ref([])

const searchForm = reactive({
  productCode: '',
  productName: '',
  status: ''
})

const form = reactive({
  id: null,
  productCode: '',
  productName: '',
  category: '',
  categoryId: null,
  style: '',
  type: '',
  specification: '',
  unit: '件',
  price: 0,
  expectedDeliveryDays: null,
  description: '',
  status: 1
})

const rules = {
  productCode: [{ required: true, message: '请输入辅料编码', trigger: 'blur' }],
  productName: [
    { required: true, message: '请输入辅料名称', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value) {
          // 检查辅料名称是否已存在
          const currentProduct = tableData.value.find(item => item.id === form.id)
          const currentProductName = currentProduct?.productName
          
          const isDuplicate = allProductNames.value.some(name => 
            name === value && (name !== currentProductName)
          )
          
          if (isDuplicate) {
            callback(new Error('辅料名称已存在，请使用其他名称'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  expectedDeliveryDays: [{ required: true, message: '请输入预期货期', trigger: 'blur' }, { type: 'number', min: 0, message: '预期货期必须大于等于0', trigger: 'blur' }]
}

const categoryList = ref([])
const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryList.value = res.data || []
  } catch (error) {
    // ignore
  }
}

const loadData = async () => {
  try {
    let res
    
    // 构建搜索参数，包括状态
    const searchParams = {
      productCode: searchForm.productCode,
      productName: searchForm.productName
    }
    
    // 只有当status有值时才添加到搜索参数中
    if (searchForm.status !== '') {
      searchParams.status = searchForm.status
    }
    
    if (searchForm.productCode || searchForm.productName || searchForm.status !== '') {
      res = await searchProducts(searchParams)
    } else {
      res = await getProductList()
    }
    
    let data = res.data || []
    
    // 简化数据处理，直接使用数据库返回的字段
    data = data.map(item => {
      // 处理图片URL
      let imageUrl = item.image || item.imageUrl || item.image_url || ''
      if (imageUrl && typeof imageUrl === 'string') {
        // 过滤掉错误的图片URL（如"上传成功"）
        if (imageUrl === '上传成功' || imageUrl === '[]') {
          imageUrl = ''
        } else if (imageUrl.startsWith('http')) {
          // 提取文件名（忽略bucket名称）
          const lastSlashIndex = imageUrl.lastIndexOf('/')
          if (lastSlashIndex !== -1) {
            const filename = imageUrl.substring(lastSlashIndex + 1)
            // 使用后端接口获取图片，避免MinIO认证问题
            imageUrl = '/api/file/get-image?filename=' + filename
          }
        } else if (!imageUrl.startsWith('/api/file/get-image')) {
          // 如果不是http开头也不是/api/file/get-image格式，直接使用文件名
          imageUrl = '/api/file/get-image?filename=' + imageUrl
        }
        // 保留已经是/api/file/get-image格式的URL
      }
      
      return {
        ...item,
        // 确保分类字段存在
        category: item.category || item.categoryName || '',
        // 直接使用数据库返回的type和style字段
        type: item.type || '',
        style: item.style || '',
        // 确保库存数量字段存在，处理不同可能的字段名
        stock: item.stock || item.quantity || 0,
        // 操作数量，用于存储要增减的库存数量，初始化为0
        operationQuantity: 0,
        // 添加处理后的图片URL
        imageUrl: imageUrl
      }
    })
    
    allData.value = data
    total.value = data.length
    currentPage.value = 1
    updatePagination()
    
    // 更新所有辅料名称列表，用于唯一性验证
    allProductNames.value = data.map(item => item.productName).filter(Boolean)
  } catch (error) {
    console.error('加载辅料数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.productCode = ''
  searchForm.productName = ''
  searchForm.status = ''
  loadData()
}

const handleAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增辅料'
    resetForm()
    dialogVisible.value = true
  }

  const handleEdit = (row) => {
    isEdit.value = true
    dialogTitle.value = '编辑辅料'
    Object.assign(form, row)
    if (row.category && categoryList.value.length) {
      const cat = categoryList.value.find(c => c.categoryName === row.category)
      form.categoryId = cat ? cat.id : null
    }
    dialogVisible.value = true
  }

const handleDelete = async (row) => {
  try {
    // 检查状态，只有下架状态才能删除
    if (row.status !== 0) {
      ElMessage.warning('只有下架状态的辅料才能删除')
      return
    }
    
    await ElMessageBox.confirm('确定要删除该辅料吗？', '提示', {
      type: 'warning'
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1:
      return '上架'
    case 2:
      return '待审核'
    case 0:
    default:
      return '下架'
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'info'
    case 0:
    default:
      return 'danger'
  }
}

// 处理状态变更
const handleStatusChange = async (row, newStatus) => {
  try {
    // 当状态为待审核且用户点击上架操作时，检查单价是否大于0
    if (row.status === 2 && newStatus === 1) {
      if (!row.price || row.price <= 0) {
        ElMessage.error('待审核的辅料上架前必须设置非零单价')
        return
      }
    }
    
    const updatedProduct = {
      ...row,
      status: newStatus
    }
    await updateProduct(updatedProduct)
    ElMessage.success(`已${getStatusText(newStatus)}`)
    loadData()
  } catch (error) {
    console.error('状态变更失败:', error)
    ElMessage.error('状态变更失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 当状态为待审核且设置了单价时，自动变更为上架状态
        if (form.status === 2 && form.price > 0) {
          form.status = 1
        }
        
        if (isEdit.value) {
          await updateProduct(form)
          ElMessage.success('更新成功')
        } else {
          await saveProduct(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      }
    }
  })
}

const resetForm = () => {
  form.id = null
  form.productCode = ''
  form.productName = ''
  form.category = ''
  form.categoryId = null
  form.style = ''
  form.type = ''
  form.specification = ''
  form.unit = '件'
  form.price = 0
  form.description = ''
  // 不再设置status字段，状态通过操作按钮控制
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
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  tableData.value = allData.value.slice(start, end)
}

// AI Recognition Methods
const openAIRecognition = () => {
  recognitionResult.value = null
  isRecognizing.value = false
  aiDialogVisible.value = true
}

// 处理文件上传（只预览，不自动识别）
const handleFileChange = (file) => {
  console.log('开始处理文件上传:', file);
  
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

// 实时更新校正辅料名称
const updateAuxiliaryName = () => {
  // 优先使用校正值，如果没有则使用AI识别值
  const type = correctionForm.type || recognitionResult.value?.type
  const category = correctionForm.category || recognitionResult.value?.category
  const material = correctionForm.material || recognitionResult.value?.material
  const color = correctionForm.color || recognitionResult.value?.color
  
  // 按照统一格式构建辅料名称：具体类型--类别--材质--颜色
  const parts = []
  if (type) parts.push(type)
  if (category) parts.push(category)
  if (material) parts.push(material)
  if (color) parts.push(color)
  
  // 如果有部分值，则更新辅料名称
  if (parts.length > 0) {
    correctionForm.auxiliaryName = parts.join('--')
    console.log('实时更新辅料名称:', correctionForm.auxiliaryName)
  }
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
const isAddingMaterial = ref(false)

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
      // 显示加载动画
      isAddingMaterial.value = true
      
      // 检查是否有上传的文件
      if (uploadedFiles.value.length === 0 || !uploadedFiles.value[0].raw) {
        ElMessage.error('请先上传图片')
        isAddingMaterial.value = false
        return
      }
      
      // 检查文件是否已存在
      console.log('检查文件是否已存在');
      const checkRes = await checkFileExists(uploadedFiles.value[0].raw)
      console.log('文件检查返回结果:', checkRes);
      
      if (checkRes.code === 200) {
        if (checkRes.data.exists) {
          ElMessage.warning('该图片已存在于辅料库中，无法重复添加')
          isAddingMaterial.value = false
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
        isAddingMaterial.value = false
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
      // 确保辅料名称格式统一为"具体类型--类别--材质--颜色"
      const buildAuxiliaryName = () => {
        if (correctionForm.auxiliaryName) {
          return correctionForm.auxiliaryName
        }
        const parts = []
        const type = correctionForm.type || recognitionResult.value?.type
        const category = correctionForm.category || recognitionResult.value?.category
        const material = correctionForm.material || recognitionResult.value?.material
        const color = correctionForm.color || recognitionResult.value?.color
        if (type) parts.push(type)
        if (category) parts.push(category)
        if (material) parts.push(material)
        if (color) parts.push(color)
        return parts.join('--')
      }
      
      const auxiliaryName = buildAuxiliaryName()
      
      const materialData = {
        productCode: generateProductCode(), // 自动生成辅料编码
        productName: auxiliaryName,
        category: correctionForm.category || recognitionResult.value.category,
        type: correctionForm.type || recognitionResult.value.type || '',
        material: correctionForm.material || recognitionResult.value.material || '',
        color: correctionForm.color || recognitionResult.value.color || '',
        style: correctionForm.style || recognitionResult.value.style || '',
        specification: '',
        unit: '件',
        price: 0,
        expectedDeliveryDays: 0,
        description: correctionForm.description || auxiliaryName,
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
      
      // 构建临时辅料信息，用于向量化
      const tempMaterialData = {
        ...materialData,
        productCode: generateProductCode()
      }
      
      // 首先尝试进行向量化
      console.log('=== 向量化流程开始 ===');
      console.log('开始向量化操作');
      
      // 检查上传文件是否存在
      console.log('检查上传文件状态:');
      console.log('uploadedFiles.value:', uploadedFiles.value);
      console.log('uploadedFiles.value.length:', uploadedFiles.value.length);
      console.log('第一个文件是否存在:', !!uploadedFiles.value[0]);
      console.log('第一个文件是否有raw属性:', !!uploadedFiles.value[0]?.raw);
      
      if (!uploadedFiles.value || uploadedFiles.value.length === 0 || !uploadedFiles.value[0]?.raw) {
        console.error('向量化失败: 没有可用的上传文件');
        ElMessage.error('向量化失败：无可用文件，无法添加辅料');
        isAddingMaterial.value = false
        return;
      }
      
      let vectorizeSuccess = false;
      let productId = null;
      
      try {
        // 首先保存辅料信息，获取productId用于向量化
        console.log('先保存辅料信息以获取ID');
        const saveResult = await saveProduct(materialData);
        console.log('辅料保存返回结果:', saveResult);
        
        if (saveResult.code === 200 && saveResult.data && saveResult.data.id) {
          productId = saveResult.data.id;
          console.log('辅料保存成功，ID:', productId);
          
          // 调用新的向量化端点
          console.log('调用向量化端点');
          
          // 创建FormData对象
          const vectorizeFormData = new FormData();
          vectorizeFormData.append('productId', productId);
          vectorizeFormData.append('file', uploadedFiles.value[0].raw);
          
          console.log('FormData构建完成:');
          console.log('FormData包含productId:', vectorizeFormData.has('productId'));
          console.log('FormData包含file:', vectorizeFormData.has('file'));
          console.log('文件大小:', uploadedFiles.value[0].raw.size, '字节');
          console.log('文件类型:', uploadedFiles.value[0].raw.type);
          
          // 使用aiRequest实例发送请求，确保正确的baseURL和请求头
          console.log('使用aiRequest发送向量化请求');
          console.log('请求URL:', '/ai/vectorize');
          console.log('请求方法:', 'post');
          console.log('请求参数:', { productId, fileName: uploadedFiles.value[0].name });
          console.log('FormData内容检查:', {
            hasProductId: vectorizeFormData.has('productId'),
            productIdValue: vectorizeFormData.get('productId'),
            hasFile: vectorizeFormData.has('file'),
            fileValue: vectorizeFormData.get('file')
          });
          
          try {
            console.log('开始发送向量化请求...');
            const startTime = Date.now();
            console.log('发送请求前的FormData状态:', {
              productId: vectorizeFormData.get('productId'),
              fileExists: !!vectorizeFormData.get('file')
            });
            const vectorizeData = await aiRequest({
              url: '/ai/vectorize',
              method: 'post',
              data: vectorizeFormData
            });
            const endTime = Date.now();
            console.log('向量化请求完成，耗时:', endTime - startTime, 'ms');
            console.log('向量化返回结果:', vectorizeData);
            console.log('向量化返回结果详细信息:', JSON.stringify(vectorizeData, null, 2));
          
            if (vectorizeData.code === 200) {
              console.log('向量化成功');
              vectorizeSuccess = true;
              ElMessage.success('辅料添加成功，向量化完成');
            } else {
              console.error('向量化失败:', vectorizeData.message);
              console.error('向量化失败详细信息:', JSON.stringify(vectorizeData, null, 2));
              // 向量化失败，需要删除已保存的辅料
              throw new Error('向量化失败: ' + vectorizeData.message);
            }
          } catch (vectorizeError) {
            console.error('向量化过程中发生错误:', vectorizeError);
            console.error('错误详情:', {
              message: vectorizeError.message,
              stack: vectorizeError.stack,
              config: vectorizeError.config,
              response: vectorizeError.response
            });
            
            // 分析错误类型并提供具体的错误信息
            let errorMessage = '向量化失败';
            if (vectorizeError.message) {
              if (vectorizeError.message.includes('Network Error')) {
                errorMessage = '网络连接失败，请检查网络设置';
              } else if (vectorizeError.message.includes('401')) {
                errorMessage = 'API密钥无效，请检查配置';
              } else if (vectorizeError.message.includes('403')) {
                errorMessage = 'API权限不足，请检查权限设置';
              } else if (vectorizeError.message.includes('408')) {
                errorMessage = '请求超时，请检查网络连接';
              } else if (vectorizeError.message.includes('500')) {
                errorMessage = '服务器内部错误，请稍后重试';
              } else if (vectorizeError.message.includes('502')) {
                errorMessage = '网关错误，请稍后重试';
              } else if (vectorizeError.message.includes('503')) {
                errorMessage = '服务不可用，请稍后重试';
              } else if (vectorizeError.message.includes('504')) {
                errorMessage = '网关超时，请稍后重试';
              } else {
                errorMessage = '向量化失败: ' + vectorizeError.message;
              }
            } else {
              errorMessage = '向量化失败: 未知错误';
            }
            
            // 向量化失败，需要删除已保存的辅料
            throw new Error(errorMessage);
          }
        } else {
          throw new Error('辅料保存失败: ' + saveResult.message);
        }
      } catch (vectorizeOuterError) {
        console.error('向量化外层异常:', vectorizeOuterError);
        console.error('外层错误详情:', {
          message: vectorizeOuterError.message,
          stack: vectorizeOuterError.stack
        });
        
        // 向量化失败，返回详细的错误提示
        const errorMessage = vectorizeOuterError.message || '向量化失败：未知错误';
        
        // 如果已经保存了辅料，必须删除，因为向量化失败了
        if (productId) {
          console.log('向量化失败，删除已保存的辅料:', productId);
          try {
            await deleteProduct(productId);
            console.log('已删除保存的辅料:', productId);
          } catch (deleteError) {
            console.error('删除辅料失败:', deleteError);
          }
        }
        
        ElMessage.error('向量化失败：' + errorMessage + '，无法添加辅料');
        isAddingMaterial.value = false
        return;
      }
      
      // 只有向量化成功后，才显示成功提示
      if (vectorizeSuccess) {
        // 显示成功提示
        ElMessage.success('辅料已成功添加到待审核列表，请等待管理员审核')
        
        // 询问管理员是否需要进行AI识别
        if (userStore.user?.role === 'admin') {
          ElMessageBox.confirm(
            '辅料添加成功！是否需要对该辅料进行AI识别分析？',
            'AI识别询问',
            {
              confirmButtonText: '是',
              cancelButtonText: '否',
              type: 'info'
            }
          ).then(() => {
            // 打开AI识别对话框
            aiDialogVisible.value = true
            // 可以在这里预填充相关信息
            console.log('打开AI识别对话框');
          }).catch(() => {
            // 用户取消，不执行任何操作
          })
        }
      }
      
      // 关闭AI识别对话框
      aiDialogVisible.value = false
      
      // 重置相关状态
      recognitionResult.value = null
      uploadedFiles.value = []
      additionalFiles.value = []
      recognitionProgress.value = 0
      isAddingMaterial.value = false
      
      // 刷新辅料列表
      loadData()
    } catch (error) {
      console.error('添加辅料失败:', error)
      ElMessage.error('添加辅料失败，请稍后重试')
      isAddingMaterial.value = false
    }
  }).catch(() => {
    // 用户取消，不执行任何操作
  })
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
      
      // 上传图片以获取持久化的URL
      let persistentImageUrl = file.url // 默认使用临时URL
      try {
        console.log('开始上传图片以获取持久化URL...')
        const uploadRes = await uploadFile(file.raw)
        if (uploadRes.code === 200 && uploadRes.data) {
          persistentImageUrl = uploadRes.data
          console.log('图片上传成功，获取到持久化URL:', persistentImageUrl)
        } else {
          console.warn('图片上传失败，将使用临时URL:', uploadRes)
        }
      } catch (error) {
        console.error('上传图片失败:', error)
        // 继续使用临时URL，不影响识别流程
      }
      
      // 处理图片URL，确保格式正确
      const processedImageUrl = processImageUrl(persistentImageUrl)
      console.log('处理后的图片URL:', processedImageUrl)
      
      // 保存识别结果，包含处理后的图片URL
      const resultWithImage = {
        ...res.data,
        image: processedImageUrl || persistentImageUrl // 使用处理后的URL或原始URL
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

// AI Recognition History
const openAIHistory = () => {
  loadRecognitionHistory()
  aiHistoryVisible.value = true
}

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

// 增加库存数量
const addInventory = async (row) => {
  try {
    if (row.operationQuantity <= 0) {
      ElMessage.warning('请输入有效的增加数量')
      return
    }
    
    // 计算新的库存数量
    const newQuantity = row.stock + Number(row.operationQuantity)
    
    // 调用API更新产品库存
    await updateProduct({
      id: row.id,
      productCode: row.productCode,
      productName: row.productName,
      price: row.price,
      stock: newQuantity,
      status: row.status,
      categoryId: row.categoryId,
      description: row.description,
      images: row.images
    })
    
    ElMessage.success('库存增加成功')
    // 刷新数据以确保一致性
    loadData()
  } catch (error) {
    console.error('增加库存失败:', error)
    ElMessage.error('库存增加失败')
  }
}

// 减少库存数量
const reduceInventory = async (row) => {
  try {
    if (row.operationQuantity <= 0) {
      ElMessage.warning('请输入有效的减少数量')
      return
    }
    
    if (row.operationQuantity > row.stock) {
      ElMessage.warning('减少数量不能大于当前库存数量')
      return
    }
    
    // 计算新的库存数量
    const newQuantity = row.stock - Number(row.operationQuantity)
    
    // 调用API更新产品库存
    await updateProduct({
      id: row.id,
      productCode: row.productCode,
      productName: row.productName,
      price: row.price,
      stock: newQuantity,
      status: row.status,
      categoryId: row.categoryId,
      description: row.description,
      images: row.images
    })
    
    ElMessage.success('库存减少成功')
    // 刷新数据以确保一致性
    loadData()
  } catch (error) {
    console.error('减少库存失败:', error)
    ElMessage.error('库存减少失败')
  }
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.product-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>




