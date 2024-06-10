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
        :type="type"
        :weekdays="weekday"
        :timezones="timezonesSelected"
        @dblclick:event="openAddScheduleModal"
      >
        <template v-slot:event="{ event }">
          <div class="event" :style="{ backgroundColor: event.color }">
            <strong>{{ event.title }}</strong>
            <br>
            {{ event.start.format('YYYY-MM-DD HH:mm') }} - {{ event.end.format('YYYY-MM-DD HH:mm') }}
          </div>
        </template>
        <template v-slot:timeGutter="{ time, index }">
          <div class="time-gutter">
            <v-btn icon @click="addTimeZone(index)">
              <v-icon>mdi-plus</v-icon>
            </v-btn>
            <span>{{ time }}</span>
          </div>
        </template>
      </v-calendar>
    </v-sheet>
    <add-schedule-modal
      v-model:dialog="dialog"
      @schedule-saved="fetchSchedules"
    />
    <v-menu v-model="timezoneMenu" bottom right>
      <v-list>
        <v-list-item
          v-for="timezone in availableTimezones"
          :key="timezone.timezoneId"
          @click="addTimezone(timezone)"
        >
          <v-list-item-title>{{ timezone.timezoneName }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import dayjs from 'dayjs';
import isBetween from 'dayjs/plugin/isBetween';
import AddScheduleModal from './AddScheduleModal.vue';
import { useAuthStore } from '@/stores/auth';

dayjs.extend(isBetween);

export default {
  components: {
    AddScheduleModal,
  },
  setup() {
    const authStore = useAuthStore();
    const types = ref(['month', 'week', 'day']);
    const type = ref('month');
    const weekday = ref([0, 1, 2, 3, 4, 5, 6]);
    const weekdays = ref([
      { title: 'Sun - Sat', value: [0, 1, 2, 3, 4, 5, 6] },
      { title: 'Mon - Sun', value: [1, 2, 3, 4, 5, 6, 0] },
      { title: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
      { title: 'Mon, Wed, Fri', value: [1, 3, 5] },
    ]);
    const value = ref([dayjs()]);
    const events = ref([]);
    const dialog = ref(false);
    const timezoneMenu = ref(false);
    const availableTimezones = ref([]);
    const timezonesSelected = ref([]);

    const fetchSchedules = async () => {
      try {
        const token = authStore.accessToken;
        if (!token) {
          throw new Error('No access token found');
        }

        const response = await axios.get('/api/schedule/findAll', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        const schedules = response.data;
        console.log('Fetched schedules:', schedules);

        if (Array.isArray(schedules)) {
          events.value = schedules.map(schedule => ({
            title: schedule.title,  // title로 변경
            start: dayjs(schedule.startTime),
            end: dayjs(schedule.endTime),
            color: 'blue',
          }));
        } else {
          console.error('Expected an array but got:', schedules);
        }
      } catch (error) {
        console.error('Failed to fetch schedules:', error);
        if (error.response) {
          console.error('Error response data:', error.response.data);
          console.error('Error response status:', error.response.status);
          console.error('Error response headers:', error.response.headers);
        } else if (error.request) {
          console.error('No response received:', error.request);
        } else {
          console.error('Error setting up the request:', error.message);
        }
      }
    };

    const fetchTimezones = async () => {
      try {
        const response = await axios.get('/api/timezones');
        const timezones = response.data.data;
        availableTimezones.value = timezones;
        console.log('Fetched timezones:', timezones);
      } catch (error) {
        console.error('Failed to fetch timezones:', error);
      }
    };

    const fetchUserTimezones = async () => {
      try {
        const empId = authStore.session.empNo;
        const response = await axios.get(`/api/timezone-emp/${empId}`);
        const userTimezones = response.data.data.timezones;
        timezonesSelected.value = userTimezones.map(tz => tz.timezoneName);
        console.log('Fetched user timezones:', userTimezones);
      } catch (error) {
        console.error('Failed to fetch user timezones:', error);
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
      fetchTimezones();
      fetchUserTimezones();
    });

    const toToday = () => {
      value.value = [dayjs()];
    };

    const prevMonth = () => {
      const newDate = dayjs(value.value[0]).subtract(1, 'month');
      value.value = [newDate];
    };

    const nextMonth = () => {
      const newDate = dayjs(value.value[0]).add(1, 'month');
      value.value = [newDate];
    };

    const showTimezoneMenu = () => {
      timezoneMenu.value = true;
    };

    const addTimezone = async (timezone) => {
      try {
        const empId = authStore.session.empNo;
        await axios.post(`/api/timezone-emp/${empId}/add`, null, {
          params: {
            timezoneId: timezone.timezoneId,
            isDefault: false,
          },
        });
        if (!timezonesSelected.value.includes(timezone.timezoneName)) {
          timezonesSelected.value.push(timezone.timezoneName);
        }
        timezoneMenu.value = false;
      } catch (error) {
        console.error('Failed to add timezone:', error);
      }
    };

    const addTimeZone = (index) => {
      console.log('Adding timezone at index:', index);
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
      timezoneMenu,
      availableTimezones,
      timezonesSelected,
      showTimezoneMenu,
      addTimezone,
      addTimeZone,
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

.add-timezone-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  z-index: 1;
}

.event {
  padding: 5px;
  border-radius: 4px;
  background-color: var(--v-primary-base);
  color: white;
  margin-bottom: 5px;
}

@media (max-width: 768px) {
  .calendar {
    max-width: 100%;
  }
  .date-display {
    font-size: 16px;
  }
}
.time-gutter {
  display: flex;
  align-items: center;
}
.time-gutter span {
  margin-left: 8px;
}
</style>
