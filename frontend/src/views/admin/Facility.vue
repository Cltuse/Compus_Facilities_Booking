<template>
  <div class="facility-page">
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
        <p class="page-subtitle">管理设施信息，支持添加、编辑和删除设施</p>
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
      <div class="button-section">
        <el-button type="primary" size="large" class="add-button" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加设施
        </el-button>
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
        <el-table-column label="图片" width="120" align="center">
          <template #default="{ row }">
            <div class="facility-image">
              <img 
                v-if="row.imageUrl && isValidImageUrl(row.imageUrl)" 
                :src="row.imageUrl" 
                :alt="row.name"
                class="facility-thumb"
                @error="handleImageError"
              />
              <div v-else class="no-image">
                <el-icon><Picture /></el-icon>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="设施名称" min-width="180" align="center">
          <template #default="{ row }">
            <div class="facility-name">
              <div class="name">{{ row.name }}</div>
              <div class="model" v-if="row.model">型号: {{ row.model }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="category" label="类别" min-width="140" align="center">
          <template #default="{ row }">
            <el-tag class="category-tag" effect="light">
              {{ row.category || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="位置" min-width="180" align="center">
          <template #default="{ row }">
            <span class="location">{{ row.location || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" min-width="110" align="center">
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
      width="600px"
      class="facility-dialog"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" class="facility-form" label-width="100px">
        <el-form-item label="设施名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设施名称" clearable />
        </el-form-item>
        <el-form-item label="设施型号" prop="model">
          <el-input v-model="form.model" placeholder="请输入设施型号" clearable />
        </el-form-item>
        <el-form-item label="设施类别" prop="category">
          <el-select v-model="form.category" style="width: 100%;" placeholder="请选择设施类别">
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryName"
              :label="item.categoryName"
              :value="item.categoryName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入存放位置" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%;" placeholder="请选择状态">
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="维护中" value="MAINTENANCE" />
            <el-option label="损坏" value="DAMAGED" />
          </el-select>
        </el-form-item>
        <el-form-item label="购买日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择购买日期"
            style="width: 100%;"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%;" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="设施描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入设施描述信息" maxlength="200" show-word-limit />
        </el-form-item>
        
        <!-- 设施图片 -->
        <el-form-item label="设施图片" prop="imageUrl">
          <div class="image-upload-section">
            <div v-if="form.imageUrl" class="current-image-preview">
              <img :src="form.imageUrl" :alt="form.name" class="facility-preview-image" />
              <div class="image-actions">
                <el-button size="small" type="danger" @click="removeImage" :icon="Delete">删除图片</el-button>
                <el-button size="small" type="primary" @click="triggerFileSelect" :icon="Upload">更换图片</el-button>
              </div>
            </div>
            <div v-else class="no-image-upload">
              <el-upload
                ref="dialogUploadRef"
                class="dialog-image-uploader"
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :before-upload="beforeDialogImageUpload"
                :on-change="handleDialogImageSelect"
                accept="image/*"
                drag
              >
                <el-icon class="upload-icon"><Upload /></el-icon>
                <div class="upload-text">
                  <div class="upload-title">点击或拖拽上传设施图片</div>
                  <div class="upload-hint">支持 JPG、PNG、GIF 格式，大小不超过 10MB</div>
                </div>
              </el-upload>
            </div>
            <input 
              ref="fileInputRef" 
              type="file" 
              accept="image/*" 
              style="display: none;" 
              @change="handleFileInputChange"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片上传对话框 -->
    <el-dialog
      title="设施图片管理"
      v-model="imageDialogVisible"
      width="500px"
      class="image-dialog"
      :close-on-click-modal="false"
    >
      <div class="image-upload-container">
        <div v-if="currentFacility?.imageUrl" class="current-image">
          <img :src="currentFacility.imageUrl" :alt="currentFacility.name" class="preview-image" />
        </div>
        <div v-else class="no-image-placeholder">
          <el-icon><Picture /></el-icon>
          <p>暂无设施图片</p>
        </div>

        <el-upload
          ref="uploadRef"
          class="image-uploader"
          action="#"
          :auto-upload="false"
          :show-file-list="false"
          :before-upload="beforeImageUpload"
          :on-change="handleImageChange"
          accept="image/*"
        >
          <template #trigger>
            <el-button type="primary" class="upload-btn">
              <el-icon><Upload /></el-icon>
              选择图片
            </el-button>
          </template>
        </el-upload>

        <div v-if="selectedImage" class="selected-image-info">
          <p>已选择: {{ selectedImage.name }}</p>
          <p>大小: {{ formatFileSize(selectedImage.size) }}</p>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="imageDialogVisible = false">关闭</el-button>
          <el-button 
            type="danger" 
            @click="handleDeleteImage"
            v-if="currentFacility?.imageUrl"
          >
            删除图片
          </el-button>
          <el-button 
            type="primary" 
            @click="handleUploadImage"
            :disabled="!selectedImage"
            :loading="uploading"
          >
            上传图片
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Search, 
  Plus, 
  Edit, 
  Delete, 
  Picture, 
  Upload,
  CircleCheck, 
  CircleClose, 
  Tools, 
  Timer 
} from '@element-plus/icons-vue';
import { facilityAPI, facilityCategoryAPI, fileAPI } from '../../api';

const loading = ref(false);
const facilityList = ref([]);
const searchKeyword = ref('');
const isSearchMode = ref(false);
const dialogVisible = ref(false);
const dialogTitle = ref('添加设施');
const formRef = ref(null);
const isEdit = ref(false);
const categoryOptions = ref([]);

// 图片上传相关
const imageDialogVisible = ref(false);
const currentFacility = ref(null);
const selectedImage = ref(null);
const uploading = ref(false);
const uploadRef = ref(null);
// 对话框图片上传相关
const dialogUploadRef = ref(null);
const fileInputRef = ref(null);

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
  name: '',
  model: '',
  category: '',
  location: '',
  status: 'AVAILABLE',
  description: '',
  purchaseDate: '',
  price: 0
});

const rules = {
  name: [{ required: true, message: '请输入设施名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入设施类别', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
  loadFacilityList();
  loadCategoryOptions();
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
      const facilities = result.data.content || [];
      facilities.forEach(facility => {
        if (facility.imageUrl && facility.imageUrl.startsWith('blob:')) {
          facility.imageUrl = null;
        }
      });
      facilityList.value = facilities;
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

const loadCategoryOptions = async () => {
  try {
    const res = await facilityCategoryAPI.active();
    categoryOptions.value = res.data;
  } catch (error) {
    console.error('加载设施类别失败:', error);
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
  handleEdit(row);
};

const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '添加设施';
  form.value = {
    id: null,
    name: '',
    model: '',
    category: '',
    location: '',
    status: 'AVAILABLE',
    description: '',
    purchaseDate: '',
    price: 0,
    imageUrl: null,
    _imageFile: null  // 清理临时图片文件引用
  };
  // 清理文件输入框
  if (fileInputRef.value) {
    fileInputRef.value.value = '';
  }
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;
  dialogTitle.value = '编辑设施';
  
  // 清理之前的临时图片URL
  if (form.value.imageUrl && form.value.imageUrl.startsWith('blob:')) {
    URL.revokeObjectURL(form.value.imageUrl);
  }
  
  form.value = { 
    ...row,
    _imageFile: null  // 清理临时图片文件引用
  };
  
  // 如果 imageUrl 是 blob URL（之前编辑时留下的预览），清除它
  if (form.value.imageUrl && form.value.imageUrl.startsWith('blob:')) {
    form.value.imageUrl = null;
  }
  
  // 清理文件输入框
  if (fileInputRef.value) {
    fileInputRef.value.value = '';
  }
  
  dialogVisible.value = true;
};

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除设施 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger',
      dangerouslyUseHTMLString: true,
      message: `
        <div style="margin: 10px 0;">
          <p style="font-weight: bold; color: #f56c6c;">⚠️ 此操作不可恢复！</p>
          <p style="margin-top: 8px;">设施名称：${row.name}</p>
          ${row.model ? `<p>型号：${row.model}</p>` : ''}
          <p>类别：${row.category || '未分类'}</p>
        </div>
      `
    }
  ).then(async () => {
    try {
      await facilityAPI.delete(row.id);
      ElMessage.success('设施删除成功');
      loadFacilityList();
    } catch (error) {
      console.error('删除失败:', error);
      ElMessage.error('删除失败：' + (error.response?.data?.message || '未知错误'));
    }
  }).catch(() => {
    ElMessage.info('已取消删除操作');
  });
};

// 图片上传相关函数
const handleImageUpload = (row) => {
  currentFacility.value = { ...row };
  selectedImage.value = null;
  imageDialogVisible.value = true;
};

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt10M = file.size / 1024 / 1024 < 10;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!');
    return false;
  }
  return true;
};

const handleImageChange = async (uploadFile) => {
  if (!uploadFile || !uploadFile.raw) return;
  
  const file = uploadFile.raw;
  if (!beforeImageUpload(file)) return;
  
  selectedImage.value = file;
  
  // 清理之前的blob URL
  if (currentFacility.value.imageUrl && currentFacility.value.imageUrl.startsWith('blob:')) {
    URL.revokeObjectURL(currentFacility.value.imageUrl);
  }
  
  // 创建新的预览URL
  const tempUrl = URL.createObjectURL(file);
  currentFacility.value.imageUrl = tempUrl;
};

const handleUploadImage = async () => {
  if (!selectedImage.value) {
    ElMessage.warning('请选择图片');
    return;
  }

  uploading.value = true;
  try {
    // 使用正确的文件上传API
    const result = await fileAPI.uploadFacilityImage(currentFacility.value.id, selectedImage.value);
    if (result.code === 200) {
      // 更新设施图片URL
      currentFacility.value.imageUrl = result.data;
      ElMessage.success('图片上传成功');
      imageDialogVisible.value = false;
      loadFacilityList();
    } else {
      ElMessage.error(result.message || '图片上传失败');
    }
  } catch (error) {
    console.error('图片上传失败:', error);
    ElMessage.error('图片上传失败');
  } finally {
    uploading.value = false;
    selectedImage.value = null;
  }
};

const handleDeleteImage = () => {
  ElMessageBox.confirm(
    `确定要删除设施 "${currentFacility.value.name}" 的图片吗？`,
    '删除图片确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger',
      dangerouslyUseHTMLString: true,
      message: `
        <div style="margin: 10px 0;">
          <p style="font-weight: bold; color: #f56c6c;">⚠️ 此操作将永久删除设施图片！</p>
          <p style="margin-top: 8px;">设施名称：${currentFacility.value.name}</p>
          <p>删除后设施将显示默认图片</p>
        </div>
      `
    }
  ).then(async () => {
    try {
      const result = await facilityAPI.deleteImage(currentFacility.value.id);
      if (result.code === 200) {
        // 更新当前设施对象，移除图片URL
        currentFacility.value.imageUrl = null;
        // 同时更新表格中对应行的图片URL
        const idx = facilityList.value.findIndex(f => f.id === currentFacility.value.id);
        if (idx !== -1) facilityList.value[idx].imageUrl = null;
        ElMessage.success('图片删除成功');
        imageDialogVisible.value = false;
      } else {
        ElMessage.error(result.message || '图片删除失败');
      }
    } catch (error) {
      console.error('图片删除失败:', error);
      ElMessage.error('图片删除失败：' + (error.response?.data?.message || '未知错误'));
    }
  }).catch(() => {
    ElMessage.info('已取消删除图片操作');
  });
};

const handleImageError = (event) => {
  if (event && event.target) {
    event.target.style.display = 'none';
    const parent = event.target.parentElement;
    if (parent) {
      const noImageEl = parent.querySelector('.no-image');
      if (noImageEl) {
        noImageEl.style.display = 'flex';
      }
    }
  }
};

const isValidImageUrl = (url) => {
  if (!url) return false;
  if (url.startsWith('blob:')) {
    return false;
  }
  return true;
};

// 图片上传成功处理函数
const handleDialogUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.imageUrl = response.data;
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error(response.message || '图片上传失败');
  }
};

// 图片上传失败处理函数
const handleDialogUploadError = (error) => {
  ElMessage.error('图片上传失败: ' + (error.message || '未知错误'));
};

// 设施图片上传成功处理函数
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    currentFacility.value.imageUrl = response.data;
    ElMessage.success('图片上传成功');
    loadFacilityList();
  } else {
    ElMessage.error(response.message || '图片上传失败');
  }
};

// 设施图片上传失败处理函数
const handleUploadError = (error) => {
  ElMessage.error('图片上传失败: ' + (error.message || '未知错误'));
};

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 对话框图片上传相关方法
const triggerFileSelect = () => {
  fileInputRef.value.click();
};

const handleFileInputChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    handleDialogImageSelect({ raw: file });
  }
};

const beforeDialogImageUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt10M = file.size / 1024 / 1024 < 10;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!');
    return false;
  }
  return true;
};

const handleDialogImageSelect = (uploadFile) => {
  if (!uploadFile || !uploadFile.raw) return;
  
  const file = uploadFile.raw;
  if (!beforeDialogImageUpload(file)) return;
  
  // 只创建预览，不上传
  const tempUrl = URL.createObjectURL(file);
  form.value.imageUrl = tempUrl;
  // 保存文件引用，在提交时上传
  form.value._imageFile = file;
};

const removeImage = () => {
  form.value.imageUrl = null;
  if (fileInputRef.value) {
    fileInputRef.value.value = '';
  }
  ElMessage.success('图片已移除');
};

const handleDialogClose = () => {
  // 清理临时图片URL
  if (form.value.imageUrl && form.value.imageUrl.startsWith('blob:')) {
    URL.revokeObjectURL(form.value.imageUrl);
  }
  // 清理临时文件引用
  if (form.value._imageFile) {
    delete form.value._imageFile;
  }
  // 清空文件输入
  if (fileInputRef.value) {
    fileInputRef.value.value = '';
  }
  
  // 重置表单数据
  if (dialogUploadRef.value) {
    dialogUploadRef.value.clearFiles();
  }
  
  // 重置selectedImage
  selectedImage.value = null;
};;

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    
    let formData = { ...form.value };
    let newFacilityId = null;
    let imageFile = form.value._imageFile; // 保存图片文件引用
    
    // 清理临时数据
    delete formData._imageFile;
    
    if (isEdit.value) {
      // 编辑模式：确保不提交临时的blob URL
      if (formData.imageUrl && formData.imageUrl.startsWith('blob:')) {
        // 如果是临时预览URL，使用原来的URL或null
        const originalFacility = facilityList.value.find(f => f.id === formData.id);
        formData.imageUrl = originalFacility ? originalFacility.imageUrl : null;
      }
      
      console.log('更新设施信息，formData:', formData);
      // 更新设施信息（不包含新图片）
      await facilityAPI.update(formData.id, formData);
      ElMessage.success('更新成功');
      
      // 如果有新选择的图片文件，单独上传
      if (imageFile && imageFile instanceof File) {
        try {
          console.log('开始上传图片，文件类型:', imageFile.type, '文件大小:', imageFile.size);
          const uploadResponse = await facilityAPI.uploadImage(formData.id, imageFile);
          if (uploadResponse.code === 200) {
            // 更新设施图片信息 - 从返回的设施对象中提取imageUrl
            const updatedFacility = uploadResponse.data;
            if (updatedFacility && updatedFacility.imageUrl) {
              await facilityAPI.update(formData.id, { imageUrl: updatedFacility.imageUrl });
              ElMessage.success('图片上传成功');
            } else {
              console.error('上传图片后返回的数据格式错误:', updatedFacility);
              ElMessage.warning('设施更新成功，但图片URL提取失败');
            }
          } else {
            ElMessage.warning('设施更新成功，但图片上传失败：' + (uploadResponse.message || '未知错误'));
          }
        } catch (uploadError) {
          console.error('图片上传失败详情:', uploadError);
          console.error('上传失败响应:', uploadError.response);
          console.error('上传失败数据:', uploadError.response?.data);
          ElMessage.warning('设施更新成功，但图片上传失败：' + (uploadError.message || uploadError.response?.data?.message || '网络错误'));
        }
      } else if (imageFile) {
        console.warn('imageFile不是有效的File对象:', imageFile);
      }
    } else {
      // 新增模式：确保不提交临时的blob URL
      if (formData.imageUrl && formData.imageUrl.startsWith('blob:')) {
        console.log('检测到新增模式下的blob URL，设置为null');
        formData.imageUrl = null;
      }
      
      console.log('新增设施，formData:', formData);
      // 先创建设施
      const createResponse = await facilityAPI.create(formData);
      newFacilityId = createResponse.data.id;
      ElMessage.success('添加成功');
      
      // 如果有新选择的图片文件，上传图片
      if (imageFile && imageFile instanceof File) {
        try {
          console.log('开始上传图片，文件类型:', imageFile.type, '文件大小:', imageFile.size);
          const uploadResponse = await facilityAPI.uploadImage(newFacilityId, imageFile);
          if (uploadResponse.code === 200) {
            // 更新设施图片信息 - 从返回的设施对象中提取imageUrl
            const updatedFacility = uploadResponse.data;
            if (updatedFacility && updatedFacility.imageUrl) {
              await facilityAPI.update(newFacilityId, { imageUrl: updatedFacility.imageUrl });
              ElMessage.success('设施创建成功，图片上传成功');
            } else {
              console.error('上传图片后返回的数据格式错误:', updatedFacility);
              ElMessage.warning('设施创建成功，但图片URL提取失败');
            }
          } else {
            ElMessage.warning('设施创建成功，但图片上传失败：' + (uploadResponse.message || '未知错误'));
          }
        } catch (uploadError) {
          console.error('图片上传失败详情:', uploadError);
          console.error('上传失败响应:', uploadError.response);
          console.error('上传失败数据:', uploadError.response?.data);
          ElMessage.warning('设施创建成功，但图片上传失败：' + (uploadError.message || uploadError.response?.data?.message || '网络错误'));
        }
      } else if (imageFile) {
        console.warn('imageFile不是有效的File对象:', imageFile);
      }
    }
    
    // 关闭对话框并刷新列表
    dialogVisible.value = false;
    loadFacilityList();
  } catch (error) {
    console.error('提交失败:', error);
    if (error.response?.data?.message) {
      ElMessage.error('提交失败: ' + error.response.data.message);
    } else {
      ElMessage.error('提交失败，请稍后重试');
    }
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
.facility-page {
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
.facility-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.facility-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8fafc 0%, #e6f7ff 100%);
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.facility-dialog :deep(.el-dialog__title) {
  color: #1a202c;
  font-weight: 600;
  font-size: 18px;
}

.facility-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.facility-form :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
}

.facility-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.facility-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
}

.facility-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.facility-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.facility-form :deep(.el-textarea__inner:hover) {
  border-color: #cbd5e0;
}

.facility-form :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.facility-form :deep(.el-select .el-input__wrapper) {
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

/* 设施图片样式 */
.facility-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e2e8f0;
}

/* 对话框内图片上传样式 */
.image-upload-section {
  width: 100%;
}

.current-image-preview {
  text-align: center;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px dashed #e2e8f0;
}

.facility-preview-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

.image-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.no-image-upload {
  text-align: center;
}

.dialog-image-uploader {
  width: 100%;
}

.dialog-image-uploader :deep(.el-upload-dragger) {
  width: 100%;
  height: 180px;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
  transition: all 0.3s ease;
}

.dialog-image-uploader :deep(.el-upload-dragger:hover) {
  border-color: #409eff;
  background: #f0f9ff;
}

.dialog-image-uploader :deep(.el-upload-dragger.is-dragover) {
  border-color: #409eff;
  background: #e6f7ff;
}

.upload-icon {
  font-size: 48px;
  color: #a0aec0;
  margin-bottom: 12px;
}

.upload-text {
  color: #4a5568;
}

.upload-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 14px;
  color: #718096;
}

.facility-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #a0aec0;
  font-size: 12px;
}

.no-image .el-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

/* 图片上传对话框样式 */
.image-dialog :deep(.el-dialog) {
  border-radius: 16px;
}

.image-upload-container {
  text-align: center;
}

.current-image {
  margin-bottom: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.no-image-placeholder {
  width: 100%;
  height: 200px;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #a0aec0;
  margin-bottom: 20px;
}

.no-image-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.image-uploader {
  margin-bottom: 16px;
}

.upload-btn {
  border-radius: 8px;
  font-weight: 500;
}

.selected-image-info {
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #4a5568;
}

.selected-image-info p {
  margin: 4px 0;
}

/* 操作按钮样式调整 */
.image-btn {
  border-color: #f59e0b;
  color: #f59e0b;
}

.image-btn:hover {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-color: transparent;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
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
