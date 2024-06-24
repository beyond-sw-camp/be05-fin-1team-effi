<template>
  <div id="app">
    <div class="timezone">
      <button class="add-timezone-button" @click="showTimezoneModal = true">+</button>
      <div class="timezone-display" v-if="selectedTimezones.length">
        <div class="timezone-row">
          <div class="timezone-column" v-for="timezone in selectedTimezones" :key="timezone.timezoneId">
            <div class="timezone-header">
              {{ timezone.timezoneName }}
            </div>
            <div class="timezone-time" v-for="hour in hours" :key="hour">
              {{ formatTime(hour, timezone.offset) }}
            </div>
          </div>
        </div>
      </div>
      <TimezoneModal :show="showTimezoneModal" @close="showTimezoneModal = false" @add-timezone="addTimezone" />
    </div>
  </div>
</template>

<script>
import TimezoneModal from '@/components/TimezoneModal.vue';
import axiosInstance from '@/services/axios';
import { ref, onMounted } from 'vue';

export default {
  components: {
    TimezoneModal
  },
  setup() {
    const showTimezoneModal = ref(false);
    const selectedTimezones = ref([]);
    const hours = ref([...Array(24).keys()]); // 0 to 23 hours

    const empId = sessionStorage.getItem('empNo');

    const fetchTimezones = async () => {
      try {
        const response = await axiosInstance.get(`/api/timezone-emp/${empId}`);
        const allTimezones = response.data.data.timezones;

        // 기본 타임존 제외 (기본 타임존이 "Asia/Seoul"인 경우만 제외)
        const defaultTimezoneResponse = await axiosInstance.get(`/api/timezone-emp/${empId}/default`);
        const defaultTimezone = defaultTimezoneResponse.data.data;

        if (defaultTimezone.timezoneName === "Asia/Seoul") {
          selectedTimezones.value = allTimezones.filter(
            (timezone) => timezone.timezoneName !== "Asia/Seoul"
          );
        } else {
          selectedTimezones.value = allTimezones;
        }
      } catch (error) {
        console.error('타임존을 불러오는 데 실패했습니다:', error);
      }
    };

    onMounted(() => {
      fetchTimezones();
    });

    const formatTime = (hour, offset) => {
      const seoulTime = new Date();
      seoulTime.setHours(hour);

      const utcTime = seoulTime.getUTCHours(); // UTC 시간으로 변환
      const targetTime = (utcTime + offset) % 24; // 타임존 오프셋 적용

      return targetTime.toString().padStart(2, '0');
    };

    const addTimezone = async (timezone) => {
      try {
        await axiosInstance.post(`/api/timezone-emp/${empId}/add`, null, {
          params: {
            timezoneId: timezone.timezoneId
          }
        });
        selectedTimezones.value.push(timezone);
      } catch (error) {
        console.error('타임존을 추가하는 데 실패했습니다:', error);
      }
    };

    return {
      showTimezoneModal,
      selectedTimezones,
      hours,
      formatTime,
      addTimezone
    };
  }
};
</script>

<style scoped>
button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.add-timezone-button {
  background-color: #FBB584;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  color: white;
  font-weight: bold;
  margin-bottom: 16px;
}

.timezone-display {
  display: flex;
  flex-direction: column;
}

.timezone-row {
  display: flex;
}

.timezone-column {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 16px;
}

.timezone-header {
  font-weight: bold;
  margin-bottom: 8px;
}

.timezone-time {
  margin-bottom: 4px;
  text-align: center;
}
</style>
