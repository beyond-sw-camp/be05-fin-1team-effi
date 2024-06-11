<template>
  <div>
    <HomeHeader @search-results="updateSearches" />
    <div class="controls">
      <button @click="toggleStatusSort" class="status-sort">status</button>
      <WeekNavigator :currentWeek="currentWeek" @change-week="changeWeek" />
    </div>
    <div v-if="sortedByStatus">
      <div v-for="status in allStatuses" :key="status">
        <h2 class="status-title">{{ statusLabels[status] }}</h2>
        <SearchList :searches="sortedSearchesByStatus(status)" />
      </div>
    </div>
    <div v-else>
      <SearchList :searches="filteredSearches" />
    </div>
  </div>
</template>

<script>
import HomeHeader from '../components/HomeHeader.vue';
import WeekNavigator from '../components/WeekNavigator.vue';
import SearchList from '../components/SearchList.vue';
import { ref, computed } from 'vue';
import { startOfWeek, endOfWeek, addWeeks } from 'date-fns';

export default {
  components: {
    HomeHeader,
    WeekNavigator,
    SearchList
  },
  setup() {
    const searches = ref([]);
    const currentWeek = ref(new Date());
    const sortedByStatus = ref(false);

    const statusLabels = {
      '0': '예정됨',
      '1': '진행중',
      '2': '완료됨'
    };

    const allStatuses = ['0', '1', '2'];

    const filteredSearches = computed(() => {
      const start = startOfWeek(currentWeek.value, { weekStartsOn: 1 });
      const end = endOfWeek(currentWeek.value, { weekStartsOn: 1 });
      return searches.value.filter(schedule => {
        const startTime = new Date(schedule.startTime);
        return startTime >= start && startTime <= end;
      });
    });

    const sortedSearchesByStatus = (status) => {
      return filteredSearches.value.filter(schedule => schedule.status == status);
    };

    const updateSearches = (newSearches) => {
      searches.value = newSearches;
    };

    const changeWeek = (weeks) => {
      currentWeek.value = addWeeks(currentWeek.value, weeks);
    };

    const toggleStatusSort = () => {
      sortedByStatus.value = !sortedByStatus.value;
    };

    return {
      searches,
      currentWeek,
      sortedByStatus,
      filteredSearches,
      sortedSearchesByStatus,
      statusLabels,
      allStatuses,
      updateSearches,
      changeWeek,
      toggleStatusSort
    };
  }
};
</script>

<style scoped>
.controls {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.status-sort {
  margin-left: auto;
}

.status-title {
  text-align: center;
  font-size: 1.2rem;
  margin: 20px 0;
}
</style>
