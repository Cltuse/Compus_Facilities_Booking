<template>
  <div class="blacklist">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><Warning /></el-icon>
        黑名单管理
      </h1>
      <p class="page-subtitle">管理违规用户的黑名单记录</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="operation-bar">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="生效中" value="ACTIVE" />
            <el-option label="已过期" value="EXPIRED" />
            <el-option label="手动移除" value="REMOVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input v-model="searchForm.userName" placeholder="输入用户姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="danger" @click="handleAddBlacklist" :icon="Plus">添加黑名单</el-button>
    </div>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card active-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeCount }}</div>
              <div class="stat-label">生效中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card expired-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.expiredCount }}</div>
              <div class="stat-label">已过期</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card removed-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.removedCount }}</div>
              <div class="stat-label">手动移除</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card total-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCount }}</div>
              <div class="stat-label">总记录数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 黑名单列表 -->
    <el-card class="blacklist-card">
      <el-table :data="blacklistData" style="width: 100%" v-loading="loading">
        <el-table-column prop="userRealName" label="用户姓名" width="100" />
        <el-table-column prop="userName" label="学号/工号" width="120" />
        <el-table-column prop="reason" label="拉黑原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="scope">
            <span v-if="scope.row.endTime">{{ formatDateTime(scope.row.endTime) }}</span>
            <el-tag v-else type="danger" size="small">永久</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作员" width="80" />
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'ACTIVE'"
              type="success"
              link
              @click="handleRemove(scope.row)"
              :icon="CircleCheck">
              移出
            </el-button>
            <el-button
              type="info"
              link
              @click="handleView(scope.row)"
              :icon="View">
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

    <!-- 添加黑名单对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="添加黑名单"
      width="500px"
      :close-on-click-modal="false">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="用户" prop="userId">
          <el-select
            v-model="addForm.userId"
            placeholder="选择用户"
            filterable
            remote
            :remote-method="searchUsers"
            :loading="userLoading"
            style="width: 100%">
            <el-option
              v-for="user in userOptions"
              :key="user.id"
              :label="`${user.realName} (${user.username})`"
              :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="拉黑原因" prop="reason">
          <el-input
            v-model="addForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入拉黑原因"
            maxlength="255"
            show-word-limit />
        </el-form-item>
        <el-form-item label="拉黑期限" prop="endTime">
          <el-radio-group v-model="addForm.durationType">
            <el-radio label="permanent">永久</el-radio>
            <el-radio label="custom">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="addForm.durationType === 'custom'" label="结束时间" prop="endTime">
          <el-date-picker
            v-model="addForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="handleAddSubmit" :loading="addLoading">
            添加
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="黑名单详情"
      width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="用户姓名">{{ currentDetail.userRealName }}</el-descriptions-item>
        <el-descriptions-item label="学号/工号">{{ currentDetail.userName }}</el-descriptions-item>
        <el-descriptions-item label="拉黑原因" :span="2">{{ currentDetail.reason }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(currentDetail.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">
          <span v-if="currentDetail.endTime">{{ formatDateTime(currentDetail.endTime) }}</span>
          <el-tag v-else type="danger" size="small">永久</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.status)" size="small">
            {{ getStatusText(currentDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作员">{{ currentDetail.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ formatDateTime(currentDetail.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Warning, Search, Refresh, Plus, CircleCheck, View, Clock, List } from '@element-plus/icons-vue';
import { adminAPI, userAPI } from '../../api';

const loading = ref(false);
const userLoading = ref(false);
const addLoading = ref(false);

const blacklistData = ref([]);
const userOptions = ref([]);
const currentDetail = ref(null);

const searchForm = reactive({
  status: '',
  userName: ''
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const stats = reactive({
  activeCount: 0,
  expiredCount: 0,
  removedCount: 0,
  totalCount: 0
});

const addDialogVisible = ref(false);
const detailDialogVisible = ref(false);
const addFormRef = ref();

const addForm = reactive({
  userId: '',
  reason: '',
  durationType: 'permanent',
  endTime: ''
});

const addRules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  reason: [
    { required: true, message: '请输入拉黑原因', trigger: 'blur' },
    { min: 5, max: 255, message: '原因长度应在5-255个字符之间', trigger: 'blur' }
  ],
  endTime: [{ 
    validator: (rule, value, callback) => {
      if (addForm.durationType === 'custom' && !value) {
        callback(new Error('请选择结束时间'));
      } else {
        callback();
      }
    },
    trigger: 'change'
  }]
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

const getStatusType = (status) => {
  const types = {
    'ACTIVE': 'danger',
    'EXPIRED': 'info',
    'REMOVED': 'success'
  };
  return types[status] || 'info';
};

const getStatusText = (status) => {
  const texts = {
    'ACTIVE': '生效中',
    'EXPIRED': '已过期',
    'REMOVED': '手动移除'
  };
  return texts[status] || status;
};

const loadBlacklist = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.currentPage - 1,
      size: pagination.pageSize,
      status: searchForm.status,
      userName: searchForm.userName
    };
    
    const response = await adminAPI.getBlacklist(params);
    const data = response.data;
    
    blacklistData.value = data.content;
    pagination.total = data.totalElements;
    
    // 加载统计信息
    await loadStats();
  } catch (error) {
    ElMessage.error('加载黑名单失败');
  } finally {
    loading.value = false;
  }
};

const loadStats = async () => {
  try {
    // 这里可以根据实际需求调用统计接口
    // 暂时使用模拟数据
    stats.activeCount = blacklistData.value.filter(item => item.status === 'ACTIVE').length;
    stats.expiredCount = blacklistData.value.filter(item => item.status === 'EXPIRED').length;
    stats.removedCount = blacklistData.value.filter(item => item.status === 'REMOVED').length;
    stats.totalCount = pagination.total;
  } catch (error) {
    console.error('加载统计信息失败:', error);
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  loadBlacklist();
};

const resetSearch = () => {
  searchForm.status = '';
  searchForm.userName = '';
  handleSearch();
};

const handleSizeChange = (val) => {
  pagination.pageSize = val;
  loadBlacklist();
};

const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  loadBlacklist();
};

const handleAddBlacklist = () => {
  addForm.userId = '';
  addForm.reason = '';
  addForm.durationType = 'permanent';
  addForm.endTime = '';
  userOptions.value = [];
  addDialogVisible.value = true;
};

const searchUsers = async (query) => {
  if (!query || query.length < 2) {
    userOptions.value = [];
    return;
  }
  
  userLoading.value = true;
  try {
    const response = await userAPI.searchUsers(query);
    userOptions.value = response.data;
  } catch (error) {
    ElMessage.error('搜索用户失败');
  } finally {
    userLoading.value = false;
  }
};

const handleAddSubmit = async () => {
  try {
    await addFormRef.value.validate();
    addLoading.value = true;
    
    const formData = {
      userId: addForm.userId,
      reason: addForm.reason,
      operatorId: 1 // 这里应该从登录信息获取
    };
    
    if (addForm.durationType === 'custom' && addForm.endTime) {
      formData.endTime = addForm.endTime;
    }
    
    await adminAPI.addToBlacklist(formData);
    ElMessage.success('添加黑名单成功');
    addDialogVisible.value = false;
    loadBlacklist();
  } catch (error) {
    if (error !== false) {
      ElMessage.error('添加黑名单失败');
    }
  } finally {
    addLoading.value = false;
  }
};

const handleRemove = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要将 ${row.userRealName} 移出黑名单吗？`,
      '确认移出',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await adminAPI.removeFromBlacklist(row.id, { operatorId: 1 }); // operatorId应该从登录信息获取
    ElMessage.success('移出黑名单成功');
    loadBlacklist();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移出黑名单失败');
    }
  }
};

const handleView = (row) => {
  currentDetail.value = row;
  detailDialogVisible.value = true;
};

onMounted(() => {
  loadBlacklist();
});
</script>

<style scoped>
.blacklist {
  padding: 20px;
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
  color: #f56c6c;
}

.page-subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
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

.active-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
}

.expired-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #a8a8a8, #999999);
}

.removed-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #52c41a, #3daa0d);
}

.total-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #1890ff, #096dd9);
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

.blacklist-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}
</style>