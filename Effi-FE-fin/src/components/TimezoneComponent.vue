<template>
  <div class="timezone-component">
    <button class="add-timezone-button" @click="showTimezoneModal = true">+</button>
    <div class="timezone-display">
      <div v-for="timezone in selectedTimezones" :key="timezone.timezoneId" class="timezone-column">
        <div class="timezone-header">
          {{ timezone.timezoneName }}
        </div>
        <div v-for="time in times" :key="time" class="timezone-time">
          {{ formatTime(time, timezone.offset) }}
        </div>
      </div>
    </div>
    <TimezoneModal :show="showTimezoneModal" @close="showTimezoneModal = false" @add-timezone="addTimezone" />
  </div>
</template>

<script>
import { ref } from 'vue';
import TimezoneModal from './TimezoneModal.vue';

export default {
  components: { TimezoneModal },
  setup() {
    const showTimezoneModal = ref(false);
    const selectedTimezones = ref([]);
    const times = ref([
      '2:00 AM', '3:00 AM', '4:00 AM', '5:00 AM', '6:00 AM',
      '7:00 AM', '8:00 AM', '9:00 AM', '10:00 AM', '11:00 AM',
      '12:00 PM', '1:00 PM'
    ]);

    const formatTime = (time, offset) => {
      const [hour, period] = time.split(' ');
      let [hours, minutes] = hour.split(':').map(Number);
      if (period === 'PM' && hours !== 12) hours += 12;
      if (period === 'AM' && hours === 12) hours = 0;

      hours = (hours + offset + 24) % 24;
      const formattedHours = hours % 12 || 12;
      const formattedPeriod = hours < 12 ? 'AM' : 'PM';

      return `${formattedHours}:${minutes < 10 ? '0' + minutes : minutes} ${formattedPeriod}`;
    };

    const addTimezone = (timezone) => {
      selectedTimezones.value.push(timezone);
    };

    return {
      showTimezoneModal,
      selectedTimezones,
      times,
      formatTime,
      addTimezone
    };
  }
};
</script>

<style scoped>
.timezone-component {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
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
}

.timezone-column {
  display: flex;
  flex-direction: column;
  margin-right: 16px;
}

.timezone-header {
  font-weight: bold;
  margin-bottom: 8px;
}

.timezone-time {
  margin-bottom: 4px;
}
</style>
