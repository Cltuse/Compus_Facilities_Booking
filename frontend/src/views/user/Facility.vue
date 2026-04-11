<template>
  <div class="facility-page">
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

    <!-- 快捷功能和搜索区域 -->
    <div class="quick-search-section">
      <!-- 搜索和筛选区域 -->
        <div class="search-filter-container">
          <div class="search-row">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索设施名称、型号、位置或描述..."
              class="search-input enhanced"
              clearable
              @input="handleSearchInput"
              @clear="handleSearchClear"
            >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
            <template #suffix>
              <el-icon 
                v-if="searchKeyword" 
                class="clear-icon" 
                @click="clearSearch"
              ><CircleClose /></el-icon>
            </template>
          </el-input>
          <el-button 
            type="primary" 
            class="search-btn"
            @click="handleSearch"
          >
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <!-- 分类筛选 -->
        <div class="filter-row">
          <div class="filter-group">
            <span class="filter-label">设施类别：</span>
            <el-select 
              v-model="selectedCategory" 
              placeholder="全部类别"
              class="category-select"
              clearable
              @change="handleCategoryChange"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.categoryName"
                :value="category.categoryName"
              >
                <div class="category-option">
                  <span>{{ category.categoryName }}</span>
                  <span class="category-count">({{ getCategoryCount(category.categoryName) }})</span>
                </div>
              </el-option>
            </el-select>
          </div>

          <div class="filter-group">
            <span class="filter-label">设施状态：</span>
            <el-radio-group 
              v-model="selectedStatus" 
              size="small"
              @change="handleStatusChange"
            >
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button label="AVAILABLE">可用</el-radio-button>
              <el-radio-button label="MAINTENANCE">维护中</el-radio-button>
            </el-radio-group>
          </div>

          <div class="filter-group">
            <span class="filter-label">排序方式：</span>
            <el-select 
              v-model="sortBy" 
              placeholder="默认排序"
              class="sort-select"
              @change="handleSortChange"
            >
              <el-option label="默认排序" value="" />
              <el-option label="名称升序" value="nameAsc" />
              <el-option label="名称降序" value="nameDesc" />
              <el-option label="状态排序" value="status" />
            </el-select>
          </div>
        </div>

        <!-- 搜索结果统计 -->
        <div class="search-stats" v-if="searchKeyword || selectedCategory || selectedStatus">
          <span class="stats-text">
            找到 <strong>{{ filteredFacilityList.length }}</strong> 个设施
            <template v-if="searchKeyword">
              ，关键词: "<strong>{{ searchKeyword }}</strong>"
            </template>
            <template v-if="selectedCategory">
              ，类别: "<strong>{{ selectedCategory }}</strong>"
            </template>
            <template v-if="selectedStatus">
              ，状态: "<strong>{{ getStatusText(selectedStatus) }}</strong>"
            </template>
          </span>
          <el-button 
            type="text" 
            class="clear-filters"
            @click="clearAllFilters"
          >
            清除筛选
          </el-button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner">
        <el-icon class="is-loading">
          <Loading />
        </el-icon>
      </div>
      <p class="loading-text">正在加载设施信息...</p>
    </div>

    <!-- 设施卡片网格 -->
    <div v-else class="facility-grid">
      <div
        class="facility-card"
        v-for="item in paginatedFacilityList"
        :key="item.id"
        @click="handleCardClick(item)"
      >
        <div class="card-image">
          <img 
            v-if="item.imageUrl" 
            :src="item.imageUrl" 
            :alt="item.name"
            class="facility-image"
            @error="handleImageError"
          />
          <div v-else class="no-image">
            <el-icon><Picture /></el-icon>
          </div>
        </div>
        
        <div class="card-header">
          <div class="facility-status">
            <el-tag
              :type="getStatusType(item.status)"
              class="status-tag"
              effect="light"
              size="small"
            >
              <el-icon>
                <CircleCheck v-if="item.status === 'AVAILABLE'" />
                <Tools v-else-if="item.status === 'MAINTENANCE'" />
                <CircleClose v-else />
              </el-icon>
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
        </div>

        <div class="card-body">
          <h3 class="facility-name">{{ item.name }}</h3>

          <div class="facility-details">
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

          <div class="facility-description" v-if="item.description">
            {{ item.description }}
          </div>
        </div>

        <div class="card-actions">
          <el-button
            class="detail-btn"
            @click.stop="handleViewDetail(item)"
          >
            <el-icon><InfoFilled /></el-icon>
            查看详情
          </el-button>
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
        <div class="facility-info">
          <div class="info-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="info-details">
            <div class="info-name">{{ currentFacility.name }}</div>
            <div class="info-model">{{ currentFacility.model || '未知型号' }}</div>
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

    <!-- 空状态 -->
    <div class="empty-state" v-if="!loading && filteredFacilityList.length === 0">
      <h3 class="empty-title">暂无设施</h3>
      <p class="empty-description">当前没有任何设施信息</p>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container" v-if="filteredFacilityList.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="filteredFacilityList.length"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Search, Calendar, Check, CircleCheck, Timer, Tools, CircleClose, Picture, Loading, Warning, ChatDotRound, InfoFilled } from '@element-plus/icons-vue';
import { facilityAPI, reservationAPI, facilityCategoryAPI } from '../../api';

const router = useRouter();

const facilityList = ref([]);
const allFacilityList = ref([]); // 存储所有设施数据
const searchKeyword = ref('');
const dialogVisible = ref(false);
const formRef = ref(null);
const currentFacility = ref({});
const userInfo = ref({});
const loading = ref(true); // 加载状态
const categories = ref([]); // 设施类别列表
const selectedCategory = ref(''); // 选中的类别
const selectedStatus = ref(''); // 选中的状态
const sortBy = ref(''); // 排序方式
const activeCollapse = ref([]); // 折叠面板状态

// 分页相关
const currentPage = ref(1);
const pageSize = ref(12);

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
  facilityId: null,
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
  loadFacilityList();
  loadCategories();
});

const loadFacilityList = async () => {
  try {
    loading.value = true;
    const res = await facilityAPI.list();
    allFacilityList.value = res.data;
    facilityList.value = res.data;
  } catch (error) {
    console.error('加载设施列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 过滤后的设施列表（搜索+筛选）
const filteredFacilityList = computed(() => {
  let filtered = allFacilityList.value;

  // 搜索关键词过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase().trim();
    filtered = filtered.filter(item => {
      return (
        (item.name && item.name.toLowerCase().includes(keyword)) ||
        (item.model && item.model.toLowerCase().includes(keyword)) ||
        (item.category && item.category.toLowerCase().includes(keyword)) ||
        (item.location && item.location.toLowerCase().includes(keyword)) ||
        (item.description && item.description.toLowerCase().includes(keyword))
      );
    });
  }

  // 类别过滤
  if (selectedCategory.value) {
    filtered = filtered.filter(item => 
      item.category === selectedCategory.value
    );
  }

  // 状态过滤
  if (selectedStatus.value) {
    filtered = filtered.filter(item => 
      item.status === selectedStatus.value
    );
  }

  // 排序
  if (sortBy.value) {
    filtered = [...filtered].sort((a, b) => {
      switch (sortBy.value) {
        case 'nameAsc':
          return a.name.localeCompare(b.name, 'zh-CN');
        case 'nameDesc':
          return b.name.localeCompare(a.name, 'zh-CN');
        case 'status':
          const statusOrder = { 'AVAILABLE': 1, 'MAINTENANCE': 2, 'DAMAGED': 3 };
          return (statusOrder[a.status] || 5) - (statusOrder[b.status] || 5);
        default:
          return 0;
      }
    });
  }

  return filtered;
});

// 分页后的设施列表
const paginatedFacilityList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredFacilityList.value.slice(start, end);
});

// 加载设施类别
const loadCategories = async () => {
  try {
    const res = await facilityCategoryAPI.active();
    categories.value = res.data;
  } catch (error) {
    console.error('加载设施类别失败:', error);
  }
};

// 获取类别设施数量
const getCategoryCount = (categoryName) => {
  return allFacilityList.value.filter(item => item.category === categoryName).length;
};

// 处理搜索输入
const handleSearchInput = () => {
  currentPage.value = 1;
  facilityList.value = filteredFacilityList.value;
};

// 处理搜索按钮点击
const handleSearch = () => {
  currentPage.value = 1;
  facilityList.value = filteredFacilityList.value;
};

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = '';
  handleSearchInput();
};

// 处理搜索清除
const handleSearchClear = () => {
  clearSearch();
};

// 处理类别变化
const handleCategoryChange = () => {
  currentPage.value = 1;
  facilityList.value = filteredFacilityList.value;
};

// 处理状态变化
const handleStatusChange = () => {
  currentPage.value = 1;
  facilityList.value = filteredFacilityList.value;
};

// 处理排序变化
const handleSortChange = () => {
  currentPage.value = 1;
  facilityList.value = filteredFacilityList.value;
};

// 清除所有筛选
const clearAllFilters = () => {
  searchKeyword.value = '';
  selectedCategory.value = '';
  selectedStatus.value = '';
  sortBy.value = '';
  currentPage.value = 1;
  facilityList.value = filteredFacilityList.value;
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleReserve = (item) => {
  currentFacility.value = item;
  form.value = {
    facilityId: item.id,
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
    
    const response = await reservationAPI.create(form.value);
    ElMessage.success(response.message || '预约成功');
    
    dialogVisible.value = false;
  } catch (error) {
    console.error('预约失败:', error);
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

const handleImageError = (event) => {
  event.target.style.display = 'none';
  event.target.parentElement.querySelector('.no-image').style.display = 'flex';
};

// 卡片点击处理
const handleCardClick = (item) => {
  if (item.status === 'AVAILABLE') {
    handleReserve(item);
  }
};

const handleViewDetail = (item) => {
  router.push(`/user/facility/${item.id}`);
};
</script>

<style scoped>
/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  min-height: 300px;
}

.loading-spinner {
  margin-bottom: 16px;
}

.loading-spinner .el-icon {
  font-size: 48px;
  color: #409eff;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 页面背景 */
.facility-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  padding: 24px;
  position: relative;
}

/* 设施卡片图片样式 */
.card-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  position: relative;
  border-radius: 12px 12px 0 0;
}

.facility-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.facility-card:hover .facility-image {
  transform: scale(1.05);
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #a0aec0;
  font-size: 48px;
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

/* 快捷功能和搜索区域 */
.quick-search-section {
  margin-bottom: 32px;
}

.quick-actions {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  margin-bottom: 24px;
}

.section-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #3498db;
}

.action-buttons {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.quick-btn {
  border-radius: 10px;
  padding: 12px 20px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 8px;
}

.quick-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 搜索和筛选区域 */
.search-filter-container {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

.search-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input.enhanced {
  flex: 1;
  max-width: none;
}

.search-input.enhanced :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  background: #ffffff;
  padding: 12px 16px;
  height: 48px;
}

.search-input.enhanced :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-input.enhanced :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.search-btn {
  border-radius: 10px;
  padding: 12px 24px;
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.3);
  display: flex;
  align-items: center;
  gap: 8px;
}

.clear-icon {
  cursor: pointer;
  color: #a0aec0;
  transition: color 0.3s ease;
}

.clear-icon:hover {
  color: #e53e3e;
}

.filter-row {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
  white-space: nowrap;
}

.category-select,
.sort-select {
  min-width: 160px;
}

.category-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.category-count {
  color: #a0aec0;
  font-size: 12px;
  margin-left: 8px;
}

.search-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.stats-text {
  font-size: 14px;
  color: #4a5568;
}

.stats-text strong {
  color: #2d3748;
  font-weight: 700;
}

.clear-filters {
  color: #e53e3e;
  font-weight: 600;
}

.clear-filters:hover {
  color: #c53030;
}

.search-icon {
  color: #718096;
  font-size: 16px;
}

/* 设施网格 */
.facility-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

/* 设施卡片 */
.facility-card {
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

.facility-card::before {
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

.facility-card:hover::before {
  opacity: 1;
  animation: gradient-shimmer 3s ease-in-out infinite;
}

.facility-card:hover {
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

.facility-icon {
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

.facility-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.facility-status .status-tag {
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

.facility-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.facility-details {
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

.facility-description {
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

.detail-btn {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 1px solid #dee2e6;
  border-radius: 10px;
  padding: 12px 20px;
  font-weight: 600;
  color: #495057;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-right: 12px;
}

.detail-btn:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  border-color: #adb5bd;
  color: #343a40;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.detail-btn .el-icon {
  margin-right: 4px;
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

.facility-info {
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
  .facility-page {
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

  .quick-search-section {
    margin-bottom: 24px;
  }

  .quick-actions {
    padding: 20px;
  }

  .action-buttons {
    justify-content: center;
  }

  .quick-btn {
    flex: 1;
    min-width: 120px;
  }

  .search-filter-container {
    padding: 20px;
  }

  .search-row {
    flex-direction: column;
    gap: 12px;
  }

  .search-input.enhanced {
    width: 100%;
  }

  .search-btn {
    width: 100%;
    justify-content: center;
  }

  .filter-row {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .filter-group {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .filter-label {
    text-align: left;
    width: 100%;
  }

  .category-select,
  .sort-select {
    width: 100%;
  }

  .search-stats {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .facility-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .facility-card {
    padding: 20px;
  }

  .facility-name {
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

  .facility-info {
    flex-direction: column;
    text-align: center;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .el-pagination {
    justify-content: center;
  }
}

@media (max-width: 1024px) and (min-width: 769px) {
  .facility-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>