<template>
  <div class="operation-log">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          操作日志审计
        </h1>
        <p class="page-subtitle">查看系统操作记录，便于问题排查和责任追踪</p>
      </div>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作人">
          <el-select
            v-model="searchForm.operatorId"
            placeholder="选择操作人"
            clearable
            filterable
            remote
            :remote-method="searchOperators"
            :loading="operatorLoading"
            style="width: 200px">
            <el-option
              v-for="operator in operatorOptions"
              :key="operator.id"
              :label="`${operator.realName} (${operator.username})`"
              :value="operator.id" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="选择操作类型" clearable style="width: 180px">
            <el-option
              v-for="type in operationTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 180px" />
        </el-form-item>
        
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 180px" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search" size="large">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh" size="large">重置</el-button>
        </el-form-item>
      </el-form>
      </div>
    </div>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCount }}</div>
              <div class="stat-label">总记录数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card today-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayCount }}</div>
              <div class="stat-label">今日操作</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card week-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.weekCount }}</div>
              <div class="stat-label">本周操作</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card user-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作日志列表 -->
    <div class="table-container">
      <el-table :data="logData" class="operation-table" v-loading="loading" stripe>
        <el-table-column prop="operatorName" label="操作人" width="140" />
        <el-table-column prop="operationType" label="操作类型" width="160">
          <template #default="scope">
            <el-tag :type="getOperationTypeType(scope.row.operationType)" size="small">
              {{ getOperationTypeText(scope.row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="目标ID" width="100" />
        <el-table-column prop="detail" label="操作详情" min-width="200" show-overflow-tooltip  align="center"/>
        <el-table-column prop="ipAddress" label="IP地址" width="160" />
        <el-table-column prop="createdAt" label="操作时间" width="220">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="scope">
            <el-button type="info" link @click="handleView(scope.row)" :icon="View">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="custom-pagination" />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="操作日志详情"
      width="600px"
      class="detail-dialog">
      <el-descriptions :column="1" border v-if="currentDetail">
        <el-descriptions-item label="操作人">
          {{ currentDetail.operatorName || '系统' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeType(currentDetail.operationType)" size="small">
            {{ getOperationTypeText(currentDetail.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="目标ID">
          {{ currentDetail.targetId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作详情">
          {{ currentDetail.detail || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">
          {{ currentDetail.ipAddress || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">
          {{ formatDateTime(currentDetail.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Document, Search, Refresh, View, Clock, Calendar, User } from '@element-plus/icons-vue';
import { adminAPI, userAPI } from '../../api';

const loading = ref(false);
const operatorLoading = ref(false);

const logData = ref([]);
const operatorOptions = ref([]);
const operationTypes = ref([]);
const currentDetail = ref(null);

const searchForm = reactive({
  operatorId: '',
  operationType: '',
  startTime: '',
  endTime: ''
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const stats = reactive({
  totalCount: 0,
  todayCount: 0,
  weekCount: 0,
  activeUsers: 0
});

const detailDialogVisible = ref(false);

const loadOperationTypes = async () => {
  try {
    console.log('开始加载操作类型...');
    const response = await adminAPI.getOperationTypes();
    console.log('操作类型响应:', response);
    const types = response.data;
    console.log('操作类型数据:', types);
    operationTypes.value = types.map(type => ({
      value: type,
      label: getOperationTypeText(type)
    }));
    console.log('操作类型选项:', operationTypes.value);
  } catch (error) {
    console.error('加载操作类型失败:', error);
    ElMessage.error('加载操作类型失败');
  }
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

const getOperationTypeType = (type) => {
  const types = {
    'APPROVE_RESERVATION': 'success',
    'REJECT_RESERVATION': 'danger',
    'VERIFY_CHECKIN': 'primary',
    'ADD_BLACKLIST': 'danger',
    'REMOVE_BLACKLIST': 'success',
    'REPLY_FEEDBACK': 'info',
    'UPDATE_RULE': 'warning',
    'CREATE_FACILITY': 'success',
    'UPDATE_FACILITY': 'primary',
    'CREATE_NOTICE': 'info',
    'UPDATE_NOTICE': 'info',
    'MAINTENANCE_COMPLETE': 'success'
  };
  return types[type] || 'info';
};

const getOperationTypeText = (type) => {
  const texts = {
    'APPROVE_RESERVATION': '审核通过预约',
    'REJECT_RESERVATION': '拒绝预约',
    'VERIFY_CHECKIN': '核销签到',
    'ADD_BLACKLIST': '加入黑名单',
    'REMOVE_BLACKLIST': '移出黑名单',
    'REPLY_FEEDBACK': '回复反馈',
    'UPDATE_RULE': '更新规则',
    'CREATE_FACILITY': '创建设施',
    'UPDATE_FACILITY': '更新设施',
    'CREATE_NOTICE': '发布通知',
    'UPDATE_NOTICE': '更新通知',
    'MAINTENANCE_COMPLETE': '完成维护'
  };
  return texts[type] || type;
};

const loadOperationLogs = async (updateStats = false) => {
  loading.value = true;
  try {
    const params = {
      page: pagination.currentPage - 1,
      size: pagination.pageSize
    };
    
    // 添加搜索条件
    if (searchForm.operatorId) {
      params.operatorId = searchForm.operatorId;
    }
    if (searchForm.operationType) {
      params.operationType = searchForm.operationType;
    }
    if (searchForm.startTime) {
      params.startTime = searchForm.startTime;
    }
    if (searchForm.endTime) {
      params.endTime = searchForm.endTime;
    }
    
    const response = await adminAPI.getOperationLogs(params);
    const data = response.data;
    
    logData.value = data.content || [];
    pagination.total = data.totalElements || 0;
    
    // 只有在需要更新统计信息时才调用loadStats
    if (updateStats) {
      await loadStats();
    }
  } catch (error) {
    ElMessage.error('加载操作日志失败');
  } finally {
    loading.value = false;
  }
};

const loadStats = async () => {
  try {
    // 只有在有搜索条件时才更新统计信息
    const hasSearchCondition = searchForm.operatorId || searchForm.operationType || searchForm.startTime || searchForm.endTime;
    
    if (hasSearchCondition) {
      // 有搜索条件时，基于当前搜索结果计算统计信息
      stats.totalCount = pagination.total;
      stats.todayCount = logData.value.filter(log => {
        const logDate = new Date(log.createdAt);
        const today = new Date();
        return logDate.toDateString() === today.toDateString();
      }).length;
      stats.weekCount = logData.value.filter(log => {
        const logDate = new Date(log.createdAt);
        const weekAgo = new Date();
        weekAgo.setDate(weekAgo.getDate() - 7);
        return logDate >= weekAgo;
      }).length;
      stats.activeUsers = new Set(logData.value.map(log => log.operatorId).filter(Boolean)).size;
    } else {
      // 无搜索条件时，统计信息保持不变（基于所有数据）
      // 这里可以调用专门的统计接口获取全局数据
      // 暂时保持当前值不变
      console.log('无搜索条件，统计信息保持不变');
    }
  } catch (error) {
    console.error('加载统计信息失败:', error);
  }
};

const searchOperators = async (query) => {
  if (!query || query.length < 2) {
    operatorOptions.value = [];
    return;
  }
  
  operatorLoading.value = true;
  try {
    const response = await userAPI.searchUsers(query);
    operatorOptions.value = response.data;
  } catch (error) {
    ElMessage.error('搜索操作人失败');
  } finally {
    operatorLoading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  loadOperationLogs(true); // 搜索时更新统计信息
};

const resetSearch = () => {
  searchForm.operatorId = '';
  searchForm.operationType = '';
  searchForm.startTime = '';
  searchForm.endTime = '';
  handleSearch();
};

const handleSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  loadOperationLogs(false); // 分页时不更新统计信息
};

const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  loadOperationLogs(false); // 分页时不更新统计信息
};

const handleView = (row) => {
  currentDetail.value = row;
  detailDialogVisible.value = true;
};

onMounted(() => {
  loadOperationTypes();
  loadOperationLogs(true); // 页面初始化时加载统计信息
});
</script>

<style scoped>
.operation-log {
  padding: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  min-height: calc(100vh - 88px);
}

.empty-state {
  padding: 40px 0;
  text-align: center;
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
  padding: 0 40px;
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.search-section {
  flex: 1;
  min-width: 300px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.search-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #4a5568;
}

.search-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.search-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1), 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-form :deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}

.search-form :deep(.el-date-editor.el-input) {
  width: 180px;
}

.search-form :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.search-form :deep(.el-button:hover) {
  transform: translateY(-1px);
}

/* 统计信息 */
.stats-row {
  margin-bottom: 24px;
  padding: 0 40px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
}

.stat-card :deep(.el-card__body) {
  padding: 0;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 24px;
}

.stat-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.total-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #1890ff, #096dd9);
}

.today-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #52c41a, #3daa0d);
}

.week-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #faad14, #d48806);
}

.user-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #722ed1, #531dab);
}

.stat-icon {
  font-size: 28px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
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
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin: 0 40px 24px;
}

.operation-table {
  width: 100%;
}

.operation-table :deep(.el-table) {
  width: 100% !important;
}

.operation-table :deep(.el-table__header-wrapper) {
  width: 100% !important;
}

.operation-table :deep(.el-table__body-wrapper) {
  width: 100% !important;
}

.operation-table :deep(.el-table__header) {
  width: 100% !important;
}

.operation-table :deep(.el-table__body) {
  width: 100% !important;
}

.operation-table :deep(.el-table__header th) {
  background: #f8fafc;
  color: #2d3748;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
}

.operation-table :deep(.el-table__row:hover) {
  background: #f7fafc;
}

.operation-table :deep(.el-button--info) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.operation-table :deep(.el-button--info:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 分页 */
.pagination-container {
  padding: 20px 0;
  display: flex;
  justify-content: center;
}

.custom-pagination :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-pagination :deep(.el-pagination__total) {
  color: #4a5568;
  font-weight: 500;
  margin-right: 16px;
}

.custom-pagination :deep(.el-pager) {
  display: flex;
  gap: 4px;
}

.custom-pagination :deep(.el-pager li) {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.custom-pagination :deep(.el-pager li:hover) {
  transform: translateY(-1px);
}

.custom-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
}

.custom-pagination :deep(.el-select) {
  margin: 0 8px;
}

.custom-pagination :deep(.el-input__wrapper) {
  border-radius: 6px;
  border-color: #e2e8f0;
}

.custom-pagination :deep(.el-input__inner) {
  font-size: 13px;
}

/* 详情对话框 */
.detail-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.detail-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.detail-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.detail-dialog :deep(.el-descriptions__label) {
  font-weight: 600;
  color: #4a5568;
}

.detail-dialog :deep(.el-descriptions__content) {
  color: #2d3748;
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
    padding: 0 20px;
  }

  .stats-row {
    padding: 0 20px;
  }

  .table-container {
    margin: 0 20px 24px;
  }

  .search-form {
    flex-direction: column;
    align-items: stretch;
  }

  .search-form :deep(.el-form-item) {
    margin-right: 0;
    width: 100%;
  }

  .search-form :deep(.el-date-editor.el-input) {
    width: 100%;
  }
}
</style>