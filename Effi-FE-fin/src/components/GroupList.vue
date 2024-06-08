<template>
  <div class="participant-list-container">
    <div class="header">
      <h1>일정을 확인할 사람들</h1>
      <div class="search-bar">
        <input
            type="text"
            v-model="searchQuery"
            placeholder="사원의 이름을 입력하세요"
        />
      </div>
    </div>
    <div class="participant-list">
      <div
          v-for="user in filteredUsers"
          :key="user.id"
          class="user-item"
          @click="toggleSelection(user.id)"
      >
        <img :src="user.image" alt="user image" class="user-image" />
        <div class="user-info">
          <h2>{{ user.name }}</h2>
          <p>{{ user.position }}</p>
        </div>
        <div class="user-selection">
          <span v-if="user.selected" class="checkmark">✔️</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchQuery: '',
      users: [
        { id: 1, name: '김지연', position: '마케팅팀 / 팀장', image: 'path/to/image1.jpg', selected: false },
        { id: 2, name: '임찬규', position: '영업팀 / 부장', image: 'path/to/image2.jpg', selected: false },
        { id: 3, name: '이승우', position: '인사팀 / 과장', image: 'path/to/image3.jpg', selected: false },
        { id: 4, name: '정현진', position: '연구개발팀 / 수석연구원', image: 'path/to/image4.jpg', selected: false },
        { id: 5, name: '정민수', position: '생산관리팀 / 주임', image: 'path/to/image5.jpg', selected: false },
        { id: 6, name: '전예은', position: '연구개발팀 / 차석연구원', image: 'path/to/image6.jpg', selected: false }
      ]
    };
  },
  computed: {
    filteredUsers() {
      return this.users.filter(user =>
          user.name.includes(this.searchQuery)
      );
    }
  },
  methods: {
    toggleSelection(userId) {
      const user = this.users.find(user => user.id === userId);
      if (user) {
        user.selected = !user.selected;
      }
    }
  }
};
</script>

<style scoped>
.user-list-container {
  background-color: #f7d6c3;
  padding: 20px;
  border-radius: 10px;
  width: 350px; /* 조정된 너비 */
  margin: 0 auto; /* 가운데 정렬 */
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
}

.header {
  text-align: center;
}

.search-bar {
  display: flex;
  justify-content: center;
  margin: 10px 0;
}

.search-bar input {
  padding: 10px; /* 조정된 패딩 */
  border: 1px solid #ccc;
  border-radius: 20px;
  width: 100%; /* 너비를 100%로 설정 */
  max-width: 300px; /* 최대 너비 설정 */
}

.user-list {
  margin-top: 20px;
}

.user-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px; /* 조정된 하단 여백 */
  cursor: pointer;
  padding: 10px; /* 조정된 패딩 */
  border-bottom: 1px solid #ccc;
  background-color: #fff; /* 배경 색상 추가 */
  border-radius: 5px; /* 테두리 둥글게 설정 */
}

.user-image {
  width: 50px; /* 조정된 너비 */
  height: 50px; /* 조정된 높이 */
  border-radius: 50%;
  margin-right: 15px; /* 조정된 오른쪽 여백 */
}

.user-info {
  flex-grow: 1;
}

.user-info h2 {
  font-size: 16px; /* 조정된 글꼴 크기 */
  margin: 0;
}

.user-info p {
  font-size: 14px; /* 조정된 글꼴 크기 */
  margin: 5px 0 0;
}

.user-selection {
  width: 20px;
}

.checkmark {
  color: blue;
}
</style>
