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
          预约规则配置
        </h1>
        <p class="page-subtitle">管理不同设施类别的预约规则</p>
      </div>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索规则类别..."
          size="large"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleClearSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch" type="primary" size="large">
              搜索
            </el-button>
          </template>
        </el-input>
      </div>
      <div class="button-section">
        <el-button type="primary" size="large" class="add-button" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建规则
        </el-button>
        <el-button size="large" @click="loadRuleConfigs" class="refresh-button">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 规则列表 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">
          <el-icon><Document /></el-icon>
          <span>规则配置列表</span>
          <el-tag type="info" size="small">{{ filteredRuleConfigs.length }} 条规则</el-tag>
        </div>
      </div>
      <el-table :data="filteredRuleConfigs" class="rule-table" v-loading="loading" stripe>
        <el-table-column prop="categoryName" label="规则类别" min-width="140">
          <template #default="scope">
            <el-tag :type="scope.row.categoryId ? 'primary' : 'success'">
              {{ scope.row.categoryName || '全局默认' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="minDurationMinutes" label="最小时长" width="100">
          <template #default="scope">
            {{ scope.row.minDurationMinutes }}分钟
          </template>
        </el-table-column>

        <el-table-column prop="maxDurationMinutes" label="最大时长" width="100">
          <template #default="scope">
            {{ scope.row.maxDurationMinutes }}分钟
          </template>
        </el-table-column>

        <el-table-column prop="advanceDaysMax" label="提前天数" width="100">
          <template #default="scope">
            {{ scope.row.advanceDaysMax }}天
          </template>
        </el-table-column>

        <el-table-column prop="maxBookingsPerDay" label="每日预约" width="100">
          <template #default="scope">
            {{ scope.row.maxBookingsPerDay }}次
          </template>
        </el-table-column>

        <el-table-column prop="maxActiveBookings" label="生效预约" width="100">
          <template #default="scope">
            {{ scope.row.maxActiveBookings }}个
          </template>
        </el-table-column>

        <el-table-column prop="needApproval" label="需要审核" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.needApproval ? 'warning' : 'info'" size="small">
              {{ scope.row.needApproval ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="openTime" label="开放时间" width="260" align="center">
          <template #default="scope">
            {{ scope.row.openTime }} - {{ scope.row.closeTime }}
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="更新时间" width="260" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.updatedAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)" :icon="Edit">
              编辑
            </el-button>
            <el-button type="info" link @click="handleHistory(scope.row)" :icon="Clock">
              历史版本
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>


    <!-- 规则编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="1200px"
      :close-on-click-modal="false"
      class="rule-dialog">
      <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="140px">
        <el-form-item label="规则类别" prop="categoryId">
          <el-select v-model="ruleForm.categoryId" placeholder="选择设施类别" clearable style="width: 100%">
            <el-option label="全局默认规则" :value="null" />
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id" />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="最小预约时长" prop="minDurationMinutes">
              <el-input-number v-model="ruleForm.minDurationMinutes" :min="15" :max="1440" :step="15" style="width: 100%" />
              <div class="form-tip">分钟</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大预约时长" prop="maxDurationMinutes">
              <el-input-number v-model="ruleForm.maxDurationMinutes" :min="30" :max="1440" :step="30" style="width: 100%" />
              <div class="form-tip">分钟</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="时间片粒度" prop="timeSlotMinutes">
              <el-input-number v-model="ruleForm.timeSlotMinutes" :min="15" :max="240" :step="15" style="width: 100%" />
              <div class="form-tip">分钟</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="提前天数" prop="advanceDaysMax">
              <el-input-number v-model="ruleForm.advanceDaysMax" :min="1" :max="365" style="width: 100%" />
              <div class="form-tip">用户可以提前多少天预约</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预约截止时间" prop="advanceCutoffMinutes">
              <el-input-number v-model="ruleForm.advanceCutoffMinutes" :min="5" :max="1440" :step="5" style="width: 100%" />
              <div class="form-tip">开始前多少分钟停止预约</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="取消截止时间" prop="cancelDeadlineMinutes">
              <el-input-number v-model="ruleForm.cancelDeadlineMinutes" :min="5" :max="1440" :step="5" style="width: 100%" />
              <div class="form-tip">开始前多少分钟可取消</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="每日预约次数" prop="maxBookingsPerDay">
              <el-input-number v-model="ruleForm.maxBookingsPerDay" :min="1" :max="20" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="生效预约数" prop="maxActiveBookings">
              <el-input-number v-model="ruleForm.maxActiveBookings" :min="1" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="开放时间" prop="openTime">
              <el-time-picker v-model="ruleForm.openTime" format="HH:mm" value-format="HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="关闭时间" prop="closeTime">
              <el-time-picker v-model="ruleForm.closeTime" format="HH:mm" value-format="HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="当天预约" prop="allowSameDayBooking">
              <el-switch v-model="ruleForm.allowSameDayBooking" />
              <div class="form-tip">是否允许预约当天的设施</div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="需要审核" prop="needApproval">
              <el-switch v-model="ruleForm.needApproval" />
              <div class="form-tip">预约是否需要管理员审核</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 历史版本对话框 -->
    <el-dialog
      v-model="historyDialogVisible"
      title="规则历史版本"
      width="800px"
      class="history-dialog">
      <el-timeline>
        <el-timeline-item
          v-for="rule in ruleHistory"
          :key="rule.id"
          :timestamp="formatDateTime(rule.createdAt)"
          placement="top">
          <el-card>
            <h4>{{ rule.categoryName || '全局默认' }} 规则版本</h4>
            <el-descriptions :column="3" size="small">
              <el-descriptions-item label="最小时长">{{ rule.minDurationMinutes }}分钟</el-descriptions-item>
              <el-descriptions-item label="最大时长">{{ rule.maxDurationMinutes }}分钟</el-descriptions-item>
              <el-descriptions-item label="提前天数">{{ rule.advanceDaysMax }}天</el-descriptions-item>
              <el-descriptions-item label="每日预约">{{ rule.maxBookingsPerDay }}次</el-descriptions-item>
              <el-descriptions-item label="生效预约">{{ rule.maxActiveBookings }}个</el-descriptions-item>
              <el-descriptions-item label="需要审核">
                <el-tag :type="rule.needApproval ? 'warning' : 'info'" size="small">
                  {{ rule.needApproval ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="开放时间">{{ rule.openTime }} - {{ rule.closeTime }}</el-descriptions-item>
              <el-descriptions-item label="当天预约">
                <el-tag :type="rule.allowSameDayBooking ? 'success' : 'info'" size="small">
                  {{ rule.allowSameDayBooking ? '允许' : '不允许' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Setting, Plus, Refresh, Edit, Clock, Search } from '@element-plus/icons-vue';
import { adminAPI, facilityCategoryAPI } from '../../api';

const loading = ref(false);
const submitLoading = ref(false);
const ruleConfigs = ref([]);
const filteredRuleConfigs = ref([]);
const categories = ref([]);
const ruleHistory = ref([]);
const searchKeyword = ref('');

const dialogVisible = ref(false);
const historyDialogVisible = ref(false);
const dialogTitle = ref('');
const ruleFormRef = ref();

const ruleForm = ref({
  categoryId: null,
  minDurationMinutes: 30,
  maxDurationMinutes: 120,
  advanceDaysMax: 7,
  advanceCutoffMinutes: 60,
  allowSameDayBooking: true,
  maxBookingsPerDay: 2,
  maxActiveBookings: 3,
  cancelDeadlineMinutes: 30,
  needApproval: false,
  openTime: '08:00:00',
  closeTime: '22:00:00',
  timeSlotMinutes: 30
});

const rules = {
  categoryId: [{ required: false, message: '请选择设施类别', trigger: 'change' }],
  minDurationMinutes: [{ required: true, message: '请输入最小预约时长', trigger: 'blur' }],
  maxDurationMinutes: [{ required: true, message: '请输入最大预约时长', trigger: 'blur' }],
  advanceDaysMax: [{ required: true, message: '请输入提前天数', trigger: 'blur' }],
  openTime: [{ required: true, message: '请选择开放时间', trigger: 'change' }],
  closeTime: [{ required: true, message: '请选择关闭时间', trigger: 'change' }]
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

const loadRuleConfigs = async () => {
  loading.value = true;
  try {
    const response = await adminAPI.getRuleConfigs();
    ruleConfigs.value = response.data;
    filterRuleConfigs();
  } catch (error) {
    ElMessage.error('加载规则配置失败');
  } finally {
    loading.value = false;
  }
};

const filterRuleConfigs = () => {
  if (!searchKeyword.value.trim()) {
    filteredRuleConfigs.value = ruleConfigs.value;
    return;
  }
  
  const keyword = searchKeyword.value.toLowerCase();
  filteredRuleConfigs.value = ruleConfigs.value.filter(config => {
    const categoryName = config.categoryName || '全局默认';
    return categoryName.toLowerCase().includes(keyword);
  });
};

const handleSearch = () => {
  filterRuleConfigs();
};

const handleClearSearch = () => {
  searchKeyword.value = '';
  filterRuleConfigs();
};

const loadCategories = async () => {
  try {
    const response = await facilityCategoryAPI.list();
    categories.value = response.data;
  } catch (error) {
    ElMessage.error('加载设施类别失败');
  }
};

const handleCreate = () => {
  dialogTitle.value = '新建规则配置';
  ruleForm.value = {
    categoryId: null,
    minDurationMinutes: 30,
    maxDurationMinutes: 120,
    advanceDaysMax: 7,
    advanceCutoffMinutes: 60,
    allowSameDayBooking: true,
    maxBookingsPerDay: 2,
    maxActiveBookings: 3,
    cancelDeadlineMinutes: 30,
    needApproval: false,
    openTime: '08:00:00',
    closeTime: '22:00:00',
    timeSlotMinutes: 30
  };
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  dialogTitle.value = '编辑规则配置';
  ruleForm.value = { ...row };
  dialogVisible.value = true;
};

const handleHistory = async (row) => {
  try {
    const response = await adminAPI.getRuleConfigHistory(row.categoryId);
    ruleHistory.value = response.data;
    historyDialogVisible.value = true;
  } catch (error) {
    ElMessage.error('加载历史版本失败');
  }
};

const handleSubmit = async () => {
  try {
    await ruleFormRef.value.validate();
    submitLoading.value = true;
    
    await adminAPI.createRuleConfig(ruleForm.value);
    ElMessage.success('规则配置保存成功');
    dialogVisible.value = false;
    loadRuleConfigs();
  } catch (error) {
    if (error !== false) {
      ElMessage.error('规则配置保存失败');
    }
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  loadRuleConfigs();
  loadCategories();
});
</script>

<style scoped>
.rule-config {
  padding: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  min-height: calc(100vh - 88px);
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

.title-icon :deep(.el-icon) {
  font-size: 24px;
  color: #409eff;
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

.search-input {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  height: 48px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1), 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__inner) {
  font-size: 15px;
  height: 46px;
  font-weight: 500;
}

.search-input :deep(.el-input-group__append) {
  border-radius: 0 12px 12px 0;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
}

.search-input :deep(.el-input-group__append .el-button) {
  border-radius: 0 12px 12px 0;
  background: transparent;
  border: none;
  color: white;
  font-weight: 600;
}

.button-section {
  flex-shrink: 0;
}

.add-button {
  border-radius: 12px;
  font-weight: 600;
  height: 48px;
  padding: 0 24px;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.add-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
}

.refresh-button {
  border-radius: 12px;
  font-weight: 600;
  height: 48px;
  padding: 0 24px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.refresh-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-color: #cbd5e0;
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin: 0 40px 24px;
}

.table-header {
  padding: 16px 24px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.table-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
}

.table-title .el-icon {
  color: #409eff;
}

.rule-table :deep(.el-table__header th) {
  background: #f8fafc;
  color: #2d3748;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
  padding: 12px 8px;
}

.rule-table :deep(.el-table__row:hover) {
  background: #f7fafc;
}

.rule-table :deep(.el-tag) {
  border-radius: 6px;
  font-weight: 500;
}

.rule-table :deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 4px 8px;
}

.rule-table :deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.rule-table :deep(.el-table__cell) {
  padding: 12px 8px;
}

.rule-table {
  width: 100%;
}

.rule-table :deep(.el-table) {
  width: 100% !important;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .rule-dialog :deep(.el-dialog) {
    width: 90% !important;
    max-width: 700px;
  }

  .rule-dialog :deep(.el-col-8) {
    span: 12;
  }
}

@media (max-width: 768px) {
  .rule-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin-top: 5vh !important;
  }

  .rule-dialog :deep(.el-col-8) {
    span: 24;
  }

  .rule-dialog :deep(.el-form-item__label) {
    width: 100% !important;
    text-align: left;
    padding-bottom: 4px;
  }

  .rule-dialog :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
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

.form-tip {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
  line-height: 1.3;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
}

:deep(.el-descriptions__label) {
  color: #606266;
  font-weight: 500;
}

/* 对话框样式 */
.rule-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.rule-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.rule-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.rule-dialog :deep(.el-dialog__body) {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.rule-dialog :deep(.el-form-item) {
  margin-bottom: 20px;
}

.rule-dialog :deep(.el-form-item__label) {
  font-weight: 500;
  color: #4a5568;
}

.rule-dialog :deep(.el-input-number) {
  width: 100% !important;
}

.rule-dialog :deep(.el-time-picker) {
  width: 100% !important;
}

.rule-dialog :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
}

.rule-dialog :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.rule-dialog :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.rule-dialog :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.rule-dialog :deep(.el-input-number) {
  width: 100% !important;
}

.rule-dialog :deep(.el-input-number__decrease),
.rule-dialog :deep(.el-input-number__increase) {
  border-radius: 0 6px 6px 0;
}

.rule-dialog :deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

.rule-dialog :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.rule-dialog :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.rule-dialog :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.history-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.history-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.history-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.history-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.history-dialog :deep(.el-timeline-item__timestamp) {
  color: #718096;
  font-weight: 500;
}

.history-dialog :deep(.el-timeline-item__content) {
  color: #2d3748;
}
</style>