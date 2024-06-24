<template>
  <v-app>
    <v-main>
      <v-container>
        <Sidebar />
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
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import TimezoneModal from '@/components/TimezoneModal.vue';
import Sidebar from '@/components/VSidebar.vue'; // Sidebar 컴포넌트를 임포트합니다.
import axiosInstance from '@/services/axios';
import { ref, onMounted } from 'vue';

export default {
  components: {
    TimezoneModal,
    Sidebar // Sidebar 컴포넌트를 등록합니다.
  },
  setup() {
    const showTimezoneModal = ref(false);
    const selectedTimezones = ref([]);
    const defaultTimezone = ref({});
    const hours = ref([...Array(24).keys()]); // 0 to 23 hours
    const amPmHours = ref([
      'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'AM', 'PM',
      'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM', 'PM'
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
