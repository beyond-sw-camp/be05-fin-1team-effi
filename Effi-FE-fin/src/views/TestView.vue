<template>
  <GroupNameList/>
<!--  <GroupParticipants/>-->
</template>

<script>
import { defineComponent } from 'vue';
import GroupNameList from '@/components/GroupNameList.vue'
// import GroupParticipants from '@/components/GroupParticipants.vue'

export default defineComponent({
  components: {
    GroupNameList,
    // GroupParticipants
  },
});
</script>

<style>

</style>