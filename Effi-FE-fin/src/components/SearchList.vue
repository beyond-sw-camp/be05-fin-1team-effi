<template>
  <div>
    <table class="table table-striped table-bordered">
      <thead class="table-dark">
        <tr>
          <th class="date">날짜</th>
          <th class="category">카테고리</th>
          <th class="status">상태</th>
          <th class="title">일정</th>
          <th class="tags">태그</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="search in searches" :key="search.scheduleId">
          <td>{{ formatDate(search.startTime) }}</td>
          <td>
            <span :style="{ backgroundColor: getCategoryColor(search.categoryName) }" class="category-dot"></span>
            {{ search.categoryName }}
          </td>
          <td><span :class="getStatusClass(search.status)">{{ getStatus(search.status) }}</span></td>
          <td>{{ search.title }}</td>
          <td>
            <span v-for="tag in search.tagNames" :key="tag" :style="{ backgroundColor: randomColor() }"
              class="badge me-1">#{{ tag }}</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  props: ['searches'],
  methods: {
    formatDate(date) {
      return new Date(date).toISOString().split('T')[0];
    },
    getStatus(status) {
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
    },
    getStatusClass(status) {
      switch (status) {
        case 0:
          return 'badge bg-info text-dark';
        case 1:
          return 'badge bg-warning text-dark';
        case 2:
          return 'badge bg-success';
        default:
          return 'badge bg-secondary';
      }
    },
    randomColor() {
      const colors = ['#ff9999', '#99ccff', '#99ff99', '#ffcc99', '#ff99ff'];
      return colors[Math.floor(Math.random() * colors.length)];
    },
    getCategoryColor(categoryName) {
      switch (categoryName) {
        case '회사':
          return '#05CC6C';
        case '개인':
          return '#FA0E0E';
        case '부서':
          return '#F2DB0C';
        case '그룹':
          return '#060EDE';
        default:
          return '#000000'; // 기본 색상 (필요한 경우)
      }
    }
  }
};
</script>

<style scoped>
.table {
  width: 100%;
  table-layout: fixed;
  /* 테이블 너비 고정 */
}

.table th,
.table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.table th {
  background-color: #f4f4f4;
  color: #333399;
  text-align: center;
}

.table .category-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  margin-right: 5px;
}

.badge {
  color: #000;
  /* 태그의 글자 색상을 흰색으로 설정 */
}
</style>
