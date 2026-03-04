<template>
  <div class="notice-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M9.5 2.5A3.5 3.5 0 0 1 13 6a1 1 0 0 0 0 2 3.5 3.5 0 0 1-3.5 3.5H7a4 4 0 0 1-4-4 1 1 0 0 0-2 0 6 6 0 0 0 6 6h2.5A5.5 5.5 0 0 0 15 2.5Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          系统通知
        </h1>
        <p class="page-subtitle">查看系统公告和重要通知</p>
      </div>
    </div>

    <!-- 通知列表 -->
    <div class="notices-container" v-if="paginatedNoticeList.length > 0">
      <div
        class="notice-item"
        v-for="item in paginatedNoticeList"
        :key="item.id"
        @click="handleNoticeClick(item)"
      >
        <div class="notice-header">
          <div class="notice-left">
            <div class="notice-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M22 17H2L12 2L22 17Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="12" y1="11" x2="12" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="12" y1="20" x2="12.01" y2="20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="notice-title-section">
              <h3 class="notice-title">{{ item.title }}</h3>
              <p class="notice-meta">
                <span class="publisher">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14">
                    <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  {{ item.publisherName }}
                </span>
                <span class="time">
                  <svg viewBox="0 0 24 24" fill="none" width="14" height="14">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <path d="M12 6v6l4 2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  {{ formatTime(item.publishTime) }}
                </span>
              </p>
            </div>
          </div>
          <div class="notice-badge">
            <div class="new-badge" v-if="isNewNotice(item.publishTime)">新</div>
          </div>
        </div>

        <div class="notice-content">
          <div class="content-text">{{ item.content }}</div>
        </div>

        <div class="notice-actions">
          <div class="action-buttons">
            <el-button
              size="small"
              class="action-btn detail-btn"
              @click.stop="handleViewDetail(item)"
            >
              <el-icon><Document /></el-icon>
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else>
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <h3 class="empty-title">暂无通知</h3>
      <p class="empty-description">当前没有任何系统通知</p>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container" v-if="noticeList.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="noticeList.length"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      width="600px"
      class="notice-detail-dialog"
      :show-close="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M22 17H2L12 2L22 17Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <line x1="12" y1="11" x2="12" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <line x1="12" y1="20" x2="12.01" y2="20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">{{ currentNotice.title }}</span>
      </div>

      <div class="dialog-body">
        <div class="notice-info">
          <div class="info-row">
            <div class="info-item">
              <div class="info-label">发布人</div>
              <div class="info-value">{{ currentNotice.publisherName }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">发布时间</div>
              <div class="info-value">{{ formatTime(currentNotice.publishTime) }}</div>
            </div>
          </div>
        </div>

        <div class="notice-detail-content">
          <h4 class="content-title">通知内容</h4>
          <div class="content-text-full">{{ currentNotice.content }}</div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="detailDialogVisible = false" class="close-btn">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Document } from '@element-plus/icons-vue';
import { noticeAPI } from '../../api';
const noticeList = ref([]);
const detailDialogVisible = ref(false);
const currentNotice = ref({});

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);

onMounted(() => {
  loadNotices();
});

// 分页后的通知列表
const paginatedNoticeList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return noticeList.value.slice(start, end);
});

const loadNotices = async () => {
  try {
    const res = await noticeAPI.published();
    noticeList.value = res.data;
  } catch (error) {
    console.error('加载通知失败:', error);
  }
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  try {
    const date = new Date(timeStr);
    const now = new Date();
    const diff = now - date;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (days === 0) {
      const hours = Math.floor(diff / (1000 * 60 * 60));
      if (hours === 0) {
        const minutes = Math.floor(diff / (1000 * 60));
        return minutes <= 0 ? '刚刚' : `${minutes}分钟前`;
      }
      return `${hours}小时前`;
    } else if (days === 1) {
      return '昨天';
    } else if (days < 7) {
      return `${days}天前`;
    } else {
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    }
  } catch (error) {
    return timeStr;
  }
};

// 判断是否为新通知（3天内）
const isNewNotice = (timeStr) => {
  if (!timeStr) return false;
  try {
    const date = new Date(timeStr);
    const now = new Date();
    const diff = now - date;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    return days <= 3;
  } catch (error) {
    return false;
  }
};

// 处理通知点击
const handleNoticeClick = (notice) => {
  handleViewDetail(notice);
};

// 查看详情
const handleViewDetail = (notice) => {
  currentNotice.value = notice;
  detailDialogVisible.value = true;
};

</script>

<style scoped>
/* 页面背景 */
.notice-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  padding: 24px;
  position: relative;
}

/* 页面标题区域 */
.page-header {
  position: relative;
  margin-bottom: 32px;
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 200% 100%;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

.header-content {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.header-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
  line-height: 1.3;
}

.title-icon {
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

.title-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.25);
}

.title-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.page-subtitle {
  margin: 0;
  color: #718096;
  font-size: 14px;
  font-weight: 400;
  opacity: 0.8;
}

/* 通知容器 */
.notices-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 通知项 */
.notice-item {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.notice-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 200% 100%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.notice-item:hover::before {
  opacity: 1;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

.notice-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border-color: #bae7ff;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.notice-left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  flex: 1;
}

.notice-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.notice-icon svg {
  width: 20px;
  height: 20px;
  color: #409eff;
}

.notice-title-section {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 16px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 0;
  font-size: 12px;
  color: #718096;
}

.publisher, .time {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.publisher svg, .time svg {
  color: #409eff;
  opacity: 0.7;
}

.notice-badge {
  flex-shrink: 0;
}

.new-badge {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
  color: #ffffff;
  font-size: 11px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(245, 101, 101, 0.3);
  animation: pulse-badge 2s infinite;
}

/* 通知内容 */
.notice-content {
  margin-bottom: 16px;
}

.content-text {
  font-size: 14px;
  color: #4a5568;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 通知操作 */
.notice-actions {
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.action-btn {
  border-radius: 8px;
  font-weight: 600;
  font-size: 12px;
  padding: 8px 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}


.detail-btn {
  color: #38a169;
  border-color: #38a169;
  background: rgba(72, 187, 120, 0.05);
}

.detail-btn:hover {
  background: linear-gradient(135deg, #38a169 0%, #2f855a 100%);
  color: #ffffff;
  border-color: #38a169;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(72, 187, 120, 0.3);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #f7fafc 0%, #e6f7ff 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.1);
}

.empty-icon svg {
  width: 40px;
  height: 40px;
  color: #409eff;
  opacity: 0.6;
}

.empty-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 8px 0;
}

.empty-description {
  font-size: 14px;
  color: #718096;
  margin: 0;
}

/* 通知详情对话框 */
.notice-detail-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.notice-detail-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.notice-detail-dialog :deep(.el-dialog__header) {
  padding: 0;
  margin: 0;
  border-bottom: none;
}

.notice-detail-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.notice-detail-dialog :deep(.el-dialog__footer) {
  padding: 0;
  margin: 0;
  border-top: none;
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 32px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-bottom: 1px solid #e4e7ed;
}

.dialog-title-icon {
  width: 48px;
  height: 48px;
  background: rgba(64, 158, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.dialog-title-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.dialog-title-text {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
}

.dialog-body {
  padding: 32px;
}

.notice-info {
  margin-bottom: 24px;
  padding: 16px;
  background: #f7fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.info-row {
  display: flex;
  gap: 32px;
}

.info-item {
  flex: 1;
}

.info-label {
  font-size: 12px;
  color: #718096;
  font-weight: 500;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #2d3748;
  font-weight: 600;
}

.notice-detail-content {
  margin-top: 24px;
}

.content-title {
  font-size: 16px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 16px 0;
}

.content-text-full {
  font-size: 14px;
  color: #4a5568;
  line-height: 1.8;
  white-space: pre-wrap;
  background: #f7fafc;
  padding: 20px;
  border-radius: 12px;
  border-left: 4px solid #bae7ff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px 32px;
  background: #f8fafc;
  border-top: 1px solid #e4e7ed;
}

.close-btn {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  color: #718096;
  background: #ffffff;
  transition: all 0.3s ease;
}

.close-btn:hover {
  border-color: #cbd5e0;
  background: #f7fafc;
  transform: translateY(-1px);
}

/* 动画效果 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

@keyframes pulse-badge {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .notice-page {
    padding: 16px;
  }

  .header-content {
    padding: 20px;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .page-title {
    font-size: 24px;
  }

  .notice-item {
    padding: 20px;
  }

  .notice-left {
    gap: 12px;
  }

  .notice-icon {
    width: 36px;
    height: 36px;
  }

  .notice-title {
    font-size: 15px;
  }

  .notice-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 8px;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }

  .info-row {
    flex-direction: column;
    gap: 16px;
  }

  .dialog-body {
    padding: 20px;
  }

  .empty-state {
    padding: 60px 16px;
  }

  .empty-icon {
    width: 60px;
    height: 60px;
  }

  .empty-icon svg {
    width: 30px;
    height: 30px;
  }

  .empty-title {
    font-size: 18px;
  }
}
</style>