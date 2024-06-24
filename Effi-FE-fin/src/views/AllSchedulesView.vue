<template>
  <div class="container">
    <Navigation class="navigation" />
    <div class="content">
      <div class="controls d-flex align-items-center mb-3 flex-wrap">
        <div class="timezone-container d-flex align-items-center me-auto mb-2 mb-md-0">
          <i class="bi bi-globe me-2"></i>
          <span>{{ timezoneName }}</span>
        </div>
        <button @click="toggleStatusSort" class="btn btn-outline-primary me-3 mb-2 mb-md-0">status</button>
        <SearchNavigator :currentPeriod="currentPeriod" :viewMode="viewMode" @change-period="changePeriod"
          @change-view-mode="changeViewMode" />
      </div>
      <h1>전체 일정</h1>
      <div v-if="sortedByStatus">
        <div v-for="status in sortedStatuses" :key="status">
          <h2 class="status-title">{{ getStatusLabel(status) }}</h2>
          <AllSchedulesList :schedules="sortedSchedulesByStatus(status)" />
        </div>
      </div>
      <div v-else>
        <AllSchedulesList :schedules="filteredSchedules" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import Navigation from '@/components/LeftSidebar.vue';
import SearchNavigator from '@/components/SearchNavigator.vue';
import AllSchedulesList from '@/components/AllSchedulesList.vue';
import axiosInstance from '@/services/axios';
import { useAuthStore } from '@/stores/auth';
import { startOfWeek, endOfWeek, startOfDay, endOfDay, startOfMonth, endOfMonth } from 'date-fns';

const allSchedules = ref([]);
const timezoneName = ref('');
const currentPeriod = ref(new Date());
const viewMode = ref('week');
const sortedByStatus = ref(false);
const sortStatus = ref(0); // 정렬 상태 변수: 0 = 원래 상태, 1 = 오름차순, 2 = 내림차순
const authStore = useAuthStore();

const fetchAllSchedules = async () => {
  try {
    const response = await axiosInstance.get('/api/schedule/findAllForSearch', {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`,
      },
    });
    console.log('Schedules fetched:', response.data); // 응답 데이터 구조 확인
    allSchedules.value = response.data;
  } catch (error) {
    console.error('Error fetching schedules:', error);
  }
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

const changePeriod = (newPeriod) => {
  currentPeriod.value = newPeriod;
};

const changeViewMode = (mode) => {
  viewMode.value = mode;
};

const filteredSchedules = computed(() => {
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
  return allSchedules.value.filter(schedule => {
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
  return ['0', '1', '2'];
});

const sortedSchedulesByStatus = (status) => {
  return filteredSchedules.value.filter(schedule => schedule.status == status);
};

const getStatusLabel = (status) => {
  switch (status) {
    case '0':
      return '예정됨';
    case '1':
      return '진행중';
    case '2':
      return '완료됨';
    default:
      return '알 수 없음';
  }
};

const toggleStatusSort = () => {
  sortStatus.value = (sortStatus.value + 1) % 3;
  sortedByStatus.value = sortStatus.value !== 0;
};

onMounted(() => {
  fetchAllSchedules();
  fetchTimezone();
});

watch(allSchedules, (newVal) => {
  console.log('All schedules updated:', newVal);
});
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
