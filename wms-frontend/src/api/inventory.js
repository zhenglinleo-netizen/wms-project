import request from '@/utils/request'

export const getInventoryList = () => {
  return request({
    url: '/inventory/list',
    method: 'get'
  })
}

export const getInventoriesByWarehouse = (warehouseId) => {
  return request({
    url: `/inventory/warehouse/${warehouseId}`,
    method: 'get'
  })
}

export const getInventoriesByProduct = (productId) => {
  return request({
    url: `/inventory/product/${productId}`,
    method: 'get'
  })
}

export const updateInventory = (data) => {
  return request({
    url: '/inventory/update',
    method: 'post',
    data
  })
}




