<template>
  <div class="feedback-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="8" y1="9" x2="16" y2="9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="8" y1="13" x2="14" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          意见反馈
        </h1>
        <p class="page-subtitle">提交您的建议、投诉或咨询，查看管理员回复</p>
      </div>
    </div>

    <!-- 反馈提交表单 -->
    <div class="content-card">
      <div class="card-header">
        <h3 class="card-title">
          <div class="card-title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          提交反馈
        </h3>
      </div>
      
      <div class="card-body">
        <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="100px" class="feedback-form">
          <el-form-item label="反馈类型" prop="type">
            <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型" class="feedback-select">
              <el-option label="建议" value="SUGGESTION" />
              <el-option label="投诉" value="COMPLAINT" />
              <el-option label="咨询" value="QUESTION" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="反馈标题" prop="title">
            <el-input v-model="feedbackForm.title" placeholder="请输入反馈标题" maxlength="200" show-word-limit class="feedback-input" />
          </el-form-item>
          
          <el-form-item label="反馈内容" prop="content">
            <el-input 
              v-model="feedbackForm.content" 
              type="textarea" 
              :rows="6" 
              placeholder="请详细描述您的反馈内容"
              maxlength="1000"
              show-word-limit
              class="feedback-textarea"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" size="large" @click="submitFeedback" :loading="submitting" class="submit-btn">
              <el-icon><Check /></el-icon>
              {{ submitting ? '提交中...' : '提交反馈' }}
            </el-button>
            <el-button size="large" @click="resetForm" class="reset-btn">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!-- 我的反馈列表 -->
    <div class="content-card">
      <div class="card-header">
        <h3 class="card-title">
          <div class="card-title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          我的反馈
        </h3>
        <div class="card-actions">
          <el-button type="primary" size="small" @click="refreshData" class="refresh-btn">
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
        <div v-else-if="feedbacks.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <p class="empty-text">暂无反馈记录</p>
          <p class="empty-description">您还没有提交过任何反馈，欢迎提出宝贵意见！</p>
        </div>

        <!-- 反馈列表 -->
        <div v-else class="feedback-list">
          <div 
            class="feedback-item" 
            v-for="feedback in feedbacks" 
            :key="feedback.id"
          >
            <div class="feedback-header">
              <div class="feedback-type">
                <el-tag 
                  :type="getFeedbackTypeTagType(feedback.type)" 
                  class="type-tag" 
                  effect="light"
                >
                  <el-icon>
                    <ChatDotRound v-if="feedback.type === 'SUGGESTION'" />
                    <Warning v-else-if="feedback.type === 'COMPLAINT'" />
                    <QuestionFilled v-else />
                  </el-icon>
                  {{ getFeedbackTypeText(feedback.type) }}
                </el-tag>
              </div>
              <div class="feedback-status">
                <el-tag 
                  :type="getStatusType(feedback.status)" 
                  class="status-tag" 
                  effect="light"
                >
                  <el-icon>
                    <Clock v-if="feedback.status === 'PENDING'" />
                    <CircleCheck v-else-if="feedback.status === 'RESOLVED'" />
                    <CircleClose v-else />
                  </el-icon>
                  {{ getStatusText(feedback.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="feedback-body">
              <h4 class="feedback-title">{{ feedback.title }}</h4>
              <div class="feedback-content">
                {{ feedback.content }}
              </div>
              
              <div class="feedback-meta">
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>提交时间：{{ formatDateTime(feedback.createTime) }}</span>
                </div>
                <div class="meta-item" v-if="feedback.updateTime">
                  <el-icon><Clock /></el-icon>
                  <span>更新时间：{{ formatDateTime(feedback.updateTime) }}</span>
                </div>
              </div>
              
              <div class="admin-reply" v-if="feedback.reply && feedback.reply.trim() !== ''">
                <div class="reply-header">
                  <el-icon><ChatDotRound /></el-icon>
                  <span class="reply-title">管理员回复</span>
                </div>
                <div class="reply-content">
                  {{ feedback.reply }}
                </div>
                <div class="feedback-meta" v-if="feedback.replyTime">
                  <div class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    <span>回复时间：{{ formatDateTime(feedback.replyTime) }}</span>
                  </div>
                  <div class="meta-item" v-if="feedback.replyByName">
                    <el-icon><User /></el-icon>
                    <span>回复人：{{ feedback.replyByName }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="feedbacks.length > 0" class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { userClientAPI } from '@/api'
import { formatDateTime } from '@/utils/date'
import { Check, Refresh, Loading, ChatDotRound, Calendar, Clock, CircleCheck, CircleClose, Warning, QuestionFilled, User } from '@element-plus/icons-vue'

export default {
  name: 'Feedback',
  setup() {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.id

    // 表单相关
    const feedbackFormRef = ref()
    const feedbackForm = reactive({
      type: 'SUGGESTION',
      title: '',
      content: ''
    })

    const feedbackRules = {
      type: [
        { required: true, message: '请选择反馈类型', trigger: 'change' }
      ],
      title: [
        { required: true, message: '请输入反馈标题', trigger: 'blur' },
        { min: 5, max: 200, message: '标题长度在 5 到 200 个字符', trigger: 'blur' }
      ],
      content: [
        { required: true, message: '请输入反馈内容', trigger: 'blur' },
        { min: 10, max: 1000, message: '内容长度在 10 到 1000 个字符', trigger: 'blur' }
      ]
    }

    // 列表相关
    const feedbacks = ref([])
    const loading = ref(false)
    const submitting = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    // 方法
    const loadFeedbacks = async () => {
      if (!userId) {
        ElMessage.error('用户信息获取失败')
        return
      }

      loading.value = true
      try {
        const response = await userClientAPI.getMyFeedbacks(userId, currentPage.value - 1, pageSize.value)
        if (response.code === 200) {
          feedbacks.value = response.data.feedbacks || []
          total.value = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取反馈列表失败')
        }
      } catch (error) {
        console.error('获取反馈列表失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        loading.value = false
      }
    }

    const submitFeedback = async () => {
      if (!feedbackFormRef.value) return

      try {
        const valid = await feedbackFormRef.value.validate()
        if (!valid) return

        if (!userId) {
          ElMessage.error('用户信息获取失败')
          return
        }

        submitting.value = true
        const submitData = {
          ...feedbackForm,
          userId: userId
        }

        const response = await userClientAPI.submitFeedback(submitData)
        if (response.code === 200) {
          ElMessage.success('反馈提交成功')
          resetForm()
          loadFeedbacks() // 刷新列表
        } else {
          ElMessage.error(response.message || '反馈提交失败')
        }
      } catch (error) {
        console.error('提交反馈失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        submitting.value = false
      }
    }

    const resetForm = () => {
      if (feedbackFormRef.value) {
        feedbackFormRef.value.resetFields()
        feedbackForm.type = 'SUGGESTION'
      }
    }

    const refreshData = () => {
      currentPage.value = 1
      loadFeedbacks()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadFeedbacks()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadFeedbacks()
    }

    const getFeedbackTypeClass = (type) => {
      const typeMap = {
        'SUGGESTION': 'type-suggestion',
        'COMPLAINT': 'type-complaint',
        'QUESTION': 'type-question'
      }
      return typeMap[type] || 'type-suggestion'
    }

    const getFeedbackTypeText = (type) => {
      const textMap = {
        'SUGGESTION': '建议',
        'COMPLAINT': '投诉',
        'QUESTION': '咨询'
      }
      return textMap[type] || '建议'
    }

    const getStatusClass = (status) => {
      const statusMap = {
        'PENDING': 'status-pending',
        'PROCESSED': 'status-processed'
      }
      return statusMap[status] || 'status-pending'
    }

    const getStatusText = (status) => {
      const textMap = {
        'PENDING': '待处理',
        'PROCESSED': '已处理'
      }
      return textMap[status] || '待处理'
    }

    // 生命周期
    onMounted(() => {
      loadFeedbacks()
    })

    const getFeedbackTypeTagType = (type) => {
      const map = {
        'SUGGESTION': 'primary',
        'COMPLAINT': 'danger',
        'QUESTION': 'warning'
      }
      return map[type] || 'info'
    }

    const getStatusType = (status) => {
      const map = {
        'PENDING': 'warning',
        'RESOLVED': 'success',
        'REJECTED': 'danger'
      }
      return map[status] || 'info'
    }

    return {
      feedbackFormRef,
      feedbackForm,
      feedbackRules,
      feedbacks,
      loading,
      submitting,
      currentPage,
      pageSize,
      total,
      
      submitFeedback,
      resetForm,
      refreshData,
      handleSizeChange,
      handleCurrentChange,
      getFeedbackTypeClass,
      getFeedbackTypeText,
      getFeedbackTypeTagType,
      getStatusClass,
      getStatusText,
      getStatusType,
      formatDateTime
    }
  }
}
</script>

<style scoped>
/* 页面背景 */
.feedback-page {
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

@keyframes gradient-shimmer {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
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
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.title-icon svg {
  width: 24px;
  height: 24px;
}

.page-subtitle {
  font-size: 16px;
  color: #718096;
  margin: 0;
  font-weight: 500;
}

/* 内容卡片 */
.content-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.content-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.card-title-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.card-title-icon svg {
  width: 18px;
  height: 18px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-body {
  padding: 24px;
}

/* 表单样式 */
.feedback-form {
  max-width: 800px;
  margin: 0 auto;
}

.feedback-select, .feedback-input, .feedback-textarea {
  width: 100%;
}

.feedback-select :deep(.el-input__wrapper),
.feedback-input :deep(.el-input__wrapper),
.feedback-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  background: #f7fafc;
}

.feedback-select :deep(.el-input__wrapper:hover),
.feedback-input :deep(.el-input__wrapper:hover),
.feedback-textarea :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feedback-select :deep(.el-input__wrapper.is-focus),
.feedback-input :deep(.el-input__wrapper.is-focus),
.feedback-textarea :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.submit-btn, .reset-btn, .refresh-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 10px 20px;
  transition: all 0.3s ease;
}

.submit-btn {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.reset-btn, .refresh-btn {
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  color: #4a5568;
}

.reset-btn:hover, .refresh-btn:hover {
  background: #edf2f7;
  border-color: #cbd5e0;
  color: #2d3748;
}

/* 加载和空状态 */
.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #718096;
  font-size: 16px;
}

.loading-container .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  color: #cbd5e0;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  font-size: 18px;
  color: #718096;
  margin: 0 0 10px 0;
  font-weight: 500;
}

.empty-description {
  color: #a0aec0;
  margin: 0;
  font-size: 14px;
}

/* 反馈列表 */
.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.feedback-item:hover {
  border-color: #409eff;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.1);
  transform: translateY(-2px);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.type-tag, .status-tag {
  border-radius: 20px;
  font-weight: 500;
  padding: 6px 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.feedback-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feedback-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  line-height: 1.5;
}

.feedback-content {
  color: #4a5568;
  line-height: 1.6;
  font-size: 14px;
}

.feedback-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #718096;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.admin-reply {
  background: #f7fafc;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #409eff;
  margin-top: 8px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 8px;
}

.reply-title {
  font-size: 14px;
}

.reply-content {
  color: #4a5568;
  line-height: 1.6;
  font-size: 14px;
}

/* 分页 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .feedback-page {
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .feedback-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .feedback-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>