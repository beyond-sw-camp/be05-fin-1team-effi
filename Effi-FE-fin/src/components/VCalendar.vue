<template>
  <div v-if="show">
    <v-sheet class="d-flex" height="54" tile>
      <v-select
        v-model="type"
        :items="types"
        class="ma-2"
        label="View Mode"
        variant="outlined"
        dense
        hide-details
        @change="onViewModeChange"
      ></v-select>
      <v-select
        v-model="weekday"
        :items="weekdays"
        class="ma-2"
        label="Weekdays"
        variant="outlined"
        dense
        hide-details
        @change="onWeekdayChange"
      ></v-select>
    </v-sheet>
    <v-sheet>
      <v-calendar
        :key="calendarKey"
        ref="calendar"
        v-model="calendarValue"
        :events="events"
        :view-mode="type"
        :weekdays="weekday"
        @click:date="onDateClick"
        :event-color="getEventColor"
      >
        <template v-slot:event="{ event }">
          <div
            class="v-sheet v-theme--light rounded-t v-calendar-internal-event"
            :style="{ backgroundColor: event.color, color: event.textColor }"
            @click.stop="onEventClick(event)"
          >
            <strong>{{ event.title }}</strong>
            <br>
            {{ event.start ? event.start.format('YYYY-MM-DD hh:mm A') : '' }} - {{ event.end ? event.end.format('YYYY-MM-DD hh:mm A') : '' }}
          </div>
        </template>
      </v-calendar>
    </v-sheet>
    <schedule-modal
      :show="showScheduleDialog"
      :selected-date="selectedDate"
      @close="updateShowDialog(false)"
      @submit="handleEventSubmit"
    ></schedule-modal>
    <edit-schedule-modal
      :show="showEditDialog"
      :schedule-id="selectedEventId"
      @close="updateShowDialog(false)"
      @submit="handleEventSubmit"
    ></edit-schedule-modal>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed, nextTick } from 'vue';
import { useAuthStore } from '@/stores/auth';
import dayjs from 'dayjs';
import axiosInstance from '@/services/axios';
import isBetween from 'dayjs/plugin/isBetween';
import ScheduleModal from './ScheduleModal.vue';
import EditScheduleModal from './EditScheduleModal.vue';

dayjs.extend(isBetween);

export default {
  components: { ScheduleModal, EditScheduleModal },
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
            start: dayjs(schedule.startTime),
            end: dayjs(schedule.endTime),
            color: schedule.categoryColor,
            textColor: schedule.categoryTextColor,
            open: ref(false),
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

    const getEventColor = (event) => {
      return event.color;
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
          el.style.color = event.textColor;
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
  border: 1px solid #1867c0;
  width: 100%;
  font-size: 12px;
  padding: 3px;
  cursor: pointer;
  margin-bottom: 1px;
}
</style>
