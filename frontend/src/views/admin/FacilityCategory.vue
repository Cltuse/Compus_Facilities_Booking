<template>
  <div class="facility-category-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <line x1="7" y1="7" x2="7.01" y2="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          设施类别
        </h1>
        <p class="page-subtitle">管理设施分类信息，支持添加、编辑和删除设施类别</p>
      </div>
    </div>

    <!-- 搜索和工具栏 -->
    <div class="toolbar">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索类别名称或描述..."
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
          添加类别
        </el-button>
      </div>
    </div>

    <!-- 设施类别表格 -->
    <div class="table-container">
      <el-table
        :data="categoryList"
        class="category-table"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @row-click="handleRowClick"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="categoryName" label="类别名称" min-width="150">
          <template #default="{ row }">
            <div class="category-name">
              <div class="name">{{ row.categoryName }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="描述" min-width="200" align="center">
          <template #default="{ row }">
            <span class="description">{{ row.description || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="sortOrder" label="排序" width="80" align="center">
          <template #default="{ row }">
            <span class="sort-order">{{ row.sortOrder || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'ACTIVE' ? 'success' : 'info'"
              class="status-tag"
              effect="light"
            >
              <el-icon><CircleCheck v-if="row.status === 'ACTIVE'" /><CircleClose v-else /></el-icon>
              {{ row.status === 'ACTIVE' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            <span class="time">{{ formatDateTime(row.createdTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updatedTime" label="更新时间" width="160" align="center">
          <template #default="{ row }">
            <span class="time">{{ formatDateTime(row.updatedTime) }}</span>
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
      v-model="dialogVisible"
      :title="isEdit ? '编辑设施类别' : '添加设施类别'"
      width="500px"
      class="category-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        class="category-form"
        label-width="80px"
      >
        <el-form-item label="类别名称" prop="categoryName">
          <el-input
            v-model="formData.categoryName"
            placeholder="请输入类别名称"
            clearable
          />
        </el-form-item>

        
        <el-form-item label="描述信息" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入类别描述信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="排序顺序" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="9999"
            placeholder="排序顺序"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select
            v-model="formData.status"
            placeholder="请选择状态"
            style="width: 100%;"
          >
            <el-option label="启用" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { facilityCategoryAPI } from '../../api';

// 数据
const loading = ref(false);
const dialogVisible = ref(false);
const submitLoading = ref(false);
const isEdit = ref(false);
const categoryList = ref([]);
const formRef = ref(null);

// 搜索和分页
const searchKeyword = ref('');
const isSearchMode = ref(false);
const total = ref(0);
const pagination = reactive({
  page: 1,
  size: 10,
  sortBy: 'sortOrder',
  sortDir: 'asc'
});

// 表单数据
const formData = reactive({
  id: null,
  categoryName: '',
  description: '',
  sortOrder: 0,
  status: 'ACTIVE'
});

// 表单验证规则
const rules = {
  categoryName: [
    { required: true, message: '请输入类别名称', trigger: 'blur' },
    { min: 2, max: 50, message: '类别名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
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

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  const date = new Date(dateTime);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取设施类别列表
const fetchCategories = async () => {
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
      result = await facilityCategoryAPI.searchPage(searchKeyword.value.trim(), params);
    } else {
      result = await facilityCategoryAPI.listPage(params);
    }

    if (result.code === 200) {
      categoryList.value = result.data.content || [];
      total.value = result.data.totalElements || 0;
    } else {
      ElMessage.error(result.message || '获取设施类别列表失败');
    }
  } catch (error) {
    console.error('获取设施类别列表失败:', error);
    ElMessage.error('网络错误，请重试');
  } finally {
    loading.value = false;
  }
};

// 搜索处理
const handleSearch = () => {
  pagination.page = 1;
  isSearchMode.value = true;
  fetchCategories();
};

// 清除搜索
const handleClearSearch = () => {
  searchKeyword.value = '';
  isSearchMode.value = false;
  pagination.page = 1;
  fetchCategories();
};

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  fetchCategories();
};

const handleCurrentChange = (page) => {
  pagination.page = page;
  fetchCategories();
};

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    categoryName: '',
    description: '',
    sortOrder: 0,
    status: 'ACTIVE'
  });
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// 处理添加
const handleAdd = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

// 处理编辑
const handleEdit = (row) => {
  isEdit.value = true;
  Object.assign(formData, {
    id: row.id,
    categoryName: row.categoryName,
    description: row.description || '',
    sortOrder: row.sortOrder || 0,
    status: row.status
  });
  dialogVisible.value = true;
};

// 处理行点击
const handleRowClick = (row) => {
  handleEdit(row);
};

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除设施类别 "${row.categoryName}" 吗？此操作不可撤销。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    const result = await facilityCategoryAPI.delete(row.id);

    if (result.code === 200) {
      ElMessage.success('删除成功');
      await fetchCategories();
    } else {
      ElMessage.error(result.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败，请重试');
    }
  }
};

// 处理表单提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    submitLoading.value = true;

    const result = isEdit.value
      ? await facilityCategoryAPI.update(formData.id, formData)
      : await facilityCategoryAPI.create(formData);

    if (result.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功');
      dialogVisible.value = false;
      await fetchCategories();
    } else {
      ElMessage.error(result.message || (isEdit.value ? '更新失败' : '创建失败'));
    }
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败，请重试');
  } finally {
    submitLoading.value = false;
  }
};

// 页面加载时获取数据
onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
.facility-category-page {
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
}

.category-table {
  width: 100%;
}

.category-name .name {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.category-name .code {
  font-size: 12px;
  color: #718096;
  margin-top: 2px;
}

.description {
  color: #4a5568;
  font-size: 14px;
  line-height: 1.5;
}

.sort-order {
  color: #4a5568;
  font-weight: 600;
}

.time {
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
.category-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.category-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.category-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.category-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.category-form :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
}

.category-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.category-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.category-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.category-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.category-form :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.category-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.category-form :deep(.el-select .el-input__wrapper) {
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