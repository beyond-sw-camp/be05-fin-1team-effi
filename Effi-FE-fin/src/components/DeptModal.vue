<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
<!--      <h2>부서 선택</h2>-->
      <ul>
        <li>마케팅팀</li>
        <li>영업팀</li>
        <li>인사팀</li>
        <li>연구개발팀</li>
      </ul>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  created() {
    this.fetchFindDept();
  },
  methods: {
    async fetchFindDept() {
      try {
        const response = await axios.get('http://localhost:8080/api/search/dept');

        // 부서 전체 출력 필요 (화면상)

        this.$emit('close');
      } catch (error) {
        console.error('그룹 생성 오류:', error.response ? error.response.data : error.message);
      }
    }
  }
}
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

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  cursor: pointer;
}
</style>
