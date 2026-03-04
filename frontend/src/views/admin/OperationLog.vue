<template>
  <div class="operation-log">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><Document /></el-icon>
        操作日志审计
      </h1>
      <p class="page-subtitle">查看系统操作记录，便于问题排查和责任追踪</p>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card">
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
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

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
    <el-card class="log-list-card">
      <el-table :data="logData" style="width: 100%" v-loading="loading">
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
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="createdAt" label="操作时间" width="200">
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
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="操作日志详情"
      width="600px">
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
  padding: 20px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-title .el-icon {
  margin-right: 8px;
  font-size: 28px;
  color: #409eff;
}

.page-subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
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
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.log-list-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}
</style>