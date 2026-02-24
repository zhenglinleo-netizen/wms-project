<template>
  <span 
    class="animated-number"
    :class="{
      'is-animating': isAnimating,
      'is-increasing': isIncreasing,
      'is-decreasing': isDecreasing
    }"
    :style="animatedStyle"
  >
    {{ formattedValue }}
  </span>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
  value: {
    type: [Number, String],
    required: true
  },
  duration: {
    type: Number,
    default: 1000
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
  easing: {
    type: String,
    default: 'easeOutCubic',
    validator: (value) => {
      return ['linear', 'easeInQuad', 'easeOutQuad', 'easeInOutQuad', 'easeInCubic', 'easeOutCubic', 'easeInOutCubic', 'easeInQuart', 'easeOutQuart', 'easeInOutQuart', 'easeInQuint', 'easeOutQuint', 'easeInOutQuint', 'easeInSine', 'easeOutSine', 'easeInOutSine', 'easeInExpo', 'easeOutExpo', 'easeInOutExpo', 'easeInCirc', 'easeOutCirc', 'easeInOutCirc', 'easeInBack', 'easeOutBack', 'easeInOutBack', 'easeInElastic', 'easeOutElastic', 'easeInOutElastic', 'easeInBounce', 'easeOutBounce', 'easeInOutBounce'].includes(value)
    }
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
  useScale: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['animation-start', 'animation-update', 'animation-end'])

const currentValue = ref(0)
const startValue = ref(0)
const startTime = ref(0)
const isAnimating = ref(false)
const animationId = ref(null)

// 计算目标值
const targetValue = computed(() => {
  return Number(props.value) || 0
})

// 计算是否正在增加或减少
const isIncreasing = computed(() => {
  return targetValue.value > startValue.value
})

const isDecreasing = computed(() => {
  return targetValue.value < startValue.value
})

// 计算动画样式
const animatedStyle = computed(() => {
  const style = {}
  
  if (props.useColor) {
    if (isAnimating.value) {
      if (isIncreasing.value) {
        style.color = props.increasingColor
      } else if (isDecreasing.value) {
        style.color = props.decreasingColor
      }
    }
  }
  
  if (props.useScale && isAnimating.value) {
    style.transform = 'scale(1.1)'
  }
  
  return style
})

// 格式化显示值
const formattedValue = computed(() => {
  if (props.formatter) {
    return props.formatter(currentValue.value)
  }
  
  const roundedValue = Number(currentValue.value).toFixed(props.precision)
  return `${props.prefix}${roundedValue}${props.suffix}`
})

// 缓动函数集合
const easingFunctions = {
  linear: (t) => t,
  easeInQuad: (t) => t * t,
  easeOutQuad: (t) => t * (2 - t),
  easeInOutQuad: (t) => t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t,
  easeInCubic: (t) => t * t * t,
  easeOutCubic: (t) => (--t) * t * t + 1,
  easeInOutCubic: (t) => t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1,
  easeInQuart: (t) => t * t * t * t,
  easeOutQuart: (t) => 1 - (--t) * t * t * t,
  easeInOutQuart: (t) => t < 0.5 ? 8 * t * t * t * t : 1 - 8 * (--t) * t * t * t,
  easeInQuint: (t) => t * t * t * t * t,
  easeOutQuint: (t) => 1 + (--t) * t * t * t * t,
  easeInOutQuint: (t) => t < 0.5 ? 16 * t * t * t * t * t : 1 + 16 * (--t) * t * t * t * t,
  easeInSine: (t) => 1 - Math.cos(t * Math.PI / 2),
  easeOutSine: (t) => Math.sin(t * Math.PI / 2),
  easeInOutSine: (t) => (1 - Math.cos(Math.PI * t)) / 2,
  easeInExpo: (t) => Math.pow(2, 10 * (t - 1)),
  easeOutExpo: (t) => 1 - Math.pow(2, -10 * t),
  easeInOutExpo: (t) => t < 0.5 ? Math.pow(2, 20 * t - 10) / 2 : (2 - Math.pow(2, -20 * t + 10)) / 2,
  easeInCirc: (t) => 1 - Math.sqrt(1 - t * t),
  easeOutCirc: (t) => Math.sqrt(1 - (t - 1) * (t - 1)),
  easeInOutCirc: (t) => t < 0.5 ? (1 - Math.sqrt(1 - 4 * t * t)) / 2 : (Math.sqrt(1 - (-2 * t + 2) * (-2 * t + 2)) + 1) / 2,
  easeInBack: (t) => 2.70158 * t * t * t - 1.70158 * t * t,
  easeOutBack: (t) => 1 + 2.70158 * (t - 1) * (t - 1) * (t - 1) + 1.70158 * (t - 1) * (t - 1),
  easeInOutBack: (t) => t < 0.5 ? (Math.pow(2 * t, 2) * ((2.5949095 + 1) * 2 * t - 2.5949095)) / 2 : (Math.pow(2 * t - 2, 2) * ((2.5949095 + 1) * (t * 2 - 2) + 2.5949095) + 2) / 2,
  easeInElastic: (t) => -(Math.pow(2, 10 * (t - 1)) * Math.sin((t - 1.1) * (2 * Math.PI) / 0.4)),
  easeOutElastic: (t) => Math.pow(2, -10 * t) * Math.sin((t - 0.1) * (2 * Math.PI) / 0.4) + 1,
  easeInOutElastic: (t) => t < 0.5 ? -(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * (2 * Math.PI) / 1.75)) / 2 : Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * (2 * Math.PI) / 1.75) / 2 + 1,
  easeInBounce: (t) => 1 - easingFunctions.easeOutBounce(1 - t),
  easeOutBounce: (t) => {
    if (t < 1 / 2.75) {
      return 7.5625 * t * t
    } else if (t < 2 / 2.75) {
      return 7.5625 * (t -= 1.5 / 2.75) * t + 0.75
    } else if (t < 2.5 / 2.75) {
      return 7.5625 * (t -= 2.25 / 2.75) * t + 0.9375
    } else {
      return 7.5625 * (t -= 2.625 / 2.75) * t + 0.984375
    }
  },
  easeInOutBounce: (t) => t < 0.5 ? (1 - easingFunctions.easeOutBounce(1 - 2 * t)) / 2 : (1 + easingFunctions.easeOutBounce(2 * t - 1)) / 2
}

// 动画函数
const animateValue = (timestamp) => {
  if (!startTime.value) {
    startTime.value = timestamp
    isAnimating.value = true
    emit('animation-start', { startValue: startValue.value, targetValue: targetValue.value })
  }
  
  const progress = Math.min((timestamp - startTime.value) / props.duration, 1)
  // 使用选择的缓动函数
  const easedProgress = easingFunctions[props.easing](progress)
  
  const value = startValue.value + (targetValue.value - startValue.value) * easedProgress
  currentValue.value = value
  
  emit('animation-update', { value, progress: easedProgress })
  
  if (progress < 1) {
    animationId.value = requestAnimationFrame(animateValue)
  } else {
    currentValue.value = targetValue.value
    isAnimating.value = false
    emit('animation-end', { value: targetValue.value })
  }
}

// 启动动画
const startAnimation = () => {
  // 取消之前的动画
  if (animationId.value) {
    cancelAnimationFrame(animationId.value)
    animationId.value = null
  }
  
  startValue.value = Number(currentValue.value) || 0
  startTime.value = 0
  requestAnimationFrame(animateValue)
}

// 监听值变化，重新启动动画
watch(() => props.value, () => {
  startAnimation()
}, { immediate: true })

// 监听其他属性变化
watch([() => props.duration, () => props.precision, () => props.prefix, () => props.suffix, () => props.easing], () => {
  // 这些属性变化时不需要重新启动动画，只需要更新显示
})

// 组件挂载时启动动画
onMounted(() => {
  startAnimation()
})
</script>

<style scoped>
.animated-number {
  font-variant-numeric: tabular-nums;
  transition: all 0.3s ease;
  display: inline-block;
}

.animated-number.is-animating {
  font-weight: 600;
  transition: all 0.2s ease;
}

.animated-number.is-increasing {
  animation: pulseIn 0.5s ease-in-out;
}

.animated-number.is-decreasing {
  animation: pulseOut 0.5s ease-in-out;
}

@keyframes pulseIn {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes pulseOut {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.9);
  }
  100% {
    transform: scale(1);
  }
}
</style>