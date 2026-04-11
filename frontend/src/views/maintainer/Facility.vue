<template>
  <div class="maintainer-facility">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18" stroke="currentColor" stroke-width="2"/>
              <line x1="7" y1="2" x2="7" y2="22" stroke="currentColor" stroke-width="2"/>
              <line x1="17" y1="2" x2="17" y2="22" stroke="currentColor" stroke-width="2"/>
              <line x1="2" y1="12" x2="22" y2="12" stroke="currentColor" stroke-width="2"/>
              <line x1="2" y1="7" x2="7" y2="7" stroke="currentColor" stroke-width="2"/>
              <line x1="2" y1="17" x2="7" y2="17" stroke="currentColor" stroke-width="2"/>
              <line x1="17" y1="17" x2="22" y2="17" stroke="currentColor" stroke-width="2"/>
              <line x1="17" y1="7" x2="22" y2="7" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          设施管理
        </h1>
        <p class="page-subtitle">查看设施信息及维护状态</p>
      </div>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索设施名称、型号或类别..."
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

    <!-- 设施表格 -->
    <div class="table-container">
      <el-table
          :data="facilityList"
          class="facility-table"
          :header-cell-style="headerCellStyle"
          :cell-style="cellStyle"
          @row-click="handleRowClick"
          v-loading="loading"
          stripe
      >
        <el-table-column prop="name" label="设施名称" min-width="180">
          <template #default="{ row }">
            <div class="facility-name">
              <div class="name">{{ row.name }}</div>
              <div class="model" v-if="row.model">型号: {{ row.model }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="category" label="类别" width="180"align="center">
          <template #default="{ row }">
            <el-tag class="category-tag" effect="light">
              {{ row.category || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="位置" width="180" align="center">
          <template #default="{ row }">
            <span class="location">{{ row.location || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag
                :type="getStatusType(row.status)"
                class="status-tag"
                effect="light"
            >
              <el-icon><CircleCheck v-if="row.status === 'AVAILABLE'" /><CircleClose v-else-if="row.status === 'DAMAGED'" /><Tools v-else-if="row.status === 'MAINTENANCE'" /><Timer v-else /></el-icon>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="price" label="价格" width="130" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.price ? row.price.toLocaleString() : '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="330" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                  size="small"
                  type="primary"
                  :plain="true"
                  class="action-btn edit-btn"
                  @click.stop="handleView(row)"
              >
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button
                  size="small"
                  type="success"
                  :plain="true"
                  class="action-btn edit-btn"
                  @click.stop="handleStartMaintenance(row)"
                  v-if="row.status !== 'MAINTENANCE'"
              >
                <el-icon><Tools /></el-icon>
                开始维护
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

    <!-- 查看设施详情对话框 -->
    <el-dialog
        title="设施详情"
        v-model="detailDialogVisible"
        width="600px"
        class="facility-detail-dialog"
        :close-on-click-modal="false"
    >
      <div class="facility-detail-content">
        <div class="detail-item">
          <span class="detail-label">设施名称:</span>
          <span class="detail-value">{{ currentFacility.name }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">设施型号:</span>
          <span class="detail-value">{{ currentFacility.model || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">设施类别:</span>
          <span class="detail-value">{{ currentFacility.category || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">存放位置:</span>
          <span class="detail-value">{{ currentFacility.location || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态:</span>
          <span class="detail-value">
            <el-tag :type="getStatusType(currentFacility.status)" size="small">
              {{ getStatusText(currentFacility.status) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item">
          <span class="detail-label">购买日期:</span>
          <span class="detail-value">{{ currentFacility.purchaseDate || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">价格:</span>
          <span class="detail-value">¥{{ currentFacility.price ? currentFacility.price.toLocaleString() : '-' }}</span>
        </div>
        <div class="detail-item full-width">
          <span class="detail-label">设施描述:</span>
          <span class="detail-value">{{ currentFacility.description || '-' }}</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 维护任务创建对话框 -->
    <el-dialog
        title="开始维护"
        v-model="maintenanceDialogVisible"
        width="600px"
        class="maintenance-dialog"
        :close-on-click-modal="false"
    >
      <el-form :model="maintenanceForm" :rules="maintenanceRules" ref="maintenanceFormRef" class="maintenance-form" label-width="100px">
        <el-form-item label="设施名称">
          <el-input v-model="currentFacility.name" disabled />
        </el-form-item>
        <el-form-item label="维护类型" prop="maintenanceType">
          <el-select v-model="maintenanceForm.maintenanceType" style="width: 100%;" placeholder="请选择维护类型">
            <el-option label="日常保养" value="日常保养" />
            <el-option label="故障维修" value="故障维修" />
            <el-option label="设备升级" value="设备升级" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="维护描述" prop="description">
          <el-input type="textarea" v-model="maintenanceForm.description" :rows="3" placeholder="请输入维护描述信息" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="计划开始时间" prop="startTime">
          <el-date-picker
              v-model="maintenanceForm.startTime"
              type="datetime"
              placeholder="选择计划开始时间"
              style="width: 100%;"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="maintenanceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitMaintenance">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { facilityAPI, maintenanceAPI } from '../../api';
import { View, CircleCheck, CircleClose, Tools, Timer, Search, Plus, Edit, Delete, Upload } from '@element-plus/icons-vue';

const loading = ref(false);
const facilityList = ref([]);
const searchKeyword = ref('');
const isSearchMode = ref(false);
const detailDialogVisible = ref(false);
const maintenanceDialogVisible = ref(false);
const currentFacility = ref({});
const maintenanceFormRef = ref(null);
const currentUser = ref({});

// 搜索和分页
const total = ref(0);
const pagination = reactive({
  page: 1,
  size: 10,
  sortBy: 'id',
  sortDir: 'desc'
});

const maintenanceForm = ref({
  facilityId: null,
  maintainerId: null,
  maintenanceType: '',
  description: '',
  startTime: ''
});

const maintenanceRules = {
  maintenanceType: [{ required: true, message: '请选择维护类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入维护描述', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择计划开始时间', trigger: 'change' }]
};

// 表格样式
const headerCellStyle = {
  backgroundColor: '#f8fafc',
  color: '#4a5568',
  fontWeight: '600',
  borderBottom: '1px solid #e2e8f0'
};

const cellStyle = {
  borderBottom: '1px solid #e2e8f0',
  padding: '16px 12px'
};

onMounted(() => {
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    currentUser.value = JSON.parse(userInfo);
  }
  loadFacilityList();
});

const loadFacilityList = async () => {
  try {
    loading.value = true;

    let result;
    const params = {
      page: pagination.page - 1,
      size: pagination.size,
      sortBy: pagination.sortBy,
      sortDir: pagination.sortDir
    };

    if (isSearchMode.value && searchKeyword.value.trim()) {
      result = await facilityAPI.searchPage(searchKeyword.value.trim(), params);
    } else {
      result = await facilityAPI.listPage(params);
    }

    if (result.code === 200) {
      facilityList.value = result.data.content || [];
      total.value = result.data.totalElements || 0;
    } else {
      ElMessage.error(result.message || '获取设施列表失败');
    }
  } catch (error) {
    console.error('加载设施列表失败:', error);
    ElMessage.error('网络错误，请重试');
  } finally {
    loading.value = false;
  }
};

const handleSearch = async () => {
  pagination.page = 1;
  if (searchKeyword.value.trim()) {
    isSearchMode.value = true;
  } else {
    isSearchMode.value = false;
  }
  loadFacilityList();
};

// 清除搜索
const handleClearSearch = () => {
  searchKeyword.value = '';
  isSearchMode.value = false;
  pagination.page = 1;
  loadFacilityList();
};

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  loadFacilityList();
};

const handleCurrentChange = (page) => {
  pagination.page = page;
  loadFacilityList();
};

// 处理行点击
const handleRowClick = (row) => {
  handleView(row);
};

const handleView = (row) => {
  currentFacility.value = { ...row };
  detailDialogVisible.value = true;
};

const handleStartMaintenance = (row) => {
  currentFacility.value = { ...row };
  maintenanceForm.value = {
    facilityId: row.id,
    maintainerId: currentUser.value.id || null,
    maintenanceType: '',
    description: '',
    startTime: ''
  };
  maintenanceDialogVisible.value = true;
};

const submitMaintenance = async () => {
  try {
    await maintenanceFormRef.value.validate();

    const result = await maintenanceAPI.create(maintenanceForm.value);
    if (result.code === 200) {
      ElMessage.success('维护任务创建成功');
      maintenanceDialogVisible.value = false;
      loadFacilityList();
    } else {
      ElMessage.error(result.message || '维护任务创建失败');
    }
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败，请重试');
  }
};

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
</script>

<style scoped>
.maintainer-facility {
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

/* 工具栏 */
.toolbar {
  margin-bottom: 24px;
  padding: 0;
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

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  width: 100%;
}

.facility-table {
  width: 100%;
}

.facility-table :deep(.el-table) {
  width: 100% !important;
}

.facility-table :deep(.el-table__header-wrapper) {
  width: 100% !important;
}

.facility-table :deep(.el-table__body-wrapper) {
  width: 100% !important;
}

.facility-table :deep(.el-table__header) {
  width: 100% !important;
}

.facility-table :deep(.el-table__body) {
  width: 100% !important;
}

.facility-name .name {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.facility-name .model {
  font-size: 12px;
  color: #718096;
  margin-top: 2px;
}

.category-tag {
  border-radius: 6px;
  font-weight: 500;
  font-size: 12px;
  padding: 4px 8px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #bae6fd;
  color: #0369a1;
}

.location {
  color: #4a5568;
  font-size: 14px;
}

.price {
  color: #059669;
  font-weight: 600;
  font-size: 14px;
}

/* 状态标签 */
.status-tag {
  border-radius: 6px;
  font-weight: 500;
  padding: 4px 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 6px 12px;
  transition: all 0.3s ease;
}

.edit-btn {
  border-color: #409eff;
  color: #409eff;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border-color: transparent;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.delete-btn {
  border-color: #f56565;
  color: #f56565;
}

.delete-btn:hover {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
  border-color: transparent;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.3);
}

/* 分页容器 */
.pagination-container {
  padding: 20px 0;
  display: flex;
  justify-content: center;
}

.custom-pagination :deep(.el-pagination) {
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

/* 对话框样式 */
.facility-detail-dialog :deep(.el-dialog),
.maintenance-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.facility-detail-dialog :deep(.el-dialog__header),
.maintenance-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.facility-detail-dialog :deep(.el-dialog__title),
.maintenance-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.facility-detail-dialog :deep(.el-dialog__body),
.maintenance-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.facility-detail-content .detail-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.detail-item.full-width {
  flex-direction: column;
}

.detail-item.full-width .detail-value {
  margin-top: 8px;
}

.detail-label {
  font-weight: 600;
  color: #4a5568;
  min-width: 100px;
  margin-right: 16px;
  flex-shrink: 0;
}

.detail-value {
  color: #2d3748;
  flex: 1;
  word-break: break-word;
}

.maintenance-form :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
}

.maintenance-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.maintenance-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.maintenance-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.maintenance-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.maintenance-form :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.maintenance-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.maintenance-form :deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

.dialog-footer {
  padding: 16px 24px 24px;
  text-align: right;
}

.dialog-footer .el-button {
  border-radius: 8px;
}

.damage-image-upload .image-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.damage-image-upload .preview-img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 8px;
  object-fit: contain;
}

.damage-image-upload .image-actions {
  display: flex;
  gap: 8px;
}

.damage-image-upload .image-upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
  cursor: pointer;
  transition: all 0.3s ease;
}

.damage-image-upload .image-upload-area:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.damage-image-upload .upload-icon {
  font-size: 48px;
  color: #a0aec0;
  margin-bottom: 12px;
}

.damage-image-upload .upload-text {
  text-align: center;
}

.damage-image-upload .upload-title {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  margin-bottom: 4px;
}

.damage-image-upload .upload-hint {
  font-size: 12px;
  color: #a0aec0;
}
</style>