<template>
  <div class="search-navigator">
    <select class="form-select short-select" v-model="viewMode">
      <option value="day">일</option>
      <option value="week">주</option>
      <option value="month">월</option>
    </select>
    <button @click="prev" class="btn btn-outline-secondary me-2">&lt;</button>
    <span class="formatted-period">{{ formattedPeriod }}</span>
    <button @click="next" class="btn btn-outline-secondary ms-2">&gt;</button>
    <button @click="goToToday" class="btn btn-outline-primary ms-3">Today</button>
    <button @click="goToHome" class="btn btn-outline-secondary ms-2">달력</button> <!-- 추가된 부분 -->
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { format, startOfWeek, endOfWeek, startOfMonth, endOfMonth, addDays, subDays, addWeeks, subWeeks, addMonths, subMonths } from 'date-fns';
import { useRouter } from 'vue-router'; // 추가된 부분

const props = defineProps({
  currentPeriod: {
    type: Date,
    required: true
  },
  viewMode: {
    type: String,
    default: 'week'
  }
});

const emit = defineEmits(['change-view-mode', 'change-period']);

const viewMode = ref(props.viewMode);
const currentPeriod = ref(props.currentPeriod);

const formattedPeriod = computed(() => {
  if (viewMode.value === 'day') {
    return format(currentPeriod.value, 'yyyy년 MM월 dd일');
  } else if (viewMode.value === 'week') {
    const start = startOfWeek(currentPeriod.value, { weekStartsOn: 1 });
    const end = endOfWeek(currentPeriod.value, { weekStartsOn: 1 });
    return `${format(start, 'yyyy년 MM월 dd일')} ~ ${format(end, 'yyyy년 MM월 dd일')}`;
  } else if (viewMode.value === 'month') {
    const start = startOfMonth(currentPeriod.value);
    const end = endOfMonth(currentPeriod.value);
    return `${format(start, 'yyyy년 MM월 dd일')} ~ ${format(end, 'yyyy년 MM월 dd일')}`;
  } else {
    return format(currentPeriod.value, 'yyyy년 MM월 dd일');
  }
});

watch(
  () => props.currentPeriod,
  (newVal) => {
    currentPeriod.value = newVal;
  },
  { immediate: true }
);

watch(
  () => props.viewMode,
  (newVal) => {
    viewMode.value = newVal;
  },
  { immediate: true }
);

watch(viewMode, () => {
  emit('change-view-mode', viewMode.value);
});

const goToToday = () => {
  const today = new Date();
  emit('change-period', today);
};

const prev = () => {
  if (viewMode.value === 'day') {
    emit('change-period', subDays(currentPeriod.value, 1));
  } else if (viewMode.value === 'week') {
    emit('change-period', subWeeks(currentPeriod.value, 1));
  } else if (viewMode.value === 'month') {
    emit('change-period', subMonths(currentPeriod.value, 1));
  }
};

const next = () => {
  if (viewMode.value === 'day') {
    emit('change-period', addDays(currentPeriod.value, 1));
  } else if (viewMode.value === 'week') {
    emit('change-period', addWeeks(currentPeriod.value, 1));
  } else if (viewMode.value === 'month') {
    emit('change-period', addMonths(currentPeriod.value, 1));
  }
};

const router = useRouter(); // 추가된 부분

const goToHome = () => { // 추가된 부분
  router.push({ name: 'home' }); // 추가된 부분
};
</script>

<style scoped>
.search-navigator {
  display: flex;
  align-items: center;
}

.form-select.short-select {
  width: auto;
  padding: 5px;
  margin: 0 10px;
}

button {
  margin: 0 10px;
}

button.active {
  font-weight: bold;
  background-color: #e0e0e0;
}

.formatted-period {
  font-size: 1.1rem;
  /* 글자 크기 확대 */
  font-weight: bold;
  /* 글자 진하게 */
}
</style>