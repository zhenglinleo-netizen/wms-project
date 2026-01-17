import request from '@/utils/request'

// 采购需求相关API

// 获取采购需求列表
export const getRequirementList = (params) => {
  return request({
    url: '/requirement/list',
    method: 'get',
    params
  })
}

// 获取采购需求详情
export const getRequirementDetail = (id) => {
  return request({
    url: `/requirement/${id}`,
    method: 'get'
  })
}

// 创建采购需求
export const createRequirement = (data) => {
  return request({
    url: '/requirement',
    method: 'post',
    data
  })
}

// 更新采购需求
export const updateRequirement = (data) => {
  return request({
    url: '/requirement',
    method: 'put',
    data
  })
}

// 删除采购需求
export const deleteRequirement = (id) => {
  return request({
    url: `/requirement/${id}`,
    method: 'delete'
  })
}

// 审核采购需求
export const auditRequirement = (data) => {
  return request({
    url: '/requirement/audit',
    method: 'post',
    data
  })
}

// 提交审核采购需求
export const submitRequirementForAudit = (id) => {
  return request({
    url: `/requirement/submit/${id}`,
    method: 'post'
  })
}

// 确定需求的货期和货款
export const confirmRequirement = (data) => {
  return request({
    url: '/requirement/confirm',
    method: 'post',
    data
  })
}

// 从方案创建采购需求
export const createRequirementFromScheme = (schemeId) => {
  // 确保schemeId是数字类型
  const numericSchemeId = parseInt(schemeId, 10)
  return request({
    url: `/requirement/from-scheme/${numericSchemeId}`,
    method: 'post'
  })
}

// 提交议价
export const submitNegotiation = (data) => {
  return request({
    url: '/requirement/negotiate',
    method: 'post',
    data
  })
}

// 审核议价
export const auditNegotiation = (data) => {
  return request({
    url: '/requirement/negotiate/audit',
    method: 'post',
    data
  })
}
