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
          <el-card class="stat-card primary" @click="handleFacilityClick" style="cursor: pointer;">
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
          <el-card class="stat-card warning" @click="handlePendingMaintenanceClick" style="cursor: pointer;">
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
          <el-card class="stat-card success" @click="handleCompletedMaintenanceClick" style="cursor: pointer;">
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
          <el-card class="stat-card info" @click="handleTotalMaintenanceClick" style="cursor: pointer;">
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
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M3 3v18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M18 9l-5 5-4-4-3 3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>维修任务趋势图</span>
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
          <div class="chart-container" ref="trendChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>

      <!-- 维修类型分布饼图 -->
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M21.21 15.89A10 10 0 1 1 8 2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M22 12A10 10 0 0 0 12 2v10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>维修类型分布</span>
              </div>
              <div class="chart-controls">
              </div>
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
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>平均维修时长统计</span>
              </div>
              <div class="chart-controls">
              </div>
            </div>
          </template>
          <div class="chart-container" ref="durationChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- 设施故障数排行 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <div class="chart-title">
                <div class="title-icon">
                  <svg viewBox="0 0 24 24" fill="none">
                    <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <span>设施故障数排行</span>
              </div>
              <div class="chart-controls">
              </div>
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
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import { Warning, Check, Document, Box } from '@element-plus/icons-vue';
import { maintenanceAPI, facilityAPI } from '../../api';

const trendChartRef = ref(null);
const pieChartRef = ref(null);
const durationChartRef = ref(null);
const faultChartRef = ref(null);

let trendChart = null;
let pieChart = null;
let durationChart = null;
let faultChart = null;
const router = useRouter();

const timeRange = ref('7d');

const stats = ref({
  totalFacilities: 0,
  pendingMaintenance: 0,
  completedMaintenance: 0,
  totalMaintenance: 0
});

const trendChartData = ref({});
const typeDistributionData = ref([]);
const durationData = ref([]);
const faultRankingData = ref([]);

onMounted(async () => {
  await loadStats();
  await loadChartData();
  await nextTick();
  initCharts();
});

const loadStats = async () => {
  try {
    const summaryRes = await maintenanceAPI.getSummaryStats();
    if (summaryRes.data) {
      stats.value = {
        totalFacilities: summaryRes.data.totalFacilities || 0,
        pendingMaintenance: summaryRes.data.pendingMaintenance || 0,
        completedMaintenance: summaryRes.data.completedMaintenance || 0,
        totalMaintenance: summaryRes.data.totalMaintenance || 0
      };
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

const loadChartData = async () => {
  try {
    const [trendRes, typeRes, durationRes, faultRes] = await Promise.all([
      maintenanceAPI.getStatsByTimeRange(timeRange.value),
      maintenanceAPI.getTypeDistribution(timeRange.value),
      maintenanceAPI.getDurationStats(timeRange.value),
      maintenanceAPI.getFacilityFaultStats(timeRange.value)
    ]);

    processTrendData(trendRes.data?.maintenances || []);
    typeDistributionData.value = typeRes.data?.typeDistribution || [];
    durationData.value = durationRes.data?.durationData || [];
    faultRankingData.value = faultRes.data?.faultRanking || [];
  } catch (error) {
    console.error('加载图表数据失败:', error);
  }
};

const handleTimeRangeChange = async () => {
  await loadChartData();
  updateCharts();
};

// 根据时间范围动态确定最大数据点数量
const getMaxDataPointsByTimeRange = () => {
  switch (timeRange.value) {
    case '1d': return 24; // 按小时显示
    case '7d': return 7;   // 按天显示
    case '30d': return 15; // 每2天显示一个点
    case '180d': return 30; // 每6天显示一个点
    case '365d': return 52; // 按周显示
    default: return 7;
  }
};

const processTrendData = (maintenances) => {
  const allData = {};
  const completedData = {};
  const pendingData = {};
  const inProgressData = {};
  const cancelledData = {};

  maintenances.forEach(m => {
    if (m.createdAt) {
      let date;
      if (typeof m.createdAt === 'string') {
        if (m.createdAt.includes('T')) {
          date = m.createdAt.split('T')[0];
        } else if (m.createdAt.includes(' ')) {
          date = m.createdAt.split(' ')[0];
        } else {
          date = m.createdAt;
        }
      } else if (m.createdAt.date) {
        date = m.createdAt.date;
      } else if (m.createdAt.year) {
        date = `${m.createdAt.year}-${String(m.createdAt.monthValue).padStart(2, '0')}-${String(m.createdAt.dayOfMonth).padStart(2, '0')}`;
      }

      if (date) {
        // 所有任务统计
        allData[date] = (allData[date] || 0) + 1;
        
        // 按状态统计
        if (m.status === 'COMPLETED') {
          completedData[date] = (completedData[date] || 0) + 1;
        } else if (m.status === 'PENDING') {
          pendingData[date] = (pendingData[date] || 0) + 1;
        } else if (m.status === 'IN_PROGRESS') {
          inProgressData[date] = (inProgressData[date] || 0) + 1;
        } else if (m.status === 'CANCELLED') {
          cancelledData[date] = (cancelledData[date] || 0) + 1;
        }
      }
    }
  });

  trendChartData.value = { 
    all: allData, 
    completed: completedData, 
    pending: pendingData, 
    inProgress: inProgressData, 
    cancelled: cancelledData 
  };
};

const initCharts = async () => {
  initTrendChart();
  initPieChart();
  initDurationChart();
  initFaultChart();
};

const updateCharts = () => {
  if (trendChart) initTrendChart();
  if (pieChart) initPieChart();
  if (durationChart) initDurationChart();
  if (faultChart) initFaultChart();
};

const initTrendChart = () => {
  if (!trendChartRef.value) return;

  if (trendChart) {
    trendChart.dispose();
  }

  const chartDom = trendChartRef.value;
  trendChart = echarts.init(chartDom);

  // 获取所有任务数据
  const allData = trendChartData.value.all || {};
  const completedData = trendChartData.value.completed || {};
  const pendingData = trendChartData.value.pending || {};
  const inProgressData = trendChartData.value.inProgress || {};
  
  // 合并所有日期，并根据时间范围动态调整显示密度
  let allDates = [...new Set([
    ...Object.keys(allData),
    ...Object.keys(completedData),
    ...Object.keys(pendingData),
    ...Object.keys(inProgressData)
  ])].sort();

  // 根据时间范围动态调整显示的数据点数量
  const maxDataPoints = getMaxDataPointsByTimeRange();
  if (allDates.length > maxDataPoints) {
    // 按时间间隔采样，而不是简单取最后几个
    const step = Math.ceil(allDates.length / maxDataPoints);
    allDates = allDates.filter((_, index) => index % step === 0 || index === allDates.length - 1);
  }

  const allValues = allDates.map(date => allData[date] || 0);
  const completedValues = allDates.map(date => completedData[date] || 0);
  const pendingValues = allDates.map(date => pendingData[date] || 0);
  const inProgressValues = allDates.map(date => inProgressData[date] || 0);

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: 'none',
      textStyle: {
        color: '#fff'
      },
      formatter: (params) => {
        let result = `${params[0].axisValue}<br/>`;
        params.forEach(param => {
          result += `${param.marker} ${param.seriesName}: ${param.value} 单<br/>`;
        });
        return result;
      }
    },
    legend: {
      data: ['所有任务', '已完成', '待处理', '进行中'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: allDates.length > 0 ? allDates : ['暂无数据'],
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
        rotate: 45,//四十五度
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
    series: [
      {
        name: '所有任务',
        type: 'line',
        data: allValues,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#3498db'
        },
        lineStyle: {
          width: 3,
          color: '#3498db'
        },
        emphasis: {
          itemStyle: {
            color: '#3498db',
            borderColor: '#fff',
            borderWidth: 2,
            shadowBlur: 10,
            shadowColor: 'rgba(52, 152, 219, 0.5)'
          }
        }
      },
      {
        name: '已完成',
        type: 'line',
        data: completedValues,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#2ecc71'
        },
        lineStyle: {
          width: 3,
          color: '#2ecc71'
        },
        emphasis: {
          itemStyle: {
            color: '#2ecc71',
            borderColor: '#fff',
            borderWidth: 2,
            shadowBlur: 10,
            shadowColor: 'rgba(46, 204, 113, 0.5)'
          }
        }
      },
      {
        name: '待处理',
        type: 'line',
        data: pendingValues,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#f39c12'
        },
        lineStyle: {
          width: 2,
          color: '#f39c12'
        },
        emphasis: {
          itemStyle: {
            color: '#f39c12',
            borderColor: '#fff',
            borderWidth: 2,
            shadowBlur: 10,
            shadowColor: 'rgba(243, 156, 18, 0.5)'
          }
        }
      },
      {
        name: '进行中',
        type: 'line',
        data: inProgressValues,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#9b59b6'
        },
        lineStyle: {
          width: 2,
          color: '#9b59b6'
        },
        emphasis: {
          itemStyle: {
            color: '#9b59b6',
            borderColor: '#fff',
            borderWidth: 2,
            shadowBlur: 10,
            shadowColor: 'rgba(155, 89, 182, 0.5)'
          }
        }
      }
    ]
  };

  trendChart.setOption(option);

  window.addEventListener('resize', () => {
    trendChart?.resize();
  });
};

const initPieChart = () => {
  if (!pieChartRef.value) return;

  if (pieChart) {
    pieChart.dispose();
  }

  const chartDom = pieChartRef.value;
  pieChart = echarts.init(chartDom);

  // 使用实际数据而不是静态数据
  const typeData = typeDistributionData.value.map(item => ({
    value: item.value || item.count || 0,
    name: item.name || item.typeName || item.type || '未知类型',
    itemStyle: item.itemStyle || {}
  })).filter(item => item.value > 0); // 只显示有数据的类型

  // 如果没有数据，显示提示
  if (typeData.length === 0) {
    typeData.push({ value: 1, name: '暂无数据', itemStyle: { color: '#909399' } });
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: {
        color: '#718096'
      }
    },
    series: [
      {
        name: '维修类型分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
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
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: typeData
      }
    ]
  };

  pieChart.setOption(option);
  window.addEventListener('resize', () => {
    pieChart?.resize();
  });
};

const initDurationChart = () => {
  if (!durationChartRef.value) return;

  if (durationChart) {
    durationChart.dispose();
  }

  const chartDom = durationChartRef.value;
  durationChart = echarts.init(chartDom);

  // 过滤掉平均时长为0的数据，并按时长排序
  const filteredData = durationData.value
    .filter(item => item.avgDuration > 0)
    .sort((a, b) => b.avgDuration - a.avgDuration)
    .slice(0, 5); // 最多显示5个类型
  
  const categories = filteredData.map(item => item.type || item.typeName || '未知类型');
  const values = filteredData.map(item => item.avgDuration || 0);
  
  // 如果没有数据，显示提示
  if (filteredData.length === 0) {
    categories.push('暂无数据');
    values.push(0);
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: 'none',
      textStyle: {
        color: '#fff'
      },
      formatter: (params) => {
        const data = params[0];
        return `${data.axisValue}<br/>平均时长: ${data.value} 小时`;
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisTick: {
        alignWithLabel: true
      },
      axisLine: {
        lineStyle: {
          color: '#e2e8f0'
        }
      },
      axisLabel: {
        color: '#718096',
        fontSize: 12,
        rotate: categories.length > 3 ? 30 : 0,
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      name: '小时',
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
    series: [
      {
        name: '平均维修时长',
        type: 'bar',
        barWidth: '50%',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#3498db' },
            { offset: 1, color: '#5dade2' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#5dade2' },
              { offset: 1, color: '#3498db' }
            ]),
            shadowBlur: 10,
            shadowColor: 'rgba(52, 152, 219, 0.3)'
          }
        }
      }
    ]
  };

  durationChart.setOption(option);

  window.addEventListener('resize', () => {
    durationChart?.resize();
  });
};

const initFaultChart = () => {
  if (!faultChartRef.value) return;

  if (faultChart) {
    faultChart.dispose();
  }

  const chartDom = faultChartRef.value;
  faultChart = echarts.init(chartDom);

  // 按故障次数排序（降序），最多显示5个设施，过滤掉故障次数为0的设施
  const sortedData = [...faultRankingData.value]
    .filter(item => item.faultCount > 0)
    .sort((a, b) => b.faultCount - a.faultCount)
    .slice(0, 5);
  
  const categories = sortedData.map(item => item.facilityName || '未知设施');
  const values = sortedData.map(item => item.faultCount || 0);
  
  // 如果没有数据，显示提示
  if (sortedData.length === 0) {
    categories.push('暂无数据');
    values.push(0);
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: 'none',
      textStyle: {
        color: '#fff'
      },
      formatter: (params) => {
        const data = params[0];
        // 如果设施名称为空或空格，不显示提示
        if (!data.axisValue || data.axisValue.trim() === '') {
          return '';
        }
        return `${data.axisValue}<br/>故障次数: ${data.value} 次`;
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisTick: {
        alignWithLabel: true
      },
      axisLine: {
        lineStyle: {
          color: '#e2e8f0'
        }
      },
      axisLabel: {
        color: '#718096',
        fontSize: 12,
        rotate: 30,
        interval: 0,
        formatter: (value) => {
          // 如果设施名称为空或空格，不显示标签
          if (!value || value.trim() === '') {
            return '';
          }
          return value.length > 8 ? value.substring(0, 8) + '...' : value;
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '故障次数',
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
    series: [
      {
        name: '故障次数',
        type: 'bar',
        label: {
          show: true,
          position: 'top',
          color: '#718096',
          fontSize: 11,
          formatter: (params) => {
            // 如果故障次数为0，不显示标签
            return params.value > 0 ? params.value : '';
          }
        },
        data: values,
        itemStyle: {
          color: (params) => {
            // 如果故障次数为0，使用透明色
            return params.value > 0 
              ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#e67e22' },
                  { offset: 1, color: '#f39c12' }
                ])
              : 'transparent';
          },
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#f39c12' },
              { offset: 1, color: '#e67e22' }
            ]),
            shadowBlur: 10,
            shadowColor: 'rgba(230, 126, 34, 0.3)'
          }
        }
      }
    ]
  };

  faultChart.setOption(option);

  window.addEventListener('resize', () => {
    faultChart?.resize();
  });
};

// 点击事件处理函数
const handleFacilityClick = () => {
  // 跳转至设施管理界面
  router.push('/maintainer/facility');
};

const handlePendingMaintenanceClick = () => {
  // 跳转至维护任务，筛选未维护数据
  router.push({
    path: '/maintainer/maintenance',
    query: {
      maintenanceStatus: 'UNMAINTAINED'
    }
  });
};

const handleCompletedMaintenanceClick = () => {
  // 跳转至维护任务，筛选已维护数据
  router.push({
    path: '/maintainer/maintenance',
    query: {
      maintenanceStatus: 'MAINTAINED'
    }
  });
};

const handleTotalMaintenanceClick = () => {
  // 跳转至维护任务，筛选所有记录
  router.push({
    path: '/maintainer/maintenance',
    query: {
      maintenanceStatus: ''
    }
  });
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 12px;
  border-bottom: 1px solid #edf2f7;
}

.chart-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
  margin: 0 0 4px 0;
}

.chart-title .title-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.chart-title .title-icon svg {
  width: 18px;
  height: 18px;
  color: #409eff;
}

.chart-title span {
  font-size: 16px;
}

.chart-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0 0 0 42px;
  font-weight: 400;
}

.chart-controls {
  flex-shrink: 0;
}

.time-select {
  width: 120px;
}

.time-select :deep(.el-input__wrapper) {
  border-radius: 8px;
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