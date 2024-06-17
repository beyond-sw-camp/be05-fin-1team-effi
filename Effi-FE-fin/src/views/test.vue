<template>
  <div id="app">
    <button @click="showMainModal = true">카테고리 선택</button>
    <MainModal :show="showMainModal" @close="showMainModal = false" />
  </div>
</template>

<script>
import MainModal from '@/components/CategoryModal.vue';

export default {
  components: {
    MainModal
  },
  data() {
    return {
      showMainModal: false,
    };
  }
}
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
</style>
