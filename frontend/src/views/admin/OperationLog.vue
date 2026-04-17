<template>
  <div class="operation-log">
    <!-- 椤甸潰鏍囬鍖哄煙 -->
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
          鎿嶄綔鏃ュ織瀹¤
        </h1>
        <p class="page-subtitle">鏌ョ湅绯荤粺鎿嶄綔璁板綍锛屼究浜庨棶棰樻帓鏌ュ拰璐ｄ换杩借釜</p>
      </div>
    </div>

    <!-- 鎼滅储鍜屽伐鍏锋爮 -->
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
            
            <el-form-item label="鎿嶄綔绫诲瀷">
              <el-select v-model="searchForm.operationType" placeholder="閫夋嫨鎿嶄綔绫诲瀷" clearable style="width: 140px">
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
            
            <el-form-item label="缁撴潫鏃堕棿">
              <el-date-picker
                v-model="searchForm.endTime"
                type="datetime"
                placeholder="閫夋嫨缁撴潫鏃堕棿"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 160px" />
            </el-form-item>
            
            <el-form-item class="button-group">
              <el-button type="primary" @click="handleSearch" :icon="Search" size="default" :loading="loading">鎼滅储</el-button>
              <el-button @click="resetSearch" :icon="Refresh" size="default">閲嶇疆</el-button>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 鎿嶄綔鏃ュ織鍒楄〃 -->
    <div class="table-container">
      <el-empty v-if="!loading && logData.length === 0 && (searchForm.operatorId || searchForm.operationType || searchForm.startTime || searchForm.endTime)" description="鏈壘鍒扮鍚堟潯浠剁殑鎿嶄綔鏃ュ織">
        <el-button type="primary" @click="resetSearch">娓呴櫎鎼滅储鏉′欢</el-button>
      </el-empty>
      <el-table v-else :data="logData" class="operation-table" v-loading="loading" stripe>
        <el-table-column prop="operatorName" label="操作人" width="140" />
        <el-table-column prop="operationType" label="鎿嶄綔绫诲瀷" width="160">
          <template #default="scope">
            <el-tag :type="getOperationTypeType(scope.row.operationType)" size="small">
              {{ getOperationTypeText(scope.row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="鐩爣ID" width="100" align="center"/>
        <el-table-column prop="detail" label="鎿嶄綔璇︽儏" min-width="200" show-overflow-tooltip  align="center"/>
        <el-table-column prop="ipAddress" label="IP鍦板潃" width="160" align="center"/>
        <el-table-column prop="createdAt" label="鎿嶄綔鏃堕棿" width="220" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="鎿嶄綔" width="80" fixed="right" align="center">
          <template #default="scope">
            <el-button type="info" link @click="handleView(scope.row)" :icon="View">
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
          @current-change="handleCurrentChange"
          class="custom-pagination" />
      </div>
    </div>

    <!-- 璇︽儏瀵硅瘽妗?-->
    <el-dialog
      v-model="detailDialogVisible"
      title="鎿嶄綔鏃ュ織璇︽儏"
      width="600px"
      class="detail-dialog">
      <el-descriptions :column="1" border v-if="currentDetail">
        <el-descriptions-item label="操作人">
          {{ currentDetail.operatorName || '绯荤粺' }}
        </el-descriptions-item>
        <el-descriptions-item label="鎿嶄綔绫诲瀷">
          <el-tag :type="getOperationTypeType(currentDetail.operationType)" size="small">
            {{ getOperationTypeText(currentDetail.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="鐩爣ID">
          {{ currentDetail.targetId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="鎿嶄綔璇︽儏">
          {{ currentDetail.detail || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="IP鍦板潃">
          {{ currentDetail.ipAddress || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="鎿嶄綔鏃堕棿">
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

// 鎼滅储鍔熻兘鏀逛负鎵嬪姩瑙﹀彂锛屼笉鍐嶈嚜鍔ㄧ洃鍚〃鍗曞彉鍖?
// 淇濈暀鎼滅储瓒呮椂鏈哄埗鐢ㄤ簬闃叉姈
let searchTimeout = null;

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});



const detailDialogVisible = ref(false);

// 鏃堕棿閫夋嫨鍣ㄥ揩鎹烽�夐」
const startTimeShortcuts = [
  {
    text: '浠婂ぉ',
    value: () => {
      const date = new Date();
      date.setHours(0, 0, 0, 0);
      return date;
    }
  },
  {
    text: '鏄ㄥぉ',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() - 1);
      date.setHours(0, 0, 0, 0);
      return date;
    }
  },
  {
    text: '7澶╁墠',
    value: () => {
      const date = new Date();
      date.setDate(date.getDate() - 7);
      date.setHours(0, 0, 0, 0);
      return date;
    }
  },
  {
    text: '30澶╁墠',
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
    text: '鐜板湪',
    value: () => new Date()
  },
  {
    text: '浠婂ぉ',
    value: () => {
      const date = new Date();
      date.setHours(23, 59, 59, 999);
      return date;
    }
  },
  {
    text: '鏄ㄥぉ',
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
    ElMessage.error('鍔犺浇鎿嶄綔绫诲瀷澶辫触');
  }
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

const getOperationTypeType = (type) => {
  const types = {
    'APPROVE_RESERVATION': 'success',
    'APPROVE_BOOKING': 'success',
    'REJECT_RESERVATION': 'danger',
    'REJECT_BOOKING': 'danger',
    'VERIFY_CHECKIN': 'primary',
    'VERIFY_CHECKOUT': 'primary',
    'ADD_BLACKLIST': 'danger',
    'REMOVE_BLACKLIST': 'success',
    'AUTO_EXPIRE_BLACKLIST': 'warning',
    'REPLY_FEEDBACK': 'info',
    'UPDATE_FEEDBACK_STATUS': 'warning',
    'DELETE_FEEDBACK': 'danger',
    'UPDATE_RULE': 'warning',
    'DELETE_RULE': 'danger',
    'CREATE_FACILITY': 'success',
    'UPDATE_FACILITY': 'primary',
    'UPDATE_FACILITY_STATUS': 'warning',
    'DELETE_FACILITY': 'danger',
    'UPLOAD_FACILITY_IMAGE': 'info',
    'DELETE_FACILITY_IMAGE': 'warning',
    'CREATE_FACILITY_CATEGORY': 'success',
    'UPDATE_FACILITY_CATEGORY': 'primary',
    'DELETE_FACILITY_CATEGORY': 'danger',
    'TOGGLE_FACILITY_CATEGORY_STATUS': 'warning',
    'CREATE_NOTICE': 'info',
    'PUBLISH_NOTICE': 'info',
    'UPDATE_NOTICE': 'info',
    'DELETE_NOTICE': 'danger',
    'PUBLISH_SCHEDULED_NOTICE': 'info',
    'CREATE_MAINTENANCE': 'success',
    'UPDATE_MAINTENANCE': 'primary',
    'COMPLETE_MAINTENANCE': 'success',
    'MAINTENANCE_COMPLETE': 'success',
    'DELETE_MAINTENANCE': 'danger',
    'CREATE_VIOLATION': 'warning',
    'APPROVE_VIOLATION': 'success',
    'REJECT_VIOLATION': 'danger',
    'REVOKE_VIOLATION': 'warning',
    'DELETE_BOOKING': 'danger'
  };
  return types[type] || 'info';
};
const getOperationTypeText = (type) => {
  const texts = {
    'APPROVE_RESERVATION': '审核通过预约',
    'APPROVE_BOOKING': '审核通过预约',
    'REJECT_RESERVATION': '拒绝预约',
    'REJECT_BOOKING': '拒绝预约',
    'VERIFY_CHECKIN': '核校签到',
    'VERIFY_CHECKOUT': '核校签退',
    'ADD_BLACKLIST': '加入黑名单',
    'REMOVE_BLACKLIST': '移出黑名单',
    'AUTO_EXPIRE_BLACKLIST': '自动过期黑名单',
    'REPLY_FEEDBACK': '回复反馈',
    'UPDATE_FEEDBACK_STATUS': '更新反馈状态',
    'DELETE_FEEDBACK': '删除反馈',
    'UPDATE_RULE': '更新规则',
    'DELETE_RULE': '删除规则',
    'CREATE_FACILITY': '创建设施',
    'UPDATE_FACILITY': '更新设施',
    'UPDATE_FACILITY_STATUS': '更新设施状态',
    'DELETE_FACILITY': '删除设施',
    'UPLOAD_FACILITY_IMAGE': '上传设施图片',
    'DELETE_FACILITY_IMAGE': '删除设施图片',
    'CREATE_FACILITY_CATEGORY': '创建设施分类',
    'UPDATE_FACILITY_CATEGORY': '更新设施分类',
    'DELETE_FACILITY_CATEGORY': '删除设施分类',
    'TOGGLE_FACILITY_CATEGORY_STATUS': '切换设施分类状态',
    'CREATE_NOTICE': '发布通知',
    'PUBLISH_NOTICE': '发布通知',
    'UPDATE_NOTICE': '更新通知',
    'DELETE_NOTICE': '删除通知',
    'PUBLISH_SCHEDULED_NOTICE': '发布定时通知',
    'CREATE_MAINTENANCE': '创建维护任务',
    'UPDATE_MAINTENANCE': '更新维护任务',
    'COMPLETE_MAINTENANCE': '完成维护',
    'MAINTENANCE_COMPLETE': '完成维护',
    'DELETE_MAINTENANCE': '删除维护任务',
    'CREATE_VIOLATION': '创建违规记录',
    'APPROVE_VIOLATION': '确认违规记录',
    'REJECT_VIOLATION': '驳回违规记录',
    'REVOKE_VIOLATION': '取消生效违规记录',
    'DELETE_BOOKING': '删除预约'
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
    
    // 娣诲姞鎼滅储鏉′欢
    if (searchForm.operatorId) {
      params.operatorId = searchForm.operatorId;
    }
    if (searchForm.operationType) {
      params.operationType = searchForm.operationType;
    }
    if (searchForm.startTime) {
      params.startTime = searchForm.startTime;
      console.log('寮�濮嬫椂闂村弬鏁?', params.startTime);
    }
    if (searchForm.endTime) {
      params.endTime = searchForm.endTime;
      console.log('缁撴潫鏃堕棿鍙傛暟:', params.endTime);
    }
    
    const response = await adminAPI.getOperationLogs(params);
    const data = response.data;
    
    // 澶勭悊涓嶅悓鐨勫搷搴旀牸寮?
    let content = [];
    let total = 0;
    
    if (data.content && Array.isArray(data.content)) {
      // Spring Data 鏍囧噯鏍煎紡
      content = data.content;
      total = data.totalElements || 0;
    } else if (Array.isArray(data)) {
      // 鐩存帴鏁扮粍鏍煎紡
      content = data;
      total = data.length;
    } else if (data.data && Array.isArray(data.data)) {
      // 宓屽data鏍煎紡
      content = data.data;
      total = data.total || data.totalElements || data.data.length;
    } else {
      console.warn('鏈鏈熺殑鏁版嵁鏍煎紡:', data);
      content = [];
      total = 0;
    }
    
    // 鎸夋椂闂村�掑簭鎺掑簭锛堟渶鏂扮殑鍦ㄥ墠锛?
    content.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    
    // 瀹㈡埛绔繃婊わ紙浠呭湪鍚庣鏈纭繃婊ゆ椂鎵ц锛?
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
    
    // 鎸夋椂闂村�掑簭鎺掑簭锛堟渶鏂扮殑鍦ㄥ墠锛?
    content.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    logData.value = content;
    pagination.total = total;
  } catch (error) {
    console.error('鍔犺浇鎿嶄綔鏃ュ織澶辫触:', error);
    ElMessage.error('鍔犺浇鎿嶄綔鏃ュ織澶辫触');
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
  // 娓呴櫎涔嬪墠鐨勫畾鏃跺櫒
  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }
  
  // 璁剧疆鏂扮殑瀹氭椂鍣紝瀹炵幇闃叉姈
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

/* 宸ュ叿鏍?*/
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

/* 鍝嶅簲寮忚璁?*/
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



/* 琛ㄦ牸瀹瑰櫒 */
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

/* 鍒嗛〉 */
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

/* 璇︽儏瀵硅瘽妗?*/
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


