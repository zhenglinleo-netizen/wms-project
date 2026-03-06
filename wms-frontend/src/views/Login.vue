<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="grid-pattern"></div>
    </div>
    
    <div class="login-wrapper">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section" :class="{ 'active': activeTab === 'login' }">
        <div class="welcome-content">
          <h1 v-if="activeTab === 'login'">你好，欢迎！</h1>
          <h1 v-else>创建账户</h1>
          <p v-if="activeTab === 'login'">还没有账户？</p>
          <p v-else>已有账户？</p>
          <el-button 
            type="primary" 
            plain 
            @click="activeTab = activeTab === 'login' ? 'register' : 'login' " 
            class="tab-btn"
            :class="{ 'register-btn': activeTab === 'login', 'login-btn': activeTab === 'register' }"
          >
            {{ activeTab === 'login' ? '注册' : '登录' }}
          </el-button>
          <div class="welcome-features">
            <div class="feature-item">
              <el-icon class="feature-icon"><Lock /></el-icon>
              <span>安全访问</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Clock /></el-icon>
              <span>24/7 支持</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Lock /></el-icon>
              <span>数据保护</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧表单区域 -->
      <div class="form-section">
        <div class="form-content">
          <!-- 登录表单 -->
          <div v-if="activeTab === 'login'" class="form-panel">
            <h2 class="form-title">登录</h2>
            <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
              <!-- 账号输入 -->
              <el-form-item prop="account">
                <el-input
                  v-model="loginForm.account"
                  placeholder="手机号或邮箱"
                  :prefix-icon="User"
                  class="form-input"
                />
              </el-form-item>
              

              
              <!-- 密码输入 -->
              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  :type="loginPasswordVisible ? 'text' : 'password'"
                  placeholder="密码"
                  :prefix-icon="Lock"
                  class="form-input"
                >
                  <template #suffix>
                    <el-icon @click="loginPasswordVisible = !loginPasswordVisible" style="cursor: pointer">
                      <View v-if="loginPasswordVisible" />
                      <Hide v-else />
                    </el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <!-- 忘记密码 -->
              <div class="form-options">
                <el-link type="primary" :underline="false" @click="showForgotPassword" class="forgot-link">
                  忘记密码？
                </el-link>
              </div>
              
              <!-- 登录按钮 -->
              <el-form-item>
                <el-button type="primary" @click="handleLogin" :loading="loading" class="submit-btn">
                  <span v-if="!loading">登录</span>
                  <span v-else>登录中...</span>
                </el-button>
              </el-form-item>
              
              <!-- 社交登录 -->
              <div class="social-login">
                <div style="text-align: center; margin-bottom: 24px;">
                  <p class="social-text">或通过社交平台登录</p>
                </div>
                <div class="social-buttons">
                  <el-button circle class="social-btn" :class="{ 'google-btn': true }">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button circle class="social-btn" :class="{ 'facebook-btn': true }">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button circle class="social-btn" :class="{ 'twitter-btn': true }">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button circle class="social-btn" :class="{ 'github-btn': true }">
                    <el-icon><Link /></el-icon>
                  </el-button>
                </div>
              </div>
              

            </el-form>
          </div>
          
          <!-- 注册表单 -->
          <div v-else class="form-panel">
            <h2 class="form-title">注册</h2>
            <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="register-form">
              <!-- 用户名输入 -->
              <el-form-item prop="username">
                <el-input v-model="registerForm.username" placeholder="用户名" :prefix-icon="User" class="form-input" />
              </el-form-item>
              
              <!-- 密码输入 -->
              <el-form-item prop="password">
                <el-input 
                  v-model="registerForm.password" 
                  :type="registerPasswordVisible ? 'text' : 'password'"
                  placeholder="密码" 
                  :prefix-icon="Lock" 
                  :suffix-icon="registerPasswordVisible ? View : Hide"
                  @click:suffix="registerPasswordVisible = !registerPasswordVisible"
                  class="form-input" 
                />
              </el-form-item>
              
              <!-- 真实姓名输入 -->
              <el-form-item prop="realName">
                <el-input v-model="registerForm.realName" placeholder="真实姓名" :prefix-icon="Postcard" class="form-input" />
              </el-form-item>
              
              <!-- 手机号输入 -->
              <el-form-item prop="phone">
                <el-input v-model="registerForm.phone" placeholder="手机号" :prefix-icon="Iphone" class="form-input" />
              </el-form-item>
              
              <!-- 邮箱输入 -->
              <el-form-item prop="email">
                <el-input v-model="registerForm.email" placeholder="邮箱" :prefix-icon="Message" class="form-input" />
              </el-form-item>
              
              <!-- 公司输入 -->
              <el-form-item prop="company">
                <el-input v-model="registerForm.company" placeholder="公司" :prefix-icon="OfficeBuilding" class="form-input" />
              </el-form-item>
              
              <!-- 角色选择 -->
              <el-form-item prop="role">
                <el-select v-model="registerForm.role" placeholder="选择角色" class="form-select">
                  <el-option label="管理员" value="admin" />
                  <el-option label="设计师" value="designer" />
                  <el-option label="采购员" value="buyer" />
                </el-select>
              </el-form-item>
              
              <!-- 注册按钮 -->
              <el-form-item>
                <el-button type="primary" @click="handleRegister" :loading="loading" class="submit-btn">
                  <span v-if="!loading">注册</span>
                  <span v-else>注册中...</span>
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>

    <!-- 找回密码弹窗 -->
    <el-dialog 
      v-model="forgotPasswordVisible" 
      title="忘记密码" 
      width="450px" 
      center
      class="forgot-password-dialog"
      append-to-body
    >
      <el-form 
        :model="forgotPasswordForm" 
        :rules="forgotPasswordRules" 
        ref="forgotPasswordFormRef" 
        label-width="100px"
        class="forgot-password-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="forgotPasswordForm.username" 
            placeholder="请输入用户名" 
            class="form-input"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="forgotPasswordForm.email" 
            placeholder="请输入邮箱" 
            class="form-input"
            :prefix-icon="Message"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="forgotPasswordVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="handleForgotPassword" :loading="loading" class="submit-btn">
            <span v-if="!loading">提交</span>
            <span v-else>提交中...</span>
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Postcard, Iphone, Message, GoodsFilled, OfficeBuilding, View, Hide, Link, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const registerFormRef = ref()
const forgotPasswordFormRef = ref()
const loading = ref(false)
const activeTab = ref('login')
const forgotPasswordVisible = ref(false)
const loginPasswordVisible = ref(false)
const registerPasswordVisible = ref(false)

const forgotPasswordForm = reactive({
  username: '',
  email: ''
})

const forgotPasswordRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const loginForm = reactive({
  account: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  company: '',
  role: 'designer'
})



const loginRules = {
  account: [{ required: true, message: '请输入手机号或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  company: [{ required: true, message: '请输入所属公司', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        console.log('开始登录:', loginForm)
        const success = await userStore.loginUser(loginForm.account, loginForm.password)
        console.log('登录结果:', success)
        if (success) {
          console.log('登录成功，准备跳转')
          ElMessage.success('登录成功')
          // 直接跳转到dashboard，避免路由守卫的问题
          router.replace('/dashboard')
        } else {
          console.log('登录失败，store返回false')
          ElMessage.error('登录失败，请检查账号和密码')
        }
      } catch (error) {
        console.error('登录异常:', error)
        console.error('错误详情:', error.message, error.stack)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    } else {
      console.log('表单验证失败')
    }
  })
}



const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register(registerForm)
        if (res.code === 200) {
          ElMessage.success(res.message || '注册成功，请等待管理员审核')
          activeTab.value = 'login'
        } else {
          ElMessage.error(res.message || '注册失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const showForgotPassword = () => {
  forgotPasswordVisible.value = true
}

const handleForgotPassword = async () => {
  if (!forgotPasswordFormRef.value) return
  await forgotPasswordFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 这里需要后端实现密码找回API
        ElMessage.success('密码重置链接已发送到您的邮箱，请查收')
        forgotPasswordVisible.value = false
      } catch (error) {
        ElMessage.error(error.message || '提交失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 登录容器 */
.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  background-size: 400% 400%;
  animation: gradientAnimation 15s ease infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

/* 网格图案 */
.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(rgba(25, 118, 210, 0.05) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(25, 118, 210, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 50px 50px;
  }
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(33, 150, 243, 0.1);
  animation: float 6s ease-in-out infinite;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(33, 150, 243, 0.1);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 背景渐变动画 */
@keyframes gradientAnimation {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

/* 登录包装器 */
.login-wrapper {
  width: 900px;
  min-height: 550px;
  max-height: 750px;
  height: auto;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 24px;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.12);
  display: flex;
  overflow: hidden;
  animation: loginWrapperSlideIn 0.8s ease-out;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

/* 登录包装器悬停效果 */
.login-wrapper:hover {
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 登录包装器滑入动画 */
@keyframes loginWrapperSlideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 左侧欢迎区域 */
.welcome-section {
  flex: 1;
  background: linear-gradient(135deg, rgba(25, 118, 210, 0.95), rgba(33, 150, 243, 0.95));
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 左侧欢迎区域激活状态 */
.welcome-section.active {
  background: linear-gradient(135deg, rgba(25, 118, 210, 0.98), rgba(33, 150, 243, 0.98));
}

/* 欢迎区域内容 */
.welcome-content {
  text-align: center;
  z-index: 2;
  padding: 50px;
  animation: welcomeContentFadeIn 1s ease-out 0.3s both;
  max-width: 400px;
}

@keyframes welcomeContentFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-content h1 {
  font-size: 2.8rem;
  font-weight: 700;
  margin-bottom: 24px;
  line-height: 1.2;
  animation: textSlideIn 0.8s ease-out 0.5s both;
  background: linear-gradient(90deg, #ffffff, #bbdefb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes textSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.welcome-content p {
  font-size: 1.1rem;
  margin-bottom: 36px;
  opacity: 0.9;
  animation: textSlideIn 0.8s ease-out 0.7s both;
  line-height: 1.5;
}

/* 左侧按钮 */
.tab-btn {
  background: transparent;
  border: 2px solid white;
  color: white;
  padding: 14px 36px;
  border-radius: 36px;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: buttonSlideIn 0.8s ease-out 0.9s both;
  position: relative;
  overflow: hidden;
  margin-bottom: 40px;
}

@keyframes buttonSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.tab-btn:hover {
  background: white;
  color: #1976d2;
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.tab-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.tab-btn:hover::before {
  left: 100%;
}

/* 欢迎区域功能列表 */
.welcome-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  animation: featuresSlideIn 0.8s ease-out 1.1s both;
}

@keyframes featuresSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(8px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 18px;
  color: #bbdefb;
  transition: all 0.3s ease;
}

.feature-item:hover .feature-icon {
  transform: scale(1.2);
  color: white;
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  padding: 50px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  transition: all 0.3s ease;
}

/* 表单内容 */
.form-content {
  width: 100%;
  max-width: 380px;
}

/* 表单面板 */
.form-panel {
  animation: formPanelSlideIn 0.8s ease-out 0.3s both;
}

@keyframes formPanelSlideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 表单标题 */
.form-title {
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 36px;
  text-align: center;
  margin-top: 20px;
  position: relative;
  animation: titleSlideIn 0.8s ease-out 0.5s both;
  background: linear-gradient(90deg, #1976d2, #2196f3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes titleSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-title::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #1976d2, #2196f3);
  border-radius: 2px;
  animation: titleUnderlineGrow 0.8s ease-out 0.7s both;
}

@keyframes titleUnderlineGrow {
  from {
    width: 0;
  }
  to {
    width: 80px;
  }
}

/* 表单样式 */
.login-form,
.register-form {
  width: 100%;
}

/* 表单项目 */
:deep(.el-form-item) {
  margin-bottom: 24px;
  animation: formItemSlideIn 0.6s ease-out both;
}

:deep(.el-form-item:nth-child(1)) {
  animation-delay: 0.7s;
}

:deep(.el-form-item:nth-child(2)) {
  animation-delay: 0.8s;
}

:deep(.el-form-item:nth-child(3)) {
  animation-delay: 0.9s;
}

:deep(.el-form-item:nth-child(4)) {
  animation-delay: 1.0s;
}

:deep(.el-form-item:nth-child(5)) {
  animation-delay: 1.1s;
}

:deep(.el-form-item:nth-child(6)) {
  animation-delay: 1.2s;
}

:deep(.el-form-item:nth-child(7)) {
  animation-delay: 1.3s;
}

@keyframes formItemSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 表单输入框 */
.form-input {
  width: 100%;
  border-radius: 12px !important;
  border: 1px solid #e0e0e0 !important;
  height: 50px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative;
  overflow: visible !important;
}

/* 输入框容器 */
:deep(.el-input__wrapper) {
  border-radius: 12px !important;
  border: 1px solid #e0e0e0 !important;
  height: 50px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 输入框输入区域 */
:deep(.el-input__inner) {
  font-size: 15px !important;
  transition: all 0.3s ease !important;
  padding: 0 16px !important;
}

/* 输入框前缀图标 */
:deep(.el-input__prefix-icon) {
  color: #999 !important;
  font-size: 18px !important;
  transition: all 0.3s ease !important;
  margin-right: 8px !important;
}

/* 输入框后缀图标 */
:deep(.el-input__suffix-icon) {
  color: #999 !important;
  font-size: 18px !important;
  transition: all 0.3s ease !important;
  cursor: pointer;
  margin-right: 8px !important;
}

:deep(.el-input__suffix-icon:hover) {
  color: #1976d2 !important;
  transform: scale(1.15);
}

/* 输入框悬停效果 */
:deep(.el-input__wrapper:hover) {
  border-color: #1976d2 !important;
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.2) !important;
  transform: translateY(-2px);
}

/* 输入框聚焦效果 */
:deep(.el-input__wrapper.is-focus) {
  border-color: #1976d2 !important;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.2) !important;
  transform: translateY(-2px);
}

/* 表单选择器 */
.form-select {
  width: 100%;
  border-radius: 12px !important;
  border: 1px solid #e0e0e0 !important;
  height: 50px !important;
  transition: all 0.3s ease !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 选择器容器 */
:deep(.el-select .el-input__wrapper) {
  border-radius: 12px !important;
  border: 1px solid #e0e0e0 !important;
  height: 50px !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* 忘记密码链接 */
.form-options {
  text-align: right;
  margin-bottom: 36px;
  animation: formOptionsSlideIn 0.6s ease-out 1.4s both;
}

@keyframes formOptionsSlideIn {
  from {
    opacity: 0;
    transform: translateX(10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.forgot-link {
  font-size: 14px;
  color: #1976d2 !important;
  text-decoration: none !important;
  transition: all 0.3s ease !important;
  position: relative;
  font-weight: 500;
}

.forgot-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #1976d2;
  transition: width 0.3s ease;
}

.forgot-link:hover {
  color: #1565c0 !important;
}

.forgot-link:hover::after {
  width: 100%;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%) !important;
  border: none !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.3) !important;
  position: relative;
  overflow: hidden;
  animation: submitButtonSlideIn 0.6s ease-out 1.5s both;
  color: white !important;
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes submitButtonSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.submit-btn:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 8px 24px rgba(33, 150, 243, 0.4) !important;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.submit-btn:hover::before {
  left: 100%;
}

/* 社交登录 */
.social-login {
  margin-top: 36px;
  margin-bottom: 36px;
  text-align: center;
  animation: socialLoginSlideIn 0.6s ease-out 1.6s both;
}

@keyframes socialLoginSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.social-text {
  font-size: 14px;
  color: #666;
  position: relative;
  font-weight: 500;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  display: inline-block;
  padding: 0 16px;
  z-index: 1;
}

.social-text::before,
.social-text::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 100px;
  height: 1px;
  background: #e0e0e0;
  z-index: 0;
}

.social-text::before {
  left: -110px;
}

.social-text::after {
  right: -110px;
}

/* 社交按钮 */
.social-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.social-btn {
  width: 48px;
  height: 48px;
  border-radius: 50% !important;
  border: 1px solid #e0e0e0 !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: #666 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.social-btn:hover {
  border-color: #1976d2 !important;
  color: #1976d2 !important;
  transform: translateY(-3px) !important;
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.3) !important;
}

.social-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(33, 150, 243, 0.1), transparent);
  transition: left 0.5s ease;
}

.social-btn:hover::before {
  left: 100%;
}

/* 社交按钮颜色 */
.google-btn:hover {
  border-color: #db4437 !important;
  color: #db4437 !important;
  box-shadow: 0 6px 20px rgba(219, 68, 55, 0.3) !important;
}

.facebook-btn:hover {
  border-color: #4267B2 !important;
  color: #4267B2 !important;
  box-shadow: 0 6px 20px rgba(66, 103, 178, 0.3) !important;
}

.twitter-btn:hover {
  border-color: #1DA1F2 !important;
  color: #1DA1F2 !important;
  box-shadow: 0 6px 20px rgba(29, 161, 242, 0.3) !important;
}

.github-btn:hover {
  border-color: #333 !important;
  color: #333 !important;
  box-shadow: 0 6px 20px rgba(51, 51, 51, 0.3) !important;
}

/* 提示信息 */
.tip {
  margin-top: 36px;
  text-align: center;
  font-size: 13px;
  color: #999;
  background: rgba(245, 247, 250, 0.9);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 18px;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  animation: tipSlideIn 0.6s ease-out 1.7s both;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

@keyframes tipSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 高亮文本 */
.highlight {
  color: #1976d2;
  font-weight: 600;
  transition: all 0.3s ease;
  position: relative;
}

.highlight:hover {
  color: #1565c0;
  text-shadow: 0 0 12px rgba(33, 150, 243, 0.4);
  transform: scale(1.05);
}

/* 对话框样式 */
:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  color: white;
  padding: 24px;
  border-radius: 12px 12px 0 0;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
  transition: all 0.3s ease;
  font-size: 20px;
}

:deep(.el-dialog__headerbtn .el-dialog__close:hover) {
  color: rgba(255, 255, 255, 0.8);
  transform: scale(1.2);
}

/* 忘记密码对话框样式 */
.forgot-password-dialog {
  border-radius: 16px !important;
  overflow: hidden !important;
  box-shadow: 0 16px 64px rgba(33, 150, 243, 0.25) !important;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  animation: dialogSlideIn 0.4s ease-out;
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.forgot-password-form {
  padding: 36px 24px;
}

:deep(.forgot-password-dialog .el-form-item) {
  margin-bottom: 24px;
}

:deep(.forgot-password-dialog .el-form-item__label) {
  font-weight: 500;
  color: #0d47a1;
  font-size: 14px;
}

:deep(.forgot-password-dialog .form-input) {
  width: 100%;
  border-radius: 12px !important;
  border: 1px solid #e0e0e0 !important;
  height: 50px !important;
  transition: all 0.3s ease !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

:deep(.forgot-password-dialog .el-input__wrapper) {
  border-radius: 12px !important;
  border: 1px solid #e0e0e0 !important;
  height: 50px !important;
  transition: all 0.3s ease !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

:deep(.forgot-password-dialog .el-input__wrapper:hover) {
  border-color: #1976d2 !important;
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.2) !important;
}

:deep(.forgot-password-dialog .el-input__wrapper.is-focus) {
  border-color: #1976d2 !important;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.2) !important;
}

:deep(.forgot-password-dialog .el-input__prefix-icon) {
  color: #999 !important;
  font-size: 18px !important;
}

:deep(.forgot-password-dialog .dialog-footer) {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 24px;
  background: #fefdff;
  border-top: 1px solid #e0f2fe;
  border-radius: 0 0 16px 16px;
}

:deep(.forgot-password-dialog .cancel-btn),
:deep(.forgot-password-dialog .submit-btn) {
  border-radius: 12px !important;
  padding: 12px 32px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative;
  overflow: hidden;
  min-width: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.forgot-password-dialog .cancel-btn) {
  background: #fefdff !important;
  border: 1px solid #e0f2fe !important;
  color: #1976d2 !important;
}

:deep(.forgot-password-dialog .cancel-btn:hover) {
  background: #e0f2fe !important;
  border-color: #bbdefb !important;
  color: #1976d2 !important;
  box-shadow: 0 4px 16px rgba(33, 150, 243, 0.2) !important;
  transform: translateY(-2px);
}

:deep(.forgot-password-dialog .cancel-btn::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(33, 150, 243, 0.1), transparent);
  transition: left 0.5s ease;
}

:deep(.forgot-password-dialog .cancel-btn:hover::before) {
  left: 100%;
}

:deep(.forgot-password-dialog .submit-btn) {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%) !important;
  border: none !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.3) !important;
}

:deep(.forgot-password-dialog .submit-btn:hover) {
  box-shadow: 0 8px 24px rgba(33, 150, 243, 0.4) !important;
  transform: translateY(-2px) !important;
}

:deep(.forgot-password-dialog .submit-btn::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

:deep(.forgot-password-dialog .submit-btn:hover::before) {
  left: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    width: 90%;
    max-width: 500px;
    height: auto;
    margin: 20px;
    min-height: 700px;
  }
  
  .welcome-section {
    padding: 60px 40px;
  }
  
  .welcome-content h1 {
    font-size: 2.2rem;
  }
  
  .form-section {
    padding: 40px 30px;
  }
  
  .form-title {
    font-size: 1.8rem;
    margin-bottom: 30px;
  }
  
  .circle-1 {
    width: 200px;
    height: 200px;
  }
  
  .circle-2 {
    width: 150px;
    height: 150px;
  }
  
  .circle-3 {
    width: 100px;
    height: 100px;
  }
  
  .social-buttons {
    gap: 16px;
  }
  
  .social-btn {
    width: 44px;
    height: 44px;
  }
}

@media (max-width: 480px) {
  .login-wrapper {
    width: 95%;
    margin: 10px;
    min-height: 650px;
  }
  
  .welcome-section {
    padding: 40px 30px;
  }
  
  .welcome-content h1 {
    font-size: 1.8rem;
  }
  
  .form-section {
    padding: 30px 20px;
  }
  
  .form-title {
    font-size: 1.6rem;
  }
  
  .circle-1 {
    width: 150px;
    height: 150px;
  }
  
  .circle-2 {
    width: 100px;
    height: 100px;
  }
  
  .circle-3 {
    width: 80px;
    height: 80px;
  }
  
  .social-buttons {
    gap: 12px;
  }
  
  .social-btn {
    width: 40px;
    height: 40px;
  }
  
  .forgot-password-dialog {
    width: 90% !important;
    margin: 0 auto;
  }
}

/* 滚动条样式 */
.form-section::-webkit-scrollbar {
  width: 6px;
}

.form-section::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.5);
  border-radius: 3px;
}

.form-section::-webkit-scrollbar-thumb {
  background: rgba(33, 150, 243, 0.3);
  border-radius: 3px;
}

.form-section::-webkit-scrollbar-thumb:hover {
  background: rgba(33, 150, 243, 0.5);
}
</style>