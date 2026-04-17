<template>
  <div class="blacklist">
    <!-- 椤甸潰鏍囬鍖哄煙 -->
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
          榛戝悕鍗曠鐞?
        </h1>
        <p class="page-subtitle">绠＄悊杩濊鐢ㄦ埛鐨勯粦鍚嶅崟璁板綍</p>
      </div>
    </div>

    <!-- 鎼滅储鍜屽伐鍏锋爮 -->
    <div class="toolbar">
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="鐘舵€?>
            <el-select v-model="searchForm.status" placeholder="鍏ㄩ儴鐘舵€? clearable style="width: 140px">
              <el-option label="鐢熸晥涓? value="ACTIVE" />
              <el-option label="宸茶繃鏈? value="EXPIRED" />
              <el-option label="鎵嬪姩绉婚櫎" value="REMOVED" />
            </el-select>
          </el-form-item>
          <el-form-item label="鐢ㄦ埛濮撳悕">
            <el-input v-model="searchForm.userName" placeholder="杈撳叆鐢ㄦ埛濮撳悕" clearable style="width: 200px" size="large" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search" size="large">鎼滅储</el-button>
            <el-button @click="resetSearch" :icon="Refresh" size="large">閲嶇疆</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="button-section">
        <el-button type="danger" size="large" class="add-button" @click="handleAddBlacklist">
          <el-icon><Plus /></el-icon>
          娣诲姞榛戝悕鍗?
        </el-button>
      </div>
    </div>

    <!-- 缁熻淇℃伅 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card active-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeCount }}</div>
              <div class="stat-label">鐢熸晥涓?/div>
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
              <div class="stat-label">宸茶繃鏈?/div>
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
              <div class="stat-label">鎵嬪姩绉婚櫎</div>
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
              <div class="stat-label">鎬昏褰曟暟</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 榛戝悕鍗曞垪琛?-->
    <el-card class="blacklist-card">
      <el-table :data="blacklistData" style="width: 100%" v-loading="loading">
        <el-table-column prop="userRealName" label="鐢ㄦ埛濮撳悕" width="100" />
        <el-table-column prop="userName" label="瀛﹀彿/宸ュ彿" width="120" />
        <el-table-column prop="reason" label="鎷夐粦鍘熷洜" min-width="200" show-overflow-tooltip align="center" />
        <el-table-column prop="startTime" label="寮€濮嬫椂闂? width="260" align="center" >
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="缁撴潫鏃堕棿" width="260" align="center">
          <template #default="scope">
            <span v-if="scope.row.endTime">{{ formatDateTime(scope.row.endTime) }}</span>
            <el-tag v-else type="danger" size="small">姘镐箙</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="鐘舵€? width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="鎿嶄綔鍛? width="100" align="center" />
        <el-table-column prop="createdAt" label="鍒涘缓鏃堕棿" width="260" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="鎿嶄綔" width="280" fixed="right" align="center">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'ACTIVE'"
              type="success"
              link
              @click="handleRemove(scope.row)"
              :icon="CircleCheck">
              绉诲嚭
            </el-button>
            <el-button
              type="info"
              link
              @click="handleView(scope.row)"
              :icon="View">
              璇︽儏
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 鍒嗛〉 -->
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

    <!-- 娣诲姞榛戝悕鍗曞璇濇 -->
    <el-dialog
      v-model="addDialogVisible"
      title="娣诲姞榛戝悕鍗?
      width="500px"
      :close-on-click-modal="false"
      class="blacklist-dialog">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="鐢ㄦ埛" prop="userId">
          <el-select
            v-model="addForm.userId"
            placeholder="閫夋嫨鐢ㄦ埛"
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
        <el-form-item label="鎷夐粦鍘熷洜" prop="reason">
          <el-input
            v-model="addForm.reason"
            type="textarea"
            :rows="3"
            placeholder="璇疯緭鍏ユ媺榛戝師鍥?
            maxlength="255"
            show-word-limit />
        </el-form-item>
        <el-form-item label="鎷夐粦鏈熼檺" prop="endTime">
          <el-radio-group v-model="addForm.durationType">
            <el-radio label="permanent">姘镐箙</el-radio>
            <el-radio label="custom">鑷畾涔?/el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="addForm.durationType === 'custom'" label="缁撴潫鏃堕棿" prop="endTime">
          <el-date-picker
            v-model="addForm.endTime"
            type="datetime"
            placeholder="閫夋嫨缁撴潫鏃堕棿"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">鍙栨秷</el-button>
          <el-button type="danger" @click="handleAddSubmit" :loading="addLoading">
            娣诲姞
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 璇︽儏瀵硅瘽妗?-->
    <el-dialog
      v-model="detailDialogVisible"
      title="榛戝悕鍗曡鎯?
      width="600px"
      class="detail-dialog">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="鐢ㄦ埛濮撳悕">{{ currentDetail.userRealName }}</el-descriptions-item>
        <el-descriptions-item label="瀛﹀彿/宸ュ彿">{{ currentDetail.userName }}</el-descriptions-item>
        <el-descriptions-item label="鎷夐粦鍘熷洜" :span="2">{{ currentDetail.reason }}</el-descriptions-item>
        <el-descriptions-item label="寮€濮嬫椂闂?>{{ formatDateTime(currentDetail.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="缁撴潫鏃堕棿">
          <span v-if="currentDetail.endTime">{{ formatDateTime(currentDetail.endTime) }}</span>
          <el-tag v-else type="danger" size="small">姘镐箙</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="鐘舵€?>
          <el-tag :type="getStatusType(currentDetail.status)" size="small">
            {{ getStatusText(currentDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="鎿嶄綔鍛?>{{ currentDetail.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鍒涘缓鏃堕棿" :span="2">{{ formatDateTime(currentDetail.createdAt) }}</el-descriptions-item>
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
  userId: [{ required: true, message: '璇烽€夋嫨鐢ㄦ埛', trigger: 'change' }],
  reason: [
    { required: true, message: '璇疯緭鍏ユ媺榛戝師鍥?, trigger: 'blur' },
    { min: 5, max: 255, message: '鍘熷洜闀垮害搴斿湪5-255涓瓧绗︿箣闂?, trigger: 'blur' }
  ],
  endTime: [{ 
    validator: (rule, value, callback) => {
      if (addForm.durationType === 'custom' && !value) {
        callback(new Error('璇烽€夋嫨缁撴潫鏃堕棿'));
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
    'ACTIVE': '鐢熸晥涓?,
    'EXPIRED': '宸茶繃鏈?,
    'REMOVED': '鎵嬪姩绉婚櫎'
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
    
    // 鍔犺浇缁熻淇℃伅
    await loadStats();
  } catch (error) {
    ElMessage.error('鍔犺浇榛戝悕鍗曞け璐?);
  } finally {
    loading.value = false;
  }
};

const loadStats = async () => {
  try {
    // 杩欓噷鍙互鏍规嵁瀹為檯闇€姹傝皟鐢ㄧ粺璁℃帴鍙?
    // 鏆傛椂浣跨敤妯℃嫙鏁版嵁
    stats.activeCount = blacklistData.value.filter(item => item.status === 'ACTIVE').length;
    stats.expiredCount = blacklistData.value.filter(item => item.status === 'EXPIRED').length;
    stats.removedCount = blacklistData.value.filter(item => item.status === 'REMOVED').length;
    stats.totalCount = pagination.total;
  } catch (error) {
    console.error('鍔犺浇缁熻淇℃伅澶辫触:', error);
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
    ElMessage.error('鎼滅储鐢ㄦ埛澶辫触');
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
    };
    
    if (addForm.durationType === 'custom' && addForm.endTime) {
      formData.endTime = addForm.endTime;
    }
    
    await adminAPI.addToBlacklist(formData);
    ElMessage.success('娣诲姞榛戝悕鍗曟垚鍔?);
    addDialogVisible.value = false;
    loadBlacklist();
  } catch (error) {
    if (error !== false) {
      ElMessage.error('娣诲姞榛戝悕鍗曞け璐?);
    }
  } finally {
    addLoading.value = false;
  }
};

const handleRemove = async (row) => {
  try {
    await ElMessageBox.confirm(
      `纭畾瑕佸皢 ${row.userRealName} 绉诲嚭榛戝悕鍗曞悧锛焋,
      '纭绉诲嚭',
      {
        confirmButtonText: '纭畾',
        cancelButtonText: '鍙栨秷',
        type: 'warning'
      }
    );
    
    await adminAPI.removeFromBlacklist(row.id);
    ElMessage.success('绉诲嚭榛戝悕鍗曟垚鍔?);
    loadBlacklist();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('绉诲嚭榛戝悕鍗曞け璐?);
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
  padding: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  min-height: calc(100vh - 88px);
}

/* 椤甸潰鏍囬鍖哄煙 */
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
  background: linear-gradient(90deg, #ff6b6b 0%, #ff8e8e 50%, #ff6b6b 100%);
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
  background: linear-gradient(135deg, #ffe6e6 0%, #ffcccc 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
}

.title-icon svg {
  width: 24px;
  height: 24px;
  color: #ff6b6b;
}

.page-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0 0 0 64px;
  font-weight: 400;
}

/* 宸ュ叿鏍?*/
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
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1), 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-form :deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}

.search-form :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.search-form :deep(.el-button:hover) {
  transform: translateY(-1px);
}

/* 缁熻淇℃伅 */
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

/* 琛ㄦ牸瀹瑰櫒 */
.blacklist-card {
  margin: 0 40px 24px;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.blacklist-card :deep(.el-table__header th) {
  background: #f8fafc;
  color: #2d3748;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
}

.blacklist-card :deep(.el-table__row:hover) {
  background: #f7fafc;
}

.blacklist-card :deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.blacklist-card :deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  padding: 20px 0;
  display: flex;
  justify-content: center;
}

.blacklist-card :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.blacklist-card :deep(.el-pagination__total) {
  color: #4a5568;
  font-weight: 500;
  margin-right: 16px;
}

.blacklist-card :deep(.el-pager) {
  display: flex;
  gap: 4px;
}

.blacklist-card :deep(.el-pager li) {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.blacklist-card :deep(.el-pager li:hover) {
  transform: translateY(-1px);
}

.blacklist-card :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff5252 100%);
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 4px 8px rgba(255, 107, 107, 0.3);
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

/* 瀵硅瘽妗嗘牱寮?*/
.blacklist-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.blacklist-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #ffe6e6;
}

.blacklist-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.blacklist-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.blacklist-dialog :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
}

.blacklist-dialog :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.blacklist-dialog :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.blacklist-dialog :deep(.el-input__wrapper.is-focus) {
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.blacklist-dialog :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.blacklist-dialog :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.blacklist-dialog :deep(.el-textarea__inner:focus) {
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.blacklist-dialog :deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

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

/* 鍔ㄧ敾鏁堟灉 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 鍝嶅簲寮忚璁?*/
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

  .blacklist-card {
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
}
</style>

