<template>
  <div class="violation-report-page">
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
          违规记录上报
        </h1>
        <p class="page-subtitle">上报用户违规行为，维护设施使用秩序</p>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 违规记录上报表单 -->
      <el-card class="report-form-card" header="违规记录上报">
        <el-form
          ref="violationFormRef"
          :model="violationForm"
          :rules="violationRules"
          label-width="120px"
          class="violation-form"
        >
          <el-form-item label="违规用户" prop="userId" required>
            <el-select
              v-model="violationForm.userId"
              placeholder="请选择违规用户"
              filterable
              clearable
              style="width: 100%"
              @change="handleUserChange"
            >
              <el-option
                v-for="user in userList"
                :key="user.id"
                :label="(user.realName || user.username) + ' (' + (user.roleName || user.role || '用户') + ')'"
                :value="user.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="违规类型" prop="violationType" required>
            <el-select
              v-model="violationForm.violationType"
              placeholder="请选择违规类型"
              style="width: 100%"
            >
              <el-option label="破坏设施" value="破坏设施" />
              <el-option label="超时使用" value="超时使用" />
              <el-option label="未按时归还" value="未按时归还" />
              <el-option label="违规操作" value="违规操作" />
              <el-option label="其他违规" value="其他违规" />
            </el-select>
          </el-form-item>

          <el-form-item label="相关预约" prop="reservationId">
            <el-select
              v-model="violationForm.reservationId"
              placeholder="请选择相关预约（可选）"
              filterable
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="reservation in userReservations"
                :key="reservation.id"
                :label="`${reservation.facilityName} - ${formatDate(reservation.startTime)}`"
                :value="reservation.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="处罚扣分" prop="penaltyPoints" required>
            <el-input-number
              v-model="violationForm.penaltyPoints"
              :min="1"
              :max="100"
              placeholder="请输入处罚扣分"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="违规描述" prop="description" required>
            <el-input
              v-model="violationForm.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述违规情况，包括时间、地点、具体行为等"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitViolation" :loading="submitting">
              提交上报
            </el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 上报记录列表 -->
      <el-card class="records-card" header="我的上报记录">
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

        <el-table
          :data="violationRecords"
          v-loading="loading"
          style="width: 100%"
          :default-sort="{ prop: 'reportedTime', order: 'descending' }"
        >
          <el-table-column prop="userName" label="违规用户" width="120" />
          <el-table-column prop="violationType" label="违规类型" width="120" />
          <el-table-column prop="description" label="违规描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="penaltyPoints" label="扣分" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="getPointsType(row.penaltyPoints)" size="small">
                -{{ row.penaltyPoints }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reportedTime" label="上报时间" width="180" sortable>
            <template #default="{ row }">
              {{ formatDateTime(row.reportedTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                link
                size="small"
                @click="viewDetails(row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="违规记录详情"
      width="600px"
    >
      <div v-if="currentViolation" class="violation-details">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="违规用户">
            {{ currentViolation.userName }}
          </el-descriptions-item>
          <el-descriptions-item label="违规类型">
            {{ currentViolation.violationType }}
          </el-descriptions-item>
          <el-descriptions-item label="违规描述">
            {{ currentViolation.description }}
          </el-descriptions-item>
          <el-descriptions-item label="处罚扣分">
            <el-tag :type="getPointsType(currentViolation.penaltyPoints)">
              -{{ currentViolation.penaltyPoints }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentViolation.status)">
              {{ getStatusText(currentViolation.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="上报时间">
            {{ formatDateTime(currentViolation.reportedTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(currentViolation.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh } from '@element-plus/icons-vue';
import { userAPI, reservationAPI } from '@/api';
import { violationAPI } from '@/api/violation';

// 响应式数据
const violationFormRef = ref();
const violationForm = reactive({
  userId: '',
  violationType: '',
  reservationId: '',
  penaltyPoints: 5,
  description: ''
});

const violationRules = {
  userId: [{ required: true, message: '请选择违规用户', trigger: 'change' }],
  violationType: [{ required: true, message: '请选择违规类型', trigger: 'change' }],
  penaltyPoints: [{ required: true, message: '请输入处罚扣分', trigger: 'blur' }],
  description: [{ required: true, message: '请输入违规描述', trigger: 'blur' }]
};

const userList = ref([]);
const userReservations = ref([]);
const violationRecords = ref([]);
const loading = ref(false);
const submitting = ref(false);
const searchDescription = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const detailDialogVisible = ref(false);
const currentViolation = ref(null);

// 计算属性
const filteredRecords = computed(() => {
  if (!searchDescription.value) {
    return violationRecords.value;
  }
  return violationRecords.value.filter(record => 
    record.description.toLowerCase().includes(searchDescription.value.toLowerCase())
  );
});

// 生命周期
onMounted(() => {
  loadUsers();
  loadViolationRecords();
});

// 方法
const loadUsers = async () => {
  try {
    const response = await userAPI.list({ page: 0, size: 1000 });
    if (response && response.data) {
      // 确保用户数据有正确的显示名称
      userList.value = response.data.content || response.data || [];
      
      // 处理用户显示名称
      userList.value.forEach(user => {
        if (!user.realName && !user.username) {
          user.realName = '未知用户';
        }
      });
      
      console.log('加载用户列表成功，共', userList.value.length, '个用户');
    } else {
      userList.value = [];
      console.warn('用户列表响应格式异常:', response);
    }
  } catch (error) {
    console.error('加载用户列表失败:', error);
    ElMessage.error('加载用户列表失败: ' + error.message);
  }
};

const handleUserChange = async (userId) => {
  if (userId) {
    try {
      const response = await reservationAPI.getByUserId(userId);
      userReservations.value = response.data || [];
    } catch (error) {
      console.error('加载用户预约记录失败:', error);
      userReservations.value = [];
    }
  } else {
    userReservations.value = [];
  }
};

const submitViolation = async () => {
  try {
    await violationFormRef.value.validate();
    
    submitting.value = true;
    
    // 将中文违规类型转换为数据库枚举值
    const violationTypeMap = {
      '破坏设施': 'DAMAGE',
      '超时使用': 'OVERDUE',
      '未按时归还': 'OTHER',
      '违规操作': 'OTHER',
      '其他违规': 'OTHER'
    };
    
    const mappedViolationType = violationTypeMap[violationForm.violationType] || 'OTHER';
    
    // 获取当前用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    const violationDataWithReporter = {
      ...violationForm,
      violationType: mappedViolationType,
      reportedBy: userInfo.id,
      reportedTime: formatDateTimeForBackend(new Date())
    };
    
    await violationAPI.reportViolation(violationDataWithReporter);
    
    ElMessage.success('违规记录上报成功');
    resetForm();
    loadViolationRecords();
  } catch (error) {
    if (error.errors) {
      ElMessage.error('请完善表单信息');
    } else {
      console.error('上报违规记录失败:', error);
      ElMessage.error('上报违规记录失败');
    }
  } finally {
    submitting.value = false;
  }
};

const resetForm = () => {
  violationFormRef.value?.resetFields();
  violationForm.reservationId = '';
  userReservations.value = [];
};

const loadViolationRecords = async () => {
  try {
    loading.value = true;
    // 获取当前用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    
    const response = await violationAPI.getMaintainerViolations({
      page: currentPage.value - 1,
      size: pageSize.value,
      maintainerId: userInfo.id
    });
    
    if (response.data) {
      violationRecords.value = response.data.content || [];
      total.value = response.data.totalElements || 0;
    }
  } catch (error) {
    console.error('加载违规记录失败:', error);
    ElMessage.error('加载违规记录失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadViolationRecords();
};

const refreshData = () => {
  currentPage.value = 1;
  searchDescription.value = '';
  loadViolationRecords();
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadViolationRecords();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  loadViolationRecords();
};

const viewDetails = (record) => {
  currentViolation.value = record;
  detailDialogVisible.value = true;
};

const getPointsType = (points) => {
  if (points >= 20) return 'danger';
  if (points >= 10) return 'warning';
  return 'info';
};

const getStatusType = (status) => {
  return status === 'PENDING' ? 'warning' : 'success';
};

const getStatusText = (status) => {
  return status === 'PENDING' ? '待处理' : '已处理';
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

const formatDate = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleDateString('zh-CN');
};

const formatDateTimeForBackend = (date) => {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
</script>

<style scoped>
.violation-report-page {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
  position: relative;
}

.header-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  border-radius: 2px;
}

.header-content {
  margin-left: 16px;
}

.page-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.title-icon {
  width: 32px;
  height: 32px;
  margin-right: 12px;
  color: #ff6b6b;
}

.page-subtitle {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.report-form-card, .records-card {
  min-height: 400px;
}

.violation-form {
  max-width: 600px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.search-container {
  flex: 1;
  max-width: 300px;
}

.search-input {
  width: 100%;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.violation-details {
  line-height: 1.6;
}

@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
}
</style>