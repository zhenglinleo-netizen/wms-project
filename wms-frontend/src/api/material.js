import request from '@/utils/request'
import config from '@/config/api'

// 智谱大模型 AI 识别
export const recognizeMaterial = async (file, timeout = 30000) => {
  // 从配置文件获取智谱大模型 API 配置
  const { apiKey, apiUrl, model } = config.zhipu;
  
  try {
    console.log('开始AI识别，文件信息:', file);
    
    // 检查file参数结构
    if (!file || !file.raw) {
      throw new Error('文件格式错误，缺少原始文件对象');
    }
    
    // 模拟API响应，用于测试
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log('模拟API响应');
        resolve({
          code: 200,
          data: {
            category: '辅料',
            type: '蕾丝花边',
            material: '蕾丝（或具体为棉/聚酯纤维等蕾丝材质，从外观判断为蕾丝类）',
            color: '白色',
            style: '优雅、浪漫、精致（常用于礼服、婚纱等服饰的装饰风格）',
            confidence: 0.95,
            image: 'https://via.placeholder.com/100'
          }
        });
      }, 1000);
    });
    
    // 以下是真实API调用代码，暂时注释掉
    /*
    // 1. 读取文件为 base64 编码
    const base64 = await new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result.split(',')[1]);
      reader.onerror = reject;
      reader.readAsDataURL(file.raw);
    });
    
    console.log('文件读取完成，base64长度:', base64.length);
    
    // 2. 构建请求体
    const requestBody = {
      model: model,
      messages: [
        {
          role: "system",
          content: "你是一个专业的辅料识别专家，请根据图片识别辅料并提取信息。识别结果需要包含：类别（面料/辅料/扣件）、具体类型（如拉链、纽扣、线、衬里等）、材质、颜色、风格。"
        },
        {
          role: "user",
          content: [
            {
              type: "text",
              text: "请识别图片中的辅料并提取类别、具体类型、材质、颜色、风格信息。"
            },
            {
              type: "image_url",
              image_url: {
                url: `data:image/${file.raw.type.split('/')[1]};base64,${base64}`
              }
            }
          ]
        }
      ],
      temperature: 0.7,
      max_tokens: 1024
    };
    
    console.log('请求体构建完成，准备发送请求');
    
    // 3. 创建带超时的 fetch 请求
    const controller = new AbortController();
    const timeoutId = setTimeout(() => {
      console.log('请求超时，正在中止');
      controller.abort();
    }, timeout);
    
    // 4. 发送请求到智谱大模型 API
    console.log('正在发送请求到智谱大模型 API:', apiUrl);
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey}`
      },
      body: JSON.stringify(requestBody),
      signal: controller.signal
    });
    
    clearTimeout(timeoutId);
    console.log('请求发送完成，响应状态:', response.status);
    
    // 5. 处理响应
    const result = await response.json();
    console.log('响应解析完成，结果:', result);
    
    if (result.choices && result.choices.length > 0) {
      const content = result.choices[0].message.content;
      console.log('模型返回内容:', content);
      
      // 6. 解析返回结果（需要根据实际返回格式调整解析逻辑）
      // 这里假设返回格式为："类别：辅料\n具体类型：拉链\n材质：金属\n颜色：银色\n风格：现代"
      const categoryMatch = content.match(/类别：([^\n]+)/);
      const typeMatch = content.match(/具体类型：([^\n]+)/);
      const materialMatch = content.match(/材质：([^\n]+)/);
      const colorMatch = content.match(/颜色：([^\n]+)/);
      const styleMatch = content.match(/风格：([^\n]+)/);
      
      const recognitionResult = {
        category: categoryMatch ? categoryMatch[1] : '未识别',
        type: typeMatch ? typeMatch[1] : '未识别',
        material: materialMatch ? materialMatch[1] : '未识别',
        color: colorMatch ? colorMatch[1] : '未识别',
        style: styleMatch ? styleMatch[1] : '未识别',
        confidence: 0.95, // 智谱 API 可能不返回置信度，这里使用默认值
        similar: [
          { id: 101, name: '相似面料 A', price: 20, similarity: 0.9 },
          { id: 102, name: '相似面料 B', price: 22, similarity: 0.85 }
        ]
      };
      
      console.log('识别结果解析完成:', recognitionResult);
      
      return {
        code: 200,
        data: recognitionResult
      };
    } else {
      throw new Error('识别失败：' + (result.error?.message || '未知错误'));
    }
    */
  } catch (error) {
    console.error('AI 识别错误:', error);
    if (error.name === 'AbortError') {
      throw new Error('识别超时，请稍后重试');
    }
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
