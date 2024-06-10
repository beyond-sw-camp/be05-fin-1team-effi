<template>
  <div id="app">
    <SelectCategory @update-schedule="updateSchedule"></SelectCategory>
    <ScheduleTable :schedule="schedule"></ScheduleTable>
  </div>
</template>

<script>
import SelectCategory from "@/components/SelectCategory.vue";
import ScheduleTable from "@/components/ScheduleTableWithCategory.vue"

export default {
  components: {
    SelectCategory,
    ScheduleTable
  },
  data() {
    return {
      schedule: []
    };
  },
  methods: {
    updateSchedule(scheduleData) {
      this.schedule = scheduleData;
    }
  }
};
</script>

<style>
/* Add your styles here */
</style>
