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
    margin-left: 80px;
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
  min-height: 600px;
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
  }

  .schedule-container {
    max-width: 100%;
  }
}
</style>
