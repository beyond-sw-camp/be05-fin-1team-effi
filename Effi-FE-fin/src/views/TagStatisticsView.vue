<template>
  <div class="container">
    <div class="main-content">
      <ScheduleChart class="schedule-chart"/>
      <TagTop5 class="top-chart"/>
      <TagPie class="bottom-chart"/>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import TagPie from '@/components/TagPie.vue';
import TagTop5 from '@/components/TagTop5.vue';
import ScheduleChart from '@/components/ScheduleChart.vue';

export default defineComponent({
  components: {
    ScheduleChart,
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
  margin-left: 80px;
  height: calc(100vh - 60px); /* 전체 높이에서 헤더 높이를 뺀 높이 */
  width: 100%;
}

.main-content {
  display: grid;
  grid-template-columns: 3fr 1fr; /* 두 개의 열을 만듭니다 */
  grid-template-rows: 1fr 1fr; /* 두 개의 행을 만듭니다 */
  grid-template-areas:
    "schedule-chart top-chart"
    "schedule-chart bottom-chart"; /* 그리드 템플릿 영역 정의 */
  gap: 20px; /* 그리드 항목 간의 간격 */
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

.schedule-chart {
  grid-area: schedule-chart; /* 첫 번째 열과 두 행에 걸쳐 위치 */
  width: 100%;
  height: 100%;
}

.top-chart {
  grid-area: top-chart; /* 두 번째 열의 첫 번째 행에 위치 */
  width: 100%;
  height: 100%;
}

.bottom-chart {
  grid-area: bottom-chart; /* 두 번째 열의 두 번째 행에 위치 */
  width: 100%;
  height: 100%;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .main-content {
    display: flex;
    flex-direction: column;
  }
  .schedule-chart, .top-chart, .bottom-chart {
    width: 100%;
    height: auto;
  }
}
</style>
