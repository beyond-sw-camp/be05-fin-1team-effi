<template>
  <div class="status-filter">
    <button @click="filterByStatus(null)" :class="{ active: currentStatus === null }">전체</button>
    <button @click="filterByStatus('예정됨')" :class="{ active: currentStatus === '예정됨' }">예정됨</button>
    <button @click="filterByStatus('진행중')" :class="{ active: currentStatus === '진행중' }">진행중</button>
    <button @click="filterByStatus('완료됨')" :class="{ active: currentStatus === '완료됨' }">완료됨</button>
  </div>
</template>

<script>
export default {
  props: ['searches'],
  data() {
    return {
      currentStatus: null
    };
  },
  methods: {
    filterByStatus(status) {
      this.currentStatus = status;
      this.$emit('filter-status', status);
    }
  }
};
</script>

<style scoped>
.status-filter {
  display: flex;
  gap: 10px;
}

.status-filter button {
  padding: 5px 10px;
  border: none;
  background-color: #f0f0f0;
  cursor: pointer;
}

.status-filter button.active {
  background-color: #ccc;
}
</style>
