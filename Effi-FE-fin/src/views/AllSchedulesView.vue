<template>
  <div class="container">
    <div class="content">
      <div class="controls d-flex align-items-center mb-3 flex-wrap">
        <div class="timezone-container d-flex align-items-center me-auto mb-2 mb-md-0">
          <i class="bi bi-globe me-2"></i>
          <span>{{ timezoneName }}</span>
        </div>
        <div class="d-flex align-items-center me-3 mb-2 mb-md-0 nowrap">
          <span class="me-2"><strong>상태</strong></span>
          <select v-model="selectedStatus" class="form-select" @change="filterByStatus">
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
        <AllSchedulesList :schedules="filteredSchedulesByStatus" @edit-schedule="showEditScheduleModal" />
      </div>
      <EditScheduleModal v-if="showModal" :show="showModal" :schedule-id="selectedScheduleId" @close="showModal = false"
        @update-schedule="handleScheduleUpdate" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import SearchNavigator from '@/components/SearchNavigator.vue';
import AllSchedulesList from '@/components/AllSchedulesList.vue';
import EditScheduleModal from '@/components/EditScheduleModal.vue';
import axiosInstance from '@/services/axios';
import { useAuthStore } from '@/stores/auth';
import { startOfWeek, endOfWeek, startOfDay, endOfDay, startOfMonth, endOfMonth } from 'date-fns';

const allSchedules = ref([]);
const timezoneName = ref('');
const currentPeriod = ref(new Date());
const viewMode = ref('week');
const selectedStatus = ref('all');
const authStore = useAuthStore();
const showModal = ref(false);
const selectedScheduleId = ref(null);

const fetchAllSchedules = async () => {
  try {
    const response = await axiosInstance.get('/api/schedule/findAllForSearch', {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`,
      },
    });
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
  } else if (viewMode.value === 'all') {
    return [...allSchedules.value].sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
  }

  return [...allSchedules.value].filter(schedule => {
    const startTime = new Date(schedule.startTime);
    return startTime >= start && startTime <= end;
  }).sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
});

const filteredSchedulesByStatus = computed(() => {
  if (selectedStatus.value === 'all') {
    return filteredSchedules.value;
  }
  return filteredSchedules.value.filter(schedule => schedule.status == selectedStatus.value);
});

const showEditScheduleModal = (scheduleId) => {
  selectedScheduleId.value = scheduleId;
  showModal.value = true;
};

const handleScheduleUpdate = () => {
  fetchAllSchedules();
  showModal.value = false;
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
  margin-left: 80px;
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

.nowrap {
  white-space: nowrap;
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
}
</style>
