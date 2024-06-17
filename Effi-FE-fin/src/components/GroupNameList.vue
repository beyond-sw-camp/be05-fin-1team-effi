<template>
  <div class="group-container">
    <div class="group-header" @click="toggleGroups">
      <h2>My Groups</h2>
      <span>{{ isOpen ? '▲' : '▼' }}</span>
    </div>
    <div v-if="isOpen" class="group-list">
      <div v-for="group in groups" :key="group.id" class="group-item">
        <input
          type="checkbox"
          :id="group.id"
          v-model="group.selected"
        />
        <label :for="group.id">{{ group.name }}</label>
        <button v-if="group.selected" @click="removeGroup(group.id)" class="remove-button">━</button>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/services/axios.js";
import { watch } from 'vue';

export default {
  data() {
    return {
      isOpen: true,
      groups: []
    };
  },
  methods: {
    toggleGroups() {
      this.isOpen = !this.isOpen;
    },
    async removeGroup(groupId) {
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
        // 서버에 그룹 탈퇴 요청을 보냅니다.
        await axiosInstance.delete(`/api/groups/${groupId}/employees`, config);
        console.log(`Group with id ${groupId} has been removed.`);
        // 그룹 리스트를 다시 조회합니다.
        this.fetchGroups();
      } catch (error) {
        console.error(`Error removing group with id ${groupId}:`, error);
        if (error.response) {
          console.error('Error details:', error.response.data);
        }
      }
    },
    async fetchGroups() {
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
          this.groups = data.map(group => ({
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
    }
  },
  mounted() {
    this.fetchGroups(); // 컴포넌트가 마운트될 때 그룹 목록을 불러옵니다.
  },
  watch: {
    groups: {
      handler(newGroups) {
        const selectedGroupIds = newGroups.filter(group => group.selected).map(group => group.id);
        this.$emit('selectedGroups', selectedGroupIds);
      },
      deep: true,
    }
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
}

.group-item input[type="checkbox"] {
  margin-right: 10px;
}

.group-item label {
  flex-grow: 1;
  cursor: pointer;
}

.remove-button {
  background: none;
  border: none;
  color: black;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}
</style>
