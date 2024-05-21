<template>
  <div>
    <input v-model="searchName" @input="searchEmployees" placeholder="직원 이름 검색" />
    <ul>
      <li v-for="employee in employees" :key="employee.id">
        <label>
          <input type="checkbox" :value="employee" @change="toggleEmployeeSelection" />
          {{ employee.name }}
        </label>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const emit = defineEmits(['selected-employees']);

const searchName = ref('');
const employees = ref([]);
const selectedEmployees = ref([]);

const searchEmployees = async () => {
  try {
    const response = await axios.get('/api/groups/search', { params: { name: searchName.value } });
    employees.value = response.data;
  } catch (error) {
    alert('직원 검색 실패');
  }
};

const toggleEmployeeSelection = (event) => {
  const employee = JSON.parse(event.target.value);
  if (event.target.checked) {
    selectedEmployees.value.push(employee);
  } else {
    selectedEmployees.value = selectedEmployees.value.filter(emp => emp.id !== employee.id);
  }
  emit('selected-employees', selectedEmployees.value);
};
</script>
