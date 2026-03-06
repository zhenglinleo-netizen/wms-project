# 智能辅料库自动加载问题 - 实施计划

## [ ] 任务1: 检查页面挂载时的加载逻辑
- **优先级**: P0
- **依赖**: 无
- **描述**:
  - 检查MaterialLibrary.vue文件中的onMounted函数，确保它正确调用了loadMaterials()函数
  - 检查是否有其他代码阻止了数据加载
  - 检查路由监听逻辑是否正确
- **验收标准**: AC-1
- **测试需求**:
  - `human-judgment` TR-1.1: 页面加载时控制台是否显示加载辅料数据的日志
  - `programmatic` TR-1.2: onMounted函数是否正确调用了loadMaterials()
- **注意**: 确保页面挂载时的加载逻辑正确执行

## [ ] 任务2: 检查loadMaterials()函数的实现
- **优先级**: P0
- **依赖**: 任务1
- **描述**:
  - 检查loadMaterials()函数的实现，确保它正确处理API响应
  - 检查数据过滤逻辑，确保只显示status=1的已上架辅料
  - 检查错误处理机制，确保能够捕获和处理API请求失败的情况
- **验收标准**: AC-2, AC-3
- **测试需求**:
  - `programmatic` TR-2.1: loadMaterials()函数是否正确调用了getMaterialList() API
  - `programmatic` TR-2.2: 数据过滤逻辑是否正确，只显示status=1的辅料
  - `human-judgment` TR-2.3: API请求失败时是否显示友好的错误提示
- **注意**: 确保loadMaterials()函数能够正确处理各种情况

## [ ] 任务3: 检查API接口和数据返回
- **优先级**: P0
- **依赖**: 任务2
- **描述**:
  - 检查material.js文件中的getMaterialList()函数，确保它正确调用后端API
  - 检查后端ProductController.java中的getAllProducts()方法，确保它返回正确的数据
  - 检查ProductMapper.xml中的selectAll()查询，确保它返回所有status!=-1的辅料
- **验收标准**: AC-3
- **测试需求**:
  - `programmatic` TR-3.1: 后端API是否返回正确的辅料数据
  - `programmatic` TR-3.2: 数据库查询是否返回status!=-1的辅料
- **注意**: 确保API接口和数据返回正确

## [ ] 任务4: 检查前端数据处理和渲染
- **优先级**: P0
- **依赖**: 任务3
- **描述**:
  - 检查前端如何处理API返回的数据
  - 检查数据是否正确存储在materials和allMaterials变量中
  - 检查分页逻辑是否正确，确保数据能够正确显示
- **验收标准**: AC-1, AC-4
- **测试需求**:
  - `programmatic` TR-4.1: 前端是否正确处理API返回的数据
  - `human-judgment` TR-4.2: 辅料列表是否正确显示数据
  - `human-judgment` TR-4.3: 页面加载速度是否快，不出现明显卡顿
- **注意**: 确保前端数据处理和渲染正确

## [ ] 任务5: 测试功能
- **优先级**: P0
- **依赖**: 任务1, 任务2, 任务3, 任务4
- **描述**:
  - 测试智能辅料库页面是否能够自动加载数据
  - 测试API请求失败时是否显示友好的错误提示
  - 测试数据过滤是否正确，只显示status=1的已上架辅料
  - 测试页面加载性能是否良好
- **验收标准**: AC-1, AC-2, AC-3, AC-4
- **测试需求**:
  - `human-judgment` TR-5.1: 页面是否自动加载数据
  - `human-judgment` TR-5.2: API请求失败时是否显示友好的错误提示
  - `programmatic` TR-5.3: 数据过滤是否正确
  - `human-judgment` TR-5.4: 页面加载性能是否良好
- **注意**: 确保测试覆盖所有场景