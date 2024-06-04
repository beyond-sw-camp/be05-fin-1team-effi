<template>
  <div>
    <AppHeader class="header" />
    <div class="container">
      <Navigation class="navigation" />
      <div class="content">
        <VCalendar />
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import Navigation from '@/components/LeftSidebar.vue';
import AppHeader from '@/components/HomeHeader.vue';
import VCalendar from '@/components/VCalendar.vue';

export default defineComponent({
  components: {
    Navigation,
    AppHeader,
    VCalendar,
  },
});
</script>

<style>
.header {
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 1000; /* 헤더가 다른 요소 위에 위치하도록 설정 */
}

.container {
  display: flex;
  margin-top: 60px; /* 헤더 높이만큼의 여백을 추가 */
}

.navigation {
  width: 200px; /* 내비게이션의 너비 설정 */
  height: calc(100vh - 60px); /* 전체 높이에서 헤더 높이를 뺀 높이 */
  position: fixed; /* 고정 위치 */
  left: 0; /* 왼쪽에 위치 */
  top: 60px; /* 헤더 높이만큼 아래에 위치 */
}

.content {
  margin-left: 200px; /* 내비게이션 너비만큼의 여백을 추가 */
  padding: 20px;
  flex-grow: 1; /* 남은 공간을 채우도록 설정 */
  width: calc(100% - 200px); /* 내비게이션을 제외한 너비 */
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .navigation {
    width: 100%;
    height: auto;
    position: relative;
    top: 0;
    left: 0;
  }
  .container {
    flex-direction: column;
  }
  .content {
    margin-left: 0;
    padding: 10px;
    width: 100%;
  }
}
</style>
