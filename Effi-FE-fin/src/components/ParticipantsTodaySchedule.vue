<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-toolbar flat>
          <v-toolbar-title>Today’s Schedule for {{ selectedUserNames.join(', ') }}</v-toolbar-title>
        </v-toolbar>
        <v-data-table :headers="headers" :items="formattedSchedules" item-value="time" class="elevation-1">
          <template v-slot:item="{ item }">
            <tr>
              <td>{{ item.time }}</td>
              <td v-for="(schedule, index) in item.schedules" :key="index">
                <div v-if="schedule" class="event" :style="{ backgroundColor: '#4682B4' }">
                  <strong>{{ schedule.title }}</strong><br>
                  {{ schedule.start.format('HH:mm') }} - {{ schedule.end.format('HH:mm') }}
                </div>
              </td>
            </tr>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import dayjs from 'dayjs';
import axiosInstance from '@/services/axios';

export default {
  name: 'ParticipantsTodaySchedule',
  props: {
    selectedUsers: {
      type: Array,
      default: () => [],
    },
  },
  setup(props) {
    const headers = ref([]);
    const schedules = ref([]);
    const formattedSchedules = ref([]);
    const selectedUserNames = ref([]);

    const fetchTodaySchedules = async () => {
      console.log('Fetching schedules for users:', props.selectedUsers);
      try {
        const token = sessionStorage.getItem('accessToken');
        if (!token) {
          throw new Error('No access token found');
        }

        const today = dayjs().startOf('day');
        const tomorrow = dayjs(today).add(1, 'day');

        const schedulePromises = props.selectedUsers.map(user =>
          axiosInstance.get(`/api/schedule/find/other/${user.id}`, {
            headers: { Authorization: `Bearer ${token}` },
          }).then(response => {
            console.log(`Schedules for user ${user.id}:`, response.data);
            const todaySchedules = response.data.filter(schedule => {
              const start = dayjs(schedule.startTime);
              return start.isAfter(today) && start.isBefore(tomorrow);
            });
            console.log(`Today's schedules for user ${user.id}:`, todaySchedules);
            return {
              userId: user.id,
              schedules: todaySchedules.map(schedule => ({
                title: schedule.title,
                start: dayjs(schedule.startTime),
                end: dayjs(schedule.endTime),
              })),
            };
          })
        );

        const results = await Promise.all(schedulePromises);
        schedules.value = results;
        console.log('Filtered today schedules:', schedules.value);
        formatSchedules();
      } catch (error) {
        console.error('Failed to fetch schedules:', error);
      }
    };

    const formatSchedules = () => {
      const times = Array.from({ length: 24 }, (_, i) => `${i}:00`);
      const scheduleMap = {};

      times.forEach(time => {
        scheduleMap[time] = { time, schedules: Array(props.selectedUsers.length).fill(null) };
      });

      schedules.value.forEach(({ userId, schedules }) => {
        schedules.forEach(schedule => {
          const startHour = schedule.start.hour();
          const time = `${startHour}:00`;
          const userIndex = props.selectedUsers.findIndex(user => user.id === userId);
          if (scheduleMap[time] && userIndex !== -1) {
            scheduleMap[time].schedules[userIndex] = schedule;
          }
        });
      });

      formattedSchedules.value = Object.values(scheduleMap);
      console.log('Formatted schedules:', formattedSchedules.value);
    };

    const updateSelectedUserNames = () => {
      selectedUserNames.value = props.selectedUsers.map(user => user.name);
    };

    onMounted(() => {
      headers.value = [
        { text: 'Time', value: 'time' },
        ...props.selectedUsers.map(user => ({ text: user.name, value: user.id })),
      ];
      fetchTodaySchedules();
      updateSelectedUserNames();
    });

    watch(() => props.selectedUsers, () => {
      headers.value = [
        { text: 'Time', value: 'time' },
        ...props.selectedUsers.map(user => ({ text: user.name, value: user.id })),
      ];
      fetchTodaySchedules();
      updateSelectedUserNames();
    }, { deep: true });

    return {
      headers,
      formattedSchedules,
      selectedUserNames,
    };
  },
};
</script>

<style scoped>
.event {
  padding: 5px;
  border-radius: 4px;
  color: #fff;
  text-align: center;
}
</style>
