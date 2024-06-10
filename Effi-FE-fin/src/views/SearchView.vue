<!-- src/views/SearchView.vue -->
<template>
  <div>
    <HomeHeader @search-results="updateSearches" />
    <StatusFilter @filter-status="updateStatusFilter" />
    <WeekNavigator :startDate="startDate" @update-date="updateDate" />
    <div v-if="showGrouped">
      <div v-if="groupedSearches['예정됨'].length">
        <h2>예정됨</h2>
        <SearchList :searches="groupedSearches['예정됨']" />
      </div>
      <div v-if="groupedSearches['진행중'].length">
        <h2>진행중</h2>
        <SearchList :searches="groupedSearches['진행중']" />
      </div>
      <div v-if="groupedSearches['완료됨'].length">
        <h2>완료됨</h2>
        <SearchList :searches="groupedSearches['완료됨']" />
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
import SearchList from '../components/SearchList.vue';
import WeekNavigator from '../components/WeekNavigator.vue';
import StatusFilter from '../components/StatusFilter.vue';
import { sortSchedulesByStartDate } from '../filters/filters';

const searches = ref([]);
const startDate = ref(new Date());
const statusFilter = ref(null);
const showGrouped = ref(false);

const updateSearches = (newSearches) => {
  searches.value = sortSchedulesByStartDate(newSearches);
  showGrouped.value = false;
};

const updateDate = (date) => {
  startDate.value = date;
};

const updateStatusFilter = (status) => {
  statusFilter.value = status;
  showGrouped.value = true;
};

const filteredSearches = computed(() => {
  const endDate = new Date(startDate.value);
  endDate.setDate(startDate.value.getDate() + 6);

  return searches.value.filter(search => {
    const searchStartDate = new Date(search.startTime);
    const matchesDate = searchStartDate >= startDate.value && searchStartDate <= endDate;
    const matchesStatus = statusFilter.value === null || search.status === statusFilter.value;
    return matchesDate && matchesStatus;
  });
});

const groupedSearches = computed(() => {
  const groups = {
    '예정됨': [],
    '진행중': [],
    '완료됨': []
  };

  filteredSearches.value.forEach(search => {
    switch (search.status) {
      case 0:
        groups['예정됨'].push(search);
        break;
      case 1:
        groups['진행중'].push(search);
        break;
      case 2:
        groups['완료됨'].push(search);
        break;
    }
  });

  return groups;
});
</script>
