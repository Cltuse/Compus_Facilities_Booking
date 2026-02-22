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
          维护任务管理
        </h1>
        <p class="page-subtitle">管理设施维护任务</p>
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operations-container">
      <el-row :gutter="20" align="middle">
        <el-col :span="16">
          <el-button type="primary" @click="handleCreate" :icon="Plus">新建维护任务</el-button>
        </el-col>
        <el-col :span="8" class="text-right">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="pagination"
          />
        </el-col>
      </el-row>
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

        <el-table-column prop="maintenanceType" label="维护类型" width="120">
          <template #default="{ row }">
            <el-tag
                :type="getMaintenanceTypeTag(row.maintenanceType)"
                size="small"
            >
              {{ row.maintenanceType }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="维护描述" min-width="200">
          <template #default="{ row }">
            <div class="description-info">{{ row.description }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="scheduledTime" label="计划时间" width="160">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ row.scheduledTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
                :type="getMaintenanceStatusType(row.status)"
                class="status-tag"
                effect="light"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
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
          <el-form-item label="设施名称" prop="facilityId">
            <el-select
                v-model="form.facilityId"
                placeholder="请选择设施"
                style="width: 100%"
                :disabled="!!currentRow.id"
            >
              <el-option
                  v-for="facility in facilityOptions"
                  :key="facility.id"
                  :label="facility.name"
                  :value="facility.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="维护类型" prop="maintenanceType">
            <el-radio-group v-model="form.maintenanceType">
              <el-radio label="PREVENTIVE">预防性维护</el-radio>
              <el-radio label="CORRECTIVE">纠正性维护</el-radio>
              <el-radio label="UPGRADE">升级维护</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="维护描述" prop="description">
            <el-input
                type="textarea"
                v-model="form.description"
                :rows="4"
                placeholder="请输入维护任务的详细描述..."
                maxlength="500"
                show-word-limit
            />
          </el-form-item>

          <el-form-item label="计划时间" prop="scheduledTime">
            <el-date-picker
                v-model="form.scheduledTime"
                type="datetime"
                placeholder="选择计划维护时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
            />
          </el-form-item>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, EditPen, Check, Box, Clock } from '@element-plus/icons-vue';
import { maintenanceAPI, facilityAPI } from '../../api';

const maintenanceList = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const loading = ref(false);
const submitLoading = ref(false);
const formRef = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const currentRow = ref({});
const form = ref({
  id: null,
  facilityId: null,
  maintenanceType: 'PREVENTIVE',
  description: '',
  scheduledTime: null
});

const facilityOptions = ref([]);

// 表单验证规则
const rules = {
  facilityId: [
    { required: true, message: '请选择设施', trigger: 'change' }
  ],
  maintenanceType: [
    { required: true, message: '请选择维护类型', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入维护描述', trigger: 'blur' },
    { max: 500, message: '描述长度不能超过500个字符', trigger: 'blur' }
  ],
  scheduledTime: [
    { required: true, message: '请选择计划时间', trigger: 'change' }
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

    // 获取所有维护任务
    const res = await maintenanceAPI.list();
    const allData = res.data || [];

    // 计算总数
    total.value = allData.length;

    // 计算当前页的数据
    const startIndex = (currentPage.value - 1) * pageSize.value;
    const endIndex = startIndex + pageSize.value;
    maintenanceList.value = allData.slice(startIndex, endIndex);

  } catch (error) {
    console.error('加载维护任务列表失败:', error);
    ElMessage.error('加载维护任务列表失败');
  } finally {
    loading.value = false;
  }
};

const loadFacilities = async () => {
  try {
    const res = await facilityAPI.list();
    facilityOptions.value = res.data || [];
  } catch (error) {
    console.error('加载设施列表失败:', error);
    ElMessage.error('加载设施列表失败');
  }
};

const handleCreate = () => {
  currentRow.value = {};
  form.value = {
    id: null,
    facilityId: null,
    maintenanceType: 'PREVENTIVE',
    description: '',
    scheduledTime: null
  };
  dialogTitle.value = '新建维护任务';
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  currentRow.value = row;
  form.value = {
    id: row.id,
    facilityId: row.facilityId,
    maintenanceType: row.maintenanceType,
    description: row.description,
    scheduledTime: row.scheduledTime
  };
  dialogTitle.value = '编辑维护任务';
  dialogVisible.value = true;
};

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要将"${row.facilityName}"的维护任务标记为已完成吗？`,
        '确认操作',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    );

    // 调用API完成维护任务
    await maintenanceAPI.update(row.id, { ...row, status: 'COMPLETED' });
    ElMessage.success('维护任务已完成');
    loadMaintenanceList(); // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成维护任务失败:', error);
      ElMessage.error('完成维护任务失败');
    }
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;

    if (form.value.id) {
      // 更新维护任务
      await maintenanceAPI.update(form.value.id, form.value);
      ElMessage.success('维护任务更新成功');
    } else {
      // 创建新的维护任务
      await maintenanceAPI.create(form.value);
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
    case 'PREVENTIVE': return 'warning';
    case 'CORRECTIVE': return 'danger';
    case 'UPGRADE': return 'info';
    default: return 'primary';
  }
};

const getMaintenanceStatusType = (status) => {
  switch (status) {
    case 'PENDING': return 'warning';
    case 'IN_PROGRESS': return 'primary';
    case 'COMPLETED': return 'success';
    case 'CANCELLED': return 'info';
    default: return 'info';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 'PENDING': return '待处理';
    case 'IN_PROGRESS': return '进行中';
    case 'COMPLETED': return '已完成';
    case 'CANCELLED': return '已取消';
    default: return status;
  }
};

// 分页相关函数
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadMaintenanceList();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  loadMaintenanceList();
};

onMounted(() => {
  loadMaintenanceList();
  loadFacilities();
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

.operations-container {
  margin-bottom: 20px;
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.text-right {
  text-align: right;
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
</style>