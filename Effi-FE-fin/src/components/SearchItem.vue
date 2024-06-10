<!-- src/components/SearchItem.vue -->
<template>
  <div>
    <h3>{{ search.title }}</h3>
    <p>{{ search.context }}</p>
    <p>{{ new Date(search.startTime).toLocaleString() }} - {{ new Date(search.endTime).toLocaleString() }}</p>
    <p>Status: {{ search.status }}</p>
  </div>
</template>

<script>
export default {
  props: ['search']
};
</script>
