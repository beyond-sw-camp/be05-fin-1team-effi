<template>
  <v-list>
    <v-list-item v-for="category in categories" :key="category.id" @click="handleCategoryClick(category)"
      class="category-item">
      <div class="category-content">
        <v-checkbox-btn :model-value="category.selected" class="custom-checkbox"></v-checkbox-btn>
        <v-list-item-title>{{ category.name }}</v-list-item-title>
        <div class="category-dot" :style="{ backgroundColor: getCategoryColor(category.name) }"></div>

      </div>
    </v-list-item>
  </v-list>
</template>

<script>
import axiosInstance from "@/services/axios.js";

export default {
  data() {
    return {
      categories: [
        { id: 1, name: "회사", selected: false },
        { id: 2, name: "부서", selected: false },
        { id: 3, name: "그룹", selected: false },
        { id: 4, name: "개인", selected: false }
      ]
    };
  },
  methods: {
    async handleCategoryClick(category) {
      category.selected = !category.selected;
      console.log(`Category clicked: ${category.name}`);

      const selectedCategories = this.categories
        .filter(cat => cat.selected)
        .map(cat => cat.id);

      console.log(selectedCategories);
      if (selectedCategories.length > 0) {
        await this.fetchSchedulesForSelectedCategories(selectedCategories);
      } else {
        await this.fetchAllSchedules();
      }
      this.$emit('selectCategory', selectedCategories);
    },
    async fetchSchedulesForSelectedCategories(categories) {
      const token = sessionStorage.getItem('accessToken');
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
          scheduleResults.push(...response.data);
        } catch (error) {
          console.error(`Error fetching schedules for categoryId ${categoryId}:`, error);
        }
      }

      const scheduleWithTags = await Promise.all(scheduleResults.map(async item => {
        const tags = await this.fetchTagsForSchedule(item.scheduleId);
        return {
          ...item,
          tags
        };
      }));

      this.$emit('update-schedule', scheduleWithTags);
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
      const token = sessionStorage.getItem('accessToken');
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
    },
    getCategoryColor(categoryName) {
      switch (categoryName) {
        case '회사':
          return '#EAFFCF';
        case '부서':
          return '#ABC4FF';
        case '그룹':
          return '#EAB9F0';
        case '개인':
          return '#FFB5C9';
        default:
          return '#000000'; // 기본 색상 
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
  width: 200px;
}

.category-list {
  margin-top: 10px;
  max-height: 200px;
  overflow-y: auto;
}

.category-item {
  margin-bottom: 10px;
  cursor: pointer;
}

.category-item:hover {
  background-color: #e0e0e0;
}

.category-content {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 0;
  margin: 0;
  width: 100%;
}

.category-dot {
  width: 15px;
  height: 15px;
  border-radius: 50%;
}

.category-title {
  margin-left: 10px;
}

.custom-checkbox {
  padding: 0;
  margin: 0;
  align-items: center;
}

.v-selection-control {
  margin: 0 !important;
  padding: 0 !important;
  flex: 0 0 auto !important;
}
.v-selection-control__input {
  display: none;
}

.v-selection-control__ripple {
  display: none;
}

.checkmark {
  margin-left: auto;
  color: black;
  font-size: 15px;
  font-weight: bold;
}
</style>
