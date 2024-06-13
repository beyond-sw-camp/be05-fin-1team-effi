<template>
  <div class="left-sidebar">
    <button class="create-group-button" @click="showModal = true">
      <span class="plus-icon">+</span>
      <span class="text">group<br>create</span>
      <span class="dropdown-icon">▼</span>
    </button>
    <CreateGroupModal :show="showModal" @close="closeModal" />

    <div class="content">
      <div v-if="!isMyPage" class="content">
        <SelectCategory @selectCategory="handleUpdateCategories"/>
        <GroupNameList @selectedGroups="handleUpdateGroups"/>
      </div>
    </div>

    <div class="bottom-content">
      <button class="logout-button" @click="logout">Logout</button>
      <img src="@/assets/logo.png" alt="Rabbit" class="rabbit-image">
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineEmits } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import CreateGroupModal from '@/components/CreateGroupModal.vue';
import SelectCategory from '@/components/SelectCategory.vue';
import GroupNameList from '@/components/GroupNameList.vue';
import { useAuthStore } from '@/stores/auth';
import axiosInstance from '@/services/axios';

const emit = defineEmits(['update-categories', 'update-groups']);
const showModal = ref(false);
const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const accessToken = ref(authStore.accessToken);
const isMyPage = computed(() => route.path === '/mypage');

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

const logout = async () => {
  try {
    await axiosInstance.post('/api/auth/signout', { token: accessToken.value });
    authStore.logout();
    router.push({ name: 'login' });
  } catch (error) {
    console.error('Error logging out:', error);
  }
};
</script>

<style scoped>
.left-sidebar {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100vh;
  /* 전체 높이 사용 */
  background-color: #FBB584;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  /* 그림자 추가 */
}

.create-group-button {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: white;
  color: #333;
  border: none;
  border-radius: 50px;
  /* 원형 모양 */
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  /* 그림자 효과 */
  cursor: pointer;
  width: 200px;
  /* 버튼의 너비 설정 */
  margin-bottom: 20px;
  /* 아래쪽 여백 추가 */
}

.create-group-button:hover {
  background-color: #f1f1f1;
}

.plus-icon {
  font-size: 24px;
  margin-right: 10px;
}

.text {
  flex: 1;
  text-align: center;
  font-size: 16px;
  line-height: 1.2;
  font-weight: bold;
}

.dropdown-icon {
  font-size: 12px;
  margin-left: 10px;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.bottom-content {
  display: flex;
  align-items: center;
  margin-top: auto;
  /* 상단 여백을 자동으로 설정하여 하단에 위치 */
}

.logout-button {
  background-color: black;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  margin-left: 10px;
  margin-right: 20px;
  /* 오른쪽 여백 추가 */
}

.rabbit-image {
  width: 100px;
  height: auto;
}
</style>
