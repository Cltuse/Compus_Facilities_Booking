<template>
  <div class="violation-records-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="12" y1="9" x2="12" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="12" y1="17" x2="12.01" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          我的违规记录
        </h1>
        <p class="page-subtitle">查看您的历史违规记录和扣分情况</p>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="search-container">
        <el-input
          v-model="searchDescription"
          placeholder="请输入违规描述进行搜索"
          class="search-input"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="toolbar-actions">
        <el-button type="primary" size="small" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon warning">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <line x1="12" y1="9" x2="12" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <line x1="12" y1="17" x2="12.01" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ activeViolations }}</div>
          <div class="stat-label">生效中违规</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon danger">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M15 9L9 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 9L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalDeduction }}</div>
          <div class="stat-label">累计扣分</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon success">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ currentCreditScore }}</div>
          <div class="stat-label">当前信用分</div>
        </div>
      </div>
    </div>

    <!-- 违规记录表格 -->
    <div class="table-container">
      <el-table
        :data="paginatedViolationList"
        class="violation-table"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @row-click="handleRowClick"
      >
        <el-table-column prop="violationType" label="违规类型" width="150" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getViolationTypeTagType(row.violationType)"
              class="violation-type-tag"
              effect="light"
            >
              <el-icon>
                <CircleClose v-if="row.violationType === 'NO_SHOW'" />
                <Clock v-else-if="row.violationType === 'OVERDUE'" />
                <Close v-else-if="row.violationType === 'CANCEL_FREQ'" />
                <Warning v-else-if="row.violationType === 'DAMAGE'" />
                <QuestionFilled v-else />
              </el-icon>
              {{ getViolationTypeText(row.violationType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="违规描述" min-width="300">
          <template #default="{ row }">
            <div class="violation-description">
              <div class="description-text">{{ row.description }}</div>
            </div>
          </template>
        </el-table-column>



        <el-table-column prop="reportedTime" label="上报时间" width="210">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatDateTime(row.reportedTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="penaltyPoints" label="处罚分" width="120" align="center">
          <template #default="{ row }">
            <div class="penalty-info">
              <span class="penalty-value">{{ row.penaltyPoints || 0 }}分</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="150" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              class="status-tag"
              effect="light"
            >
              <el-icon>
                <Clock v-if="row.status === 'PENDING'" />
                <CircleCheck v-else-if="row.status === 'PROCESSED'" />
                <CircleClose v-else />
              </el-icon>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                size="small"
                type="primary"
                :plain="true"
                class="action-btn view-btn"
                @click.stop="showViolationDetail(row)"
              >
                <el-icon><View /></el-icon>
                查看
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="filteredViolationList.length"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    </div>

    <!-- 违规记录详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="违规记录详情"
      width="600px"
      :before-close="handleCloseDetail"
    >
      <div v-if="currentViolation" class="violation-detail">
        <div class="detail-section">
          <h4>信用信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <label>当前信用分：</label>
              <span class="credit-score">{{ currentCreditScore }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <label>违规类型：</label>
              <span class="type-badge" :class="getViolationTypeClass(currentViolation.violationType)">
                {{ getViolationTypeText(currentViolation.violationType) }}
              </span>
            </div>
            <div class="detail-item">
              <label>上报人：</label>
              <span>{{ currentViolation.reporterName || '系统记录' }}</span>
            </div>
            <div class="detail-item">
              <label>上报时间：</label>
              <span>{{ formatDateTime(currentViolation.reportedTime) || '暂无数据' }}</span>
            </div>
            <div class="detail-item">
              <label>处罚分：</label>
              <span class="penalty-text">{{ currentViolation.penaltyPoints || 0 }}分</span>
            </div>
            <div class="detail-item">
              <label>状态：</label>
              <span class="status-text" :class="getStatusClass(currentViolation.status)">
                {{ getStatusText(currentViolation.status) }}
              </span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>违规描述</h4>
          <div class="description-content">
            {{ currentViolation.description }}
          </div>
        </div>

        <div v-if="currentViolation.facilityName" class="detail-section">
          <h4>关联设施</h4>
          <div class="facility-info">
            {{ currentViolation.facilityName }}
          </div>
        </div>

        <div v-if="currentViolation.expireTime" class="detail-section">
          <h4>有效期</h4>
          <div class="expire-info">
            有效期至：{{ formatDateTime(currentViolation.expireTime) }}
          </div>
        </div>

      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Calendar, Clock, CircleCheck, CircleClose, Close, Warning, QuestionFilled, View } from '@element-plus/icons-vue'
import { userClientAPI, violationAPI } from '../../api'
import { formatDateTime } from '@/utils/date'

const userInfo = ref({})
const violationList = ref([])
const currentViolation = ref({})
const detailDialogVisible = ref(false)
const searchDescription = ref('')
const currentCreditScore = ref(100)
const violationCount = ref(0)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    try {
      userInfo.value = JSON.parse(info)
      console.log('用户信息:', userInfo.value)
      if (userInfo.value.id) {
        loadMyViolations()
        loadCurrentCreditScore()
        loadViolationCount()
      } else {
        console.error('用户ID不存在')
        ElMessage.error('用户信息不完整，请重新登录')
      }
    } catch (error) {
      console.error('解析用户信息失败:', error)
      ElMessage.error('用户信息解析失败')
    }
  } else {
    console.error('未找到用户信息')
    ElMessage.error('请先登录')
  }
})

// 加载当前违规次数
const loadViolationCount = async () => {
  try {
    const res = await violationAPI.getUserViolationCount(userInfo.value.id)
    if (res && res.code === 200) {
      violationCount.value = res.data || 0
    }
  } catch (error) {
    console.error('加载违规次数失败:', error)
  }
}

// 加载当前信用分
const loadCurrentCreditScore = async () => {
  try {
    const res = await violationAPI.getUserCurrentCreditScore(userInfo.value.id)
    if (res && res.code === 200) {
      currentCreditScore.value = res.data || 100
    }
  } catch (error) {
    console.error('加载信用分失败:', error)
  }
}

const loadMyViolations = async () => {
  try {
    const res = await userClientAPI.getMyViolationRecords(userInfo.value.id)
    console.log('违规记录API响应:', res)
    
    // 根据后端实际返回的数据结构处理
    if (res && res.data) {
      if (Array.isArray(res.data)) {
        // 如果直接返回数组
        violationList.value = res.data
      } else if (res.data.violations && Array.isArray(res.data.violations)) {
        // 后端返回的数据结构：{ violations: [...], total: 20, page: 0, size: 10 }
        violationList.value = res.data.violations
      } else {
        // 其他情况，设置为空数组
        violationList.value = []
      }
    } else {
      violationList.value = []
    }
    console.log('加载违规记录成功:', violationList.value)
  } catch (error) {
    console.error('加载我的违规记录失败:', error)
    ElMessage.error('加载违规记录失败')
    violationList.value = []
  }
}

// 过滤后的违规记录列表（违规描述模糊搜索）
const filteredViolationList = computed(() => {
  let filtered = violationList.value

  if (searchDescription.value) {
    const searchTerm = searchDescription.value.toLowerCase()
    filtered = filtered.filter(item =>
      item.description && item.description.toLowerCase().includes(searchTerm)
    )
  }

  return filtered
})

// 分页后的违规记录列表
const paginatedViolationList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredViolationList.value.slice(start, end)
})

const processedViolationList = computed(() => {
  return violationList.value.filter(item => item.status === 'PROCESSED')
})

// 计算属性
const activeViolations = computed(() => {
  return processedViolationList.value.length
})

const totalDeduction = computed(() => {
  return processedViolationList.value.reduce((sum, v) => sum + (v.penaltyPoints || 0), 0)
})

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 表格样式配置
const headerCellStyle = {
  backgroundColor: '#f8fafc',
  color: '#2d3748',
  fontWeight: '600',
  fontSize: '14px',
  borderBottom: '2px solid #e2e8f0'
}

const cellStyle = ({ row, column, rowIndex, columnIndex }) => {
  return {
    padding: '16px 12px',
    borderBottom: '1px solid #f0f0f0',
    color: '#4a5568',
    fontSize: '14px'
  }
}

// 表格行点击处理
const handleRowClick = (row) => {
  showViolationDetail(row)
}

const showViolationDetail = (row) => {
  console.log('显示违规记录详情:', row)
  currentViolation.value = row
  detailDialogVisible.value = true
}

const handleCloseDetail = () => {
  detailDialogVisible.value = false
  currentViolation.value = {}
}

const refreshData = () => {
  currentPage.value = 1
  loadMyViolations()
  loadCurrentCreditScore()
  loadViolationCount()
}

// 违规类型相关方法
const getViolationTypeTagType = (type) => {
  const map = {
    'NO_SHOW': 'danger',
    'OVERDUE': 'warning',
    'CANCEL_FREQ': 'info',
    'DAMAGE': 'error',
    'OTHER': 'info'
  }
  return map[type] || 'info'
}

const getViolationTypeText = (type) => {
  const map = {
    'NO_SHOW': '爽约',
    'OVERDUE': '超时占用',
    'CANCEL_FREQ': '频繁取消',
    'DAMAGE': '损坏公物',
    'OTHER': '其他违规'
  }
  return map[type] || '未知类型'
}

// 状态相关方法
const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'PROCESSED': 'success',
    'REVOKED': 'info',
    'REJECTED': 'danger',
    'EXPIRED': 'info',
    'APPEALED': 'primary'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'PROCESSED': '已处理',
    'REVOKED': '已取消生效',
    'REJECTED': '已驳回',
    'EXPIRED': '已过期',
    'APPEALED': '申诉中'
  }
  return map[status] || status
}

const getStatusClass = (status) => {
  const map = {
    'PENDING': 'status-pending',
    'PROCESSED': 'status-processed',
    'REVOKED': 'status-revoked',
    'REJECTED': 'status-rejected',
    'EXPIRED': 'status-expired',
    'APPEALED': 'status-appealed'
  }
  return map[status] || 'status-default'
}

const getViolationTypeClass = (type) => {
  const map = {
    'NO_SHOW': 'type-no-show',
    'OVERDUE': 'type-overdue',
    'CANCEL_FREQ': 'type-cancel-freq',
    'DAMAGE': 'type-damage',
    'OTHER': 'type-other'
  }
  return map[type] || 'type-other'
}


</script>

<style scoped>
/* 页面背景 */
.violation-records-page {
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

/* 工具栏 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 8px;
}

.search-container {
  flex: 0 0 300px;
}

.search-input {
  width: 100%;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #ffc107 0%, #ffa000 100%);
  color: white;
}

.stat-icon.danger {
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1a202c;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  overflow: hidden;
  margin-bottom: 24px;
}

.violation-table {
  width: 100%;
  border: none;
}

.violation-table :deep(.el-table__header) {
  background: #f8fafc;
}

.violation-table :deep(.el-table__body tr:hover > td) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%) !important;
}

.violation-table :deep(.el-table__body tr:hover > td .cell) {
  color: #1a202c !important;
}

.violation-table :deep(.el-table__body td) {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 12px;
  transition: all 0.3s ease;
}

.violation-table :deep(.el-table__body td .cell) {
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}

/* 表格单元格内容样式 */
.violation-type-tag {
  font-weight: 600;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  font-size: 12px;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.violation-description {
  min-width: 0;
}

.description-text {
  font-size: 14px;
  color: #4a5568;
  line-height: 1.4;
  word-break: break-word;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #718096;
  font-size: 14px;
}

.deduction-info {
  text-align: center;
}

.deduction-value {
  font-size: 16px;
  font-weight: 600;
  color: #f44336;
}

.penalty-info {
  text-align: center;
}

.penalty-value {
  font-size: 16px;
  font-weight: 600;
  color: #ff9800;
}

.status-tag {
  font-weight: 600;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  font-size: 12px;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

/* 动画效果 */
@keyframes gradient-shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.stat-icon svg {
  width: 28px;
  height: 28px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
}

.stat-label {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-top: 5px;
}

.content-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
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

.violation-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.violation-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.violation-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.violation-card .card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.violation-card .card-body {
  padding: 20px;
}

.violation-card .card-footer {
  padding: 0 20px 20px;
}

.violation-type {
  flex: 1;
}

.type-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.type-no-show { background: #ff6b6b; color: white; }
.type-late-cancellation { background: #feca57; color: white; }
.type-overstay { background: #48dbfb; color: white; }
.type-damage { background: #ff9ff3; color: white; }
.type-other { background: #c8d6e5; color: #2c3e50; }

.violation-status {
  margin-left: 12px;
}

.violation-title {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.violation-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-size: 0.85rem;
  color: #7f8c8d;
  font-weight: 500;
}

.detail-value {
  font-size: 0.85rem;
  color: #2c3e50;
  font-weight: 600;
}

.violation-meta {
  display: flex;
  gap: 16px;
  font-size: 0.75rem;
  color: #7f8c8d;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.violation-detail {
  space-y: 4;
}

.detail-section {
  margin-bottom: 25px;
}

.detail-section h4 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 15px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #3498db;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-item label {
  font-weight: 600;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.deduction-text {
  color: #e74c3c;
  font-weight: 600;
}

.penalty-text {
  color: #ff9800;
  font-weight: 600;
}

.status-text {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  display: inline-block;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-processed {
  background: #d4edda;
  color: #155724;
}

.status-revoked {
  background: #e2e3e5;
  color: #383d41;
}

.status-rejected {
  background: #fde2e2;
  color: #c45656;
}

.status-expired {
  background: #e2e3e5;
  color: #383d41;
}

.status-appealed {
  background: #cce5ff;
  color: #004085;
}

.status-default {
  background: #f8f9fa;
  color: #6c757d;
}

.description-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  line-height: 1.6;
  color: #2c3e50;
}

.facility-info, .expire-info {
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  color: #2c3e50;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .violation-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .violation-side {
    flex-direction: row;
    align-items: center;
    width: 100%;
    justify-content: space-between;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
