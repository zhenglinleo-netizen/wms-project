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

export const recommendProducts = (id) => {
  return request({
    url: `/product/recommend/${id}`,
    method: 'get'
  })
}

export const collaborativeRecommend = (userId) => {
  return request({
    url: `/product/collaborative-recommend`,
    method: 'get',
    params: { userId }
  })
}

export const recordBehavior = (userId, materialId, behaviorType) => {
  return request({
    url: `/product/record-behavior`,
    method: 'post',
    params: { userId, materialId, behaviorType }
  })
}




