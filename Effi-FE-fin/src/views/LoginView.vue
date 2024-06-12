<template>
  <div class="container">
    <div class="background-shape">
      <a href="/">
        <p class="logo_txt">Effi Planner</p>
      </a>
      <div class="login-container">
        <form class="login-form" @submit.prevent="handleLogin">
          <input type="text" v-model="loginData.empNo" placeholder="사원번호 입력">
          <br>
          <input type="password" v-model="loginData.password" placeholder="비밀번호 입력">
          <br>
          <button type="submit">로그인</button>
        </form>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      </div>
    </div>
    <div class="logo">
      <img src="@/assets/logo.png" alt="logo_img">
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import axiosInstance from '@/services/axios';

const loginData = ref({
  empNo: '',
  password: ''
});

const authStore = useAuthStore();
const router = useRouter();
const errorMessage = ref('');

const handleLogin = async () => {
  errorMessage.value = '';
  try {
    const response = await axiosInstance.post('/api/auth/signin', {
      empNo: loginData.value.empNo,
      password: loginData.value.password,
    });
    const { empNo, name, rank, accessToken, refreshToken } = response.data.data;
    authStore.login(empNo, name, rank, accessToken, refreshToken);
    router.push({ name: 'home' });
  } catch (error) {
    errorMessage.value = error.response.data.data.msg || '로그인 중 오류가 발생했습니다.';
    console.error('Login error:', error.response.data.data.msg);
  }
};
</script>

<style scoped>
.container {
  display: flex;
  justify-content: space-between; /* 좌우 배치를 위한 설정 */
  align-items: center; /* 세로 중앙 정렬 */
  height: 100vh; /* 화면 전체 높이 */
  position: relative; /* 배경 모양을 위한 상대적 위치 설정 */
  padding: 0 20px; /* 좌우 패딩 추가 */
}

.background-shape {
  display: flex; /* 내부 요소 중앙 정렬을 위해 flexbox 사용 */
  flex-direction: column; /* 내부 요소 세로 정렬 */
  justify-content: center; /* 세로 중앙 정렬 */
  align-items: center; /* 가로 중앙 정렬 */
  width: 500px; /* 너비 */
  height: 600px; /* 높이 */
  background-color: #FBB584; /* 배경색 */
  border-radius: 15px; /* 모서리 둥글기 */
  margin-right: auto; /* 좌측에 위치 */
}

.login-container {
  text-align: center;
  width: 350px; /* 폼의 너비를 조정합니다. */
  padding: 20px;
  border-radius: 10px; /* 모서리 둥근 사각형 */
  background-color: #ffffff; /* 로그인 입력 부분의 배경색 */
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* 그림자 추가 */
  margin-top: 20px; /* 상단 여백 추가 */
}
.login-container input {
  width: calc(100% - 16px);
  margin-bottom: 10px;
  padding: 8px;
  border-radius: 5px; /* 입력란의 모서리 둥글게 */
  border: 1px solid #ccc;
}
.login-container button {
  width: 100%;
  padding: 8px 20px;
  background-color: #000000;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.login-form {
  background-color: #ffffff; /* 로그인 입력 부분의 배경색 */
}

/* 로고 이미지 스타일 */
.logo {
  width: 200px; /* 로고 이미지의 너비를 조정합니다. */
  height: auto; /* 높이 자동 조정 */
  position: absolute; /* 절대 위치 설정 */
  left: 700px;
  right: 20px; /* 오른쪽에서 20px 떨어진 위치 */
  top: 50%; /* 상위 요소 대비 중앙 정렬을 위한 설정 */
  transform: translateY(-50%); /* 정확한 세로 중앙 정렬을 위한 설정 */
}

/* 로고 텍스트 스타일 */
.logo_txt {
  color: black; /* 텍스트 색상 변경 */
  font-size: 36px; /* 폰트 크기를 36px로 설정합니다. */
  font-weight: bold; /* 폰트를 굵게 설정합니다. */
  text-align: center; /* 가운데 정렬합니다. */
  margin-bottom: 20px; /* 아래 여백 추가 */
}

.error-message {
  color: red;
  margin-top: 10px;
}
</style>
