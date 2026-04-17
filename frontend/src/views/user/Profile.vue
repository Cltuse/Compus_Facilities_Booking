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
          个人中心
        </h1>
        <p class="page-subtitle">管理您的个人信息和账户安全</p>
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
            <p>更新您的基本资料和联系方式</p>
          </div>
        </div>
        <div class="card-content">
          <div class="info-item">
            <span class="label">学号/工号</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">姓名</span>
            <span class="value">{{ userInfo.realName }}</span>
          </div>
          <div class="info-item">
            <span class="label">身份</span>
            <span class="value">{{ getRoleDisplayName(userInfo.role) }}</span>
          </div>
          <div class="info-item">
            <span class="label">电话</span>
            <span class="value">{{ userInfo.phone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱</span>
            <span class="value">{{ userInfo.email || '未设置' }}</span>
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
            <p>更改您的登录密码以保护账户安全</p>
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
              <span class="status-text">定期修改密码，保护账户安全</span>
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
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">编辑个人信息</span>
      </div>

      <div class="dialog-body">
        <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="80px" class="profile-form">
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
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="profileForm.phone" placeholder="请输入电话号码" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="profileForm.email" placeholder="请输入邮箱地址" />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="profileDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" size="large" @click="handleProfileSubmit" class="submit-btn">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      width="560px"
      class="profile-dialog"
      :show-close="false"
    >
      <div class="dialog-header">
        <div class="dialog-title-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
            <path d="M7 11V7C7 4.23858 9.23858 2 12 2C14.7614 2 17 4.23858 17 7V11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="dialog-title-text">修改密码</span>
      </div>

      <div class="dialog-body">
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" class="password-form">
          <el-form-item label="当前密码" prop="currentPassword">
            <el-input
              v-model="passwordForm.currentPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
              clearable
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="passwordDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" size="large" @click="handlePasswordSubmit" class="submit-btn">修改密码</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Edit, Lock, Camera, Upload } from '@element-plus/icons-vue';
import { userAPI, fileAPI } from '../../api';
import { getRoleDisplayName } from '../../utils/roleMapping';
import { clearAuth, getUserInfo, updateStoredUserInfo } from '../../utils/auth';

const userInfo = ref({});
const profileDialogVisible = ref(false);
const passwordDialogVisible = ref(false);
const profileFormRef = ref(null);
const passwordFormRef = ref(null);

const profileForm = ref({
  realName: '',
  phone: '',
  email: '',
  avatar: ''
});

const avatarFile = ref(null);

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const profileRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
};

const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
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

const loadUserInfo = () => {
  const info = getUserInfo();
  if (info) {
    userInfo.value = info;
    profileForm.value = {
      realName: userInfo.value.realName || '',
      phone: userInfo.value.phone || '',
      email: userInfo.value.email || '',
      avatar: userInfo.value.avatar || ''
    };
  }
};

const handleEditProfile = () => {
  profileDialogVisible.value = true;
};

const handleChangePassword = () => {
  passwordDialogVisible.value = true;
};

const handleProfileSubmit = async () => {
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

    // 更新个人信息
    const updatedUser = {
      realName: profileForm.value.realName,
      phone: profileForm.value.phone,
      email: profileForm.value.email,
      avatar: profileForm.value.avatar
    };

    // 调用API更新用户信息
    const res = await userAPI.updateProfile(updatedUser);
    const nextUser = { ...userInfo.value, ...res.data };

    // 更新本地存储
    updateStoredUserInfo(nextUser);
    userInfo.value = nextUser;

    profileDialogVisible.value = false;
    ElMessage.success('个人信息更新成功');
    avatarFile.value = null;
  } catch (error) {
    console.error('更新个人信息失败:', error);
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

const handlePasswordSubmit = async () => {
  try {
    await passwordFormRef.value.validate();

    // 调用API修改密码
    const passwordData = {
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword
    };

    await userAPI.changePassword(userInfo.value.id, passwordData);

    passwordDialogVisible.value = false;
    ElMessage.success('密码修改成功，请重新登录');

    // 清空表单
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    };

    // 2秒后自动退出登录
    setTimeout(() => {
      clearAuth();
      window.location.href = '/login';
    }, 2000);
  } catch (error) {
    console.error('修改密码失败:', error);
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(error.response.data.message);
    } else {
      ElMessage.error('修改密码失败，请重试');
    }
  }
};
</script>

<style scoped>
/* 页面背景 */
.profile-page {
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

/* 功能卡片 */
.profile-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.profile-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 32px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.profile-card::before {
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

.profile-card:hover::before {
  opacity: 1;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

.profile-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: #bae7ff;
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 24px;
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.personal-info-icon {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
}

.password-icon {
  background: linear-gradient(135deg, #fff7ed 0%, #fed7aa 100%);
}

.card-icon svg {
  width: 32px;
  height: 32px;
}

.personal-info-icon svg {
  color: #409eff;
}

.password-icon svg {
  color: #f97316;
}

.card-title {
  flex: 1;
}

.card-title h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.card-title p {
  font-size: 14px;
  color: #718096;
  margin: 0;
  line-height: 1.5;
}

.card-content {
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f7fafc;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

.info-item .value {
  font-size: 14px;
  color: #2d3748;
  font-weight: 600;
}

.password-status {
  padding: 16px;
  background: #f7fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
}

.status-text {
  font-size: 14px;
  color: #4a5568;
}

.card-actions {
  display: flex;
  justify-content: center;
}

.action-btn {
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border: none;
  border-radius: 12px;
  padding: 12px 32px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.action-btn:hover::before {
  left: 100%;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, #66b1ff 0%, #1976d2 100%);
}

.action-btn:active {
  transform: translateY(0);
}

.action-btn.el-button--warning {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  box-shadow: 0 4px 14px rgba(249, 115, 22, 0.3);
}

.action-btn.el-button--warning:hover {
  background: linear-gradient(135deg, #fb923c 0%, #ea580c 100%);
  box-shadow: 0 8px 20px rgba(249, 115, 22, 0.4);
}

/* 对话框样式 */
.profile-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.profile-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.profile-dialog :deep(.el-dialog__header) {
  padding: 0;
  margin: 0;
  border-bottom: none;
}

.profile-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.profile-dialog :deep(.el-dialog__footer) {
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

/* 头像上传样式 */
.avatar-section {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
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
  font-size: 20px;
  margin-bottom: 4px;
}

.avatar-upload-overlay span {
  font-size: 12px;
  font-weight: 500;
}

.avatar-form-item {
  margin-bottom: 24px;
}

.avatar-upload-section {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.current-avatar {
  flex-shrink: 0;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  object-fit: cover;
  border: 3px solid #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-preview-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 700;
  color: #ffffff;
  border: 3px solid #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.upload-actions {
  flex: 1;
}

.upload-tip {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: #718096;
}

.profile-form :deep(.el-form-item__label),
.password-form :deep(.el-form-item__label) {
  color: #2d3748;
  font-weight: 600;
  font-size: 14px;
}

.profile-form :deep(.el-input__wrapper),
.password-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  background: #f7fafc;
}

.profile-form :deep(.el-input__wrapper:hover),
.password-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  background: #ffffff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.profile-form :deep(.el-input__wrapper.is-focus),
.password-form :deep(.el-input__wrapper.is-focus) {
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
  .profile-page {
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

  .profile-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .profile-card {
    padding: 24px;
  }

  .card-header {
    gap: 16px;
  }

  .card-icon {
    width: 48px;
    height: 48px;
  }

  .card-icon svg {
    width: 24px;
    height: 24px;
  }

  .card-title h3 {
    font-size: 18px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .dialog-body {
    padding: 20px;
  }

  .card-actions {
    justify-content: center;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
