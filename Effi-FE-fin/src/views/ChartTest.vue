<template>
  <div class="container">
    <Navigation @update-categories="handleUpdateCategories" @update-groups="handleUpdateGroups" class="navigation" />
    <schedule-chart/>
    <div class="content">
      <TagTop5 class="chart"/>
      <TagPie class="chart"/>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import Navigation from '@/components/LeftSidebar.vue';
import TagPie from '@/components/TagPie.vue';
import TagTop5 from '@/components/TagTop5.vue';
import ScheduleChart from '@/components/ScheduleChart.vue'

export default defineComponent({
  components: {
    ScheduleChart,
    Navigation,
    TagPie,
    TagTop5
  },
  setup() {
    const selectedCategories = ref([]);
    const selectedGroupId = ref([]);
    const calendarValue = ref([]);

    const handleUpdateCategories = (categories) => {
      selectedCategories.value = categories;
    };

    const handleUpdateGroups = (groups) => {
      selectedGroupId.value = groups;
    };

    return {
      selectedCategories,
      handleUpdateCategories,
      selectedGroupId,
      handleUpdateGroups,
      calendarValue
    };
  },
});
</script>

<style>
.container {
  display: flex;
  margin-top: 60px; /* 헤더 높이만큼의 여백을 추가 */
  height: calc(100vh - 60px); /* 전체 높이에서 헤더 높이를 뺀 높이 */
  //width: 100%; /* 전체 너비 사용 */
}

.navigation {
  width: 200px; /* 네비게이션 너비 고정 */
  height: 100%; /* 전체 높이 사용 */
  padding: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

.content {
  display: flex;
  //flex-grow: 1; /* 남은 공간 모두 사용 */
  //height: 100%; /* 전체 높이 사용 */
  justify-content: flex-start; /* 왼쪽 정렬 */
  align-items: flex-start; /* 위쪽 정렬 */
  padding: 20px;
  //box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

.chart {
  //flex: 0 0 45%; /* 각 차트가 가로의 45% 차지 */
  //height: 100%;
  padding: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}
</style>
