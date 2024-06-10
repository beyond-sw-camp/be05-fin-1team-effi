<template>
  <div class="week-navigator">
    <button @click="prevWeek">&lt;</button>
    <span>{{ formattedWeek }}</span>
    <button @click="nextWeek">&gt;</button>
  </div>
</template>

<script>
import { format } from 'date-fns';

export default {
  props: {
    currentWeek: {
      type: Date,
      required: true
    }
  },
  computed: {
    formattedWeek() {
      const startOfWeek = this.startOfWeek(this.currentWeek);
      const endOfWeek = this.endOfWeek(this.currentWeek);
      return `${format(startOfWeek, 'yyyy년 MM월 dd일')} ~ ${format(endOfWeek, 'yyyy년 MM월 dd일')}`;
    }
  },
  methods: {
    startOfWeek(date) {
      const day = date.getDay();
      const diff = date.getDate() - day + (day === 0 ? -6 : 1);
      return new Date(date.setDate(diff));
    },
    endOfWeek(date) {
      const start = this.startOfWeek(date);
      return new Date(start.getFullYear(), start.getMonth(), start.getDate() + 6);
    },
    prevWeek() {
      this.$emit('change-week', -1);
    },
    nextWeek() {
      this.$emit('change-week', 1);
    }
  }
};
</script>

<style scoped>
.week-navigator {
  display: flex;
  align-items: center;
}

button {
  margin: 0 10px;
}
</style>
