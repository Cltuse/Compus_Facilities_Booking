<template>
  <div class="maintenance-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          设施维护管理
        </h1>
        <p class="page-subtitle">管理设施维护记录，跟踪设施维护状态和历史</p>
      </div>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索设施名称或维护人员..."
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
        <el-button type="primary" size="large" class="add-button" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加维护记录
        </el-button>
      </div>
    </div>

    <!-- 维护记录表格 -->
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
        <el-table-column prop="facilityName" label="设施名称" min-width="180">
          <template #default="{ row }">
            <div class="facility-name">
              <div class="name">{{ row.facilityName }}</div>
              <div class="type">维护类型: {{ getMaintenanceTypeText(row.maintenanceType) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="maintenanceType" label="维护类型" width="150" align="center">
          <template #default="{ row }">
            <el-tag class="type-tag" effect="light">
              {{ getMaintenanceTypeText(row.maintenanceType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="maintainer" label="维护人员" width="150">
          <template #default="{ row }">
            <span class="maintainer">{{ row.maintainer || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="140" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              class="status-tag"
              effect="light"
            >
              <el-icon><CircleCheck v-if="row.status === 'COMPLETED'" /><Tools v-else /></el-icon>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="cost" label="费用" width="130" align="center">
          <template #default="{ row }">
            <span class="cost">¥{{ row.cost ? row.cost.toLocaleString() : '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="startTime" label="开始时间" width="220" align="center">
          <template #default="{ row }">
            <span class="time">{{ row.startTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="220" align="center">
          <template #default="{ row }">
            <span class="time">{{ row.endTime || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                size="small"
                type="primary"
                :plain="true"
                class="action-btn edit-btn"
                @click.stop="handleEdit(row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                size="small"
                type="danger"
                :plain="true"
                class="action-btn delete-btn"
                @click.stop="handleDelete(row)"
              >
                <el-icon><Delete /></el-icon>
                删除
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      class="maintenance-dialog"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" class="maintenance-form" label-width="100px">
        <el-form-item label="设施" prop="facilityId">
          <el-select v-model="form.facilityId" style="width: 100%;" placeholder="请选择设施">
            <el-option
              v-for="item in facilityOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="维护类型" prop="maintenanceType">
          <el-select v-model="form.maintenanceType" style="width: 100%;" placeholder="请选择维护类型">
            <el-option label="定期维护" value="ROUTINE" />
            <el-option label="维修" value="REPAIR" />
            <el-option label="升级" value="UPGRADE" />
          </el-select>
        </el-form-item>
        <el-form-item label="维护人员" prop="maintainerId">
          <el-select v-model="form.maintainerId" style="width: 100%;" placeholder="请选择维护人员" @change="handleMaintainerChange">
            <el-option
              v-for="item in maintainerOptions"
              :key="item.id"
              :label="item.realName || item.username"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="费用" prop="cost">
          <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%;" placeholder="请输入维护费用" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%;"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%;"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%;" placeholder="请选择状态">
            <el-option label="待处理" value="PENDING" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item label="维护描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入维护描述信息" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="维护结果" prop="result">
          <el-input type="textarea" v-model="form.result" :rows="3" placeholder="请输入维护结果信息" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { maintenanceAPI, facilityAPI, userAPI } from '../../api';

const loading = ref(false);
const maintenanceList = ref([]);
const facilityOptions = ref([]);
const maintainerOptions = ref([]);
const searchKeyword = ref('');
const isSearchMode = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('添加维护记录');
const formRef = ref(null);
const isEdit = ref(false);

// 搜索和分页
const total = ref(0);
const pagination = reactive({
  page: 1,
  size: 10,
  sortBy: 'id',
  sortDir: 'desc'
});

const form = ref({
  id: null,
  facilityId: null,
  maintenanceType: 'ROUTINE',
  maintainerId: null,
  maintainer: '',
  cost: 0,
  startTime: '',
  endTime: '',
  status: 'PENDING',
  description: '',
  result: ''
});

const rules = {
  facilityId: [{ required: true, message: '请选择设施', trigger: 'change' }],
  maintenanceType: [{ required: true, message: '请选择维护类型', trigger: 'change' }],
  maintainerId: [{ required: true, message: '请选择维护人员', trigger: 'change' }]
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
  loadMaintenanceList();
  loadFacilityOptions();
  loadMaintainerOptions();
});

const loadMaintenanceList = async () => {
  try {
    loading.value = true;

    const result = await maintenanceAPI.list();

    if (result.code === 200) {
      let filteredData = result.data || [];

      // 如果有搜索关键词，进行前端过滤
      if (isSearchMode.value && searchKeyword.value.trim()) {
        const keyword = searchKeyword.value.trim().toLowerCase();
        filteredData = filteredData.filter(item =>
          (item.facilityName && item.facilityName.toLowerCase().includes(keyword)) ||
          (item.maintainer && item.maintainer.toLowerCase().includes(keyword))
        );
      }

      // 前端分页处理
      total.value = filteredData.length;
      const startIndex = (pagination.page - 1) * pagination.size;
      const endIndex = startIndex + pagination.size;
      maintenanceList.value = filteredData.slice(startIndex, endIndex);
    } else {
      ElMessage.error(result.message || '获取维护记录列表失败');
    }
  } catch (error) {
    console.error('加载维护记录列表失败:', error);
    ElMessage.error('网络错误，请重试');
  } finally {
    loading.value = false;
  }
};

const loadFacilityOptions = async () => {
  try {
    const res = await facilityAPI.list();
    facilityOptions.value = res.data;
  } catch (error) {
    console.error('加载设施列表失败:', error);
  }
};

const loadMaintainerOptions = async () => {
  try {
    const res = await userAPI.list({ page: 0, size: 1000 });
    if (res.data && res.data.content) {
      maintainerOptions.value = res.data.content;
    }
  } catch (error) {
    console.error('加载维护人员列表失败:', error);
  }
};

const handleMaintainerChange = (maintainerId) => {
  const maintainer = maintainerOptions.value.find(item => item.id === maintainerId);
  if (maintainer) {
    form.value.maintainer = maintainer.realName || maintainer.username;
  }
};

const handleSearch = async () => {
  pagination.page = 1;
  if (searchKeyword.value.trim()) {
    isSearchMode.value = true;
  } else {
    isSearchMode.value = false;
  }
  loadMaintenanceList();
};

// 清除搜索
const handleClearSearch = () => {
  searchKeyword.value = '';
  isSearchMode.value = false;
  pagination.page = 1;
  loadMaintenanceList();
};

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  loadMaintenanceList();
};

const handleCurrentChange = (page) => {
  pagination.page = page;
  loadMaintenanceList();
};

// 处理行点击
const handleRowClick = (row) => {
  handleEdit(row);
};

const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '添加维护记录';
  form.value = {
    id: null,
    facilityId: null,
    maintenanceType: 'ROUTINE',
    maintainerId: null,
    maintainer: '',
    cost: 0,
    startTime: '',
    endTime: '',
    status: 'PENDING',
    description: '',
    result: ''
  };
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;
  dialogTitle.value = '编辑维护记录';
  form.value = { ...row };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();

    if (isEdit.value) {
      await maintenanceAPI.update(form.value.id, form.value);
      ElMessage.success('更新成功');
    } else {
      await maintenanceAPI.create(form.value);
      ElMessage.success('添加成功');
    }

    dialogVisible.value = false;
    loadMaintenanceList();
  } catch (error) {
    console.error('提交失败:', error);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该维护记录？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await maintenanceAPI.delete(row.id);
      ElMessage.success('删除成功');
      loadMaintenanceList();
    } catch (error) {
      console.error('删除失败:', error);
    }
  }).catch(() => {});
};

const getMaintenanceTypeText = (type) => {
  const map = {
    'ROUTINE': '定期维护',
    'REPAIR': '维修',
    'UPGRADE': '升级'
  };
  return map[type] || type;
};

const getStatusType = (status) => {
  if (!status || status.trim() === '') {
    return 'info';
  }
  const map = {
    'PENDING': 'info',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  };
  return map[status] || 'info';
};

const getStatusText = (status) => {
  if (!status || status.trim() === '') {
    return '未知';
  }
  const map = {
    'PENDING': '待处理',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return map[status] || status;
};
</script>

<style scoped>
.maintenance-page {
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

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  width: 100%;
}

.maintenance-table {
  width: 100%;
}

.maintenance-table :deep(.el-table) {
  width: 100% !important;
}

.maintenance-table :deep(.el-table__header-wrapper) {
  width: 100% !important;
}

.maintenance-table :deep(.el-table__body-wrapper) {
  width: 100% !important;
}

.maintenance-table :deep(.el-table__header) {
  width: 100% !important;
}

.maintenance-table :deep(.el-table__body) {
  width: 100% !important;
}

.facility-name .name {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.facility-name .type {
  font-size: 12px;
  color: #718096;
  margin-top: 2px;
}

.type-tag {
  border-radius: 6px;
  font-weight: 500;
  font-size: 12px;
  padding: 4px 8px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #bae6fd;
  color: #0369a1;
}

.maintainer {
  color: #4a5568;
  font-size: 14px;
}

.cost {
  color: #059669;
  font-weight: 600;
  font-size: 14px;
}

.time {
  color: #4a5568;
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
.maintenance-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.maintenance-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.maintenance-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.maintenance-dialog :deep(.el-dialog__body) {
  padding: 24px;
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
  font-weight: 500;
  padding: 10px 20px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
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

  .action-buttons {
    flex-direction: column;
    gap: 6px;
  }

  .action-btn {
    width: 100%;
  }
}
</style>