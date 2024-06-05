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
      </div>
    </div>
    <div class="logo">
      <img src="@/assets/logo.png" alt="logo_img">
    </div>
  </div>
</template>


<script>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router'
import axiosInstance from '@/services/axios';

export default {
  setup() {
    const loginData = ref({
      empNo: '',
      password: ''
    });

    const authStore = useAuthStore();

    const handleLogin = async () => {
      try {
        const response = await axiosInstance.post('/api/auth/signin', {
          empNo: loginData.value.empNo,
          password: loginData.value.password,
        });
        const { empNo, name, rank, accessToken, refreshToken } = response.data;
        authStore.login(empNo, name, rank, accessToken, refreshToken);
      } catch (error) {
        console.error('Login error:', error);
      }
    };

    return {
      loginData,
      handleLogin,
    };
  },
};
// import { ref, onMounted } from 'vue'
// import axios from 'axios'
// import { useRouter } from 'vue-router'

// const email = ref('')
// const password = ref('')
// const accessToken = ref('')
// const refreshToken = ref('')
// const router = useRouter()

// // 페이지가 로드될 때 마다 확인하는 함수
// onMounted( () => {
//   //로컬 스토리지에서 accessToken 존재하는지 확인
//   accessToken.value = localStorage.getItem('accessToken');

//   // accessToken이 존재할 경우 리다이렉트
//   if (accessToken.value) {
//     router.push({ name: 'todo' })
//   }
// })

// const login = async () => {
//   try {
//     const response = await axios.post("http://localhost:8080/api/auth/signin", {
//       email: email.value,
//       password: password.value
//     })
//     console.log(response.data)
//     accessToken.value = response.data.token
//     refreshToken.value = response.data.refreshToken

//     localStorage.setItem('accessToken', accessToken.value)
//     localStorage.setItem('email', email.value) // 이메일 저장
//     localStorage.setItem('password', password.value) // 비밀번호 저장
//     localStorage.setItem('userNickname', response.data.userNickname)
//     localStorage.setItem('refreshToken', refreshToken.value)

//     router.push({ name: "todo" })

//   } catch (error) {
//     console.error(error)
//     alert("로그인에 실패하였습니다. 이메일과 비밀번호를 확인해주세요");
//     localStorage.removeItem('accesstoken');  // 로그인 실패 시 토큰 삭제
//     localStorage.removeItem('email');  // 로그인 실패 시 이메일 삭제
//     localStorage.removeItem('password');  // 로그인 실패 시 비밀번호 삭제
//     this.isLoggedIn = false;  // 로그인 상태를 false로 설정
//   }
// }
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
</style>
