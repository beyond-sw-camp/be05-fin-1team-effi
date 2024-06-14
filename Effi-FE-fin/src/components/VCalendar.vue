<template>
  <div>
    <v-sheet class="d-flex" height="54" tile>
      <v-select v-model="type" :items="types" class="ma-2" label="View Mode" variant="outlined" dense hide-details
        @change="onViewModeChange"></v-select>
      <v-select v-model="weekday" :items="weekdays" class="ma-2" label="Weekdays" variant="outlined" dense hide-details
        @change="onWeekdayChange"></v-select>
    </v-sheet>
    <v-sheet>
      <v-calendar ref="calendar" v-model="calendarValue" :events="events" :view-mode="type" :weekdays="weekday"
        @dblclick:date="openAddScheduleModal" @click:event="openEditScheduleModal">
        <template v-slot:event="{ event }">
          <div class="event" :style="{ backgroundColor: event.color }">
            <strong>{{ event.title }}</strong>
            <br>
            {{ event.start ? event.start.format('YYYY-MM-DD HH:mm') : '' }} - {{ event.end ?
              event.end.format('YYYY-MM-DD HH:mm') : '' }}
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
    <schedule-modal v-if="dialog" :show="dialog" :is-edit-mode="isEditMode" :event="selectedEvent"
      @close="updateDialog(false)" />
    <v-menu v-model="timezoneMenu" bottom right>
      <v-list>
        <v-list-item v-for="timezone in availableTimezones" :key="timezone.timezoneId" @click="addTimezone(timezone)">
          <v-list-item-title>{{ timezone.timezoneName }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed } from 'vue';
import axiosInstance from '@/services/axios';
import dayjs from 'dayjs';
import isBetween from 'dayjs/plugin/isBetween';
import ScheduleModal from './ScheduleModal.vue';
import { useAuthStore } from '@/stores/auth';

dayjs.extend(isBetween);

export default {
  components: {
    ScheduleModal,
  },
  props: {
    selectedCategories: {
      type: Array,
      default: () => [],
    },
    selectedGroupId: {
      type: Array,
      default: () => [],
    }
  },
  setup(props) {
    const authStore = useAuthStore();
    authStore.loadSession();

    const types = ref(['month', 'week', 'day']);
    const type = ref('month');
    const weekday = ref([0, 1, 2, 3, 4, 5, 6]);
    const weekdays = ref([
      { title: 'Sun - Sat', value: [0, 1, 2, 3, 4, 5, 6] },
      { title: 'Mon - Sun', value: [1, 2, 3, 4, 5, 6, 0] },
      { title: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
      { title: 'Mon, Wed, Fri', value: [1, 3, 5] },
    ]);
    const calendarValue = ref([dayjs()]);
    const events = ref([]);
    const dialog = ref(false);
    const isEditMode = ref(false);
    const selectedEvent = ref({
      title: '',
      content: '',
      startDate: '',
      startTime: '',
      endDate: '',
      endTime: '',
    });
    const timezoneMenu = ref(false);
    const availableTimezones = ref([]);
    const timezonesSelected = ref([]);

    const fetchSchedules = async () => {
      try {
        const token = authStore.accessToken;
        if (!token) {
          throw new Error('No access token found');
        }

        let schedules = [];
        if (props.selectedCategories.length === 0 && props.selectedGroupId.length === 0) {
          const response = await axiosInstance.get('/api/schedule/findAll', {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          schedules = response.data;
        } else if (props.selectedGroupId.length === 0) {
          const scheduleResults = [];
          for (const categoryId of props.selectedCategories) {
            try {
              const response = await axiosInstance.get(`/api/schedule/find/category/${categoryId}`, {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              });
              scheduleResults.push(...response.data);
            } catch (error) {
              console.error(`Error fetching schedules for categoryId ${categoryId}:`, error);
            }
          }
          schedules = scheduleResults;
        } else {
          const scheduleResults = [];
          for (const groupId of props.selectedGroupId) {
            try {
              const response = await axiosInstance.get(`/api/schedule/find/group/${groupId}`, {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              });
              scheduleResults.push(...response.data);
            } catch (error) {
              console.error(`Error fetching schedules for groupId ${groupId}:`, error);
            }
          }
          schedules = scheduleResults;
        }

        if (Array.isArray(schedules)) {
          events.value = schedules.map((schedule) => ({
            id: schedule.id,
            title: schedule.title,
            content: schedule.context,
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

    const fetchUserTimezones = async () => {
      try {
        const empId = authStore.empNo;
        if (!empId) {
          throw new Error('No employee ID found');
        }
        const response = await axiosInstance.get(`/api/timezone-emp/${empId}`);
        const userTimezones = response.data.data.timezones;
        timezonesSelected.value = userTimezones.map((tz) => tz.timezoneName);
      } catch (error) {
        console.error('Failed to fetch user timezones:', error);
      }
    };

    const openAddScheduleModal = ({ date }) => {
      selectedEvent.value = {
        title: '',
        content: '',
        startDate: dayjs(date).format('YYYY-MM-DD'),
        startTime: '',
        endDate: dayjs(date).format('YYYY-MM-DD'),
        endTime: '',
      };
      isEditMode.value = false;
      dialog.value = true;
    };

    const openEditScheduleModal = ({ event }) => {
      selectedEvent.value = {
        title: event.title,
        content: event.content,
        startDate: event.start ? event.start.format('YYYY-MM-DD') : '',
        startTime: event.start ? event.start.format('HH:mm') : '',
        endDate: event.end ? event.end.format('YYYY-MM-DD') : '',
        endTime: event.end ? event.end.format('HH:mm') : '',
      };
      isEditMode.value = true;
      dialog.value = true;
    };

    const updateDialog = (newVal) => {
      dialog.value = newVal;
    };

    const currentMonth = computed(() => dayjs(calendarValue.value[0]).format('M'));

    onMounted(() => {
      fetchSchedules();
      fetchUserTimezones();
    });

    watch(
      () => [props.selectedCategories, props.selectedGroupId],
      (newValues) => {
        fetchSchedules();
      },
      { deep: true }
    );

    const toToday = () => {
      calendarValue.value = [dayjs()];
    };

    const prevPeriod = () => {
      switch (type.value) {
        case 'month':
          calendarValue.value = [dayjs(calendarValue.value[0]).subtract(1, 'month')];
          break;
        case 'week':
          calendarValue.value = [dayjs(calendarValue.value[0]).subtract(1, 'week')];
          break;
        case 'day':
          calendarValue.value = [dayjs(calendarValue.value[0]).subtract(1, 'day')];
          break;
      }
    };

    const nextPeriod = () => {
      switch (type.value) {
        case 'month':
          calendarValue.value = [dayjs(calendarValue.value[0]).add(1, 'month')];
          break;
        case 'week':
          calendarValue.value = [dayjs(calendarValue.value[0]).add(1, 'week')];
          break;
        case 'day':
          calendarValue.value = [dayjs(calendarValue.value[0]).add(1, 'day')];
          break;
      }
    };

    const showTimezoneMenu = () => {
      timezoneMenu.value = true;
    };

    const addTimezone = async (timezone) => {
      try {
        const empId = authStore.session?.empNo;
        if (!empId) {
          throw new Error('No employee ID found');
        }
        await axiosInstance.post(`/api/timezone-emp/${empId}/add`, null, {
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

    const onViewModeChange = () => {
      console.log('View mode changed to:', type.value);
    };

    const onWeekdayChange = () => {
      console.log('Weekday changed to:', weekday.value);
    };

    watch(type, (newType) => {
      switch (newType) {
        case 'month':
          calendarValue.value = [calendarValue.value[0].startOf('month')];
          break;
        case 'week':
          calendarValue.value = [calendarValue.value[0].startOf('week')];
          break;
        case 'day':
          calendarValue.value = [calendarValue.value[0].startOf('day')];
          break;
      }
    });

    return {
      type,
      types,
      weekday,
      weekdays,
      calendarValue,
      events,
      dialog,
      isEditMode,
      selectedEvent,
      currentMonth,
      toToday,
      prevPeriod,
      nextPeriod,
      openAddScheduleModal,
      openEditScheduleModal,
      updateDialog,
      timezoneMenu,
      availableTimezones,
      timezonesSelected,
      showTimezoneMenu,
      addTimezone,
      addTimeZone,
      onViewModeChange,
      onWeekdayChange,
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