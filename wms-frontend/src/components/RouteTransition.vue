<template>
  <transition 
    :name="transitionName" 
    mode="out-in" 
    :duration="transitionDuration"
    :enter-active-class="enterActiveClass"
    :leave-active-class="leaveActiveClass"
    :enter-from-class="enterFromClass"
    :leave-to-class="leaveToClass"
  >
    <slot></slot>
  </transition>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  defaultTransition: {
    type: String,
    default: 'fade'
  },
  duration: {
    type: Number,
    default: 300
  },
  // 可以为特定路由配置不同的过渡动画
  routeTransitions: {
    type: Object,
    default: () => ({})
  },
  // 自定义动画类名
  customClasses: {
    type: Object,
    default: () => ({})
  }
})

const route = useRoute()

// 根据路由配置或默认值计算过渡动画名称
const transitionName = computed(() => {
  return props.routeTransitions[route.name] || props.defaultTransition
})

// 计算过渡动画持续时间
const transitionDuration = computed(() => {
  return props.duration
})

// 计算动画类名
const enterActiveClass = computed(() => {
  return props.customClasses.enterActive || `${transitionName.value}-enter-active`
})

const leaveActiveClass = computed(() => {
  return props.customClasses.leaveActive || `${transitionName.value}-leave-active`
})

const enterFromClass = computed(() => {
  return props.customClasses.enterFrom || `${transitionName.value}-enter-from`
})

const leaveToClass = computed(() => {
  return props.customClasses.leaveTo || `${transitionName.value}-leave-to`
})
</script>

<style scoped>
/* 组件特定的动画样式 */

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 向上滑动动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from {
  transform: translateY(30px);
  opacity: 0;
}

.slide-up-leave-to {
  transform: translateY(-30px);
  opacity: 0;
}

/* 向左滑动动画 */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-left-enter-from {
  transform: translateX(30px);
  opacity: 0;
}

.slide-left-leave-to {
  transform: translateX(-30px);
  opacity: 0;
}

/* 向右滑动动画 */
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-right-enter-from {
  transform: translateX(-30px);
  opacity: 0;
}

.slide-right-leave-to {
  transform: translateX(30px);
  opacity: 0;
}

/* 缩放动画 */
.scale-enter-active,
.scale-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.scale-enter-from,
.scale-leave-to {
  transform: scale(0.95);
  opacity: 0;
}

/* 弹跳动画 */
.bounce-enter-active {
  animation: bounce-in 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.bounce-leave-active {
  animation: bounce-out 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes bounce-in {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes bounce-out {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
  100% {
    transform: scale(0.8);
    opacity: 0;
  }
}

/* 弹性动画 */
.elastic-enter-active {
  animation: elastic-in 0.8s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.elastic-leave-active {
  animation: elastic-out 0.8s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes elastic-in {
  0% {
    transform: scale(0.3);
    opacity: 0;
  }
  40% {
    transform: scale(1.1);
    opacity: 1;
  }
  60% {
    transform: scale(0.9);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes elastic-out {
  0% {
    transform: scale(1);
  }
  40% {
    transform: scale(0.9);
  }
  60% {
    transform: scale(1.1);
    opacity: 1;
  }
  100% {
    transform: scale(0.3);
    opacity: 0;
  }
}

/* 旋转动画 */
.rotate-enter-active,
.rotate-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.rotate-enter-from {
  transform: rotate(-10deg) scale(0.9);
  opacity: 0;
}

.rotate-leave-to {
  transform: rotate(10deg) scale(0.9);
  opacity: 0;
}
</style>