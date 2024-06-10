<template>
  <tr>
    <td>{{ formatDate(search.startTime) }}</td>
    <td>{{ search.categoryName }}</td>
    <td>{{ getStatusText(search.status) }}</td>
    <td>{{ search.title }}</td>
    <td>
      <span v-for="tag in search.tagNames" :key="tag" class="tag">{{ tag }}</span>
    </td>
  </tr>
</template>

<script>
export default {
  props: ['search'],
  methods: {
    formatDate(date) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString('ko-KR', options);
    },
    getStatusText(status) {
      switch (status) {
        case 0:
          return '예정됨';
        case 1:
          return '진행중';
        case 2:
          return '완료됨';
        default:
          return '알 수 없음';
      }
    }
  }
};
</script>

<style scoped>
.tag {
  background-color: #e0e0e0;
  border-radius: 4px;
  padding: 2px 8px;
  margin-right: 4px;
}
</style>
