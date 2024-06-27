<template>
  <div class="container">
    <div class="timezone-and-calendar">
      <TimezoneComponent v-if="isTimezoneVisible" class="timezone-component" />
      <VCalendar class="calendar" :selectedCategories="selectedCategories" :searchResults="searchResults"
        :selectedGroupId="selectedGroupId" :show="true" @update-view-mode="updateViewMode" />
    </div>
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
    const isTimezoneVisible = computed(() => currentViewMode.value === 'week' || currentViewMode.value === 'day');

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

    return {
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
  margin-top: 60px;
  height: calc(100vh - 60px);
  width: 100%;
}

.timezone-and-calendar {
  display: flex;
  flex-grow: 1;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

.timezone-component {
  width: 200px;
  height: 100%;
  margin-right: 20px;
}

.calendar {
  flex-grow: 1;
  height: 100%;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .timezone-component {
    display: none;
  }

  .timezone-and-calendar {
    width: 100%;
    padding: 10px;
  }

  .calendar {
    width: 100%;
    height: auto;
  }
}
</style>