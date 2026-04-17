<template>
  <div class="header-container">
    <div class="header-decoration"></div>

    <div class="header-content">
      <div class="logo-section">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
        </div>
        <div class="logo-text">
          <h1 class="system-title">校园公共设施预约管理系统</h1>
          <p class="system-subtitle">Campus Public Facility Booking System</p>
        </div>
      </div>

      <div class="user-section">
        <div class="user-info-wrapper">
          <div class="user-avatar">
            <span>{{ displayInitial }}</span>
          </div>
          <div class="user-details">
            <div class="user-name">{{ displayName }}</div>
            <div class="user-role">{{ roleText }}</div>
          </div>
        </div>
        <el-dropdown class="user-dropdown" trigger="click">
          <div class="dropdown-trigger">
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="custom-dropdown-menu">
              <el-dropdown-item class="dropdown-item" @click="handleProfile">
                <el-icon><User /></el-icon>
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item class="dropdown-item logout-item" @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { ArrowDown, SwitchButton, User } from '@element-plus/icons-vue'
import { clearAuth, getUserInfo } from '../utils/auth'

const router = useRouter()
const userInfo = ref({})

onMounted(() => {
  userInfo.value = getUserInfo() || {}
})

const displayName = computed(() => userInfo.value.realName || userInfo.value.username || '用户')
const displayInitial = computed(() => displayName.value.charAt(0).toUpperCase())

const roleText = computed(() => {
  if (userInfo.value.role === 'ADMIN') {
    return '系统管理员'
  }
  if (userInfo.value.role === 'MAINTAINER') {
    return '维护人员'
  }
  return '普通用户'
})

const handleProfile = () => {
  const info = getUserInfo()
  if (!info) {
    return
  }

  if (info.role === 'ADMIN') {
    router.push('/admin/profile')
  } else if (info.role === 'MAINTAINER') {
    router.push('/maintainer/profile')
  } else {
    router.push('/user/profile')
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    clearAuth()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.header-container {
  position: relative;
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.header-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 200% 100%;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  height: 80px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.system-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.system-subtitle {
  font-size: 12px;
  color: #718096;
  margin: 0;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  border-radius: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
}

.user-role {
  font-size: 11px;
  color: #718096;
}

.dropdown-trigger {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  border: 1px solid rgba(64, 158, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #409eff;
}

.custom-dropdown-menu {
  margin-top: 8px;
  border-radius: 8px;
}

.dropdown-item {
  border-top: 1px solid #e2e8f0;
}

.logout-item {
  color: #f56565;
}

@keyframes gradient-shimmer {
  0%, 100% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 12px 16px;
    height: 70px;
  }

  .logo-text {
    display: none;
  }
}
</style>
