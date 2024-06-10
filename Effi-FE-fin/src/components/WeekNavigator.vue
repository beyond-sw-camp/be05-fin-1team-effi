<template>
  <div class="week-navigator">
    <button @click="prevWeek">&lt;</button>
    <span>{{ formattedDateRange }}</span>
    <button @click="nextWeek">&gt;</button>
  </div>
</template>

<script setup>
import { computed, toRefs } from 'vue';

const props = defineProps({
  startDate: {
    type: Date,
    required: true
  }
});

const emit = defineEmits(['update-date']);

const { startDate } = toRefs(props);

const formattedDateRange = computed(() => {
  const endDate = new Date(startDate.value);
  endDate.setDate(startDate.value.getDate() + 6);
  return `${startDate.value.toLocaleDateString('ko-KR')} ~ ${endDate.toLocaleDateString('ko-KR')}`;
});

const prevWeek = () => {
  const newStartDate = new Date(startDate.value);
  newStartDate.setDate(startDate.value.getDate() - 7);
  emit('update-date', newStartDate);
};

const nextWeek = () => {
  const newStartDate = new Date(startDate.value);
  newStartDate.setDate(startDate.value.getDate() + 7);
  emit('update-date', newStartDate);
};
</script>

<style scoped>
.week-navigator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

button {
  background-color: #f0f0f0;
  border: none;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
}

span {
  margin: 0 10px;
  font-size: 1.2em;
}
</style>
