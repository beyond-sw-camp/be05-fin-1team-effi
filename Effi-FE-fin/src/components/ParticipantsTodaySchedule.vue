<template>
  <v-container class="main-container">
    <v-row>
      <v-col cols="12">
        <v-toolbar flat>
          <v-toolbar-title>오늘의 일정: {{ selectedUserNames.join(', ') }}</v-toolbar-title>
        </v-toolbar>
        <v-data-table :headers="headers" :items="formattedSchedules" item-value="time"
          class="elevation-1 schedule-table" :items-per-page="-1" hide-default-footer>
          <template v-slot:item="{ item }">
            <tr>
              <td>{{ item.time }}</td>
              <td v-for="(schedule, index) in item.schedules" :key="index">
                <div v-if="schedule" :class="['event', { first: schedule.isFirstSlot, last: schedule.isLastSlot }]"
                  :style="{ backgroundColor: getCategoryColor(schedule.categoryName), color: getTextColor(schedule.categoryName) }">
                  <template v-if="schedule.isFirstSlot">
                    <strong>{{ schedule.userName }}의 일정 : {{ schedule.title }}</strong><br>
                    {{ formatTime(schedule.start) }} - {{ formatTime(schedule.end) }} / {{ timezoneName }}<br>
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
    const headers = ref([{ text: 'Time', value: 'time' }]);
    const schedules = ref([]);
    const formattedSchedules = ref([]);
    const selectedUserNames = ref([]);
    const timezoneName = ref('');

    const fetchTodaySchedules = async () => {
      console.log('Fetching schedules for users:', props.selectedUsers);
      try {
        const token = sessionStorage.getItem('accessToken');
        if (!token) {
          throw new Error('No access token found');
        }

        const today = new Date();
        today.setHours(0, 0, 0, 0); // 오늘의 00:00:00으로 설정
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1); // 내일의 00:00:00으로 설정

        const schedulePromises = props.selectedUsers.map(user =>
          axiosInstance.get(`/api/schedule/find/other/${user.id}`, {
            headers: { Authorization: `Bearer ${token}` },
          }).then(response => {
            const todaySchedules = response.data.filter(schedule => {
              const start = new Date(schedule.startTime);
              const end = new Date(schedule.endTime);

              return (
                (start < tomorrow && end > today) ||
                (start.toDateString() === today.toDateString() || end.toDateString() === today.toDateString())
              );
            });
            return {
              userId: user.id,
              userName: user.name,
              schedules: todaySchedules.map(schedule => ({
                title: schedule.title,
                start: new Date(schedule.startTime),
                end: new Date(schedule.endTime),
                categoryName: schedule.categoryName,
                userName: user.name 
              })),
            };
          })
        );

        const results = await Promise.all(schedulePromises);
        schedules.value = results;
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

      schedules.value.forEach(({ userId, schedules, userName }) => {
        schedules.forEach(schedule => {
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
                  userName: userName,
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
    };

    const fetchTimezone = async () => {
      const accessToken = sessionStorage.getItem('accessToken');
      if (!accessToken) {
        console.error('Token not found');
        return;
      }
      try {
        const response = await axiosInstance.get('/api/mypage/me', {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        timezoneName.value = response.data.timezoneName;
      } catch (error) {
        console.error('Error fetching timezone:', error.response ? error.response.data : error.message);
      }
    };


    const updateSelectedUserNames = () => {
      selectedUserNames.value = props.selectedUsers.map(user => user.name);
    };

    const getCategoryColor = (categoryName) => {
      switch (categoryName) {
        case '회사':
          return '#FA0E0E';
        case '개인':
          return '#0100FF';
        case '부서':
          return '#FFFF00';
        case '그룹':
          return '#008001';
        default:
          return '#000000'; // 기본 색상 (필요한 경우)
      }
    };

    const getTextColor = (categoryName) => {
      if (categoryName === '부서') {
        return '#000000';
      }
      return '#FFFFFF';
    };

    onMounted(() => {
      fetchTodaySchedules();
      updateSelectedUserNames();
      fetchTimezone();
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
      getCategoryColor,
      getTextColor,
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
  text-align: center;
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

.main-container {
  max-width: 900px;
  /* 고정된 최대 너비 */
  margin: 0 auto;
  /* 가운데 정렬 */
  width: 100%;
}

.schedule-table {
  width: 100%;
  min-height: 600px;
  /* 최소 높이 설정 */
  table-layout: fixed;
  /* 테이블 레이아웃 고정 */
}

.v-toolbar {
  width: 100%;
}
</style>
