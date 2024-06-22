<template>
  <div class="tag-list">
    <h3>Top 5 Tags</h3>
    <ul>
      <li v-for="(tag, index) in tags" :key="tag">{{ index + 1 }}. {{ tag }}</li>
    </ul>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import axiosInstance from '@/services/axios';

export default defineComponent({
  name: 'TagList',
  setup() {
    const tags = ref([]);

    const fetchTopTags = async () => {
      try {
        const response = await axiosInstance.get('/api/tag/find/top5Tag');
        tags.value = response.data;
      } catch (error) {
        console.error('Error fetching top tags:', error);
      }
    };

    onMounted(() => {
      fetchTopTags();
    });

    return {
      tags
    };
  }
});
</script>

<style scoped>
.tag-list {
  padding: 20px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 300px;
  margin: 0 auto;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tag-list h3 {
  margin-bottom: 10px;
}

.tag-list ul {
  list-style-type: none;
  padding: 0;
}

.tag-list li {
  padding: 5px 0;
}
</style>
