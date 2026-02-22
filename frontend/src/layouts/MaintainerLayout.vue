<template>
  <div class="maintainer-layout">
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
          <el-menu-item index="/maintainer/dashboard" class="menu-item">
            <el-icon class="menu-icon-item"><DataAnalysis /></el-icon>
            <template #title>
              <span class="menu-text">数据统计</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/maintainer/maintenance" class="menu-item">
            <el-icon class="menu-icon-item"><Tools /></el-icon>
            <template #title>
              <span class="menu-text">维护任务</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/maintainer/facility" class="menu-item">
            <el-icon class="menu-icon-item"><Box /></el-icon>
            <template #title>
              <span class="menu-text">设施管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/maintainer/profile" class="menu-item">
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
import { DataAnalysis, Box, Tools, Setting } from '@element-plus/icons-vue';

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
.maintainer-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar-container {
  width: 240px;
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.side-menu {
  border-right: none;
  padding-top: 20px;
}

.menu-item {
  margin: 4px 12px;
  border-radius: 8px;
}

.menu-icon-item {
  width: 24px;
  height: 24px;
  margin-right: 8px;
}

.menu-text {
  font-size: 14px;
  color: #303133;
}

.sidebar-footer {
  margin-top: auto;
  padding: 20px;
}

.footer-decoration {
  height: 1px;
  background: linear-gradient(to right, transparent, #dcdfe6, transparent);
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
}
</style>