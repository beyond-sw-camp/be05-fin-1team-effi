<template>
  <div class="container">
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

<script setup>
import { ref, computed } from 'vue';
import HomeHeader from '../components/HomeHeader.vue';
import WeekNavigator from '../components/WeekNavigator.vue';
import SearchList from '../components/SearchList.vue';
import { startOfWeek, endOfWeek, addWeeks } from 'date-fns';

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
</script>

<style scoped>
.controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  margin-bottom: 10px;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.status-sort {
  margin-left: auto;
  padding: 5px 15px;
  /* 크기를 작게 하기 위해 패딩 조정 */
  border: 1px solid #333;
  /* 테두리 두께 조정 */
  border-radius: 5px;
  background-color: #f4f4f4;
  cursor: pointer;
  font-size: 0.9rem;
  /* 텍스트 크기 조정 */
  transition: background-color 0.3s, border-color 0.3s;
}

.status-sort:hover {
  background-color: #e0e0e0;
  border-color: #000;
}

.week-navigator button {
  padding: 10px 20px;
  border: 2px solid #333;
  border-radius: 5px;
  background-color: #f4f4f4;
  cursor: pointer;
  margin: 0 10px;
  transition: background-color 0.3s, border-color 0.3s;
}

.week-navigator button:hover {
  background-color: #e0e0e0;
  border-color: #000;
}

.status-title {
  text-align: center;
  font-size: 1.2rem;
  margin: 20px 0;
}
</style>
