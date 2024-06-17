<template>
  <div class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
      <h2>그룹</h2>
      <ul class="scrollable-list">
        <li v-for="group in groups" :key="group.groupId" @click="selectGroup(group.groupId)">{{ group.groupName }}</li>
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
      groups: []
    };
  },
  created() {
    this.fetchFindGroup();
  },
  methods: {
    async fetchFindGroup() {//
      const token = sessionStorage.getItem('accessToken');
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
        const response = await axiosInstance.get('/api/groups/find/myGroup', config);
        this.groups = response.data;
      } catch (error) {
        console.error('부서 검색 오류:', error.response ? error.response.data : error.message);
      }
    },
    selectGroup(groupId) {
      this.$emit('select-group', groupId); // 선택한 부서의 ID를 상위 컴포넌트로 전달
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

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  cursor: pointer;
}

.scrollable-list {
  max-height: 150px;
  overflow-y: auto;
}
</style>
