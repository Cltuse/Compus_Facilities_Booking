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
          <div class="form-row">
            <el-form-item label="操作人">
              <el-select
                v-model="searchForm.operatorId"
                placeholder="选择操作人"
                clearable
                filterable
                remote
                :remote-method="searchOperators"
                :loading="operatorLoading"
                style="width: 160px">
                <el-option
                  v-for="operator in operatorOptions"
                  :key="operator.id"
                  :label="`${operator.realName} (${operator.username})`"
                  :value="operator.id" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="操作类型">
              <el-select v-model="searchForm.operationType" placeholder="选择操作类型" clearable style="width: 140px">
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
                style="width: 160px" />
            </el-form-item>
            
            <el-form-item label="结束时间">
              <el-date-picker
                v-model="searchForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 160px" />
            </el-form-item>
            
            <el-form-item class="button-group">
              <el-button type="primary" @click="handleSearch" :icon="Search" size="default" :loading="loading">搜索</el-button>
              <el-button @click="resetSearch" :icon="Refresh" size="default">重置</el-button>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 操作日志列表 -->
    <div class="table-container">
      <el-empty v-if="!loading && logData.length === 0 && (searchForm.operatorId || searchForm.operationType || searchForm.startTime || searchForm.endTime)" description="未找到符合条件的操作日志">
        <el-button type="primary" @click="resetSearch">清除搜索条件</el-button>
      </el-empty>
      <el-table v-else :data="logData" class="operation-table" v-loading="loading" stripe>
        <el-table-column prop="operatorName" label="操作人" width="140" />
        <el-table-column prop="operationType" label="操作类型" width="230" align="center">
          <template #default="scope">
            <el-tag :type="getOperationTypeType(scope.row.operationType)" size="small">
              {{ getOperationTypeText(scope.row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="目标ID" width="120" align="center"/>
        <el-table-column prop="detail" label="操作详情" min-width="200" show-overflow-tooltip  align="center"/>
        <el-table-column prop="ipAddress" label="IP地址" width="160" align="center"/>
        <el-table-column prop="createdAt" label="操作时间" width="220" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
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
import { ref, reactive, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Refresh, View } from '@element-plus/icons-vue';
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

// 搜索功能改为手动触发，不再自动监听表单变化
// 保留搜索超时机制用于防抖
let searchTimeout = null;

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});



const detailDialogVisible = ref(false);

// 时间选择器快捷选项
const startTimeShortcuts = [
  {
    text: '今天',
    value: () => {
      const date = new Date();
      date.setHours(0, 0, 0, 0);
      return date;
    }
  },
  {
    text: '昨天',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() - 1);
      date.setHours(0, 0, 0, 0);
      return date;
    }
  },
  {
    text: '7天前',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() - 7);
      date.setHours(0, 0, 0, 0);
      return date;
    }
  },
  {
    text: '30天前',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() - 30);
      date.setHours(0, 0, 0, 0);
      return date;
    }
  }
];

const endTimeShortcuts = [
  {
    text: '现在',
    value: () => new Date()
  },
  {
    text: '今天',
    value: () => {
      const date = new Date();
      date.setHours(23, 59, 59, 999);
      return date;
    }
  },
  {
    text: '昨天',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() - 1);
      date.setHours(23, 59, 59, 999);
      return date;
    }
  }
];

const loadOperationTypes = async () => {
  try {
    const response = await adminAPI.getOperationTypes();
    const types = response.data;
    operationTypes.value = types.map(type => ({
      value: type,
      label: getOperationTypeText(type)
    }));
  } catch (error) {
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

const loadOperationLogs = async () => {
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
      console.log('开始时间参数:', params.startTime);
    }
    if (searchForm.endTime) {
      params.endTime = searchForm.endTime;
      console.log('结束时间参数:', params.endTime);
    }
    
    const response = await adminAPI.getOperationLogs(params);
    const data = response.data;
    
    // 处理不同的响应格式
    let content = [];
    let total = 0;
    
    if (data.content && Array.isArray(data.content)) {
      // Spring Data 标准格式
      content = data.content;
      total = data.totalElements || 0;
    } else if (Array.isArray(data)) {
      // 直接数组格式
      content = data;
      total = data.length;
    } else if (data.data && Array.isArray(data.data)) {
      // 嵌套data格式
      content = data.data;
      total = data.total || data.totalElements || data.data.length;
    } else {
      console.warn('未预期的数据格式:', data);
      content = [];
      total = 0;
    }
    
    // 按时间倒序排序（最新的在前）
    content.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    
    // 客户端过滤（仅在后端未正确过滤时执行）
    if (searchForm.operationType) {
      content = content.filter(item => item.operationType === searchForm.operationType);
    }
    if (searchForm.operatorId) {
      content = content.filter(item => item.operatorId == searchForm.operatorId);
    }
    if (searchForm.startTime || searchForm.endTime) {
      const start = searchForm.startTime ? new Date(searchForm.startTime) : null;
      const end = searchForm.endTime ? new Date(searchForm.endTime) : null;
      content = content.filter(item => {
        const itemTime = new Date(item.createdAt);
        return (!start || itemTime >= start) && (!end || itemTime <= end);
      });
    }
    
    // 按时间倒序排序（最新的在前）
    content.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    logData.value = content;
    pagination.total = total;
  } catch (error) {
    console.error('加载操作日志失败:', error);
    ElMessage.error('加载操作日志失败');
  } finally {
    loading.value = false;
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
  // 清除之前的定时器
  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }
  
  // 设置新的定时器，实现防抖
  searchTimeout = setTimeout(() => {
    pagination.currentPage = 1;
    loadOperationLogs();
  }, 300);
};

const resetSearch = () => {
  searchForm.operatorId = '';
  searchForm.operationType = '';
  searchForm.startTime = '';
  searchForm.endTime = '';
  operatorOptions.value = [];
  handleSearch();
};

const handleSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  loadOperationLogs();
};

const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  loadOperationLogs();
};

const handleView = (row) => {
  currentDetail.value = row;
  detailDialogVisible.value = true;
};

onMounted(() => {
  loadOperationTypes();
  loadOperationLogs();
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

.loading-container {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
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

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
  width: 100%;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  flex-shrink: 0;
}

.search-form :deep(.el-form-item.button-group) {
  margin-left: auto;
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .form-row {
    gap: 12px;
  }
  
  .search-form :deep(.el-form-item) {
    margin-bottom: 8px;
  }
}

@media (max-width: 1200px) {
  .form-row {
    gap: 8px;
  }
  
  .search-form :deep(.el-select),
  .search-form :deep(.el-date-editor) {
    width: 140px !important;
  }
}

@media (max-width: 992px) {
  .form-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-form :deep(.el-form-item) {
    width: 100%;
    margin-bottom: 12px;
  }
  
  .search-form :deep(.el-form-item.button-group) {
    margin-left: 0;
    display: flex;
    justify-content: flex-end;
  }
  
  .search-form :deep(.el-select),
  .search-form :deep(.el-date-editor) {
    width: 100% !important;
  }
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



/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin: 24px 40px 24px;
}

.search-no-results {
  padding: 60px 0;
  text-align: center;
}

.filter-status {
  margin-bottom: 16px;

  background: #fafbfc;
}

.search-no-results :deep(.el-empty__description p) {
  color: #909399;
  font-size: 14px;
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
