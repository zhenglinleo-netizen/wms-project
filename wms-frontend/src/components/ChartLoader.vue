<template>
  <div 
    class="chart-loader"
    :class="{
      'is-loading': loading,
      [`loader-${type}`]: loading
    }"
  >
    <div v-if="loading" class="loader-content">
      <!-- 不同类型的加载动画 -->
      <div v-if="type === 'spinner'" class="loader-spinner"></div>
      <div v-else-if="type === 'dots'" class="loader-dots">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
      <div v-else-if="type === 'bars'" class="loader-bars">
        <div class="bar"></div>
        <div class="bar"></div>
        <div class="bar"></div>
      </div>
      <div v-else-if="type === 'pulse'" class="loader-pulse"></div>
      <div v-else-if="type === 'grid'" class="loader-grid">
        <div class="grid-item"></div>
        <div class="grid-item"></div>
        <div class="grid-item"></div>
        <div class="grid-item"></div>
      </div>
      
      <div v-if="text" class="loader-text" :class="{
        'with-icon': showIcon
      }">
        <span v-if="showIcon" class="text-icon">{{ icon }}</span>
        {{ text }}
      </div>
    </div>
    <slot v-else></slot>
  </div>
</template>

<script setup>
import { defineProps, computed } from 'vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: true
  },
  text: {
    type: String,
    default: '加载中...'
  },
  type: {
    type: String,
    default: 'spinner',
    validator: (value) => {
      return ['spinner', 'dots', 'bars', 'pulse', 'grid'].includes(value)
    }
  },
  color: {
    type: String,
    default: 'var(--primary-color)'
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => {
      return ['small', 'medium', 'large'].includes(value)
    }
  },
  showIcon: {
    type: Boolean,
    default: false
  },
  icon: {
    type: String,
    default: '⏳'
  }
})

// 计算加载器大小
const loaderSize = computed(() => {
  const sizes = {
    small: '24px',
    medium: '40px',
    large: '60px'
  }
  return sizes[props.size]
})

// 计算文本大小
const textSize = computed(() => {
  const sizes = {
    small: 'var(--font-size-xs)',
    medium: 'var(--font-size-sm)',
    large: 'var(--font-size-md)'
  }
  return sizes[props.size]
})
</script>

<style scoped>
.chart-loader {
  position: relative;
  width: 100%;
  min-height: 300px;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.loader-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--card-background);
  animation: fadeIn 0.3s ease-out;
  transition: all 0.3s ease;
}

/* 基本加载器样式 */
.loader-spinner {
  width: v-bind(loaderSize);
  height: v-bind(loaderSize);
  border: 3px solid var(--border-color);
  border-top: 3px solid v-bind(color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

.loader-dots {
  display: flex;
  gap: 8px;
  margin-bottom: var(--spacing-md);
}

.loader-dots .dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: v-bind(color);
  animation: dotPulse 1.4s ease-in-out infinite both;
}

.loader-dots .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loader-dots .dot:nth-child(2) {
  animation-delay: -0.16s;
}

.loader-bars {
  display: flex;
  gap: 4px;
  margin-bottom: var(--spacing-md);
}

.loader-bars .bar {
  width: 8px;
  height: 40px;
  background-color: v-bind(color);
  border-radius: 4px;
  animation: barPulse 1.2s ease-in-out infinite both;
}

.loader-bars .bar:nth-child(1) {
  animation-delay: -0.48s;
}

.loader-bars .bar:nth-child(2) {
  animation-delay: -0.32s;
}

.loader-bars .bar:nth-child(3) {
  animation-delay: -0.16s;
}

.loader-pulse {
  width: v-bind(loaderSize);
  height: v-bind(loaderSize);
  background-color: v-bind(color);
  border-radius: 50%;
  animation: pulse 1.5s ease-in-out infinite;
  margin-bottom: var(--spacing-md);
}

.loader-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  margin-bottom: var(--spacing-md);
}

.loader-grid .grid-item {
  width: 20px;
  height: 20px;
  background-color: v-bind(color);
  border-radius: 4px;
  animation: gridPulse 1.5s ease-in-out infinite;
}

.loader-grid .grid-item:nth-child(1) {
  animation-delay: 0s;
}

.loader-grid .grid-item:nth-child(2) {
  animation-delay: 0.1s;
}

.loader-grid .grid-item:nth-child(3) {
  animation-delay: 0.2s;
}

.loader-grid .grid-item:nth-child(4) {
  animation-delay: 0.3s;
}

.loader-text {
  font-size: v-bind(textSize);
  color: var(--text-secondary);
  animation: textPulse 1.5s infinite;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.loader-text.with-icon {
  color: v-bind(color);
}

.loader-text .text-icon {
  font-size: 1.2em;
  animation: iconSpin 2s linear infinite;
}

/* 动画定义 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes dotPulse {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes barPulse {
  0%, 40%, 100% {
    transform: scaleY(0.4);
  }
  20% {
    transform: scaleY(1);
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  50% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
}

@keyframes gridPulse {
  0%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  50% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes textPulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

@keyframes iconSpin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .chart-loader {
    min-height: 200px;
  }
  
  .loader-spinner {
    width: 32px;
    height: 32px;
  }
  
  .loader-dots .dot {
    width: 10px;
    height: 10px;
  }
  
  .loader-bars .bar {
    height: 32px;
  }
  
  .loader-pulse {
    width: 32px;
    height: 32px;
  }
  
  .loader-text {
    font-size: var(--font-size-xs);
  }
}
</style>