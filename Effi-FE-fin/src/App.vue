<!-- app.vue -->
<template>
  <div id="app">
    <AppHeader v-if="showHeader" />
    <div class="content">
      <router-view />
    </div>
  </div>
</template>

<script>
import { defineComponent, computed } from 'vue';
import { useRoute } from 'vue-router';
import AppHeader from '@/components/HomeHeader.vue';

export default defineComponent({
  components: {
    AppHeader,
  },
  setup() {
    const route = useRoute();
    const showHeader = computed(() => route.name !== 'login' && route.name !== 'test');

    return {
      showHeader,
    };
  },
});
</script>


<style>
#app {
  margin: 0;
  padding-top: 10px; /* 헤더의 높이만큼 패딩 추가 */
}

.header {
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 1000; /* 헤더가 다른 요소 위에 위치하도록 설정 */
}

.content {
  padding: 0;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>