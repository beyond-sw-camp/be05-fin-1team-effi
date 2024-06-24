<template>
  <v-navigation-drawer expand-on-hover rail>
    <v-list>
      <v-list-item
        :prepend-avatar="avatarUrl"
        :subtitle="userEmail"
        :rank="userRank"
        :title="userName"
      ></v-list-item>
    </v-list>

    <v-divider></v-divider>

    <v-list density="compact" nav>
      <v-list-item @click="goToHome" prepend-icon="mdi-home" title="Home" value="home"></v-list-item>
      <v-list-item prepend-icon="mdi-account-multiple" title="MyPage" @click="goToMyPage"></v-list-item>
    </v-list>

    <v-divider></v-divider>

    <v-list color="transparent">
      <v-list-item>
        <v-btn @click="showModal = true" class="create-group-button">
          <v-icon left>mdi-plus</v-icon>
          <span>group<br>create</span>
          <v-icon right>mdi-chevron-down</v-icon>
        </v-btn>
        <CreateGroupModal :show="showModal" @close="closeModal" />
      </v-list-item>
      <v-list-item v-if="!isMyPage">
        <SelectCategory @selectCategory="handleUpdateCategories" />
      </v-list-item>
      <v-list-item v-if="!isMyPage">
        <GroupNameList @selectedGroups="handleUpdateGroups" />
      </v-list-item>
      <v-list-item v-if="!isMyPage">
        <GroupNameListParticipants @selectedGroups="handleUpdateGroupsParticipants" />
      </v-list-item>
      <v-list-item>
        <v-select v-model="searchCriterion" :items="searchCriteria" label="Search By" class="mt-4" />
        <v-text-field v-model="searchQuery" placeholder="검색어를 입력하세요" @keyup.enter="search" />
        <v-btn @click="search">
          <v-icon>mdi-magnify</v-icon>
        </v-btn>
      </v-list-item>
    </v-list>

    <template v-slot:append>
      <div class="pa-2">
        <v-btn block @click="logout">
          Logout
        </v-btn>
        <v-img src="@/assets/logo.png" alt="Rabbit" class="rabbit-image"></v-img>
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script setup>
import { ref, computed, onMounted, defineEmits } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import CreateGroupModal from '@/components/CreateGroupModal.vue';
import SelectCategory from '@/components/SelectCategory.vue';
import GroupNameList from '@/components/GroupNameList.vue';
import GroupNameListParticipants from './GroupNameListParticipants.vue';
import { useAuthStore } from '@/stores/auth';
import axiosInstance from '@/services/axios';

const emit = defineEmits(['update-categories', 'update-groups', 'search-results']);
const showModal = ref(false);
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const isMyPage = computed(() => route.path === '/mypage');

const userName = ref(authStore.name);
const userEmail = ref(authStore.empNo + '@company.com'); // 예시 이메일 생성, 필요 시 변경
const avatarUrl = 'https://randomuser.me/api/portraits/women/85.jpg'; // 예시 아바타 URL

onMounted(() => {
  authStore.loadSession();
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

const handleUpdateGroupsParticipants = (groups2) => {
  console.log('Selected groupsParticipants:', groups2);
  emit('update-groupsParticipants', groups2);
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
const searchCriteria = ref(['title', 'tag', 'category']);

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
        Authorization: `Bearer ${authStore.accessToken}`,
      },
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

.rabbit-image {
  width: 100px;
  height: auto;
  margin-top: 10px;
}
</style>
