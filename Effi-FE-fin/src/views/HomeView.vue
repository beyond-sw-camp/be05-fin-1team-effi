<template>
  <div class="container">
    <Navigation @update-categories="handleUpdateCategories"  @update-groups="handleUpdateGroups" class="navigation" />
    <div class="content">
      <VCalendar class="calendar" :selectedCategories="selectedCategories" :selectedGroupId="selectedGroupId" />
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import Navigation from '@/components/LeftSidebar.vue';
import VCalendar from '@/components/VCalendar.vue';

export default defineComponent({
  components: {
    Navigation,
    VCalendar
  },
  setup() {
    const selectedCategories = ref([]);
    const selectedGroupId = ref([]);

    const handleUpdateCategories = (categories) => {
      selectedCategories.value = categories;
    };

    const handleUpdateGroups = (groups) => {
      selectedGroupId.value = groups;
    }

    return {
      selectedCategories,
      handleUpdateCategories,
      selectedGroupId,
      handleUpdateGroups
    };
  },
});
</script>

<style>
.container {
  display: flex;
  margin-top: 60px; /* 헤더 높이만큼의 여백을 추가 */
  height: calc(100vh - 60px); /* 전체 높이에서 헤더 높이를 뺀 높이 */
  width: 100%; /* 전체 너비 사용 */
}

.navigation {
  width: 200px; /* 네비게이션 너비 고정 */
  height: 100%; /* 전체 높이 사용 */
  padding: 20px;
  margin-right: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

.content {
  flex-grow: 1; /* 남은 공간 모두 사용 */
  height: 100%; /* 전체 높이 사용 */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box; /* 패딩을 포함한 박스 크기 */
}

.calendar {
  width: 100%;
  height: 100%;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .navigation {
    display: none; /* 작은 화면에서는 네비게이션 숨김 */
  }

  .content {
    width: 100%;
    padding: 10px;
  }

  .calendar {
    width: 100%;
    height: auto;
  }
}
</style>
