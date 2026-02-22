<template>
  <div class="reservation-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          预约管理
        </h1>
        <p class="page-subtitle">审核和管理设施预约申请</p>
      </div>
    </div>

    <!-- 状态标签页 -->
    <div class="tabs-container">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="status-tabs">
        <el-tab-pane label="待审核" name="PENDING" />
        <el-tab-pane label="已通过" name="APPROVED" />
        <el-tab-pane label="已拒绝" name="REJECTED" />
        <el-tab-pane label="全部" name="ALL" />
      </el-tabs>
    </div>

    <!-- 预约表格 -->
    <div class="table-container">
      <el-table
        :data="reservationList"
        class="reservation-table"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @row-click="handleRowClick"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="facilityName" label="设施名称" min-width="150">
          <template #default="{ row }">
            <div class="facility-info">
              <div class="facility-name">{{ row.facilityName }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="userName" label="申请人" min-width="120">
          <template #default="{ row }">
            <div class="user-info">{{ row.userName }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ row.startTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ row.endTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="purpose" label="使用目的" min-width="200">
          <template #default="{ row }">
            <div class="purpose-info">{{ row.purpose }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              class="status-tag"
              effect="light"
            >
              <el-icon>
                <CircleCheck v-if="row.status === 'APPROVED'" />
                <Clock v-else-if="row.status === 'PENDING'" />
                <CircleClose v-else-if="row.status === 'REJECTED'" />
                <Check v-else-if="row.status === 'COMPLETED'" />
                <CircleClose v-else />
              </el-icon>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 'PENDING'"
                size="small"
                type="success"
                :plain="true"
                class="action-btn approve-btn"
                @click.stop="handleApprove(row)"
              >
                <el-icon><CircleCheck /></el-icon>
                通过
              </el-button>
              <el-button
                v-if="row.status === 'PENDING'"
                size="small"
                type="danger"
                :plain="true"
                class="action-btn reject-btn"
                @click.stop="handleReject(row)"
              >
                <el-icon><CircleClose /></el-icon>
                拒绝
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      class="reservation-dialog"
      :close-on-click-modal="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">{{ dialogTitle }}</span>
      </div>

      <div class="dialog-content">
        <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          class="audit-form"
          label-width="100px"
        >
          <el-form-item label="设施名称">
            <el-input
              v-model="currentRow.facilityName"
              disabled
              class="readonly-input"
            />
          </el-form-item>

          <el-form-item label="申请人">
            <el-input
              v-model="currentRow.userName"
              disabled
              class="readonly-input"
            />
          </el-form-item>

          <el-form-item label="使用目的">
            <el-input
              v-model="currentRow.purpose"
              disabled
              type="textarea"
              :rows="2"
              class="readonly-input"
            />
          </el-form-item>

          <el-form-item label="管理员备注" prop="adminRemark">
            <el-input
              type="textarea"
              v-model="form.adminRemark"
              :rows="4"
              placeholder="请输入审核意见或备注信息..."
              maxlength="200"
              show-word-limit
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
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { reservationAPI, facilityAPI } from '../../api';
import { Clock, CircleCheck, CircleClose, Check } from '@element-plus/icons-vue';

const activeTab = ref('PENDING');
const reservationList = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('');
const actionType = ref('');
const loading = ref(false);
const submitLoading = ref(false);
const formRef = ref(null);

const currentRow = ref({});
const form = ref({
  adminRemark: ''
});

// 表单验证规则
const rules = {
  adminRemark: [
    { max: 200, message: '备注长度不能超过200个字符', trigger: 'blur' }
  ]
};

onMounted(() => {
  loadReservationList();
});

const loadReservationList = async () => {
  try {
    loading.value = true;
    let res;
    // 获取所有预约数据，然后在前端根据状态筛选
    res = await reservationAPI.list();

    if (activeTab.value === 'ALL') {
      reservationList.value = res.data;
    } else {
      // 根据当前选中的标签页状态筛选数据
      reservationList.value = res.data.filter(item => item.status === activeTab.value);
    }
  } catch (error) {
    console.error('加载预约列表失败:', error);
    ElMessage.error('加载预约列表失败');
  } finally {
    loading.value = false;
  }
};

const handleTabChange = () => {
  loadReservationList();
};

const handleApprove = (row) => {
  currentRow.value = row;
  actionType.value = 'approve';
  dialogTitle.value = '审核通过';
  form.value = { adminRemark: '' };
  dialogVisible.value = true;
};

const handleReject = (row) => {
  currentRow.value = row;
  actionType.value = 'reject';
  dialogTitle.value = '拒绝预约';
  form.value = { adminRemark: '' };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;

    if (actionType.value === 'approve') {
      // 通过预约
      await reservationAPI.approve(currentRow.value.id, form.value);

      // 同时更新设施状态为使用中
      await updateFacilityStatus(currentRow.value.facilityId, 'IN_USE');

      ElMessage.success('审核通过，设施状态已更新为使用中');
    } else {
      await reservationAPI.reject(currentRow.value.id, form.value);
      ElMessage.success('已拒绝');
    }

    dialogVisible.value = false;
    loadReservationList();
  } catch (error) {
    console.error('审核失败:', error);
    ElMessage.error('审核失败，请重试');
  } finally {
    submitLoading.value = false;
  }
};

// 更新设施状态
const updateFacilityStatus = async (facilityId, status) => {
  try {
    await facilityAPI.updateStatus(facilityId, status);
  } catch (error) {
    console.error('更新设施状态失败:', error);
    // 即使设施状态更新失败，也不影响预约审核的结果
    // 但可以给用户一个提示
    ElMessage.warning('预约已通过，但设施状态更新失败，请联系管理员');
  }
};

// 表格样式配置
const headerCellStyle = {
  backgroundColor: '#f8fafc',
  color: '#2d3748',
  fontWeight: '600',
  fontSize: '14px',
  borderBottom: '2px solid #e2e8f0'
};

const cellStyle = ({ row, column, rowIndex, columnIndex }) => {
  return {
    padding: '16px 12px',
    borderBottom: '1px solid #f0f0f0',
    color: '#4a5568',
    fontSize: '14px'
  };
};

// 表格行点击处理
const handleRowClick = (row) => {
  // 可以在这里添加行点击的逻辑，比如显示详情
};

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'COMPLETED': 'info',
    'CANCELLED': 'info'
  };
  return map[status] || '';
};

const getStatusText = (status) => {
  const map = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return map[status] || status;
};
</script>

<style scoped>
/* 页面背景 */
.reservation-page {
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

/* 标签页容器 */
.tabs-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  overflow: hidden;
}

.status-tabs {
  padding: 0 40px;
}

.status-tabs :deep(.el-tabs__header) {
  margin: 0;
}

.status-tabs :deep(.el-tabs__nav-wrap) {
  padding: 0;
}

.status-tabs :deep(.el-tabs__item) {
  font-weight: 600;
  color: #718096;
  padding: 0 20px;
  height: 56px;
  line-height: 56px;
  border-bottom: none;
  position: relative;
}

.status-tabs :deep(.el-tabs__item:hover) {
  color: #409eff;
}

.status-tabs :deep(.el-tabs__item.is-active) {
  color: #409eff;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05) 0%, rgba(64, 158, 255, 0.1) 100%);
  border-radius: 8px 8px 0 0;
}

.status-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  height: 3px;
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 表格样式 */
.reservation-table {
  width: 100%;
  border-radius: 0;
  overflow: hidden;
}

.reservation-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 100%);
}

.reservation-table :deep(.el-table__header) {
  border-radius: 0;
}

.reservation-table :deep(.el-table__header th) {
  background: transparent !important;
  border-bottom: 2px solid #e2e8f0;
  color: #2d3748;
  font-weight: 600;
  font-size: 14px;
  padding: 20px 12px;
}

.reservation-table :deep(.el-table__header th .cell) {
  font-weight: 600;
  color: #2d3748;
}

.reservation-table :deep(.el-table__body-wrapper) {
  background: #ffffff;
}

.reservation-table :deep(.el-table__body tr) {
  transition: all 0.3s ease;
  cursor: pointer;
}

.reservation-table :deep(.el-table__body tr:hover > td) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%) !important;
}

.reservation-table :deep(.el-table__body tr:hover > td .cell) {
  color: #1a202c !important;
}

.reservation-table :deep(.el-table__body td) {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 12px;
  transition: all 0.3s ease;
}

.reservation-table :deep(.el-table__body td .cell) {
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}

/* 表格单元格内容样式 */
.facility-info {
  min-width: 0;
}

.facility-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-info {
  font-size: 14px;
  font-weight: 500;
  color: #2d3748;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

.time-info .el-icon {
  font-size: 14px;
  color: #718096;
}

.purpose-info {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
  line-height: 1.4;
  word-break: break-word;
}

/* 状态标签 */
.status-tag {
  font-weight: 600;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  font-size: 12px;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.status-tag.el-tag--success {
  background: linear-gradient(135deg, rgba(72, 187, 120, 0.1) 0%, rgba(56, 161, 105, 0.1) 100%);
  color: #38a169;
}

.status-tag.el-tag--warning {
  background: linear-gradient(135deg, rgba(237, 137, 54, 0.1) 0%, rgba(214, 108, 32, 0.1) 100%);
  color: #d69e2e;
}

.status-tag.el-tag--danger {
  background: linear-gradient(135deg, rgba(245, 101, 101, 0.1) 0%, rgba(229, 62, 62, 0.1) 100%);
  color: #e53e3e;
}

.status-tag.el-tag--info {
  background: linear-gradient(135deg, rgba(160, 174, 192, 0.1) 0%, rgba(113, 128, 150, 0.1) 100%);
  color: #718096;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  border-radius: 8px;
  font-weight: 600;
  font-size: 12px;
  padding: 8px 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.approve-btn {
  color: #67c23a;
  border-color: #67c23a;
  background: rgba(103, 194, 58, 0.05);
}

.approve-btn:hover {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  color: #ffffff;
  border-color: #67c23a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.reject-btn {
  color: #f56565;
  border-color: #f56565;
  background: rgba(245, 101, 101, 0.05);
}

.reject-btn:hover {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
  color: #ffffff;
  border-color: #f56565;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.3);
}

.action-btn .el-icon {
  font-size: 14px;
}

/* 对话框样式 */
.reservation-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.reservation-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.reservation-dialog :deep(.el-dialog__header) {
  padding: 0;
  margin: 0;
  border-bottom: none;
}

.reservation-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.reservation-dialog :deep(.el-dialog__footer) {
  padding: 0;
  margin: 0;
  border-top: none;
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 32px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-bottom: 1px solid #e4e7ed;
}

.dialog-title-icon {
  width: 48px;
  height: 48px;
  background: rgba(64, 158, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.dialog-title-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.dialog-title-text {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
}

.dialog-content {
  padding: 32px;
}

.audit-form :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
}

.audit-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.audit-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.audit-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.audit-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.audit-form :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.audit-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.readonly-input :deep(.el-input__wrapper),
.readonly-input :deep(.el-textarea__inner) {
  background: #f7fafc;
  border-color: #e2e8f0;
  color: #4a5568;
  cursor: not-allowed;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px 32px;
  background: #f8fafc;
  border-top: 1px solid #e4e7ed;
}

.cancel-btn {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  color: #718096;
  background: #ffffff;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  border-color: #cbd5e0;
  background: #f7fafc;
  transform: translateY(-1px);
}

.submit-btn {
  border-radius: 8px;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
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

  .status-tabs {
    padding: 0 20px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 6px;
  }

  .action-btn {
    width: 100%;
  }

  .dialog-content {
    padding: 20px;
  }

  .dialog-header {
    padding: 20px;
  }

  .dialog-footer {
    padding: 20px;
  }
}
</style>
