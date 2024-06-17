<template>
  <div class="category-container">
    <div class="category-header" @click="toggleCategories">
      <h2>Category</h2>
      <span>{{ isOpen ? '▲' : '▼' }}</span>
    </div>
    <div v-if="isOpen" class="category-list">
      <div
          v-for="category in categories"
          :key="category.name"
          class="category-item"
          @click="handleCategoryClick(category)"
      >
        <span :class="category.colorClass" class="category-dot"></span>
        <label>{{ category.name }}</label>
        <span v-if="category.selected" class="checkmark">✔️</span>
      </div>
    </div>
  </div>
</template>

<!--update-schedule 이벤트로 schedule 정보가 들어있는 array 넘겨줌 -->
<!--scheduleTableWithCategory.vue랑 같이 사용하면 됌-->
<script>
import axiosInstance from "@/services/axios.js";

export default {
  data() {
    return {
      isOpen: true,
      categories: [
        { id: 1, name: "회사", colorClass: "red", selected: false },
        { id: 2, name: "부서", colorClass: "yellow", selected: false },
        { id: 3, name: "그룹", colorClass: "green", selected: false },
        { id: 4, name: "개인", colorClass: "blue", selected: false }
      ]
    };
  },
  methods: {
    toggleCategories() {
      this.isOpen = !this.isOpen;
    },
    async handleCategoryClick(category) {
      category.selected = !category.selected;
      console.log(`Category clicked: ${category.name}`);

      // 선택된 카테고리들의 ID를 수집합니다.
      const selectedCategories = this.categories
          .filter(cat => cat.selected)
          .map(cat => cat.id);

      console.log(selectedCategories);
      if (selectedCategories.length > 0) {
        // 선택된 카테고리 ID를 서버에 전달하여 스케줄을 조회합니다.
        await this.fetchSchedulesForSelectedCategories(selectedCategories);
      } else {
        // 선택된 카테고리가 없으면 전체 스케줄을 조회합니다.
        await this.fetchAllSchedules();
      }
      this.$emit('selectCategory', selectedCategories); // id만 넘겨주기
    },
    async fetchSchedulesForSelectedCategories(categories) {
      const token = sessionStorage.getItem('accessToken'); // 토큰을 sessionStorage에서 가져오기
      if (!token) {
        console.error('No token found');
        return;
      }
      const config = {
        headers: {
          Authorization: `Bearer ${token}`
        }
      };

      const scheduleResults = [];

      for (const categoryId of categories) {
        try {
          const response = await axiosInstance.get(`/api/schedule/find/category/${categoryId}`, config);
          console.log(`Schedules for category ${categoryId}:`, response.data);
          scheduleResults.push(...response.data); // 각 카테고리의 스케줄을 결과 배열에 추가
        } catch (error) {
          console.error(`Error fetching schedules for categoryId ${categoryId}:`, error);
        }
      }

      // 태그 정보를 추가합니다.
      const scheduleWithTags = await Promise.all(scheduleResults.map(async item => {
        const tags = await this.fetchTagsForSchedule(item.scheduleId);
        return {
          ...item,
          tags
        };
      }));

      this.$emit('update-schedule', scheduleWithTags); // 부모 컴포넌트로 데이터를 전달
    },
    async fetchTagsForSchedule(scheduleId) {
      try {
        const response = await axiosInstance.get(`/api/tag/find/schedule/${scheduleId}`);
        return response.data;
      } catch (error) {
        console.error(`Error fetching tags for scheduleId ${scheduleId}:`, error);
        return [];
      }
    },
    async fetchAllSchedules() {
      const token = sessionStorage.getItem('accessToken'); // 토큰을 sessionStorage에서 가져오기
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
        const response = await axiosInstance.get('/api/schedule/findAll', config);
        console.log(response.data);
        const data = response.data;
        if (Array.isArray(data)) {
          const scheduleWithTags = await Promise.all(data.map(async item => {
            const tags = await this.fetchTagsForSchedule(item.scheduleId);
            return {
              ...item,
              tags
            };
          }));

          this.$emit('update-schedule', scheduleWithTags);

        } else {
          console.error('Expected an array but got an object:', data);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
        console.error('Error details:', error.response ? error.response.data : error.message);
      }
    }
  },
  mounted() {
    this.fetchAllSchedules();
  }
};
</script>

<style scoped>
.category-container {
  //background-color: #f7d6c3;
  padding: 10px;
  border-radius: 5px;
  width: 200px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.category-header h2 {
  font-size: 18px;
  margin: 0;
}

.category-list {
  margin-top: 10px;
}

.category-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  cursor: pointer;
}

.category-dot {
  width: 15px;
  height: 15px;
  border-radius: 50%;
  margin-right: 10px;
}

.red {
  background-color: red;
}

.yellow {
  background-color: yellow;
}

.green {
  background-color: green;
}

.blue {
  background-color: blue;
}

.checkmark {
  margin-left: auto;
  color: black;
  font-size: 15px;
  font-weight: bold;
}

.category-item label {
  cursor: pointer;
  flex-grow: 1;
}
</style>
