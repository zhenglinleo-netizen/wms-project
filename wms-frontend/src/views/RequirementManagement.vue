<template>
  <div class="requirement-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>采购需求管理</span>
          <el-button type="primary" :icon="Plus" @click="handleCreateRequirement">创建需求</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="filters" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="需求单号/项目名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="草稿" value="draft" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="待确定" value="confirming" />
            <el-option label="待议价" value="negotiating" />
            <el-option label="已确定" value="confirmed" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="filters.priority" placeholder="全部优先级" clearable style="width: 120px">
            <el-option label="低" value="low" />
            <el-option label="普通" value="normal" />
            <el-option label="高" value="high" />
            <el-option label="紧急" value="urgent" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRequirementList">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 需求列表 -->
      <el-table :data="requirementList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="requirementCode" label="需求单号" min-width="180" />
        <el-table-column prop="projectName" label="所属项目" min-width="180" />
        <el-table-column prop="schemeName" label="所属方案" min-width="180" />
        <el-table-column prop="purpose" label="用途说明" />
        <el-table-column prop="expectedDeliveryDays" label="预期货期" width="150">
          <template #default="{ row }">
            {{ row.expectedDeliveryDays === null || row.expectedDeliveryDays === undefined ? '-' : row.expectedDeliveryDays + '天' }}
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="150">
          <template #default="{ row }">
            {{ formatDate(row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalPayment" label="货款总额" width="120">
          <template #default="{ row }">
            {{ row.totalPayment ? '¥' + row.totalPayment.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">{{ getPriorityLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="600" fixed="right">
          <template #default="{ row }">
            <el-button 
          v-if="userStore.user?.role !== 'admin'" 
          :type="row.status === 'draft' || row.status === 'rejected' ? 'primary' : ''" 
          plain 
          size="small" 
          @click="handleSubmitForAudit(row)"
          :disabled="row.status !== 'draft' && row.status !== 'rejected'"
        >提交审核</el-button>
            <el-button 
          v-if="userStore.user?.role !== 'admin'" 
          type="warning" 
          plain 
          size="small" 
          @click="handleEditRequirement(row)"
          :disabled="row.status !== 'rejected'"
        >编辑</el-button>
            <!-- 管理员需求审核按钮 -->
            <el-button 
              v-if="userStore.user?.role === 'admin'" 
              type="primary" 
              plain 
              size="small" 
              @click="handleRequirementAudit(row)"
              :disabled="row.status !== 'pending'"
            >需求审核</el-button>
            <el-button 
              type="warning" 
              plain 
              size="small" 
              @click="handleNegotiate(row)"
              :disabled="row.status === 'draft' || row.status === 'pending' || row.status === 'rejected' || row.status === 'cancelled' || row.status === 'approved' || row.status === 'confirmed' || row.creatorRole === 'admin' || userStore.user?.role === 'admin'"
            >议价</el-button>
            <!-- 管理员议价审核按钮 -->
            <el-button 
              v-if="userStore.user?.role === 'admin'" 
              type="success" 
              plain 
              size="small" 
              @click="handleNegotiationAudit(row)"
              :disabled="row.status !== 'negotiating_pending'"
            >议价审核</el-button>
            <el-button type="primary" plain size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button type="danger" plain size="small" @click="handleDeleteRequirement(row)" :disabled="row.status !== 'draft' && row.status !== 'pending'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 需求详情弹窗 -->
    <el-dialog v-model="detailVisible" title="采购需求详情" width="800px" append-to-body>
      <div v-if="currentRequirement" class="requirement-detail">
        <el-row :gutter="20">
          <el-col :span="24">
            <div class="detail-header">
              <h3>{{ currentRequirement.requirementCode }}</h3>
              <div class="status-tag">
                <el-tag :type="getStatusType(currentRequirement.status)">{{ getStatusLabel(currentRequirement.status) }}</el-tag>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="所属项目">{{ currentRequirement.projectName }}</el-descriptions-item>
              <el-descriptions-item label="所属方案">{{ currentRequirement.schemeName }}</el-descriptions-item>
              <el-descriptions-item label="用途说明">{{ currentRequirement.purpose }}</el-descriptions-item>
              <el-descriptions-item label="优先级">{{ getPriorityLabel(currentRequirement.priority) }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="创建人">{{ currentRequirement.creatorName }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDateTime(currentRequirement.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="审核人" v-if="currentRequirement.auditorName">{{ currentRequirement.auditorName }}</el-descriptions-item>
              <el-descriptions-item label="审核时间" v-if="currentRequirement.auditTime">{{ formatDateTime(currentRequirement.auditTime) }}</el-descriptions-item>
              <el-descriptions-item label="审核意见" v-if="currentRequirement.auditRemark">{{ currentRequirement.auditRemark }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="24">
            <div style="margin-top: 20px;">
              <h4>需求明细</h4>
              <el-table :data="currentRequirement.items" border style="width: 100%">
                <el-table-column prop="materialName" label="辅料名称" width="200" />
                <el-table-column prop="materialCode" label="辅料编码" width="150" />
                <el-table-column prop="specification" label="规格" width="150" />
                <el-table-column prop="price" label="单价" width="100" />
                <el-table-column prop="quantity" label="数量" width="100" />
                <el-table-column prop="unit" label="单位" width="80" />
                <el-table-column prop="purpose" label="用途" />
              </el-table>
            </div>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 创建需求弹窗 -->
    <el-dialog v-model="createVisible" title="创建采购需求" width="600px" append-to-body>
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="80px">
        <el-form-item label="项目" prop="projectId">
          <el-select v-model="createForm.projectId" placeholder="请选择项目" style="width: 100%">
            <el-option
              v-for="project in projectList"
              :key="project.id"
              :label="project.projectName"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="方案" prop="schemeId">
          <el-select v-model="createForm.schemeId" placeholder="请选择方案" style="width: 100%">
            <el-option
              v-for="scheme in schemeList"
              :key="scheme.id"
              :label="scheme.schemeName"
              :value="scheme.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用途说明" prop="purpose">
          <el-input v-model="createForm.purpose" type="textarea" placeholder="请输入用途说明" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="createForm.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="低" value="low" />
            <el-option label="普通" value="normal" />
            <el-option label="高" value="high" />
            <el-option label="紧急" value="urgent" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitCreate" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 提交审核弹窗 -->
    <el-dialog v-model="confirmVisible" title="提交审核" width="600px" append-to-body>
      <el-form :model="confirmForm" :rules="confirmRules" ref="confirmFormRef" label-width="100px">
        <el-form-item label="预计货期（天数）" prop="expectedDeliveryDays">
          <el-input
            v-model="confirmForm.expectedDeliveryDays"
            type="number"
            placeholder="输入预计货期天数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker
            v-model="confirmForm.deadline"
            type="date"
            placeholder="请选择截止日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="confirmVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitConfirm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 议价弹窗 -->
    <el-dialog v-model="negotiateVisible" title="议价" width="1000px" append-to-body>
      <div v-if="currentNegotiateRequirement" class="negotiate-detail">
        <h3>{{ currentNegotiateRequirement.requirementCode }}</h3>
        <p class="negotiate-status">状态：<el-tag :type="getStatusType(currentNegotiateRequirement.status)">{{ getStatusLabel(currentNegotiateRequirement.status) }}</el-tag></p>
        
        <el-divider content-position="left">辅料议价</el-divider>
        
        <div class="material-negotiation-list">
          <el-card v-for="(item, index) in currentNegotiateRequirement.items" :key="index" class="negotiation-card">
            <template #header>
              <div class="card-header-content">
                <span>{{ item.materialName }}</span>
              </div>
            </template>
            <div class="negotiation-card-body">
              <div class="material-info">
                <div class="info-item">
                  <span class="label">辅料编码：</span>
                  <span class="value">{{ item.materialCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">规格：</span>
                  <span class="value">{{ item.specification || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">原单价：</span>
                  <span class="value">¥{{ item.originalPrice || item.price }}</span>
                </div>
                <div class="info-item">
                  <span class="label">数量：</span>
                  <span class="value">{{ item.quantity }} {{ item.unit }}</span>
                </div>
                <div class="info-item">
                  <span class="label">用途：</span>
                  <span class="value">{{ item.purpose || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">预计货期：</span>
                  <span class="value">{{ item.expectedDeliveryDays === null || item.expectedDeliveryDays === undefined ? '-' : item.expectedDeliveryDays }}天</span>
                </div>
              </div>
              <div class="price-negotiation">
                <div class="price-label">议价单价：</div>
                <el-input-number
                  v-model="item.negotiatedPrice"
                  :precision="2"
                  :step="0.01"
                  :min="0"
                  style="width: 180px"
                  @change="calculateNegotiatedTotal"
                />
              </div>
            </div>
          </el-card>
        </div>
        
        <div class="negotiated-total" style="margin-top: 20px; text-align: right;">
          <span style="font-size: 16px; font-weight: bold;">议价总额：¥{{ negotiatedTotal.toFixed(2) }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="negotiateVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitNegotiation" :loading="submitting">提交议价</el-button>
      </template>
    </el-dialog>
    
    <!-- 议价审核弹窗 -->
    <el-dialog v-model="negotiationAuditVisible" title="议价审核" width="1000px" append-to-body>
      <div v-if="currentNegotiationAudit" class="audit-detail">
        <h3>{{ currentNegotiationAudit.requirementCode }}</h3>
        <p class="audit-status">状态：<el-tag :type="getStatusType(currentNegotiationAudit.status)">{{ getStatusLabel(currentNegotiationAudit.status) }}</el-tag></p>
        
        <el-divider content-position="left">议价信息</el-divider>
        
        <div class="negotiation-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="提交人">{{ currentNegotiationAudit.submittedBy || '-' }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatDateTime(currentNegotiationAudit.submittedAt) }}</el-descriptions-item>
            <el-descriptions-item label="议价总额" :span="2">¥{{ currentNegotiationAudit.negotiatedTotal ? currentNegotiationAudit.negotiatedTotal.toFixed(2) : '0.00' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <el-divider content-position="left">议价明细</el-divider>
        
        <div class="material-negotiation-list">
          <el-card v-for="(item, index) in currentNegotiationAudit.negotiatedItems" :key="index" class="negotiation-card">
            <template #header>
              <div class="card-header-content">
                <span>{{ item.materialName }}</span>
              </div>
            </template>
            <div class="negotiation-card-body">
              <!-- 左侧图片区域 -->
              <div class="material-image-section">
                <el-image
                  :src="item.imageUrl || ''"
                  :preview-src-list="[item.imageUrl || '']"
                  fit="cover"
                  class="material-image-thumbnail"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <div class="image-error-text">无法加载</div>
                    </div>
                  </template>
                  <template #default>
                    <div v-if="!item.imageUrl" class="image-error">
                      <el-icon><Picture /></el-icon>
                      <div class="image-error-text">无法加载</div>
                    </div>
                  </template>
                </el-image>
              </div>
              
              <!-- 右侧文字信息区域 -->
              <div class="material-info-section">
                <div class="info-item">
                  <span class="label">辅料编码：</span>
                  <span class="value">{{ item.materialCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">规格：</span>
                  <span class="value">{{ item.specification || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">原单价：</span>
                  <span class="value">¥{{ item.originalPrice !== null && item.originalPrice !== undefined ? item.originalPrice.toFixed(2) : '0.00' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">议价单价：</span>
                  <span class="value">¥{{ item.negotiatedPrice !== null && item.negotiatedPrice !== undefined ? item.negotiatedPrice.toFixed(2) : '0.00' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">数量：</span>
                  <span class="value">{{ item.quantity }} {{ item.unit }}</span>
                </div>
                <div class="info-item">
                  <span class="label">用途：</span>
                  <span class="value">{{ item.purpose || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">预计货期：</span>
                  <span class="value">{{ item.expectedDeliveryDays === null || item.expectedDeliveryDays === undefined ? '-' : item.expectedDeliveryDays }}天</span>
                </div>
              </div>
              
              <!-- 差价区域 -->
              <div class="price-difference">
                <div class="difference-label">差价：</div>
                <div class="difference-value" :class="(item.difference !== null && item.difference !== undefined && !isNaN(item.difference) && item.difference > 0) ? 'positive' : 'negative'">
                  {{ calculateDifferenceDisplay(item.difference) }}
                </div>
              </div>
            </div>
          </el-card>
        </div>
        
        <el-divider content-position="left">审核操作</el-divider>
        
        <div class="audit-operation">
          <el-form :model="auditForm" :rules="auditRules" ref="auditFormRef" label-width="80px">
            <el-form-item label="审核结果" prop="status" required>
              <el-radio-group v-model="auditForm.status" class="audit-status-radio">
                <el-radio label="approved">通过</el-radio>
                <el-radio label="rejected">驳回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="auditForm.status === 'rejected'" label="驳回原因" prop="rejectionReason" required>
              <el-input
                v-model="auditForm.rejectionReason"
                type="textarea"
                placeholder="请输入驳回原因"
                rows="4"
                style="width: 100%"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer>
        <el-button @click="negotiationAuditVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAudit('negotiation')" :loading="submitting">提交审核</el-button>
      </template>
    </el-dialog>
    
    <!-- 需求审核弹窗 -->
    <el-dialog v-model="requirementAuditVisible" title="需求审核" width="1000px" append-to-body>
      <div v-if="currentRequirementAudit" class="audit-detail">
        <h3>{{ currentRequirementAudit.requirementCode }}</h3>
        <p class="audit-status">状态：<el-tag :type="getStatusType(currentRequirementAudit.status)">{{ getStatusLabel(currentRequirementAudit.status) }}</el-tag></p>
        
        <el-divider content-position="left">需求信息</el-divider>
        
        <div class="negotiation-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="提交人">{{ currentRequirementAudit.creatorName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatDateTime(currentRequirementAudit.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="用途说明" :span="2">{{ currentRequirementAudit.purpose || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预计货期">{{ currentRequirementAudit.expectedDeliveryDays === null || currentRequirementAudit.expectedDeliveryDays === undefined ? '-' : currentRequirementAudit.expectedDeliveryDays }}天</el-descriptions-item>
            <el-descriptions-item label="截止日期">{{ formatDate(currentRequirementAudit.deadline) }}</el-descriptions-item>
            <el-descriptions-item label="货款总额" :span="2">¥{{ currentRequirementAudit.totalPayment ? currentRequirementAudit.totalPayment.toFixed(2) : '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <el-divider content-position="left">需求明细</el-divider>
        
        <div class="material-negotiation-list">
          <el-card v-for="(item, index) in currentRequirementAudit.items" :key="index" class="negotiation-card">
            <template #header>
              <div class="card-header-content">
                <span>{{ item.materialName }}</span>
              </div>
            </template>
            <div class="negotiation-card-body">
              <!-- 左侧图片区域 -->
              <div class="material-image-section">
                <el-image
                  v-if="item.imageUrl"
                  :src="item.imageUrl"
                  :preview-src-list="[item.imageUrl]"
                  fit="cover"
                  class="material-image-thumbnail"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <div class="image-error-text">无法加载</div>
                    </div>
                  </template>
                </el-image>
                <div v-else class="image-error">
                  <el-icon><Picture /></el-icon>
                  <div class="image-error-text">无法加载</div>
                </div>
              </div>
              
              <!-- 右侧文字信息区域 -->
              <div class="material-info-section">
                <div class="info-item">
                  <span class="label">辅料编码：</span>
                  <span class="value">{{ item.materialCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">规格：</span>
                  <span class="value">{{ item.specification || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">单价：</span>
                  <span class="value">¥{{ item.price || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">数量：</span>
                  <span class="value">{{ item.quantity }} {{ item.unit }}</span>
                </div>
                <div class="info-item">
                  <span class="label">用途：</span>
                  <span class="value">{{ item.purpose || '-' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">预计货期：</span>
                  <span class="value">{{ item.expectedDeliveryDays === null || item.expectedDeliveryDays === undefined ? '-' : item.expectedDeliveryDays }}天</span>
                </div>
                <div class="info-item">
                  <span class="label">小计：</span>
                  <span class="value">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        
        <el-divider content-position="left">审核操作</el-divider>
        
        <div class="audit-operation">
          <el-form :model="auditForm" :rules="auditRules" ref="auditFormRef" label-width="80px">
            <el-form-item label="审核结果" prop="status" required>
              <el-radio-group v-model="auditForm.status" class="audit-status-radio">
                <el-radio label="approved">通过</el-radio>
                <el-radio label="rejected">驳回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="auditForm.status === 'rejected'" label="驳回原因" prop="rejectionReason" required>
              <el-input
                v-model="auditForm.rejectionReason"
                type="textarea"
                placeholder="请输入驳回原因"
                rows="4"
                style="width: 100%"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer>
        <el-button @click="requirementAuditVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAudit('requirement')" :loading="submitting">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Picture } from '@element-plus/icons-vue'
import { getRequirementList, getRequirementDetail, deleteRequirement, createRequirement, createRequirementFromScheme, confirmRequirement, submitRequirementForAudit, submitNegotiation, auditNegotiation, auditRequirement } from '@/api/requirement'
import { getProjectList, getSchemeListByProjectId } from '@/api/project'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 获取当前用户信息
const userStore = useUserStore()

// 需求列表
const requirementList = ref([])
const loading = ref(false)
const submitting = ref(false)

// 筛选条件
const filters = reactive({
  keyword: '',
  status: '',
  priority: ''
})

// 分页信息
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 项目列表
const projectList = ref([])
// 方案列表
const schemeList = ref([])

// 详情弹窗
const detailVisible = ref(false)
const currentRequirement = ref(null)

// 创建需求弹窗
const createVisible = ref(false)
const createFormRef = ref()
const createForm = reactive({
  projectId: null,
  schemeId: null,
  purpose: '',
  priority: 'normal'
})

const createRules = {
  projectId: [{ required: true, message: '请选择项目', trigger: 'blur' }],
  schemeId: [{ required: true, message: '请选择方案', trigger: 'blur' }],
  purpose: [{ required: true, message: '请输入用途说明', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'blur' }]
}

// 确认货期弹窗
const confirmVisible = ref(false)
const confirmFormRef = ref()
const confirmForm = reactive({
  id: null,
  expectedDeliveryDays: null,
  deadline: null
})

const confirmRules = {
  expectedDeliveryDays: [{ required: true, message: '请输入预计货期天数', trigger: ['blur', 'change'] },
                        { validator: (rule, value, callback) => {
                            if (value === null || value === undefined || value === '') {
                              callback(new Error('请输入预计货期天数'))
                            } else if (isNaN(value) || value <= 0) {
                              callback(new Error('预计货期天数必须大于0'))
                            } else {
                              callback()
                            }
                          }, trigger: ['blur', 'change'] }],
  deadline: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
}

// 议价弹窗
const negotiateVisible = ref(false)
const currentNegotiateRequirement = ref(null)
const negotiatedTotal = ref(0)

// 议价审核弹窗
const negotiationAuditVisible = ref(false)
const currentNegotiationAudit = ref(null)

// 需求审核弹窗
const requirementAuditVisible = ref(false)
const currentRequirementAudit = ref(null)

// 审核表单
const auditFormRef = ref()
const auditForm = reactive({
  status: '',
  rejectionReason: ''
})

const auditRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  rejectionReason: [{ required: true, message: '请输入驳回原因', trigger: 'blur', validator: (rule, value, callback) => {
    if (auditForm.status === 'rejected' && !value) {
      callback(new Error('请输入驳回原因'))
    } else {
      callback()
    }
  }}]
}

// 处理议价
const handleNegotiate = async (row) => {
  // 检查需求单状态，只有审核通过的需求单才能进行议价
  if (row.status === 'draft') {
    ElMessage.warning('该需求单处于草稿状态，无法进行议价操作，请先提交审核')
    return
  } else if (row.status === 'pending') {
    ElMessage.warning('该需求单处于待审核状态，无法进行议价操作，请等待审核结果')
    return
  } else if (row.status === 'rejected') {
    ElMessage.warning('该需求单审核未通过，无法进行议价操作')
    return
  } else if (row.status === 'cancelled') {
    ElMessage.warning('该需求单已取消，无法进行议价操作')
    return
  }
  
  try {
    const res = await getRequirementDetail(row.id)
    if (res.code === 200) {
      // 深拷贝，避免直接修改原始数据
      currentNegotiateRequirement.value = JSON.parse(JSON.stringify(res.data))
      
      // 为每个辅料添加原单价和议价单价字段
      if (currentNegotiateRequirement.value.items) {
        currentNegotiateRequirement.value.items.forEach(item => {
          item.originalPrice = item.price
          item.negotiatedPrice = item.price
        })
      }
      
      // 计算初始议价总额
      calculateNegotiatedTotal()
      
      negotiateVisible.value = true
    }
  } catch (error) {
    console.error('获取需求详情失败:', error)
    ElMessage.error('获取需求详情失败')
  }
}

// 计算议价总额
const calculateNegotiatedTotal = () => {
  if (!currentNegotiateRequirement.value.items) {
    negotiatedTotal.value = 0
    return
  }
  
  negotiatedTotal.value = currentNegotiateRequirement.value.items.reduce((sum, item) => {
    const price = item.negotiatedPrice || 0
    return sum + (price * item.quantity)
  }, 0)
  
  // 保留两位小数
  negotiatedTotal.value = Math.round(negotiatedTotal.value * 100) / 100
}

// 提交议价
const handleSubmitNegotiation = async () => {
  if (!currentNegotiateRequirement) return
  
  submitting.value = true
  try {
    // 构建议价数据
    const negotiationData = {
      requirementId: currentNegotiateRequirement.value.id,
      negotiatedItems: currentNegotiateRequirement.value.items.map(item => ({
        materialId: item.materialId,
        originalPrice: item.originalPrice,
        negotiatedPrice: item.negotiatedPrice,
        quantity: item.quantity
      })),
      totalNegotiatedAmount: negotiatedTotal.value
    }
    
    // 调用实际的议价API
    const res = await submitNegotiation(negotiationData)
    
    if (res.code === 200) {
      ElMessage.success('议价提交成功，需求单已进入议价审核阶段')
      negotiateVisible.value = false
      loadRequirementList()
    } else {
      ElMessage.error(res.message || '议价提交失败')
    }
  } catch (error) {
    console.error('提交议价失败:', error)
    ElMessage.error('提交议价失败')
  } finally {
    submitting.value = false
  }
}

// 处理议价审核
const handleNegotiationAudit = async (row) => {
  try {
    // 获取议价审核详情
    const res = await getRequirementDetail(row.id)
    if (res.code === 200) {
      // 深拷贝，避免直接修改原始数据
      currentNegotiationAudit.value = JSON.parse(JSON.stringify(res.data))
      
      // 检查议价总额是否为零或null
      const negotiatedTotal = currentNegotiationAudit.value.negotiatedTotal
      if (!negotiatedTotal || negotiatedTotal <= 0) {
        ElMessage.warning('该需求单尚未提交议价审核或议价总额为零')
        return
      }
      
      // 重置审核表单
      auditForm.status = ''
      auditForm.rejectionReason = ''
      
      negotiationAuditVisible.value = true
    }
  } catch (error) {
    console.error('获取需求详情失败:', error)
    ElMessage.error('获取需求详情失败')
  }
}

// 处理需求审核
const handleRequirementAudit = async (row) => {
  try {
    // 获取需求审核详情
    const res = await getRequirementDetail(row.id)
    if (res.code === 200) {
      // 深拷贝，避免直接修改原始数据
      currentRequirementAudit.value = JSON.parse(JSON.stringify(res.data))
      
      // 重置审核表单
      auditForm.status = ''
      auditForm.rejectionReason = ''
      
      requirementAuditVisible.value = true
    }
  } catch (error) {
    console.error('获取需求详情失败:', error)
    ElMessage.error('获取需求详情失败')
  }
}

// 提交审核
const handleSubmitAudit = async (auditType) => {
  if (!auditFormRef.value) return
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        let res
        
        if (auditType === 'requirement') {
          // 需求审核
          try {
            // 构造审核数据 - 直接传递需求ID，后端会处理
            const auditData = {
              auditId: currentRequirementAudit.value.id,
              status: auditForm.status,
              rejectionReason: auditForm.status === 'rejected' ? auditForm.rejectionReason : null
            }
            
            // 调用需求审核API
            res = await auditRequirement(auditData)
            
            if (res.code === 200) {
              ElMessage.success('需求审核成功')
              requirementAuditVisible.value = false
              loadRequirementList()
            } else {
              ElMessage.error(res.message || '需求审核失败')
            }
          } catch (error) {
            console.error('需求审核失败:', error)
            ElMessage.error('需求审核失败')
          }
        } else {
          // 议价审核
          // 构造审核数据
          const auditData = {
            auditId: currentNegotiationAudit.value.negotiationAuditId,
            status: auditForm.status,
            rejectionReason: auditForm.status === 'rejected' ? auditForm.rejectionReason : null
          }
          
          // 调用议价审核API
          res = await auditNegotiation(auditData)
          
          if (res.code === 200) {
            ElMessage.success('议价审核成功')
            negotiationAuditVisible.value = false
          } else {
            ElMessage.error(res.message || '议价审核失败')
          }
        }
        
        if (res && res.code === 200) {
          loadRequirementList()
        }
      } catch (error) {
        console.error('审核失败:', error)
        ElMessage.error('审核失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 加载需求列表
const loadRequirementList = async () => {
  loading.value = true
  try {
    const params = {
      ...filters,
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    
    // 只有非管理员才添加creatorId，管理员查看所有需求单
      if (userStore.user?.role !== 'admin') {
        params.creatorId = userStore.user?.id || 1
      }
    
    const res = await getRequirementList(params)
    if (res.code === 200) {
      requirementList.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      requirementList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取需求列表失败:', error)
    ElMessage.error('获取需求列表失败')
  } finally {
    loading.value = false
  }
}

// 加载项目列表
const loadProjectList = async () => {
  try {
    const res = await getProjectList({ userId: userStore.user?.id || 1 })
    if (res.code === 200) {
      projectList.value = res.data || []
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
  }
}

// 加载方案列表
const loadSchemeList = async (projectId) => {
  if (!projectId) {
    schemeList.value = []
    return
  }
  try {
    const res = await getSchemeListByProjectId(projectId)
    if (res.code === 200) {
      schemeList.value = res.data || []
    }
  } catch (error) {
    console.error('获取方案列表失败:', error)
    ElMessage.error('获取方案列表失败')
  }
}

// 查看需求详情
const handleViewDetail = async (row) => {
  try {
    const res = await getRequirementDetail(row.id)
    if (res.code === 200) {
      currentRequirement.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('获取需求详情失败:', error)
    ElMessage.error('获取需求详情失败')
  }
}

// 创建需求
const handleCreateRequirement = () => {
  // 重置表单
  Object.assign(createForm, {
    projectId: null,
    schemeId: null,
    purpose: '',
    priority: 'normal'
  })
  // 加载项目列表
  loadProjectList()
  // 清空方案列表
  schemeList.value = []
  createVisible.value = true
}

// 提交创建需求
const handleSubmitCreate = async () => {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 从方案创建需求
        const res = await createRequirementFromScheme(createForm.schemeId)
        if (res.code === 200) {
          ElMessage.success('需求创建成功')
          createVisible.value = false
          loadRequirementList()
        }
      } catch (error) {
        console.error('创建需求失败:', error)
        ElMessage.error('创建需求失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除需求
const handleDeleteRequirement = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该采购需求吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteRequirement(row.id)
    if (res.code === 200) {
      ElMessage.success('需求删除成功')
      loadRequirementList()
    }
  } catch (error) {
    console.error('删除需求失败:', error)
    ElMessage.error('删除需求失败')
  }
}

// 优先级类型映射
const getPriorityType = (priority) => {
  const map = {
    'low': 'info',
    'normal': 'primary',
    'high': 'warning',
    'urgent': 'danger'
  }
  return map[priority] || 'info'
}

// 优先级名称映射
const getPriorityLabel = (priority) => {
  const map = {
    'low': '低',
    'normal': '普通',
    'high': '高',
    'urgent': '紧急'
  }
  return map[priority] || '普通'
}

// 状态类型映射
const getStatusType = (status) => {
  const map = {
    'draft': 'info',
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger',
    'cancelled': 'danger',
    'confirming': 'info',
    'negotiating': 'warning',
    'negotiating_pending': 'warning',
    'confirmed': 'success'
  }
  return map[status] || 'info'
}

// 状态名称映射
const getStatusLabel = (status) => {
  const map = {
    'draft': '草稿',
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已驳回',
    'cancelled': '已取消',
    'confirming': '待确定',
    'negotiating': '待议价',
    'negotiating_pending': '议价中',
    'confirmed': '已确定'
  }
  return map[status] || status
}

// 格式化日期时间为24小时制
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  // 转换为Date对象
  const date = new Date(dateTime)
  // 格式化日期时间为24小时制：yyyy-MM-dd HH:mm:ss
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 格式化日期为yyyy-MM-dd
const formatDate = (date) => {
  if (!date) return ''
  const dateObj = new Date(date)
  const year = dateObj.getFullYear()
  const month = String(dateObj.getMonth() + 1).padStart(2, '0')
  const day = String(dateObj.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 计算差价显示
const calculateDifferenceDisplay = (difference) => {
  if (difference === null || difference === undefined || isNaN(difference)) {
    return '¥0.00'
  }
  const sign = difference >= 0 ? '+' : '-'
  const absValue = Math.abs(difference)
  return `${sign}¥${absValue.toFixed(2)}`
}

// 处理确认货期和货款
const handleConfirmRequirement = (row) => {
  // 加载需求明细，计算货款总额
  loadRequirementDetailForConfirm(row.id)
}

// 处理预期货期天数变化，自动计算截止日期（截止日期为货期前3天）
const handleExpectedDeliveryDaysChange = (days) => {
  if (days && days > 0) {
    // 计算预期货期日期
    const expectedDeliveryDate = new Date()
    expectedDeliveryDate.setDate(expectedDeliveryDate.getDate() + days)
    
    // 截止日期为货期前3天
    const deadline = new Date(expectedDeliveryDate)
    deadline.setDate(deadline.getDate() - 3)
    confirmForm.deadline = deadline
  } else {
    confirmForm.deadline = null
  }
}

// 提交确认货期
const handleSubmitConfirm = async () => {
  if (!confirmFormRef.value) return
  await confirmFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 计算预期货期日期
        const expectedDeliveryDate = new Date()
        expectedDeliveryDate.setDate(expectedDeliveryDate.getDate() + confirmForm.expectedDeliveryDays)
        
        // 构造请求数据
        const requestData = {
          id: confirmForm.id,
          expectedDeliveryDate: expectedDeliveryDate,
          expectedDeliveryDays: confirmForm.expectedDeliveryDays,
          deadline: confirmForm.deadline
        }
        
        const res = await confirmRequirement(requestData)
        if (res.code === 200) {
          ElMessage.success('需求提交审核成功')
          confirmVisible.value = false
          loadRequirementList()
        }
      } catch (error) {
        console.error('提交审核失败:', error)
        ElMessage.error('提交审核失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 提交审核需求
const handleSubmitForAudit = async (row) => {
  // 加载需求明细，计算货款总额
  await loadRequirementDetailForConfirm(row.id)
}

// 编辑需求
const handleEditRequirement = async (row) => {
  try {
    const res = await getRequirementDetail(row.id)
    if (res.code === 200) {
      // 重置表单
      Object.assign(confirmForm, {
        id: row.id,
        expectedDeliveryDays: res.data.expectedDeliveryDays || null,
        deadline: res.data.deadline || null
      })
      
      confirmVisible.value = true
    }
  } catch (error) {
    console.error('获取需求明细失败:', error)
    ElMessage.error('获取需求明细失败')
  }
}

// 加载需求明细
const loadRequirementDetailForConfirm = async (id) => {
  try {
    const res = await getRequirementDetail(id)
    if (res.code === 200) {
      // 重置表单
      Object.assign(confirmForm, {
        id: id,
        expectedDeliveryDays: res.data.expectedDeliveryDays || null,
        deadline: res.data.deadline || null
      })
      
      confirmVisible.value = true
    }
  } catch (error) {
    console.error('获取需求明细失败:', error)
    ElMessage.error('获取需求明细失败')
  }
}

// 分页事件处理
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadRequirementList()
}

const handleCurrentChange = (current) => {
  pagination.page = current
  loadRequirementList()
}

// 重置筛选条件
const resetFilters = () => {
  Object.assign(filters, {
    keyword: '',
    status: '',
    priority: ''
  })
  loadRequirementList()
}

// 监听项目变化，加载对应方案
const handleProjectChange = async (value) => {
  createForm.schemeId = null
  await loadSchemeList(value)
}

onMounted(() => {
  loadRequirementList()
})
</script>

<style scoped>
.requirement-management {
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

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status-tag {
  margin-right: 20px;
}

.requirement-detail {
  padding: 10px;
}

/* 议价弹窗样式 */
.negotiate-detail {
  padding: 10px;
}

.negotiate-status {
  margin-bottom: 10px;
}

.material-negotiation-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.negotiation-card {
  margin-bottom: 16px;
}

.card-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.negotiation-card-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.material-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  flex: 1;
  padding: 10px 0;
}

.info-item {
  display: flex;
  align-items: center;
  width: 100%;
}

.info-item .label {
  font-weight: bold;
  margin-right: 8px;
  color: #606266;
  white-space: nowrap;
  min-width: 80px;
}

.info-item .value {
  color: #303133;
  flex: 1;
  word-break: break-word;
}

.price-negotiation {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding-left: 20px;
  border-left: 1px solid #ebeef5;
}

.price-label {
  font-weight: bold;
  color: #606266;
}

.negotiated-total {
  margin-top: 20px;
  text-align: right;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

/* 差价样式 */
.price-difference {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding-left: 20px;
  border-left: 1px solid #ebeef5;
}

.difference-label {
  font-weight: bold;
  color: #606266;
}

.difference-value {
  font-size: 16px;
  font-weight: bold;
}

/* 差价为正（加价）时显示红色 */
.difference-value.positive {
  color: #f56c6c;
}

/* 差价为负（降价）时显示绿色 */
.difference-value.negative {
  color: #67c23a;
}

/* 辅料图片样式 */
.negotiation-card-body {
  display: flex;
  gap: 20px;
  align-items: center;
}

/* 左侧图片区域 */
.material-image-section {
  width: 100px;
  height: 100px;
  flex-shrink: 0;
}

.material-image-thumbnail {
  width: 100%;
  height: 100%;
  cursor: pointer;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.material-image-thumbnail:hover {
  transform: scale(1.05);
  border-color: #409eff;
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  color: #909399;
}

.image-error-text {
  margin-top: 8px;
  font-size: 12px;
}

/* 右侧文字信息区域 */
.material-info-section {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-gap: 12px;
  align-items: center;
}

.info-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  min-width: 0;
}

.info-item .label {
  font-weight: bold;
  margin-right: 8px;
  color: #606266;
  white-space: nowrap;
  min-width: 70px;
}

.info-item .value {
  color: #303133;
  flex: 1;
  word-break: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
