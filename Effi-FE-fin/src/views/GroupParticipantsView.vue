<template>
  <div class="main-container">
    <div class="participants-container">
      <GroupParticipants @selectedUsers="updateSelectedUsers" />
    </div>
    <div class="schedule-container">
      <ParticipantsTodaySchedule :selectedUsers="selectedUsers" />
    </div>
  </div>
</template>

<script>
import GroupParticipants from '../components/GroupParticipants.vue';
import ParticipantsTodaySchedule from '../components/ParticipantsTodaySchedule.vue';

export default {
  components: {
    GroupParticipants,
    ParticipantsTodaySchedule,
  },
  data() {
    return {
      selectedUsers: []
    };
  },
  methods: {
    updateSelectedUsers(users) {
      console.log('Updating selected users:', users);
      this.selectedUsers = users;
    }
  }
};
</script>

<style>
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
}

.participants-container {
  width: 300px;
  /* 고유 사이즈 */
  flex: 0 0 auto;
  /* 고유 사이즈를 유지 */
  margin: 10px;
}

.schedule-container {
  width: 100%;
  max-width: 900px;
  /* 일정 컴포넌트의 최대 너비 설정 */
  min-height: 600px;
  /* 최소 높이 설정 */
  margin: 10px;
  flex: 1;
}

@media (min-width: 768px) {
  .main-container {
    flex-direction: row;
    justify-content: center;
    align-items: flex-start;
  }

  .participants-container {
    margin-right: 20px;
    /* 간격 추가 */
  }

  .schedule-container {
    max-width: 100%;
    /* 최대 너비를 100%로 설정 */
  }
}
</style>
