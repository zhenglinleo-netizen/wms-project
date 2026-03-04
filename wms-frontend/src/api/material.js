import request from '@/utils/request'
import axios from 'axios'

// 创建一个专门用于AI识别的axios实例，设置更长的超时时间
export const aiRequest = axios.create({
  baseURL: '/api',
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
    console.log('完整的响应数据:', JSON.stringify(response.data, null, 2));
    const res = response.data;
    
    // 检查响应数据的结构
    console.log('响应数据结构检查:', {
      hasCode: 'code' in res,
      codeValue: res.code,
      hasMessage: 'message' in res,
      messageValue: res.message,
      hasData: 'data' in res,
      dataValue: res.data
    });
    
    if (res.code === 200) {
      console.log('响应成功，返回数据:', res);
      return res;
    } else {
      const errorMessage = res.message || '请求失败';
      console.error('AI响应错误，状态码:', res.code, '错误信息:', errorMessage);
      console.error('完整的错误响应:', JSON.stringify(res, null, 2));
      return Promise.reject(new Error(errorMessage));
    }
  },
  error => {
    console.error('AI响应错误:', {
      message: error.message,
      code: error.code,
      config: error.config,
      response: error.response
    });
    console.error('完整的错误对象:', JSON.stringify(error, null, 2));
    
    // 处理网络错误和其他异常
    let errorMessage = '网络请求失败';
    if (error.message) {
      errorMessage = error.message;
    }
    return Promise.reject(new Error(errorMessage));
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
    console.log('请求URL:', '/ai/recognize');
    console.log('请求方法:', 'post');
    
    // 使用专门的aiRequest实例
    const response = await aiRequest({
      url: '/ai/recognize',
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

// Search by Image - 真实的 API 调用
export const searchByImage = (file, limit = 10, threshold = 0.6) => {
  try {
    console.log('准备搜索图片，文件信息:', file);
    console.log('limit:', limit, 'threshold:', threshold);
    
    const formData = new FormData()
    formData.append('file', file.raw || file)
    formData.append('limit', limit)
    formData.append('threshold', threshold)
    
    console.log('FormData构建完成，文件字段:', formData.has('file'));
    console.log('FormData limit:', formData.get('limit'));
    console.log('FormData threshold:', formData.get('threshold'));
    
    return request({
      url: '/product/search-by-image',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  } catch (error) {
    console.error('searchByImage函数错误:', error);
    throw error;
  }
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
