<template>
  <div class="user-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M9 11C11.2091 11 13 9.20914 13 7C13 4.79086 11.2091 3 9 3C6.79086 3 5 4.79086 5 7C5 9.20914 6.79086 11 9 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M23 21V19C22.9993 18.1137 22.7044 17.2528 22.1214 16.6718C21.5383 16.0908 20.6762 15.7987 19.79 15.8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 3.13C16.8604 3.35031 17.6246 3.85071 18.1677 4.55232C18.7108 5.25392 19.0038 6.11683 19.0038 7.005C19.0038 7.89318 18.7108 8.75608 18.1677 9.45769C17.6246 10.1593 16.8604 10.6597 16 10.88" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          用户管理
        </h1>
        <p class="page-subtitle">管理系统用户账户信息</p>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" size="large" class="add-button" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加用户
      </el-button>
    </div>

    <!-- 用户表格 -->
    <div class="table-container">
      <el-table
        :data="userList"
        class="user-table"
        :header-cell-style="headerCellStyle"
        :cell-style="cellStyle"
        @row-click="handleRowClick"
      >
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <div class="user-details">
              <div class="username">{{ row.username }}</div>
              <div class="real-name">{{ row.realName }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="role" label="角色" width="150" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getRoleTagType(row.role)"
              class="role-tag"
              effect="light"
            >
              <el-icon><User v-if="row.role === 'ADMIN'" /><Tools v-else-if="row.role === 'MAINTAINER'" /><School v-else-if="row.role === 'TEACHER'" /><UserFilled v-else /></el-icon>
              {{ formatRoleDisplayName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="电话" min-width="150" align="center">
          <template #default="{ row }">
            <span class="contact-info">{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="邮箱" min-width="200" align="center">
          <template #default="{ row }">
            <span class="contact-info email-info">{{ row.email || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="140" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'ACTIVE' ? 'success' : 'info'"
              class="status-tag"
              effect="light"
            >
              <el-icon><CircleCheck v-if="row.status === 'ACTIVE'" /><CircleClose v-else /></el-icon>
              {{ row.status === 'ACTIVE' ? '激活' : '禁用' }}
            </el-tag>
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

    <!-- 编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="560px"
      class="user-dialog"
      :show-close="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">{{ dialogTitle }}</span>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="user-form">
        <div class="form-content">
          <div class="form-row">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="form.username"
                :disabled="isEdit"
                placeholder="请输入用户名"
              />
            </el-form-item>
            <el-form-item v-if="!isEdit" label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="角色" prop="role">
              <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%;">
                <el-option v-for="option in roleOptions" :key="option.value" :label="option.label" :value="option.value" />
              </el-select>
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话号码" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="激活" value="ACTIVE" />
                <el-option label="禁用" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </div>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" size="large" @click="handleSubmit" class="submit-btn">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { userAPI } from '../../api';
import { getRoleDisplayName, getRoleOptions } from '../../utils/roleMapping';
import { Plus, Edit, Delete, Lock, CircleCheck, CircleClose, User, UserFilled, Tools, School } from '@element-plus/icons-vue';

const userList = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref('添加用户');
const formRef = ref(null);
const isEdit = ref(false);
const roleOptions = ref(getRoleOptions());
const total = ref(0);

const pagination = reactive({
  page: 1,
  size: 10
});

const form = ref({
  id: null,
  username: '',
  password: '',
  realName: '',
  role: 'USER',
  phone: '',
  email: '',
  status: 'ACTIVE'
});

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
};

onMounted(() => {
  loadUserList();
});

const loadUserList = async () => {
  try {
    const res = await userAPI.list({
      page: pagination.page - 1,
      size: pagination.size
    });
    userList.value = res.data?.content || res.data || [];
    total.value = res.data?.totalElements || res.data?.length || 0;
  } catch (error) {
    console.error('加载用户列表失败:', error);
  }
};

const handleSizeChange = (size) => {
  pagination.size = size;
  pagination.page = 1;
  loadUserList();
};

const handleCurrentChange = (page) => {
  pagination.page = page;
  loadUserList();
};

const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '添加用户';
  form.value = {
    id: null,
    username: '',
    password: '',
    realName: '',
    role: 'USER',
    phone: '',
    email: '',
    status: 'ACTIVE'
  };
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;
  dialogTitle.value = '编辑用户';
  form.value = { ...row, password: '' };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();

    if (isEdit.value) {
      // 编辑模式：直接发送表单数据（不包含密码字段）
      await userAPI.update(form.value.id, form.value);
      ElMessage.success('更新成功');
    } else {
      // 新增模式：密码是必填的
      await userAPI.create(form.value);
      ElMessage.success('添加成功');
    }

    dialogVisible.value = false;
    loadUserList();
  } catch (error) {
    console.error('提交失败:', error);
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'delete-confirm-dialog'
  }).then(async () => {
    try {
      await userAPI.delete(row.id);
      ElMessage.success('删除成功');
      loadUserList();
    } catch (error) {
      console.error('删除失败:', error);
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

// 获取角色显示名称
    const formatRoleDisplayName = (role) => {
      return getRoleDisplayName(role);
    };

// 获取角色标签类型
const getRoleTagType = (role) => {
  switch (role) {
    case 'ADMIN': return 'danger';
    case 'MAINTAINER': return 'warning';
    case 'TEACHER': return 'success';
    case 'STUDENT': return 'primary';
    default: return 'info';
  }
};

// 表格行点击处理
const handleRowClick = (row) => {
  handleEdit(row);
};
</script>

<style scoped>
/* 页面背景 */
.user-page {
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
}

/* 移除hover动画效果，保持与其他页面一致 */
/* .header-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
} */

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

/* 移除鼠标悬停效果，保持与其他页面一致 */
/* .title-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.25);
} */

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
}

.add-button {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.add-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.add-button:hover::before {
  left: 100%;
}

.add-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #1976d2 100%);
}

.add-button:active {
  transform: translateY(0);
}

/* 表格容器 */
.table-container {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 表格样式 */
.user-table {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
}

.user-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 100%);
}

.user-table :deep(.el-table__header) {
  border-radius: 16px 16px 0 0;
}

.user-table :deep(.el-table__header th) {
  background: transparent !important;
  border-bottom: 2px solid #e2e8f0;
  color: #2d3748;
  font-weight: 600;
  font-size: 14px;
  padding: 20px 12px;
}

.user-table :deep(.el-table__header th .cell) {
  font-weight: 600;
  color: #2d3748;
}

.user-table :deep(.el-table__body-wrapper) {
  background: #ffffff;
}

.user-table :deep(.el-table__body tr) {
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-table :deep(.el-table__body tr:hover > td) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%) !important;
}

.user-table :deep(.el-table__body tr:hover > td .cell) {
  color: #1a202c !important;
}

.user-table :deep(.el-table__body td) {
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 12px;
  transition: all 0.3s ease;
}

.user-table :deep(.el-table__body td .cell) {
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
}

/* 表格单元格内容样式 */
.user-details {
  min-width: 0;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.real-name {
  font-size: 12px;
  color: #718096;
  font-weight: 400;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-info {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

.email-info {
  word-break: break-all;
  line-height: 1.4;
}

/* 标签样式 */
.role-tag, .status-tag {
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

.role-tag {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(25, 118, 210, 0.1) 100%);
  color: #1976d2;
}

.role-tag.el-tag--danger {
  background: linear-gradient(135deg, rgba(245, 101, 101, 0.1) 0%, rgba(229, 62, 62, 0.1) 100%);
  color: #e53e3e;
}

.status-tag {
  background: linear-gradient(135deg, rgba(72, 187, 120, 0.1) 0%, rgba(56, 161, 105, 0.1) 100%);
  color: #38a169;
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

.edit-btn {
  color: #409eff;
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.05);
}

.edit-btn:hover {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  color: #ffffff;
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.delete-btn {
  color: #f56565;
  border-color: #f56565;
  background: rgba(245, 101, 101, 0.05);
}

.delete-btn:hover {
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
.user-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.user-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.user-dialog :deep(.el-dialog__header) {
  padding: 0;
  margin: 0;
  border-bottom: none;
}

.user-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.user-dialog :deep(.el-dialog__footer) {
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

.user-form {
  padding: 32px;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
  margin-bottom: 0;
}

.user-form :deep(.el-form-item__label) {
  color: #2d3748;
  font-weight: 600;
  font-size: 14px;
}

.user-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  background: #f7fafc;
}

.user-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  background: #ffffff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.user-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.user-form :deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

.user-form :deep(.el-select:hover .el-input__wrapper) {
  border-color: #cbd5e0;
  background: #ffffff;
}

.user-form :deep(.el-select.is-focused .el-input__wrapper) {
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
  .user-page {
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

  .table-container {
    overflow-x: auto;
    border-radius: 12px;
  }

  .user-table {
    min-width: 800px;
  }

  .user-table :deep(.el-table__header th),
  .user-table :deep(.el-table__body td) {
    padding: 12px 8px;
  }

  
  .username {
    font-size: 13px;
  }

  .real-name {
    font-size: 11px;
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

  .form-row {
    flex-direction: column;
    gap: 0;
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