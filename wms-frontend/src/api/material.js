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
    
    // 以下是真实API调用代码
    
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
          content: "你是一个专业的辅料识别专家，请根据图片识别辅料并提取信息。识别结果需要包含：类别（面料/辅料/扣件）、具体类型（如拉链、纽扣、线、衬里等）、材质、颜色、风格、辅料类别、辅料名称（格式为：具体类型--材质--颜色）、工艺大类、材料层、效果层、适用阶段、描述（详细描述辅料的特点、用途、适用场景等）。"
        },
        {
          role: "user",
          content: [
            {
              type: "text",
              text: "请识别图片中的辅料并提取以下信息：类别、具体类型、材质、颜色、风格、辅料类别、辅料名称（格式为：具体类型--材质--颜色）、工艺大类、材料层、效果层、适用阶段、详细描述。"
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
      max_tokens: 2048
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
    console.log('请求头:', {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey.substring(0, 20)}...` // 只显示部分API密钥
    });
    console.log('请求体大小:', JSON.stringify(requestBody).length, '字符');
    
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
    console.log('响应头:', Object.fromEntries(response.headers.entries()));
    
    // 5. 处理响应
    const result = await response.json();
    console.log('响应解析完成，结果:', JSON.stringify(result, null, 2));
    
    if (result.choices && result.choices.length > 0) {
      const content = result.choices[0].message.content;
      console.log('模型返回内容:', content);
      console.log('模型返回内容长度:', content.length, '字符');
      
      // 6. 解析返回结果
      // 首先尝试正则表达式匹配（### 字段名格式）
      let category = '未识别';
      let type = '未识别';
      let material = '未识别';
      let color = '未识别';
      let style = '未识别';
      let auxiliaryCategory = '未识别';
      let auxiliaryName = '未识别--未识别--未识别';
      let processCategory = '未识别';
      let materialLayer = '未识别';
      let effectLayer = '未识别';
      let applicationStage = '未识别';
      let description = '未识别';
      
      // 尝试不同格式的正则表达式匹配
      const patterns = [
        // ### 字段名格式（从截图中看到的格式）
        { regex: /###\s*辅料类别\s*\n([\s\S]*?)(?=\n###|$)/, field: 'auxiliaryCategory' },
        { regex: /###\s*辅料名称\s*\n([\s\S]*?)(?=\n###|$)/, field: 'auxiliaryName' },
        { regex: /###\s*工艺大类\s*\n([\s\S]*?)(?=\n###|$)/, field: 'processCategory' },
        { regex: /###\s*材料层\s*\n([\s\S]*?)(?=\n###|$)/, field: 'materialLayer' },
        { regex: /###\s*效果层\s*\n([\s\S]*?)(?=\n###|$)/, field: 'effectLayer' },
        { regex: /###\s*适用阶段\s*\n([\s\S]*?)(?=\n###|$)/, field: 'applicationStage' },
        { regex: /###\s*详细描述\s*\n([\s\S]*?)(?=\n###|$)/, field: 'description' },
        { regex: /###\s*风格\s*\n([\s\S]*?)(?=\n###|$)/, field: 'style' },
        // 中文冒号格式
        { regex: /类别[：:]\s*([^\n]+)/, field: 'category' },
        { regex: /具体类型[：:]\s*([^\n]+)/, field: 'type' },
        { regex: /材质[：:]\s*([^\n]+)/, field: 'material' },
        { regex: /颜色[：:]\s*([^\n]+)/, field: 'color' },
        { regex: /风格[：:]\s*([^\n]+)/, field: 'style' },
        { regex: /辅料类别[：:]\s*([^\n]+)/, field: 'auxiliaryCategory' },
        { regex: /辅料名称[：:]\s*([^\n]+)/, field: 'auxiliaryName' },
        { regex: /工艺大类[：:]\s*([^\n]+)/, field: 'processCategory' },
        { regex: /材料层[：:]\s*([^\n]+)/, field: 'materialLayer' },
        { regex: /效果层[：:]\s*([^\n]+)/, field: 'effectLayer' },
        { regex: /适用阶段[：:]\s*([^\n]+)/, field: 'applicationStage' },
        { regex: /描述[：:]\s*([\s\S]+)/, field: 'description' },
        // 英文格式
        { regex: /Category[：:]\s*([^\n]+)/i, field: 'category' },
        { regex: /Type[：:]\s*([^\n]+)/i, field: 'type' },
        { regex: /Material[：:]\s*([^\n]+)/i, field: 'material' },
        { regex: /Color[：:]\s*([^\n]+)/i, field: 'color' },
        { regex: /Style[：:]\s*([^\n]+)/i, field: 'style' }
      ];
      
      // 执行所有匹配
      patterns.forEach(pattern => {
        const match = content.match(pattern.regex);
        if (match && match[1]) {
          let value = match[1].trim();
          // 移除多余的空白行和空格
          value = value.replace(/\s+/g, ' ').trim();
          
          console.log(`匹配到字段 ${pattern.field}: ${value.substring(0, 50)}...`);
          
          switch (pattern.field) {
            case 'category': category = value; break;
            case 'type': type = value; break;
            case 'material': material = value; break;
            case 'color': color = value; break;
            case 'style': style = value; break;
            case 'auxiliaryCategory': auxiliaryCategory = value; break;
            case 'auxiliaryName': auxiliaryName = value; break;
            case 'processCategory': processCategory = value; break;
            case 'materialLayer': materialLayer = value; break;
            case 'effectLayer': effectLayer = value; break;
            case 'applicationStage': applicationStage = value; break;
            case 'description': description = value; break;
          }
        }
      });
      
      // 输出匹配结果
      console.log('匹配结果:');
      console.log('category:', category);
      console.log('type:', type);
      console.log('material:', material);
      console.log('color:', color);
      console.log('style:', style);
      console.log('auxiliaryCategory:', auxiliaryCategory);
      console.log('auxiliaryName:', auxiliaryName);
      console.log('processCategory:', processCategory);
      console.log('materialLayer:', materialLayer);
      console.log('effectLayer:', effectLayer);
      console.log('applicationStage:', applicationStage);
      console.log('description:', description.substring(0, 100) + '...');
      
      // 直接从辅料名称中提取信息（如果格式正确）
      if (auxiliaryName !== '未识别--未识别--未识别') {
        const parts = auxiliaryName.split('--');
        if (parts.length >= 3) {
          type = parts[0].trim();
          material = parts[1].trim();
          color = parts[2].trim();
          console.log('从辅料名称中提取信息:');
          console.log('type:', type);
          console.log('material:', material);
          console.log('color:', color);
        }
      }
      
      // 强制设置默认值
      if (category === '未识别') category = '辅料';
      if (type === '未识别' && auxiliaryName !== '未识别--未识别--未识别') {
        type = auxiliaryName.split('--')[0] || '未识别';
      }
      if (material === '未识别' && auxiliaryName !== '未识别--未识别--未识别') {
        material = auxiliaryName.split('--')[1] || '未识别';
      }
      if (color === '未识别' && auxiliaryName !== '未识别--未识别--未识别') {
        color = auxiliaryName.split('--')[2] || '未识别';
      }
      
      console.log('强制设置默认值后:');
      console.log('category:', category);
      console.log('type:', type);
      console.log('material:', material);
      console.log('color:', color);
      
      // 如果没有匹配到辅料名称，自动生成
      if (auxiliaryName === '未识别--未识别--未识别') {
        auxiliaryName = `${type}--${material.split('（')[0]}--${color}`;
      }
      
      // 如果没有匹配到描述，自动生成
      if (description === '未识别') {
        description = `这是一款${color}的${type}，采用${material}制成，风格${style}。`;
      }
      
      const recognitionResult = {
        category: category,
        type: type,
        material: material,
        color: color,
        style: style,
        auxiliaryCategory: auxiliaryCategory,
        auxiliaryName: auxiliaryName,
        processCategory: processCategory,
        materialLayer: materialLayer,
        effectLayer: effectLayer,
        applicationStage: applicationStage,
        description: description,
        confidence: 0.95,
        similar: [
          { id: 101, name: '相似面料 A', price: 20, similarity: 0.9 },
          { id: 102, name: '相似面料 B', price: 22, similarity: 0.85 }
        ]
      };
      
      console.log('识别结果解析完成:', recognitionResult);
      
      // 检查是否所有字段都是未识别
      const allUnrecognized = Object.values(recognitionResult).every(value => 
        value === '未识别' || 
        (Array.isArray(value) && value.length === 0)
      );
      
      if (allUnrecognized) {
        console.error('所有字段均未识别，可能是模型返回格式问题');
        console.error('原始返回内容:', content);
        
        // 尝试直接从返回内容中提取信息
        recognitionResult.category = '辅料';
        recognitionResult.type = '未识别';
        recognitionResult.material = '未识别';
        recognitionResult.color = '未识别';
        recognitionResult.auxiliaryName = '未识别--未识别--未识别';
        recognitionResult.description = content;
      }
      
      return {
        code: 200,
        data: recognitionResult
      };
    } else {
      console.error('API返回结果中没有choices字段:', result);
      throw new Error('识别失败：' + (result.error?.message || '未知错误，API返回格式异常'));
    }

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
