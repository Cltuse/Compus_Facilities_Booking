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

    <!-- 统计卡片 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalReservations || 0 }}</div>
              <div class="stat-label">总预约数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.pendingReservations || 0 }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon approved">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.approvedReservations || 0 }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon completed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.completedReservations || 0 }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>



    <!-- 状态标签页 -->
    <div class="tabs-container">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="status-tabs">
        <el-tab-pane label="待审核" name="PENDING" />
        <el-tab-pane label="已通过" name="APPROVED" />
        <el-tab-pane label="已完成" name="COMPLETED" />
        <el-tab-pane label="已拒绝" name="REJECTED" />
        <el-tab-pane label="全部" name="ALL" />
      </el-tabs>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索设施名称或申请人..."
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
            <div class="faciltyment-info">
              <div class="faciltyment-name">{{ row.facilityName }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="userName" label="申请人" min-width="120" >
          <template #default="{ row }">
            <div class="user-info">{{ row.userName }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="startTime" label="开始时间" width="210" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ row.startTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="endTime" label="结束时间" width="210" align="center">
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

        <el-table-column prop="status" label="预约状态" width="160" align="center">
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

        <el-table-column label="操作" width="200" align="center" fixed="right">
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
                  v-if="row.status === 'APPROVED' && row.checkinStatus === 'CHECKED_IN' && row.verificationCode"
                  size="small"
                  type="warning"
                  :plain="true"
                  class="action-btn verify-btn"
                  @click.stop="handleVerify(row)"
              >
                <el-icon><Key /></el-icon>
                核销
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="custom-pagination"
        />
      </div>
    </div>

    <!-- 预约详情对话框 -->
    <el-dialog
        v-model="detailDialogVisible"
        title="预约详情"
        width="700px"
        class="detail-dialog"
        :close-on-click-modal="false"
    >
      <div class="detail-content" v-if="currentRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设施名称">{{ currentRow.facilityName }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentRow.userName }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentRow.startTime }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ currentRow.endTime }}</el-descriptions-item>
          <el-descriptions-item label="预约状态">
            <el-tag :type="getStatusType(currentRow.status)" size="small">
              {{ getStatusText(currentRow.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="签到状态">
            <el-tag :type="getCheckinStatusType(currentRow.checkinStatus)" size="small">
              {{ getCheckinStatusText(currentRow.checkinStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="核销码" v-if="currentRow.verificationCode">
            <el-tag type="info" size="small">{{ currentRow.verificationCode }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="核销管理员" v-if="currentRow.verifiedByName">
            {{ currentRow.verifiedByName }}
          </el-descriptions-item>
          <el-descriptions-item label="签到时间" v-if="currentRow.checkinTime">
            {{ currentRow.checkinTime }}
          </el-descriptions-item>
          <el-descriptions-item label="签退时间" v-if="currentRow.checkoutTime">
            {{ currentRow.checkoutTime }}
          </el-descriptions-item>
          <el-descriptions-item label="使用目的" :span="2">
            <div class="purpose-detail">{{ currentRow.purpose || '无' }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="管理员备注" :span="2" v-if="currentRow.adminRemark">
            <div class="admin-remark">{{ currentRow.adminRemark }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false" size="large">
            关闭
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 核销对话框 -->
    <el-dialog
        v-model="verifyDialogVisible"
        title="核销预约"
        width="400px"
        class="verify-dialog"
        :close-on-click-modal="false"
    >
      <div class="verify-content">
        <div class="verify-info">
          <p><strong>设施名称：</strong>{{ currentRow.facilityName }}</p>
          <p><strong>申请人：</strong>{{ currentRow.userName }}</p>
          <p><strong>预约时间：</strong>{{ currentRow.startTime }} - {{ currentRow.endTime }}</p>
          <p><strong>签到状态：</strong>{{ getCheckinStatusText(currentRow.checkinStatus) }}</p>
        </div>
        <el-form :model="verifyForm" ref="verifyFormRef" label-width="100px">
          <el-form-item label="核销码" prop="verificationCode" required>
            <el-input
              v-model="verifyForm.verificationCode"
              placeholder="请输入用户提供的核销码"
              maxlength="8"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="verifyDialogVisible = false" size="large">
            取消
          </el-button>
          <el-button
            type="primary"
            size="large"
            :loading="verifyLoading"
            @click="handleVerifySubmit"
          >
            确认核销
          </el-button>
        </div>
      </template>
    </el-dialog>

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
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { reservationAPI, facilityAPI, userAPI, adminAPI } from '../../api';
import { Clock, CircleCheck, CircleClose, Check, Search, Key, Document } from '@element-plus/icons-vue';
import * as echarts from 'echarts';

const activeTab = ref('PENDING');
const reservationList = ref([]);
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const verifyDialogVisible = ref(false);
const dialogTitle = ref('');
const actionType = ref('');
const loading = ref(false);
const submitLoading = ref(false);
const verifyLoading = ref(false);
const formRef = ref(null);
const verifyFormRef = ref(null);
const currentRow = ref({});
const form = ref({
  adminRemark: ''
});
const verifyForm = ref({
  verificationCode: ''
});

// 统计数据
const stats = ref({
  totalReservations: 0,
  pendingReservations: 0,
  approvedReservations: 0,
  completedReservations: 0,
  rejectedReservations: 0,
  cancelledReservations: 0,
  notCheckedReservations: 0,
  checkedInReservations: 0,
  checkedOutReservations: 0,
  missedReservations: 0,
  todayTotal: 0,
  todayPending: 0,
  todayApproved: 0
});

// 图表相关
const trendChart = ref(null);
let chartInstance = null;
const trends = ref([]);

// 搜索和分页
const searchKeyword = ref('');
const isSearchMode = ref(false);
const pagination = ref({
  page: 1,
  size: 10
});
const total = ref(0);

// 表单验证规则
const rules = {
  adminRemark: [
    { max: 200, message: '备注长度不能超过200个字符', trigger: 'blur' }
  ]
};

onMounted(() => {
  loadReservationList();
  loadStats();
});

const loadReservationList = async () => {
  try {
    loading.value = true;
    let res;
    // 获取所有预约数据，然后在前端根据状态筛选
    res = await reservationAPI.list();
    
    let reservations;
    if (activeTab.value === 'ALL') {
      reservations = res.data;
    } else {
      // 根据当前选中的标签页状态筛选数据
      reservations = res.data.filter(item => item.status === activeTab.value);
    }
    
    // 如果有搜索关键词，进行前端过滤
    if (isSearchMode.value && searchKeyword.value.trim()) {
      const keyword = searchKeyword.value.trim().toLowerCase();
      reservations = reservations.filter(item =>
        (item.facilityName && item.facilityName.toLowerCase().includes(keyword)) ||
        (item.userName && item.userName.toLowerCase().includes(keyword))
      );
    }
    
    // 前端分页处理
    total.value = reservations.length;
    const startIndex = (pagination.value.page - 1) * pagination.value.size;
    const endIndex = startIndex + pagination.value.size;
    reservationList.value = reservations.slice(startIndex, endIndex);
  } catch (error) {
    console.error('加载预约列表失败:', error);
    ElMessage.error('加载预约列表失败');
  } finally {
    loading.value = false;
  }
};

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await adminAPI.getReservationStats();
    // 根据API返回的数据结构判断是否成功
    if (res.code === 200 || res.success) {
      stats.value = res.data;
      console.log('统计数据加载成功:', res.data);
    } else {
      // 如果API调用成功但返回数据异常，设置默认值
      console.warn('统计数据API返回异常:', res);
      stats.value = {
        totalReservations: 0,
        pendingReservations: 0,
        approvedReservations: 0,
        completedReservations: 0,
        rejectedReservations: 0
      };
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
    // 设置默认值防止页面显示异常
    stats.value = {
      totalReservations: 0,
      pendingReservations: 0,
      approvedReservations: 0,
      completedReservations: 0,
      rejectedReservations: 0
    };
  }
};

// 加载趋势数据
const loadTrends = async () => {
  try {
    const res = await adminAPI.getReservationTrends();
    if (res.success) {
      trends.value = res.data;
      nextTick(() => {
        initChart();
      });
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error);
  }
};

// 初始化图表
const initChart = () => {
  if (!trendChart.value) return;
  
  if (chartInstance) {
    chartInstance.dispose();
  }
  
  chartInstance = echarts.init(trendChart.value);
  
  const dates = trends.value.map(item => item.date);
  const totalData = trends.value.map(item => item.total);
  const pendingData = trends.value.map(item => item.pending);
  const approvedData = trends.value.map(item => item.approved);
  const completedData = trends.value.map(item => item.completed);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['总数', '待审核', '已通过', '已完成']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '总数',
        type: 'line',
        data: totalData,
        smooth: true,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '待审核',
        type: 'line',
        data: pendingData,
        smooth: true,
        itemStyle: { color: '#e6a23c' }
      },
      {
        name: '已通过',
        type: 'line',
        data: approvedData,
        smooth: true,
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '已完成',
        type: 'line',
        data: completedData,
        smooth: true,
        itemStyle: { color: '#909399' }
      }
    ]
  };
  
  chartInstance.setOption(option);
};

// 窗口大小改变时重新调整图表
window.addEventListener('resize', () => {
  if (chartInstance) {
    chartInstance.resize();
  }
});

// 搜索处理
const handleSearch = () => {
  pagination.value.page = 1;
  if (searchKeyword.value.trim()) {
    isSearchMode.value = true;
  } else {
    isSearchMode.value = false;
  }
  loadReservationList();
};

// 清除搜索
const handleClearSearch = () => {
  searchKeyword.value = '';
  isSearchMode.value = false;
  pagination.value.page = 1;
  loadReservationList();
};

// 分页处理
const handleSizeChange = (size) => {
  pagination.value.size = size;
  pagination.value.page = 1;
  loadReservationList();
};

const handleCurrentChange = (page) => {
  pagination.value.page = page;
  loadReservationList();
};

const handleTabChange = () => {
  pagination.value.page = 1;
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

      ElMessage.success('审核通过');
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
const updateFaciltymentStatus = async (facilityId, status) => {
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
  // 显示预约详情
  currentRow.value = row;
  detailDialogVisible.value = true;
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
    'CHECKED_OUT': 'success',
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

// 签到处理
const handleCheckin = async (row) => {
  try {
    await reservationAPI.checkin(row.id);
    ElMessage.success('签到成功');
    loadReservationList();
  } catch (error) {
    console.error('签到失败:', error);
    ElMessage.error('签到失败: ' + (error.response?.data?.message || '请重试'));
  }
};

// 签退处理
const handleCheckout = async (row) => {
  try {
    await reservationAPI.checkout(row.id);
    ElMessage.success('签退成功');
    loadReservationList();
  } catch (error) {
    console.error('签退失败:', error);
    ElMessage.error('签退失败: ' + (error.response?.data?.message || '请重试'));
  }
};

// 核销处理 - 显示核销码输入对话框
const handleVerify = (row) => {
  currentRow.value = row;
  verifyForm.value.verificationCode = '';
  verifyDialogVisible.value = true;
};

// 提交核销
const handleVerifySubmit = async () => {
  if (!verifyForm.value.verificationCode.trim()) {
    ElMessage.warning('请输入核销码');
    return;
  }
  
  try {
    verifyLoading.value = true;
    await reservationAPI.verify(currentRow.value.id, verifyForm.value.verificationCode.trim());
    ElMessage.success('核销成功');
    verifyDialogVisible.value = false;
    loadReservationList();
  } catch (error) {
    console.error('核销失败:', error);
    ElMessage.error('核销失败: ' + (error.response?.data?.message || '请重试'));
  } finally {
    verifyLoading.value = false;
  }
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

/* 统计卡片容器 */
.stats-container {
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.total {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #1890ff;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
  color: #fa8c16;
}

.stat-icon.approved {
  background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
  color: #52c41a;
}

.stat-icon.completed {
  background: linear-gradient(135deg, #f0f5ff 0%, #d6e4ff 100%);
  color: #597ef7;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

/* 搜索工具栏 */
.toolbar {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.search-input :deep(.el-input__inner) {
  font-size: 14px;
}

.search-input :deep(.el-input-group__append) {
  background: #409eff;
  border-color: #409eff;
  color: #ffffff;
  border-radius: 0 8px 8px 0;
}

.search-input :deep(.el-input-group__append .el-button) {
  color: #ffffff;
  font-weight: 600;
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow-x: auto;
  overflow-y: hidden;
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
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

/* 核销对话框样式 */
.verify-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.verify-content {
  text-align: center;
}

.verify-info {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  text-align: left;
}

.verify-info p {
  margin: 8px 0;
  color: #2d3748;
  font-size: 14px;
}

.verify-info strong {
  color: #4a5568;
  margin-right: 8px;
}

/* 表格单元格内容样式 */
.faciltyment-info {
  min-width: 0;
}

.faciltyment-name {
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

/* 核销码样式 */
.verification-code {
  display: flex;
  justify-content: center;
}

.verification-code .el-tag {
  font-family: 'Courier New', monospace;
  font-weight: 600;
  letter-spacing: 1px;
}

.no-code {
  color: #a0aec0;
  font-size: 14px;
  text-align: center;
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

/* 详情对话框样式 */
.detail-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.detail-content {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
}

.purpose-detail {
  color: #4a5568;
  line-height: 1.6;
  word-break: break-word;
}

.admin-remark {
  color: #2d3748;
  background: #fff3cd;
  padding: 12px;
  border-radius: 8px;
  border-left: 4px solid #ffc107;
  font-style: italic;
}

/* 趋势图表样式 */
.chart-container {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: none;
}

.chart-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 100%);
  border-bottom: 1px solid #e2e8f0;
  padding: 20px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: 600;
  color: #2d3748;
}

.trend-chart {
  width: 100%;
  height: 300px;
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

.checkin-btn {
  color: #409eff;
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.05);
}

.checkin-btn:hover {
  background: linear-gradient(135deg, #409eff 0%, #3375cc 100%);
  color: #ffffff;
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.checkout-btn {
  color: #67c23a;
  border-color: #67c23a;
  background: rgba(103, 194, 58, 0.05);
}

.checkout-btn:hover {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  color: #ffffff;
  border-color: #67c23a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.verify-btn {
  color: #e6a23c;
  border-color: #e6a23c;
  background: rgba(230, 162, 60, 0.05);
}

.verify-btn:hover {
  background: linear-gradient(135deg, #e6a23c 0%, #d48a1b 100%);
  color: #ffffff;
  border-color: #e6a23c;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
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

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px 40px;
  background: #ffffff;
  border-top: 1px solid #f0f0f0;
}

.custom-pagination {
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

.custom-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  color: #ffffff;
  font-weight: 600;
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
