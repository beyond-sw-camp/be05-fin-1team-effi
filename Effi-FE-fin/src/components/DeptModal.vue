<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
      <h2>부서</h2>
      <ul class="scrollable-list">
        <li v-for="dept in departments" :key="dept.deptId" @click="selectDept(dept.deptId)">{{ dept.deptName }}</li>
      </ul>
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/services/axios';

export default {
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      departments: []
    };
  },
  created() {
    this.fetchFindDept();
  },
  methods: {
    async fetchFindDept() {
      try {
        const response = await axiosInstance.get('/api/search/dept');
        this.departments = response.data;
      } catch (error) {
        console.error('부서 검색 오류:', error.response ? error.response.data : error.message);
      }
    },
    selectDept(deptId) {
      this.$emit('select-dept', deptId); // 선택한 부서의 ID를 상위 컴포넌트로 전달
    }
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-container {
  background: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 200px;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  position: absolute;
  top: 10px;
  right: 10px;
}

h2, p {
  text-align: center; /* 텍스트 가운데 정렬 */
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  cursor: pointer;
}

.scrollable-list {
  max-height: 150px; /* 스크롤이 나타나는 최대 높이 설정 */
  overflow-y: auto; /* 세로 스크롤 활성화 */
}
</style>
