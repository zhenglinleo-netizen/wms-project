# 登录连接错误问题分析 - 产品需求文档

## Overview
- **Summary**: 分析并解决用户无法登录系统的问题，控制台显示网络连接错误（ERR_CONNECTION_REFUSED）
- **Purpose**: 确保用户能够正常登录系统，恢复系统的核心功能
- **Target Users**: 所有系统用户，包括管理员和普通用户

## Goals
- 定位登录失败的根本原因
- 修复网络连接错误问题
- 确保用户能够正常登录系统
- 验证修复后的登录功能

## Non-Goals (Out of Scope)
- 不涉及用户密码重置功能
- 不涉及用户权限管理修改
- 不涉及系统其他功能的修改

## Background & Context
- 前端服务运行在 localhost:8081
- 后端服务配置为运行在 9090 端口
- 控制台显示网络连接被拒绝（ERR_CONNECTION_REFUSED）
- 错误发生在调用登录API时

## Functional Requirements
- **FR-1**: 系统应能正确处理用户登录请求
- **FR-2**: 前端应能正确连接到后端API
- **FR-3**: 登录功能应恢复正常运行

## Non-Functional Requirements
- **NFR-1**: 登录响应时间应在 2 秒内
- **NFR-2**: 错误提示应清晰明确
- **NFR-3**: 系统应具备基本的错误处理机制

## Constraints
- **Technical**: 前端使用Vue.js，后端使用Spring Boot
- **Dependencies**: 前端需要正确配置API代理，后端服务需要正常运行

## Assumptions
- 后端服务应该运行在 9090 端口
- 前端服务运行在 8081 端口
- 数据库连接正常
- 用户凭证（admin/admin123）是正确的

## Acceptance Criteria

### AC-1: 登录功能恢复正常
- **Given**: 后端服务正常运行，前端配置正确
- **When**: 用户输入正确的用户名和密码并点击登录
- **Then**: 系统应成功登录并跳转到主页面
- **Verification**: `programmatic`
- **Notes**: 登录成功后应返回200状态码和JWT令牌

### AC-2: 网络连接配置正确
- **Given**: 前端和后端服务都已启动
- **When**: 检查前端API代理配置
- **Then**: 前端应能正确连接到后端API
- **Verification**: `programmatic`
- **Notes**: 代理配置应指向正确的后端端口

### AC-3: 错误处理机制完善
- **Given**: 后端服务未运行或网络连接失败
- **When**: 用户尝试登录
- **Then**: 系统应显示清晰的错误提示
- **Verification**: `human-judgment`
- **Notes**: 错误信息应便于用户理解

## Open Questions
- [ ] 后端服务是否正在运行？
- [ ] 前端API代理配置是否正确？
- [ ] 后端服务是否运行在正确的端口？
- [ ] 网络防火墙是否阻止了连接？