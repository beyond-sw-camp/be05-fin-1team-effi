<template>
  <div class="tag-list">
    <h3>Top 5 Tags</h3>
    <ul>
      <li v-for="(tag, index) in tags" :key="tag">
        <button @click="searchByTag(tag)" :class="['tag-button', getColorClass(index)]"> {{ tag }}</button>
      </li>
    </ul>
  </div>
</template>


<script>
import { defineComponent, ref, onMounted } from 'vue';
import axiosInstance from '@/services/axios';
import {useRouter} from 'vue-router'

export default defineComponent({
  name: 'TagList',
  setup() {
    const tags = ref([]);
    const router = useRouter();

    const fetchTopTags = async () => {
      try {
        const response = await axiosInstance.get('/api/tag/find/top5Tag');
        tags.value = response.data;
      } catch (error) {
        console.error('Error fetching top tags:', error);
      }
    };

    const searchByTag = (tag) => {
      router.push({ path: '/search', query: { criterion: 'tag', query: tag } });
    };

    const getColorClass = (index) => {
      const colors = ['color1', 'color2', 'color3', 'color4', 'color5'];
      return colors[index % colors.length];
    };

    onMounted(() => {
      fetchTopTags();
    });

    return {
      tags,
      searchByTag,
      getColorClass
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
  width: 400px;
  margin: 0 auto;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tag-list h3 {
  margin-bottom: 10px;
  text-align: center;
}

.tag-list ul {
  list-style-type: none;
  padding: 0;
}

.tag-list li {
  padding: 5px 0;
}

.tag-button {
  border: none;
  padding: 10px 15px;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  font-size: 14px;
}

.color1 {
  background-color: #ff6666;
}

.color2 {
  background-color: #ffcc66;
}

.color3 {
  background-color: #66cc66;
}

.color4 {
  background-color: #66cccc;
}

.color5 {
  background-color: #cc66cc;
}
</style>
