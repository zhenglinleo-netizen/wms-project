<template>
  <div ref="lazyRef" style="width: 100%;">
    <!-- 占位符：使用Element Plus的Skeleton组件 -->
    <el-skeleton v-if="!isVisible" :rows="6" animated>
      <template #template>
        <el-skeleton-item variant="image" style="width: 100%; height: 200px; border-radius: 8px;" />
        <el-skeleton-item variant="p" style="margin-top: 15px;" />
        <el-skeleton-item variant="p" style="width: 60%;" />
        <el-skeleton-item variant="p" style="width: 80%;" />
        <el-skeleton-item variant="p" style="width: 40%;" />
      </template>
    </el-skeleton>
    
    <!-- 实际内容：当元素进入视口时显示 -->
    <transition name="fade" mode="out-in">
      <div v-if="isVisible" style="width: 100%;">
        <slot />
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  // 提前加载距离（默认为100px）
  rootMargin: {
    type: String,
    default: '0px 0px 100px 0px'
  }
})

const emit = defineEmits(['load'])

const lazyRef = ref(null)
const isVisible = ref(false)
let observer = null

onMounted(() => {
  // 检查Intersection Observer是否支持
  if ('IntersectionObserver' in window) {
    observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            // 元素进入视口
            isVisible.value = true
            emit('load')
            // 停止观察
            if (observer) {
              observer.disconnect()
            }
          }
        })
      },
      {
        rootMargin: props.rootMargin
      }
    )

    if (lazyRef.value) {
      observer.observe(lazyRef.value)
    }
  } else {
    // 浏览器不支持Intersection Observer，直接显示
    isVisible.value = true
  }
})

onUnmounted(() => {
  // 清理观察器
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>