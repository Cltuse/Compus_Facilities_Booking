<template>
  <div class="facility-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          意见反馈
        </h1>
        <p class="page-subtitle">提交您的建议、投诉或咨询，查看管理员回复</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="page-content">
      <!-- 反馈提交表单 -->
      <div class="content-card">
        <div class="card-header">
          <h3 class="card-title">提交反馈</h3>
        </div>
        
        <div class="card-body">
          <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="100px">
            <el-form-item label="反馈类型" prop="type">
              <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型" style="width: 100%">
                <el-option label="建议" value="SUGGESTION" />
                <el-option label="投诉" value="COMPLAINT" />
                <el-option label="咨询" value="QUESTION" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="反馈标题" prop="title">
              <el-input v-model="feedbackForm.title" placeholder="请输入反馈标题" maxlength="200" show-word-limit />
            </el-form-item>
            
            <el-form-item label="反馈内容" prop="content">
              <el-input 
                v-model="feedbackForm.content" 
                type="textarea" 
                :rows="6" 
                placeholder="请详细描述您的反馈内容"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" size="large" @click="submitFeedback" :loading="submitting">
                <el-icon><Check /></el-icon>
                {{ submitting ? '提交中...' : '提交反馈' }}
              </el-button>
              <el-button size="large" @click="resetForm">
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
          <h3 class="card-title">我的反馈</h3>
          <div class="card-actions">
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
              class="feedback-card" 
              v-for="feedback in feedbacks" 
              :key="feedback.id"
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
                <h4 class="feedback-title">{{ feedback.title }}</h4>
                <div class="feedback-content">
                  {{ feedback.content }}
                </div>
                
                <div class="feedback-details">
                  <div class="detail-item">
                    <div class="detail-label">提交时间</div>
                    <div class="detail-value">{{ formatDateTime(feedback.createTime) }}</div>
                  </div>
                  <div class="detail-item" v-if="feedback.updateTime">
                    <div class="detail-label">更新时间</div>
                    <div class="detail-value">{{ formatDateTime(feedback.updateTime) }}</div>
                  </div>
                </div>
                
                <div class="admin-reply" v-if="feedback.adminReply">
                  <div class="reply-header">
                    <el-icon><ChatDotRound /></el-icon>
                    <span class="reply-title">管理员回复</span>
                  </div>
                  <div class="reply-content">
                    {{ feedback.adminReply }}
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
      getStatusClass,
      getStatusText,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.feedback-page {
  padding: 0;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 0;
  position: relative;
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  transform: translate(30%, -30%);
}

.header-content {
  position: relative;
  z-index: 2;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  display: flex;
  align-items: center;
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.title-icon {
  width: 48px;
  height: 48px;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
}

.title-icon svg {
  width: 24px;
  height: 24px;
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin: 0;
}

.page-content {
  max-width: 1200px;
  margin: -60px auto 40px;
  padding: 0 20px;
  position: relative;
  z-index: 3;
}

.content-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 30px;
}

.card-header {
  padding: 25px 30px;
  border-bottom: 1px solid #eaeaea;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.card-body {
  padding: 30px;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #7f8c8d;
}

.loading-container .el-icon {
  margin-right: 10px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  color: #bdc3c7;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  font-size: 1.2rem;
  color: #7f8c8d;
  margin: 0 0 10px 0;
}

.empty-description {
  color: #95a5a6;
  margin: 0;
}

.feedback-list {
  space-y: 4;
}

.feedback-item {
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.feedback-item:hover {
  border-color: #3498db;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.1);
}

.feedback-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  gap: 15px;
}

.feedback-type {
  flex-shrink: 0;
}

.type-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.type-suggestion { background: #48dbfb; color: white; }
.type-complaint { background: #ff6b6b; color: white; }
.type-question { background: #1dd1a1; color: white; }

.feedback-title {
  flex: 1;
}

.feedback-title h4 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.feedback-status {
  flex-shrink: 0;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-pending { background: #feca57; color: white; }
.status-processed { background: #1dd1a1; color: white; }

.feedback-content {
  margin-bottom: 15px;
}

.feedback-content p {
  color: #2c3e50;
  line-height: 1.6;
  margin: 0;
}

.feedback-meta {
  display: flex;
  gap: 20px;
  font-size: 0.85rem;
  color: #7f8c8d;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.feedback-reply {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  border-left: 4px solid #3498db;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

.reply-time {
  font-size: 0.8rem;
  color: #7f8c8d;
  margin-left: auto;
}

.reply-content {
  color: #2c3e50;
  line-height: 1.6;
  margin-bottom: 8px;
}

.reply-author {
  text-align: right;
  font-size: 0.9rem;
  color: #7f8c8d;
  font-style: italic;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .feedback-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .feedback-status {
    align-self: flex-end;
  }
  
  .reply-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .reply-time {
    margin-left: 0;
  }
}
</style>