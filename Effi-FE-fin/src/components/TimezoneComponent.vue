<template>
  <div id="app">
    <div class="timezone">
      <button class="add-timezone-button" @click="showTimezoneModal = true">+</button>
      <div class="timezone-display">
        <div class="timezone-row">
          <div v-for="timezone in selectedTimezones" :key="timezone.timezoneId" class="timezone-column">
            <div class="timezone-header">
              {{ timezone.timezoneName }}
            </div>
            <div class="timezone-time" v-for="hour in hours" :key="hour">
              {{ formatTime(hour, timezone.gmtOffset) }}
            </div>
          </div>
          <div class="timezone-column">
            <div class="timezone-header">
              {{ defaultTimezone.timezoneName }}
            </div>
            <div class="timezone-time" v-for="hour in amPmHours" :key="hour">
              {{ hour }}
            </div>
          </div>
        </div>
      </div>
      <TimezoneModal :show="showTimezoneModal" @close="showTimezoneModal = false" @add-timezone="handleAddTimezone" @remove-timezone="handleRemoveTimezone" />
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
    const defaultTimezone = ref({});
    const hours = ref([...Array(24).keys()]); // 0 to 23 hours
    const amPmHours = ref([
      'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'PM',
      'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'AM'
    ]);

    const empId = sessionStorage.getItem('empNo');

    const fetchTimezones = async () => {
      try {
        const response = await axiosInstance.get(`/api/timezone-emp/${empId}`);
        const allTimezones = response.data.data.timezones;

        // 기본 타임존 조회
        const defaultTimezoneResponse = await axiosInstance.get(`/api/timezone-emp/${empId}/default`);
        defaultTimezone.value = defaultTimezoneResponse.data.data;

        selectedTimezones.value = allTimezones.filter(
          (timezone) => timezone.timezoneId !== defaultTimezone.value.timezoneId
        );

        console.log("Selected Timezones:", selectedTimezones.value);
        console.log("Default Timezone:", defaultTimezone.value);
      } catch (error) {
        console.error('타임존을 불러오는 데 실패했습니다:', error);
      }
    };

    onMounted(() => {
      fetchTimezones();
    });

    const formatTime = (hour, offset) => {
      const seoulOffset = 9 * 60 * 60; // Asia/Seoul 타임존의 오프셋 (초 단위)
      const totalOffset = offset - seoulOffset; // 타임존 오프셋과 Seoul 오프셋의 차이
      const targetTime = (hour * 60 * 60 + totalOffset) / 3600; // 초 단위를 시간 단위로 변환

      let formattedTime = Math.floor(targetTime);
      if (formattedTime < 0) formattedTime += 24;
      if (formattedTime >= 24) formattedTime -= 24;

      const period = formattedTime < 12 ? 'AM' : 'PM';
      const displayHour = formattedTime % 12 === 0 ? 12 : formattedTime % 12;
      const formattedHour = displayHour.toString().padStart(2, '0');

      console.log(`Hour: ${hour}, Offset: ${offset}, Target Time: ${formattedTime}, Display Hour: ${formattedHour} ${period}`);

      return `${formattedHour} ${period}`;
    };

    const handleAddTimezone = (timezone) => {
      selectedTimezones.value.push(timezone);
    };

    const handleRemoveTimezone = (timezoneId) => {
      selectedTimezones.value = selectedTimezones.value.filter(
        timezone => timezone.timezoneId !== timezoneId
      );
    };

    return {
      showTimezoneModal,
      selectedTimezones,
      defaultTimezone,
      hours,
      amPmHours,
      formatTime,
      handleAddTimezone,
      handleRemoveTimezone
    };
  }
};
</script>

<style scoped>
.timezone{
  margin-top: 80px;
}
button {
  padding: 10px 20px;
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
  flex-direction: row;
}

.timezone-row {
  display: flex;
  margin-right: 0;
  margin-top:10px;
}

.timezone-column {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 16px;
}

.timezone-header {
  font-weight: bold;
  margin-bottom: 15px;
  font-size: 0.8rem;
}

.timezone-time {
  margin-bottom: 40px;
  text-align: center;
  font-size: 0.7rem;
}
</style>
