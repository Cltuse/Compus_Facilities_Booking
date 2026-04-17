<template>
  <div class="maintainer-maintenance">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          我的维护任务
        </h1>
        <p class="page-subtitle">管理您的设施维护任务</p>
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operations-container">
      <el-row :gutter="20" align="middle">
        <el-col :span="16">
          <el-space>
            <!-- 维护状态筛选（已维护?未维护?所有记录）- 置顶显示 -->
            <el-select
                v-model="maintenanceStatusFilter"
                placeholder="维护状态"
                style="width: 130px"
                clearable
                @change="handleMaintenanceStatusFilter"
            >
              <el-option label="所有记录" value="" />
              <el-option label="已维护" value="MAINTAINED" />
              <el-option label="未维护" value="UNMAINTAINED" />
            </el-select>
            
            <!-- 任务状态筛选 -->
            <el-select
                v-model="statusFilter"
                placeholder="任务状态"
                style="width: 130px"
                clearable
                @change="handleStatusFilter"
            >
              <el-option label="所有记录" value="" />
              <el-option label="待处理" value="PENDING" />
              <el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-space>
        </el-col>
        <el-col :span="8" class="text-right">
          <el-button type="primary" @click="handleCreate" :icon="Plus">新建维护任务</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选统计信息 -->
    <div class="filter-stats" v-if="total > 0 || statusFilter || maintenanceStatusFilter">
      <el-tag type="info" size="small" v-if="maintenanceStatusFilter">
        {{ getMaintenanceStatusFilterText(maintenanceStatusFilter) }}: {{ total }} 条记录
      </el-tag>
      <el-tag type="info" size="small" v-if="statusFilter" style="margin-left: 8px;">
        {{ getMaintenanceStatusText(statusFilter) }}: {{ total }} 条记录
      </el-tag>
      <el-tag type="warning" size="small" v-if="total === 0 && (statusFilter || maintenanceStatusFilter)" style="margin-left: 8px;">
        当前筛选条件下无记录
      </el-tag>
    </div>

    <!-- 维护任务表格 -->
    <div class="table-container">
      <el-table
          :data="maintenanceList"
          class="maintenance-table"
          :header-cell-style="headerCellStyle"
          :cell-style="cellStyle"
          @row-click="handleRowClick"
          v-loading="loading"
          stripe
      >
        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column prop="facilityName" label="设施名称" min-width="150">
          <template #default="{ row }">
            <div class="facility-info">
              <el-icon><Box /></el-icon>
              <span class="facility-name">{{ row.facilityName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="maintenanceType" label="维护类型" width="100">
          <template #default="{ row }">
            <el-tag
                :type="getMaintenanceTypeTag(row.maintenanceType)"
                size="small"
            >
              {{ getMaintenanceTypeText(row.maintenanceType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="maintainer" label="维护人员" width="100">
          <template #default="{ row }">
            <span>{{ row.maintainer || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="维护描述" min-width="180" align="center">
          <template #default="{ row }">
            <div class="description-info">{{ row.description }}</div>
          </template>
        </el-table-column>

        <el-table-column label="计划时间" width="180" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatMaintenanceTime(row) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="任务状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
                :type="getMaintenanceStatusType(row.status)"
                class="status-tag"
                effect="light"
            >
              {{ getMaintenanceStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="cost" label="费用" width="70">
          <template #default="{ row }">
            <span>{{ row.cost ? `¥${row.cost}` : '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="result" label="结果" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.result || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                  size="small"
                  type="primary"
                  :plain="true"
                  class="action-btn edit-btn"
                  @click.stop="handleEdit(row)"
                  :disabled="row.status === 'COMPLETED' || row.status === 'CANCELLED'"
              >
                <el-icon><EditPen /></el-icon>
                编辑
              </el-button>
              <el-button
                  size="small"
                  type="success"
                  :plain="true"
                  class="action-btn complete-btn"
                  @click.stop="handleComplete(row)"
                  :disabled="row.status !== 'PENDING' && row.status !== 'IN_PROGRESS'"
              >
                <el-icon><Check /></el-icon>
                完成
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            class="custom-pagination"
        />
      </div>

    </div>

    <!-- 编辑/创建对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        class="maintenance-dialog"
        :close-on-click-modal="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">{{ dialogTitle }}</span>
      </div>

      <div class="dialog-content">
        <el-form
            :model="form"
            :rules="rules"
            ref="formRef"
            class="maintenance-form"
            label-width="100px"
        >
          <el-form-item label="设施名称" prop="facilityId" align="center">
            <el-select
                v-model="form.facilityId"
                placeholder="直接选择或搜索选择设施"
                style="width: 100%"
                :disabled="!!currentRow.id"
                filterable
                remote
                :remote-method="searchFacilities"
                :loading="facilityLoading"
            >
              <el-option
                  v-for="facility in filteredFacilityOptions"
                  :key="facility.id"
                  :label="facility.name"
                  :value="facility.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="维护人员" prop="maintainer" align="center">
            <el-input
                v-model="form.maintainer"
                placeholder="请输入维护人员姓名或用户名"
                maxlength="50"
                :disabled="true"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="维护类型" prop="maintenanceType" align="center">
            <el-radio-group v-model="form.maintenanceType">
              <el-radio label="ROUTINE">常规维护</el-radio>
              <el-radio label="REPAIR">故障维修</el-radio>
              <el-radio label="UPGRADE">设备升级</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="维护描述" prop="description" align="center">
            <el-input
                type="textarea"
                v-model="form.description"
                :rows="4"
                placeholder="请输入维护任务的详细描述..."
                maxlength="500"
                show-word-limit
            />
          </el-form-item>

          <el-form-item label="开始时间" prop="startTime" align="center">
            <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="选择开始时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
            />
          </el-form-item>

          <!-- 新增时隐藏以下字段 -->
          <template v-if="form.id">
            <el-form-item label="结束时间" prop="endTime" align="center">
              <el-date-picker
                  v-model="form.endTime"
                  type="datetime"
                  placeholder="选择结束时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="维护结果" prop="result" align="center">
              <el-input
                  type="textarea"
                  v-model="form.result"
                  :rows="3"
                  placeholder="请输入维护结果..."
                  maxlength="500"
                  show-word-limit
              />
            </el-form-item>

            <el-form-item label="费用" prop="cost" align="center">
              <el-input-number
                  v-model="form.cost"
                  :precision="2"
                  :step="0.1"
                  :min="0"
                  placeholder="请输入费用..."
                  style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="状态" prop="status" align="center">
              <el-select v-model="form.status" style="width: 100%" placeholder="请选择状态">
                <el-option label="待处理" value="PENDING" />
                <el-option label="进行中" value="IN_PROGRESS" />
                <el-option label="已完成" value="COMPLETED" />
                <el-option label="已取消" value="CANCELLED" />
              </el-select>
            </el-form-item>
          </template>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogVisible = false" class="cancel-btn">
            取消
          </el-button>
          <el-button
              type="primary"
              size="large"
              :loading="submitLoading"
              @click="handleSubmit"
              class="submit-btn"
          >
            确认提交
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 维护完成结果输入对话框 -->
    <el-dialog
        v-model="completeDialogVisible"
        title="维护完成确认"
        width="500px"
        class="complete-dialog"
        :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <el-form label-width="80px">
          <el-form-item label="维护结果">
            <el-input
                type="textarea"
                v-model="completeResult"
                :rows="4"
                placeholder="请输入维护结果..."
                maxlength="500"
                show-word-limit
            />
          </el-form-item>
          <el-form-item label="维护费用">
            <el-input-number
                v-model="completeCost"
                :precision="2"
                :step="0.1"
                :min="0"
                placeholder="请输入费用..."
                style="width: 100%"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="completeDialogVisible = false">取消</el-button>
          <el-button type="success" @click="submitCompleteWithLocalTime">确认完成</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, EditPen, Check, Box, Clock, User } from '@element-plus/icons-vue';
import { maintenanceAPI, facilityAPI } from '../../api';
import { ElInputNumber } from 'element-plus';

const maintenanceList = ref([]);
const dialogVisible = ref(false);
const completeDialogVisible = ref(false);
const dialogTitle = ref('');
const loading = ref(false);
const submitLoading = ref(false);
const formRef = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const pagination = ref({
  page: 1,
  size: 10
});

const currentRow = ref({});
const completeResult = ref('');
const completeCost = ref(0);
const currentUser = ref({});
const statusFilter = ref('');
const maintenanceStatusFilter = ref(''); // 已维护/未维护/所有记录筛选
const facilityLoading = ref(false);
const filteredFacilityOptions = ref([]);
const form = ref({
  id: null,
  facilityId: null,
  maintainerId: null,
  maintenanceType: 'ROUTINE',
  description: '',
  maintainer: '',
  cost: null,
  startTime: null,
  endTime: null,
  result: '',
  status: 'PENDING'
});

const facilityOptions = ref([]);
const route = useRoute();
const router = useRouter();

const formatLocalDateTime = (date = new Date()) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};



// 计算属性：维护任务统计
const maintenanceStats = computed(() => {
  const allData = maintenanceList.value;
  const total = allData.length;
  const maintained = allData.filter(item => item.status === 'COMPLETED').length;
  const unmaintained = allData.filter(item => item.status === 'PENDING' || item.status === 'IN_PROGRESS').length;
  
  return {
    total,
    maintained,
    unmaintained,
    maintainedPercent: total > 0 ? Math.round((maintained / total) * 100) : 0,
    unmaintainedPercent: total > 0 ? Math.round((unmaintained / total) * 100) : 0
  };
});

// 表单验证规则
const rules = {
  facilityId: [
    { required: true, message: '请选择设施', trigger: 'change' }
  ],
  maintenanceType: [
    { required: true, message: '请选择维护类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入维护描述...', trigger: 'blur' },
    { max: 500, message: '描述长度不能超过500个字符', trigger: 'blur' }
  ],
  startTime: [
    { required: false, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: false, message: '请选择结束时间', trigger: 'change' }
  ]
};

const headerCellStyle = {
  backgroundColor: '#f8f9fa',
  fontWeight: 'bold',
  color: '#333'
};

const cellStyle = {
  padding: '12px 0'
};

const loadMaintenanceList = async () => {
  try {
    loading.value = true;
    
    // 确保分页参数正确
    console.log('Loading data - Page:', currentPage.value, 'Size:', pageSize.value);

    let allData = [];
    
    // 如果当前用户是维护人员，只加载自己的维护任务
    if (currentUser.value.role === 'MAINTAINER' && currentUser.value.id) {
      const res = await maintenanceAPI.getByMaintainerId(currentUser.value.id);
      allData = res.data || [];
    } else {
      // 获取所有维护任务
      const res = await maintenanceAPI.list();
      allData = res.data || [];
    }
    
    // 应用已维护/未维护筛选
    if (maintenanceStatusFilter.value) {
      if (maintenanceStatusFilter.value === 'MAINTAINED') {
        // 已维护：状态为已完成
        allData = allData.filter(item => item.status === 'COMPLETED');
      } else if (maintenanceStatusFilter.value === 'UNMAINTAINED') {
        // 未维护：状态为待处理或进行中
        allData = allData.filter(item => item.status === 'PENDING' || item.status === 'IN_PROGRESS');
      }
    }
    
    // 应用任务状态筛选
    if (statusFilter.value) {
      allData = allData.filter(item => item.status === statusFilter.value);
    }
    
    // 计算总数
    total.value = allData.length;
    
    // 计算当前页的数据
    const startIndex = (currentPage.value - 1) * pageSize.value;
    const endIndex = startIndex + pageSize.value;
    maintenanceList.value = allData.slice(startIndex, endIndex);
    
    console.log('Data loaded successfully - Total:', total.value, 'Current page data:', maintenanceList.value.length);

  } catch (error) {
    console.error('加载维护任务列表失败:', error);
    ElMessage.error('加载维护任务列表失败');
    maintenanceList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const loadFacilities = async () => {
  try {
    const res = await facilityAPI.list();
    facilityOptions.value = res.data || [];
    filteredFacilityOptions.value = res.data || [];
  } catch (error) {
    console.error('加载设施列表失败:', error);
    ElMessage.error('加载设施列表失败');
  }
};

const searchFacilities = async (keyword) => {
  if (!keyword || keyword.trim() === '') {
    filteredFacilityOptions.value = facilityOptions.value;
    return;
  }
  
  facilityLoading.value = true;
  try {
    const res = await facilityAPI.search(keyword);
    filteredFacilityOptions.value = res.data || [];
  } catch (error) {
    console.error('搜索设施失败:', error);
    // 如果搜索API失败，使用本地过滤
    filteredFacilityOptions.value = facilityOptions.value.filter(facility =>
      facility.name.toLowerCase().includes(keyword.toLowerCase())
    );
  } finally {
    facilityLoading.value = false;
  }
};

const handleCreate = () => {
  currentRow.value = {};
  form.value = {
    id: null,
    facilityId: null,
    maintainerId: currentUser.value.id || null,
    maintenanceType: 'ROUTINE',
    description: '',
    maintainer: currentUser.value.realName || currentUser.value.username || '',
    cost: null,
    startTime: null,
    endTime: null,
    result: '',
    status: 'PENDING' // 默认状态为待处理
  };
  dialogTitle.value = '新建维护任务';
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  currentRow.value = row;
  form.value = {
    id: row.id,
    facilityId: row.facilityId,
    maintainerId: row.maintainerId || currentUser.value.id || null,
    maintenanceType: row.maintenanceType,
    description: row.description,
    maintainer: row.maintainer || currentUser.value.realName || currentUser.value.username || '',
    cost: row.cost || null,
    startTime: row.startTime,
    endTime: row.endTime,
    result: row.result || '',
    status: row.status || 'PENDING'
  };
  dialogTitle.value = '编辑维护任务';
  dialogVisible.value = true;
};

const handleComplete = async (row) => {
  currentRow.value = row;
  completeResult.value = '';
  completeCost.value = row.cost || 0;
  completeDialogVisible.value = true;
};

const handleCompleteSubmit = async () => {
  return submitCompleteWithLocalTime();
};

const submitCompleteWithLocalTime = async () => {
  if (!completeResult.value.trim()) {
    ElMessage.error('请输入处理结果');
    return;
  }

  try {
    const currentEndTime = new Date();

    if (currentRow.value.startTime) {
      const startTime = new Date(currentRow.value.startTime);
      if (currentEndTime < startTime) {
        ElMessage.error('完成时间不能早于开始时间');
        return;
      }
    }

    const updateData = {
      ...currentRow.value,
      status: 'COMPLETED',
      endTime: formatLocalDateTime(currentEndTime),
      result: completeResult.value.trim(),
      cost: completeCost.value
    };

    await maintenanceAPI.complete(currentRow.value.id, updateData);
    ElMessage.success('维护任务已完成');
    completeDialogVisible.value = false;
    loadMaintenanceList();
  } catch (error) {
    console.error('完成维护任务失败:', error);
    ElMessage.error(error?.message || error?.response?.data?.message || '完成维护任务失败');
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    
    // 验证时间逻辑：只在编辑模式下验证（编辑模式下有结束时间字段）
    if (form.value.id && form.value.startTime && form.value.endTime) {
      const startTime = new Date(form.value.startTime);
      const endTime = new Date(form.value.endTime);
      if (endTime < startTime) {
        ElMessage.error('结束时间不能早于开始时间');
        return;
      }
    }
    
    // 确保维护人员信息完整
    if (!form.value.maintainerId) {
      form.value.maintainerId = currentUser.value.id;
    }
    if (!form.value.maintainer) {
      form.value.maintainer = currentUser.value.realName || currentUser.value.username;
    }
    
    submitLoading.value = true;

    if (form.value.id) {
      // 更新维护任务
      await maintenanceAPI.update(form.value.id, form.value);
      ElMessage.success('维护任务更新成功');
    } else {
      // 创建新的维护任务，默认状态为待处理
      const createData = {
        ...form.value,
        status: 'PENDING' // 新增时强制设置为待处理
      };
      await maintenanceAPI.create(createData);
      ElMessage.success('维护任务创建成功');
    }

    dialogVisible.value = false;
    loadMaintenanceList();
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败，请重试');
  } finally {
    submitLoading.value = false;
  }
};

const handleRowClick = (row) => {
  // 可以在这里处理行点击事件，比如查看详情
  console.log('Clicked row:', row);
};

const getMaintenanceTypeTag = (type) => {
  switch (type) {
    case 'ROUTINE': return 'primary';
    case 'REPAIR': return 'danger';
    case 'UPGRADE': return 'info';
    default: return 'primary';
  }
};

const getMaintenanceStatusType = (status) => {
  switch (status) {
    case 'PENDING': return 'warning';
    case 'IN_PROGRESS': return '';
    case 'COMPLETED': return 'success';
    case 'CANCELLED': return 'info';
    default: return 'info';
  }
};

const getMaintenanceStatusText = (status) => {
  switch (status) {
    case 'PENDING': return '待处理';
    case 'IN_PROGRESS': return '进行中';
    case 'COMPLETED': return '已完成';
    case 'CANCELLED': return '已取消';
    default: return status;
  }
};

const getMaintenanceTypeText = (type) => {
  switch (type) {
    case 'ROUTINE': return '常规维护';
    case 'REPAIR': return '故障维修';
    case 'UPGRADE': return '设备升级';
    default: return type;
  }
};

const getMaintenanceStatusFilterText = (filter) => {
  switch (filter) {
    case 'MAINTAINED': return '已维护';
    case 'UNMAINTAINED': return '未维护';
    default: return '所有记录';
  }
};

const formatMaintenanceTime = (row) => {
  if (!row.startTime && !row.endTime) {
    return '未安排';
  }
  if (row.startTime && !row.endTime) {
    return `开始: ${row.startTime}`;
  }
  if (!row.startTime && row.endTime) {
    return `结束: ${row.endTime}`;
  }
  return `${row.startTime} - ${row.endTime}`;
};

// 分页相关函数
const handleSizeChange = (size) => {
  pageSize.value = size;
  pagination.value.size = size;
  currentPage.value = 1;
  pagination.value.page = 1;
  loadMaintenanceList();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  pagination.value.page = page;
  loadMaintenanceList();
};

const handleStatusFilter = () => {
  // 状态筛选会在loadMaintenanceList中处理
  currentPage.value = 1;
  pagination.value.page = 1;
  loadMaintenanceList();
  
  // 更新URL查询参数
  updateQueryParams();
};

// 更新URL查询参数
const updateQueryParams = () => {
  const query = {};
  if (maintenanceStatusFilter.value) {
    query.maintenanceStatus = maintenanceStatusFilter.value;
  }
  if (statusFilter.value) {
    query.status = statusFilter.value;
  }
  
  router.replace({
    query: query
  });
};

// 处理路由查询参数
const handleRouteQuery = (query) => {
  if (query.maintenanceStatus !== undefined) {
    maintenanceStatusFilter.value = query.maintenanceStatus;
    currentPage.value = 1;
    pagination.value.page = 1;
    loadMaintenanceList();
  }
};

const handleMaintenanceStatusFilter = () => {
  // 已维护/未维护筛选
  currentPage.value = 1;
  pagination.value.page = 1;
  loadMaintenanceList();
  
  // 更新URL查询参数
  updateQueryParams();
};

// 点击统计项应用筛选
const applyFilterByClick = (filterType) => {
  if (filterType === 'maintained') {
    maintenanceStatusFilter.value = 'MAINTAINED';
  } else if (filterType === 'unmaintained') {
    maintenanceStatusFilter.value = 'UNMAINTAINED';
  } else {
    maintenanceStatusFilter.value = '';
  }
  currentPage.value = 1;
  pagination.value.page = 1;
  loadMaintenanceList();
};

const getCurrentUser = () => {
  try {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      currentUser.value = JSON.parse(userInfo);
      console.log('当前用户信息:', currentUser.value);
    }
  } catch (error) {
    console.error('获取当前用户信息失败:', error);
  }
};

onMounted(() => {
  getCurrentUser();
  loadMaintenanceList();
  loadFacilities();
  
  // 监听路由查询参数变化
  watch(() => route.query, (newQuery) => {
    handleRouteQuery(newQuery);
  }, { immediate: true });
});
</script>

<style scoped>
.maintainer-maintenance {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
  position: relative;
}

.header-decoration {
  position: absolute;
  top: -10px;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c, #f56c6c);
  border-radius: 2px;
}

.header-content {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.page-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 600;
  color: #1f2d3d;
  margin: 0 0 8px 0;
}

.title-icon {
  margin-right: 12px;
}

.title-icon svg {
  width: 32px;
  height: 32px;
  vertical-align: middle;
}

.page-subtitle {
  font-size: 14px;
  color: #8492a6;
  margin: 0;
}

.user-info {
  margin-top: 8px;
}

.operations-container {
  margin-bottom: 20px;
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-stats {
  margin-bottom: 16px;
  padding: 12px 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.el-space {
  display: inline-flex;
  vertical-align: middle;
}

.text-right {
  text-align: right;
}

.el-space {
  display: inline-flex;
  vertical-align: middle;
}

.pagination {
  display: inline-block;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.maintenance-table {
  width: 100%;
}

.facility-info {
  display: flex;
  align-items: center;
}

.facility-info .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.facility-name {
  font-weight: 500;
  color: #303133;
}

.description-info {
  color: #606266;
  line-height: 1.5;
}

.time-info {
  display: flex;
  align-items: center;
  color: #606266;
}

.time-info .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

.status-tag {
  border-radius: 4px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  margin: 0 2px;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
}

.edit-btn {
  color: #409eff;
  border-color: #b3d8ff;
}

.edit-btn:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.complete-btn {
  color: #67c23a;
  border-color: #c2e7b0;
}

.complete-btn:hover {
  background-color: #f0f9eb;
  border-color: #67c23a;
  color: #67c23a;
}

/* 对话框样式 */
.maintenance-dialog .el-dialog__body {
  padding: 20px;
}

.dialog-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.dialog-title-icon {
  margin-right: 12px;
}

.dialog-title-icon svg {
  width: 24px;
  height: 24px;
  vertical-align: middle;
}

.dialog-title-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.dialog-content {
  padding: 0 10px;
}

.maintenance-form {
  width: 100%;
}

.readonly-input {
  background-color: #f5f7fa;
  cursor: not-allowed;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 10px;
}

.cancel-btn {
  width: 80px;
}

.submit-btn {
  width: 100px;
}

/* 维护完成对话框样式 */
.complete-dialog .el-dialog__body {
  padding: 20px;
}

.complete-dialog .dialog-content {
  padding: 0 10px;
}

.complete-dialog .dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 10px;
}
/* 分页容器 */
.pagination-container {
  padding: 20px 0;
  display: flex;
  justify-content: center;
  background: white;
  border-top: 1px solid #ebeef5;
  margin-top: auto;
}

.custom-pagination {
  display: flex;
  justify-content: center;
}

/* 表格容器样式调整 */
.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 400px;
}

.maintenance-table {
  width: 100%;
  flex: 1;
}

.maintenance-stats {
  margin-bottom: 16px;
}

.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.stats-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-percent {
  font-size: 12px;
  opacity: 0.8;
}

.progress-container {
  flex: 1;
  min-width: 200px;
  margin-left: 20px;
}

.progress-container .el-progress {
  width: 100%;
}

</style>

