<template>
  <div id="app">
    <div class="schedule">
      <button @click="showMainModal = true">일정 추가</button>
      <EventDialog :show="showMainModal" @close="showMainModal = false" />
    </div>
    <div class="timezone">
      <button @click="showTimezoneModal = true">타임존 추가</button>
      <TimezoneModal :show="showTimezoneModal" @close="showTimezoneModal = false" />
    </div>
      <div class="routine">
      <button @click="showRoutineModal = true">루틴 추가</button>
      <RoutineModal v-if="showRoutineModal" :show="showRoutineModal" @close="showRoutineModal = false" @confirm="handleRoutineConfirm"/>
    </div>
  </div>
</template>

<script>
import EventDialog from '@/components/EventDialog.vue';
import TimezoneModal from '@/components/TimezoneModal.vue';
import RoutineModal from '@/components/RoutineModal.vue';

export default {
  components: {
    EventDialog,
    TimezoneModal,
    RoutineModal
  },
  data() {
    return {
      showMainModal: false,
      showTimezoneModal: false,
      showRoutineModal: false,
      selectedRoutine: null
    };
  },
  methods: {
    handleRoutineConfirm(routineData) {
      console.log('Routine Data:', routineData);
      this.selectedRoutine = routineData;
      this.showRoutineModal = false;
    }
  }
};
</script>

<style scoped>
button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
/* 버튼들 간의 간격을 띄우기 */
.schedule, .timezone, .routine {
  margin-bottom: 20px;
}
</style>
