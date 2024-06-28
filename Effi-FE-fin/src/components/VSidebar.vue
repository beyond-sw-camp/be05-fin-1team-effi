<template>
  <v-navigation-drawer expand-on-hover rail style="background-color: #FBB584;" :width="300">
    <v-list>
      <v-list-item :prepend-avatar="avatarUrl" :subtitle="userEmail" :title="`${userName} (${userRank})`"></v-list-item>
    </v-list>

    <v-divider />

    <v-list color="transparent">
      <v-list-item>
        <select class="form-select me-2" v-model="searchCriterion">
          <option value="title">제목</option>
          <option value="tag">태그</option>
          <option value="category">카테고리</option>
        </select>
        <v-text-field v-model="searchQuery" placeholder="검색어를 입력하세요" @keyup.enter="search" />
        <v-btn @click="search">
          <v-icon>mdi-magnify</v-icon>
        </v-btn>
      </v-list-item>
    </v-list>

    <v-divider />

    <v-list density="compact" nav>
      <v-list-item @click="goToHome" prepend-icon="mdi-home" title="Home" value="home"></v-list-item>
      <v-list-item @click="goToMyPage" prepend-icon="mdi-account" title="My Page" value="mypage"></v-list-item>
      <v-list-item @click="goToAllSchedules" prepend-icon="mdi-calendar" title="All Schedules"
        value="allschedules"></v-list-item>
      <!-- Category Selection -->
      <v-list-group v-model="openCategories">
        <template v-slot:activator="{ props }">
          <v-list-item v-bind="props" prepend-icon="mdi-view-list" title="Category"
            @click="toggleGroup('openCategories')"></v-list-item>
        </template>
        <v-list v-show="openCategories" class="scrollable-list light-scroll">
          <SelectCategory @selectCategory="handleUpdateCategories" />
        </v-list>
      </v-list-group>

      <!-- Group Selection -->
      <v-list-group v-model="openMyGroups">
        <template v-slot:activator="{ props }">
          <v-list-item v-bind="props" prepend-icon="mdi-account-group" title="My Groups"
            @click="toggleGroup('openMyGroups')"></v-list-item>
        </template>
        <v-list v-show="openMyGroups" class="scrollable-list light-scroll">
          <GroupNameList @selectedGroups="handleUpdateGroups"
            @update-groupsParticipants="handleUpdateGroupsParticipants"
            @update-groupsSchedules="handleUpdateGroupsSchedules" />
        </v-list>
      </v-list-group>

      <v-list-item prepend-icon="mdi-plus" title="Create Group" @click="showModal = true"></v-list-item>
    </v-list>

    <v-divider></v-divider>

    <template v-slot:append>
      <div class="logout-container">
        <v-img :src="logoImage" alt="Rabbit" class="rabbit-image" @click="logout"></v-img>
        <v-btn  class="logout-button" @click="logout">
          Logout
        </v-btn>
      </div>
    </template>
  </v-navigation-drawer>

  <CreateGroupModal :show="showModal" @close="closeModal" />
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue';
import { useRouter } from 'vue-router';
import CreateGroupModal from '@/components/CreateGroupModal.vue';
import SelectCategory from '@/components/SelectCategory.vue';
import GroupNameList from '@/components/GroupNameList.vue';
import { useAuthStore } from '@/stores/auth';
import axiosInstance from '@/services/axios';
import logoImage from '@/assets/logo.png';


const emit = defineEmits(['update-categories', 'update-groups', 'update-groupsParticipants', 'update-groupsSchedules', 'search-results']);
const showModal = ref(false);
const openCategories = ref(false);
const openMyGroups = ref(false);
const router = useRouter();
const authStore = useAuthStore();

const userName = ref('');
const userEmail = ref('');
const userRank = ref('');
const avatarUrl = 'https://randomuser.me/api/portraits/women/85.jpg';

const fetchUserEmail = async () => {
  try {
    const response = await axiosInstance.get('/api/mypage/me', {
      headers: {
        Authorization: `Bearer ${authStore.accessToken}`
      }
    });
    userEmail.value = response.data.email;
    userName.value = response.data.name;
    userRank.value = response.data.rank;
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};

onMounted(() => {
  authStore.loadSession();
  fetchUserEmail();
});

const closeModal = () => {
  showModal.value = false;
};

const handleUpdateCategories = (categories) => {
  console.log('Selected categories:', categories);
  emit('update-categories', categories);
};

const handleUpdateGroups = (groups) => {
  console.log('Selected groups:', groups);
  emit('update-groups', groups);
};

const handleUpdateGroupsParticipants = (groups) => {
  console.log('Selected groupsParticipants:', groups);
  emit('update-groupsParticipants', groups);
};

const handleUpdateGroupsSchedules = (groups) => {
  console.log('Selected groupsSchedules:', groups);
  emit('update-groupsSchedules', groups);
};

const logout = async () => {
  try {
    await axiosInstance.post('/api/auth/signout', { token: authStore.accessToken });
    authStore.logout();
    router.push({ name: 'login' });
  } catch (error) {
    console.error('Error logging out:', error);
  }
};

const searchQuery = ref('');
const searchCriterion = ref('title');

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

const goToAllSchedules = () => {
  router.push({ name: 'allschedules' });
};

const toggleGroup = (group) => {
  if (group === 'openCategories') {
    openCategories.value = !openCategories.value;
  } else if (group === 'openMyGroups') {
    openMyGroups.value = !openMyGroups.value;
  }
};
</script>

<style scoped>
.create-group-button {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: white;
  color: #333;
  border: none;
  border-radius: 50px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  width: 200px;
  margin-bottom: 20px;
}

.create-group-button:hover {
  background-color: #f1f1f1;
}

.logout-container {
  display: flex;
  align-items: center;
  padding: 10px;
}

.rabbit-image {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.logout-button {
  flex-shrink: 0;
  margin-left:10px;
  margin-right:10px;
  max-width: 100px;
  white-space: nowrap;
}

.scrollable-list {
  max-height: 200px;
  overflow-y: auto;
  background-color: #FBB584
}

.light-scroll::-webkit-scrollbar {
  width: 6px;
}

.light-scroll::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

.light-scroll::-webkit-scrollbar-thumb {
  background-color: #888;
}

.light-scroll::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}

/* 추가: 마우스 오버 효과 */
.v-list-item:hover {
  background-color: #FF7D2A !important;
}

.v-dialog--active {
  right: 300px !important;
  /* 사이드바의 너비 만큼 이동 */
}
</style>
