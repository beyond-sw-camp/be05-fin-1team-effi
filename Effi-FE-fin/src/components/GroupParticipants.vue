<template>
  <div class="group-list-wrapper">
    <div class="group-list-container">
      <div class="search-bar">
        <h6>일정을 확인할 사람들</h6>
        <input type="text" v-model="searchQuery" placeholder="사원의 이름을 입력하세요" />
      </div>
      <div class="group-list">
        <div v-for="user in filteredUsers" :key="user.id" class="group-item" @click="toggleSelection(user.id)">
          <img :src="user.image" alt="user image" class="user-image" />
          <div class="user-info">
            <h2>{{ user.name }}</h2>
            <p>{{ user.rank }}</p>
            <p>{{ user.company }}</p>
          </div>
          <div class="user-selection">
            <span v-if="user.selected" class="checkmark">✔️</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // useRoute 임포트
import axiosInstance from "@/services/axios.js";
import userImage from '@/assets/logo.png';


export default {
  name: 'GroupParticipants',
  emits: ['selectedUsers'],

  setup(props, { emit }) {
    const route = useRoute();
    const searchQuery = ref('');
    const users = ref([]);
    const groupId = ref(route.query.groupId); // 쿼리 파라미터에서 groupId 가져오기

    const filteredUsers = computed(() => {
      return users.value.filter(user =>
        user.name.includes(searchQuery.value)
      );
    });

    const toggleSelection = (userId) => {
      const user = users.value.find(user => user.id === userId);
      console.log('User toggled:', user);
      if (user) {
        user.selected = !user.selected;
        const selectedUsers = users.value.filter(user => user.selected);
        console.log('Selected Users:', selectedUsers);
        emit('selectedUsers', selectedUsers);
      }
    };

    const fetchGroupUsers = async () => {
      try {
        const response = await axiosInstance.get(`/api/groups/find/${groupId.value}`);
        console.log('Group Users Data:', response.data);
        const data = Array.isArray(response.data) ? response.data : response.data.data;
        if (Array.isArray(data)) {
          users.value = data.map(user => ({
            id: user.id,
            empNo: user.empNo,
            company: user.company,
            name: user.name,
            email: user.email,
            phoneNum: user.phoneNum,
            extensionNum: user.extensionNum,
            rank: user.rank,
            deptId: user.deptId,
            image: userImage, 
            selected: false
          }));
          console.log('Processed Users:', users.value);

        } else {
          console.error('Expected an array but got:', data);
        }
      } catch (error) {
        console.error('Error fetching group users:', error);
        if (error.response) {
          console.error('Error details:', error.response.data);
        }
      }
    };

    onMounted(() => {
      fetchGroupUsers(); // 컴포넌트가 마운트될 때 사용자 목록을 불러옵니다.
    });

    return {
      searchQuery,
      users,
      filteredUsers,
      toggleSelection,
      fetchGroupUsers
    };
  }
};
</script>

<style scoped>
/* 외부 래퍼는 전체 컨테이너를 감싸도록 */
.group-list-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  /* 페이지 중앙에 위치하도록 */
}

.group-list-container {
  background-color: #f7d6c3;
  padding: 20px;
  border-radius: 10px;
  width: 300px;
  /* 고정된 너비 */
  height: 500px;
  /* 고정된 높이 */
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  /* 그림자 효과 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
  /* 전체 컨테이너의 넘침 숨기기 */
}

.search-bar {
  width: 100%;
  /* 검색 바가 컨테이너 너비를 사용하도록 설정 */
  margin-bottom: 10px;
}

.search-bar h6 {
  text-align: center;
  margin: 0;
  font-size: 16px;
  /* 제목 크기 조정 */
  padding-bottom: 10px;
  /* 아래쪽 여백 추가 */
}

.search-bar input {
  display: block;
  /* 블록 요소로 변경하여 한 줄에 표시 */
  margin: 0 auto;
  /* 중앙 정렬 */
  padding: 8px;
  /* 패딩 조정 */
  border: 1px solid #ccc;
  border-radius: 20px;
  width: 100%;
  /* 너비를 100%로 설정 */
  max-width: 250px;
  /* 최대 너비 설정 */
}

.group-list {
  width: 100%;
  /* 그룹 리스트가 컨테이너 너비를 사용하도록 설정 */
  overflow-y: auto;
  /* 세로 스크롤 활성화 */
  flex-grow: 1;
  /* 그룹 리스트가 남은 공간을 모두 사용하도록 설정 */
}

.group-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  /* 하단 여백 조정 */
  cursor: pointer;
  padding: 10px;
  /* 패딩 조정 */
  border-bottom: 1px solid #ccc;
  background-color: #fff;
  /* 배경 색상 추가 */
  border-radius: 5px;
  /* 테두리 둥글게 설정 */
}

.user-image {
  width: 40px;
  /* 이미지 너비 조정 */
  height: 40px;
  /* 이미지 높이 조정 */
  border-radius: 50%;
  margin-right: 10px;
  /* 오른쪽 여백 조정 */
}

.user-info {
  flex-grow: 1;
}

.user-info h2 {
  font-size: 14px;
  /* 글꼴 크기 조정 */
  margin: 0;
}

.user-info p {
  font-size: 12px;
  /* 글꼴 크기 조정 */
  margin: 5px 0 0;
}

.user-selection {
  width: 20px;
}

.checkmark {
  color: blue;
}
</style>
