<template>
  <tr>
    <td>{{ new Date(search.startTime).toLocaleDateString() }}</td>
    <td>{{ search.categoryName }}</td>
    <td>{{ search.status }}</td>
    <td>{{ search.title }}</td>
    <td>
      <span v-for="tag in search.tagNames" :key="tag" class="tag">{{ tag }}</span>
    </td>
  </tr>
</template>

<script>
export default {
  props: ['search']
};
</script>

<style scoped>
.tag {
  background-color: #e0e0e0;
  border-radius: 3px;
  padding: 2px 5px;
  margin-right: 5px;
  display: inline-block;
}
</style>
