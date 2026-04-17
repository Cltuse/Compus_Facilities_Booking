<template>
  <div class="recommend-widget">
    <!-- 推荐标题 -->
    <div class="widget-header">
      <div class="header-left">
        <el-icon class="recommend-icon"><Star /></el-icon>
        <h3 class="widget-title">个性化推荐</h3>
        <el-tag type="success" size="small" v-if="recommendType">基于{{ recommendType }}</el-tag>
      </div>
      <div class="header-right">
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
    </div>

    <!-- 推荐内容 -->
    <div class="widget-content">
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
              <span class="score">{{ item.score.toFixed(1) }}</span>
            </div>
          </div>

          <div class="facility-info">
            <h4 class="facility-name">{{ item.facility.name }}</h4>
            <p class="facility-location">{{ item.facility.location }}</p>
            <div class="recommend-reason">
              <el-icon><InfoFilled /></el-icon>
              <span>{{ getRecommendReason(item.reason) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">
          <el-icon><Star /></el-icon>
        </div>
        <p class="empty-text">暂无推荐设施</p>
        <p class="empty-subtext">多预约一些设施，我们会为您提供更精准的推荐</p>
        <el-button type="primary" @click="$router.push('/user/facility')">
          去浏览设施
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Refresh, InfoFilled } from '@element-plus/icons-vue'
import { getUserRecommendations, regenerateRecommendations } from '../api/recommend'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const router = useRouter()
const loading = ref(false)
const refreshing = ref(false)
const recommendations = ref([])

// 计算推荐类型
const recommendType = computed(() => {
  if (recommendations.value.length === 0) return ''
  const firstItem = recommendations.value[0]
  if (firstItem.reason?.includes('相似用户')) return '协同过滤'
  if (firstItem.reason?.includes('热门')) return '热门推荐'
  if (firstItem.reason?.includes('类别')) return '类别偏好'
  return '个性化推荐'
})

// 获取推荐设施
const fetchRecommendations = async () => {
  loading.value = true
  try {
    const response = await getUserRecommendations(props.userId)
    if (response.code === 200) {
      recommendations.value = response.data || []
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

// 获取推荐原因描述
const getRecommendReason = (reason) => {
  if (!reason) return '根据您的使用习惯推荐'
  return reason
}

// 查看设施详情
const viewFacilityDetail = (facility) => {
  router.push(`/user/facility/${facility.id}`)
}

onMounted(() => {
  fetchRecommendations()
})
</script>

<style scoped>
.recommend-widget {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.widget-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.recommend-icon {
  color: #ff9900;
  font-size: 20px;
}

.widget-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.refresh-btn {
  color: #409eff;
}

.loading-container {
  padding: 20px 0;
}

.recommend-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
}

.recommend-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
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
  margin-bottom: 8px;
}

.facility-image img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 6px;
}

.score-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  background: rgba(255, 255, 255, 0.9);
  padding: 2px 6px;
  border-radius: 4px;
  backdrop-filter: blur(4px);
}

.score-badge .score {
  font-size: 12px;
  font-weight: 600;
  color: #ff9900;
}

.facility-info {
  color: #606266;
}

.facility-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
  line-height: 1.2;
}

.facility-location {
  font-size: 12px;
  margin: 2px 0;
  color: #909399;
  line-height: 1.2;
}

.recommend-reason {
  display: flex;
  align-items: center;
  margin-top: 6px;
  padding: 4px 6px;
  background: #f0f9ff;
  border-radius: 4px;
  font-size: 11px;
  color: #409eff;
  line-height: 1.2;
}

.recommend-reason .el-icon {
  margin-right: 2px;
  font-size: 10px;
}

.empty-state {
  text-align: center;
  padding: 30px 20px;
  color: #909399;
}

.empty-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 14px;
  margin-bottom: 6px;
}

.empty-subtext {
  font-size: 12px;
  margin-bottom: 12px;
  color: #c0c4cc;
}
</style>
