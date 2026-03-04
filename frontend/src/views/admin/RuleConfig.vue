<template>
  <div class="rule-config">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><Setting /></el-icon>
        预约规则配置
      </h1>
      <p class="page-subtitle">管理不同设施类别的预约规则</p>
    </div>

    <!-- 操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" @click="handleCreate" :icon="Plus">
        新建规则
      </el-button>
      <el-button @click="loadRuleConfigs" :icon="Refresh">
        刷新
      </el-button>
    </div>

    <!-- 规则列表 -->
    <el-card class="rule-list-card">
      <el-table :data="ruleConfigs" style="width: 100%" v-loading="loading">
        <el-table-column prop="categoryName" label="规则类别" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.categoryId ? 'primary' : 'success'">
              {{ scope.row.categoryName || '全局默认' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="minDurationMinutes" label="最小时长" width="80">
          <template #default="scope">
            {{ scope.row.minDurationMinutes }}分钟
          </template>
        </el-table-column>
        
        <el-table-column prop="maxDurationMinutes" label="最大时长" width="80">
          <template #default="scope">
            {{ scope.row.maxDurationMinutes }}分钟
          </template>
        </el-table-column>
        
        <el-table-column prop="advanceDaysMax" label="提前天数" width="80">
          <template #default="scope">
            {{ scope.row.advanceDaysMax }}天
          </template>
        </el-table-column>
        
        <el-table-column prop="maxBookingsPerDay" label="每日预约" width="80">
          <template #default="scope">
            {{ scope.row.maxBookingsPerDay }}次
          </template>
        </el-table-column>
        
        <el-table-column prop="maxActiveBookings" label="生效预约" width="80">
          <template #default="scope">
            {{ scope.row.maxActiveBookings }}个
          </template>
        </el-table-column>
        
        <el-table-column prop="needApproval" label="需要审核" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.needApproval ? 'warning' : 'info'" size="small">
              {{ scope.row.needApproval ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="openTime" label="开放时间" width="100">
          <template #default="scope">
            {{ scope.row.openTime }} - {{ scope.row.closeTime }}
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
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
    </el-card>

    <!-- 规则编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false">
      <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="120px">
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
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最小预约时长" prop="minDurationMinutes">
              <el-input-number v-model="ruleForm.minDurationMinutes" :min="15" :max="1440" :step="15" style="width: 100%" />
              <div class="form-tip">分钟</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大预约时长" prop="maxDurationMinutes">
              <el-input-number v-model="ruleForm.maxDurationMinutes" :min="30" :max="1440" :step="30" style="width: 100%" />
              <div class="form-tip">分钟</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="提前天数" prop="advanceDaysMax">
          <el-input-number v-model="ruleForm.advanceDaysMax" :min="1" :max="365" style="width: 100%" />
          <div class="form-tip">用户可以提前多少天预约</div>
        </el-form-item>
        
        <el-form-item label="预约截止时间" prop="advanceCutoffMinutes">
          <el-input-number v-model="ruleForm.advanceCutoffMinutes" :min="5" :max="1440" :step="5" style="width: 100%" />
          <div class="form-tip">预约开始前多少分钟停止预约</div>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="每日预约次数" prop="maxBookingsPerDay">
              <el-input-number v-model="ruleForm.maxBookingsPerDay" :min="1" :max="20" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生效预约数" prop="maxActiveBookings">
              <el-input-number v-model="ruleForm.maxActiveBookings" :min="1" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="取消截止时间" prop="cancelDeadlineMinutes">
          <el-input-number v-model="ruleForm.cancelDeadlineMinutes" :min="5" :max="1440" :step="5" style="width: 100%" />
          <div class="form-tip">预约开始前多少分钟可取消</div>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开放时间" prop="openTime">
              <el-time-picker v-model="ruleForm.openTime" format="HH:mm" value-format="HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关闭时间" prop="closeTime">
              <el-time-picker v-model="ruleForm.closeTime" format="HH:mm" value-format="HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="时间片粒度" prop="timeSlotMinutes">
          <el-input-number v-model="ruleForm.timeSlotMinutes" :min="15" :max="240" :step="15" style="width: 100%" />
          <div class="form-tip">预约时间的最小单位（分钟）</div>
        </el-form-item>
        
        <el-form-item label="当天预约" prop="allowSameDayBooking">
          <el-switch v-model="ruleForm.allowSameDayBooking" />
          <div class="form-tip">是否允许用户预约当天的设施</div>
        </el-form-item>
        
        <el-form-item label="需要审核" prop="needApproval">
          <el-switch v-model="ruleForm.needApproval" />
          <div class="form-tip">预约是否需要管理员审核</div>
        </el-form-item>
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
      width="800px">
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
import { Setting, Plus, Refresh, Edit, Clock } from '@element-plus/icons-vue';
import { adminAPI, facilityCategoryAPI } from '../../api';

const loading = ref(false);
const submitLoading = ref(false);
const ruleConfigs = ref([]);
const categories = ref([]);
const ruleHistory = ref([]);

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
  } catch (error) {
    ElMessage.error('加载规则配置失败');
  } finally {
    loading.value = false;
  }
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
  color: #409eff;
}

.page-subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.rule-list-card {
  margin-bottom: 20px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
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
</style>