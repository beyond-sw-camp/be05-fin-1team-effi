<template>
  <GroupParticipants @selectedUsers="updateSelectedUsers" />
  <ParticipantsTodaySchedule :selectedUsers="selectedUsers" />
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

<style></style>
