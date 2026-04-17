<template>
  <div class="notice-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z" stroke="currentColor" stroke-width="2"/>
              <path d="M12 7v10M7 12h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <circle cx="12" cy="12" r="8" stroke="currentColor" stroke-width="2" fill="none"/>
            </svg>
          </div>
          通知管理
        </h1>
        <p class="page-subtitle">管理系统通知公告，支持发布、编辑和删除通知</p>
      </div>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索通知标题或内容..."
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
          发布通知
        </el-button>
      </div>
    </div>

    <!-- 通知表格 -->
    <div class="table-container">
      <el-table
        :data="filteredNoticeList"
        class="notice-table"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @row-click="handleRowClick"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="title" label="通知标题" min-width="200">
          <template #default="{ row }">
            <div class="notice-title">
              <div class="title">{{ row.title }}</div>
              <div class="content-preview" v-if="row.content">{{ getContentPreview(row.content) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="publisherName" label="发布人" width="120">
          <template #default="{ row }">
            <span class="publisher">{{ row.publisherName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="publishTime" label="发布时间" width="200" align="center" >
          <template #default="{ row }">
            <span class="publish-time">{{ formatTime(row.publishTime) || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="150" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              class="status-tag"
              effect="light"
            >
              <el-icon><CircleCheck v-if="row.status === 'PUBLISHED'" /><EditPen v-else /></el-icon>
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" align="center" fixed="right">
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
      width="700px"
      class="notice-dialog"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" class="notice-form" label-width="100px">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" clearable />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="8" placeholder="请输入通知内容" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%;" placeholder="请选择状态">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="定时发布" value="SCHEDULED" />
            <el-option label="已发布" value="PUBLISHED" />
          </el-select>
        </el-form-item>
        <el-form-item label="定时发布时间" prop="scheduledTime" v-if="form.status === 'SCHEDULED'">
          <el-date-picker
            v-model="form.scheduledTime"
            type="datetime"
            placeholder="选择定时发布时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%;"
            :disabled-date="disabledDate"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '发布' }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { noticeAPI } from '../../api';

const loading = ref(false);
const noticeList = ref([]);
const searchKeyword = ref('');
const isSearchMode = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('发布通知');
const formRef = ref(null);
const isEdit = ref(false);
const userInfo = ref({});
const total = ref(0);

const pagination = reactive({
  page: 1,
  size: 10
});

const form = ref({
  id: null,
  title: '',
  content: '',
  publisherId: null,
  publisherName: '',
  status: 'PUBLISHED',
  scheduledTime: null
});

const rules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
};

// 过滤后的通知列表
const filteredNoticeList = computed(() => {
  if (!searchKeyword.value.trim()) {
    return noticeList.value;
  }
  const keyword = searchKeyword.value.toLowerCase();
  return noticeList.value.filter(notice =>
    notice.title.toLowerCase().includes(keyword) ||
    notice.content.toLowerCase().includes(keyword)
  );
});

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
  const info = localStorage.getItem('userInfo');
  if (info) {
    userInfo.value = JSON.parse(info);
  }
  loadNoticeList();
});

const loadNoticeList = async () => {
  try {
    loading.value = true;
    const res = await noticeAPI.list({
      page: pagination.page - 1,
      size: pagination.size
    });
    noticeList.value = res.data?.content || res.data || [];
    total.value = res.data?.totalElements || res.data?.length || 0;
  } catch (error) {
    console.error('加载通知列表失败:', error);
    ElMessage.error('网络错误，请重试');
  } finally {
    loading.value = false;
  }
};

const handleSearch = async () => {
  if (searchKeyword.value.trim()) {
    isSearchMode.value = true;
  } else {
    isSearchMode.value = false;
  }
};

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  loadNoticeList();
};

// 页码变化
const handleCurrentChange = (page) => {
  pagination.page = page;
  loadNoticeList();
};

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};
const handleClearSearch = () => {
  searchKeyword.value = '';
  isSearchMode.value = false;
};

// 处理行点击
const handleRowClick = (row) => {
  handleEdit(row);
};

const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '发布通知';
  form.value = {
    id: null,
    title: '',
    content: '',
    publisherId: userInfo.value.id,
    publisherName: userInfo.value.realName,
    status: 'PUBLISHED'
  };
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;
  dialogTitle.value = '编辑通知';
  form.value = { ...row };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();

    if (isEdit.value) {
      await noticeAPI.update(form.value.id, form.value);
      ElMessage.success('更新成功');
    } else {
      await noticeAPI.create(form.value);
      ElMessage.success('发布成功');
    }

    dialogVisible.value = false;
    loadNoticeList();
  } catch (error) {
    console.error('提交失败:', error);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该通知？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await noticeAPI.delete(row.id);
      ElMessage.success('删除成功');
      loadNoticeList();
    } catch (error) {
      console.error('删除失败:', error);
    }
  }).catch(() => {});
};

const getStatusType = (status) => {
  const map = {
    'PUBLISHED': 'success',
    'DRAFT': 'warning',
    'SCHEDULED': 'info'
  };
  return map[status] || '';
};

const getStatusText = (status) => {
  const map = {
    'PUBLISHED': '已发布',
    'DRAFT': '草稿',
    'SCHEDULED': '待发布'
  };
  return map[status] || status;
};

const getContentPreview = (content) => {
  if (!content) return '';
  return content.length > 50 ? content.substring(0, 50) + '...' : content;
};

const formatTime = (time) => {
  if (!time) return '';
  try {
    return new Date(time).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return time;
  }
};
</script>

<style scoped>
.notice-page {
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

.notice-table {
  width: 100%;
}

.notice-table :deep(.el-table) {
  width: 100% !important;
}

.notice-table :deep(.el-table__header-wrapper) {
  width: 100% !important;
}

.notice-table :deep(.el-table__body-wrapper) {
  width: 100% !important;
}

.notice-table :deep(.el-table__header) {
  width: 100% !important;
}

.notice-table :deep(.el-table__body) {
  width: 100% !important;
}

.notice-title .title {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.notice-title .content-preview {
  font-size: 12px;
  color: #718096;
  margin-top: 2px;
}

.publisher {
  color: #4a5568;
  font-size: 14px;
}

.publish-time {
  color: #718096;
  font-size: 13px;
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

/* 对话框样式 */
.notice-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.notice-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.notice-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.notice-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.notice-form :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
}

.notice-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.notice-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.notice-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.notice-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.notice-form :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.notice-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.notice-form :deep(.el-select .el-input__wrapper) {
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
</style>