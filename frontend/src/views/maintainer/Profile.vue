<template>
  <div class="profile-page">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          维修人员中心
        </h1>
        <p class="page-subtitle">管理您的个人信息和系统设置</p>
      </div>
    </div>

    <!-- 功能卡片 -->
    <div class="profile-cards">
      <!-- 个人信息卡片 -->
      <div class="profile-card" @click="handleEditProfile">
        <div class="card-header">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar-image" alt="用户头像" />
              <div v-else class="avatar-placeholder">{{ userInfo.realName ? userInfo.realName.charAt(0) : 'U' }}</div>
              <div class="avatar-upload-overlay" @click.stop="handleAvatarUpload">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </div>
          </div>
          <div class="card-title">
            <h3>个人信息</h3>
            <p>更新维修人员的基本资料和联系方式</p>
          </div>
        </div>
        <div class="card-content">
          <div class="info-item">
            <span class="label">用户名</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">真实姓名</span>
            <span class="value">{{ userInfo.realName }}</span>
          </div>
          <div class="info-item">
            <span class="label">角色</span>
            <span class="value">设施维护员</span>
          </div>
        </div>
        <div class="card-actions">
          <el-button type="primary" class="action-btn">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </div>

      <!-- 修改密码卡片 -->
      <div class="profile-card" @click="handleChangePassword">
        <div class="card-header">
          <div class="card-icon password-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
              <path d="M7 11V7C7 4.23858 9.23858 2 12 2C14.7614 2 17 4.23858 17 7V11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="card-title">
            <h3>修改密码</h3>
            <p>更改维修人员的登录密码以保护账户安全</p>
          </div>
        </div>
        <div class="card-content">
          <div class="password-status">
            <div class="status-item">
              <div class="status-icon">
                <svg viewBox="0 0 24 24" fill="none" width="16" height="16">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                  <path d="M7 11V7C7 4.23858 9.23858 2 12 2C14.7614 2 17 4.23858 17 7V11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="status-text">定期修改密码，保护维护员账户安全</span>
            </div>
          </div>
        </div>
        <div class="card-actions">
          <el-button type="warning" class="action-btn">
            <el-icon><Lock /></el-icon>
            修改密码
          </el-button>
        </div>
      </div>
    </div>

    <!-- 编辑个人信息对话框 -->
    <el-dialog
        v-model="profileDialogVisible"
        width="560px"
        class="profile-dialog"
        :show-close="false"
    >
      <template #header>
        <div class="dialog-header">
          <h3 class="dialog-title">编辑个人信息</h3>
          <p class="dialog-subtitle">更新您的基本资料和联系信息</p>
        </div>
      </template>

      <el-form
          :model="profileForm"
          :rules="profileRules"
          ref="profileFormRef"
          label-position="top"
          class="profile-form"
      >
        <el-form-item label="头像" class="avatar-form-item">
          <div class="avatar-upload-section">
            <div class="current-avatar">
              <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar-preview" alt="当前头像" />
              <div v-else class="avatar-preview-placeholder">{{ profileForm.realName ? profileForm.realName.charAt(0) : 'U' }}</div>
            </div>
            <div class="upload-actions">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :on-change="handleAvatarChange"
                accept="image/*"
                :auto-upload="false"
              >
                <el-button type="primary" :icon="Upload">选择图片</el-button>
              </el-upload>
              <p class="upload-tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" placeholder="请输入用户名" :disabled="true" />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelEditProfile">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
        v-model="passwordDialogVisible"
        width="560px"
        class="password-dialog"
        :show-close="false"
    >
      <template #header>
        <div class="dialog-header">
          <h3 class="dialog-title">修改密码</h3>
          <p class="dialog-subtitle">输入新密码以保护账户安全</p>
        </div>
      </template>

      <el-form
          :model="passwordForm"
          :rules="passwordRules"
          ref="passwordFormRef"
          label-position="top"
          class="password-form"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmNewPassword">
          <el-input
              v-model="passwordForm.confirmNewPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelChangePassword">取消</el-button>
          <el-button type="primary" @click="updatePassword">更新密码</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { userAPI, fileAPI } from '../../api';
import { Edit, Lock, Camera, Upload } from '@element-plus/icons-vue';
import { clearAuth, getUserInfo, updateStoredUserInfo } from '../../utils/auth';

const profileDialogVisible = ref(false);
const passwordDialogVisible = ref(false);
const profileFormRef = ref(null);
const passwordFormRef = ref(null);

const userInfo = ref({
  username: '',
  realName: '',
  id: null
});

const profileForm = ref({
  username: '',
  realName: '',
  id: null,
  avatar: ''
});

const avatarFile = ref(null);

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
});

const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '真实姓名长度应在2到20个字符之间', trigger: 'blur' }
  ]
};

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6到20个字符之间', trigger: 'blur' }
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

onMounted(() => {
  loadUserInfo();
});

const loadUserInfo = async () => {
  try {
    const storedUserInfo = getUserInfo();
    if (!storedUserInfo) {
      ElMessage.error('加载用户信息失败');
      return;
    }
    userInfo.value = { ...storedUserInfo };
    profileForm.value = {
      username: storedUserInfo.username || '',
      realName: storedUserInfo.realName || '',
      id: storedUserInfo.id || null,
      avatar: storedUserInfo.avatar || ''
    };
  } catch (error) {
    console.error('加载用户信息失败:', error);
    ElMessage.error('加载用户信息失败');
  }
};

const handleEditProfile = () => {
  profileDialogVisible.value = true;
};

const cancelEditProfile = () => {
  profileDialogVisible.value = false;
  // 重置表单
  profileFormRef.value?.resetFields();
  avatarFile.value = null;
  loadUserInfo(); // 恢复原始数据
};

const saveProfile = async () => {
  try {
    await profileFormRef.value.validate();

    // 如果有新头像，先上传头像
    if (avatarFile.value) {
      try {
        const uploadResult = await fileAPI.uploadAvatar(avatarFile.value);
        if (uploadResult.code === 200) {
          profileForm.value.avatar = uploadResult.data;
        }
      } catch (error) {
        console.error('头像上传失败:', error);
        ElMessage.error('头像上传失败，请重试');
        return;
      }
    }

    const result = await userAPI.updateProfile({
      realName: profileForm.value.realName,
      avatar: profileForm.value.avatar
    });

    if (result.code === 200) {
      ElMessage.success('个人信息更新成功');

      // 更新本地存储
      updateStoredUserInfo({
        realName: profileForm.value.realName,
        avatar: profileForm.value.avatar
      });

      // 更新显示信息
      userInfo.value = {
        ...userInfo.value,
        ...result.data
      };

      profileDialogVisible.value = false;
      avatarFile.value = null;
    } else {
      ElMessage.error(result.message || '更新失败');
    }
  } catch (error) {
    console.error('更新个人信息失败:', error);
  }
};

const handleChangePassword = () => {
  // 重置表单
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  };
  passwordDialogVisible.value = true;
};

const cancelChangePassword = () => {
  passwordDialogVisible.value = false;
  // 重置表单
  passwordFormRef.value?.resetFields();
};

const updatePassword = async () => {
  try {
    await passwordFormRef.value.validate();

    const result = await userAPI.changePassword(userInfo.value.id, {
      currentPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    });

    if (result.code === 200) {
      ElMessage.success('密码修改成功，请重新登录');
      passwordDialogVisible.value = false;
      setTimeout(() => {
        clearAuth();
        window.location.href = '/login';
      }, 2000);
    } else {
      ElMessage.error(result.message || '密码修改失败');
    }
  } catch (error) {
    console.error('修改密码失败:', error);
  }
};

const handleAvatarUpload = () => {
  profileDialogVisible.value = true;
};

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传图片文件！');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！');
    return false;
  }
  return true;
};

const handleAvatarChange = (file) => {
  if (beforeAvatarUpload(file.raw)) {
    avatarFile.value = file.raw;
    const reader = new FileReader();
    reader.onload = (e) => {
      profileForm.value.avatar = e.target.result;
    };
    reader.readAsDataURL(file.raw);
  }
};
</script>

<style scoped>
.profile-page {
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

/* 个人信息卡片 */
.profile-cards {
  display: grid;
  gap: 24px;
  margin-bottom: 24px;
}

.profile-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e2e8f0;
}

.profile-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #bee3f8;
}

.card-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.personal-info-icon {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #409eff;
}

.password-icon {
  background: linear-gradient(135deg, #fffbeb 0%, #fefcbf 100%);
  color: #d69e2e;
}

.card-title h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 4px 0;
}

.card-title p {
  font-size: 14px;
  color: #718096;
  margin: 0;
  font-weight: 400;
}

.card-content {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #edf2f7;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 500;
  color: #4a5568;
}

.value {
  font-weight: 600;
  color: #2d3748;
  word-break: break-all;
  text-align: right;
  max-width: 60%;
}

.password-status {
  padding: 12px 0;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-text {
  color: #718096;
  font-size: 14px;
}

.card-actions {
  text-align: right;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 10px 20px;
  transition: all 0.3s ease;
}

/* 对话框样式 */
.dialog-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.dialog-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 4px 0;
}

.dialog-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0;
  font-weight: 400;
}

.profile-form,
.password-form {
  padding: 20px 0;
}

/* 头像样式 */
.avatar-section {
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  color: #409eff;
}

.avatar-upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.avatar-wrapper:hover .avatar-upload-overlay {
  opacity: 1;
}

.avatar-upload-overlay .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.avatar-upload-overlay span {
  font-size: 12px;
}

/* 头像上传表单样式 */
.avatar-form-item :deep(.el-form-item__content) {
  flex-direction: column;
  align-items: flex-start;
}

.avatar-upload-section {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.current-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  flex-shrink: 0;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-preview-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 700;
  color: #409eff;
}

.upload-actions {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-tip {
  margin: 0;
  font-size: 12px;
  color: #718096;
}

.profile-form :deep(.el-form-item__label),
.password-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #4a5568;
  font-size: 14px;
}

.profile-form :deep(.el-input__wrapper),
.password-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.profile-form :deep(.el-input__wrapper:hover),
.password-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.profile-form :deep(.el-input__wrapper.is-focus),
.password-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.dialog-footer {
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
  text-align: right;
}

.dialog-footer .el-button {
  border-radius: 8px;
  padding: 10px 20px;
}

.profile-dialog :deep(.el-dialog),
.password-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.profile-dialog :deep(.el-dialog__header),
.password-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.profile-dialog :deep(.el-dialog__title),
.password-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.profile-dialog :deep(.el-dialog__body),
.password-dialog :deep(.el-dialog__body) {
  padding: 24px;
}
</style>
