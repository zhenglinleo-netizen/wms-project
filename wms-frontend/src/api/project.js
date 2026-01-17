import request from '@/utils/request'

// 项目相关API
export const getProjectList = (params) => {
  return request({
    url: '/project/list',
    method: 'get',
    params
  })
}

export const getProjectDetail = (id) => {
  return request({
    url: `/project/${id}`,
    method: 'get'
  })
}

export const createProject = (data) => {
  return request({
    url: '/project',
    method: 'post',
    data
  })
}

export const updateProject = (data) => {
  return request({
    url: '/project',
    method: 'put',
    data
  })
}

export const deleteProject = (id) => {
  return request({
    url: `/project/${id}`,
    method: 'delete'
  })
}

// 方案相关API
export const getSchemeListByProjectId = (projectId) => {
  return request({
    url: `/scheme/list/${projectId}`,
    method: 'get'
  })
}

export const addScheme = (projectId, scheme) => {
  return request({
    url: '/scheme',
    method: 'post',
    data: {
      ...scheme,
      projectId
    }
  })
}

export const updateScheme = (scheme) => {
  return request({
    url: '/scheme',
    method: 'put',
    data: scheme
  })
}

export const deleteScheme = (id) => {
  return request({
    url: `/scheme/${id}`,
    method: 'delete'
  })
}

// 方案明细相关API
export const getSchemeItemListBySchemeId = (schemeId) => {
  return request({
    url: `/scheme-item/list/${schemeId}`,
    method: 'get'
  })
}

export const addSchemeItem = (schemeItem) => {
  return request({
    url: '/scheme-item',
    method: 'post',
    data: schemeItem
  })
}

export const updateSchemeItem = (schemeItem) => {
  return request({
    url: '/scheme-item',
    method: 'put',
    data: schemeItem
  })
}

export const deleteSchemeItem = (id) => {
  return request({
    url: `/scheme-item/${id}`,
    method: 'delete'
  })
}

// 添加辅料到方案
export const addMaterialToScheme = (schemeId, material) => {
  return request({
    url: '/scheme-item',
    method: 'post',
    data: {
      schemeId,
      materialId: material.materialId,
      quantity: material.quantity,
      unit: material.unit,
      purpose: material.purpose || '',
      remark: material.remark || ''
    }
  })
}
