<template>
  <div class="group-container">
    <div class="group-header" @click="toggleGroups">
      <h2>My Groups Participants </h2>
      <span>{{ isOpen ? '▲' : '▼' }}</span>
    </div>
    <div v-if="isOpen" class="group-list">
      <div v-for="group in groups" :key="group.id" class="group-item" @click="navigateToParticipants(group.id)">
        <label :for="`participant-${group.id}`">{{ group.name }}</label>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // useRouter 임포트
import axiosInstance from "@/services/axios.js";

export default {
  name: 'GroupNameListParticipants',
  setup() {
    const isOpen = ref(true);
    const groups = ref([]);
    const router = useRouter(); // useRouter 사용

    const toggleGroups = () => {
      isOpen.value = !isOpen.value;
    };

    const fetchGroups = async () => {
      const token = sessionStorage.getItem('accessToken'); // 토큰을 sessionStorage에서 가져오기
      if (!token) {
        console.error('No token found');
        return;
      }
      const config = {
        headers: {
          Authorization: `Bearer ${token}`
        }
      };
      try {
        const response = await axiosInstance.get(`/api/groups/find/myGroup`, config);
        console.log(response.data);
        const data = Array.isArray(response.data) ? response.data : response.data.data;
        if (Array.isArray(data)) {
          groups.value = data.map(group => ({
            id: group.groupId,
            name: group.groupName,
            selected: false
          }));
        } else {
          console.error('Expected an array but got:', data);
        }
      } catch (error) {
        console.error('Error fetching groups:', error);
        if (error.response) {
          console.error('Error details:', error.response.data);
        }
      }
    };

    const navigateToParticipants = (groupId) => {
      router.push({ name: 'groupparticipants', query: { groupId } });
    };

    onMounted(() => {
      fetchGroups(); // 컴포넌트가 마운트될 때 그룹 목록을 불러옵니다.
    });

    return {
      isOpen,
      groups,
      toggleGroups,
      fetchGroups,
      navigateToParticipants
    };
  }
};
</script>

<style scoped>
.group-container {
  padding: 10px;
  border-radius: 5px;
  width: 200px;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.group-header h2 {
  font-size: 18px;
  margin: 0;
}

.group-list {
  margin-top: 10px;
}

.group-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
  cursor: pointer;
}

.group-item label {
  flex-grow: 1;
  cursor: pointer;
}
</style>
