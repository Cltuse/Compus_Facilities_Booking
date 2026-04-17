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
          违规记录管理
        </h1>
        <p class="page-subtitle">管理系统中的所有违规记录</p>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="search-container">
        <el-input
            v-model="searchUser"
            placeholder="搜索用户姓名"
            class="search-input"
            clearable
            @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
            v-model="searchViolationType"
            placeholder="违规类型"
            class="search-select"
            clearable
            @change="handleSearch"
        >
          <el-option label="爽约" value="NO_SHOW" />
          <el-option label="超时占用" value="OVERDUE" />
          <el-option label="频繁取消" value="CANCEL_FREQ" />
          <el-option label="损坏公物" value="DAMAGE" />
          <el-option label="其他违规" value="OTHER" />
        </el-select>

        <el-select
            v-model="searchStatus"
            placeholder="处理状态"
            class="search-select"
            clearable
            @change="handleSearch"
        >
          <el-option label="待处理" value="PENDING" />
          <el-option label="已处理" value="PROCESSED" />
          <el-option label="已取消生效" value="REVOKED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
      </div>

      <div class="toolbar-actions">
        <el-button type="primary" size="large" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增违规
        </el-button>
        <el-button type="success" size="large" @click="refreshData">
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
          <div class="stat-value">{{ totalViolations }}</div>
          <div class="stat-label">总违规记录</div>
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
          <div class="stat-value">{{ pendingViolations }}</div>
          <div class="stat-label">待处理违规</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon info">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 16V12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 8H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalPenaltyPoints }}</div>
          <div class="stat-label">累计处罚分</div>
        </div>
      </div>
    </div>

    <!-- 违规记录表格 -->
    <div class="table-container">
      <el-table
    :data="filteredViolationList"
    class="violation-table"
    :header-cell-style="headerCellStyle"
    :cell-style="cellStyle"
    @row-click="handleRowClick"
>
        <el-table-column prop="userName" label="用户姓名" min-width="120">
          <template #default="{ row }">
            <div class="user-name">{{ row.userName }}</div>
          </template>
        </el-table-column>

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

        <el-table-column prop="description" label="违规描述" min-width="300" align="center">
          <template #default="{ row }">
            <div class="violation-description">
              <div class="description-text">{{ row.description }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 删除关联设施列，数据库中不存在此字段 -->

        <el-table-column prop="reportedTime" label="上报时间" width="240" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDateTime(row.reportedTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="penaltyPoints" label="处罚分" width="100" align="center">
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

        <el-table-column label="操作" width="260" align="center" fixed="right">
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
              <el-button
                  v-if="row.status === 'PENDING'"
                  size="small"
                  type="success"
                  :plain="true"
                  class="action-btn process-btn"
                  @click.stop="processViolation(row)"
              >
                <el-icon><CircleCheck /></el-icon>
                处理
              </el-button>
              <el-button
                  v-if="row.status === 'PROCESSED'"
                  size="small"
                  type="warning"
                  :plain="true"
                  class="action-btn revoke-btn"
                  @click.stop="revokeViolation(row)"
              >
                <el-icon><Close /></el-icon>
                取消生效
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
          :total="totalItems"
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
      width="700px"
      :before-close="handleCloseDetail"
  >
    <div v-if="currentViolation" class="violation-detail">
      <div class="detail-section">
        <h4>基本信息</h4>
        <div class="detail-grid">
          <div class="detail-item">
            <label>用户姓名：</label>
            <span>{{ currentViolation.userName }}</span>
          </div>
          <!-- 删除用户ID字段 -->
          <div class="detail-item">
            <label>违规类型：</label>
            <span class="type-badge" :class="getViolationTypeClass(currentViolation.violationType)">
                {{ getViolationTypeText(currentViolation.violationType) }}
              </span>
          </div>
          <!-- 删除关联设施字段，数据库中不存在 -->
          <div class="detail-item">
            <label>上报人：</label>
            <span>{{ currentViolation.reporterName || '系统记录' }}</span>
          </div>
          <div class="detail-item">
            <label>上报时间：</label>
            <span>{{ formatDateTime(currentViolation.reportedTime) }}</span>
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

      <div v-if="currentViolation.reservationId" class="detail-section">
        <h4>关联预约</h4>
        <div class="reservation-info">
          预约ID：{{ currentViolation.reservationId }}
        </div>
      </div>
    </div>

    <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
              v-if="currentViolation.status === 'PENDING'"
              type="success"
              @click="processViolation(currentViolation)"
          >
            标记为已处理
          </el-button>
          <el-button
              v-if="currentViolation.status === 'PROCESSED'"
              type="warning"
              @click="revokeViolation(currentViolation)"
          >
            取消生效
          </el-button>
        </span>
    </template>
  </el-dialog>

  <!-- 新增违规记录弹窗 -->
  <el-dialog
      v-model="addDialogVisible"
      title="新增违规记录"
      width="600px"
      :before-close="handleCloseAdd"
  >
    <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addFormRules"
        label-width="100px"
        class="add-form"
    >
      <el-form-item label="违规用户" prop="userId">
        <el-select
            v-model="addForm.userId"
            placeholder="选择违规用户"
            filterable
            remote
            :remote-method="searchUsers"
            :loading="userLoading"
        >
          <el-option
              v-for="user in userOptions"
              :key="user.id"
              :label="`${user.realName} (${user.username})`"
              :value="user.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="违规类型" prop="violationType">
        <el-select v-model="addForm.violationType" placeholder="选择违规类型">
          <el-option label="爽约" value="NO_SHOW" />
          <el-option label="超时占用" value="OVERDUE" />
          <el-option label="频繁取消" value="CANCEL_FREQ" />
          <el-option label="损坏公物" value="DAMAGE" />
          <el-option label="其他违规" value="OTHER" />
        </el-select>
      </el-form-item>

      <el-form-item label="关联预约" prop="reservationId">
        <el-input
            v-model="addForm.reservationId"
            placeholder="关联预约ID（可选）"
            type="number"
        />
      </el-form-item>

      <el-form-item label="处罚分" prop="penaltyPoints">
        <el-input-number
            v-model="addForm.penaltyPoints"
            :min="0"
            :max="100"
            :step="5"
        />
      </el-form-item>

      <el-form-item label="详细描述" prop="description">
        <el-input
            v-model="addForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入违规详细描述"
        />
      </el-form-item>
    </el-form>

    <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddViolation">提交</el-button>
        </span>
    </template>
  </el-dialog>

</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Calendar, Clock, CircleCheck, CircleClose, Close, Warning, QuestionFilled, View, Plus } from '@element-plus/icons-vue'
import { formatDateTime } from '@/utils/date'
import { violationAPI } from '../../api'
import { userAPI } from '../../api'

const violationList = ref([])
const currentViolation = ref({})
const detailDialogVisible = ref(false)
const addDialogVisible = ref(false)
const addFormRef = ref(null)

// 搜索条件
const searchUser = ref('')
const searchViolationType = ref('')
const searchStatus = ref('')

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

// 新增表单
const addForm = ref({
  userId: null,
  violationType: '',
  reservationId: null,
  penaltyPoints: 10,
  description: ''
})

const addFormRules = {
  userId: [{ required: true, message: '请选择违规用户', trigger: 'change' }],
  violationType: [{ required: true, message: '请选择违规类型', trigger: 'change' }],
  penaltyPoints: [{ required: true, message: '请输入处罚分', trigger: 'blur' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }]
}

// 用户搜索
const userOptions = ref([])
const userLoading = ref(false)

// 统计数据（从后端API获取完整数据）
const stats = ref({
  totalViolations: 0,
  pendingViolations: 0,
  totalPenaltyPoints: 0
})

onMounted(() => {
  loadViolationList()
  loadViolationStats()
})

// 加载违规记录统计数据
const loadViolationStats = async () => {
  try {
    const res = await violationAPI.getViolationStats()
    if (res && res.code === 200 && res.data) {
      stats.value = {
        totalViolations: res.data.totalViolations || 0,
        pendingViolations: res.data.pendingViolations || 0,
        totalPenaltyPoints: res.data.totalPenaltyPoints || 0
      }
    }
  } catch (error) {
    console.error('加载违规记录统计数据失败:', error)
  }
}

const loadViolationList = async () => {
  try {
    // 计算分页参数，前端页码从1开始，后端从0开始
    const page = currentPage.value - 1
    const res = await violationAPI.getAllViolations(
      page, 
      pageSize.value, 
      searchUser.value, 
      searchViolationType.value, 
      searchStatus.value
    )
    if (res && res.code === 200 && res.data) {
      // 处理分页数据，提取content数组和总数
      violationList.value = res.data.content || []
      totalItems.value = res.data.totalElements || 0
    } else {
      violationList.value = []
      totalItems.value = 0
      ElMessage.error(res?.message || '获取数据失败')
    }
  } catch (error) {
    console.error('加载违规记录失败:', error)
    ElMessage.error('加载违规记录失败')
    violationList.value = []
    totalItems.value = 0
  }
}

// 搜索用户
const searchUsers = async (query) => {
  if (!query) {
    userOptions.value = []
    return
  }

  userLoading.value = true
  try {
    const res = await userAPI.searchUsers(query)
    userOptions.value = res.data || []
  } catch (error) {
    console.error('搜索用户失败:', error)
  } finally {
    userLoading.value = false
  }
}

// 注释掉前端的过滤逻辑，因为过滤现在在后端进行
// const filteredViolationList = computed(() => {
//   let filtered = violationList.value
//
//   if (searchUser.value) {
//     filtered = filtered.filter(item =>
//         item.userName && item.userName.toLowerCase().includes(searchUser.value.toLowerCase())
//     )
//   }
//
//   if (searchViolationType.value) {
//     filtered = filtered.filter(item =>
//         item.violationType === searchViolationType.value
//     )
//   }
//
//   if (searchStatus.value) {
//     filtered = filtered.filter(item =>
//         item.status === searchStatus.value
//     )
//   }
//
//   return filtered
// })

// 直接使用后端返回的数据，不再需要前端过滤
const filteredViolationList = computed(() => violationList.value)

// 使用后端统计接口获取的数据
const totalViolations = computed(() => stats.value.totalViolations)
const pendingViolations = computed(() => stats.value.pendingViolations)
const totalPenaltyPoints = computed(() => stats.value.totalPenaltyPoints)

// 处理方法
const handleSearch = () => {
  currentPage.value = 1
  loadViolationList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadViolationList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadViolationList()
}

const refreshData = () => {
  currentPage.value = 1
  loadViolationList()
  loadViolationStats()
}

const showAddDialog = () => {
  addForm.value = {
    userId: null,
    violationType: '',
    reservationId: null,
    penaltyPoints: 10,
    description: ''
  }
  userOptions.value = []
  addDialogVisible.value = true
}

const submitAddViolation = async () => {
  try {
    await addFormRef.value.validate()

    const adminInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const violationData = {
      ...addForm.value,
      reportedBy: adminInfo.id,
      reportedTime: new Date()
    }

    const res = await violationAPI.recordViolation(violationData)
    if (res.success) {
      ElMessage.success('违规记录添加成功')
      addDialogVisible.value = false
      loadViolationList()
      loadViolationStats()
    }
  } catch (error) {
    console.error('添加违规记录失败:', error)
  }
}

const handleCloseAdd = () => {
  addDialogVisible.value = false
}

const showViolationDetail = (row) => {
  currentViolation.value = row
  detailDialogVisible.value = true
}

const handleCloseDetail = () => {
  detailDialogVisible.value = false
  currentViolation.value = {}
}

const processViolation = async (row) => {
  try {
    await ElMessageBox.confirm(
        '确认将此违规记录标记为已处理？',
        '处理确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await violationAPI.approveViolation(row.id)

    if (res.success) {
      ElMessage.success('违规记录已标记为已处理')
      loadViolationList()
      loadViolationStats()
      detailDialogVisible.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('处理违规记录失败:', error)
    }
  }
}

const revokeViolation = async (row) => {
  try {
    await ElMessageBox.confirm(
        '确认取消这条已生效违规记录吗？这会重新计算用户信誉分和违约次数。',
        '取消生效确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await violationAPI.revokeViolation(row.id)

    if (res.success) {
      ElMessage.success('违规记录已取消生效')
      loadViolationList()
      loadViolationStats()
      detailDialogVisible.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消生效违规记录失败:', error)
    }
  }
}

const handleRowClick = (row) => {
  showViolationDetail(row)
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

// 状态相关方法
const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'PROCESSED': 'success',
    'REVOKED': 'info',
    'REJECTED': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'PROCESSED': '已处理',
    'REVOKED': '已取消生效',
    'REJECTED': '已驳回'
  }
  return map[status] || status
}

const getStatusClass = (status) => {
  const map = {
    'PENDING': 'status-pending',
    'PROCESSED': 'status-processed',
    'REVOKED': 'status-revoked',
    'REJECTED': 'status-rejected'
  }
  return map[status] || 'status-default'
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
  display: flex;
  gap: 12px;
  flex: 1;
  max-width: 600px;
}

.search-input {
  flex: 1;
}

.search-select {
  width: 150px;
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

.stat-icon.info {
  background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
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
  padding: 16px 12px;
  transition: all 0.3s ease;
}

.violation-table :deep(.el-table__body td .cell) {
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}

/* 表格单元格内容样式 */
.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
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

/* 删除facility-info样式，不再需要 */

.text-muted {
  color: #95a5a6;
  font-style: italic;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #718096;
  font-size: 14px;
}

.penalty-info {
  text-align: center;
}

.penalty-value {
  font-size: 16px;
  font-weight: 600;
  color: #ff9800;
}

.violation-type-tag, .status-tag {
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
  gap: 8px;
  justify-content: center;
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

.view-btn {
  color: #409eff;
  border-color: #409eff;
}

.process-btn {
  color: #67c23a;
  border-color: #67c23a;
}

.revoke-btn {
  color: #e6a23c;
  border-color: #e6a23c;
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

/* 详情弹窗样式 */
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
  background: #eef2f7;
  color: #606266;
}

.status-rejected {
  background: #fde2e2;
  color: #c45656;
}

.description-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  line-height: 1.6;
  color: #2c3e50;
}

.reservation-info {
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  color: #2c3e50;
}

/* 新增表单样式 */
.add-form {
  padding: 20px 0;
}

.add-form :deep(.el-form-item__label) {
  color: #2d3748;
  font-weight: 600;
  font-size: 14px;
}

.add-form :deep(.el-input__wrapper),
.add-form :deep(.el-textarea__inner),
.add-form :deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  background: #f7fafc;
}

.add-form :deep(.el-input__wrapper:hover),
.add-form :deep(.el-textarea__inner:hover),
.add-form :deep(.el-select:hover .el-input__wrapper) {
  border-color: #cbd5e0;
  background: #ffffff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.add-form :deep(.el-input__wrapper.is-focus),
.add-form :deep(.el-textarea__inner:focus),
.add-form :deep(.el-select.is-focused .el-input__wrapper) {
  border-color: #409eff;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

/* 违规类型样式 */
.type-no-show { color: #e74c3c; }
.type-overdue { color: #f39c12; }
.type-cancel-freq { color: #3498db; }
.type-damage { color: #9b59b6; }
.type-other { color: #95a5a6; }

/* 动画效果 */
@keyframes gradient-shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .violation-records-page {
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

  .toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .search-container {
    max-width: none;
    flex-direction: column;
  }

  .search-select {
    width: 100%;
  }

  .toolbar-actions {
    justify-content: center;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .table-container {
    overflow-x: auto;
    border-radius: 12px;
  }

  .violation-table {
    min-width: 800px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
