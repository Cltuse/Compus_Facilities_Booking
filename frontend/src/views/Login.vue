<template>
  <div class="login-container">
    <div class="decoration-circle decoration-circle-1"></div>
    <div class="decoration-circle decoration-circle-2"></div>
    <div class="decoration-shape decoration-shape-1"></div>

    <div class="login-box">
      <div class="login-box-header">
        <div class="header-line"></div>
      </div>

      <div class="title-section">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="#409eff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="#409eff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="#409eff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="title">校园公共设施预约管理系统</h1>
        <p class="subtitle">Campus Public Facility Booking System</p>
      </div>

      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              prefix-icon="User"
            />
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-wrapper">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            <span v-if="!loading">登录系统</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？
          <el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>
      </el-form>

      <div class="login-box-footer">
        <div class="footer-line"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userAPI } from '../api'
import { setAuth } from '../utils/auth'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function resolveHomeByRole(role) {
  if (role === 'ADMIN') {
    return '/admin/dashboard'
  }
  if (role === 'MAINTAINER') {
    return '/maintainer/dashboard'
  }
  return '/user/welcome'
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true

    const res = await userAPI.login(loginForm)
    setAuth(res.data)
    ElMessage.success('登录成功')
    router.push(resolveHomeByRole(res.data.user?.role))
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8fafc;
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 25%, #f8fafc 50%, #e0f2fe 100%);
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;
  z-index: 0;
}

.decoration-circle-1 {
  width: 200px;
  height: 200px;
  background: linear-gradient(45deg, #e1f5fe, #b3e5fc);
  top: -50px;
  right: -50px;
  animation: float 6s ease-in-out infinite;
}

.decoration-circle-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(45deg, #e8f5e8, #c8e6c9);
  bottom: -30px;
  left: -30px;
  animation: float 8s ease-in-out infinite reverse;
}

.decoration-shape {
  position: absolute;
  opacity: 0.4;
  z-index: 0;
}

.decoration-shape-1 {
  width: 100px;
  height: 100px;
  background: linear-gradient(60deg, #fff3e0, #ffe0b2);
  top: 100px;
  left: 50px;
  transform: rotate(45deg);
  animation: rotate 10s linear infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes rotate {
  from { transform: rotate(45deg); }
  to { transform: rotate(405deg); }
}

.login-box {
  width: 460px;
  padding: 0;
  background: #ffffff;
  border-radius: 16px;
  box-shadow:
    0 20px 25px -5px rgba(0, 0, 0, 0.1),
    0 10px 10px -5px rgba(0, 0, 0, 0.04),
    0 0 0 1px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 1;
  overflow: hidden;
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-2px);
}

.login-box-header {
  height: 4px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 200% 100%;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.title-section {
  padding: 40px 48px 20px;
  text-align: center;
}

.logo-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.logo-icon svg {
  width: 32px;
  height: 32px;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.subtitle {
  font-size: 13px;
  color: #718096;
  margin: 0;
}

.login-form {
  padding: 20px 48px 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 52px;
  background: #f7fafc;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
  width: 100%;
}

.login-form :deep(.el-form-item__content) {
  width: 100%;
}

.login-button {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #718096;
}

.login-box-footer {
  height: 3px;
  background: linear-gradient(90deg, #e2e8f0 0%, #cbd5e0 50%, #e2e8f0 100%);
  background-size: 200% 100%;
  animation: gradient-shimmer 3s ease-in-out infinite reverse;
}

@media (max-width: 520px) {
  .login-box {
    width: 90%;
    margin: 0 20px;
  }

  .title-section {
    padding: 30px 32px 16px;
  }

  .login-form {
    padding: 16px 32px 24px;
  }
}
</style>
