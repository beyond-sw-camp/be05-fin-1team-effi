<template>
  <div class="calendar-container">
    <!-- View Mode and Weekdays -->
    <v-sheet class="d-flex justify-space-between align-center mb-2" height="50" tile>
      <div class="d-flex">
        <v-select
          v-model="type"
          :items="types"
          class="ma-1"
          label="View Mode"
          variant="outlined"
          dense
          hide-details
          style="width: 120px;"
        ></v-select>
        <v-select
          v-model="weekday"
          :items="weekdays"
          class="ma-1"
          label="Weekdays"
          variant="outlined"
          dense
          hide-details
          style="width: 150px;"
        ></v-select>
      </div>
    </v-sheet>


    <!-- Calendar -->
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
import { ref, computed, onMounted } from 'vue';
import { useDate } from 'vuetify';
import dayjs from 'dayjs';

export default {
  setup() {
    const type = ref('month');
    const types = ref(['month', 'week', 'day']);
    const weekday = ref([0, 1, 2, 3, 4, 5, 6]);
    const weekdays = ref([
      { title: 'Sun - Sat', value: [0, 1, 2, 3, 4, 5, 6] },
      { title: 'Mon - Sun', value: [1, 2, 3, 4, 5, 6, 0] },
      { title: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
      { title: 'Mon, Wed, Fri', value: [1, 3, 5] },
    ]);
    const value = ref([new Date()]);
    const events = ref([]);
    const colors = ref(['blue', 'indigo', 'deep-purple', 'cyan', 'green', 'orange', 'grey darken-1']);
    const titles = ref(['Meeting', 'Holiday', 'PTO', 'Travel', 'Event', 'Birthday', 'Conference', 'Party']);

    const currentYear = computed(() => {
      return dayjs(value.value[0]).year();
    });

    const currentMonth = computed(() => {
      return dayjs(value.value[0]).format('M');
    });

    const adapter = useDate();

    onMounted(() => {
      getEvents({
        start: adapter.startOfDay(adapter.startOfMonth(new Date())),
        end: adapter.endOfDay(adapter.endOfMonth(new Date())),
      });
    });

    const getEvents = ({ start, end }) => {
      const eventsArray = [];
      const min = start;
      const max = end;
      const days = (max.getTime() - min.getTime()) / 86400000;
      const eventCount = rnd(days, days + 20);

      for (let i = 0; i < eventCount; i++) {
        const allDay = rnd(0, 3) === 0;
        const firstTimestamp = rnd(min.getTime(), max.getTime());
        const first = new Date(firstTimestamp - (firstTimestamp % 900000));
        const secondTimestamp = rnd(2, allDay ? 288 : 8) * 900000;
        const second = new Date(first.getTime() + secondTimestamp);

        eventsArray.push({
          title: titles.value[rnd(0, titles.value.length - 1)],
          start: first,
          end: second,
          color: colors.value[rnd(0, colors.value.length - 1)],
          allDay: !allDay,
        });
      }

      events.value = eventsArray;
    };

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

    const rnd = (a, b) => {
      return Math.floor((b - a + 1) * Math.random()) + a;
    };

    return {
      type,
      types,
      weekday,
      weekdays,
      value,
      events,
      currentYear,
      currentMonth,
      toToday,
      prevMonth,
      nextMonth,
    };
  },
};
</script>

<style>
.calendar-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
}

.calendar {
  width: 100%;
  max-width: 900px;
}

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
