<template>
  <div v-if="show" class="modal">
    <div class="modal-header">
      <h2>일정 {{ isEditMode ? '수정' : '추가' }}</h2>
      <button @click="$emit('close')">닫기</button>
    </div>
    <div class="modal-body">
      <!-- Add Schedule Form -->
      <form @submit.prevent="submit">
        <!-- Form Fields -->
        <div>
          <label for="title">제목:</label>
          <input type="text" id="title" v-model="internalEvent.title" required>
        </div>
        <div>
          <label for="content">내용:</label>
          <textarea id="content" v-model="internalEvent.content" required></textarea>
        </div>
        <div>
          <label for="startDate">시작일:</label>
          <input type="date" id="startDate" v-model="internalEvent.startDate" required>
        </div>
        <div>
          <label for="startTime">시작 시간:</label>
          <input type="time" id="startTime" v-model="internalEvent.startTime" required>
        </div>
        <div>
          <label for="endDate">종료일:</label>
          <input type="date" id="endDate" v-model="internalEvent.endDate" required>
        </div>
        <div>
          <label for="endTime">종료 시간:</label>
          <input type="time" id="endTime" v-model="internalEvent.endTime" required>
        </div>
        <div>
          <label for="timezone">타임존</label>
          <div v-if="!isEditingTimezone">
            {{ internalEvent.timezoneName }}
            <button type="button" class="update-button" @click="isEditingTimezone = true">변경</button>
          </div>
          <div v-else>
            <select id="timezone" v-model="internalEvent.timezoneId" required>
              <option v-for="timezone in timezones" :key="timezone.timezoneId" :value="timezone.timezoneId">
                {{ timezone.timezoneName }}
              </option>
            </select>
            <button type="button" class="update-button" @click="updateTimezone">변경</button>
          </div>
        </div>
        <div>
          <label for="repeat">반복</label>
          <!-- 반복 여부를 선택할 수 있는 체크박스 -->
          <input type="checkbox" id="repeat" v-model="internalEvent.repeat">
        </div>
        <div>
          <label for="participants">참여자</label>
          <div class="input-group">
            <input type="text" id="participants" placeholder="추가할 사원을 검색하세요" v-model="searchQuery" class="group-input" />
            <button type="button" class="search-button" @click="searchEmployees">🔍</button>
          </div>
        </div>
        <div>
          <label for="category">카테고리</label>
          <button type="button" id="category" @click="showCategoryModal = true">카테고리 선택</button>
          <CategoryModal v-if="showCategoryModal" @close="showCategoryModal = false" />
        </div>
        <div>
          <label for="tag">태그</label>
          <TagApp id="tag" :schedule="internalEvent" @update-schedule="updateSchedule" />
        </div>
        <div>
          <label for="status">진행상황</label>
          <select id="status" v-model="internalEvent.status">
            <option value="0">예정됨</option>
            <option value="1">진행중</option>
            <option value="2">완료됨</option>
          </select>
        </div>
        <div>
          <label for="emailAlert">1시간전 메일 알림</label>
          <input type="checkbox" id="emailAlert" v-model="internalEvent.emailAlert">
        </div>
        <div>
          <label for="group">그룹</label>
          <button type="button" id="group" @click="showGroupModal = true">그룹 선택</button>
          <GroupModal v-if="showGroupModal" @close="showGroupModal = false" @select-group="selectGroup" />
        </div>
        <div class="modal-footer">
          <button type="submit">{{ isEditMode ? '수정' : '추가' }}</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, watch } from 'vue';
import GroupModal from '@/components/GroupModal.vue';
import CategoryModal from '@/components/CategoryModal.vue';
import TagApp from './TagAdd.vue';

export default {
  components: {
    GroupModal,
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

    const timezones = ref([]);
    const searchQuery = ref('');
    const isEditingTimezone = ref(false);
    const showGroupModal = ref(false);
    const showCategoryModal = ref(false);

    const closeModal = () => {
      emit('close');
    };

    const submit = async () => {
      try {
        const eventToSubmit = {
          ...internalEvent.value,
          startTime: new Date(`${internalEvent.value.startDate}T${internalEvent.value.startTime}`),
          endTime: new Date(`${internalEvent.value.endDate}T${internalEvent.value.endTime}`),
          status: parseInt(internalEvent.value.status),
        };
        if (props.isEditMode) {
          await axios.put(`/api/schedule/${internalEvent.value.id}`, eventToSubmit);
        } else {
          await axios.post('/api/schedule/add', eventToSubmit);
        }
        closeModal();
      } catch (error) {
        console.error(props.isEditMode ? '일정 수정 오류:' : '일정 추가 오류:', error.response ? error.response.data : error.message);
      }
    };

    const fetchTimezones = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/mypage/timezones');
        timezones.value = response.data;
      } catch (error) {
        console.error('시간대 목록을 불러오는 중 오류가 발생했습니다:', error);
      }
    };

    const updateTimezone = () => {
      const selectedTimezone = timezones.value.find(tz => tz.timezoneId === internalEvent.value.timezoneId);
      if (selectedTimezone) {
        internalEvent.value.timezoneName = selectedTimezone.timezoneName;
        isEditingTimezone.value = false;
      }
    };

    const updateSchedule = (updatedTags) => {
      internalEvent.value.tags = updatedTags;
    };

    const selectGroup = (groupId) => {
      internalEvent.value.groupId = groupId;
    };

    const searchEmployees = async () => {
      try {
        const empId = sessionStorage.getItem('empId');
        const response = await axios.get(`/api/timezone-emp/${empId}`);
        timezones.value = response.data.timezones;
      } catch (error) {
        console.error('사용자 타임존 가져오기 오류:', error.response ? error.response.data : error.message);
      }
    };

    watch(() => props.show, (newVal) => {
      if (newVal) {
        fetchTimezones();
      }
    });

    return {
      internalEvent,
      timezones,
      searchQuery,
      isEditingTimezone,
      closeModal,
      submit,
      fetchTimezones,
      updateTimezone,
      updateSchedule,
      showGroupModal,
      showCategoryModal,
      selectGroup,
      searchEmployees
    };
  }
};
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 80vw;
  max-width: 600px;
  height: 90vh;
  overflow-y: auto;
  z-index: 10000;
}
button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
