import request from '@/utils/request'

export const getNoticeList = (userId, limit = 20) => {
  return request({
    url: '/notice/list',
    method: 'get',
    params: { userId, limit }
  })
}

export const getUnreadCount = (userId) => {
  return request({
    url: '/notice/unread/count',
    method: 'get',
    params: { userId }
  })
}

export const getLatestNotice = (userId) => {
  return request({
    url: '/notice/latest',
    method: 'get',
    params: { userId }
  })
}

export const markAsRead = (userId, noticeId) => {
  return request({
    url: '/notice/read',
    method: 'post',
    params: { userId, noticeId }
  })
}

export const markAllAsRead = (userId) => {
  return request({
    url: '/notice/read/all',
    method: 'post',
    params: { userId }
  })
}

export const deleteNotice = (id) => {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}

export const sendNotice = (data) => {
  return request({
    url: '/notice/send',
    method: 'post',
    data
  })
}