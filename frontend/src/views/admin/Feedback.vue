<template>
  <div class="feedback-admin-page">
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

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon primary">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalFeedbacks }}</div>
            <div class="stat-label">总反馈数</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon warning">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 16H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ pendingFeedbacks }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon success">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M22 4L12 14.01l-3-3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ resolvedFeedbacks }}</div>
            <div class="stat-label">已解决</div>
          </div>
        </div>
      </div>

      <!-- 反馈列表 -->
      <div class="content-card">
        <div class="card-header">
          <h3 class="card-title">用户反馈列表</h3>
          <div class="card-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索反馈标题或内容..."
              class="search-input"
              clearable
              style="width: 240px; margin-right: 12px;"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="filterStatus" placeholder="状态筛选" style="width: 120px; margin-right: 12px;">
              <el-option label="全部" value="" />
              <el-option label="待处理" value="PENDING" />
              <el-option label="已处理" value="RESOLVED" />
              <el-option label="已关闭" value="CLOSED" />
            </el-select>
            <el-select v-model="filterType" placeholder="类型筛选" style="width: 120px; margin-right: 12px;">
              <el-option label="全部" value="" />
              <el-option label="建议" value="SUGGESTION" />
              <el-option label="投诉" value="COMPLAINT" />
              <el-option label="咨询" value="QUESTION" />
            </el-select>
            <el-button type="primary" size="small" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>

        <div class="card-body">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>

          <!-- 空状态 -->
          <div v-else-if="filteredFeedbacks.length === 0" class="empty-state">
            <div class="empty-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <p class="empty-text">暂无反馈记录</p>
            <p class="empty-description">当前没有用户提交的反馈</p>
          </div>

          <!-- 反馈列表 -->
          <div v-else class="feedback-list">
            <div 
              class="feedback-card" 
              v-for="feedback in filteredFeedbacks" 
              :key="feedback.id"
              @click="showFeedbackDetail(feedback)"
            >
              <div class="card-header">
                <div class="feedback-type">
                  <span class="type-badge" :class="getFeedbackTypeClass(feedback.type)">
                    {{ getFeedbackTypeText(feedback.type) }}
                  </span>
                </div>
                <div class="feedback-status">
                  <el-tag 
                    :type="feedback.status === 'PENDING' ? 'warning' : feedback.status === 'RESOLVED' ? 'success' : 'info'" 
                    effect="light" 
                    size="small"
                  >
                    {{ getStatusText(feedback.status) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="card-body">
                <div class="feedback-user">
                  <el-avatar :size="32" :src="feedback.userAvatar" />
                  <div class="user-info">
                    <div class="username">{{ feedback.username }}</div>
                    <div class="real-name">{{ feedback.realName }}</div>
                  </div>
                </div>
                
                <h4 class="feedback-title">{{ feedback.title }}</h4>
                <div class="feedback-content">
                  {{ feedback.content }}
                </div>
                
                <div class="feedback-details">
                  <div class="detail-item">
                    <div class="detail-label">提交时间</div>
                    <div class="detail-value">{{ formatDate(feedback.createTime) }}</div>
                  </div>
                  <div class="detail-item" v-if="feedback.updateTime">
                    <div class="detail-label">更新时间</div>
                    <div class="detail-value">{{ formatDate(feedback.updateTime) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页组件 -->
          <div class="pagination-container" v-if="filteredFeedbacks.length > 0">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="filteredFeedbacks.length"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      width="600px"
      class="feedback-detail-dialog"
      :show-close="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">反馈详情</span>
      </div>

      <div class="dialog-body">
        <div class="feedback-info">
          <div class="info-user">
            <el-avatar :size="48" :src="currentFeedback.userAvatar" />
            <div class="user-details">
              <div class="user-name">{{ currentFeedback.realName || currentFeedback.username }}</div>
              <div class="user-role">{{ currentFeedback.userRole }}</div>
            </div>
          </div>
          
          <div class="info-meta">
            <div class="meta-item">
              <span class="meta-label">反馈类型：</span>
              <span class="meta-value">{{ getFeedbackTypeText(currentFeedback.type) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">提交时间：</span>
              <span class="meta-value">{{ formatDate(currentFeedback.createTime) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">状态：</span>
              <span class="meta-value">
                <el-tag :type="currentFeedback.status === 'PENDING' ? 'warning' : currentFeedback.status === 'RESOLVED' ? 'success' : 'info'">
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
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, Check, Loading, ChatDotRound } from '@element-plus/icons-vue';
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

// 统计信息
const totalFeedbacks = computed(() => feedbacks.value.length);
const pendingFeedbacks = computed(() => feedbacks.value.filter(f => f.status === 'PENDING').length);
const resolvedFeedbacks = computed(() => feedbacks.value.filter(f => f.status === 'RESOLVED').length);

// 过滤后的反馈列表
const filteredFeedbacks = computed(() => {
  let result = feedbacks.value;
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(f => 
      f.title.toLowerCase().includes(keyword) || 
      f.content.toLowerCase().includes(keyword)
    );
  }
  
  if (filterStatus.value) {
    result = result.filter(f => f.status === filterStatus.value);
  }
  
  if (filterType.value) {
    result = result.filter(f => f.type === filterType.value);
  }
  
  return result;
});

// 分页数据
const paginatedFeedbacks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredFeedbacks.value.slice(start, end);
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
    'RESOLVED': '已解决',
    'CLOSED': '已关闭'
  };
  return statusMap[status] || '未知';
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-';
  return new Date(dateString).toLocaleString('zh-CN');
};

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
    const response = await feedbackAPI.getFeedbacks();
    feedbacks.value = response.data || [];
  } catch (error) {
    console.error('获取反馈列表失败:', error);
    ElMessage.error('获取反馈列表失败');
  } finally {
    loading.value = false;
  }
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
};

onMounted(() => {
  refreshData();
});
</script>

<style scoped>
.feedback-admin-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.header-decoration {
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a);
  border-radius: 2px;
  margin-bottom: 16px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.title-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff, #67c23a);
  border-radius: 8px;
  color: white;
}

.title-icon svg {
  width: 24px;
  height: 24px;
}

.page-subtitle {
  color: #909399;
  font-size: 16px;
  margin: 0;
}

.page-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.primary {
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: white;
}

.stat-icon.warning {
  background: linear-gradient(135deg, #e6a23c, #f56c6c);
  color: white;
}

.stat-icon.success {
  background: linear-gradient(135deg, #67c23a, #409eff);
  color: white;
}

.stat-icon svg {
  width: 32px;
  height: 32px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.content-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.card-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.search-input {
  width: 240px;
}

.card-body {
  padding: 24px;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px;
  color: #909399;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
  background: #f5f7fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon svg {
  width: 40px;
  height: 40px;
  color: #c0c4cc;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 8px;
}

.empty-description {
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
}

.feedback-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.feedback-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.feedback-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.feedback-card .card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.feedback-card .card-body {
  padding: 20px;
}

.feedback-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.real-name {
  font-size: 12px;
  color: #909399;
}

.feedback-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  line-height: 1.4;
}

.feedback-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.feedback-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.detail-value {
  font-size: 12px;
  color: #303133;
  font-weight: 600;
}

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

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* 对话框样式 */
.feedback-detail-dialog .dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
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

.dialog-body {
  max-height: 60vh;
  overflow-y: auto;
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
  border-radius: 6px;
  padding: 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.reply-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .feedback-admin-page {
    padding: 16px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .feedback-list {
    grid-template-columns: 1fr;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .card-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .info-meta {
    grid-template-columns: 1fr;
  }
}
</style>