<template>
  <div class="container">
    <TimezoneComponent v-if="isTimezoneVisible" class="timezone-component" />
    <VCalendar class="calendar" :selectedCategories="selectedCategories" :selectedGroupId="selectedGroupId"
      :searchResults="searchResults" :show="true" @update-view-mode="updateViewMode" />
  </div>
</template>

<script>
import { defineComponent, ref, computed, watch } from 'vue';
import VCalendar from '@/components/VCalendar.vue';
import TimezoneComponent from '@/components/TimezoneComponent.vue';

export default defineComponent({
  name: 'HomeView',
  components: {
    VCalendar,
    TimezoneComponent
  },
  props: {
    selectedCategories: {
      type: Array,
      required: true
    },
    selectedGroupId: {
      type: Array,
      required: true
    },
    searchResults: {
      type: Array,
      required: true
    }
  },
  setup(props) {
    const currentViewMode = ref('month');
    const calendarValue = ref([]);

    // Watchers for logging prop updates
    watch(() => props.selectedCategories, (newVal) => {
      console.log('Updated selectedCategories:', newVal);
    });

    watch(() => props.selectedGroupId, (newVal) => {
      console.log('Updated selectedGroupId:', newVal);
    });

    watch(() => props.searchResults, (newVal) => {
      console.log('Updated searchResults:', newVal);
    });

    const updateViewMode = (viewMode) => {
      currentViewMode.value = viewMode;
    };

    const isTimezoneVisible = computed(() => currentViewMode.value === 'week' || currentViewMode.value === 'day');

    return {
      calendarValue,
      currentViewMode,
      isTimezoneVisible,
      updateViewMode
    };
  }
});
</script>

<style>
.container {
  display: flex;
  flex-direction: row;
  margin-top: 60px;
  margin-left:80px;
  /* 헤더 높이만큼의 여백을 추가 */
  height: calc(100vh - 60px);
  /* 전체 높이에서 헤더 높이를 뺀 높이 */
  width: 100%;
  /* 전체 너비 사용 */
}

.timezone-component {
  width: 200px;
  /* 타임존 컴포넌트의 너비 고정 */
  height: 100%;
  /* 전체 높이 사용 */
  margin-right: 0px;
  margin-top: 70px;
}

.calendar {
  flex-grow: 1;
  /* 남은 공간 모두 사용 */
  height: 100%;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .timezone-component {
    display: none;
    /* 작은 화면에서는 타임존 컴포넌트 숨김 */
  }

  .container {
    flex-direction: column;
    padding: 10px;
  }

  .calendar {
    width: 100%;
    height: auto;
  }
}
</style>
