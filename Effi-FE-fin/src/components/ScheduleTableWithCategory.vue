<template>
  <div class="schedule-list">
    <table class="schedule-table">
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
      <tr v-for="item in schedule" :key="item.scheduleId">
        <td>{{ formatDate(item.startTime) }}</td>
        <td>
          <span :class="['category-dot', getCategoryColor(item.categoryId)]"></span>
          {{ getCategoryName(item.categoryId) }}
        </td>
        <td>{{ getStatusName(item.status) }}</td>
        <td>{{ item.title }}</td>
        <td>
          <span v-for="tag in item.tags" :key="tag.id" :class="['tag', tagColor(tag.tagId)]">{{ tag.tagName }}</span>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  props: {
    schedule: Array
  },
  data() {
    return {
      tagColors: {}, // 태그 ID와 색상을 매핑할 객체
      colors: ['pink', 'orange', 'blue', 'green', 'red', 'purple', 'yellow'] // 사용할 색상 목록
    };
  },
  methods: {
    tagColor(id) {
      if (!this.tagColors[id]) {
        const randomColor = this.colors[id % this.colors.length];
        this.tagColors[id] = randomColor;
      }
      return this.tagColors[id];
    },
    getCategoryColor(categoryId) {
      if (categoryId === 1) return 'red';
      if (categoryId === 2) return 'yellow';
      if (categoryId === 3) return 'green';
      return 'blue';
    },
    getCategoryName(categoryId) {
      if (categoryId === 1) return '회사';
      if (categoryId === 2) return '부서';
      if (categoryId === 3) return '그룹';
      return '개인';
    },
    getStatusName(status) {
      if (status === 0) return '진행중';
      if (status === 1) return '예정됨';
      return '완료';
    },
    formatDate(date) {
      const d = new Date(date);
      return d.toLocaleDateString();
    }
  }
};
</script>

<style scoped>
.schedule-table {
  width: 100%;
  border-collapse: collapse;
}

.schedule-table th, .schedule-table td {
  border: 1px solid #ddd;
  padding: 8px;
}

.schedule-table th {
  background-color: #f2f2f2;
  text-align: left;
}

.category-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 8px;
}

.tag {
  display: inline-block;
  margin-right: 4px;
  padding: 2px 4px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
}

.pink {
  background-color: pink;
}

.orange {
  background-color: orange;
}

.blue {
  background-color: blue;
}

.red {
  background-color: red;
}

.purple {
  background-color: purple;
}

.yellow {
  background-color: yellow;
}

.green {
  background-color: green;
}
</style>
