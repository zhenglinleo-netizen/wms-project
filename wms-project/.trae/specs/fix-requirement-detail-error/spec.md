# WMS系统 - 修复需求管理查看详情问题 PRD

## 概述
- **摘要**：修复需求管理页面点击查看详情时出现的错误，错误信息为缺少RequirementItem类中negotiatedPrice属性的setter方法。
- **目的**：确保用户能够正常查看需求详情，提高系统的稳定性和用户体验。
- **目标用户**：系统用户，特别是需要查看需求详情的用户。

## 目标
- 修复需求管理页面点击查看详情时的错误
- 确保RequirementItem实体类包含negotiatedPrice属性
- 确保后端服务能够正常构建和运行
- 确保需求详情能够正常显示

## 非目标（范围外）
- 不修改其他功能
- 不改变系统的其他行为
- 不添加新的功能

## 背景与上下文
- 当前需求管理页面点击查看详情时出现错误，错误信息为"Could not set property 'negotiatedPrice' of 'class com.example.wms.entity.RequirementItem' with value '1.86' Cause: There is no setter for property named 'negotiatedPrice' in 'class com.example.wms.entity.RequirementItem'"
- 经过分析，发现RequirementItem实体类中缺少negotiatedPrice属性，而在RequirementItemMapper.xml中却映射了这个字段

## 功能需求
- **FR-1**：在RequirementItem实体类中添加negotiatedPrice属性
- **FR-2**：确保后端服务能够正常构建和运行
- **FR-3**：确保需求管理页面点击查看详情不再报错
- **FR-4**：确保需求详情能够正常显示

## 非功能需求
- **NFR-1**：修复后系统运行稳定
- **NFR-2**：修复过程不影响其他功能
- **NFR-3**：代码修改简洁明了

## 约束
- **技术**：基于现有的Java + Spring Boot技术栈
- **依赖**：依赖现有的MyBatis框架

## 假设
- 只需要在RequirementItem实体类中添加negotiatedPrice属性即可修复问题
- 不需要修改其他文件

## 验收标准

### AC-1：RequirementItem实体类添加negotiatedPrice属性
- **给定**：RequirementItem实体类
- **When**：检查实体类代码
- **Then**：实体类中包含negotiatedPrice属性
- **Verification**：`human-judgment`

### AC-2：后端服务构建成功
- **Given**：后端项目
- **When**：运行构建命令
- **Then**：构建成功，无错误
- **Verification**：`programmatic`

### AC-3：后端服务运行成功
- **Given**：后端项目
- **When**：启动后端服务
- **Then**：服务成功启动，无错误
- **Verification**：`programmatic`

### AC-4：需求管理页面查看详情不再报错
- **Given**：需求管理页面
- **When**：点击查看详情
- **Then**：不再出现错误，显示需求详情
- **Verification**：`human-judgment`

## Open Questions
- [ ] 无