import request from '@/utils/request'

// 供应商管理相关API

// 获取供应商列表
export const getSupplierList = () => {
  return request({
    url: '/supplier/list',
    method: 'get'
  })
}

// 获取供应商详情
export const getSupplierDetail = (id) => {
  return request({
    url: `/supplier/${id}`,
    method: 'get'
  })
}

// 创建供应商
export const createSupplier = (data) => {
  return request({
    url: '/supplier',
    method: 'post',
    data
  })
}

// 更新供应商
export const updateSupplier = (data) => {
  return request({
    url: '/supplier',
    method: 'put',
    data
  })
}

// 删除供应商
export const deleteSupplier = (id) => {
  return request({
    url: `/supplier/${id}`,
    method: 'delete'
  })
}

// 供应商-辅料关系管理相关API

// 获取供应商列表（用于下拉选择）
export const getSuppliers = () => {
  return request({
    url: '/supplier/list',
    method: 'get'
  })
}

// 获取辅料列表（用于下拉选择）
export const getMaterials = () => {
  return request({
    url: '/product/list',
    method: 'get'
  })
}

// 获取供应商-辅料关系列表
export const getSupplierProducts = () => {
  return request({
    url: '/supplier-product/list',
    method: 'get'
  })
}

// 创建供应商-辅料关系
export const createSupplierProduct = (data) => {
  return request({
    url: '/supplier-product',
    method: 'post',
    data
  })
}

// 更新供应商-辅料关系
export const updateSupplierProduct = (data) => {
  return request({
    url: '/supplier-product',
    method: 'put',
    data
  })
}

// 删除供应商-辅料关系
export const deleteSupplierProduct = (id) => {
  return request({
    url: `/supplier-product/${id}`,
    method: 'delete'
  })
}

// 根据辅料ID获取可以提供该辅料的供应商列表
export const getSuppliersByMaterialId = (materialId) => {
  return request({
    url: `/supplier-product/suppliers/${materialId}`,
    method: 'get'
  })
}