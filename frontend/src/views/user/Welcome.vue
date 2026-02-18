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

    <!-- 心灵鸡汤卡片 -->
    <div class="inspiration-cards">
      <div class="card-grid">
        <!-- 每日箴言 -->
        <div class="inspiration-card daily-quote">
          <div class="card-header">
            <div class="card-icon quote-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M3 12C3 13.1046 3.89543 14 5 14C6.10457 14 7 13.1046 7 12C7 10.8954 6.10457 10 5 10C3.89543 10 3 10.8954 3 12Z" stroke="currentColor" stroke-width="2"/>
                <path d="M17 12C17 13.1046 17.8954 14 19 14C20.1046 14 21 13.1046 21 12C21 10.8954 20.1046 10 19 10C17.8954 10 17 10.8954 17 12Z" stroke="currentColor" stroke-width="2"/>
                <path d="M5 12H7C7 12 10 12 12 14C14 16 17 16 17 16H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </div>
            <h3 class="card-title">每日箴言</h3>
          </div>
          <div class="card-content">
            <p class="quote-text">"成功的路上并不拥挤，因为坚持的人不多。每一个不曾起舞的日子，都是对生命的辜负。"</p>
            <div class="quote-author">— 尼采</div>
          </div>
        </div>

        <!-- 励志短句 -->
        <div class="inspiration-card motivation">
          <div class="card-header">
            <div class="card-icon motivation-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M13 2L3 14H12L11 22L21 10H12L13 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3 class="card-title">今日激励</h3>
          </div>
          <div class="card-content">
            <ul class="motivation-list">
              <li>相信自己，你比想象中更强大</li>
              <li>每一个小进步都在成就更好的自己</li>
              <li>保持热爱，奔赴下一场山海</li>
            </ul>
          </div>
        </div>
      </div>
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
            <div class="weather-emoji">🌤️</div>
            <div class="weather-desc">晴转多云</div>
          </div>
          <p class="mood-message">愿你拥有如阳光般明媚的心情，温暖而充满力量。无论遇到什么，都要保持微笑和善良。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

onMounted(() => {
  
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

/* 心灵鸡汤卡片 */
.inspiration-cards {
  margin-bottom: 24px;
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