import request from '@/utils/request'

export const getWarehouseList = () => {
  return request({
    url: '/warehouse/list',
    method: 'get'
  })
}

export const saveWarehouse = (data) => {
  return request({
    url: '/warehouse',
    method: 'post',
    data
  })
}

export const updateWarehouse = (data) => {
  return request({
    url: '/warehouse',
    method: 'put',
    data
  })
}

export const deleteWarehouse = (id) => {
  return request({
    url: `/warehouse/${id}`,
    method: 'delete'
  })
}




