<template>
  <div>
    <v-sheet class="d-flex" height="54" tile>
      <v-select
        v-model="type"
        :items="types"
        class="ma-2"
        label="View Mode"
        variant="outlined"
        dense
        hide-details
      ></v-select>
      <v-select
        v-model="weekday"
        :items="weekdays"
        class="ma-2"
        label="Weekdays"
        variant="outlined"
        dense
        hide-details
      ></v-select>
    </v-sheet>
    <v-sheet>
      <v-calendar
        ref="calendar"
        v-model="value"
        :events="events"
        :view-mode="type"
        :weekdays="weekday"
        @dblclick:event="openAddScheduleModal"
      ></v-calendar>
    </v-sheet>
    <add-schedule-modal
      v-model:dialog="dialog"
      @schedule-saved="fetchSchedules"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import dayjs from 'dayjs';
import AddScheduleModal from './AddScheduleModal.vue';

export default {
  components: {
    AddScheduleModal,
  },
  setup() {
    const types = ref(['month', 'week', 'day']);
    const type = ref('month');
    const weekday = ref([0, 1, 2, 3, 4, 5, 6]);
    const weekdays = ref([
      { title: 'Sun - Sat', value: [0, 1, 2, 3, 4, 5, 6] },
      { title: 'Mon - Sun', value: [1, 2, 3, 4, 5, 6, 0] },
      { title: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
      { title: 'Mon, Wed, Fri', value: [1, 3, 5] },
    ]);
    const value = ref([new Date()]);
    const events = ref([]);
    const dialog = ref(false);

    const fetchSchedules = async () => {
      try {
        const response = await axios.get('/api/schedule/findAll');
        const schedules = response.data;
        console.log('Fetched schedules:', schedules);

        if (Array.isArray(schedules)) {
          events.value = schedules.map(schedule => ({
            name: schedule.title,
            start: new Date(schedule.startTime),
            end: new Date(schedule.endTime),
            color: 'blue',  // 임의로 색상 지정, 필요한 경우 서버에서 받아올 수 있음
          }));
        } else {
          console.error('Expected an array but got:', schedules);
        }
      } catch (error) {
        console.error('Failed to fetch schedules:', error);
      }
    };

    const openAddScheduleModal = () => {
      dialog.value = true;
    };

    const close = () => {
      dialog.value = false;
    };

    const currentYear = computed(() => {
      return dayjs(value.value[0]).year();
    });

    const currentMonth = computed(() => {
      return dayjs(value.value[0]).format('M');
    });

    onMounted(() => {
      fetchSchedules();
    });

    const toToday = () => {
      value.value = [new Date()];
    };

    const prevMonth = () => {
      const newDate = dayjs(value.value[0]).subtract(1, 'month').toDate();
      value.value = [newDate];
    };

    const nextMonth = () => {
      const newDate = dayjs(value.value[0]).add(1, 'month').toDate();
      value.value = [newDate];
    };

    return {
      type,
      types,
      weekday,
      weekdays,
      value,
      events,
      dialog,
      currentYear,
      currentMonth,
      toToday,
      prevMonth,
      nextMonth,
      openAddScheduleModal,
      close,
      fetchSchedules,
    };
  },
};
</script>

<style>
.date-display {
  margin: 0 10px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
}

.calendar-head-title {
  width: 100%;
  text-align: center;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .calendar {
    max-width: 100%;
  }
  .date-display {
    font-size: 16px;
  }
}
</style>
