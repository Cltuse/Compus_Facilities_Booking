<template>
  <div class="equipment-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          设施浏览
        </h1>
        <p class="page-subtitle">浏览和预约设施</p>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-container">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索设施名称、型号或类别..."
        class="search-input"
        clearable
        @input="handleSearchInput"
      >
        <template #prefix>
          <el-icon class="search-icon"><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 设施卡片网格 -->
    <div class="equipment-grid">
      <div
        class="equipment-card"
        v-for="item in equipmentList"
        :key="item.id"
        @click="handleCardClick(item)"
      >
        <div class="card-header">
          <div class="equipment-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="equipment-status">
            <el-tag
              :type="getStatusType(item.status)"
              class="status-tag"
              effect="light"
              size="small"
            >
              <el-icon>
                <CircleCheck v-if="item.status === 'AVAILABLE'" />
                <Timer v-else-if="item.status === 'IN_USE'" />
                <Tools v-else-if="item.status === 'MAINTENANCE'" />
                <CircleClose v-else />
              </el-icon>
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
        </div>

        <div class="card-body">
          <h3 class="equipment-name">{{ item.name }}</h3>

          <div class="equipment-details">
            <div class="detail-item" v-if="item.model">
              <div class="detail-label">型号</div>
              <div class="detail-value">{{ item.model }}</div>
            </div>
            <div class="detail-item" v-if="item.category">
              <div class="detail-label">类别</div>
              <div class="detail-value">{{ item.category }}</div>
            </div>
            <div class="detail-item" v-if="item.location">
              <div class="detail-label">位置</div>
              <div class="detail-value">{{ item.location }}</div>
            </div>
          </div>

          <div class="equipment-description" v-if="item.description">
            {{ item.description }}
          </div>
        </div>

        <div class="card-actions">
          <el-button
            type="primary"
            class="reserve-btn"
            :disabled="item.status !== 'AVAILABLE'"
            @click.stop="handleReserve(item)"
          >
            <el-icon><Calendar /></el-icon>
            预约设施
          </el-button>
        </div>
      </div>
    </div>

    <!-- 预约对话框 -->
    <el-dialog
      v-model="dialogVisible"
      width="560px"
      class="reservation-dialog"
      :show-close="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
            <line x1="16" y1="2" x2="16" y2="6" stroke="currentColor" stroke-width="2"/>
            <line x1="8" y1="2" x2="8" y2="6" stroke="currentColor" stroke-width="2"/>
            <line x1="3" y1="10" x2="21" y2="10" stroke="currentColor" stroke-width="2"/>
          </svg>
        </div>
        <span class="dialog-title-text">预约设施</span>
      </div>

      <div class="dialog-body">
        <div class="equipment-info">
          <div class="info-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="info-details">
            <div class="info-name">{{ currentEquipment.name }}</div>
            <div class="info-model">{{ currentEquipment.model || '未知型号' }}</div>
          </div>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="reservation-form">
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              placeholder="选择开始日期时间"
              style="width: 100%;"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm"
              :disabled-date="disabledStartDate"
            />
          </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="form.endTime"
              type="datetime"
              placeholder="选择结束日期时间"
              style="width: 100%;"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD HH:mm"
              :disabled-date="disabledEndDate"
            />
          </el-form-item>
          <el-form-item label="使用目的" prop="purpose">
            <el-input
              type="textarea"
              v-model="form.purpose"
              :rows="4"
              placeholder="请简要描述使用目的和实验内容..."
              show-word-limit
              maxlength="500"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" size="large" @click="handleSubmit" class="submit-btn">
            <el-icon><Check /></el-icon>
            提交预约
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Calendar, Check, CircleCheck, Timer, Tools, CircleClose } from '@element-plus/icons-vue';
import { equipmentAPI, reservationAPI } from '../../api';

const equipmentList = ref([]);
const allEquipmentList = ref([]); // 存储所有设施数据
const searchKeyword = ref('');
const dialogVisible = ref(false);
const formRef = ref(null);
const currentEquipment = ref({});
const userInfo = ref({});

const disabledStartDate = (time) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return time.getTime() < today.getTime();
};

const disabledEndDate = (time) => {
  if (!form.value.startTime) {
    return false;
  }
  const startDate = new Date(form.value.startTime);
  startDate.setHours(0, 0, 0, 0);
  return time.getTime() < startDate.getTime();
};

// 时间验证函数
const validateStartTime = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请选择开始时间'));
    return;
  }

  const startDate = new Date(value);
  const today = new Date();
  today.setHours(0, 0, 0, 0); // 设置为今天的0点

  if (startDate < today) {
    callback(new Error('开始时间不能早于今天'));
    return;
  }

  callback();
};

const validateEndTime = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请选择结束时间'));
    return;
  }

  const endDate = new Date(value);
  const startDate = new Date(form.value.startTime);

  if (startDate && endDate <= startDate) {
    callback(new Error('结束时间必须在开始时间之后'));
    return;
  }

  callback();
};

const form = ref({
  equipmentId: null,
  userId: null,
  startTime: '',
  endTime: '',
  purpose: ''
});

const rules = {
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' },
    { validator: validateStartTime, trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    { validator: validateEndTime, trigger: 'change' }
  ],
  purpose: [{ required: true, message: '请输入使用目的', trigger: 'blur' }]
};

onMounted(() => {
  const info = localStorage.getItem('userInfo');
  if (info) {
    userInfo.value = JSON.parse(info);
  }
  loadEquipmentList();
});

const loadEquipmentList = async () => {
  try {
    const res = await equipmentAPI.list();
    allEquipmentList.value = res.data;
    equipmentList.value = res.data;
  } catch (error) {
    console.error('加载设施列表失败:', error);
  }
};

// 实时搜索筛选 - 直接更新equipmentList
const handleSearchInput = () => {
  if (!searchKeyword.value) {
    equipmentList.value = allEquipmentList.value;
    return;
  }

  const keyword = searchKeyword.value.toLowerCase().trim();
  equipmentList.value = allEquipmentList.value.filter(item => {
    return (
      (item.name && item.name.toLowerCase().includes(keyword)) ||
      (item.model && item.model.toLowerCase().includes(keyword)) ||
      (item.category && item.category.toLowerCase().includes(keyword)) ||
      (item.location && item.location.toLowerCase().includes(keyword)) ||
      (item.description && item.description.toLowerCase().includes(keyword))
    );
  });
};

const handleReserve = (item) => {
  currentEquipment.value = item;
  form.value = {
    equipmentId: item.id,
    userId: userInfo.value.id,
    startTime: '',
    endTime: '',
    purpose: ''
  };

  // 重置表单验证状态
  if (formRef.value) {
    formRef.value.clearValidate();
  }

  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    
    await reservationAPI.create(form.value);
    ElMessage.success('预约成功，请等待管理员审核');
    
    dialogVisible.value = false;
  } catch (error) {
    console.error('预约失败:', error);
  }
};

const getStatusType = (status) => {
  const map = {
    'AVAILABLE': 'success',
    'IN_USE': 'warning',
    'MAINTENANCE': 'info',
    'DAMAGED': 'danger'
  };
  return map[status] || '';
};

const getStatusText = (status) => {
  const map = {
    'AVAILABLE': '可用',
    'IN_USE': '使用中',
    'MAINTENANCE': '维护中',
    'DAMAGED': '损坏'
  };
  return map[status] || status;
};

// 卡片点击处理
const handleCardClick = (item) => {
  if (item.status === 'AVAILABLE') {
    handleReserve(item);
  }
};
</script>

<style scoped>
/* 页面背景 */
.equipment-page {
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

/* 搜索区域 */
.search-container {
  margin-bottom: 32px;
}

.search-input {
  max-width: 400px;
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  background: #ffffff;
  padding: 12px 16px;
  height: 48px;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.search-icon {
  color: #718096;
  font-size: 16px;
}

/* 设施网格 */
.equipment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

/* 设施卡片 */
.equipment-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.equipment-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 200% 100%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.equipment-card:hover::before {
  opacity: 1;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

.equipment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: #bae7ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.equipment-icon {
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

.equipment-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.equipment-status .status-tag {
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
  background: linear-gradient(135deg, rgba(237, 137, 54, 0.1) 0%, rgba(221, 107, 32, 0.1) 100%);
  color: #dd6b20;
}

.status-tag.el-tag--info {
  background: linear-gradient(135deg, rgba(160, 174, 192, 0.1) 0%, rgba(113, 128, 150, 0.1) 100%);
  color: #718096;
}

.status-tag.el-tag--danger {
  background: linear-gradient(135deg, rgba(245, 101, 101, 0.1) 0%, rgba(229, 62, 62, 0.1) 100%);
  color: #e53e3e;
}

.card-body {
  margin-bottom: 20px;
}

.equipment-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.equipment-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f7fafc;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

.detail-value {
  font-size: 14px;
  color: #2d3748;
  font-weight: 600;
}

.equipment-description {
  font-size: 14px;
  color: #4a5568;
  line-height: 1.6;
  margin-top: 16px;
  padding: 12px;
  background: #f7fafc;
  border-radius: 8px;
  border-left: 4px solid #bae7ff;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.reserve-btn {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  border-radius: 10px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.reserve-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.reserve-btn:hover::before {
  left: 100%;
}

.reserve-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #1976d2 100%);
}

.reserve-btn:active {
  transform: translateY(0);
}

.reserve-btn:disabled {
  background: #e2e8f0;
  color: #a0aec0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.reserve-btn:disabled:hover {
  transform: none;
  box-shadow: none;
  background: #e2e8f0;
}

/* 预约对话框 */
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

.dialog-body {
  padding: 32px;
}

.equipment-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: #f7fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.info-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.info-icon svg {
  width: 20px;
  height: 20px;
  color: #409eff;
}

.info-name {
  font-size: 16px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.info-model {
  font-size: 14px;
  color: #718096;
}

.reservation-form {
  margin-top: 24px;
}

.reservation-form :deep(.el-form-item__label) {
  color: #2d3748;
  font-weight: 600;
  font-size: 14px;
}

.reservation-form :deep(.el-input__wrapper),
.reservation-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  background: #f7fafc;
}

.reservation-form :deep(.el-input__wrapper:hover),
.reservation-form :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
  background: #ffffff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.reservation-form :deep(.el-input__wrapper.is-focus),
.reservation-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
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
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #1976d2 100%);
}

/* 动画效果 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .equipment-page {
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

  .equipment-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .equipment-card {
    padding: 20px;
  }

  .equipment-name {
    font-size: 16px;
  }

  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .card-actions {
    justify-content: center;
  }

  .reserve-btn {
    width: 100%;
    justify-content: center;
  }

  .dialog-body {
    padding: 20px;
  }

  .equipment-info {
    flex-direction: column;
    text-align: center;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }
}

@media (max-width: 1024px) and (min-width: 769px) {
  .equipment-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
