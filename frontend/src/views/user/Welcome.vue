<template>
  <div class="welcome-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-icon">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M12 2L13.09 8.26L19 7L15.45 11.82L18 18L12 14.74L6 18L8.55 11.82L5 7L10.91 8.26L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="welcome-title">欢迎回来！</h1>
        <p class="welcome-subtitle">愿你的每一天都充满阳光和希望</p>
      </div>
      <div class="banner-decoration"></div>
    </div>



    <!-- 今日心情 -->
    <div class="mood-weather">
      <div class="mood-card">
        <div class="mood-header">
          <div class="mood-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="5" stroke="currentColor" stroke-width="2"/>
              <line x1="12" y1="1" x2="12" y2="3" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="12" y1="21" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="1" y1="12" x2="3" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="21" y1="12" x2="23" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3 class="mood-title">今日心情</h3>
        </div>
        <div class="mood-content">
          <div class="weather-display">
            <img :src="weatherInfo.weatherIcon" :alt="weatherInfo.weatherType" class="weather-icon" />
            <div class="weather-desc">{{ weatherInfo.weatherType }} {{ weatherInfo.temperature }}</div>
            <div class="weather-city">{{ weatherInfo.city }}</div>
            <div class="weather-time">{{ weatherInfo.updateTime }}</div>
          </div>
          <p class="mood-message">{{ weatherInfo.moodQuote }}</p>
          <div class="refresh-btn" @click="fetchWeather" :disabled="loading">
            <el-icon v-if="loading"><Loading /></el-icon>
            <span>{{ loading ? '更新中...' : '刷新天气' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷功能入口 -->
    <div class="quick-actions">
      <h3 class="section-title">快捷功能</h3>
      <div class="action-grid">
        <!-- 设施浏览 -->
        <div class="action-card" @click="$router.push('/user/facility')">
          <div class="action-icon info">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="action-content">
            <h4 class="action-title">设施浏览</h4>
            <p class="action-desc">浏览和预约校园设施</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 我的预约 -->
        <div class="action-card" @click="$router.push('/user/my-reservation')">
          <div class="action-icon success">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="action-content">
            <h4 class="action-title">我的预约</h4>
            <p class="action-desc">查看和管理您的设施预约记录</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 违规记录查看 -->
        <div class="action-card" @click="$router.push('/user/violation-records')">
          <div class="action-icon warning">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 9V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 17H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="action-content">
            <h4 class="action-title">违规记录</h4>
            <p class="action-desc">查看您的历史违规记录和扣分情况</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 反馈建议提交 -->
        <div class="action-card" @click="$router.push('/user/feedback')">
          <div class="action-icon primary">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="action-content">
            <h4 class="action-title">意见反馈</h4>
            <p class="action-desc">提交建议、投诉或咨询，查看管理员回复</p>
          </div>
          <div class="action-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐设施 -->
    <div class="recommend-section" v-if="currentUser && currentUser.id">
      <h3 class="section-title">个性化推荐</h3>
      <RecommendWidget :userId="currentUser.id" />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ArrowRight, Loading } from '@element-plus/icons-vue';
import { weatherAPI } from '../../api/weather';
import RecommendWidget from '../../components/RecommendWidget.vue';

const router = useRouter();

const currentUser = ref(null);

const initUserInfo = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  currentUser.value = userInfo;
};

const weatherInfo = ref({
  weatherType: '晴',
  temperature: '25℃',
  weatherIcon: '☀️',
  moodQuote: '愿你拥有如阳光般明媚的心情，温暖而充满力量。无论遇到什么，都要保持微笑和善良。',
  city: '北京',
  updateTime: ''
});

const loading = ref(false);

const fetchWeather = async () => {
  try {
    loading.value = true;
    // 优先使用自动定位接口
    const response = await weatherAPI.getAutoWeather();
    if (response.data && response.data.data) {
      weatherInfo.value = response.data.data;
    }
  } catch (error) {
    console.error('自动定位获取天气信息失败，尝试使用默认城市:', error);
    // 如果自动定位失败，回退到默认城市
    try {
      const response = await weatherAPI.getWeather('北京');
      if (response.data && response.data.data) {
        weatherInfo.value = response.data.data;
      }
    } catch (fallbackError) {
      console.error('获取默认城市天气信息失败:', fallbackError);
      // 如果都失败，使用默认的晴天信息
      weatherInfo.value = {
        weatherType: '晴',
        temperature: '25℃',
        weatherIcon: '☀️',
        moodQuote: '网络连接失败，但愿你依然拥有阳光般的心情！',
        city: '北京',
        updateTime: new Date().toLocaleString()
      };
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  initUserInfo();
  fetchWeather();
});
</script>

<style scoped>
/* 页面背景 */
.welcome-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 25%, #e6f7ff 50%, #f8fafc 100%);
  padding: 24px;
  position: relative;
}

/* 欢迎横幅 */
.welcome-banner {
  position: relative;
  background: linear-gradient(135deg, #409eff 0%, #1976d2 100%);
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 24px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.3);
}

.banner-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #ffffff;
}

.welcome-icon {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.welcome-icon svg {
  width: 30px;
  height: 30px;
  color: #ffffff;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.welcome-subtitle {
  font-size: 16px;
  font-weight: 400;
  margin: 0;
  opacity: 0.9;
  letter-spacing: 0.5px;
}

.banner-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  animation: float 6s ease-in-out infinite;
}

/* 快捷功能区域 */
.quick-actions {
  margin-bottom: 30px;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #3498db;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.action-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid #eaeaea;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 15px;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: #3498db;
}

.action-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-icon.warning {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
}

.action-icon.primary {
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: white;
}

.action-icon.success {
  background: linear-gradient(135deg, #1dd1a1 0%, #10ac84 100%);
  color: white;
}

.action-icon.info {
  background: linear-gradient(135deg, #54a0ff 0%, #2e86de 100%);
  color: white;
}

.action-icon svg {
  width: 24px;
  height: 24px;
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 5px 0;
}

.action-desc {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin: 0;
  line-height: 1.4;
}

.action-arrow {
  color: #bdc3c7;
  transition: color 0.3s ease;
}

.action-card:hover .action-arrow {
  color: #3498db;
}

/* 心情天气 */
.mood-weather {
  margin-bottom: 0;
}

.mood-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.mood-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(64, 158, 255, 0.12);
  border-color: #bae7ff;
}

.mood-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.mood-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.mood-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.mood-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.mood-content {
  display: flex;
  align-items: center;
  gap: 24px;
}

.weather-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.weather-icon {
  width: 48px;
  height: 48px;
  object-fit: contain;
}

.weather-desc {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

.weather-city {
  font-size: 12px;
  color: #718096;
  margin-top: 4px;
}

.weather-time {
  font-size: 10px;
  color: #a0aec0;
  margin-top: 2px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 12px;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 12px;
}

.refresh-btn:hover:not(:disabled) {
  background: #edf2f7;
  border-color: #cbd5e0;
}

.refresh-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.mood-message {
  font-size: 16px;
  line-height: 1.6;
  color: #2d3748;
  margin: 0;
  flex: 1;
}

/* 动画效果 */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-page {
    padding: 16px;
  }

  .welcome-banner {
    padding: 20px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.inspiration-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.inspiration-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.daily-quote::before {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.motivation::before {
  background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
}

.reminder::before {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
}

.blessing::before {
  background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
}

.inspiration-card:hover::before {
  opacity: 1;
}

.inspiration-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(64, 158, 255, 0.15);
  border-color: #bae7ff;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.quote-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.motivation-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.reminder-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.blessing-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-icon svg {
  width: 20px;
  height: 20px;
  color: #ffffff;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.card-content {
  color: #4a5568;
}

.quote-text {
  font-size: 16px;
  line-height: 1.6;
  font-style: italic;
  margin: 0 0 12px 0;
}

.quote-author {
  text-align: right;
  font-weight: 600;
  color: #2d3748;
}

.motivation-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.motivation-list li {
  padding: 8px 0;
  position: relative;
  padding-left: 24px;
  line-height: 1.5;
}

.motivation-list li::before {
  content: '✨';
  position: absolute;
  left: 0;
  top: 8px;
}

.reminder-text,
.blessing-text {
  font-size: 15px;
  line-height: 1.6;
  margin: 0;
}

/* 心情天气 */
.mood-weather {
  margin-bottom: 0;
}

.mood-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.mood-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(64, 158, 255, 0.12);
  border-color: #bae7ff;
}

.mood-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.mood-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.mood-icon svg {
  width: 24px;
  height: 24px;
  color: #409eff;
}

.mood-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.mood-content {
  display: flex;
  align-items: center;
  gap: 24px;
}

.weather-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.weather-emoji {
  font-size: 48px;
}

.weather-desc {
  font-size: 14px;
  color: #4a5568;
  font-weight: 500;
}

.mood-message {
  font-size: 16px;
  line-height: 1.6;
  color: #2d3748;
  margin: 0;
  flex: 1;
}

/* 动画效果 */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-page {
    padding: 16px;
  }

  .welcome-banner {
    padding: 20px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .card-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .inspiration-card {
    padding: 20px;
  }

  .mood-content {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
}

</style>