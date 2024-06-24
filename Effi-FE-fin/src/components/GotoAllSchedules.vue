<template>
  <button class="schedules-button" @click="goToAllSchedules">전체 일정 리스트</button>
</template>

<script>
import { useRouter } from 'vue-router';

export default {
  setup() {
    const router = useRouter();

    const goToAllSchedules = () => {
      router.push('/allschedules');
    };

    return {
      goToAllSchedules,
    };
  },
};
</script>

<style>
.schedules-button {
  background-color: #99ccff;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 20px;
}
</style>
