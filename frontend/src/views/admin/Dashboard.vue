<template>
  <div class="dashboard">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <div class="header-content">
        <h1 class="page-title">
          <div class="title-icon">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          数据统计
        </h1>
        <p class="page-subtitle">设施与预约数据分析</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card facility-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalFacility }}</div>
              <div class="stat-label">设施总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card available-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.availableFacility }}</div>
              <div class="stat-label">可用设施</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card reservation-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalReservations }}</div>
              <div class="stat-label">预约总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card user-card">
          <div class="stat-content">
            <div class="stat-icon-wrapper">
              <el-icon class="stat-icon"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUser }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="24" class="charts-row">
      <el-col :span="16">
        <el-card class="chart-card line-chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M3 17v1h6v-1H3zM3 5v1h10V5H3zm10 5v1h8v-1h-8zM3 11v1h6v-1H3zm10 0v1h8v-1h-8zM3 13v1h6v-1H3zm10 0v1h8v-1h-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>设施类别分布统计</span>
              </div>
              <div class="chart-subtitle">各类别设施数量统计</div>
            </div>
          </template>
          <div class="chart-container" ref="lineChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card pie-chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </div>
                <span>设施状态分布</span>
              </div>
              <div class="chart-subtitle">按设施状态统计</div>
            </div>
          </template>
          <div class="chart-container" ref="pieChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预约统计图表区域 -->
    <el-row :gutter="24" class="charts-row">
      <el-col :span="16">
        <el-card class="chart-card reservation-line-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M3 3v18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M18 9l-5 5-4-4-3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>近期预约数量统计</span>
              </div>
              <div class="chart-controls">
                <el-select v-model="timeRange" placeholder="选择时间段" @change="handleTimeRangeChange" class="time-select">
                  <el-option label="1天内" value="1d" />
                  <el-option label="7天内" value="7d" />
                  <el-option label="30天内" value="30d" />
                  <el-option label="半年内" value="180d" />
                  <el-option label="一年内" value="365d" />
                </el-select>
              </div>
            </div>
          </template>
          <div class="chart-container" ref="reservationLineChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card reservation-pie-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M21.21 15.89A10 10 0 1 1 8 2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M22 12A10 10 0 0 0 12 2v10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>预约类别分布</span>
              </div>
              <div class="chart-controls">
                <el-select v-model="timeRange" placeholder="选择时间段" @change="handleTimeRangeChange" class="time-select">
                  <el-option label="1天内" value="1d" />
                  <el-option label="7天内" value="7d" />
                  <el-option label="30天内" value="30d" />
                  <el-option label="半年内" value="180d" />
                  <el-option label="一年内" value="365d" />
                </el-select>
              </div>
            </div>
          </template>
          <div class="chart-container" ref="reservationPieChartRef"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import { facilityAPI, reservationAPI, userAPI } from '../../api';

const stats = ref({
  totalFacility: 0,
  availableFacility: 0,
  totalReservations: 0,
  totalUser: 0
});

const lineChartRef = ref(null);
const pieChartRef = ref(null);
const reservationLineChartRef = ref(null);
const reservationPieChartRef = ref(null);
let lineChart = null;
let pieChart = null;
let reservationLineChart = null;
let reservationPieChart = null;

const timeRange = ref('7d');

onMounted(async () => {
  await loadStats();
  await loadChartData();
  await nextTick();
  initCharts();
  await loadReservationStats();
});

const loadStats = async () => {
  try {
    const [facilityRes, availableRes, reservationRes, userRes] = await Promise.all([
      facilityAPI.list(),
      facilityAPI.available(),
      reservationAPI.list(),
      userAPI.list()
    ]);

    stats.value = {
      totalFacility: facilityRes.data.length,
      availableFacility: availableRes.data.length,
      totalReservations: reservationRes.data.length,
      totalUser: userRes.data.length
    };
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

const loadChartData = async () => {
  try {
    const [facilityRes] = await Promise.all([
      facilityAPI.list()
    ]);

    // 处理设施类别数据
    const facilityCategoryData = processFacilityCategoryData(facilityRes.data);

    // 处理设施状态数据
    const facilityStatusData = processFacilityStatusData(facilityRes.data);

    return {
      facilityCategoryData,
      facilityStatusData
    };
  } catch (error) {
    console.error('加载图表数据失败:', error);
    return null;
  }
};

// 处理设施类别数据
const processFacilityCategoryData = (facilitys) => {
  const categoryCount = {};

  // 统计各类别设施数量
  facilitys.forEach(facility => {
    const category = facility.category || '未分类';
    if (categoryCount[category]) {
      categoryCount[category]++;
    } else {
      categoryCount[category] = 1;
    }
  });

  // 转换为数组格式并排序
  const categoryData = Object.entries(categoryCount)
    .map(([category, count]) => ({
      category,
      count
    }))
    .sort((a, b) => b.count - a.count); // 按数量降序排列

  return categoryData;
};

// 处理设施状态数据
const processFacilityStatusData = (facilitys) => {
  const statusCount = {
    AVAILABLE: 0,
    MAINTENANCE: 0,
    DISABLED: 0
  };

  facilitys.forEach(facility => {
    const status = facility.status || 'UNKNOWN';
    if (statusCount.hasOwnProperty(status)) {
      statusCount[status]++;
    }
  });

  return [
    { name: '可用', value: statusCount.AVAILABLE, itemStyle: { color: '#67c23a' } },
    { name: '维护中', value: statusCount.MAINTENANCE, itemStyle: { color: '#e6a23c' } },
    { name: '停用', value: statusCount.DISABLED, itemStyle: { color: '#f56c6c' } }
  ].filter(item => item.value > 0);
};

// 初始化图表
const initCharts = async () => {
  const chartData = await loadChartData();
  if (!chartData) return;

  // 初始化柱形图
  initBarChart(chartData.facilityCategoryData);

  // 初始化饼形图
  initPieChart(chartData.facilityStatusData);
};

// 初始化柱形图
const initBarChart = (data) => {
  if (!lineChartRef.value) return;

  lineChart = echarts.init(lineChartRef.value);

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: 'none',
      textStyle: {
        color: '#fff'
      },
      formatter: (params) => {
        const data = params[0];
        return `${data.axisValue}<br/>设施数量: ${data.value} 台`;
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
      data: data.map(item => item.category),
      axisLine: {
        lineStyle: {
          color: '#e2e8f0'
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#718096',
        fontSize: 12,
        rotate: data.length > 6 ? 45 : 0, // 如果类别较多，旋转标签
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#718096',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      }
    },
    series: [{
      name: '设施数量',
      type: 'bar',
      data: data.map(item => item.count),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409eff' },
          { offset: 1, color: '#66b1ff' }
        ]),
        borderRadius: [8, 8, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#66b1ff' },
            { offset: 1, color: '#409eff' }
          ]),
          shadowBlur: 10,
          shadowColor: 'rgba(64, 158, 255, 0.3)'
        }
      },
      barWidth: '60%'
    }]
  };

  lineChart.setOption(option);

  // 响应式处理
  window.addEventListener('resize', () => {
    lineChart?.resize();
  });
};

// 初始化饼形图
const initPieChart = (data) => {
  if (!pieChartRef.value) return;

  pieChart = echarts.init(pieChartRef.value);

  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: 'none',
      textStyle: {
        color: '#fff'
      },
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '设施状态',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold',
          color: '#1a202c'
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      labelLine: {
        show: false
      },
      data: data
    }],
    color: ['#67c23a', '#409eff', '#e6a23c', '#f56c6c']
  };

  pieChart.setOption(option);

  // 响应式处理
  window.addEventListener('resize', () => {
    pieChart?.resize();
  });
};

const loadReservationStats = async () => {
  try {
    console.log('正在加载预约统计数据, timeRange:', timeRange.value);
    
    const [timeRangeRes, categoryRes] = await Promise.all([
      reservationAPI.getStatsByTimeRange(timeRange.value),
      reservationAPI.getCategoryStats(timeRange.value)
    ]);
    
    console.log('时间段数据:', timeRangeRes);
    console.log('类别数据:', categoryRes);
    console.log('reservations具体数据:', timeRangeRes.data.data?.reservations);

    processReservationLineData(timeRangeRes.data?.reservations || []);
    processReservationPieData(categoryRes.data?.categoryData || []);

    console.log('处理后的折线图数据:', window.reservationLineData);
    console.log('处理后的饼图数据:', window.reservationPieData);
    
    await nextTick();
    initReservationCharts();
  } catch (error) {
    console.error('加载预约统计数据失败:', error);
  }
};

const handleTimeRangeChange = async () => {
  await loadReservationStats();
};

const processReservationLineData = (reservations) => {
  const groupedData = {};

  console.log('原始预约记录:', reservations);
  if (reservations && reservations.length > 0) {
    console.log('第一条记录:', reservations[0]);
  }

  reservations.forEach(r => {
    if (r.createdAt) {
      let date;
      if (typeof r.createdAt === 'string') {
        // 处理字符串格式
        if (r.createdAt.includes('T')) {
          date = r.createdAt.split('T')[0];
        } else if (r.createdAt.includes(' ')) {
          date = r.createdAt.split(' ')[0];
        } else {
          date = r.createdAt;
        }
      } else if (r.createdAt.date) {
        date = r.createdAt.date;
      } else if (r.createdAt.year) {
        date = `${r.createdAt.year}-${String(r.createdAt.monthValue).padStart(2, '0')}-${String(r.createdAt.dayOfMonth).padStart(2, '0')}`;
      }
      if (date) {
        groupedData[date] = (groupedData[date] || 0) + 1;
      }
    }
  });

  console.log('分组后的数据:', groupedData);
  window.reservationLineData = groupedData;
};

const processReservationPieData = (categoryData) => {
  window.reservationPieData = categoryData;
};

const initReservationCharts = () => {
  // 销毁已存在的图表实例
  if (reservationLineChart) {
    reservationLineChart.dispose();
  }

  if (reservationPieChart) {
    reservationPieChart.dispose();
  }

  initReservationLineChart();
  initReservationPieChart();
};

const initReservationLineChart = () => {
  if (!reservationLineChartRef.value) return;
  
  reservationLineChart = echarts.init(reservationLineChartRef.value);
  
  const groupedData = window.reservationLineData || {};
  const sortedDates = Object.keys(groupedData).sort();
  const values = sortedDates.map(date => groupedData[date]);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: 'none',
      textStyle: {
        color: '#fff'
      },
      formatter: (params) => {
        const data = params[0];
        return `${data.axisValue}<br/>预约数量: ${data.value} 次`;
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
      data: sortedDates,
      axisLine: {
        lineStyle: {
          color: '#e2e8f0'
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#718096',
        fontSize: 11,
        rotate: sortedDates.length > 10 ? 45 : 0,
        interval: Math.floor(sortedDates.length / 10)
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#718096',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      }
    },
    series: [{
      name: '预约数量',
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      itemStyle: {
        color: '#e6a23c'
      },
      lineStyle: {
        width: 3,
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#e6a23c' },
          { offset: 1, color: '#f56c6c' }
        ])
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(230, 162, 60, 0.4)' },
          { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
        ])
      },
      emphasis: {
        itemStyle: {
          color: '#f56c6c',
          borderColor: '#fff',
          borderWidth: 2,
          shadowBlur: 10,
          shadowColor: 'rgba(230, 162, 60, 0.5)'
        }
      }
    }]
  };
  
  reservationLineChart.setOption(option);
  
  window.addEventListener('resize', () => {
    reservationLineChart?.resize();
  });
};

const initReservationPieChart = () => {
  if (!reservationPieChartRef.value) return;
  
  reservationPieChart = echarts.init(reservationPieChartRef.value);
  
  const categoryData = window.reservationPieData || [];
  
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: 'none',
      textStyle: {
        color: '#fff'
      },
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '预约类别',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold',
          color: '#1a202c'
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      labelLine: {
        show: false
      },
      data: categoryData
    }]
  };
  
  reservationPieChart.setOption(option);
  
  window.addEventListener('resize', () => {
    reservationPieChart?.resize();
  });
};
</script>

<style scoped>
/* 页面背景 */
.dashboard {
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

/* 统计卡片行 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--card-color) 0%, var(--card-color-light) 100%);
}

.facility-card {
  --card-color: #409eff;
  --card-color-light: #66b1ff;
}

.available-card {
  --card-color: #67c23a;
  --card-color-light: #85ce61;
}

.reservation-card {
  --card-color: #e6a23c;
  --card-color-light: #ebb563;
}

.user-card {
  --card-color: #f56c6c;
  --card-color-light: #f78989;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 24px;
}

.stat-icon-wrapper {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  position: relative;
}

.stat-icon-wrapper::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--card-color) 0%, var(--card-color-light) 100%);
  opacity: 0.1;
}

.stat-icon {
  font-size: 32px;
  color: var(--card-color);
  z-index: 1;
  position: relative;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

/* 图表卡片行 */
.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
}

.chart-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.chart-card :deep(.el-card__header) {
  padding: 0;
  border-bottom: none;
}

.chart-card :deep(.el-card__body) {
  padding: 0;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px 32px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-controls {
  display: flex;
  align-items: center;
}

.time-select {
  width: 140px;
}

.time-select :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.chart-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 4px;
}

.chart-title .title-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.chart-title .title-icon svg {
  width: 16px;
  height: 16px;
  color: #409eff;
}

.chart-subtitle {
  font-size: 14px;
  color: #718096;
  margin-left: 44px;
}

.chart-container {
  padding: 24px;
  height: 350px;
}

.line-chart-card .chart-container {
  height: 350px;
}

.pie-chart-card .chart-container {
  height: 350px;
}

.reservation-line-card .chart-container,
.reservation-pie-card .chart-container {
  height: 350px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px 32px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-controls {
  display: flex;
  align-items: center;
}

.time-select {
  width: 140px;
}

.time-select :deep(.el-input__wrapper) {
  border-radius: 8px;
}

/* 动画效果 */
@keyframes gradient-shimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-row .el-col:first-child {
    margin-bottom: 24px;
  }
}

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

  .stat-content {
    padding: 20px;
  }

  .stat-icon-wrapper {
    width: 56px;
    height: 56px;
    margin-right: 16px;
  }

  .stat-icon {
    font-size: 28px;
  }

  .stat-value {
    font-size: 28px;
  }

  .chart-header {
    padding: 20px;
  }

  .chart-container {
    padding: 20px;
    height: 300px !important;
  }

  .stats-row .el-col {
    margin-bottom: 16px;
  }
}

@media (max-width: 480px) {
  .stat-value {
    font-size: 24px;
  }

  .stat-icon-wrapper {
    width: 48px;
    height: 48px;
    margin-right: 12px;
  }

  .stat-icon {
    font-size: 24px;
  }

  .chart-container {
    height: 260px !important;
  }
}
</style>