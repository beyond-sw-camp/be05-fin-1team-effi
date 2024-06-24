<template>
  <div class="container">
    <Navigation @update-categories="handleUpdateCategories" @update-groups="handleUpdateGroups" class="navigation" />
    <div class="main-content">
      <schedule-chart class="schedule-chart"/>
      <div class="side-charts">
        <TagTop5 class="chart"/>
        <TagPie class="chart"/>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import Navigation from '@/components/LeftSidebar.vue';
import TagPie from '@/components/TagPie.vue';
import TagTop5 from '@/components/TagTop5.vue';
import ScheduleChart from '@/components/ScheduleChart.vue';

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
}

.navigation {
  width: 200px; /* 네비게이션 너비 고정 */
  height: 100%; /* 전체 높이 사용 */
  padding: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr; /* 두 개의 열을 만듭니다 */
  grid-template-rows: auto; /* 행 높이는 콘텐츠에 따라 조정됩니다 */
  gap: 20px; /* 그리드 항목 간의 간격 */
  flex-grow: 1; /* 남은 공간 모두 사용 */
  padding: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

.schedule-chart {
  grid-column: 1 / 2; /* 첫 번째 열에 위치 */
  width: 100%;
  height: 100%;
}

.side-charts {
  display: flex;
  flex-direction: column; /* 세로 정렬 */
  justify-content: space-between; /* 차트들 사이에 공간 배분 */
  grid-column: 2 / 3; /* 두 번째 열에 위치 */
  height: 100%; /* 전체 높이 사용 */
}

.chart {
  flex: 1;
  padding: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .main-content {
    display: flex;
    flex-direction: column;
  }
  .schedule-chart, .side-charts {
    width: 100%;
    height: auto;
  }
  .side-charts {
    flex-direction: column;
    align-items: center;
  }
}
</style>
