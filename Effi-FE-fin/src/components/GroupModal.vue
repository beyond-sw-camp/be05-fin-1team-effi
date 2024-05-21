<template>
  <div class="modal">
    <div class="modal-content">
      <span class="close" @click="closeModal">&times;</span>
      <h2>그룹 생성</h2>
      <input v-model="groupName" placeholder="그룹명 입력" />
      <EmployeeSearch @selected-employees="setSelectedEmployees" />
      <button @click="createGroup">생성하기</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import EmployeeSearch from './EmployeeSearch.vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['close']);

const groupName = ref('');
const selectedEmployees = ref([]);

const closeModal = () => {
  emit('close');
};

const setSelectedEmployees = (employees) => {
  selectedEmployees.value = employees;
};

const createGroup = async () => {
  const groupRequestDTO = {
    groupName: groupName.value,
    employeeIds: selectedEmployees.value.map(emp => emp.id)
  };

  try {
    const response = await axios.post('/api/groups', groupRequestDTO);
    alert(response.data.msg);
    closeModal();
  } catch (error) {
    alert('그룹 생성 실패');
  }
};
</script>

<style scoped>
.modal {
  display: block;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0,0,0);
  background-color: rgba(0,0,0,0.4);
}
.modal-content {
  background-color: #fefefe;
  margin: 15% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
}
.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}
.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
</style>
