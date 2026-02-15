import request from '@/utils/request'

/**
 * 上传文件
 * @param {File} file - 要上传的文件
 * @returns {Promise} - 返回Promise对象
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除文件
 * @param {string} filename - 文件路径
 * @returns {Promise} - 返回Promise对象
 */
export function deleteFile(filename) {
  return request({
    url: '/file/delete',
    method: 'delete',
    params: {
      filename
    }
  })
}

/**
 * 检查文件是否已存在
 * @param {File} file - 要检查的文件
 * @returns {Promise} - 返回Promise对象
 */
export function checkFileExists(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/file/checkExists',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传多个文件
 * @param {File[]} files - 要上传的文件数组
 * @returns {Promise} - 返回Promise对象
 */
export function uploadMultipleFiles(files) {
  const formData = new FormData()
  files.forEach((file, index) => {
    formData.append('files', file)
  })
  
  return request({
    url: '/file/upload-multiple',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
