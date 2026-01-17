import request from '@/utils/request'

export function getPurchaseOrderList(params) {
  return request({
    url: '/purchase-order/list',
    method: 'get',
    params
  })
}

export function getPurchaseOrderDetail(id) {
  return request({
    url: `/purchase-order/detail/${id}`,
    method: 'get'
  })
}

export function createPurchaseOrderFromRequirement(requirementId) {
  return request({
    url: `/purchase-order/create-from-requirement/${requirementId}`,
    method: 'post'
  })
}

export function updatePurchaseOrderStatus(id, status) {
  return request({
    url: `/purchase-order/status/${id}`,
    method: 'put',
    data: { status }
  })
}

export function updateOrderItemStatus(itemId, status) {
  return request({
    url: `/purchase-order/item/status/${itemId}`,
    method: 'put',
    data: { status }
  })
}

export function updateOrderItemSupplier(itemId, supplierId, supplierName) {
  return request({
    url: `/purchase-order/item/supplier/${itemId}`,
    method: 'put',
    data: { supplierId, supplierName }
  })
}

export function deletePurchaseOrder(id) {
  return request({
    url: `/purchase-order/delete/${id}`,
    method: 'delete'
  })
}

export function getUrgeListByOrderId(orderId) {
  return request({
    url: `/purchase-urge/list/${orderId}`,
    method: 'get'
  })
}

export function getPendingUrgesByOrderId(orderId) {
  return request({
    url: `/purchase-urge/pending/${orderId}`,
    method: 'get'
  })
}

export function createUrge(orderId, urgeContent) {
  return request({
    url: '/purchase-urge/create',
    method: 'post',
    data: { orderId, urgeContent }
  })
}

export function respondToUrge(urgeId, responseContent) {
  return request({
    url: `/purchase-urge/respond/${urgeId}`,
    method: 'post',
    data: { responseContent }
  })
}

export function deleteUrge(id) {
  return request({
    url: `/purchase-urge/delete/${id}`,
    method: 'delete'
  })
}
