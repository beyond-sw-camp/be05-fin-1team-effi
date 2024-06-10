<template>
  <div>
    <table class="search-table">
      <thead>
        <tr>
          <th>날짜</th>
          <th>카테고리</th>
          <th>상태</th>
          <th>일정</th>
          <th>태그</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="search in searches" :key="search.scheduleId">
          <td>{{ new Date(search.startTime).toLocaleDateString() }}</td>
          <td>{{ search.categoryName }}</td>
          <td>{{ search.status }}</td>
          <td>{{ search.title }}</td>
          <td>
            <span v-for="tag in search.tagNames" :key="tag" class="tag">{{ tag }}</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import SearchItem from './SearchItem.vue';

export default {
  props: ['searches'],
  components: {
    SearchItem
  }
};
</script>

<style scoped>
.search-table {
  width: 100%;
  border-collapse: collapse;
}

.search-table th,
.search-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.search-table th {
  background-color: #f4f4f4;
}

.tag {
  background-color: #e0e0e0;
  border-radius: 3px;
  padding: 2px 5px;
  margin-right: 5px;
  display: inline-block;
}
</style>
