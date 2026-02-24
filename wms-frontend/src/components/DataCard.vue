<template>
  <div 
    class="data-card"
    :class="{
      'is-loading': loading,
      'is-hoverable': hoverable,
      [`card-${variant}`]: variant,
      'with-trend': trend !== null
    }"
    :style="cardStyle"
  >
    <div class="card-header">
      <div class="card-title" :class="{ 'with-badge': badge }">
        {{ title }}
        <span v-if="badge" class="card-badge" :class="{
          'badge-primary': badge === 'primary',
          'badge-success': badge === 'success',
          'badge-warning': badge === 'warning',
          'badge-danger': badge === 'danger'
        }">{{ badgeText }}</span>
      </div>
      <div class="card-icon" :class="[iconClass, { 'is-animated': animatedIcon }]">
        <slot name="icon"></slot>
      </div>
    </div>
    <div class="card-body">
      <div v-if="loading" class="card-loading">
        <div class="loading-spinner"></div>
      </div>
      <div v-else class="card-value">
        <AnimatedNumber
          :value="value"
          :duration="duration"
          :precision="precision"
          :prefix="prefix"
          :suffix="suffix"
          :formatter="formatter"
          :easing="easing"
          :use-color="useColor"
          :increasing-color="increasingColor"
          :decreasing-color="decreasingColor"
        />
      </div>
      <div class="card-desc" :class="{ 'with-value': value !== 0 }">{{ desc }}</div>
      <div v-if="trend" class="card-trend" :class="{ 'is-up': trend > 0, 'is-down': trend < 0 }">
        <span class="trend-icon" :class="{ 'is-animated': animatedTrend }">{{ trend > 0 ? '↑' : '↓' }}</span>
        <span class="trend-value">{{ Math.abs(trend) }}%</span>
        <span class="trend-text">{{ trendText }}</span>
      </div>
    </div>
    <div v-if="actions" class="card-actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup>
import { defineProps, computed } from 'vue'
import AnimatedNumber from './AnimatedNumber.vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    default: 0
  },
  desc: {
    type: String,
    default: ''
  },
  trend: {
    type: Number,
    default: null
  },
  trendText: {
    type: String,
    default: ''
  },
  iconClass: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  },
  precision: {
    type: Number,
    default: 0
  },
  prefix: {
    type: String,
    default: ''
  },
  suffix: {
    type: String,
    default: ''
  },
  formatter: {
    type: Function,
    default: null
  },
  duration: {
    type: Number,
    default: 1500
  },
  easing: {
    type: String,
    default: 'easeOutCubic'
  },
  variant: {
    type: String,
    default: 'default',
    validator: (value) => {
      return ['default', 'primary', 'success', 'warning', 'danger', 'info'].includes(value)
    }
  },
  hoverable: {
    type: Boolean,
    default: true
  },
  animatedIcon: {
    type: Boolean,
    default: true
  },
  animatedTrend: {
    type: Boolean,
    default: true
  },
  useColor: {
    type: Boolean,
    default: true
  },
  increasingColor: {
    type: String,
    default: '#4caf50'
  },
  decreasingColor: {
    type: String,
    default: '#f44336'
  },
  badge: {
    type: String,
    default: null,
    validator: (value) => {
      return [null, 'primary', 'success', 'warning', 'danger'].includes(value)
    }
  },
  badgeText: {
    type: String,
    default: ''
  },
  actions: {
    type: Boolean,
    default: false
  }
})

// 计算卡片样式
const cardStyle = computed(() => {
  const style = {}
  
  // 根据variant设置不同的样式
  const variants = {
    primary: {
      borderLeft: '4px solid var(--primary-color)'
    },
    success: {
      borderLeft: '4px solid var(--success-color)'
    },
    warning: {
      borderLeft: '4px solid var(--warning-color)'
    },
    danger: {
      borderLeft: '4px solid var(--danger-color)'
    },
    info: {
      borderLeft: '4px solid var(--info-color)'
    }
  }
  
  if (variants[props.variant]) {
    Object.assign(style, variants[props.variant])
  }
  
  return style
})
</script>

<style scoped>
.data-card {
  background-color: var(--card-background);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
  transition: all var(--transition-normal);
  animation: cardFadeIn 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  overflow: hidden;
}

.data-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(25, 118, 210, 0.05), transparent);
  transition: left 0.6s ease;
  z-index: 0;
}

.data-card:hover::before {
  left: 100%;
}

.data-card.is-hoverable:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 12px 30px rgba(25, 118, 210, 0.15);
}

.data-card.with-trend {
  padding-bottom: var(--spacing-xl);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-md);
  position: relative;
  z-index: 1;
}

.card-title {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.card-title.with-badge {
  gap: 12px;
}

.card-badge {
  font-size: var(--font-size-xs);
  padding: 2px 8px;
  border-radius: var(--border-radius-sm);
  font-weight: 600;
  animation: badgePulse 1.5s ease-in-out infinite;
}

.card-badge.badge-primary {
  background-color: rgba(25, 118, 210, 0.1);
  color: var(--primary-color);
}

.card-badge.badge-success {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--success-color);
}

.card-badge.badge-warning {
  background-color: rgba(255, 152, 0, 0.1);
  color: var(--warning-color);
}

.card-badge.badge-danger {
  background-color: rgba(244, 67, 54, 0.1);
  color: var(--danger-color);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(25, 118, 210, 0.1);
  color: var(--primary-color);
  font-size: var(--font-size-lg);
  transition: all var(--transition-normal);
  position: relative;
  z-index: 1;
}

.card-icon.is-animated {
  animation: iconFloat 3s ease-in-out infinite;
}

.card-icon:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.2);
}

.card-body {
  position: relative;
  z-index: 1;
}

.card-loading {
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-color);
  border-top: 3px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.card-value {
  font-size: var(--font-size-xxl);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
  line-height: 1.1;
  transition: all var(--transition-normal);
}

.card-desc {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-sm);
  line-height: 1.4;
}

.card-desc.with-value {
  opacity: 0.8;
}

.card-trend {
  font-size: var(--font-size-xs);
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: var(--spacing-sm);
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--border-color);
  animation: trendSlideIn 0.5s ease-out;
}

.card-trend.is-up {
  color: var(--success-color);
  animation: trendUp 0.5s ease-out;
}

.card-trend.is-down {
  color: var(--danger-color);
  animation: trendDown 0.5s ease-out;
}

.trend-icon {
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.trend-icon.is-animated {
  animation: trendIconBounce 1s ease-in-out infinite;
}

.trend-value {
  font-weight: 600;
  font-size: var(--font-size-sm);
}

.trend-text {
  color: var(--text-secondary);
  font-size: var(--font-size-xs);
}

.card-actions {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-color);
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  position: relative;
  z-index: 1;
}

/* 卡片变体样式 */
.card-primary {
  background-color: rgba(25, 118, 210, 0.02);
}

.card-success {
  background-color: rgba(76, 175, 80, 0.02);
}

.card-warning {
  background-color: rgba(255, 152, 0, 0.02);
}

.card-danger {
  background-color: rgba(244, 67, 54, 0.02);
}

.card-info {
  background-color: rgba(33, 150, 243, 0.02);
}

/* 动画定义 */
@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}

@keyframes badgePulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes trendSlideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes trendUp {
  from {
    opacity: 0;
    transform: translateX(-10px) translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateX(0) translateY(0);
  }
}

@keyframes trendDown {
  from {
    opacity: 0;
    transform: translateX(-10px) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0) translateY(0);
  }
}

@keyframes trendIconBounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-2px);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .data-card {
    padding: var(--spacing-md);
  }
  
  .card-icon {
    width: 40px;
    height: 40px;
    font-size: var(--font-size-md);
  }
  
  .card-value {
    font-size: var(--font-size-xl);
  }
  
  .card-title {
    font-size: var(--font-size-xs);
  }
  
  .card-desc {
    font-size: var(--font-size-xs);
  }
}
</style>