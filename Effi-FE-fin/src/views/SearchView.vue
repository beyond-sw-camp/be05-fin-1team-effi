<template>
  <div class="container">
    <Navigation class="navigation" />
    <div class="content">
      <div class="controls d-flex align-items-center mb-3">
        <button @click="toggleStatusSort" class="btn btn-outline-primary me-3">status</button>
        <SearchNavigator :currentPeriod="currentPeriod" :viewMode="viewMode" @change-period="changePeriod"
          @change-view-mode="changeViewMode" />
      </div>
      <div v-if="sortedByStatus">
        <div v-for="status in sortedStatuses" :key="status">
          <h2 class="status-title">{{ statusLabels[status] }}</h2>
          <SearchList :searches="sortedSearchesByStatus(status)" />
        </div>
      </div>
      <div v-else>
        <SearchList :searches="filteredSearches" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import Navigation from '@/components/LeftSidebar.vue';
import SearchNavigator from '../components/SearchNavigator.vue';
import SearchList from '../components/SearchList.vue';
import { startOfWeek, endOfWeek, startOfDay, endOfDay, startOfMonth, endOfMonth } from 'date-fns';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const searches = ref([]);
const currentPeriod = ref(new Date());
const sortedByStatus = ref(false);
const viewMode = ref('week');
const route = useRoute();
const authStore = useAuthStore();
const sortStatus = ref(0); // 정렬 상태 변수: 0 = 원래 상태, 1 = 오름차순, 2 = 내림차순

const statusLabels = {
  '0': '예정됨',
  '1': '진행중',
  '2': '완료됨'
};

const allStatuses = ['0', '1', '2'];

const filteredSearches = computed(() => {
  let start, end;
  if (viewMode.value === 'day') {
    start = startOfDay(currentPeriod.value);
    end = endOfDay(currentPeriod.value);
  } else if (viewMode.value === 'week') {
    start = startOfWeek(currentPeriod.value, { weekStartsOn: 1 });
    end = endOfWeek(currentPeriod.value, { weekStartsOn: 1 });
  } else if (viewMode.value === 'month') {
    start = startOfMonth(currentPeriod.value);
    end = endOfMonth(currentPeriod.value);
  }
  return searches.value.filter(schedule => {
    const startTime = new Date(schedule.startTime);
    return startTime >= start && startTime <= end;
  });
});

const sortedStatuses = computed(() => {
  if (sortStatus.value === 1) {
    return ['0', '1', '2']; // 예정됨, 진행중, 완료됨
  } else if (sortStatus.value === 2) {
    return ['2', '1', '0']; // 완료됨, 진행중, 예정됨
  }
  return allStatuses;
});

const updateSearches = (newSearches) => {
  searches.value = newSearches;
};

onMounted(() => {
  if (route.query.criterion && route.query.query) {
    search(route.query.criterion, route.query.query);
  }
});

const search = async (criterion, query) => {
  let url = `/api/search/${criterion}?${criterion}=${encodeURIComponent(query)}`;
  if (criterion === 'title') {
    url = `/api/search/title?title=${encodeURIComponent(query)}`;
  } else if (criterion === 'tag') {
    url = `/api/search/tag?tagName=${encodeURIComponent(query)}`;
  } else if (criterion === 'category') {
    url = `/api/search/category?categoryName=${encodeURIComponent(query)}`;
  }

  try {
    const response = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`
      }
    });
    const searches = response.data;
    updateSearches(searches);
  } catch (error) {
    console.error('Error during search:', error);
  }
};

watch(currentPeriod, (newVal) => {
  currentPeriod.value = newVal;
});

watch(viewMode, (newVal) => {
  viewMode.value = newVal;
});

const sortedSearchesByStatus = (status) => {
  return filteredSearches.value.filter(schedule => schedule.status == status);
};

const changePeriod = (newPeriod) => {
  currentPeriod.value = newPeriod;
};

const changeViewMode = (mode) => {
  viewMode.value = mode;
};

const toggleStatusSort = () => {
  sortStatus.value = (sortStatus.value + 1) % 3;
  sortedByStatus.value = sortStatus.value !== 0;
};
</script>

<style scoped>
.container {
  display: flex;
  margin-top: 60px;
  height: calc(100vh - 60px);
  width: 100%;
}

.navigation {
  flex: 0 0 250px;
  background-color: #FBB584;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  margin-bottom: 10px;
}

.status-sort {
  padding: 5px 15px;
  border: 1px solid #333;
  border-radius: 5px;
  background-color: #f4f4f4;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s, border-color 0.3s;
  margin-right: 10px;
  /* 간격 추가 */
}

.status-sort:hover {
  background-color: #e0e0e0;
  border-color: #000;
}

.status-title {
  text-align: center;
  font-size: 1.2rem;
  margin: 20px 0;
  font-weight: bold;
  /* 글자 진하게 */
}

/* 테이블 스타일 수정 */
.search-table th {
  background-color: #f4f4f4;
  color: #333399;
  text-align: center;
  border-bottom: 2px solid #ddd;
}

.search-table th.date,
.search-table td.date {
  width: 15%;
}

.search-table th.category,
.search-table td.category {
  width: 10%;
}

.search-table th.status,
.search-table td.status {
  width: 10%;
}

.search-table th.title,
.search-table td.title {
  width: 35%;
}

.search-table th.tags,
.search-table td.tags {
  width: 30%;
}

.search-table tbody tr td {
  border-top: 1px solid #ddd;
}
</style>
