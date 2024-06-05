<template>
  <div>
    <AppHeader v-if="showHeader" class="header" />
    <router-view />
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
    const showHeader = computed(() => route.name !== 'login');

    return {
      showHeader,
    };
  },
});
</script>

<style>
#app {
  margin:0
}
.header {
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 1000; /* 헤더가 다른 요소 위에 위치하도록 설정 */
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
