<template>
  <div class="facility-detail-page">
    <!-- 返回按钮 -->
    <div class="back-section">
      <el-button class="back-btn" @click="goBack" :icon="ArrowLeft">
        返回设施列表
      </el-button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner">
        <el-icon class="is-loading">
          <Loading />
        </el-icon>
      </div>
      <p class="loading-text">正在加载设施详情...</p>
    </div>

    <!-- 设施详情内容 -->
    <div v-else-if="facility" class="detail-content">
      <!-- 基本信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <div class="header-title">
            <h1 class="facility-name">{{ facility.name }}</h1>
            <el-tag
                :type="getStatusType(facility.status)"
                class="status-tag"
                effect="light"
                size="large"
            >
              <el-icon>
                <CircleCheck v-if="facility.status === 'AVAILABLE'" />
                <Tools v-else-if="facility.status === 'MAINTENANCE'" />
                <CircleClose v-else />
              </el-icon>
              {{ getStatusText(facility.status) }}
            </el-tag>
          </div>
        </div>

        <div class="card-body">
          <!-- 设施图片 -->
          <div class="facility-image-section" v-if="facility.imageUrl">
            <img :src="facility.imageUrl" :alt="facility.name" class="main-image" />
          </div>

          <!-- 设施基本信息 -->
          <div class="info-grid">
            <div class="info-item" v-if="facility.model">
              <div class="info-label">设施型号</div>
              <div class="info-value">{{ facility.model }}</div>
            </div>
            <div class="info-item" v-if="facility.category">
              <div class="info-label">设施类别</div>
              <div class="info-value">{{ facility.category }}</div>
            </div>
            <div class="info-item" v-if="facility.location">
              <div class="info-label">存放位置</div>
              <div class="info-value">{{ facility.location }}</div>
            </div>
            <div class="info-item" v-if="facility.purchaseDate">
              <div class="info-label">购买日期</div>
              <div class="info-value">{{ facility.purchaseDate }}</div>
            </div>
            <div class="info-item" v-if="facility.price">
              <div class="info-label">设施价格</div>
              <div class="info-value">¥{{ facility.price }}</div>
            </div>
          </div>

          <!-- 详细描述 -->
          <div class="description-section" v-if="facility.description">
            <div class="section-title">详细描述</div>
            <div class="description-content">{{ facility.description }}</div>
          </div>


        </div>
      </div>

      <!-- 预约时间轴卡片 -->
      <div class="timeline-card">
        <div class="card-header">
          <div class="header-title">
            <h2 class="section-title">未来 {{ queryDays }} 天占用情况</h2>
            <p class="section-subtitle">灰色背景表示已被预约，方便您选择空闲时段</p>
          </div>
        </div>

        <div class="card-body">
          <!-- 时间轴 -->
          <div class="timeline-container">
            <div
                v-for="day in timelineDays"
                :key="day.date"
                class="timeline-day"
            >
              <div class="day-header">
                <div class="day-date">{{ day.dateStr }}</div>
                <div class="day-week">{{ day.weekDay }}</div>
              </div>

              <div class="day-slots">
                <!-- 遍历每天的时段 -->
                <div
                    v-for="slot in day.slots"
                    :key="slot.time"
                    class="time-slot"
                    :class="{
                    'slotoccupied': slot.occupied,
                    'slot-available': !slot.occupied
                  }"
                >
                  <div class="slot-time">{{ slot.time }}</div>
                  <div v-if="slot.occupied" class="slot-info">
                    <div class="slot-label">已预约</div>
                    <el-tooltip
                        v-if="slot.reservation"
                        :content="`${slot.reservation.startTime} - ${slot.reservation.endTime}\n预约人：${slot.reservation.userName || '未知'}\n用途：${slot.reservation.purpose || '未填写'}`"
                        placement="top"
                    >
                      <div class="slot-tooltip-trigger">
                        <el-icon><InfoFilled /></el-icon>
                      </div>
                    </el-tooltip>
                  </div>
                  <div v-else class="slot-info">
                    <div class="slot-label">空闲</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 图例说明 -->
          <div class="timeline-legend">
            <div class="legend-item">
              <div class="legend-color legend-available"></div>
              <span class="legend-text">空闲可预约</span>
            </div>
            <div class="legend-item">
              <div class="legend-color legend-occupied"></div>
              <span class="legend-text">已预约</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 预约按钮 -->
      <div class="action-section">
        <el-button
            type="primary"
            size="large"
            class="reserve-btn"
            @click="handleReserve"
        >
          <el-icon><Calendar /></el-icon>
          立即预约此设施
        </el-button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-icon class="empty-icon"><Warning /></el-icon>
      <h3 class="empty-title">未找到设施</h3>
      <p class="empty-description">您访问的设施不存在或已被删除</p>
      <el-button type="primary" @click="goBack">返回设施列表</el-button>
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
        <div class="facility-info">
          <div class="info-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="info-details">
            <div class="info-name">{{ facility?.name }}</div>
            <div class="info-model">{{ facility?.model || '未知型号' }}</div>
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
                @change="checkAvailability"
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
                @change="checkAvailability"
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

            <el-form-item v-if="form.startTime && form.endTime">
              <div class="availability-check">
                <el-icon v-if="checkingAvailability" class="is-loading"><Loading /></el-icon>
                <el-icon v-else-if="isTimeAvailable" class="available-icon"><CircleCheck /></el-icon>
                <el-icon v-else class="unavailable-icon"><CircleClose /></el-icon>
                <span :class="['availability-text', isTimeAvailable ? 'available' : 'unavailable']">
                  {{ availabilityMessage }}
                </span>
              </div>
            </el-form-item>
          </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button
            type="primary"
            size="large"
            :loading="submitLoading"
            :disabled="!isTimeAvailable || checkingAvailability"
            @click="handleSubmit"
            class="submit-btn"
          >
            <el-icon v-if="!submitLoading"><Check /></el-icon>
            提交预约
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  ArrowLeft,
  Loading,
  CircleCheck,
  Timer,
  Tools,
  CircleClose,
  Calendar,
  Check,
  InfoFilled,
  Warning,
  Checked
} from '@element-plus/icons-vue';
import { facilityAPI, reservationAPI } from '../../api';

const route = useRoute();
const router = useRouter();

const facility = ref(null);
const reservations = ref([]);
const queryDays = ref(7);
const loading = ref(true);
const dialogVisible = ref(false);
const formRef = ref(null);
const userInfo = ref({});
const checkingAvailability = ref(false);
const isTimeAvailable = ref(true);
const availabilityMessage = ref('');

const form = ref({
  facilityId: null,
  userId: null,
  startTime: '',
  endTime: '',
  purpose: ''
});

const checkAvailability = async () => {
  if (!form.value.startTime || !form.value.endTime) {
    isTimeAvailable.value = true;
    availabilityMessage.value = '';
    return;
  }

  try {
    checkingAvailability.value = true;
    const response = await reservationAPI.checkAvailability(
      form.value.facilityId,
      form.value.startTime,
      form.value.endTime
    );

    if (response.data) {
      isTimeAvailable.value = response.data.available;
      availabilityMessage.value = response.data.message || (response.data.available ? '该时间段可用' : '该时段已被预约');
    }
  } catch (error) {
    console.error('检查可用性失败:', error);
    isTimeAvailable.value = true;
    availabilityMessage.value = '';
  } finally {
    checkingAvailability.value = false;
  }
};

function validateStartTime(rule, value, callback) {
  if (!value) {
    callback(new Error('请选择开始时间'));
    return;
  }
  const startDate = new Date(value);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  if (startDate < today) {
    callback(new Error('开始时间不能早于今天'));
    return;
  }
  callback();
}

function validateEndTime(rule, value, callback) {
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
}

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

onMounted(() => {
  const info = localStorage.getItem('userInfo');
  if (info) {
    userInfo.value = JSON.parse(info);
  }
  loadFacilityDetail();
});

const loadFacilityDetail = async () => {
  try {
    loading.value = true;
    const facilityId = route.params.id;
    const res = await facilityAPI.getDetail(facilityId, queryDays.value);
    if (res.code === 200) {
      facility.value = res.data.facility;
      reservations.value = res.data.reservations || [];
    } else {
      ElMessage.error(res.message || '加载设施详情失败');
    }
  } catch (error) {
    console.error('加载设施详情失败:', error);
    ElMessage.error('加载设施详情失败');
  } finally {
    loading.value = false;
  }
};

const timelineDays = computed(() => {
  const days = [];
  const now = new Date();
  const hours = [8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22];

  for (let i = 0; i < queryDays.value; i++) {
    const date = new Date(now);
    date.setDate(date.getDate() + i);

    const dateStr = `${date.getMonth() + 1}-${date.getDate()}`;
    const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const weekDay = weekDays[date.getDay()];

    const slots = hours.map(hour => {
      const slotStart = new Date(date);
      slotStart.setHours(hour, 0, 0, 0);
      const slotEnd = new Date(date);
      slotEnd.setHours(hour + 1, 0, 0, 0);

      const reservation = reservations.value.find(res => {
        const resStart = new Date(res.startTime);
        const resEnd = new Date(res.endTime);
        return resStart < slotEnd && resEnd > slotStart;
      });

      return {
        time: `${hour}:00`,
        occupied: !!reservation,
        reservation: reservation
      };
    });

    days.push({
      date: dateStr,
      dateStr,
      weekDay,
      slots
    });
  }

  return days;
});

const getStatusType = (status) => {
  const map = {
    'AVAILABLE': 'success',
    'MAINTENANCE': 'info',
    'DAMAGED': 'danger'
  };
  return map[status] || '';
};

const getStatusText = (status) => {
  const map = {
    'AVAILABLE': '可用',
    'MAINTENANCE': '维护中',
    'DAMAGED': '损坏'
  };
  return map[status] || status;
};

const goBack = () => {
  router.push('/user/facility');
};

const handleReserve = () => {
  form.value = {
    facilityId: facility.value.id,
    userId: userInfo.value.id,
    startTime: '',
    endTime: '',
    purpose: ''
  };
  isTimeAvailable.value = true;
  availabilityMessage.value = '';

  if (formRef.value) {
    formRef.value.clearValidate();
  }

  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();

    const response = await reservationAPI.create(form.value);
    ElMessage.success(response.message || '预约成功');

    dialogVisible.value = false;
    loadFacilityDetail();
  } catch (error) {
    console.error('预约失败:', error);
    if (error.response?.data?.message) {
      const message = error.response.data.message;
      if (message.includes('已被预约') || message.includes('冲突')) {
        ElMessage.error('该时段已被预约，请选择其他时间');
      } else {
        ElMessage.error(message);
      }
    }
  }
};
</script>

<style scoped>
.facility-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  padding: 24px;
}

.back-section {
  margin-bottom: 24px;
}

.back-btn {
  background: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #f5f7fa;
  border-color: #409eff;
  color: #409eff;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  min-height: 300px;
}

.loading-spinner .el-icon {
  font-size: 48px;
  color: #409eff;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 16px;
  color: #606266;
  margin-top: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.detail-content {
  max-width: 1200px;
  margin: 0 auto;
}

.info-card,
.timeline-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  margin-bottom: 24px;
  overflow: hidden;
}

.card-header {
  padding: 24px 32px;
  border-bottom: 1px solid #f0f0f0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.facility-name {
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a202c;
  margin: 0;
}

.section-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 8px 0 0 0;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-body {
  padding: 32px;
}

.facility-image-section {
  margin-bottom: 32px;
  border-radius: 12px;
  overflow: hidden;
}

.main-image {
  width: 100%;
  max-height: 500px;
  object-fit: cover;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.info-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.info-label {
  font-size: 13px;
  color: #718096;
  margin-bottom: 8px;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  color: #1a202c;
  font-weight: 600;
}

.description-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.description-content {
  font-size: 15px;
  color: #4a5568;
  line-height: 1.8;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}


.timeline-container {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 16px;
}

.timeline-day {
  min-width: 180px;
  flex-shrink: 0;
  background: #f8fafc;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.day-header {
  padding: 12px 16px;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
  border-bottom: 1px solid #e2e8f0;
  text-align: center;
}

.day-date {
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
}

.day-week {
  font-size: 13px;
  color: #409eff;
  margin-top: 4px;
}

.day-slots {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 400px;
  overflow-y: auto;
}

.time-slot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 10px;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.2s ease;
}

.time-slot.slot-available {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border: 1px solid #86efac;
}

.time-slot.slot-occupied {
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  border: 1px solid #d1d5db;
  cursor: pointer;
}

.time-slot.slot-occupied:hover {
  background: linear-gradient(135deg, #e5e7eb 0%, #d1d5db 100%);
}

.slot-time {
  font-weight: 600;
  color: #374151;
}

.slot-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.slot-label {
  font-size: 12px;
}

.slot-available .slot-label {
  color: #16a34a;
}

.slot-occupied .slot-label {
  color: #6b7280;
}

.slot-tooltip-trigger {
  color: #9ca3af;
  cursor: pointer;
}

.slot-tooltip-trigger:hover {
  color: #409eff;
}

.timeline-legend {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.legend-available {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border: 1px solid #86efac;
}

.legend-occupied {
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  border: 1px solid #d1d5db;
}

.legend-text {
  font-size: 14px;
  color: #4b5563;
}

.action-section {
  text-align: center;
  padding: 24px 0;
}

.reserve-btn {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  border-radius: 12px;
  padding: 16px 48px;
  font-size: 18px;
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.reserve-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
}

.reserve-btn:disabled {
  background: #e2e8f0;
  color: #a0aec0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #dcdfe6;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 20px;
  color: #1a202c;
  margin: 0 0 8px 0;
}

.empty-description {
  font-size: 14px;
  color: #718096;
  margin: 0 0 24px 0;
}

.reservation-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
  border-bottom: 1px solid #f0f0f0;
}

.dialog-title-icon {
  width: 40px;
  height: 40px;
  background: #409eff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-title-icon svg {
  width: 24px;
  height: 24px;
  color: #ffffff;
}

.dialog-title-text {
  font-size: 20px;
  font-weight: 600;
  color: #1a202c;
}

.dialog-body {
  padding: 24px;
}

.facility-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  margin-bottom: 24px;
}

.info-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.info-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
}

.info-model {
  font-size: 14px;
  color: #718096;
  margin-top: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn {
  border-radius: 8px;
}

.availability-check {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 8px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
}

.availability-check .el-icon {
  font-size: 18px;
}

.availability-check .available-icon {
  color: #67c23a;
}

.availability-check .unavailable-icon {
  color: #f56c6c;
}

.availability-check .is-loading {
  color: #409eff;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.availability-text {
  font-size: 14px;
  font-weight: 500;
}

.availability-text.available {
  color: #67c23a;
}

.availability-text.unavailable {
  color: #f56c6c;
}

.submit-btn {
  border-radius: 8px;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
}

@media (max-width: 768px) {
  .timeline-day {
    min-width: 140px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
