# WMS系统 - 欢迎消息功能实施计划

## [ ] Task 1: 分析现有登录逻辑
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 找到并分析现有的登录Controller和Service代码
  - 了解登录流程和认证机制
  - 确定在登录成功后添加欢迎消息发送逻辑的最佳位置
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-1.1: 找到登录相关的Controller和Service类
  - `human-judgement` TR-1.2: 确认登录流程的关键节点
- **Notes**: 重点关注登录成功后的处理逻辑

## [ ] Task 2: 实现首次登录检测和消息发送逻辑
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 在登录成功后，检测用户的isFirstLogin状态
  - 如果是首次登录，构建欢迎消息内容
  - 调用消息服务发送欢迎消息
  - 更新用户的isFirstLogin状态为0
- **Acceptance Criteria Addressed**: AC-1, AC-2
- **Test Requirements**:
  - `programmatic` TR-2.1: 首次登录时成功发送欢迎消息
  - `programmatic` TR-2.2: 消息发送后用户isFirstLogin状态更新为0
  - `programmatic` TR-2.3: 非首次登录时不发送消息
- **Notes**: 确保消息发送和状态更新的原子性，避免消息重复发送

## [ ] Task 3: 验证欢迎消息功能
- **Priority**: P1
- **Depends On**: Task 2
- **Description**: 
  - 创建测试用户并设置isFirstLogin为1
  - 登录系统验证欢迎消息是否发送
  - 检查数据库中用户的isFirstLogin状态是否更新
  - 再次登录验证是否不再发送欢迎消息
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-3.1: 首次登录时消息中心收到欢迎消息
  - `programmatic` TR-3.2: 数据库中用户isFirstLogin状态为0
  - `programmatic` TR-3.3: 非首次登录时消息中心无新欢迎消息
- **Notes**: 测试时需要确保RabbitMQ服务正常运行