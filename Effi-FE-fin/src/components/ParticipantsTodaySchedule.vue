<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-toolbar flat>
          <v-toolbar-title>Today’s Schedule for {{ selectedUserNames.join(', ') }}</v-toolbar-title>
        </v-toolbar>
        <v-data-table :headers="headers" :items="formattedSchedules" item-value="time" class="elevation-1"
          :items-per-page="-1" hide-default-footer>
          <template v-slot:item="{ item }">
            <tr>
              <td>{{ item.time }}</td>
              <td v-for="(schedule, index) in item.schedules" :key="index">
                <div v-if="schedule" :class="['event', { first: schedule.isFirstSlot, last: schedule.isLastSlot }]">
                  <template v-if="schedule.isFirstSlot">
                    <strong>{{ schedule.title }}</strong><br>
                    {{ formatTime(schedule.start) }} - {{ formatTime(schedule.end) }}
                  </template>
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

        // 클라이언트의 현재 시간대 오프셋을 가져옴 (분 단위, 동부 시간대는 음수)
        const timezoneOffset = new Date().getTimezoneOffset();

        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);

        const schedulePromises = props.selectedUsers.map(user =>
          axiosInstance.get(`/api/schedule/find/other/${user.id}`, {
            headers: { Authorization: `Bearer ${token}` },
          }).then(response => {
            console.log(`Schedules for user ${user.id}:`, response.data);
            const todaySchedules = response.data.filter(schedule => {
              const start = new Date(schedule.startTime);
              const end = new Date(schedule.endTime);

              // UTC 시간을 로컬 시간으로 변환 (timezoneOffset을 시간 단위로 변환)
              start.setMinutes(start.getMinutes() - timezoneOffset);
              end.setMinutes(end.getMinutes() - timezoneOffset);

              console.log(`Checking schedule: ${schedule.title} (start: ${start}, end: ${end}) against today: ${today}, tomorrow: ${tomorrow}`);
              return (
                (start < tomorrow && end > today) ||
                (start.toDateString() === today.toDateString() || end.toDateString() === today.toDateString())
              );
            });
            console.log(`Filtered schedules for user ${user.id}:`, todaySchedules);
            return {
              userId: user.id,
              schedules: todaySchedules.map(schedule => ({
                title: schedule.title,
                start: new Date(schedule.startTime),
                end: new Date(schedule.endTime),
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
          console.log(`Processing schedule: ${schedule.title} from ${schedule.start} to ${schedule.end}`);
          const startHour = schedule.start.getHours();
          const endHour = schedule.end.getHours();
          const userIndex = props.selectedUsers.findIndex(user => user.id === userId);

          if (userIndex !== -1) {
            let currentHour = startHour;
            while (currentHour !== endHour) {
              const time = `${currentHour % 24}:00`;
              if (scheduleMap[time]) {
                scheduleMap[time].schedules[userIndex] = {
                  ...schedule,
                  isFirstSlot: currentHour === startHour,
                  isLastSlot: (currentHour + 1) % 24 === endHour
                };
              }
              currentHour = (currentHour + 1) % 24;
            }
          }
        });
      });

      formattedSchedules.value = Object.values(scheduleMap);
      console.log('Formatted schedules:', JSON.stringify(formattedSchedules.value, null, 2));
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
  methods: {
    formatTime(date) {
      return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
  },
};
</script>

<style scoped>
.event {
  padding: 5px;
  border-radius: 4px;
  color: #fff;
  text-align: center;
  background-color: #4682B4;
  height: 100%;
  width: 100%;
}

.event.first {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}

.event.last {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
}

.event:not(.first):not(.last) {
  border-radius: 0;
  color: transparent;
}
</style>
