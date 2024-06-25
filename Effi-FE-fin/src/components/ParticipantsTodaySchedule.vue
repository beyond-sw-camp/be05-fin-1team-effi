<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-toolbar flat>
          <v-toolbar-title>오늘의 일정: {{ selectedUserNames.join(', ') }}</v-toolbar-title>
        </v-toolbar>
        <v-data-table :headers="headers" :items="formattedSchedules" item-value="time" class="elevation-1"
          :items-per-page="-1" hide-default-footer>
          <template v-slot:item="{ item }">
            <tr>
              <td>{{ item.time }}</td>
              <td v-for="(schedule, index) in item.schedules" :key="index">
                <div v-if="schedule" :class="['event', { first: schedule.isFirstSlot, last: schedule.isLastSlot }]"
                  :style="{ backgroundColor: getCategoryColor(schedule.categoryName) }">
                  <template v-if="schedule.isFirstSlot">
                    <strong>{{ schedule.userName }}의 일정 : {{ schedule.title }}</strong><br>
                    {{ formatTime(schedule.start) }} - {{ formatTime(schedule.end) }}<br>
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

        // 현재 날짜와 시간을 가져옴
        const today = new Date();
        today.setHours(0, 0, 0, 0); // 오늘의 00:00:00으로 설정
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1); // 내일의 00:00:00으로 설정

        const schedulePromises = props.selectedUsers.map(user =>
          axiosInstance.get(`/api/schedule/find/other/${user.id}`, {
            headers: { Authorization: `Bearer ${token}` },
          }).then(response => {
            console.log(`Schedules for user ${user.id}:`, response.data);
            const todaySchedules = response.data.filter(schedule => {
              const start = new Date(schedule.startTime);
              const end = new Date(schedule.endTime);

              console.log(`Checking schedule: ${schedule.title} (start: ${start}, end: ${end}) against today: ${today}, tomorrow: ${tomorrow}`);
              return (
                (start < tomorrow && end > today) ||
                (start.toDateString() === today.toDateString() || end.toDateString() === today.toDateString())
              );
            });
            console.log(`Filtered schedules for user ${user.id}:`, todaySchedules);
            return {
              userId: user.id,
              userName: user.name, // 유저 이름 저장
              schedules: todaySchedules.map(schedule => ({
                title: schedule.title,
                start: new Date(schedule.startTime),
                end: new Date(schedule.endTime),
                categoryName: schedule.categoryName, // 카테고리 이름 추가
                userName: user.name // 유저 이름 저장
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

      schedules.value.forEach(({ userId, schedules, userName }) => {
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
                  userName: userName, // 유저 이름 저장
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
      getCategoryColor, // 메서드 반환
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
