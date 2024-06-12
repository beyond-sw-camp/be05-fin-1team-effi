<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
      <h2>일정 상세 보기</h2>
      <div class="modal-body">
        <!-- Add Schedule Form -->
        <form @submit.prevent="submit">
          <!-- Form Fields -->
          <div class="form-group">
            <label for="title">제목:</label>
            <input type="text" id="title" v-model="internalEvent.title" required>
          </div>
          <div class="form-group">
            <label for="content">내용:</label>
            <textarea id="content" v-model="internalEvent.content" required></textarea>
          </div>
          <div class="form-group">
            <label for="startDate">시작일:</label>
            <input type="date" id="startDate" v-model="internalEvent.startDate" required>
          </div>
          <div class="form-group">
            <label for="startTime">시작 시간:</label>
            <input type="time" id="startTime" v-model="internalEvent.startTime" required>
          </div>
          <div class="form-group">
            <label for="endDate">종료일:</label>
            <input type="date" id="endDate" v-model="internalEvent.endDate" required>
          </div>
          <div class="form-group">
            <label for="endTime">종료 시간:</label>
            <input type="time" id="endTime" v-model="internalEvent.endTime" required>
          </div>
          <div class="form-group">
            <label for="timezone">타임존</label>
            <div>
              {{ internalEvent.timezoneName }}
            </div>
          </div>
          <div class="form-group checkbox-group">
            <label for="repeat">반복</label>
            <input type="checkbox" id="repeat" v-model="internalEvent.repeat">
          </div>
          <div class="form-group">
            <label for="participants">참여자</label>
            <div class="input-group">
              <input type="text" id="participants" placeholder="추가할 사원을 검색하세요" v-model="searchQuery" class="group-input" />
              <button type="button" class="search-button" @click="searchEmployees">🔍</button>
            </div>
            <ul v-if="searchResults.length" class="search-results">
              <li v-for="employee in searchResults" :key="employee.id" class="search-result-item" @click="selectEmployee(employee)">
                {{ employee.name }}/{{ employee.deptName }}/{{ employee.rank }}
              </li>
            </ul>
            <div v-if="selectedEmployees.length" class="selected-employees">
              <p>선택된 사원:</p>
              <ul>
                <li v-for="emp in selectedEmployees" :key="emp.empNo">
                  {{ emp.name }}
                  <button @click="removeEmployee(emp.empNo)" class="remove-button">×</button>
                </li>
              </ul>
            </div>
          </div>
          <div class="form-group">
            <label for="category">카테고리</label>
            <button @click="showCategoryModal = true" type="button" id="category">카테고리 추가하기</button>
            <CategoryModal :show="showCategoryModal" @close="showCategoryModal = false" @select="handleCategorySelect" />
          </div>
          <div class="form-group">
            <label for="tag">태그</label>
            <TagApp id="tag" :schedule="internalEvent" @update-schedule="updateSchedule" />
          </div>
          <div class="form-group">
            <label for="status">진행상황</label>
            <select id="status" v-model="internalEvent.status">
              <option value="0">예정됨</option>
              <option value="1">진행중</option>
              <option value="2">완료됨</option>
            </select>
          </div>
          <div class="form-group checkbox-group">
            <label for="emailAlert">1시간전 메일 알림</label>
            <input type="checkbox" id="emailAlert" v-model="internalEvent.emailAlert">
          </div>
          <div class="modal-footer">
            <button type="submit">{{ isEditMode ? '수정' : '추가' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import CategoryModal from './CategoryModal.vue';
import TagApp from './TagAdd.vue';

export default {
  components: {
    CategoryModal,
    TagApp
  },
  props: {
    show: {
      type: Boolean,
      required: true
    },
    isEditMode: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    const internalEvent = ref({
      title: '',
      content: '',
      startDate: '',
      startTime: '',
      endDate: '',
      endTime: '',
      timezoneId: null,
      timezoneName: '',
      repeat: false,
      category: null,
      tags: [],
      status: 0,
      emailAlert: false,
      groupId: null
    });

    const searchQuery = ref('');
    const searchResults = ref([]);
    const selectedEmployees = ref([]);
    const showCategoryModal = ref(false);

    onMounted(() => {
      const empId = sessionStorage.getItem('empNo');
      if (empId) {
        fetchDefaultTimezone(empId);
      }
    });

    const fetchDefaultTimezone = async (empId) => {
      try {
        const response = await axios.get(`http://localhost:8080/api/timezone-emp/${empId}/default`);
        const defaultTimezone = response.data.data.timezone;
        if (defaultTimezone) {
          internalEvent.value.timezoneId = defaultTimezone.timezoneId;
          internalEvent.value.timezoneName = defaultTimezone.timezone.timezoneName;
        }
      } catch (error) {
        console.error('Error fetching default timezone:', error);
      }
    };

    const closeModal = () => {
      emit('close');
    };

    const submit = () => {
      console.log('Form submitted', internalEvent.value);
      closeModal();
    };

    const handleCategorySelect = (selection) => {
      internalEvent.value.category = selection;
      showCategoryModal.value = false;
    };

    const updateSchedule = (tags) => {
      internalEvent.value.tags = tags;
    };

    const searchEmployees = async () => {
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
        const response = await axios.get(`http://localhost:8080/api/groups/search?name=${searchQuery.value}`, config);
        const employees = response.data;
        for (let employee of employees) {
          const deptResponse = await axios.get(`http://localhost:8080/api/search/dept/${employee.deptId}`, config);
          employee.deptName = deptResponse.data;
        }
        searchResults.value = employees;
      } catch (error) {
        console.error('Error searching employees:', error.response ? error.response.data : error.message);
      }
    };

    const selectEmployee = (employee) => {
      if (!selectedEmployees.value.some(emp => emp.empNo === employee.empNo)) {
        selectedEmployees.value.push(employee);
        console.log('Selected employees:', selectedEmployees.value); // 선택된 사원 목록 로그 출력
      }
    };

    const removeEmployee = (empNo) => {
      selectedEmployees.value = selectedEmployees.value.filter(emp => emp.empNo !== empNo);
      console.log('Selected employees after removal:', selectedEmployees.value); // 제거 후 선택된 사원 목록 로그 출력
    };

    return {
      internalEvent,
      searchQuery,
      searchResults,
      selectedEmployees,
      showCategoryModal,
      closeModal,
      submit,
      handleCategorySelect,
      updateSchedule,
      searchEmployees,
      selectEmployee,
      removeEmployee
    };
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
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 600px;
  max-height: 90vh;
  overflow-y: auto;
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

h2 {
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input, select, textarea {
  width: calc(100% - 20px);
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
  background-color: #FDDAC1; /* 입력 필드 배경색 */
}

textarea {
  height: 80px;
  resize: vertical;
}

button.update-button, button#category, button#group {
  display: inline-block;
  margin-top: 10px;
  padding: 10px;
  background-color: #FBB584;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
}

button.update-button:hover, button#category:hover, button#group:hover {
  background-color: #FBB584;
}

.modal-footer {
  text-align: right;
}

.modal-footer button {
  padding: 10px 20px;
  background-color: #FBB584; /* 수정하기 버튼 배경색 */
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal-footer button:hover {
  background-color: #EC971F;
}

.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.group-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
}

.search-button {
  padding-left: 5px;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  margin-left: 5px;
}

.search-results {
  list-style: none;
  padding: 0;
  margin: 10px 0;
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #FFF;
}

.search-result-item {
  padding: 10px;
  border-bottom: 1px solid #ccc;
  cursor: pointer;
  background-color: #FDDAC1;
}

.search-result-item:last-child {
  border-bottom: none;
}

.selected-employees {
  margin-top: 20px;
}

.selected-employees ul {
  list-style: none;
  padding: 0;
}

.selected-employees li {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.selected-employees button.remove-button {
  margin-left: 10px;
  padding: 0 5px;
  background-color: #ff4d4d;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox-group label {
  margin-right: 10px;
}
</style>
