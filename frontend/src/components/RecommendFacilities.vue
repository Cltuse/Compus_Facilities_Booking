<template>
  <div class="recommend-facilities">
    <!-- 推荐标题 -->
    <div class="recommend-header">
      <div class="recommend-icon">
        <svg viewBox="0 0 24 24" fill="none">
          <path d="M12 2L15.09 8.26L22 9L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9L8.91 8.26L12 2Z" 
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <h3 class="recommend-title">为您推荐</h3>
      <el-button 
        type="primary" 
        link 
        @click="refreshRecommendations" 
        :loading="refreshing"
        class="refresh-btn"
      >
        <el-icon><Refresh /></el-icon>
        刷新推荐
      </el-button>
    </div>

    <!-- 推荐内容 -->
    <div class="recommend-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 推荐列表 -->
      <div v-else-if="recommendations.length > 0" class="recommend-list">
        <div 
          v-for="(item, index) in recommendations" 
          :key="index"
          class="recommend-item"
          @click="viewFacilityDetail(item.facility)"
        >
          <div class="facility-image">
            <img 
              :src="getFacilityImage(item.facility)" 
              :alt="item.facility.name"
              @error="handleImageError"
            />
            <div class="score-badge">
              <el-rate 
                v-model="item.score" 
                disabled 
                :max="5" 
                :score-template="item.score.toFixed(1)"
                show-score
                text-color="#ff9900"
                size="small"
              />
            </div>
          </div>
          
          <div class="facility-info">
            <h4 class="facility-name">{{ item.facility.name }}</h4>
            <p class="facility-location">{{ item.facility.location }}</p>
            <div class="recommend-reason">
              <el-icon><InfoFilled /></el-icon>
              <span>{{ item.reason }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 2L15.09 8.26L22 9L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9L8.91 8.26L12 2Z" 
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <p class="empty-text">暂无推荐设施</p>
        <p class="empty-subtext">多预约一些设施，我们会为您提供更精准的推荐</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, InfoFilled } from '@element-plus/icons-vue'
import { getUserRecommendations, regenerateRecommendations } from '../api/recommend'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const loading = ref(false)
const refreshing = ref(false)
const recommendations = ref([])

// 获取推荐设施
const fetchRecommendations = async () => {
  loading.value = true
  try {
    const response = await getUserRecommendations(props.userId)
    if (response.code === 200) {
      recommendations.value = response.data
    } else {
      ElMessage.error('获取推荐失败')
    }
  } catch (error) {
    console.error('获取推荐失败:', error)
    ElMessage.error('获取推荐失败')
  } finally {
    loading.value = false
  }
}

// 刷新推荐
const refreshRecommendations = async () => {
  refreshing.value = true
  try {
    const response = await regenerateRecommendations(props.userId)
    if (response.code === 200) {
      ElMessage.success('推荐刷新成功')
      await fetchRecommendations()
    } else {
      ElMessage.error('刷新推荐失败')
    }
  } catch (error) {
    console.error('刷新推荐失败:', error)
    ElMessage.error('刷新推荐失败')
  } finally {
    refreshing.value = false
  }
}

// 获取设施图片
const getFacilityImage = (facility) => {
  if (facility.imageUrl) {
    return facility.imageUrl
  }
  // 默认图片
  return '/images/default-facility.jpg'
}

// 图片加载失败处理
const handleImageError = (event) => {
  event.target.src = '/images/default-facility.jpg'
}

// 查看设施详情
const viewFacilityDetail = (facility) => {
  // 这里可以跳转到设施详情页面
  console.log('查看设施详情:', facility)
  // 实际项目中可以使用路由跳转
  // router.push(`/facility/${facility.id}`)
}

onMounted(() => {
  fetchRecommendations()
})
</script>

<style scoped>
.recommend-facilities {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.recommend-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  justify-content: space-between;
}

.recommend-icon {
  width: 32px;
  height: 32px;
  color: #409eff;
  margin-right: 12px;
}

.recommend-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  flex: 1;
}

.refresh-btn {
  color: #409eff;
}

.loading-container {
  padding: 20px 0;
}

.recommend-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.recommend-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.recommend-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.facility-image {
  position: relative;
  margin-bottom: 12px;
}

.facility-image img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
}

.score-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 4px;
  backdrop-filter: blur(4px);
}

.facility-info {
  color: #606266;
}

.facility-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.facility-location {
  font-size: 14px;
  margin: 4px 0;
  color: #909399;
}

.recommend-reason {
  display: flex;
  align-items: center;
  margin-top: 8px;
  padding: 8px;
  background: #f0f9ff;
  border-radius: 4px;
  font-size: 12px;
  color: #409eff;
}

.recommend-reason .el-icon {
  margin-right: 4px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: #c0c4cc;
  margin: 0 auto 16px;
}

.empty-text {
  font-size: 16px;
  margin-bottom: 8px;
}

.empty-subtext {
  font-size: 14px;
  color: #c0c4cc;
}

@media (max-width: 768px) {
  .recommend-list {
    grid-template-columns: 1fr;
  }
  
  .recommend-facilities {
    padding: 16px;
  }
}
</style>
