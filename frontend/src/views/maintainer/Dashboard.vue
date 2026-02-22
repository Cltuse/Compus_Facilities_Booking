<template>
  <div class="maintainer-dashboard">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h4a2 2 0 002-2m-6 0V5a2 2 0 012-2h4a2 2 0 012 2v14a2 2 0 01-2 2h-4a2 2 0 01-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          维修数据统计
        </h1>
        <p class="page-subtitle">设施维护任务统计分析</p>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card primary">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Box /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalFacilities }}</div>
                <div class="stat-label">设施总数</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card warning">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.pendingMaintenance }}</div>
                <div class="stat-label">待维修数量</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card success">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.completedMaintenance }}</div>
                <div class="stat-label">已维修数量</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card info">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalMaintenance }}</div>
                <div class="stat-label">总计维修数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-container">
      <!-- 维修任务趋势图 -->
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">维修任务趋势图</div>
              <div class="chart-subtitle">按日期统计的维修任务数量变化</div>
            </div>
          </template>
          <div class="chart-container" ref="trendChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>

      <!-- 维修类型分布饼图 -->
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">维修类型分布</div>
              <div class="chart-subtitle">不同类型维护任务占比</div>
            </div>
          </template>
          <div class="chart-container" ref="pieChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 维修效率统计 -->
    <el-row :gutter="20" class="charts-container">
      <!-- 平均维修时长柱状图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">平均维修时长统计</div>
              <div class="chart-subtitle">不同类型维护任务的平均完成时长</div>
            </div>
          </template>
          <div class="chart-container" ref="durationChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 设施故障率排行 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">设施故障率排行</div>
              <div class="chart-subtitle">故障次数最多的设施 Top 5</div>
            </div>
          </template>
          <div class="chart-container" ref="faultChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import { Warning, Check, Document, Box } from '@element-plus/icons-vue';

const trendChartRef = ref(null);
const pieChartRef = ref(null);
const durationChartRef = ref(null);
const faultChartRef = ref(null);

const stats = ref({
  totalFacilities: 0,
  pendingMaintenance: 0,
  completedMaintenance: 0,
  totalMaintenance: 0
});

onMounted(() => {
  loadStats();
  initCharts();
});

const loadStats = async () => {
  // 模拟加载统计数据，实际应从后端API获取
  stats.value = {
    totalFacilities: 156,
    pendingMaintenance: 24,
    completedMaintenance: 132,
    totalMaintenance: 156
  };
};

const initCharts = async () => {
  await nextTick();

  // 维修任务趋势图（折线图）
  initTrendChart();

  // 维修类型分布饼图
  initPieChart();

  // 平均维修时长柱状图
  initDurationChart();

  // 设施故障率排行柱状图
  initFaultChart();
};

const initTrendChart = () => {
  const chartDom = trendChartRef.value;
  const myChart = echarts.init(chartDom);

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['待维修', '已维修']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '待维修',
        type: 'line',
        stack: 'Total',
        data: [12, 11, 13, 10, 15, 8, 10],
        itemStyle: {
          color: '#e74c3c'
        },
        areaStyle: {
          opacity: 0.1
        }
      },
      {
        name: '已维修',
        type: 'line',
        stack: 'Total',
        data: [23, 25, 20, 22, 18, 28, 24],
        itemStyle: {
          color: '#2ecc71'
        },
        areaStyle: {
          opacity: 0.1
        }
      }
    ]
  };

  myChart.setOption(option);
  window.addEventListener('resize', () => myChart.resize());
};

const initPieChart = () => {
  const chartDom = pieChartRef.value;
  const myChart = echarts.init(chartDom);

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '维修类型',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 45, name: '日常保养' },
          { value: 32, name: '故障维修' },
          { value: 28, name: '设备升级' },
          { value: 15, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };

  myChart.setOption(option);
  window.addEventListener('resize', () => myChart.resize());
};

const initDurationChart = () => {
  const chartDom = durationChartRef.value;
  const myChart = echarts.init(chartDom);

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['日常保养', '故障维修', '设备升级', '其他'],
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '小时'
    },
    series: [
      {
        name: '平均维修时长',
        type: 'bar',
        barWidth: '60%',
        data: [2.5, 6.2, 12.8, 4.1],
        itemStyle: {
          color: '#3498db'
        }
      }
    ]
  };

  myChart.setOption(option);
  window.addEventListener('resize', () => myChart.resize());
};

const initFaultChart = () => {
  const chartDom = faultChartRef.value;
  const myChart = echarts.init(chartDom);

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: ['投影仪', '空调', '音响设备', '电脑', '打印机'],
      axisTick: {
        show: false
      }
    },
    series: [
      {
        name: '故障次数',
        type: 'bar',
        label: {
          show: true,
          position: 'right'
        },
        data: [15, 12, 9, 7, 5],
        itemStyle: {
          color: '#e67e22'
        }
      }
    ]
  };

  myChart.setOption(option);
  window.addEventListener('resize', () => myChart.resize());
};
</script>

<style scoped>
.maintainer-dashboard {
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

/* 统计卡片区域 */
.stats-container {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: none;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-card.primary {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
}

.stat-card.warning {
  background: linear-gradient(135deg, #fffbeb 0%, #fefcbf 100%);
}

.stat-card.success {
  background: linear-gradient(135deg, #ecfdf5 0%, #c6f6d5 100%);
}

.stat-card.info {
  background: linear-gradient(135deg, #ebf8ff 0%, #bee3f8 100%);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
}

.stat-card.primary .stat-icon {
  background: rgba(64, 158, 255, 0.2);
  color: #409eff;
}

.stat-card.warning .stat-icon {
  background: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
}

.stat-card.success .stat-icon {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.stat-card.info .stat-icon {
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

/* 图表容器 */
.charts-container {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: none;
}

.chart-header {
  padding-bottom: 12px;
  border-bottom: 1px solid #edf2f7;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 4px 0;
}

.chart-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0;
  font-weight: 400;
}

.chart-container {
  padding: 16px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .page-title {
    margin-left: 0;
  }

  .page-subtitle {
    margin-left: 0;
  }

  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 12px;
  }
}
</style>