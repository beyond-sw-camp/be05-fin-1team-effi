<template>
  <div v-if="show">
    <v-sheet class="d-flex" height="54" tile>
      <v-select v-model="type" :items="types" class="ma-2" label="View Mode" variant="outlined" dense hide-details
        @change="onViewModeChange"></v-select>
      <v-select v-model="weekday" :items="weekdays" class="ma-2" label="Weekdays" variant="outlined" dense hide-details
        @change="onWeekdayChange"></v-select>
    </v-sheet>
    <v-sheet class="calendar-and-timezone">
      <TimezoneComponent v-if="type === 'week' || type === 'day'" class="timezone-component" />
      <v-calendar :key="calendarKey" ref="calendar" v-model="calendarValue" :events="events" :view-mode="type"
        :weekdays="weekday" :interval-count="intervalCount" :interval-height="intervalHeight" @click:date="onDateClick"
        :event-color="getEventColor" class="calendar-component">
        <template v-slot:event="{ event }">
          <div class="v-sheet v-theme--light rounded-t v-calendar-internal-event"
            :style="{ backgroundColor: event.color }" @click.stop="onEventClick(event)">
            <strong>{{ event.title }}</strong>
            <br>
            {{ event.start ? event.start.format('MM-DD hh:mm A') : '' }} - {{ event.end ?
            event.end.format('MM-DD hh:mm A') : '' }}
          </div>
        </template>
      </v-calendar>
    </v-sheet>
    <schedule-modal :show="showScheduleDialog" :selected-date="selectedDate" @close="updateShowDialog(false)"
      @submit="handleEventSubmit"></schedule-modal>
    <edit-schedule-modal :show="showEditDialog" :schedule-id="selectedEventId" @close="updateShowDialog(false)"
      @submit="handleEventSubmit"></edit-schedule-modal>
    <vue3-snackbar bottom right :duration="5000" />
  </div>
</template>

<script>
import { ref, onMounted, watch, computed, nextTick } from 'vue';
import { useAuthStore } from '@/stores/auth';
import dayjs from 'dayjs';
import axiosInstance from '@/services/axios';
import isBetween from 'dayjs/plugin/isBetween';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';
import { useSnackbar } from 'vue3-snackbar';
import ScheduleModal from './ScheduleModal.vue';
import EditScheduleModal from './EditScheduleModal.vue';
import TimezoneComponent from './TimezoneComponent.vue';


dayjs.extend(isBetween);
dayjs.extend(utc);
dayjs.extend(timezone);

export default {
  components: { ScheduleModal, EditScheduleModal, TimezoneComponent },
  props: {
    show: {
      type: Boolean,
      required: true,
    },
    selectedCategories: {
      type: Array,
      default: () => [],
    },
    selectedGroupId: {
      type: Array,
      default: () => [],
    },
  },
  setup(props, { emit }) {
    const authStore = useAuthStore();
    authStore.loadSession();

    const types = ref(['month', 'week', 'day']);
    const type = ref('month');
    const weekday = ref([0, 1, 2, 3, 4, 5, 6]);
    const weekdays = ref([
      { title: 'Sun - Sat', value: [0, 1, 2, 3, 4, 5, 6] },
      { title: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
    ]);
    const calendarValue = ref([dayjs().toDate()]);
    const events = ref([]);
    const showScheduleDialog = ref(false);
    const showEditDialog = ref(false);
    const selectedEventId = ref(null);
    const selectedDate = ref(dayjs().toDate());
    const calendarKey = ref(0);
    const { add: showSnackbar } = useSnackbar();


    const intervalCount = computed(() => (type.value === 'week' || type.value === 'day' ? 24 : undefined));
    const intervalHeight = computed(() => (type.value === 'week' || type.value === 'day' ? 60 : undefined));

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
        } else if (props.selectedGroupId.length === 0 && props.selectedCategories.length > 0) {
          const scheduleResults = [];
          for (const categoryId of props.selectedCategories) {
            console.log('categoryId', categoryId);
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
        } else if (props.selectedGroupId.length > 0) {
          console.log('groupId');
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
            id: schedule.scheduleId,
            title: schedule.title,
            content: schedule.context,
            start: dayjs(schedule.startTime).tz(dayjs.tz.guess()), // 시간 변환
            end: dayjs(schedule.endTime).tz(dayjs.tz.guess()), // 시간 변환
            color: schedule.categoryColor,
            notificationYn: schedule.notificationYn,
            open: ref(false),
          }));
          setNotificationTimers(events.value);
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

    const getEventColor = (event) => {
      return event.color;
    };
    const setNotificationTimers = (events) => {
      console.log('Setting notification timers for events:', events);
      events.forEach((event) => {
        if (event.notificationYn) {
          const now = dayjs().tz(dayjs.tz.guess());
          const startTime = event.start;
          const diff = startTime.diff(now, 'minute');
          if (diff > 0 && diff <= 60 && !isNotified(event.id)) {
            console.log('Event:', event.title, 'Start Time:', startTime.format(), 'Now:', now.format(), 'Diff:', diff, 'minutes');
            const timeout = (diff > 60) ? (diff - 60) * 60 * 1000 : 0;
            setTimeout(() => {
              showToast(`일정 "${event.title}"이(가) 1시간 후에 시작됩니다.`);
              markAsNotified(event.id);
            }, timeout);
          }
        }
      });
    };
    const isNotified = (eventId) => {
      const notifiedEvents = JSON.parse(localStorage.getItem('notifiedEvents') || '[]');
      return notifiedEvents.includes(eventId);
    };
    const markAsNotified = (eventId) => {
      const notifiedEvents = JSON.parse(localStorage.getItem('notifiedEvents') || '[]');
      notifiedEvents.push(eventId);
      localStorage.setItem('notifiedEvents', JSON.stringify(notifiedEvents));
    };
    const showToast = (message) => {
      console.log('Triggering Snackbar:', message);
      showSnackbar({
        text: message,
        type: 'success',
        timeout: 60000,
        showClose: true,
      });
    };

    onMounted(() => {
      console.log('Component mounted');
      fetchSchedules();
      document.addEventListener('click', handleDayLabelClick);
    });

    watch(
      () => [props.selectedCategories, props.selectedGroupId],
      () => {
        fetchSchedules();
      },
      { deep: true }
    );

    watch(events, () => {
      nextTick(() => {
        applySlotStyles();
      });
    });

    const toToday = () => {
      calendarValue.value = [dayjs().toDate()];
    };

    const prevPeriod = () => {
      switch (type.value) {
        case 'month':
          calendarValue.value = [dayjs(calendarValue.value[0]).subtract(1, 'month').toDate()];
          break;
        case 'week':
          calendarValue.value = [dayjs(calendarValue.value[0]).subtract(1, 'week').toDate()];
          break;
        case 'day':
          calendarValue.value = [dayjs(calendarValue.value[0]).subtract(1, 'day').toDate()];
          break;
      }
    };

    const nextPeriod = () => {
      switch (type.value) {
        case 'month':
          calendarValue.value = [dayjs(calendarValue.value[0]).add(1, 'month').toDate()];
          break;
        case 'week':
          calendarValue.value = [dayjs(calendarValue.value[0]).add(1, 'week').toDate()];
          break;
        case 'day':
          calendarValue.value = [dayjs(calendarValue.value[0]).add(1, 'day').toDate()];
          break;
      }
    };

    const onViewModeChange = () => {
      console.log('View mode changed to:', type.value);
      emit('update-view-mode', type.value);
      updateEvents();
      calendarKey.value++;
      nextTick(() => {
        applySlotStyles();
      });
    };

    const onWeekdayChange = () => {
      console.log('Weekday changed to:', weekday.value);
      updateCalendarForWeekday();
    };

    const updateCalendarForWeekday = () => {
      let newDate = dayjs(calendarValue.value[0]);
      if (type.value === 'week' || type.value === 'day') {
        newDate = newDate.day(weekday.value[0]);
      } else {
        newDate = newDate.startOf('week').day(weekday.value[0]);
      }
      calendarValue.value = [newDate.toDate()];
      console.log('Calendar updated for weekday:', calendarValue.value);
      calendarKey.value++;
      nextTick(() => {
        applySlotStyles();
      });
    };

    watch(type, (newType) => {
      console.log('Type changed to:', newType);
      let newDate = dayjs(calendarValue.value[0]);

      switch (newType) {
        case 'month':
          newDate = newDate.startOf('month');
          break;
        case 'week':
          newDate = newDate.startOf('week').day(weekday.value[0]);
          break;
        case 'day':
          newDate = newDate.startOf('day');
          break;
      }

      console.log('New Date:', newDate.toDate());
      calendarValue.value = [newDate.toDate()];
      console.log('Calendar Value Updated:', calendarValue.value);
      updateEvents();
      calendarKey.value++;
      nextTick(() => {
        applySlotStyles();
      });
    });

    const updateEvents = () => {
      events.value = events.value.map(event => ({ ...event }));
      console.log('Events after update:', events.value);
      setNotificationTimers(events.value);
    };

    const editEvent = async (event) => {
      selectedEventId.value = event.id;
      showEditDialog.value = true;
    };

    const handleEventSubmit = () => {
      fetchSchedules();
    };

    const updateShowDialog = (value) => {
      showScheduleDialog.value = value;
      showEditDialog.value = value;
    };

    const onEventClick = (event) => {
      if (event && event.id) {
        editEvent(event);
      } else {
        console.error('Invalid event object:', event);
      }
    };

    const onDateClick = ({ date }) => {
      console.log('Date clicked:', date);
      selectedDate.value = date;
      selectedEventId.value = null;
      showScheduleDialog.value = true;
    };

    const handleDayLabelClick = (event) => {
      if (event.target.classList.contains('v-calendar-month__day')) {
        const date = event.target.getAttribute('data-date');
        console.log('Day label clicked:', date);
        onDateClick({ date: dayjs(date) });
      }
    };

    const applySlotStyles = () => {
      const slotElements = document.querySelectorAll('.v-calendar-day__row-content .v-sheet.v-calendar-internal-event');
      slotElements.forEach((el) => {
        const eventId = el.getAttribute('data-event-id');
        const event = events.value.find(e => e.id === Number(eventId));
        if (event) {
          el.style.backgroundColor = event.color;
          console.log('Slot element updated:', el);
        }
      });
    };

    return {
      calendarValue,
      types,
      type,
      weekday,
      weekdays,
      events,
      showScheduleDialog,
      showEditDialog,
      selectedEventId,
      selectedDate,
      toToday,
      prevPeriod,
      nextPeriod,
      onViewModeChange,
      onWeekdayChange,
      fetchSchedules,
      onEventClick,
      onDateClick,
      updateShowDialog,
      handleEventSubmit,
      handleDayLabelClick,
      getEventColor,
      intervalCount,
      intervalHeight,
      calendarKey,
    };
  },
};
</script>

<style scoped>
.v-calendar-internal-event {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border-radius: 2px;
  width: 100%;
  font-size: 12px;
  padding: 3px;
  cursor: pointer;
  margin-bottom: 1px;
}

.calendar-and-timezone {
  display: flex;
  height: 100vh;
  /* Full viewport height */
}

.timezone-component {
  margin-right: 5px;
}

.calendar-component {
  flex: 1;
}

/* v-calendar v-calendar-day calendar-component 를 클래스로 갖는 것의 위쪽 공간 띄우기 */
.v-calendar.v-calendar-day.calendar-component {
  margin-top: 10px;
}
</style>