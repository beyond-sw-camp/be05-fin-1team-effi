<template>
  <div class="container">
    <Navigation class="navigation" />
    <div class="content">
      <div class="controls d-flex align-items-center mb-3 flex-wrap">
        <div class="timezone-container d-flex align-items-center me-auto mb-2 mb-md-0">
          <i class="bi bi-globe me-2"></i>
          <span>{{ timezoneName }}</span>
        </div>
        <div class="d-flex align-items-center me-3 mb-2 mb-md-0 nowrap">
          <span class="me-2">진행 상태</span>
          <select v-model="selectedStatus" class="form-select">
            <option value="all">전체</option>
            <option value="0">예정됨</option>
            <option value="1">진행중</option>
            <option value="2">완료됨</option>
          </select>
          </div>
          <SearchNavigator :currentPeriod="currentPeriod" :viewMode="viewMode" @change-period="changePeriod"
            @change-view-mode="changeViewMode" />
        </div>
        <div>
          <SearchList :searches="filteredSearchesByStatus" />
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
import axiosInstance from '@/services/axios';
import { useAuthStore } from '@/stores/auth';

const searches = ref([]);
const currentPeriod = ref(new Date());
const viewMode = ref('week');
const selectedStatus = ref('all'); // 선택된 상태
const route = useRoute();
const authStore = useAuthStore();
const timezoneName = ref('');

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

const filteredSearchesByStatus = computed(() => {
  if (selectedStatus.value === 'all') {
    return filteredSearches.value;
  }
  return filteredSearches.value.filter(schedule => schedule.status == selectedStatus.value);
});

const updateSearches = (newSearches) => {
  searches.value = newSearches;
};

const fetchTimezone = async () => {
  const accessToken = sessionStorage.getItem('accessToken');
  if (!accessToken) {
    console.error('Token not found');
    return;
  }
  try {
    const response = await axiosInstance.get('/api/mypage/me', {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    timezoneName.value = response.data.timezoneName;
  } catch (error) {
    console.error('Error fetching timezone:', error.response ? error.response.data : error.message);
  }
};

onMounted(() => {
  if (route.query.criterion && route.query.query) {
    search(route.query.criterion, route.query.query);
    fetchTimezone();
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
    const response = await axiosInstance.get(url, {
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

const changePeriod = (newPeriod) => {
  currentPeriod.value = newPeriod;
};

const changeViewMode = (mode) => {
  viewMode.value = mode;
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
  flex-wrap: wrap;
  /* 컨트롤을 줄바꿈할 수 있도록 수정 */
}

.nowrap {
  white-space: nowrap;
  
}

.timezone-container {
  margin-right: 10px;
}

.timezone-container i {
  font-size: 1.5rem;
}

.timezone-container span {
  margin-right: 10px;
}

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

@media (max-width: 768px) {
  .navigation {
    display: none;
  }

  .controls {
    flex-direction: column;
    align-items: stretch;
  }

  .controls>* {
    width: 100%;
    margin-bottom: 10px;
  }

  .status-sort {
    margin-right: 0;
  }
}
</style>
