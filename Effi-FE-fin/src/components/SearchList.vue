<template>
  <div>
    <table class="table table-bordered">
      <thead class="table-header">
        <tr>
          <th class="date">시작 날짜</th>
          <th class="date">종료 날짜</th>
          <th class="category">카테고리</th>
          <th class="status">상태</th>
          <th class="title">일정</th>
          <th class="tags">태그</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="search in paginatedSearches" :key="search.scheduleId" @click="editSchedule(search.scheduleId)">
          <td>{{ formatDate(search.startTime) }}</td>
          <td>{{ formatDate(search.endTime) }}</td>
          <td>
            <span :style="{ backgroundColor: getCategoryColor(search.categoryName) }" class="category-dot"></span>
            {{ search.categoryName }}
          </td>
          <td><span :class="getStatusClass(search.status)">{{ getStatus(search.status) }}</span></td>
          <td>{{ search.title }}</td>
          <td>
            <span v-for="(tag, index) in search.tagNames" :key="tag" :style="{ backgroundColor: getTagColor(index) }"
              class="badge me-1">#{{ tag }}</span>
          </td>
        </tr>
      </tbody>
    </table>
    <nav aria-label="Page navigation">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <button class="page-link" @click="changePage(currentPage - 1)"> &lt; </button>
        </li>
        <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
          <button class="page-link" @click="changePage(page)">{{ page }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <button class="page-link" @click="changePage(currentPage + 1)"> &gt; </button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
export default {
  props: ['searches'],
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      tagColors: ['#ff9999', '#99ccff', '#99ff99', '#ffcc99', '#ff99ff']
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.searches.length / this.pageSize);
    },
    paginatedSearches() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.searches.slice(start, end);
    }
  },
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
    getCategoryColor(categoryName) {
      switch (categoryName) {
        case '회사':
          return '#EAFFCF';
        case '부서':
          return '#ABC4FF';
        case '그룹':
          return '#EAB9F0';
        case '개인':
          return '#FFB5C9';
        default:
          return '#000000'; // 기본 색상
      }
    },
    getTagColor(index) {
      return this.tagColors[index % this.tagColors.length];
    },
    editSchedule(scheduleId) {
      this.$emit('edit-schedule', scheduleId);
    },
    changePage(page) {
      if (page < 1) {
        this.currentPage = 1;
      } else if (page > this.totalPages) {
        this.currentPage = this.totalPages;
      } else {
        this.currentPage = page;
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
  background-color: #FBB584;
  color: #333;
  text-align: center;
}

.table td {
  background-color: #fff;
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

.pagination {
  margin-top: 20px;
}

.page-item .page-link {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.page-item.disabled .page-link {
  cursor: not-allowed;
}

.page-item.active .page-link {
  background-color: #FF7D2A;
  color: white;
  border-color: #FF7D2A;
}
</style>
