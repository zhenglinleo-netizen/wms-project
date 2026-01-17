import request from '@/utils/request'

export const getProductList = () => {
  return request({
    url: '/product/list',
    method: 'get'
  })
}

export const searchProducts = (data) => {
  return request({
    url: '/product/search',
    method: 'post',
    data
  })
}

export const saveProduct = (data) => {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

export const updateProduct = (data) => {
  return request({
    url: '/product',
    method: 'put',
    data
  })
}

export const deleteProduct = (id) => {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}




