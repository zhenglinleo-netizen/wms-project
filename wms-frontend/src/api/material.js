import request from '@/utils/request'

// 创建一个专门用于AI识别的axios实例，设置更长的超时时间
import axios from 'axios'

const aiRequest = axios.create({
  baseURL: '',
  timeout: 60000, // 60秒超时
})

// 添加请求拦截器
aiRequest.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    console.log('发送AI请求:', {
      url: config.url,
      method: config.method,
      timeout: config.timeout
    });
    return config
  },
  error => {
    console.error('AI请求发送失败:', error);
    return Promise.reject(error)
  }
)

// 添加响应拦截器
aiRequest.interceptors.response.use(
  response => {
    console.log('收到AI响应:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    });
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.error('AI响应错误:', {
      message: error.message,
      code: error.code,
      config: error.config,
      response: error.response
    });
    return Promise.reject(error)
  }
)

export const recognizeMaterial = async (file) => {
  try {
    console.log('开始AI识别，文件信息:', file);
    
    if (!file || !file.raw) {
      throw new Error('文件格式错误，缺少原始文件对象');
    }
    
    const formData = new FormData();
    formData.append('file', file.raw);
    console.log('FormData创建成功，文件大小:', file.raw.size, '字节');
    console.log('FormData中文件字段:', formData.has('file'));
    
    console.log('准备发送请求到后端API...');
    console.log('请求URL:', '/api/ai/recognize');
    console.log('请求方法:', 'post');
    
    // 使用专门的aiRequest实例
    const response = await aiRequest({
      url: '/api/ai/recognize',
      method: 'post',
      data: formData
    });
    
    console.log('后端AI识别结果:', response);
    return response;
  } catch (error) {
    console.error('AI 识别错误:', error);
    console.error('错误详情:', {
      message: error.message,
      code: error.code,
      config: error.config,
      response: error.response
    });
    throw error;
  }
}

// Mock Search by Image
export const searchByImage = (file) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        data: [
          { id: 1, productName: '蓝色纯棉面料', price: 25, category: '面料' },
          { id: 2, productName: '蓝色牛仔布', price: 30, category: '面料' }
        ]
      })
    }, 1500)
  })
}

// Get Materials (wrapping Product API)
export const getMaterialList = (params) => {
  // In a real app, this would call the backend with filters
  // For now we just get all products and filter in frontend or just return all
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}
