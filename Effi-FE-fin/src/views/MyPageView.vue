<template>
  <div class="container">
    <Navigation class="navigation" />
    <div v-if="mypage">
      <p><strong>사원 번호:</strong> {{ mypage.empNo }}</p>
      <p><strong>이름:</strong> {{ mypage.name }}</p>
      <p><strong>회사:</strong> {{ mypage.company }}</p>
      <p><strong>이메일:</strong> {{ mypage.email }}</p>
      <p><strong>전화번호:</strong> {{ mypage.phoneNum }}</p>
      <p><strong>내선 번호:</strong> {{ mypage.extensionNum }}</p>
      <p><strong>직급:</strong> {{ mypage.rank }}</p>
      <p><strong>부서 이름:</strong> {{ mypage.deptName }}</p>
      <p><strong>시간대:</strong> {{ mypage.timezoneName }}</p>
    </div>
    <MyPageUpdate :onSubmit="updateTimezone" />
    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';
import Navigation from '@/components/LeftSidebar.vue';
import MyPageUpdate from '@/components/MyPageUpdate.vue';

export default {
  components: {
    MyPageUpdate,
    Navigation
  },
  data() {
    return {
      mypage: null,
      message: '',
    };
  },
  async created() {
    console.log('Component created');
    await this.checkTokenAndFetchMyPage();
  },
  methods: {
    async checkTokenAndFetchMyPage() {
      const accessToken = sessionStorage.getItem('accessToken');
      console.log('Token found:', accessToken);
      if (!accessToken) {
        console.error('Token not found');
        return;
      }
      try {
        await this.fetchMyPage();
      } catch (error) {
        console.error('mypage data 불러오기 오류:', error.response ? error.response.data : error.message);
      }
    },
    async fetchMyPage() {
      const accessToken = sessionStorage.getItem('accessToken');
      console.log('Fetching my page with token:', accessToken);
      try {
        const response = await axios.get('http://localhost:8080/api/mypage/me', {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        console.log('MyPage data fetched:', response.data);
        this.mypage = response.data;
      } catch (error) {
        console.error('Error fetching my page:', error.response ? error.response.data : error.message);
        throw error;
      }
    },
    async updateTimezone(timezoneId) {
      try {
        const accessToken = sessionStorage.getItem('accessToken');
        console.log('Token retrieved:', accessToken);
        console.log('Updating timezone with ID:', timezoneId); // 로그 추가
        const response = await axios.put('http://localhost:8080/api/mypage/update', {
          timezoneId: timezoneId, // 명시적으로 필드 이름 지정
        }, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        console.log('Timezone updated:', response.data);
        this.message = response.data.message; // 수정
        await this.fetchMyPage();
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
  height: calc(100vh - 60px);
  width: 100%;
}
</style>
