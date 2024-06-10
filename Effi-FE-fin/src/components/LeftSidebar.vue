<template>
  <div class="left-sidebar">
    <button @click="$emit('showModal')">그룹 생성</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showModal: false,
    };
  },
}
</script>

<style scoped>
.left-sidebar {
  background-color: #FBB584;
}

button {
  padding: 10px;
  margin: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
