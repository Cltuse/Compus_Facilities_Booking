<template>
  <div class="feedback-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          意见反馈管理
        </h1>
        <p class="page-subtitle">查看和处理用户的意见反馈</p>
      </div>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索反馈标题或内容..."
          size="large"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleClearSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch" type="primary" size="large">
              搜索
            </el-button>
          </template>
        </el-input>
      </div>
      <div class="filter-section">
        <el-select v-model="filterStatus" placeholder="状态筛选" size="large" class="filter-select">
          <el-option label="全部" value="" />
          <el-option label="待处理" value="PENDING" />
          <el-option label="已处理" value="PROCESSED" />
          <el-option label="已关闭" value="CLOSED" />
        </el-select>
        <el-select v-model="filterType" placeholder="类型筛选" size="large" class="filter-select">
          <el-option label="全部" value="" />
          <el-option label="建议" value="SUGGESTION" />
          <el-option label="投诉" value="COMPLAINT" />
          <el-option label="咨询" value="QUESTION" />
        </el-select>
        <el-button type="primary" size="large" class="refresh-button" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 反馈表格 -->
    <div class="table-container">
      <el-table
        :data="filteredFeedbackList"
        class="feedback-table"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @row-click="handleRowClick"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="title" label="反馈标题" min-width="200">
          <template #default="{ row }">
            <div class="feedback-title">
              <div class="title">{{ row.title }}</div>
              <div class="content-preview" v-if="row.content">{{ getContentPreview(row.content) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="提交用户" width="220" align="left">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="28" :src="row.userAvatar">
                {{ (row.userName || '未知').charAt(0) }}
              </el-avatar>
              <span class="username">{{ row.userName || '未知用户' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="反馈类型" width="140" align="center">
          <template #default="{ row }">
            <span class="type-badge" :class="getFeedbackTypeClass(row.type)">
              {{ getFeedbackTypeText(row.type) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="created_at" label="提交时间" width="220" align="center">
          <template #default="{ row }">
            <!--
            <span class="create-time">{{ formatTime(row.created_at) || '-' }}</span>
          -->
            <span class="meta-value">{{ formatTime(row.created_at) }}</span>
          </template>
        </el-table-column>


        <el-table-column prop="status" label="状态" width="160" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              class="status-tag"
              effect="light"
            >
              <el-icon><CircleCheck v-if="row.status === 'PROCESSED'" /><Clock v-else-if="row.status === 'PENDING'" /><Close v-else /></el-icon>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                size="small"
                type="primary"
                :plain="true"
                class="action-btn view-btn"
                @click.stop="showFeedbackDetail(row)"
              >
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button
                v-if="row.status === 'PENDING'"
                size="small"
                type="success"
                :plain="true"
                class="action-btn reply-btn"
                @click.stop="showFeedbackDetail(row)"
              >
                <el-icon><ChatDotRound /></el-icon>
                回复
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      width="700px"
      class="feedback-detail-dialog"
      :close-on-click-modal="false"
    >
      <template #header>
        <div class="dialog-header">
          <div class="dialog-title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="dialog-title-text">反馈详情</span>
        </div>
      </template>

      <div class="dialog-body">
        <div class="feedback-info">
          <div class="info-user">
            <el-avatar :size="48" :src="currentFeedback.userAvatar">
              {{ (currentFeedback.userName || '未知').charAt(0) }}
            </el-avatar>
            <div class="user-details">
              <div class="user-name">{{ currentFeedback.userName || '未知用户' }}</div>
              </div>
          </div>
          
          <div class="info-meta">
            <div class="meta-item">
              <span class="meta-label">反馈类型：</span>
              <span class="meta-value">{{ getFeedbackTypeText(currentFeedback.type) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">提交时间：</span>
              <span class="meta-value">{{ formatTime(currentFeedback.createdAt) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">状态：</span>
              <span class="meta-value">
                <el-tag :type="currentFeedback.status === 'PENDING' ? 'warning' : currentFeedback.status === 'PROCESSED' ? 'success' : 'info'">
                  {{ getStatusText(currentFeedback.status) }}
                </el-tag>
              </span>
            </div>
          </div>
        </div>

        <div class="feedback-content-section">
          <h4 class="section-title">反馈内容</h4>
          <div class="feedback-content">
            {{ currentFeedback.content }}
          </div>
        </div>

        <div class="admin-reply-section" v-if="currentFeedback.adminReply">
          <h4 class="section-title">管理员回复</h4>
          <div class="reply-content">
            {{ currentFeedback.adminReply }}
          </div>
        </div>

        <div class="reply-form" v-if="currentFeedback.status === 'PENDING'">
          <h4 class="section-title">回复反馈</h4>
          <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef">
            <el-form-item prop="replyContent">
              <el-input
                v-model="replyForm.replyContent"
                type="textarea"
                :rows="4"
                placeholder="请输入回复内容..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="detailDialogVisible = false" class="cancel-btn">关闭</el-button>
          <el-button 
            v-if="currentFeedback.status === 'PENDING'" 
            type="primary" 
            size="large" 
            @click="handleReply" 
            class="submit-btn"
            :loading="replying"
          >
            <el-icon><Check /></el-icon>
            {{ replying ? '提交中...' : '提交回复' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, Check, Loading, ChatDotRound, CircleCheck, Clock, Close, View } from '@element-plus/icons-vue';
import { feedbackAPI } from '../../api';

const feedbacks = ref([]);
const loading = ref(true);
const searchKeyword = ref('');
const filterStatus = ref('');
const filterType = ref('');
const detailDialogVisible = ref(false);
const currentFeedback = ref({});
const replyFormRef = ref(null);
const replying = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 统计信息
const totalFeedbacks = computed(() => feedbacks.value.length);
const pendingFeedbacks = computed(() => feedbacks.value.filter(f => f.status === 'PENDING').length);
const resolvedFeedbacks = computed(() => feedbacks.value.filter(f => f.status === 'RESOLVED').length);

// 过滤后的反馈列表
const filteredFeedbackList = computed(() => {
  let result = feedbacks.value;
  
  // 按搜索关键词过滤
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(feedback =>
      feedback.title.toLowerCase().includes(keyword) ||
      feedback.content.toLowerCase().includes(keyword) ||
      (feedback.userName && feedback.userName.toLowerCase().includes(keyword))
    );
  }
  
  // 按状态过滤
  if (filterStatus.value) {
    result = result.filter(feedback => feedback.status === filterStatus.value);
  }
  
  // 按类型过滤
  if (filterType.value) {
    result = result.filter(feedback => feedback.type === filterType.value);
  }
  
  return result;
});

// 回复表单
const replyForm = ref({
  replyContent: ''
});

// 回复验证规则
const replyRules = {
  replyContent: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 5, message: '回复内容至少5个字符', trigger: 'blur' }
  ]
};

// 获取反馈类型样式
const getFeedbackTypeClass = (type) => {
  const typeMap = {
    'SUGGESTION': 'success',
    'COMPLAINT': 'danger',
    'QUESTION': 'info'
  };
  return typeMap[type] || 'info';
};

// 获取反馈类型文本
const getFeedbackTypeText = (type) => {
  const typeMap = {
    'SUGGESTION': '建议',
    'COMPLAINT': '投诉',
    'QUESTION': '咨询'
  };
  return typeMap[type] || '未知';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'PROCESSED': '已处理',
    'CLOSED': '已关闭'
  };
  return statusMap[status] || '待处理';
};

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'PROCESSED': 'success',
    'PENDING': 'warning',
    'CLOSED': 'info'
  };
  return map[status] || 'info';
};

// 获取内容预览
const getContentPreview = (content) => {
  if (!content) return '';
  return content.length > 50 ? content.substring(0, 50) + '...' : content;
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  try {
    return new Date(time).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return time;
  }
};

// 表格样式
const headerCellStyle = {
  backgroundColor: '#f8fafc',
  color: '#4a5568',
  fontWeight: '600',
  borderBottom: '1px solid #e2e8f0'
};

const cellStyle = {
  borderBottom: '1px solid #e2e8f0',
  padding: '16px 12px'
};

// 格式化日期
// formatDate 函数已不再需要，统一使用 formatTime 函数

// 显示反馈详情
const showFeedbackDetail = (feedback) => {
  currentFeedback.value = { ...feedback };
  replyForm.value.replyContent = feedback.adminReply || '';
  detailDialogVisible.value = true;
};

// 处理回复
const handleReply = async () => {
  if (!replyFormRef.value) return;
  
  try {
    await replyFormRef.value.validate();
    replying.value = true;
    
    // 调用API回复反馈
    await feedbackAPI.replyFeedback(currentFeedback.value.id, {
      replyContent: replyForm.value.replyContent
    });
    
    ElMessage.success('回复成功');
    detailDialogVisible.value = false;
    await refreshData();
  } catch (error) {
    console.error('回复失败:', error);
    ElMessage.error('回复失败');
  } finally {
    replying.value = false;
  }
};

// 刷新数据
const refreshData = async () => {
  try {
    loading.value = true;
    // 调用API时传递分页参数
    const response = await feedbackAPI.getFeedbacks(currentPage.value - 1, pageSize.value);
    // 后端返回的是分页对象，需要提取content数组
    if (response.data) {
      feedbacks.value = response.data.content || [];
      total.value = response.data.totalElements || 0;
    } else {
      feedbacks.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取反馈列表失败:', error);
    ElMessage.error('获取反馈列表失败');
    feedbacks.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  refreshData(); // 重新加载数据
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  refreshData(); // 重新加载数据
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  refreshData();
};

// 清除搜索
const handleClearSearch = () => {
  searchKeyword.value = '';
  currentPage.value = 1;
  refreshData();
};

// 处理行点击
const handleRowClick = (row) => {
  showFeedbackDetail(row);
};

// 监听筛选条件变化
watch([filterStatus, filterType], () => {
  currentPage.value = 1;
  refreshData();
});

onMounted(() => {
  refreshData();
});
</script>

<style scoped>
.feedback-page {
  padding: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  min-height: calc(100vh - 88px);
}

/* 页面标题区域 */
.page-header {
  position: relative;
  background: #ffffff;
  margin: 0 0 24px 0;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
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
  padding: 32px 40px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  display: flex;
  align-items: center;
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.title-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.title-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.page-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0 0 0 64px;
  font-weight: 400;
}

/* 工具栏 */
.toolbar {
  margin-bottom: 24px;
  padding: 0;
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.search-section {
  flex: 1;
  min-width: 300px;
}

.search-input {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  height: 48px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1), 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__inner) {
  font-size: 15px;
  height: 46px;
  font-weight: 500;
}

.filter-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-select {
  width: 140px;
}

.filter-select :deep(.el-input__wrapper) {
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  height: 48px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-select :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.filter-select :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1), 0 4px 6px rgba(0, 0, 0, 0.1);
}

.refresh-button {
  border-radius: 12px;
  font-weight: 600;
  height: 48px;
  padding: 0 20px;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.refresh-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  width: 100%;
}

.feedback-table {
  width: 100%;
}

.feedback-table :deep(.el-table) {
  width: 100% !important;
}

.feedback-table :deep(.el-table__header-wrapper) {
  width: 100% !important;
}

.feedback-table :deep(.el-table__body-wrapper) {
  width: 100% !important;
}

.feedback-table :deep(.el-table__header) {
  width: 100% !important;
}

.feedback-table :deep(.el-table__body) {
  width: 100% !important;
}

.feedback-title .title {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.feedback-title .content-preview {
  font-size: 12px;
  color: #718096;
  margin-top: 2px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info .username {
  color: #4a5568;
  font-size: 14px;
}

.create-time {
  color: #718096;
  font-size: 13px;
}

/* 状态标签 */
.status-tag {
  border-radius: 6px;
  font-weight: 500;
  padding: 4px 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 类型标签 */
.type-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.type-badge.success {
  background: #f0f9ff;
  color: #409eff;
  border: 1px solid #d9ecff;
}

.type-badge.danger {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

.type-badge.info {
  background: #f4f4f5;
  color: #909399;
  border: 1px solid #d3d4d6;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 6px 12px;
  transition: all 0.3s ease;
}

.view-btn {
  border-color: #409eff;
  color: #409eff;
}

.view-btn:hover {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border-color: transparent;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.reply-btn {
  border-color: #67c23a;
  color: #67c23a;
}

.reply-btn:hover {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  border-color: transparent;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.pagination-container {
  padding: 20px;
  display: flex;
  justify-content: center;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
}

/* 对话框样式 */
.feedback-detail-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.feedback-detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
  margin: 0;
}

.feedback-detail-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.feedback-detail-dialog :deep(.el-dialog__headerbtn) {
  top: 24px;
  right: 24px;
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dialog-title-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff, #67c23a);
  border-radius: 6px;
  color: white;
}

.dialog-title-icon svg {
  width: 18px;
  height: 18px;
}

.dialog-title-text {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.feedback-detail-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.feedback-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}

.info-user {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.user-role {
  font-size: 14px;
  color: #909399;
}

.info-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.meta-value {
  font-size: 14px;
  color: #303133;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.feedback-content-section,
.admin-reply-section,
.reply-form {
  margin-bottom: 24px;
}

.feedback-content,
.reply-content {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
}

.reply-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.reply-form :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.reply-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.feedback-detail-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  border-radius: 8px;
  font-weight: 500;
  padding: 10px 20px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
}

.cancel-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 10px 20px;
}

.submit-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 10px 20px;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
}

/* 动画效果 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 24px 20px 16px;
    flex-direction: column;
    align-items: flex-start;
  }

  .page-subtitle {
    margin: 8px 0 0 0;
  }

  .page-title {
    font-size: 24px;
  }

  .title-icon {
    width: 40px;
    height: 40px;
  }

  .title-icon svg {
    width: 20px;
    height: 20px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .search-section {
    min-width: auto;
  }

  .filter-section {
    flex-wrap: wrap;
    justify-content: space-between;
  }

  .filter-select {
    flex: 1;
    min-width: calc(50% - 6px);
  }

  .refresh-button {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
    gap: 6px;
  }

  .action-btn {
    width: 100%;
  }

  .feedback-detail-dialog :deep(.el-dialog) {
    width: 90% !important;
    margin: 5vh auto;
  }

  .info-meta {
    grid-template-columns: 1fr;
  }

  .dialog-footer {
    flex-direction: column;
  }

  .dialog-footer .el-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .filter-select {
    min-width: 100%;
  }

  .feedback-table :deep(.el-table__body-wrapper) {
    overflow-x: auto;
  }
}
</style>