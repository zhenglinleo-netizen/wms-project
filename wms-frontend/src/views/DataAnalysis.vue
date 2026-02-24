<template>
  <div class="data-analysis">
    <div class="page-header">
      <h1>数据分析与报表</h1>
      <el-button type="primary" @click="exportReport">
        <el-icon><Download /></el-icon>
        导出报表
      </el-button>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="分析维度">
          <el-select v-model="filterForm.dimension" placeholder="请选择">
            <el-option label="采购分析" value="purchase" />
            <el-option label="库存分析" value="inventory" />
            <el-option label="供应商分析" value="supplier" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyFilter">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 错误信息 -->
    <el-alert
      v-if="error"
      :title="error"
      type="error"
      show-icon
      :closable="true"
      @close="error = ''"
      style="margin-bottom: 20px"
    />

    <!-- 图表区域 -->
    <div class="charts-container" v-loading="loading" element-loading-text="图表加载中..." element-loading-background="rgba(255, 255, 255, 0.8)">
      <!-- 采购分析图表 -->
      <div v-if="filterForm.dimension === 'purchase' || !filterForm.dimension" class="chart-section">
        <h2>采购分析</h2>
        <div class="chart-grid">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>采购金额趋势</span>
              </div>
            </template>
            <div ref="purchaseTrendChart" class="chart" style="height: 400px;"></div>
          </el-card>
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>采购类别占比</span>
              </div>
            </template>
            <div ref="purchaseCategoryChart" class="chart" style="height: 400px;"></div>
          </el-card>
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>采购数量分布</span>
              </div>
            </template>
            <div ref="purchaseQuantityChart" class="chart" style="height: 400px;"></div>
          </el-card>
        </div>
      </div>

      <!-- 库存分析图表 -->
      <div v-if="filterForm.dimension === 'inventory' || !filterForm.dimension" class="chart-section">
        <h2>库存分析</h2>
        <div class="chart-grid">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>库存水平趋势</span>
              </div>
            </template>
            <div ref="inventoryLevelChart" class="chart" style="height: 400px;"></div>
          </el-card>
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>库存价值分布</span>
              </div>
            </template>
            <div ref="inventoryValueChart" class="chart" style="height: 400px;"></div>
          </el-card>
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>库存周转率</span>
              </div>
            </template>
            <div ref="inventoryTurnoverChart" class="chart" style="height: 400px;"></div>
          </el-card>
        </div>
      </div>

      <!-- 供应商分析图表 -->
      <div v-if="filterForm.dimension === 'supplier' || !filterForm.dimension" class="chart-section">
        <h2>供应商分析</h2>
        <div class="chart-grid">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>供应商绩效排名</span>
              </div>
            </template>
            <div ref="supplierPerformanceChart" class="chart" style="height: 400px;"></div>
          </el-card>
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>供应商分布</span>
              </div>
            </template>
            <div ref="supplierDistributionChart" class="chart" style="height: 400px;"></div>
          </el-card>
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>供应商合作趋势</span>
              </div>
            </template>
            <div ref="supplierTrendChart" class="chart" style="height: 400px;"></div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { Download } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'

// 筛选表单
const filterForm = ref({
  dateRange: [],
  dimension: ''
})

// 图表引用
const purchaseTrendChart = ref(null)
const purchaseCategoryChart = ref(null)
const purchaseQuantityChart = ref(null)
const inventoryLevelChart = ref(null)
const inventoryValueChart = ref(null)
const inventoryTurnoverChart = ref(null)
const supplierPerformanceChart = ref(null)
const supplierDistributionChart = ref(null)
const supplierTrendChart = ref(null)

// 图表实例
const chartInstances = ref({})

// 加载状态
const loading = ref(false)

// 错误信息
const error = ref('')

// 模拟数据
const mockData = {
  purchaseTrend: {
    dates: ['1月', '2月', '3月', '4月', '5月', '6月'],
    amounts: [120000, 150000, 180000, 160000, 200000, 220000]
  },
  purchaseCategory: {
    categories: ['面料', '辅料', '包装材料', '其他'],
    values: [45, 30, 15, 10]
  },
  purchaseQuantity: {
    ranges: ['0-100', '101-500', '501-1000', '1001-5000', '5000+'],
    counts: [120, 85, 45, 20, 5]
  },
  inventoryLevel: {
    dates: ['1月', '2月', '3月', '4月', '5月', '6月'],
    levels: [800000, 850000, 900000, 880000, 920000, 950000]
  },
  inventoryValue: {
    categories: ['面料', '辅料', '包装材料', '其他'],
    values: [50, 35, 10, 5]
  },
  inventoryTurnover: {
    months: ['1月', '2月', '3月', '4月', '5月', '6月'],
    rates: [2.1, 2.3, 2.5, 2.4, 2.6, 2.8]
  },
  supplierPerformance: {
    names: ['供应商A', '供应商B', '供应商C', '供应商D', '供应商E'],
    scores: [95, 88, 82, 75, 68]
  },
  supplierDistribution: {
    regions: ['华东', '华北', '华南', '西南', '其他'],
    counts: [45, 25, 20, 8, 2]
  },
  supplierTrend: {
    quarters: ['Q1', 'Q2', 'Q3', 'Q4'],
    cooperationCounts: [25, 32, 40, 45]
  }
}

// 初始化图表
const initCharts = () => {
  loading.value = true
  error.value = ''
  
  try {
    // 清理之前的图表实例
    Object.values(chartInstances.value).forEach(chart => {
      chart.dispose()
    })
    chartInstances.value = {}

    // 采购金额趋势图
    if (purchaseTrendChart.value) {
      chartInstances.value.purchaseTrend = echarts.init(purchaseTrendChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.purchaseTrend.setOption({
        title: {
          text: '采购金额趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: mockData.purchaseTrend.dates
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [{
          data: mockData.purchaseTrend.amounts,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          }
        }]
      })
    }

    // 采购类别占比图
    if (purchaseCategoryChart.value) {
      chartInstances.value.purchaseCategory = echarts.init(purchaseCategoryChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.purchaseCategory.setOption({
        title: {
          text: '采购类别占比',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [{
          type: 'pie',
          radius: '60%',
          data: mockData.purchaseCategory.categories.map((category, index) => ({
            name: category,
            value: mockData.purchaseCategory.values[index]
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }

    // 采购数量分布图
    if (purchaseQuantityChart.value) {
      chartInstances.value.purchaseQuantity = echarts.init(purchaseQuantityChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.purchaseQuantity.setOption({
        title: {
          text: '采购数量分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: mockData.purchaseQuantity.ranges
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: mockData.purchaseQuantity.counts,
          type: 'bar',
          itemStyle: {
            color: '#67C23A'
          }
        }]
      })
    }

    // 库存水平趋势图
    if (inventoryLevelChart.value) {
      chartInstances.value.inventoryLevel = echarts.init(inventoryLevelChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.inventoryLevel.setOption({
        title: {
          text: '库存水平趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: mockData.inventoryLevel.dates
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [{
          data: mockData.inventoryLevel.levels,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#E6A23C'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
              { offset: 1, color: 'rgba(230, 162, 60, 0.1)' }
            ])
          }
        }]
      })
    }

    // 库存价值分布图
    if (inventoryValueChart.value) {
      chartInstances.value.inventoryValue = echarts.init(inventoryValueChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.inventoryValue.setOption({
        title: {
          text: '库存价值分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [{
          type: 'pie',
          radius: '60%',
          data: mockData.inventoryValue.categories.map((category, index) => ({
            name: category,
            value: mockData.inventoryValue.values[index]
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }

    // 库存周转率图
    if (inventoryTurnoverChart.value) {
      chartInstances.value.inventoryTurnover = echarts.init(inventoryTurnoverChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.inventoryTurnover.setOption({
        title: {
          text: '库存周转率',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: mockData.inventoryTurnover.months
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: mockData.inventoryTurnover.rates,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#F56C6C'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
              { offset: 1, color: 'rgba(245, 108, 108, 0.1)' }
            ])
          }
        }]
      })
    }

    // 供应商绩效排名图
    if (supplierPerformanceChart.value) {
      chartInstances.value.supplierPerformance = echarts.init(supplierPerformanceChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.supplierPerformance.setOption({
        title: {
          text: '供应商绩效排名',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: mockData.supplierPerformance.names
        },
        yAxis: {
          type: 'value',
          max: 100
        },
        series: [{
          data: mockData.supplierPerformance.scores,
          type: 'bar',
          itemStyle: {
            color: '#909399'
          }
        }]
      })
    }

    // 供应商分布图
    if (supplierDistributionChart.value) {
      chartInstances.value.supplierDistribution = echarts.init(supplierDistributionChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.supplierDistribution.setOption({
        title: {
          text: '供应商分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [{
          type: 'pie',
          radius: '60%',
          data: mockData.supplierDistribution.regions.map((region, index) => ({
            name: region,
            value: mockData.supplierDistribution.counts[index]
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }

    // 供应商合作趋势图
    if (supplierTrendChart.value) {
      chartInstances.value.supplierTrend = echarts.init(supplierTrendChart.value, null, {
        renderer: 'canvas',
        useDirtyRect: true
      })
      chartInstances.value.supplierTrend.setOption({
        title: {
          text: '供应商合作趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: mockData.supplierTrend.quarters
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: mockData.supplierTrend.cooperationCounts,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#722ED1'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(114, 46, 209, 0.3)' },
              { offset: 1, color: 'rgba(114, 46, 209, 0.1)' }
            ])
          }
        }]
      })
    }
  } catch (err) {
    error.value = '图表初始化失败: ' + err.message
    console.error('图表初始化失败:', err)
  } finally {
    loading.value = false
  }
}

// 响应式调整
const handleResize = () => {
  Object.values(chartInstances.value).forEach(chart => {
    chart.resize()
  })
}

// 应用筛选
const applyFilter = () => {
  // 这里可以添加筛选逻辑
  console.log('应用筛选:', filterForm.value)
  // 重新加载数据
  initCharts()
}

// 重置筛选
const resetFilter = () => {
  filterForm.value = {
    dateRange: [],
    dimension: ''
  }
  initCharts()
}

// 导出报表
const exportReport = () => {
  // 准备导出数据
  const exportData = {
    '采购分析': {
      '采购金额趋势': mockData.purchaseTrend.dates.map((date, index) => ({
        '月份': date,
        '金额': mockData.purchaseTrend.amounts[index]
      })),
      '采购类别占比': mockData.purchaseCategory.categories.map((category, index) => ({
        '类别': category,
        '占比': mockData.purchaseCategory.values[index]
      })),
      '采购数量分布': mockData.purchaseQuantity.ranges.map((range, index) => ({
        '数量范围': range,
        '采购次数': mockData.purchaseQuantity.counts[index]
      }))
    },
    '库存分析': {
      '库存水平趋势': mockData.inventoryLevel.dates.map((date, index) => ({
        '月份': date,
        '库存水平': mockData.inventoryLevel.levels[index]
      })),
      '库存价值分布': mockData.inventoryValue.categories.map((category, index) => ({
        '类别': category,
        '价值占比': mockData.inventoryValue.values[index]
      })),
      '库存周转率': mockData.inventoryTurnover.months.map((month, index) => ({
        '月份': month,
        '周转率': mockData.inventoryTurnover.rates[index]
      }))
    },
    '供应商分析': {
      '供应商绩效排名': mockData.supplierPerformance.names.map((name, index) => ({
        '供应商': name,
        '绩效分数': mockData.supplierPerformance.scores[index]
      })),
      '供应商分布': mockData.supplierDistribution.regions.map((region, index) => ({
        '地区': region,
        '供应商数量': mockData.supplierDistribution.counts[index]
      })),
      '供应商合作趋势': mockData.supplierTrend.quarters.map((quarter, index) => ({
        '季度': quarter,
        '合作次数': mockData.supplierTrend.cooperationCounts[index]
      }))
    }
  }

  // 创建工作簿
  const wb = XLSX.utils.book_new()

  // 为每个分析类别创建工作表
  Object.keys(exportData).forEach(analysisType => {
    Object.keys(exportData[analysisType]).forEach(dataType => {
      const ws = XLSX.utils.json_to_sheet(exportData[analysisType][dataType])
      XLSX.utils.book_append_sheet(wb, ws, `${analysisType}-${dataType}`)
    })
  })

  // 生成Excel文件并下载
  XLSX.writeFile(wb, `数据分析报表_${new Date().toISOString().split('T')[0]}.xlsx`)
  console.log('报表导出成功')
}

// 监听维度变化
watch(() => filterForm.value.dimension, () => {
  initCharts()
})

// 生命周期
onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  cleanup()
})

// 清理
const cleanup = () => {
  window.removeEventListener('resize', handleResize)
  Object.values(chartInstances.value).forEach(chart => {
    chart.dispose()
  })
}
</script>

<style scoped>
.data-analysis {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  align-items: center;
  gap: 10px;
}

.charts-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-section {
  margin-bottom: 30px;
}

.chart-section h2 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #666;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  height: 450px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart {
  width: 100%;
  height: 100%;
}

@media (max-width: 768px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
