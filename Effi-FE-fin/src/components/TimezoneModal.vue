<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
      <h2>타임존</h2>
      <ul class="scrollable-list">
        <li
          v-for="timezone in timezones"
          :key="timezone.timezoneId"
          @click="selectTimezone(timezone.timezoneId)"
          :class="{ selected: selectedTimezoneId === timezone.timezoneId }"
        >
          {{ timezone.timezoneName }}
        </li>
      </ul>
      <button class="add-button" @click="addTimezone">추가하기</button>
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
      timezones: [],
      selectedTimezoneId: null
    };
  },
  created() {
    this.fetchFindTimezone();
  },
  methods: {
    async fetchFindTimezone() {
      try {
        const response = await axiosInstance.get('/api/mypage/timezones');
        this.timezones = response.data;
      } catch (error) {
        console.error('타임존 검색 오류:', error.response ? error.response.data : error.message);
      }
    },
    selectTimezone(timezoneId) {
      this.selectedTimezoneId = timezoneId;
    },
    async addTimezone() {
      if (!this.selectedTimezoneId) {
        alert('타임존을 선택해주세요.');
        return;
      }
      const empId = sessionStorage.getItem('empNo');
      try {
        await axiosInstance.post(`/api/timezone-emp/${empId}/add`, null, {
          params: {
            timezoneId: this.selectedTimezoneId
          }
        });
        alert('타임존이 추가되었습니다.');
        this.$emit('close');
      } catch (error) {
        console.error('타임존 추가 오류:', error.response ? error.response.data : error.message);
        alert('타임존 추가에 실패했습니다.');
      }
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
  width: 300px;
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

h2, p {
  text-align: center; /* 텍스트 가운데 정렬 */
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  cursor: pointer;
  padding: 5px;
  border-radius: 5px;
}

li.selected {
  background-color: #FDDAC1;
}

.scrollable-list {
  max-height: 150px; /* 스크롤이 나타나는 최대 높이 설정 */
  overflow-y: auto; /* 세로 스크롤 활성화 */
}

.add-button {
  background-color: #FBB584;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  color: white;
  font-weight: bold;
  width: 100%;
  margin-top: 10px;
}
</style>
