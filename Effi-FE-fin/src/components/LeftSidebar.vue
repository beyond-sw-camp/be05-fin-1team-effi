<template>
  <div>
    <button @click="$emit('showModal')">그룹 생성</button>
  </div>
</template>

<script setup>
</script>

<style scoped>
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
