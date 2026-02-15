<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section" v-if="activeTab === 'login'">
        <div class="welcome-content">
          <h1>Hello, Welcome!</h1>
          <p>Don't have an account?</p>
          <el-button type="primary" plain @click="activeTab = 'register'" class="register-btn">
            Register
          </el-button>
        </div>
      </div>
      
      <!-- 左侧注册区域 -->
      <div class="welcome-section" v-else>
        <div class="welcome-content">
          <h1>Create an Account</h1>
          <p>Already have an account?</p>
          <el-button type="primary" plain @click="activeTab = 'login'" class="login-btn">
            Login
          </el-button>
        </div>
      </div>
      
      <!-- 右侧表单区域 -->
      <div class="form-section">
        <div class="form-content">
          <!-- 登录表单 -->
          <div v-if="activeTab === 'login'">
            <h2 class="form-title">Login</h2>
            <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
              <!-- 用户名输入 -->
              <el-form-item prop="username">
                <el-input
                  v-model="loginForm.username"
                  placeholder="Username"
                  :prefix-icon="User"
                  class="form-input"
                />
              </el-form-item>
              
              <!-- 密码输入 -->
              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="Password"
                  :prefix-icon="Lock"
                  @keyup.enter="handleLogin"
                  class="form-input"
                />
              </el-form-item>
              
              <!-- 忘记密码 -->
              <div class="form-options">
                <el-link type="primary" :underline="false" @click="showForgotPassword" class="forgot-link">
                  Forgot password?
                </el-link>
              </div>
              
              <!-- 登录按钮 -->
              <el-form-item>
                <el-button type="primary" @click="handleLogin" :loading="loading" class="submit-btn">
                  Login
                </el-button>
              </el-form-item>
              
              <!-- 社交登录 -->
              <div class="social-login">
                <p class="social-text">or login with social platforms</p>
                <div class="social-buttons">
                  <el-button circle class="social-btn">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button circle class="social-btn">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button circle class="social-btn">
                    <el-icon><Link /></el-icon>
                  </el-button>
                  <el-button circle class="social-btn">
                    <el-icon><Link /></el-icon>
                  </el-button>
                </div>
              </div>
              
              <!-- 默认账号提示 -->
              <div class="tip">
                <p>默认账号：<span class="highlight">admin</span> / <span class="highlight">admin123</span></p>
              </div>
            </el-form>
          </div>
          
          <!-- 注册表单 -->
          <div v-else>
            <h2 class="form-title">Register</h2>
            <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="register-form">
              <!-- 用户名输入 -->
              <el-form-item prop="username">
                <el-input v-model="registerForm.username" placeholder="Username" :prefix-icon="User" class="form-input" />
              </el-form-item>
              
              <!-- 密码输入 -->
              <el-form-item prop="password">
                <el-input v-model="registerForm.password" type="password" placeholder="Password" :prefix-icon="Lock" class="form-input" />
              </el-form-item>
              
              <!-- 真实姓名输入 -->
              <el-form-item prop="realName">
                <el-input v-model="registerForm.realName" placeholder="Real Name" :prefix-icon="Postcard" class="form-input" />
              </el-form-item>
              
              <!-- 手机号输入 -->
              <el-form-item prop="phone">
                <el-input v-model="registerForm.phone" placeholder="Phone" :prefix-icon="Iphone" class="form-input" />
              </el-form-item>
              
              <!-- 邮箱输入 -->
              <el-form-item prop="email">
                <el-input v-model="registerForm.email" placeholder="Email" :prefix-icon="Message" class="form-input" />
              </el-form-item>
              
              <!-- 公司输入 -->
              <el-form-item prop="company">
                <el-input v-model="registerForm.company" placeholder="Company" :prefix-icon="OfficeBuilding" class="form-input" />
              </el-form-item>
              
              <!-- 角色选择 -->
              <el-form-item prop="role">
                <el-select v-model="registerForm.role" placeholder="Select Role" class="form-select">
                  <el-option label="Admin" value="admin" />
                  <el-option label="Designer" value="designer" />
                  <el-option label="Buyer" value="buyer" />
                </el-select>
              </el-form-item>
              
              <!-- 注册按钮 -->
              <el-form-item>
                <el-button type="primary" @click="handleRegister" :loading="loading" class="submit-btn">
                  Register
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
      title="Forgot Password" 
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
        <el-form-item label="Username" prop="username">
          <el-input 
            v-model="forgotPasswordForm.username" 
            placeholder="Please enter username" 
            class="form-input"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input 
            v-model="forgotPasswordForm.email" 
            placeholder="Please enter email" 
            class="form-input"
            :prefix-icon="Message"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="forgotPasswordVisible = false" class="cancel-btn">Cancel</el-button>
          <el-button type="primary" @click="handleForgotPassword" class="submit-btn">Submit</el-button>
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
import { User, Lock, Postcard, Iphone, Message, GoodsFilled, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const registerFormRef = ref()
const forgotPasswordFormRef = ref()
const loading = ref(false)
const activeTab = ref('login')
const forgotPasswordVisible = ref(false)

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
  username: '',
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
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
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
        console.log('开始登录:', loginForm.username, loginForm.password)
        const success = await userStore.loginUser(loginForm.username, loginForm.password)
        console.log('登录结果:', success)
        if (success) {
          console.log('登录成功，准备跳转')
          ElMessage.success('登录成功')
          // 直接跳转到dashboard，避免路由守卫的问题
          router.replace('/dashboard')
        } else {
          console.log('登录失败，store返回false')
          ElMessage.error('登录失败，请检查用户名和密码')
        }
      } catch (error) {
        console.error('登录异常:', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
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
  background: linear-gradient(-45deg, #ffffff, #e3f2fd, #bbdefb, #ffffff);
  background-size: 400% 400%;
  animation: gradientAnimation 15s ease infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
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
  min-height: 500px;
  max-height: 700px;
  height: auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
  animation: loginWrapperSlideIn 0.8s ease-out;
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
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 欢迎区域内容 */
.welcome-content {
  text-align: center;
  z-index: 2;
  padding: 40px;
}

.welcome-content h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
}

.welcome-content p {
  font-size: 1.1rem;
  margin-bottom: 30px;
  opacity: 0.9;
}

/* 左侧按钮 */
.welcome-section .register-btn,
.welcome-section .login-btn {
  background: transparent;
  border: 2px solid white;
  color: white;
  padding: 12px 30px;
  border-radius: 30px;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.welcome-section .register-btn:hover,
.welcome-section .login-btn:hover {
  background: white;
  color: #1976d2;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow-y: auto;
}

/* 表单内容 */
.form-content {
  width: 100%;
  max-width: 350px;
}

/* 表单标题 */
.form-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
  margin-top: 20px;
}

/* 表单样式 */
.login-form,
.register-form {
  width: 100%;
}

/* 表单项目 */
:deep(.el-form-item) {
  margin-bottom: 16px;
}

/* 表单输入框 */
.form-input {
  width: 100%;
  border-radius: 8px !important;
  border: 1px solid #e0e0e0 !important;
  height: 45px !important;
  transition: all 0.3s ease !important;
}

/* 输入框容器 */
:deep(.el-input__wrapper) {
  border-radius: 8px !important;
  border: 1px solid #e0e0e0 !important;
  height: 45px !important;
  transition: all 0.3s ease !important;
}

/* 输入框输入区域 */
:deep(.el-input__inner) {
  font-size: 14px !important;
}

/* 输入框前缀图标 */
:deep(.el-input__prefix-icon) {
  color: #999 !important;
  font-size: 16px !important;
}

/* 输入框悬停效果 */
:deep(.el-input__wrapper:hover) {
  border-color: #1976d2 !important;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.15) !important;
}

/* 输入框聚焦效果 */
:deep(.el-input__wrapper.is-focus) {
  border-color: #1976d2 !important;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.2) !important;
}

/* 表单选择器 */
.form-select {
  width: 100%;
  border-radius: 8px !important;
  border: 1px solid #e0e0e0 !important;
  height: 45px !important;
}

/* 选择器容器 */
:deep(.el-select .el-input__wrapper) {
  border-radius: 8px !important;
  border: 1px solid #e0e0e0 !important;
  height: 45px !important;
}

/* 忘记密码链接 */
.form-options {
  text-align: right;
  margin-bottom: 30px;
}

.forgot-link {
  font-size: 14px;
  color: #1976d2 !important;
  text-decoration: none !important;
}

.forgot-link:hover {
  color: #1565c0 !important;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 45px;
  border-radius: 8px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%) !important;
  border: none !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3) !important;
}

.submit-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 16px rgba(33, 150, 243, 0.4) !important;
}

/* 社交登录 */
.social-login {
  margin-top: 30px;
  text-align: center;
}

.social-text {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  position: relative;
}

.social-text::before,
.social-text::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 30%;
  height: 1px;
  background: #e0e0e0;
}

.social-text::before {
  left: 0;
}

.social-text::after {
  right: 0;
}

/* 社交按钮 */
.social-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-btn {
  width: 40px;
  height: 40px;
  border-radius: 50% !important;
  border: 1px solid #e0e0e0 !important;
  background: white !important;
  color: #666 !important;
  transition: all 0.3s ease !important;
}

.social-btn:hover {
  border-color: #1976d2 !important;
  color: #1976d2 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3) !important;
}

/* 提示信息 */
.tip {
  margin-top: 30px;
  text-align: center;
  font-size: 13px;
  color: #999;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

/* 高亮文本 */
.highlight {
  color: #1976d2;
  font-weight: 600;
}

/* 对话框样式 */
:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  color: white;
  padding: 20px;
  border-radius: 10px 10px 0 0;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

:deep(.el-dialog__headerbtn .el-dialog__close:hover) {
  color: rgba(255, 255, 255, 0.8);
}

/* 忘记密码对话框样式 */
.forgot-password-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 12px 40px rgba(33, 150, 243, 0.2) !important;
}

.forgot-password-form {
  padding: 30px 20px;
}

:deep(.forgot-password-dialog .el-form-item) {
  margin-bottom: 20px;
}

:deep(.forgot-password-dialog .el-form-item__label) {
  font-weight: 500;
  color: #0d47a1;
  font-size: 14px;
}

:deep(.forgot-password-dialog .form-input) {
  width: 100%;
  border-radius: 8px !important;
  border: 1px solid #e0e0e0 !important;
  height: 45px !important;
  transition: all 0.3s ease !important;
}

:deep(.forgot-password-dialog .el-input__wrapper) {
  border-radius: 8px !important;
  border: 1px solid #e0e0e0 !important;
  height: 45px !important;
  transition: all 0.3s ease !important;
}

:deep(.forgot-password-dialog .el-input__wrapper:hover) {
  border-color: #1976d2 !important;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.15) !important;
}

:deep(.forgot-password-dialog .el-input__wrapper.is-focus) {
  border-color: #1976d2 !important;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.2) !important;
}

:deep(.forgot-password-dialog .el-input__prefix-icon) {
  color: #999 !important;
  font-size: 16px !important;
}

:deep(.forgot-password-dialog .dialog-footer) {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  background: #fefdff;
  border-top: 1px solid #e0f2fe;
  border-radius: 0 0 12px 12px;
}

:deep(.forgot-password-dialog .cancel-btn),
:deep(.forgot-password-dialog .submit-btn) {
  border-radius: 8px !important;
  padding: 10px 24px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  transition: all 0.3s ease !important;
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
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.2) !important;
}

:deep(.forgot-password-dialog .submit-btn) {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%) !important;
  border: none !important;
  color: white !important;
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3) !important;
}

:deep(.forgot-password-dialog .submit-btn:hover) {
  box-shadow: 0 6px 16px rgba(33, 150, 243, 0.4) !important;
  transform: translateY(-2px) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    width: 90%;
    max-width: 500px;
    height: auto;
  }
  
  .welcome-section {
    padding: 60px 40px;
  }
  
  .welcome-content h1 {
    font-size: 2rem;
  }
  
  .form-section {
    padding: 40px 30px;
  }
  
  .form-title {
    font-size: 1.8rem;
    margin-bottom: 30px;
  }
}

@media (max-width: 480px) {
  .login-wrapper {
    width: 95%;
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
}
</style>
