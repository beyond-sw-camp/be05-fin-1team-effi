<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
      <h5>타임존</h5>
      <ul class="scrollable-list">
        <li
          v-for="timezone in availableTimezones"
          :key="timezone.timezoneId"
          @click="selectTimezone(timezone.timezoneId)"
          :class="{ selected: selectedTimezoneId === timezone.timezoneId }"
        >
          {{ timezone.timezoneName }}
        </li>
      </ul>
      <button class="add-button" @click="addTimezone">추가하기</button>
      <h5 class="my-timezones-header">나의 타임존</h5>
      <ul class="scrollable-list">
        <li v-for="timezone in myTimezones" :key="timezone.timezoneId" class="my-timezone-item">
          {{ timezone.timezoneName }}
          <button class="delete-button" @click="removeTimezone(timezone.timezoneId)">X</button>
        </li>
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
      availableTimezones: [],
      myTimezones: [],
      selectedTimezoneId: null
    };
  },
  created() {
    this.fetchAvailableTimezones();
    this.fetchMyTimezones();
  },
  methods: {
    async fetchAvailableTimezones() {
      try {
        const response = await axiosInstance.get('/api/mypage/timezones');
        this.availableTimezones = response.data;
      } catch (error) {
        console.error('타임존 검색 오류:', error.response ? error.response.data : error.message);
      }
    },
    async fetchMyTimezones() {
      const empId = sessionStorage.getItem('empNo');
      try {
        const response = await axiosInstance.get(`/api/timezone-emp/${empId}`);
        const defaultTimezoneResponse = await axiosInstance.get(`/api/timezone-emp/${empId}/default`);
        const defaultTimezone = defaultTimezoneResponse.data.data;

        this.myTimezones = response.data.data.timezones.filter(
          (timezone) => timezone.timezoneId !== defaultTimezone.timezoneId
        );
      } catch (error) {
        console.error('나의 타임존 검색 오류:', error.response ? error.response.data : error.message);
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
        const response = await axiosInstance.post(`/api/timezone-emp/${empId}/add`, null, {
          params: {
            timezoneId: this.selectedTimezoneId
          }
        });
        alert('타임존이 추가되었습니다.');
        this.$emit('add-timezone', response.data.data); // 새로운 타임존을 부모 컴포넌트에 전달
        this.fetchMyTimezones();
      } catch (error) {
        console.error('타임존 추가 오류:', error.response ? error.response.data : error.message);
        alert('타임존 추가에 실패했습니다.');
      }
    },
    async removeTimezone(timezoneId) {
      const empId = sessionStorage.getItem('empNo');
      try {
        await axiosInstance.delete(`/api/timezone-emp/${empId}/remove`, {
          params: {
            timezoneId: timezoneId
          }
        });
        alert('타임존이 삭제되었습니다.');
        this.$emit('remove-timezone', timezoneId); // 삭제된 타임존의 ID를 부모 컴포넌트에 전달
        this.fetchMyTimezones();
      } catch (error) {
        console.error('타임존 삭제 오류:', error.response ? error.response.data : error.message);
        alert('타임존 삭제에 실패했습니다.');
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
  z-index: 1000;
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

h5, p {
  text-align: center; /* 텍스트 가운데 정렬 */
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  padding: 5px;
  border-radius: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

li.selected {
  background-color: #FDDAC1;
}

.scrollable-list {
  max-height: 150px; /* 스크롤이 나타나는 최대 높이 설정 */
  overflow-y: auto; /* 세로 스크롤 활성화 */
}

.add-button, .delete-button {
  background-color: #FBB584;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  color: white;
  font-weight: bold;
  margin-top: 10px;
}

.add-button {
  width: 100%;
  margin-bottom: 20px; /* 추가하기 버튼과 나의 타임존 사이의 간격 설정 */
}

.my-timezones-header {
  margin-top: 20px; /* 추가하기 버튼과 나의 타임존 사이의 간격 설정 */
}

.my-timezone-item {
  font-size: 14px; /* 나의 타임존 글자 크기 조정 */
}

.delete-button {
  background-color: #ff4d4d;
  font-size: 10px; /* X 버튼 크기 조정 */
  padding: 5px;
}
</style>
