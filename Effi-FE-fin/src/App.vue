<template>
  <v-app>
    <VSidebar v-if="showVSidebar" @update-categories="handleUpdateCategories" @search-results="handleSearchResults"
      @update-groups="handleSelectedGroupId" />
    <div class="content">
      <router-view :selectedCategories="selectedCategories" :searchResults="searchResults"
        :selectedGroupId="selectedGroupId" />
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
    const showVSidebar = computed(() => route.name !== 'login');
    const selectedCategories = ref([]);
    const selectedGroupId = ref([]);
    const searchResults = ref([]);

    const handleUpdateCategories = (categories) => {
      console.log("Selected Categories in App.vue:", categories);
      selectedCategories.value = categories;
    };

    const handleSearchResults = (results) => {
      searchResults.value = results;
    };

    const handleSelectedGroupId = (groups) => {
      console.log('Selected Groups in App.vue:', groups);
      selectedGroupId.value = groups;
    };

    return {
      showVSidebar,
      selectedCategories,
      searchResults,
      selectedGroupId,
      handleUpdateCategories,
      handleSearchResults,
      handleSelectedGroupId,
    };
  },
});
</script>

<style>
#app {
  margin: 0;
  padding-top: 10px;
  /* 헤더의 높이만큼 패딩 추가 */
}

.header {
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 500;
  /* 헤더가 다른 요소 위에 위치하도록 설정 */
}

.content {
  padding: 0;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>