# WMS系统 - 欢迎消息功能产品需求文档

## Overview
- **Summary**: 实现当用户注册账号并经管理员审核通过后，首次登录系统时，系统自动向该用户的消息中心发送一条欢迎消息的功能。
- **Purpose**: 提升用户体验，让新用户感受到系统的友好性和关怀，增强用户对系统的归属感。
- **Target Users**: 所有新注册并通过审核的系统用户。

## Goals
- 当用户首次登录系统时，自动发送欢迎消息到用户的消息中心
- 确保欢迎消息只发送一次，避免重复发送
- 欢迎消息内容应包含用户姓名和系统使用提示
- 消息发送后，系统应自动更新用户的首次登录状态

## Non-Goals (Out of Scope)
- 不涉及消息中心的其他功能增强
- 不修改现有的用户注册和审核流程
- 不添加新的用户状态或角色

## Background & Context
- 系统已具备完整的用户注册、审核和登录功能
- 系统已实现消息中心功能，支持管理员接收新用户注册通知
- 系统使用RabbitMQ作为消息队列，Spring Boot作为后端框架，Vue.js作为前端框架
- 已在User实体中添加了isFirstLogin字段，用于标识用户是否首次登录

## Functional Requirements
- **FR-1**: 系统应在用户首次登录时检测其登录状态
- **FR-2**: 当检测到用户为首次登录时，系统应自动发送欢迎消息到用户的消息中心
- **FR-3**: 消息发送完成后，系统应将用户的isFirstLogin状态更新为非首次登录
- **FR-4**: 欢迎消息内容应包含用户的真实姓名、系统使用的基本提示和系统使用指南的链接

## Non-Functional Requirements
- **NFR-1**: 消息发送过程应在用户登录流程中同步完成，不影响登录速度
- **NFR-2**: 消息发送应可靠，确保不会丢失或重复发送
- **NFR-3**: 消息内容应清晰、友好，符合系统整体风格

## Constraints
- **Technical**: 基于现有的Spring Boot后端和Vue.js前端架构
- **Dependencies**: 依赖现有的消息中心功能和RabbitMQ消息队列

## Assumptions
- 用户注册和审核流程正常工作
- 消息中心功能正常运行
- User实体中已添加isFirstLogin字段
- 数据库中已添加is_first_login字段

## Acceptance Criteria

### AC-1: 首次登录发送欢迎消息
- **Given**: 用户已注册并通过管理员审核，且isFirstLogin状态为1（首次登录）
- **When**: 用户使用正确的账号密码登录系统
- **Then**: 系统应向用户的消息中心发送一条欢迎消息，内容包含用户真实姓名
- **Verification**: `programmatic`
- **Notes**: 消息应只发送一次

### AC-2: 更新首次登录状态
- **Given**: 用户首次登录并收到欢迎消息
- **When**: 欢迎消息发送成功
- **Then**: 系统应将用户的isFirstLogin状态更新为0（非首次登录）
- **Verification**: `programmatic`

### AC-3: 非首次登录不发送消息
- **Given**: 用户已登录过系统，isFirstLogin状态为0
- **When**: 用户再次登录系统
- **Then**: 系统不应向用户发送欢迎消息
- **Verification**: `programmatic`

## Open Questions
- [ ] 欢迎消息的具体内容需要确认，是否需要包含系统使用指南的链接？
- [ ] 消息的优先级如何设置？