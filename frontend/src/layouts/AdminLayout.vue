<template>
  <div class="admin-layout">
    <Header />
    <div class="main-container">
      <!-- 侧边栏菜单 -->
      <div class="sidebar-container">
        <el-menu
          :default-active="activeMenu"
          class="side-menu"
          @select="handleMenuSelect"
          :collapse="false"
          unique-opened
        >
          <el-menu-item index="/admin/dashboard" class="menu-item">
            <el-icon class="menu-icon-item"><DataAnalysis /></el-icon>
            <template #title>
              <span class="menu-text">数据统计</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/facility" class="menu-item">
            <el-icon class="menu-icon-item"><Box /></el-icon>
            <template #title>
              <span class="menu-text">设施管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/facility-category" class="menu-item">
            <el-icon class="menu-icon-item"><Collection /></el-icon>
            <template #title>
              <span class="menu-text">设施类别</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/reservation" class="menu-item">
            <el-icon class="menu-icon-item"><Calendar /></el-icon>
            <template #title>
              <span class="menu-text">预约管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/maintenance" class="menu-item">
            <el-icon class="menu-icon-item"><Tools /></el-icon>
            <template #title>
              <span class="menu-text">维护管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/user" class="menu-item">
            <el-icon class="menu-icon-item"><User /></el-icon>
            <template #title>
              <span class="menu-text">用户管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/violation-records" class="menu-item">
            <el-icon class="menu-icon-item"><Warning /></el-icon>
            <template #title>
              <span class="menu-text">违规记录</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/notice" class="menu-item">
            <el-icon class="menu-icon-item"><Bell /></el-icon>
            <template #title>
              <span class="menu-text">通知管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/rule-config" class="menu-item">
            <el-icon class="menu-icon-item"><Setting /></el-icon>
            <template #title>
              <span class="menu-text">规则配置</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/blacklist" class="menu-item">
            <el-icon class="menu-icon-item"><Warning /></el-icon>
            <template #title>
              <span class="menu-text">黑名单管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/feedback" class="menu-item">
            <el-icon class="menu-icon-item"><ChatDotSquare /></el-icon>
            <template #title>
              <span class="menu-text">意见反馈</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/operation-log" class="menu-item">
            <el-icon class="menu-icon-item"><Document /></el-icon>
            <template #title>
              <span class="menu-text">操作日志</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/admin/profile" class="menu-item">
            <el-icon class="menu-icon-item"><Setting /></el-icon>
            <template #title>
              <span class="menu-text">个人中心</span>
            </template>
          </el-menu-item>
        </el-menu>

        <!-- 侧边栏底部装饰 -->
        <div class="sidebar-footer">
          <div class="footer-decoration"></div>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import Header from '../components/Header.vue';
import { Setting, Warning, Document, User, Bell, ChatDotSquare, DataAnalysis, Box, Collection, Calendar, Tools } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const activeMenu = ref(route.path);

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath;
});

const handleMenuSelect = (index) => {
  router.push(index);
};
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏容器 */
.sidebar-container {
  width: 200px;
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

/* 侧边栏菜单 */
.side-menu {
  flex: 1;
  border: none;
  background: #ffffff;
  padding: 20px 0;
}

.side-menu :deep(.el-menu-item) {
  margin: 0 12px 8px 12px;
  border-radius: 12px;
  padding: 0 16px;
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.side-menu :deep(.el-menu-item):before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 12px;
  z-index: -1; /* 将伪元素置于文本下方 */
}

.side-menu :deep(.el-menu-item:hover) {
  color: #252b47;
  transform: translateX(4px);
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.side-menu :deep(.el-menu-item:hover):before {
  opacity: 1;
}

.side-menu :deep(.el-menu-item.is-active) {
  color: #ffffff;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
  font-weight: 600;
}

.side-menu :deep(.el-menu-item.is-active):before {
  opacity: 1;
  background: transparent;
}

.side-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.side-menu :deep(.el-menu-item:hover .el-icon) {
  transform: scale(1.1);
  color: #409eff;
  animation: icon-glow 1.5s ease-in-out infinite;
}

.side-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #ffffff;
  transform: scale(1.1);
}

.menu-icon-item {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.2px;
}

/* 侧边栏底部装饰 */
.sidebar-footer {
  padding: 20px 0;
  position: relative;
}

.footer-decoration {
  height: 3px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 200% 100%;
  animation: gradient-shimmer 3s ease-in-out infinite;
  margin: 0 16px;
  border-radius: 2px;
}

/* 主内容区域 */
.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  position: relative;
}


/* 动画效果 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

@keyframes icon-glow {
  0% { filter: drop-shadow(0 0 2px rgba(64, 158, 255, 0.3)); }
  50% { filter: drop-shadow(0 0 6px rgba(64, 158, 255, 0.6)); }
  100% { filter: drop-shadow(0 0 2px rgba(64, 158, 255, 0.3)); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-container {
    width: 160px;
  }

  .sidebar-header {
    padding: 16px 16px;
  }

  .menu-title {
    font-size: 16px;
  }

  .menu-icon {
    width: 32px;
    height: 32px;
  }

  .menu-icon svg {
    width: 16px;
    height: 16px;
  }

  .side-menu :deep(.el-menu-item) {
    margin: 0 8px 6px 8px;
    padding: 0 12px;
    height: 44px;
    line-height: 44px;
    font-size: 13px;
  }

  .side-menu :deep(.el-menu-item .el-icon) {
    font-size: 16px;
    margin-right: 10px;
  }

  .menu-icon-item {
    font-size: 16px;
    margin-right: 10px;
  }

  .content-area {
    padding: 16px;
  }
}
</style>