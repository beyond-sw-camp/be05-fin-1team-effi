<template>
  <div class="container">
    <div class="content">
      <div class="profile-container" v-if="mypage">
        <div class="profile-row">
          <div class="profile-label">사번</div>
          <div class="profile-value">{{ mypage.empNo }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">이름</div>
          <div class="profile-value">{{ mypage.name }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">회사</div>
          <div class="profile-value">{{ mypage.company }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">이메일</div>
          <div class="profile-value">{{ mypage.email }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">모바일</div>
          <div class="profile-value">{{ mypage.phoneNum }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">내선번호</div>
          <div class="profile-value">{{ mypage.extensionNum }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">부서</div>
          <div class="profile-value">{{ mypage.deptName }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">직위</div>
          <div class="profile-value">{{ mypage.rank }}</div>
        </div>
        <div class="profile-row">
          <div class="profile-label">시간대</div>
          <div class="profile-value">
            <div v-if="!isEditingTimezone">
              {{ mypage.timezoneName }}
              <button class="update-button" @click="isEditingTimezone = true">변경</button>
            </div>
            <div v-else>
              <select v-model="timezoneId" required>
                <option v-for="timezone in timezones" :key="timezone.timezoneId" :value="timezone.timezoneId">
                  {{ timezone.timezoneName }}
                </option>
              </select>
              <button class="update-button" @click="updateTimezone">변경</button>
            </div>
          </div>
        </div>
        <button class="stats-button" @click="viewTagStats">태그 통계보기</button>
      </div>
      <p v-if="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/services/axios';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const router = useRouter();

    const viewTagStats = () => {
      router.push('/tagstatistics');
    };

    return {
      viewTagStats,
    };
  },
  data() {
    return {
      mypage: null,
      message: '',
      isEditingTimezone: false,
      timezoneId: null,
      timezones: []
    };
  },
  async created() {
    await this.checkTokenAndFetchMyPage();
    await this.fetchTimezones();
  },
  methods: {
    async checkTokenAndFetchMyPage() {
      const accessToken = sessionStorage.getItem('accessToken');
      if (!accessToken) {
        console.error('Token not found');
        return;
      }
      await this.fetchMyPage();
    },
    async fetchMyPage() {
      const accessToken = sessionStorage.getItem('accessToken');
      try {
        const response = await axiosInstance.get('/api/mypage/me', {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        this.mypage = response.data;
        this.timezoneId = this.mypage.timezoneId; // Initialize timezoneId
      } catch (error) {
        console.error('Error fetching my page:', error.response ? error.response.data : error.message);
      }
    },
    async fetchTimezones() {
      try {
        const response = await axiosInstance.get('/api/mypage/timezones');
        this.timezones = response.data;
      } catch (error) {
        console.error('시간대 목록을 불러오는 중 오류가 발생했습니다:', error);
      }
    },
    async updateTimezone() {
      const accessToken = sessionStorage.getItem('accessToken');
      try {
        const response = await axiosInstance.put('/api/mypage/update', {
          timezoneId: this.timezoneId,
        }, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        this.message = response.data.message;
        await this.fetchMyPage();
        this.isEditingTimezone = false;
      } catch (error) {
        console.error('오류가 발생했습니다:', error.response ? error.response.data : error.message);
      }
    },
  },
};
</script>

<style>
.container {
  display: flex;
  margin-top: 60px;
  margin-left: 80px;
  height: calc(100vh - 60px);
  width: 100%;
}

.content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.profile-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 600px;
}

.profile-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
}

.profile-label {
  font-weight: bold;
  width: 30%;
}

.profile-value {
  width: 70%;
  text-align: left;
}

.update-button {
  background-color: #ffcc99;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 10px;
}

.stats-button {
  background-color: #99ccff;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .navigation {
    display: none;
  }
}
</style>
