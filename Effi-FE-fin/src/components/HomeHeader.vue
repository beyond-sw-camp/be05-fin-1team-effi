<template>
  <header class="header">
    <span class="planner-title">Effi Planner</span>
    <div class="search-container">
      <select v-model="searchCriterion">
        <option value="title">제목</option>
        <option value="tag">태그</option>
        <option value="category">카테고리</option>
      </select>
      <input v-model="searchQuery" placeholder="검색어를 입력하세요" @input="search" />
    </div>
    <div class="user-container">
      <span>{{ username }}</span>
      <button @click="logout">로그아웃</button>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const searchQuery = ref('');
const searchCriterion = ref('title');
const username = ref('');

onMounted(async () => {
  try {
    const response = await axios.get('/api/user');
    username.value = response.data.username;
  } catch (error) {
    console.error('Error fetching user data:', error);
  }
});

const search = () => {
  console.log(`Searching for ${searchQuery.value} by ${searchCriterion.value}`);
  // 여기에 검색 로직을 추가하세요
};

const logout = async () => {
  try {
    await axios.post('/api/logout');
    window.location.href = '/login';
  } catch (error) {
    console.error('Error logging out:', error);
  }
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between; /* 양쪽 끝에 배치 */
  align-items: center;
  padding: 10px;
  background-color: #f8f9fa;
  width: 100%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  position: fixed;
  top: 0;
  left: 0; /* 화면 왼쪽에 고정 */
  transform: none; /* 가운데 정렬 제거 */
}

.search-container {
  display: flex;
  align-items: center;
}

.user-container {
  display: flex;
  align-items: center;
}

.planner-title {
  margin-right: 20px;
  font-weight: bold;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .header {
    flex-direction: column; /* 작은 화면에서는 세로로 배치 */
    align-items: flex-start;
  }
  .search-container {
    width: 100%; /* 전체 너비 차지 */
    margin-bottom: 10px;
  }
  .user-container {
    width: 100%; /* 전체 너비 차지 */
  }
  .planner-title {
    margin-right: 0;
    margin-bottom: 10px;
  }
}

</style>
