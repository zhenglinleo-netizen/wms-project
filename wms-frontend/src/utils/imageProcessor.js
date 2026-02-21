// 图片URL处理工具函数

/**
 * 处理单个图片URL的函数
 * @param {string} url - 图片URL
 * @returns {string|null} - 处理后的图片URL或null
 */
export const processImageUrl = (url) => {
  if (!url || typeof url !== 'string') {
    return null
  }
  
  // 过滤掉错误的图片URL（如"上传成功"）
  if (url === '上传成功' || url === '[]') {
    return null
  } else if (url.startsWith('http')) {
    // 提取文件名（忽略bucket名称）
    const lastSlashIndex = url.lastIndexOf('/')
    if (lastSlashIndex !== -1) {
      const filename = url.substring(lastSlashIndex + 1)
      // 使用后端接口获取图片，避免MinIO认证问题
      return `/file/get-image?filename=${filename}`
    } else {
      // 如果无法提取文件名，尝试直接使用整个URL
      return `/file/get-image?filename=${url}`
    }
  } else if (!url.startsWith('/file/get-image')) {
    // 如果不是http开头也不是/file/get-image格式，直接使用文件名
    return `/file/get-image?filename=${url}`
  }
  
  // 保留已经是/file/get-image格式的URL
  return url
}

/**
 * 获取处理后的单个图片URL
 * @param {Object} material - 辅料对象
 * @returns {string} - 处理后的图片URL
 */
export const getProcessedImageUrl = (material) => {
  if (!material) {
    return ''
  }
  
  // 优先处理imageUrl字段（后端返回的字段）
  if (material.imageUrl) {
    const processed = processImageUrl(material.imageUrl)
    if (processed) return processed
  }
  
  // 处理image字段（前端处理后存储的字段）
  if (material.image) {
    const processed = processImageUrl(material.image)
    if (processed) return processed
  }
  
  // 处理images字段（JSON格式的图片数组）
  if (material.images) {
    try {
      // 处理可能的字符串包裹情况
      let imagesStr = material.images
      if (typeof imagesStr === 'string' && imagesStr.startsWith('"') && imagesStr.endsWith('"')) {
        imagesStr = imagesStr.substring(1, imagesStr.length - 1)
      }
      
      const parsedImages = JSON.parse(imagesStr)
      if (Array.isArray(parsedImages) && parsedImages.length > 0) {
        const processed = processImageUrl(parsedImages[0])
        if (processed) return processed
      }
    } catch (e) {
      // 尝试直接将images字段作为单个图片URL处理
      try {
        const processed = processImageUrl(material.images)
        if (processed) return processed
      } catch (e2) {
        // 忽略错误，继续处理其他字段
      }
    }
  }
  
  // 处理material_url字段（可能的字段名）
  if (material.material_url) {
    const processed = processImageUrl(material.material_url)
    if (processed) return processed
  }
  
  // 处理picture字段（可能的字段名）
  if (material.picture) {
    const processed = processImageUrl(material.picture)
    if (processed) return processed
  }
  
  // 所有图片字段处理失败，返回空字符串
  return ''
}

/**
 * 获取图片预览列表
 * @param {Object} material - 辅料对象
 * @returns {Array} - 图片URL列表
 */
export const getImagePreviewList = (material) => {
  if (!material) return []
  
  let images = []
  
  // 优先使用image字段
  if (material.image) {
    const processedUrl = processImageUrl(material.image)
    if (processedUrl) {
      images.push(processedUrl)
    }
  }
  
  // 如果有images字段，解析并合并
  if (material.images) {
    try {
      const parsedImages = JSON.parse(material.images)
      if (Array.isArray(parsedImages) && parsedImages.length > 0) {
        // 处理每个图片URL
        const processedImages = parsedImages.map(url => processImageUrl(url)).filter(Boolean)
        
        // 如果image字段不在images中，添加到列表开头
        if (material.image) {
          const processedImageUrl = processImageUrl(material.image)
          if (processedImageUrl && !processedImages.includes(processedImageUrl)) {
            images = [processedImageUrl, ...processedImages]
          } else {
            images = processedImages
          }
        } else {
          images = processedImages
        }
      }
    } catch (e) {
      // 忽略错误，继续处理其他字段
    }
  }
  
  return images
}
