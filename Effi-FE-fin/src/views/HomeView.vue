<template>
  <div class="container">
    <Navigation class="navigation"/>
    <!-- 여기에 다른 컨텐츠 추가 -->
  </div>
</template>

<script>
import Navigation from '@/components/LeftSidebar.vue'
export default {
  components: {
    Navigation
  }
}
</script>

<style>
.container {
  display: flex;
}

.navigation {
  width: 200px; /* 내비게이션의 너비 설정 */
  height: 100vh; /* 전체 높이 */
  position: fixed; /* 고정 위치 */
  left: 0; /* 왼쪽에 위치 */
}
</style>
