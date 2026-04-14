<template>
  <div class="my-reservation-page">
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
          我的预约
        </h1>
        <p class="page-subtitle">管理我的设施预约记录</p>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="search-container">
        <el-input
          v-model="searchFacilityName"
          placeholder="请输入设施名称进行搜索"
          class="search-input"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 预约表格 -->
    <div class="table-container">
      <el-table
        :data="paginatedReservationList"
        class="reservation-table"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @row-click="handleRowClick"
      >
        <el-table-column prop="facilityName" label="设施名称" min-width="180">
          <template #default="{ row }">
            <div class="facility-details">
              <div class="facility-name">{{ row.facilityName }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="startTime" label="开始时间" width="210">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ row.startTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="endTime" label="结束时间" width="210">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ row.endTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="purpose" label="使用目的" min-width="200" align="center">
          <template #default="{ row }">
            <div class="purpose-info">{{ row.purpose }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="150" align="center">
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

        <el-table-column prop="checkinStatus" label="签到状态" width="140" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getCheckinStatusType(row.checkinStatus)"
              class="status-tag"
              effect="light"
            >
              <el-icon>
                <CircleCheck v-if="row.checkinStatus === 'CHECKED_IN'" />
                <CircleCheck v-else-if="row.checkinStatus === 'CHECKED_OUT'" />
                <Clock v-else-if="row.checkinStatus === 'NOT_CHECKED'" />
                <CircleClose v-else-if="row.checkinStatus === 'MISSED'" />
              </el-icon>
              {{ getCheckinStatusText(row.checkinStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="verificationCode" label="核销码" width="120" align="center">
          <template #default="{ row }">
            <div v-if="row.verificationCode" class="verification-code">
              <el-tag type="info" effect="plain" size="small">
                {{ row.verificationCode }}
              </el-tag>
            </div>
            <div v-else class="no-code">-</div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                size="small"
                type="primary"
                :plain="true"
                class="action-btn view-btn"
                @click.stop="handleView(row)"
              >
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button
                v-if="(row.status === 'PENDING' || row.status === 'APPROVED') && row.checkinStatus === 'NOT_CHECKED'"
                size="small"
                type="warning"
                :plain="true"
                class="action-btn cancel-btn"
                @click.stop="handleCancel(row)"
              >
                <el-icon><Close /></el-icon>
                取消
              </el-button>
              <el-button
                v-if="row.status === 'APPROVED' && row.checkinStatus === 'NOT_CHECKED'"
                size="small"
                type="primary"
                :plain="true"
                class="action-btn checkin-btn"
                @click.stop="handleCheckin(row)"
              >
                <el-icon><Check /></el-icon>
                签到
              </el-button>
              <el-button
                v-if="row.status === 'APPROVED' && row.checkinStatus === 'CHECKED_IN'"
                size="small"
                type="success"
                :plain="true"
                class="action-btn checkout-btn"
                @click.stop="handleCheckout(row)"
              >
                <el-icon><CircleCheck /></el-icon>
                签退
              </el-button>
              <el-button
                v-if="row.status === 'APPROVED' && row.checkinStatus === 'CHECKED_OUT'"
                size="small"
                type="info"
                :plain="true"
                class="action-btn completed-btn"
                disabled
              >
                <el-icon><CircleCheck /></el-icon>
                已签退
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="filteredReservationList.length"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 查看详情对话框 -->
    <el-dialog
      title="预约详情"
      v-model="viewDialogVisible"
      width="600px"
      class="reservation-dialog"
      :show-close="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">预约详情</span>
      </div>

      <div class="dialog-content">
        <el-descriptions :column="1" border class="reservation-descriptions">
          <el-descriptions-item label="设施名称">
            <div class="description-value">{{ currentRow.facilityName }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">
            <div class="description-value time-value">
              <el-icon><Clock /></el-icon>
              {{ currentRow.startTime }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="结束时间">
            <div class="description-value time-value">
              <el-icon><Clock /></el-icon>
              {{ currentRow.endTime }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="使用目的">
            <div class="description-value">{{ currentRow.purpose }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRow.status)" class="status-tag">
              <el-icon>
                <CircleCheck v-if="currentRow.status === 'APPROVED'" />
                <Clock v-else-if="currentRow.status === 'PENDING'" />
                <CircleClose v-else-if="currentRow.status === 'REJECTED'" />
                <Check v-else-if="currentRow.status === 'COMPLETED'" />
                <CircleClose v-else />
              </el-icon>
              {{ getStatusText(currentRow.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="签到状态">
            <el-tag :type="getCheckinStatusType(currentRow.checkinStatus)" class="status-tag">
              <el-icon>
                <CircleCheck v-if="currentRow.checkinStatus === 'CHECKED_IN'" />
                <CircleCheck v-else-if="currentRow.checkinStatus === 'CHECKED_OUT'" />
                <Clock v-else-if="currentRow.checkinStatus === 'NOT_CHECKED'" />
                <CircleClose v-else-if="currentRow.checkinStatus === 'MISSED'" />
              </el-icon>
              {{ getCheckinStatusText(currentRow.checkinStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="核销码" v-if="currentRow.verificationCode">
            <el-tag type="info" effect="plain" size="small" class="verification-code-detail">
              {{ currentRow.verificationCode }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="管理员备注">
            <div class="description-value">{{ currentRow.adminRemark || '无' }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="viewDialogVisible = false" class="cancel-btn">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Clock, CircleCheck, CircleClose, Check, View, Close } from '@element-plus/icons-vue';
import { reservationAPI } from '../../api';

const reservationList = ref([]);
const viewDialogVisible = ref(false);
const currentRow = ref({});
const userInfo = ref({});
const searchFacilityName = ref('');

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);

onMounted(() => {
  const info = localStorage.getItem('userInfo');
  if (info) {
    userInfo.value = JSON.parse(info);
  }
  loadMyReservations();
});

const loadMyReservations = async () => {
  try {
    const res = await reservationAPI.getByUserId(userInfo.value.id);
    reservationList.value = res.data;
  } catch (error) {
    console.error('加载我的预约失败:', error);
  }
};

// 过滤后的预约列表（设施名称模糊搜索）
const filteredReservationList = computed(() => {
  let filtered = reservationList.value;

  if (searchFacilityName.value) {
    const searchTerm = searchFacilityName.value.toLowerCase();
    filtered = filtered.filter(item =>
      item.facilityName && item.facilityName.toLowerCase().includes(searchTerm)
    );
  }

  // 按开始时间倒序排序（最新的记录显示在最前面）
  return filtered.sort((a, b) => {
    const timeA = new Date(a.startTime).getTime();
    const timeB = new Date(b.startTime).getTime();
    return timeB - timeA;
  });
});

// 分页后的预约列表
const paginatedReservationList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredReservationList.value.slice(start, end);
});

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleView = (row) => {
  currentRow.value = row;
  viewDialogVisible.value = true;
};

const handleCancel = (row) => {
  ElMessageBox.confirm('确认取消该预约？', '取消确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'delete-confirm-dialog'
  }).then(async () => {
    try {
      await reservationAPI.cancel(row.id);
      ElMessage.success('已取消预约');
      loadMyReservations();
    } catch (error) {
      console.error('取消失败:', error);
    }
  }).catch(() => {});
};

const handleComplete = (row) => {
  ElMessageBox.confirm('确认完成该预约？完成后设施状态将更新为可用。', '完成确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success',
    customClass: 'complete-confirm-dialog'
  }).then(async () => {
    try {
      await reservationAPI.complete(row.id);
      ElMessage.success('预约已完成');
      loadMyReservations();
    } catch (error) {
      console.error('完成失败:', error);
    }
  }).catch(() => {});
};

const handleCheckin = (row) => {
  ElMessageBox.confirm('确认签到该预约？', '签到确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
    customClass: 'checkin-confirm-dialog'
  }).then(async () => {
    try {
      await reservationAPI.checkin(row.id);
      ElMessage.success('签到成功');
      loadMyReservations();
    } catch (error) {
      console.error('签到失败:', error);
      ElMessage.error(error.response?.data?.message || '签到失败');
    }
  }).catch(() => {});
};

const handleCheckout = (row) => {
  ElMessageBox.confirm('确认签退该预约？签退后预约将自动完成。', '签退确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success',
    customClass: 'checkout-confirm-dialog'
  }).then(async () => {
    try {
      await reservationAPI.checkout(row.id);
      ElMessage.success('签退成功，预约已完成');
      loadMyReservations();
    } catch (error) {
      console.error('签退失败:', error);
      ElMessage.error(error.response?.data?.message || '签退失败');
    }
  }).catch(() => {});
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
  handleView(row);
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

const getCheckinStatusType = (checkinStatus) => {
  const map = {
    'NOT_CHECKED': 'info',
    'CHECKED_IN': 'success',
    'CHECKED_OUT': 'info',
    'MISSED': 'danger'
  };
  return map[checkinStatus] || '';
};

const getCheckinStatusText = (checkinStatus) => {
  const map = {
    'NOT_CHECKED': '未签到',
    'CHECKED_IN': '已签到',
    'CHECKED_OUT': '已签退',
    'MISSED': '爽约'
  };
  return map[checkinStatus] || checkinStatus;
};
</script>

<style scoped>
/* 页面背景 */
.my-reservation-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  padding: 24px;
  position: relative;
}

/* 页面标题区域 */
.page-header {
  position: relative;
  margin-bottom: 32px;
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 200% 100%;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

.header-content {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.header-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
  line-height: 1.3;
}

.title-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transition: all 0.3s ease;
}

.title-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.25);
}

.title-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.page-subtitle {
  margin: 0;
  color: #718096;
  font-size: 14px;
  font-weight: 400;
  opacity: 0.8;
}

/* 工具栏 */
.toolbar {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-container {
  display: flex;
  align-items: center;
}

.search-input {
  width: 300px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  background: #f7fafc;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow-x: auto;
  overflow-y: hidden;
  transition: all 0.3s ease;
}

.table-container::-webkit-scrollbar {
  height: 8px;
}

.table-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.table-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.table-container:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 表格样式 */
.reservation-table {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
}

.reservation-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 100%);
}

.reservation-table :deep(.el-table__header) {
  border-radius: 16px 16px 0 0;
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
.facility-details {
  min-width: 0;
}

.facility-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

/* 标签样式 */
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

.view-btn {
  color: #409eff;
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.05);
}

.view-btn:hover {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  color: #ffffff;
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.cancel-btn {
  color: #f56565;
  border-color: #f56565;
  background: rgba(245, 101, 101, 0.05);
}

.cancel-btn:hover {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
  color: #ffffff;
  border-color: #f56565;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.3);
}

.complete-btn {
  color: #67c23a;
  border-color: #67c23a;
  background: rgba(103, 194, 58, 0.05);
}

.complete-btn:hover {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  color: #ffffff;
  border-color: #67c23a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.action-btn .el-icon {
  font-size: 14px;
}

/* 分页容器 */
.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  padding: 16px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pagination-container :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-container :deep(.el-pagination .el-pager li) {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.3s ease;
}

.pagination-container :deep(.el-pagination .el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  color: #ffffff;
}

/* 对话框样式 */
.reservation-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.reservation-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
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

.reservation-descriptions {
  border-radius: 12px;
  overflow: hidden;
}

.reservation-descriptions :deep(.el-descriptions__header) {
  background: #f8fafc;
}

.reservation-descriptions :deep(.el-descriptions__header th) {
  background: #f8fafc !important;
  color: #2d3748;
  font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
}

.reservation-descriptions :deep(.el-descriptions__body td) {
  border-bottom: 1px solid #f0f0f0;
}

.description-value {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

.description-value.time-value {
  display: flex;
  align-items: center;
  gap: 6px;
}

.description-value.time-value .el-icon {
  color: #718096;
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

/* 动画效果 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .my-reservation-page {
    padding: 16px;
  }

  .header-content {
    padding: 20px;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .page-title {
    font-size: 24px;
  }

  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .table-container {
    overflow-x: auto;
    border-radius: 12px;
  }

  .reservation-table {
    min-width: 800px;
  }

  .reservation-table :deep(.el-table__header th),
  .reservation-table :deep(.el-table__body td) {
    padding: 12px 8px;
  }

  .facility-name {
    font-size: 13px;
  }

  .time-info {
    font-size: 13px;
  }

  .purpose-info {
    font-size: 13px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .action-btn {
    font-size: 11px;
    padding: 6px 8px;
    width: 100%;
  }

  .pagination-container {
    padding: 12px;
  }
}
</style>