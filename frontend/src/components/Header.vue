<template>
  <div class="header-container">
    <!-- 顶部装饰线 -->
    <div class="header-decoration"></div>

    <div class="header-content">
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="logo-text">
          <h1 class="system-title">校园公共设施预约管理系统</h1>
          <p class="system-subtitle">Campus Public Facility Booking System</p>
        </div>
      </div>

      <!-- 用户信息区域 -->
      <div class="user-section">
        <div class="user-info-wrapper">
          <div class="user-avatar">
            <span>{{ userInfo.realName ? userInfo.realName.charAt(0) : 'U' }}</span>
          </div>
          <div class="user-details">
            <div class="user-name">{{ userInfo.realName }}</div>
            <div class="user-role">{{ userInfo.role === 'ADMIN' ? '系统管理员' : '普通用户' }}</div>
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
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import { User } from '@element-plus/icons-vue';

const router = useRouter();
const userInfo = ref({});

onMounted(() => {
  const info = localStorage.getItem('userInfo');
  if (info) {
    userInfo.value = JSON.parse(info);
  }
});

const handleProfile = () => {
  const info = localStorage.getItem('userInfo');
  if (info) {
    const user = JSON.parse(info);
    if (user.role === 'ADMIN') {
      router.push('/admin/profile');
    } else {
      router.push('/user/profile');
    }
  }
};

const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('userInfo');
    router.push('/login');
  }).catch(() => {});
};
</script>

<style scoped>
.header-container {
  position: relative;
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
  transition: all 0.3s ease;
}

/* 顶部装饰线 */
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

/* Logo区域 */
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
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transition: all 0.3s ease;
}

.logo-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.25);
}

.logo-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.system-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
  line-height: 1.3;
  letter-spacing: 0.3px;
}

.system-subtitle {
  font-size: 12px;
  color: #718096;
  margin: 0;
  font-weight: 400;
  letter-spacing: 0.5px;
  opacity: 0.8;
}

/* 用户信息区域 */
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
  border: 1px solid rgba(64, 158, 255, 0.1);
  transition: all 0.3s ease;
}

.user-info-wrapper:hover {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-color: rgba(64, 158, 255, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
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
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.user-role {
  font-size: 11px;
  color: #718096;
  font-weight: 500;
  white-space: nowrap;
}

/* 下拉菜单 */
.user-dropdown {
  position: relative;
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
  transition: all 0.3s ease;
  color: #409eff;
}

.dropdown-trigger:hover {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  color: #ffffff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.dropdown-trigger .el-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-trigger .el-icon {
  transform: rotate(180deg);
}


/* 自定义下拉菜单 */
.custom-dropdown-menu {
  margin-top: 8px;
  border-radius: 8px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.custom-dropdown-menu :deep(.el-dropdown-menu__item) {
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-dropdown-menu :deep(.el-dropdown-menu__item:hover) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  color: #409eff;
}

.custom-dropdown-menu :deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
  color: inherit;
}

.dropdown-item {
  border-top: 1px solid #e2e8f0;
}

.logout-item {
  color: #f56565;
  border-top: 1px solid #e2e8f0;
}

.logout-item:hover {
  color: #f56565;
  background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%);
}

/* 动画效果 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 12px 16px;
    height: 70px;
  }

  .logo-text {
    display: none;
  }

  .user-name {
    max-width: 80px;
  }

  .system-title {
    font-size: 16px;
  }
}
</style>
