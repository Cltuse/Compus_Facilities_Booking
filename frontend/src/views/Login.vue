<template>
  <div class="login-container">
    <!-- 装饰性背景元素 -->
    <div class="decoration-circle decoration-circle-1"></div>
    <div class="decoration-circle decoration-circle-2"></div>
    <div class="decoration-shape decoration-shape-1"></div>

    <div class="login-box">
      <!-- 顶部装饰线 -->
      <div class="login-box-header">
        <div class="header-line"></div>
      </div>

      <!-- Logo和标题区域 -->
      <div class="title-section">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="#409eff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="#409eff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="#409eff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="title">实验室设备预约与维护管理系统</h1>
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
          还没有账户？
          <el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>
      </el-form>

      <!-- 底部装饰 -->
      <div class="login-box-footer">
        <div class="footer-line"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { userAPI } from '../api';

const router = useRouter();
const loginFormRef = ref(null);
const loading = ref(false);

const loginForm = reactive({
  username: '',
  password: ''
});

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
};

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();
    loading.value = true;

    const res = await userAPI.login(loginForm);

    localStorage.setItem('userInfo', JSON.stringify(res.data));
    ElMessage.success('登录成功');

    // 根据角色跳转到不同页面
    if (res.data.role === 'ADMIN') {
      router.push('/admin/dashboard');
    } else {
      router.push('/user/welcome');
    }
  } catch (error) {
    console.error('登录失败:', error);
  } finally {
    loading.value = false;
  }
};

const goToRegister = () => {
  router.push('/register');
};
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
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 25%, #f8fafc 50%, #e0f2fe 100%);
  z-index: 0;
}

/* 装饰性背景元素 */
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
  box-shadow:
    0 25px 30px -5px rgba(0, 0, 0, 0.12),
    0 15px 15px -5px rgba(0, 0, 0, 0.06),
    0 0 0 1px rgba(0, 0, 0, 0.08);
}

/* 顶部装饰线 */
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

/* 标题区域 */
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
  transition: all 0.3s ease;
}

.logo-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.25);
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
  letter-spacing: 0.3px;
}

.subtitle {
  font-size: 13px;
  color: #718096;
  margin: 0;
  font-weight: 400;
  letter-spacing: 0.5px;
  opacity: 0.8;
}

.login-form {
  padding: 20px 48px 32px;
}

.input-wrapper {
  position: relative;
}

/* 重写Element Plus表单样式 */
.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 52px;
  background: #f7fafc;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  background: #ffffff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  background: #ffffff;
  box-shadow:
    0 0 0 3px rgba(64, 158, 255, 0.1),
    0 4px 6px rgba(0, 0, 0, 0.1);
}

.login-form :deep(.el-input__inner) {
  font-size: 15px;
  color: #2d3748;
  height: 50px;
  line-height: 50px;
  font-weight: 500;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #a0aec0;
  font-weight: 400;
}

.login-form :deep(.el-input__prefix-inner) {
  color: #718096;
  padding-right: 8px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #1976d2 100%);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #718096;
}

.register-link .el-link {
  margin-left: 4px;
  font-weight: 500;
}

/* 底部装饰线 */
.login-box-footer {
  height: 3px;
  background: linear-gradient(90deg, #e2e8f0 0%, #cbd5e0 50%, #e2e8f0 100%);
  background-size: 200% 100%;
  animation: gradient-shimmer 3s ease-in-out infinite reverse;
}

/* 响应式优化 */
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

  .title {
    font-size: 20px;
  }
}
</style>
