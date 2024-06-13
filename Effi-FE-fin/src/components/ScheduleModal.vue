<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="closeModal">×</button>
      <h2>{{ isEditMode ? '일정 수정하기' : '일정 추가하기' }}</h2>
      <div class="modal-body">
        <form @submit.prevent="submit">
          <div class="form-group">
            <label for="title">제목:</label>
            <input type="text" id="title" v-model="internalEvent.title" required>
          </div>
          <div class="form-group">
            <label for="context">내용:</label>
            <textarea id="context" v-model="internalEvent.context" required></textarea>
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
          <div class="form-group timezone-group">
            <label for="timezone">타임존:</label>
            <div class="timezone-value">
              {{ internalEvent.timezoneName }}
            </div>
          </div>
          <div class="form-group checkbox-group">
            <label for="repeat">반복<input type="checkbox" id="repeat" v-model="internalEvent.repeat" @change="toggleRoutineModal"></label>
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
            <div v-if="internalEvent.categoryName" class="selected-category">
              선택된 카테고리: {{ internalEvent.categoryName }}
            </div>
          </div>
          <div class="form-group">
            <label for="tag">태그</label>
            <div>
              <TagApp :schedule="internalEvent" @update-schedule="updateSchedule"/>
              <div class="tag-list">
                <span v-for="tag in internalEvent.tags" :key="tag" class="tag-item">{{ tag }}</span>
              </div>
            </div>
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
            <label for="notificationYn">1시간 전 메일 알림<input type="checkbox" id="notificationYn" v-model="internalEvent.notificationYn"></label>
          </div>
          <div class="modal-footer">
            <button type="submit">{{ isEditMode ? '수정' : '추가' }}</button>
          </div>
        </form>
      </div>
      <RoutineModal
        v-if="showRoutineModal"
        :show="showRoutineModal"
        @close="handleRoutineClose"
        @confirm="handleRoutineConfirm"
      />
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axiosInstance from '@/services/axios';
import CategoryModal from './CategoryModal.vue';
import TagApp from './TagAdd.vue';
import RoutineModal from './RoutineModal.vue';

export default {
  components: {
    CategoryModal,
    TagApp,
    RoutineModal
  },
  props: {
    show: {
      type: Boolean,
      required: true
    },
    isEditMode: {
      type: Boolean,
      default: false
    },
    event: {
      type: Object,
      default: () => ({
        title: '',
        content: '',
        startDate: '',
        startTime: '',
        endDate: '',
        endTime: '',
      })
    }
  },
  setup(props, { emit }) {
    const internalEvent = ref({
      title: '',
      context: '',
      startDate: '',
      startTime: '',
      endDate: '',
      endTime: '',
      status: 0,
      notificationYn: false,
      deleteYn: false,
      createdAt: new Date().toISOString(),
      updatedAt: null,
      categoryNo: null,
      routineId: null,
      tags: []
    });

    const searchQuery = ref('');
    const searchResults = ref([]);
    const selectedEmployees = ref([]);
    const showCategoryModal = ref(false);
    const showRoutineModal = ref(false);

    onMounted(() => {
      if (props.isEditMode) {
        Object.assign(internalEvent.value, props.event);
      } else {
        const empId = sessionStorage.getItem('empNo'); // 여기서 empNo를 가져옴
        if (empId) {
          fetchDefaultTimezone(empId);
        } else {
          console.error('empNo not found in sessionStorage');
        }
      }
    });

    const fetchDefaultTimezone = async (empId) => {
      try {
        const response = await axiosInstance.get(`/api/timezone-emp/${empId}/default`);
        console.log('API response:', response.data); // 전체 응답 데이터 로깅
        const defaultTimezone = response.data.data;
        if (defaultTimezone) {
          internalEvent.value.timezoneId = defaultTimezone.timezoneId;
          internalEvent.value.timezoneName = defaultTimezone.timezoneName;
          console.log('defaultTimezone ID:', internalEvent.value.timezoneId);
          console.log('defaultTimezone Name:', internalEvent.value.timezoneName);
        } else {
          console.error('No default timezone found');
        }
      } catch (error) {
        console.error('Error fetching default timezone:', error);
      }
    };

    const closeModal = () => {
      emit('close');
    };

    const submit = async () => {
      try {
        // 시간 형식 수정
        const formattedEvent = {
          ...internalEvent.value,
          startTime: new Date(`${internalEvent.value.startDate}T${internalEvent.value.startTime}`),
          endTime: new Date(`${internalEvent.value.endDate}T${internalEvent.value.endTime}`),
          tags: internalEvent.value.tags.map(tag => tag.name)
        };

        // 루틴 추가
        if (internalEvent.value.repeat && !internalEvent.value.routineId) {
          const routineResponse = await axios.post('http://localhost:8080/api/routine/add', {
            routineStart: formattedEvent.startTime,
            routineEnd: formattedEvent.endTime,
            routineCycle: internalEvent.value.routineCycle
          });
          formattedEvent.routineId = routineResponse.data.routineId;
        } else if (!internalEvent.value.repeat) {
          formattedEvent.routineId = null;
        }

        const apiUrl = props.isEditMode
          ? `http://localhost:8080/api/schedule/update/${props.event.id}`
          : `http://localhost:8080/api/schedule/add${formattedEvent.categoryNo === 2 ? `/dept/${formattedEvent.deptId}` : formattedEvent.categoryNo === 3 ? `/group/${formattedEvent.groupId}` : ''}`;

        console.log('Submitting to API:', apiUrl);
        console.log('Formatted event data:', formattedEvent);

        const response = await axios.post(apiUrl, formattedEvent);
        console.log('Response from API:', response.data);

        const scheduleId = response.data.scheduleId;

        // 태그 추가
        for (let tag of internalEvent.value.tags) {
          await axios.post(`http://localhost:8080/api/tag/add/${scheduleId}`, tag);
        }

        // 참여자 추가
        for (let employee of selectedEmployees.value) {
          await axios.post(`http://localhost:8080/api/participant/add`, {
            scheduleId,
            empId: employee.empNo
          });
        }

        alert('일정이 추가되었습니다.');
        closeModal();
      } catch (error) {
        console.error('Error submitting form:', error.response ? error.response.data : error.message);
        alert('일정 추가에 실패했습니다.');
      }
    };

    const handleCategorySelect = async (selection) => {
      const category = {
        1: '회사',
        2: '부서',
        3: '그룹',
        4: '개인'
      };
      internalEvent.value.categoryNo = selection.selectedOption;
      internalEvent.value.categoryName = category[selection.selectedOption] || '';

      if (selection.selectedOption === 2) {
        internalEvent.value.deptId = selection.selectedDeptId;
      } else if (selection.selectedOption === 3) {
        internalEvent.value.groupId = selection.selectedGroupId;
      }
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
        const response = await axiosInstance.get(`/api/groups/search?name=${searchQuery.value}`, config);
        const employees = response.data;
        for (let employee of employees) {
          const deptResponse = await axiosInstance.get(`/api/search/dept/${employee.deptId}`, config);
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
      }
    };

    const removeEmployee = (empNo) => {
      selectedEmployees.value = selectedEmployees.value.filter(emp => emp.empNo !== empNo);
    };

    const toggleRoutineModal = () => {
      showRoutineModal.value = internalEvent.value.repeat;
    };

    const handleRoutineClose = () => {
      showRoutineModal.value = false;
    };

    const handleRoutineConfirm = (routineData) => {
      internalEvent.value.routineId = routineData.routineId;
      internalEvent.value.routineCycle = routineData.routineCycle;
      showRoutineModal.value = false;
    };

    watch(() => props.event, (newEvent) => {
      if (props.isEditMode) {
        Object.assign(internalEvent.value, newEvent);
      }
    });

    return {
      internalEvent,
      searchQuery,
      searchResults,
      selectedEmployees,
      showCategoryModal,
      showRoutineModal,
      closeModal,
      submit,
      handleCategorySelect,
      updateSchedule,
      searchEmployees,
      selectEmployee,
      removeEmployee,
      toggleRoutineModal,
      handleRoutineClose,
      handleRoutineConfirm
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
  white-space: nowrap; /* 줄바꿈 방지 */
}

.timezone-group {
  display: flex;
  align-items: center;
}

.timezone-value {
  margin-left: 10px;
  white-space: nowrap; /* 줄바꿈 방지 */
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px; /* 태그 사이의 간격을 설정합니다 */
}

.tag-item {
  padding: 5px 10px;
  border-radius: 5px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  display: inline-block;
  background-color: var(--tag-color, #888); /* 기본 태그 색상 */
}

h2 {
  text-align: center;
  font-weight: bold;
}

label {
  font-size: 15px;
  font-weight: bold;
}
</style>
