<template>
  <div class="status-filter">
    <button @click="setStatus(null)">전체</button>
    <button @click="setStatus(0)">예정됨</button>
    <button @click="setStatus(1)">진행중</button>
    <button @click="setStatus(2)">완료됨</button>
  </div>
</template>

<script setup>
const emit = defineEmits(['filter-status']);

const setStatus = (status) => {
  emit('filter-status', status);
};
</script>

<style scoped>
.status-filter {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

button {
  background-color: #f0f0f0;
  border: none;
  padding: 8px;
  cursor: pointer;
  margin: 0 5px;
  border-radius: 4px;
}
</style>
