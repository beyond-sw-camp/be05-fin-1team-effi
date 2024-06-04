<template>
  <div class="calendar-container">
    <v-sheet class="d-flex justify-space-between align-center mb-4" height="54" tile>
      <div class="d-flex">
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
      </div>
      <div class="d-flex align-center">
        <v-btn icon @click="toToday">
          <v-icon>mdi-calendar-today</v-icon>
        </v-btn>
        <v-btn icon @click="prevMonth">
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>
        <span class="date-display">{{ currentYear }}년 {{ currentMonth }}월</span>
        <v-btn icon @click="nextMonth">
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>
      </div>
    </v-sheet>
    <v-sheet>
      <v-calendar
        ref="calendar"
        v-model="value"
        :events="events"
        :view-mode="type"
        :weekdays="weekday"
        class="calendar"
      ></v-calendar>
    </v-sheet>
  </div>
</template>

<script>
import { useDate } from 'vuetify';
import dayjs from '@/plugins/dayjs';

export default {
  data: () => ({
    type: 'month',
    types: ['month', 'week', 'day'],
    weekday: [0, 1, 2, 3, 4, 5, 6],
    weekdays: [
      { title: 'Sun - Sat', value: [0, 1, 2, 3, 4, 5, 6] },
      { title: 'Mon - Sun', value: [1, 2, 3, 4, 5, 6, 0] },
      { title: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
      { title: 'Mon, Wed, Fri', value: [1, 3, 5] },
    ],
    value: [new Date()],
    events: [],
    colors: ['blue', 'indigo', 'deep-purple', 'cyan', 'green', 'orange', 'grey darken-1'],
    titles: ['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party'],
  }),
  computed: {
    currentYear() {
      return dayjs(this.value[0]).year();
    },
    currentMonth() {
      return dayjs(this.value[0]).format('M');
    },
  },
  mounted() {
    const adapter = useDate();
    this.getEvents({
      start: adapter.startOfDay(adapter.startOfMonth(new Date())),
      end: adapter.endOfDay(adapter.endOfMonth(new Date())),
    });
  },
  methods: {
    getEvents({ start, end }) {
      const events = [];

      const min = start;
      const max = end;
      const days = (max.getTime() - min.getTime()) / 86400000;
      const eventCount = this.rnd(days, days + 20);

      for (let i = 0; i < eventCount; i++) {
        const allDay = this.rnd(0, 3) === 0;
        const firstTimestamp = this.rnd(min.getTime(), max.getTime());
        const first = new Date(firstTimestamp - (firstTimestamp % 900000));
        const secondTimestamp = this.rnd(2, allDay ? 288 : 8) * 900000;
        const second = new Date(first.getTime() + secondTimestamp);

        events.push({
          title: this.titles[this.rnd(0, this.titles.length - 1)],
          start: first,
          end: second,
          color: this.colors[this.rnd(0, this.colors.length - 1)],
          allDay: !allDay,
        });
      }

      this.events = events;
    },
    getEventColor(event) {
      return event.color;
    },
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
    },
    rnd(a, b) {
      return Math.floor((b - a + 1) * Math.random()) + a;
    },
    toToday() {
      this.value = [new Date()];
    },
    prevMonth() {
      const newDate = dayjs(this.value[0]).subtract(1, 'month').toDate();
      this.value = [newDate];
    },
    nextMonth() {
      const newDate = dayjs(this.value[0]).add(1, 'month').toDate();
      this.value = [newDate];
    },
  },
};
</script>

<style>
.calendar-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.calendar {
  width: 100%;
  max-width: 900px; /* 최대 너비 설정 */
}

.date-display {
  margin: 0 15px;
  font-size: 18px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .calendar {
    max-width: 100%; /* 모바일에서 전체 너비 사용 */
  }
  .date-display {
    font-size: 16px;
  }
}
</style>
