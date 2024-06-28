<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="closeModal">✕</button>
      <h2>일정 추가하기</h2>
      <div class="modal-body">
        <form @submit.prevent="submit">
          <!-- Form Fields -->
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
          <div class="form-group">
            <label for="timezone">타임존:</label>
            <div class="timezone-value">
              {{ internalEvent.timezoneName }}
            </div>
          </div>
          <div class="form-group checkbox-group">
            <label for="repeat">반복<input type="checkbox" id="repeat" v-model="internalEvent.repeat" @change="toggleRoutineModal"></label>
          </div>
          <div class="form-group">
            <label for="category">카테고리</label>
            <button @click="showCategoryModal = true" type="button" id="category">카테고리 추가하기</button>
            <CategoryModal :show="showCategoryModal" :schedule-id="null" @close="showCategoryModal = false" @select="handleCategorySelect" />
            <div v-if="internalEvent.categoryName" class="selected-category">
              선택된 카테고리: {{ internalEvent.categoryName }}
            </div>
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
            <label for="tag">태그</label>
            <div>
              <TagAdd :schedule="internalEvent" @update-schedule="updateSchedule"/>
              <div class="tag-list">
                <span v-for="tag in internalEvent.tags" :key="tag" :style="{ backgroundColor: tagColors[tag] }" class="tag-item">{{ tag }}</span>
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
            <button type="submit" class="update-button">추가</button>
          </div>
        </form>
      </div>
      <RoutineModal
        v-if="showRoutineModal"
        :show="showRoutineModal"
        @close-routine="handleRoutineClose"
        @confirm-routine="handleRoutineConfirm"
      />
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue';
import axiosInstance from '@/services/axios';
import CategoryModal from './CategoryModal.vue';
import TagAdd from './TagAdd.vue';
import RoutineModal from './RoutineModal.vue';

export default {
  components: {
    CategoryModal,
    TagAdd,
    RoutineModal
  },
  props: {
    show: {
      type: Boolean,
      required: true
    },
    initialDate: {
      type: String,
      default: ''
    }
  },
  setup(props, { emit }) {
    const internalEvent = ref({
      title: '',
      context: '',
      startDate: props.initialDate,
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
    const tagColors = reactive({});

    onMounted(() => {
      const empId = sessionStorage.getItem('empNo'); // 여기서 empNo를 가져옴
      if (empId) {
        fetchDefaultTimezone(empId);
      } else {
        console.error('empNo not found in sessionStorage');
      }
    });

    watch(() => props.initialDate, (newDate) => {
      internalEvent.value.startDate = newDate;
    });

    const fetchDefaultTimezone = async (empId) => {
      try {
        const response = await axiosInstance.get(`/api/timezone-emp/${empId}/default`);
        const defaultTimezone = response.data.data;
        if (defaultTimezone) {
          internalEvent.value.timezoneId = defaultTimezone.timezoneId;
          internalEvent.value.timezoneName = defaultTimezone.timezoneName;
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
        const formattedEvent = {
          ...internalEvent.value,
          startTime: new Date(`${internalEvent.value.startDate}T${internalEvent.value.startTime}`),
          endTime: new Date(`${internalEvent.value.endDate}T${internalEvent.value.endTime}`),
          tags: internalEvent.value.tags.map(tag => tag.name)
        };

        // Log the formattedEvent to check its contents
        console.log("Formatted Event: ", formattedEvent);

        if (internalEvent.value.repeat && !internalEvent.value.routineId) {
          const routineResponse = await axiosInstance.post('/api/routine/add', {
            routineStart: formattedEvent.startTime,
            routineEnd: formattedEvent.endTime,
            routineCycle: internalEvent.value.routineCycle
          });
          formattedEvent.routineId = routineResponse.data.routineId;
        } else if (!internalEvent.value.repeat && internalEvent.value.routineId) {
          await axiosInstance.put(`/api/routine/delete/${internalEvent.value.routineId}`);
          formattedEvent.routineId = null;
        }

        // // Fetch category details by categoryNo and update categoryId
        const categoryResponse = await axiosInstance.get(`/api/category/find/${formattedEvent.categoryNo}`);
        console.log('categoryResponse:', categoryResponse.data) // 카테고리 응답 확인
        if (categoryResponse.data) {
          formattedEvent.categoryNo = categoryResponse.data.categoryId;
        }

        let apiUrl;
        switch (formattedEvent.categoryNo) {
          case 1:
            apiUrl = `/api/schedule/add/company`;
            break;
          case 2:
            apiUrl = `/api/schedule/add/dept/${formattedEvent.deptId}`;
            break;
          case 3:
            apiUrl = `/api/schedule/add/group/${formattedEvent.groupId}`;
            break;
          case 4:
            apiUrl = `/api/schedule/add`;
            break;
          default:
            apiUrl = `/api/schedule/add`;
        }

        const response = await axiosInstance.post(apiUrl, formattedEvent);
        const scheduleId = response.data.scheduleId;
        console.log("scheduleId: ", scheduleId);

        for (let tag of internalEvent.value.tags) {
          await axiosInstance.post(`/api/tag/add/${scheduleId}`, { "tag": tag });
        }

        // if (formattedEvent.categoryNo === 2) {
        //   await fetchAndAddDeptMembers(scheduleId, formattedEvent.deptId);
        // }

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
        console.log('selectedDeptId:', selection.selectedDeptId);
      } else if (selection.selectedOption === 3) {
        internalEvent.value.groupId = selection.selectedGroupId;
      }

      showCategoryModal.value = false;
    };

    const updateSchedule = ({ tags, tagColors: newTagColors }) => {
      internalEvent.value.tags = tags;
      for (let tag in newTagColors) {
        if (!tagColors[tag]) {
          tagColors[tag] = newTagColors[tag];
        }
      }
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

    // const fetchAndAddDeptMembers = async (scheduleId, deptId) => {
    //   try {
    //     console.log('fetchAndAddDeptMembers called');
    //     const response = await axiosInstance.post(`/api/participant/add/dept/${deptId}`, null, {
    //       params: {
    //         scheduleId: scheduleId
    //       }
    //     });
    //     console.log('fetchAndAddDeptMembers response:', response.data);
    //   } catch (error) {
    //     console.error('Error fetching and adding dept members:', error.response ? error.response.data : error.message);
    //     alert('부서원 추가에 실패했습니다.');
    //   }
    // };

    const toggleRoutineModal = () => {
      console.log('toggleRoutineModal called');
      showRoutineModal.value = internalEvent.value.repeat;
    };

    const handleRoutineClose = () => {
      console.log('handleRoutineClose called');
      showRoutineModal.value = false;
    };

    const handleRoutineConfirm = (routineData) => {
      internalEvent.value.routineId = routineData.routineId;
      internalEvent.value.routineCycle = routineData.routineCycle;
      internalEvent.value.repeat = true;  // 루틴 설정을 완료했으므로 반복 체크를 true로 설정
    };

    const getRandomColor = () => {
      const colors = ['#FFB6C1', '#FF69B4', '#FF1493', '#DB7093', '#C71585'];
      return colors[Math.floor(Math.random() * colors.length)];
    };

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
      handleRoutineConfirm,
      tagColors,
      getRandomColor
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
  z-index: 1000;
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
  z-index: 1001;
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
  background-color: #FDDAC1;
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
  background-color: #FBB584;
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
  white-space: nowrap;
}

.timezone-group {
  display: flex;
  align-items: center;
}

.timezone-value {
  margin-left: 10px;
  white-space: nowrap;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  padding: 5px 10px;
  border-radius: 5px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  display: inline-block;
  background-color: var(--tag-color, #888);
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
