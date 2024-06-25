<template>
  <v-list>
    <v-list-item
      v-for="group in groups"
      :key="group.id"
      @click="navigateToParticipants(group.id)"
    >
      <template v-slot:prepend>
        <v-list-item-action start>
          <v-checkbox-btn :model-value="false" :disabled="true"></v-checkbox-btn>
        </v-list-item-action>
      </template>
      <v-list-item-title>{{ group.name }}</v-list-item-title>
    </v-list-item>
  </v-list>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // useRouter 임포트
import axiosInstance from "@/services/axios.js";

export default {
  name: 'GroupNameListParticipants',
  setup() {
    const groups = ref([]);
    const router = useRouter(); // useRouter 사용

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
      groups,
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

.group-list {
  margin-top: 10px;
  max-height: 200px;
  overflow-y: auto;
}

.group-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
  cursor: pointer;
}

.group-item:hover {
  background-color: #e0e0e0;
}

.group-item label {
  flex-grow: 1;
  cursor: pointer;
}
</style>
