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
          <td>{{ formatDate(search.startTime) }}</td>
          <td>
            <span :style="{ backgroundColor: getCategoryColor(search.categoryName) }" class="category-dot"></span>
            {{ search.categoryName }}
          </td>
          <td><span :class="getStatusClass(search.status)">{{ getStatus(search.status) }}</span></td>
          <td>{{ search.title }}</td>
          <td>
            <span v-for="tag in search.tagNames" :key="tag" :style="{ backgroundColor: randomColor() }" class="tag">{{
              '#' + tag }}</span>
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
          return 'status-planned';
        case 1:
          return 'status-in-progress';
        case 2:
          return 'status-completed';
        default:
          return '';
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
  color: #333399;
  text-align: center;
}

.tag {
  border-radius: 3px;
  padding: 2px 5px;
  margin-right: 5px;
  display: inline-block;
}

.status-planned {
  border-radius: 3px;
  padding: 2px 5px;
  margin-right: 5px;
  background-color: #b3e5fc;
  display: inline-block;
}

.status-in-progress {
  border-radius: 3px;
  padding: 2px 5px;
  margin-right: 5px;
  background-color: #ccff90;
  display: inline-block;
}

.status-completed {
  border-radius: 3px;
  padding: 2px 5px;
  margin-right: 5px;
  background-color: #ffab91;
  display: inline-block;
}

.category-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  margin-right: 5px;
}
</style>
