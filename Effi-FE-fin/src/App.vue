<template>
  <v-app>
    <VSidebar v-if="showHeader" @search-results="handleSearchResults" />
    <div class="content" :search-results="searchResults">
      <router-view />
      <vue3-snackbar top right :duration="5000"></vue3-snackbar>
    </div>
  </v-app>
</template>

<script>
import { ref, defineComponent, computed } from 'vue';
import { useRoute } from 'vue-router';
import VSidebar from '@/components/VSidebar.vue';

export default defineComponent({
  components: {
    VSidebar,
  },
  setup() {
    const route = useRoute();
    const showHeader = computed(() => route.name !== 'login' && route.name !== 'test' && route.name !== 'schedule');
    const searchResults = ref([]);

    const handleSearchResults = (results) => {
      searchResults.value = results;
    };

    return {
      showHeader,
      searchResults,
      handleSearchResults,
    };
  },
});
</script>

<style>
#app {
  margin: 0;
  padding-top: 10px;
}

.header {
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 500;
}

.content {
  padding: 0;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
