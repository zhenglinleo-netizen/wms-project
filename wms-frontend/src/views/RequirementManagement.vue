<template>
  <div class="requirement-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>采购需求管理</span>
          <el-button type="primary" :icon="Plus" @click="handleCreateRequirement" v-show="activeTab === 'requirement'">创建需求</el-button>
        </div>
      </template>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="demo-tabs" @tab-click="handleTabClick">
        <!-- 需求管理标签页 -->
        <el-tab-pane label="需求管理" name="requirement">
          <!-- 搜索栏 -->
          <el-form :inline="true" :model="filters" class="search-form">
            <el-form-item label="关键词">
              <el-input v-model="filters.keyword" placeholder="需求单号/项目名称" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 120px">
                <el-option label="待审核" value="pending" />
                <el-option label="已通过" value="approved" />
                <el-option label="已驳回" value="rejected" />
                <el-option label="已取消" value="cancelled" />
                <el-option label="待确定" value="confirming" />
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
              :type="row.status === 'rejected' ? 'primary' : ''" 
              plain 
              size="small" 
              @click="handleSubmitForAudit(row)"
              :disabled="row.status !== 'rejected'"
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
                <el-button type="primary" plain size="small" @click="handleViewDetail(row)">查看详情</el-button>
                <el-button type="danger" plain size="small" @click="handleDeleteRequirement(row)" :disabled="row.status !== 'pending'">删除</el-button>
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
        </el-tab-pane>

        <!-- 采购管理标签页 -->
        <el-tab-pane label="采购管理" name="purchase">
          <!-- 搜索栏 -->
          <el-form :inline="true" :model="purchaseFilters" class="search-form">
            <el-form-item label="关键词">
              <el-input v-model="purchaseFilters.keyword" placeholder="订单号/需求单号" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="purchaseFilters.status" placeholder="全部状态" clearable style="width: 120px">
                <el-option label="待处理" value="pending" />
                <el-option label="采购中" value="purchasing" />
                <el-option label="已完成" value="completed" />
                <el-option label="已取消" value="cancelled" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadPurchaseList">查询</el-button>
              <el-button @click="resetPurchaseFilters">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 采购订单列表 -->
          <el-table :data="purchaseList" border style="width: 100%" v-loading="purchaseLoading">
            <el-table-column prop="orderCode" label="订单号" min-width="180" />
            <el-table-column prop="requirementCode" label="需求单号" min-width="180" />
            <el-table-column prop="supplierName" label="供应商" min-width="150" />
            <el-table-column prop="totalAmount" label="订单金额" width="120">
              <template #default="{ row }">
                {{ row.totalAmount ? '¥' + row.totalAmount.toFixed(2) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getPurchaseStatusType(row.status)">{{ getPurchaseStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" plain size="small" @click="handleViewPurchaseDetail(row)">查看详情</el-button>
                <el-button v-if="userStore.user?.role !== 'admin'" type="warning" plain size="small" @click="handleUrge(row)">催促采购</el-button>
                <el-button v-if="userStore.user?.role === 'admin'" type="success" plain size="small" @click="handleUpdatePurchaseStatus(row)">更新进度</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination" style="margin-top: 20px; text-align: right;">
            <el-pagination
              v-model:current-page="purchasePagination.page"
              v-model:page-size="purchasePagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="purchasePagination.total"
              @size-change="handlePurchaseSizeChange"
              @current-change="handlePurchaseCurrentChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
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
              <el-descriptions-item label="预计货期">{{ currentRequirement.expectedDeliveryDays === null || currentRequirement.expectedDeliveryDays === undefined ? '-' : currentRequirement.expectedDeliveryDays }}天</el-descriptions-item>
              <el-descriptions-item label="货款总额">¥{{ currentRequirement.totalPayment ? currentRequirement.totalPayment.toFixed(2) : '0.00' }}</el-descriptions-item>
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
                <el-table-column label="当前库存" width="120">
                  <template #default="{ row }">
                    {{ row.stockQuantity || 0 }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="handleInventoryOperation(row, 'add')">增加库存</el-button>
                    <el-button type="warning" size="small" @click="handleInventoryOperation(row, 'reduce')">减少库存</el-button>
                  </template>
                </el-table-column>
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
                  <span class="label">当前库存：</span>
                  <span class="value">{{ item.stockQuantity || 0 }} {{ item.unit }}</span>
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
        <el-button type="primary" @click="handleSubmitAudit()" :loading="submitting">提交审核</el-button>
      </template>
    </el-dialog>

    <!-- 采购订单详情弹窗 -->
    <el-dialog v-model="detailPurchaseVisible" title="采购订单详情" width="1000px" append-to-body>
      <div v-if="currentPurchase" class="purchase-detail">
        <el-row :gutter="20">
          <el-col :span="24">
            <div class="detail-header">
              <h3>{{ currentPurchase.orderCode }}</h3>
              <div class="status-tag">
                <el-tag :type="getPurchaseStatusType(currentPurchase.status)" size="large">{{ getPurchaseStatusLabel(currentPurchase.status) }}</el-tag>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="需求单号">{{ currentPurchase.requirementCode || '-' }}</el-descriptions-item>
              <el-descriptions-item label="供应商">{{ currentPurchase.supplierName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="订单金额">¥{{ currentPurchase.totalAmount ? currentPurchase.totalAmount.toFixed(2) : '0.00' }}</el-descriptions-item>
              <el-descriptions-item label="创建人">{{ currentPurchase.creatorName || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="创建时间">{{ formatDateTime(currentPurchase.createTime) }}</el-descriptions-item>
              <el-descriptions-item label="订单日期">{{ formatDate(currentPurchase.orderDate) }}</el-descriptions-item>
              <el-descriptions-item label="交货日期">{{ formatDate(currentPurchase.deliveryDate) }}</el-descriptions-item>
              <el-descriptions-item label="备注">{{ currentPurchase.remark || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
          
          <el-col :span="24">
            <div style="margin-top: 20px;">
              <h4>采购明细</h4>
              <el-table :data="currentPurchase.items" border style="width: 100%">
                <el-table-column prop="materialName" label="辅料名称" width="150" />
                <el-table-column prop="materialCode" label="辅料编码" width="120" />
                <el-table-column prop="specification" label="规格" width="120" />
                <el-table-column prop="supplierName" label="供应商" min-width="120" />
                <el-table-column prop="quantity" label="数量" width="100" />
                <el-table-column prop="unit" label="单位" width="80" />
                <el-table-column prop="unitPrice" label="单价" width="100">
                  <template #default="{ row }">
                    ¥{{ row.unitPrice ? row.unitPrice.toFixed(2) : '0.00' }}
                  </template>
                </el-table-column>
                <el-table-column prop="totalPrice" label="总价" width="120">
                  <template #default="{ row }">
                    ¥{{ row.totalPrice ? row.totalPrice.toFixed(2) : '0.00' }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="采购状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getPurchaseItemStatusType(row.status)" size="small">{{ getPurchaseItemStatusLabel(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column v-if="userStore.user?.role === 'admin'" label="操作" width="200">
                  <template #default="{ row }">
                    <el-button type="primary" size="small" @click="handleUpdatePurchaseItemStatus(row)" :disabled="!row.supplierId">更新状态</el-button>
                    <el-button type="success" size="small" @click="handleAssignSupplier(row)">分配供应商</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>

          <el-col :span="24">
            <div style="margin-top: 20px;">
              <h4>催促记录</h4>
              <el-table :data="urgeList" border style="width: 100%" v-if="urgeList.length > 0">
                <el-table-column prop="userName" label="催促人" width="100" />
                <el-table-column prop="urgeContent" label="催促内容" />
                <el-table-column prop="urgeTime" label="催促时间" width="180">
                  <template #default="{ row }">
                    {{ formatDateTime(row.urgeTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'pending' ? 'warning' : 'success'" size="small">
                      {{ row.status === 'pending' ? '待响应' : '已响应' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column v-if="userStore.user?.role === 'admin'" label="响应" width="250">
                  <template #default="{ row }">
                    <div v-if="row.status === 'responded'">
                      <div>{{ row.responseContent }}</div>
                      <div style="font-size: 12px; color: #909399;">{{ formatDateTime(row.responseTime) }}</div>
                    </div>
                    <el-button v-else type="primary" size="small" @click="handleRespondUrge(row)">响应</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无催促记录" :image-size="60" />
            </div>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <el-button @click="detailPurchaseVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 采购订单状态更新弹窗 -->
    <el-dialog v-model="purchaseStatusVisible" title="更新采购状态" width="500px" append-to-body>
      <el-form :model="purchaseStatusForm" :rules="purchaseStatusRules" ref="purchaseStatusFormRef" label-width="100px">
        <el-form-item label="采购状态" prop="status">
          <el-select v-model="purchaseStatusForm.status" placeholder="请选择采购状态" style="width: 100%">
            <el-option label="待处理" value="pending" />
            <el-option label="采购中" value="purchasing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="purchaseStatusVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPurchaseStatus" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 采购项目状态更新弹窗 -->
    <el-dialog v-model="purchaseItemStatusVisible" title="更新物料采购状态" width="500px" append-to-body>
      <el-form :model="purchaseItemStatusForm" label-width="120px">
        <el-form-item label="物料名称">
          <span>{{ purchaseItemStatusForm.materialName }}</span>
        </el-form-item>
        <el-form-item label="采购状态" prop="status">
          <el-select v-model="purchaseItemStatusForm.status" placeholder="请选择采购状态" style="width: 100%">
            <el-option label="待采购" value="pending" />
            <el-option label="采购中" value="purchasing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="purchaseItemStatusVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPurchaseItemStatus" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 采购催促弹窗 -->
    <el-dialog v-model="urgeVisible" title="催促采购" width="500px" append-to-body>
      <el-form :model="urgeForm" :rules="urgeRules" ref="urgeFormRef" label-width="100px">
        <el-form-item label="催促内容" prop="urgeContent">
          <el-input
            v-model="urgeForm.urgeContent"
            type="textarea"
            placeholder="请输入催促内容"
            rows="4"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="urgeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitUrge" :loading="submitting">发送催促</el-button>
      </template>
    </el-dialog>

    <!-- 响应催促弹窗 -->
    <el-dialog v-model="respondVisible" title="响应催促" width="500px" append-to-body>
      <el-form :model="respondForm" :rules="respondRules" ref="respondFormRef" label-width="100px">
        <el-form-item label="响应内容" prop="responseContent">
          <el-input
            v-model="respondForm.responseContent"
            type="textarea"
            placeholder="请输入响应内容"
            rows="4"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="respondVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRespond" :loading="submitting">提交响应</el-button>
      </template>
    </el-dialog>

    <!-- 供应商分配弹窗 -->
    <el-dialog v-model="supplierVisible" title="分配供应商" width="600px" append-to-body>
      <el-form :model="supplierForm" :rules="supplierRules" ref="supplierFormRef" label-width="100px">
        <el-form-item label="辅料名称">
          <span>{{ supplierForm.materialName }}</span>
        </el-form-item>
        <el-form-item label="当前供应商">
          <span>{{ supplierForm.currentSupplier || '-' }}</span>
        </el-form-item>
        <el-form-item label="分配供应商" prop="supplierId">
          <el-select v-model="supplierForm.supplierId" placeholder="请选择供应商" style="width: 100%">
            <el-option 
              v-for="supplier in supplierList" 
              :key="supplier.id" 
              :label="supplier.supplierName" 
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="supplierVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitSupplier" :loading="submitting">确定分配</el-button>
      </template>
    </el-dialog>

    <!-- 库存操作弹窗 -->
    <el-dialog v-model="inventoryOperationVisible" :title="inventoryOperationForm.type === 'add' ? '增加库存' : '减少库存'" width="500px" append-to-body>
      <el-form :model="inventoryOperationForm" :rules="inventoryOperationRules" ref="inventoryOperationFormRef" label-width="100px">
        <el-form-item label="辅料名称">
          <span>{{ inventoryOperationForm.materialName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ inventoryOperationForm.currentStock }} {{ inventoryOperationForm.unit }}</span>
        </el-form-item>
        <el-form-item label="操作数量" prop="quantity">
          <el-input
            v-model.number="inventoryOperationForm.quantity"
            type="number"
            placeholder="请输入操作数量"
            min="1"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inventoryOperationVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitInventoryOperation" :loading="submitting">确定操作</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Picture } from '@element-plus/icons-vue'
import { getRequirementList, getRequirementDetail, deleteRequirement, createRequirement, createRequirementFromScheme, confirmRequirement, submitRequirementForAudit, auditRequirement } from '@/api/requirement'
import { getProjectList, getSchemeListByProjectId } from '@/api/project'
import { getPurchaseOrderList, getPurchaseOrderDetail, updatePurchaseOrderStatus, updateOrderItemStatus, updateOrderItemSupplier, getUrgeListByOrderId, createUrge, respondToUrge } from '@/api/purchaseOrder'
import { getSupplierList, getSuppliersByMaterialId } from '@/api/supplier'

import { sendNotice } from '@/api/notice'
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

// 标签页状态
const activeTab = ref('requirement')

// 采购订单相关
const purchaseList = ref([])
const purchaseLoading = ref(false)

const purchaseFilters = reactive({
  keyword: '',
  status: ''
})

const purchasePagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const detailPurchaseVisible = ref(false)
const currentPurchase = ref(null)
const urgeList = ref([])

const purchaseStatusVisible = ref(false)
const purchaseStatusFormRef = ref()
const purchaseStatusForm = reactive({
  id: null,
  status: ''
})
const purchaseStatusRules = {
  status: [{ required: true, message: '请选择采购状态', trigger: 'change' }]
}

const purchaseItemStatusVisible = ref(false)
const purchaseItemStatusForm = reactive({
  id: null,
  materialName: '',
  status: ''
})

const urgeVisible = ref(false)
const urgeFormRef = ref()
const urgeForm = reactive({
  orderId: null,
  urgeContent: ''
})
const urgeRules = {
  urgeContent: [{ required: true, message: '请输入催促内容', trigger: 'blur' }]
}

const respondVisible = ref(false)
const respondFormRef = ref()
const respondForm = reactive({
  urgeId: null,
  responseContent: ''
})
const respondRules = {
  responseContent: [{ required: true, message: '请输入响应内容', trigger: 'blur' }]
}

const supplierVisible = ref(false)
const supplierFormRef = ref()
const supplierList = ref([])
const supplierForm = reactive({
  id: null,
  materialName: '',
  currentSupplier: '',
  supplierId: null
})
const supplierRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }]
}

// 库存操作相关
const inventoryOperationVisible = ref(false)
const inventoryOperationFormRef = ref()
const inventoryOperationForm = reactive({
  id: null,
  materialCode: '',
  materialName: '',
  currentStock: 0,
  quantity: 1,
  unit: '',
  type: 'add' // add 或 reduce
})
const inventoryOperationRules = {
  quantity: [{ required: true, message: '请输入操作数量', trigger: ['blur', 'change'] },
            { validator: (rule, value, callback) => {
                if (value === null || value === undefined || value === '') {
                  callback(new Error('请输入操作数量'))
                } else if (isNaN(value) || value <= 0) {
                  callback(new Error('操作数量必须大于0'))
                } else if (inventoryOperationForm.type === 'reduce' && value > inventoryOperationForm.currentStock) {
                  callback(new Error('减少数量不能大于当前库存数量'))
                } else {
                  callback()
                }
              }, trigger: ['blur', 'change'] }]
}

// 库存预警相关
const inventoryAlertThreshold = 100 // 库存预警阈值
const checkInventoryAlert = async (materialCode, quantity) => {
  if (quantity < inventoryAlertThreshold) {
    // 触发库存预警
    console.log(`库存预警：辅料 ${materialCode} 库存不足，当前库存: ${quantity}，阈值: ${inventoryAlertThreshold}`)
    
    // 发送预警消息给管理员
    try {
      const noticeData = {
        title: '库存预警',
        content: `辅料 ${materialCode} 库存不足，当前库存: ${quantity}，阈值: ${inventoryAlertThreshold}`,
        type: 'system',
        relatedId: null, // 可以关联到具体的辅料ID
        userId: null // 为null时，后端会发送给所有管理员
      }
      
      const res = await sendNotice(noticeData)
      if (res.code === 200) {
        console.log('库存预警消息发送成功')
      }
    } catch (error) {
      console.error('发送库存预警消息失败:', error)
    }
    
    ElMessage.warning(`库存预警：辅料 ${materialCode} 库存不足，当前库存: ${quantity}，阈值: ${inventoryAlertThreshold}`)
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
      
      // 使用产品自身的库存数据
      if (currentRequirementAudit.value.items) {
        currentRequirementAudit.value.items.forEach(item => {
          item.stockQuantity = item.stock || 0
        })
      }
      
      // 计算预计货期（取所有辅料中预计货期的最大值）
      if (currentRequirementAudit.value.items && currentRequirementAudit.value.items.length > 0) {
        const maxDeliveryDays = Math.max(...currentRequirementAudit.value.items.map(item => item.expectedDeliveryDays || 0))
        currentRequirementAudit.value.expectedDeliveryDays = maxDeliveryDays > 0 ? maxDeliveryDays : null
      }
      
      // 计算货款总额（所有辅料小计的总和）
      if (currentRequirementAudit.value.items && currentRequirementAudit.value.items.length > 0) {
        const totalPayment = currentRequirementAudit.value.items.reduce((sum, item) => {
          const itemTotal = (item.price || 0) * (item.quantity || 0)
          return sum + itemTotal
        }, 0)
        currentRequirementAudit.value.totalPayment = totalPayment
      }
      
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
const handleSubmitAudit = async () => {
  if (!auditFormRef.value) return
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 需求审核
        // 构造审核数据 - 直接传递需求ID，后端会处理
        const auditData = {
          auditId: currentRequirementAudit.value.id,
          status: auditForm.status,
          rejectionReason: auditForm.status === 'rejected' ? auditForm.rejectionReason : null
        }
        
        // 调用需求审核API
        const res = await auditRequirement(auditData)
        
        if (res.code === 200) {
          ElMessage.success('需求审核成功')
          requirementAuditVisible.value = false
          loadRequirementList()
        } else {
          ElMessage.error(res.message || '需求审核失败')
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
      
      // 使用产品自身的库存数据
      if (currentRequirement.value.items) {
        currentRequirement.value.items.forEach(item => {
          item.stockQuantity = item.stock || 0
        })
      }
      
      // 计算预计货期（取所有辅料中预计货期的最大值）
      if (currentRequirement.value.items && currentRequirement.value.items.length > 0) {
        const maxDeliveryDays = Math.max(...currentRequirement.value.items.map(item => item.expectedDeliveryDays || 0))
        currentRequirement.value.expectedDeliveryDays = maxDeliveryDays > 0 ? maxDeliveryDays : null
      }
      
      // 计算货款总额（所有辅料小计的总和）
      if (currentRequirement.value.items && currentRequirement.value.items.length > 0) {
        const totalPayment = currentRequirement.value.items.reduce((sum, item) => {
          const itemTotal = (item.price || 0) * (item.quantity || 0)
          return sum + itemTotal
        }, 0)
        currentRequirement.value.totalPayment = totalPayment
      }
      
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
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger',
    'cancelled': 'danger',
    'confirming': 'info',
    'confirmed': 'success'
  }
  return map[status] || 'info'
}

// 状态名称映射
const getStatusLabel = (status) => {
  const map = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已驳回',
    'cancelled': '已取消',
    'confirming': '待确定',
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

// 标签页切换处理
const handleTabClick = (tab) => {
  if (tab.props.name === 'purchase') {
    loadPurchaseList()
  }
}

// 加载采购订单列表
const loadPurchaseList = async () => {
  purchaseLoading.value = true
  try {
    const params = {
      ...purchaseFilters,
      page: purchasePagination.page,
      pageSize: purchasePagination.pageSize
    }
    
    if (userStore.user?.role !== 'admin' && userStore.user?.id) {
      params.creatorId = userStore.user.id
    }
    
    const res = await getPurchaseOrderList(params)
    
    if (res.code === 200) {
      purchaseList.value = res.data.records || []
      purchasePagination.total = res.data.total || 0
    } else {
      purchaseList.value = []
      purchasePagination.total = 0
    }
  } catch (error) {
    console.error('获取采购订单列表失败:', error)
    ElMessage.error('获取采购订单列表失败')
  } finally {
    purchaseLoading.value = false
  }
}

// 查看采购订单详情
const handleViewPurchaseDetail = async (row) => {
  try {
    const res = await getPurchaseOrderDetail(row.id)
    if (res.code === 200) {
      currentPurchase.value = res.data
      
      const urgeRes = await getUrgeListByOrderId(row.id)
      if (urgeRes.code === 200) {
        urgeList.value = urgeRes.data || []
      }
      
      detailPurchaseVisible.value = true
    }
  } catch (error) {
    console.error('获取采购详情失败:', error)
    ElMessage.error('获取采购详情失败')
  }
}

// 更新采购订单状态
const handleUpdatePurchaseStatus = (row) => {
  Object.assign(purchaseStatusForm, {
    id: row.id,
    status: row.status
  })
  purchaseStatusVisible.value = true
}

const handleSubmitPurchaseStatus = async () => {
  if (!purchaseStatusFormRef.value) return
  await purchaseStatusFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await updatePurchaseOrderStatus(purchaseStatusForm.id, purchaseStatusForm.status)
        if (res.code === 200) {
          ElMessage.success('采购状态更新成功')
          purchaseStatusVisible.value = false
          loadPurchaseList()
        }
      } catch (error) {
        console.error('更新采购状态失败:', error)
        ElMessage.error('更新采购状态失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 更新采购项目状态
const handleUpdatePurchaseItemStatus = (row) => {
  if (!row.supplierId) {
    ElMessage.warning('请先分配供应商，再更新采购状态')
    return
  }
  Object.assign(purchaseItemStatusForm, {
    id: row.id,
    materialName: row.materialName,
    status: row.status
  })
  purchaseItemStatusVisible.value = true
}

const handleSubmitPurchaseItemStatus = async () => {
  submitting.value = true
  try {
    const res = await updateOrderItemStatus(purchaseItemStatusForm.id, purchaseItemStatusForm.status)
    if (res.code === 200) {
      ElMessage.success('物料采购状态更新成功')
      purchaseItemStatusVisible.value = false
      handleViewPurchaseDetail(currentPurchase.value)
    }
  } catch (error) {
    console.error('更新物料采购状态失败:', error)
    ElMessage.error('更新物料采购状态失败')
  } finally {
    submitting.value = false
  }
}

// 催促采购
const handleUrge = (row) => {
  Object.assign(urgeForm, {
    orderId: row.id,
    urgeContent: ''
  })
  urgeVisible.value = true
}

const handleSubmitUrge = async () => {
  if (!urgeFormRef.value) return
  await urgeFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await createUrge(urgeForm.orderId, urgeForm.urgeContent)
        if (res.code === 200) {
          ElMessage.success('催促已发送')
          urgeVisible.value = false
        }
      } catch (error) {
        console.error('发送催促失败:', error)
        ElMessage.error('发送催促失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 响应催促
const handleRespondUrge = (row) => {
  Object.assign(respondForm, {
    urgeId: row.id,
    responseContent: ''
  })
  respondVisible.value = true
}

const handleSubmitRespond = async () => {
  if (!respondFormRef.value) return
  await respondFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await respondToUrge(respondForm.urgeId, respondForm.responseContent)
        if (res.code === 200) {
          ElMessage.success('响应已提交')
          respondVisible.value = false
          handleViewPurchaseDetail(currentPurchase.value)
        }
      } catch (error) {
        console.error('提交响应失败:', error)
        ElMessage.error('提交响应失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 加载供应商列表
const loadSuppliers = async (materialId = null) => {
  try {
    if (materialId) {
      // 加载可以提供该辅料的供应商
      const res = await getSuppliersByMaterialId(materialId)
      if (res.code === 200) {
        supplierList.value = res.data || []
      }
    } else {
      // 加载所有供应商
      const res = await getSupplierList()
      if (res.code === 200) {
        supplierList.value = res.data || []
      }
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
    ElMessage.error('获取供应商列表失败')
  }
}

// 分配供应商
const handleAssignSupplier = async (row) => {
  await loadSuppliers(row.materialId)
  Object.assign(supplierForm, {
    id: row.id,
    materialId: row.materialId,
    materialName: row.materialName,
    currentSupplier: row.supplierName || '-',
    supplierId: null
  })
  supplierVisible.value = true
}

const handleSubmitSupplier = async () => {
  if (!supplierFormRef.value) return
  await supplierFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // Find the selected supplier to get the supplier name
        const selectedSupplier = supplierList.value.find(supplier => supplier.id === supplierForm.supplierId)
        if (!selectedSupplier) {
          ElMessage.error('请选择有效的供应商')
          return
        }
        
        const res = await updateOrderItemSupplier(supplierForm.id, supplierForm.supplierId, selectedSupplier.supplierName)
        if (res.code === 200) {
          ElMessage.success('供应商分配成功')
          supplierVisible.value = false
          handleViewPurchaseDetail(currentPurchase.value)
        }
      } catch (error) {
        console.error('分配供应商失败:', error)
        ElMessage.error('分配供应商失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 处理库存操作
const handleInventoryOperation = (row, type) => {
  Object.assign(inventoryOperationForm, {
    id: row.id,
    materialCode: row.materialCode,
    materialName: row.materialName,
    currentStock: row.stockQuantity || 0,
    quantity: 1,
    unit: row.unit,
    type: type
  })
  inventoryOperationVisible.value = true
}

// 提交库存操作
const handleSubmitInventoryOperation = async () => {
  if (!inventoryOperationFormRef.value) return
  await inventoryOperationFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 计算新的库存数量
        let newQuantity
        if (inventoryOperationForm.type === 'add') {
          newQuantity = inventoryOperationForm.currentStock + Number(inventoryOperationForm.quantity)
        } else {
          newQuantity = inventoryOperationForm.currentStock - Number(inventoryOperationForm.quantity)
        }
        
        // 直接更新本地数据，实际库存更新通过辅料管理库进行
        ElMessage.success(inventoryOperationForm.type === 'add' ? '库存增加成功' : '库存减少成功')
        inventoryOperationVisible.value = false
        
        // 更新当前需求详情中的库存数量
        if (currentRequirement.value && currentRequirement.value.items) {
          const item = currentRequirement.value.items.find(i => i.materialCode === inventoryOperationForm.materialCode)
          if (item) {
            item.stockQuantity = newQuantity
          }
        }
        
        // 检查库存预警
        await checkInventoryAlert(inventoryOperationForm.materialCode, newQuantity)
      } catch (error) {
        console.error('库存操作失败:', error)
        ElMessage.error('库存操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 采购订单分页事件处理
const handlePurchaseSizeChange = (size) => {
  purchasePagination.pageSize = size
  loadPurchaseList()
}

const handlePurchaseCurrentChange = (current) => {
  purchasePagination.page = current
  loadPurchaseList()
}

// 重置采购订单筛选条件
const resetPurchaseFilters = () => {
  Object.assign(purchaseFilters, {
    keyword: '',
    status: ''
  })
  loadPurchaseList()
}

// 采购订单状态类型映射
const getPurchaseStatusType = (status) => {
  const map = {
    pending: 'info',
    purchasing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

// 采购订单状态名称映射
const getPurchaseStatusLabel = (status) => {
  const map = {
    pending: '待处理',
    purchasing: '采购中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

// 采购项目状态类型映射
const getPurchaseItemStatusType = (status) => {
  const map = {
    pending: 'info',
    purchasing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

// 采购项目状态名称映射
const getPurchaseItemStatusLabel = (status) => {
  const map = {
    pending: '待采购',
    purchasing: '采购中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
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
