<template>
  <header class="header navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
      <a class="navbar-brand planner-title" @click="goToHome" style="cursor: pointer;">Effi Planner</a>
      <div class="collapse navbar-collapse">
        <div class="search-container d-flex ms-auto me-auto">
          <select class="form-select me-2" v-model="searchCriterion">
            <option value="title">제목</option>
            <option value="tag">태그</option>
            <option value="category">카테고리</option>
          </select>
          <input class="form-control me-2" v-model="searchQuery" placeholder="검색어를 입력하세요" @keyup.enter="search" />
          <button class="btn btn-outline-secondary" @click="search">
            <i class="bi bi-search"></i>
          </button>
        </div>
        <div class="user-container d-flex align-items-center">
          <span @click="goToMyPage" style="cursor: pointer;">{{ authStore.name }} 님 </span>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import axiosInstance from '@/services/axios';

const router = useRouter();

const searchQuery = ref('');
const searchCriterion = ref('title');
const authStore = useAuthStore();
const emit = defineEmits(['search-results']);

onMounted(() => {
  authStore.loadSession();
  console.log('Loaded session:', {
    name: authStore.name,
    empNo: authStore.empNo,
    rank: authStore.rank,
  });
});

const search = async () => {
  console.log(`Searching for ${searchQuery.value} by ${searchCriterion.value}`);
  let url = `/api/search/${searchCriterion.value}?${searchCriterion.value}=${encodeURIComponent(searchQuery.value)}`;
  if (searchCriterion.value === 'title') {
    url = `/api/search/title?title=${encodeURIComponent(searchQuery.value)}`;
  } else if (searchCriterion.value === 'tag') {
    url = `/api/search/tag?tagName=${encodeURIComponent(searchQuery.value)}`;
  } else if (searchCriterion.value === 'category') {
    url = `/api/search/category?categoryName=${encodeURIComponent(searchQuery.value)}`;
  }

  try {
    const response = await axiosInstance.get(url, {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`
      }
    });
    const searches = response.data;
    console.log('Search results:', searches);
    emit('search-results', searches);
    router.push({ path: '/search', query: { criterion: searchCriterion.value, query: searchQuery.value } });
  } catch (error) {
    console.error('Error during search:', error);
  }
};

const goToMyPage = () => {
  router.push({ name: 'mypage' });
};

const goToHome = () => {
  router.push({ name: 'home' });
};

</script>

<style scoped>
.header {
  padding: 10px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
}

.search-container select,
.search-container input {
  width: auto;
  margin-right: 10px;
}

.user-container {
  cursor: pointer;
  font-weight: bold;
}

.planner-title {
  font-weight: bold;
  cursor: pointer;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-container {
    width: 100%;
    margin-bottom: 10px;
  }

  .user-container {
    width: 100%;
  }

  .planner-title {
    margin-bottom: 10px;
  }
}
</style>
