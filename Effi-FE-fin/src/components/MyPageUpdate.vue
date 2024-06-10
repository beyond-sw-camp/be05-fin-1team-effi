<template>
  <form @submit.prevent="handleSubmit">
    <div>
      <label for="timezoneId">내 기본 시간대</label>
      <select v-model="timezoneId" id="timezoneId" required>
        <option v-for="timezone in timezones" :key="timezone.timezoneId" :value="timezone.timezoneId">
          {{ timezone.timezoneName }}
        </option>
      </select>
    </div>
    <button type="submit">업데이트</button>
  </form>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    onSubmit: {
      type: Function,
      required: true,
    },
  },
  data() {
    return {
      timezoneId: null,
      timezones: [],
    };
  },
  async created() {
    console.log('MyPageUpdate component created');
    await this.fetchTimezones();
  },
  methods: {
    async fetchTimezones() {
      console.log('Fetching timezones...');
      try {
        const response = await axios.get('http://localhost:8080/api/mypage/timezones');
        console.log('Timezones fetched:', response.data);
        this.timezones = response.data;
      } catch (error) {
        console.error('시간대 목록을 불러오는 중 오류가 발생했습니다:', error);
      }
    },
    handleSubmit() {
      console.log('Submitting timezone update with timezoneId:', this.timezoneId);
      if (this.timezoneId) {
        this.onSubmit(this.timezoneId);
      } else {
        console.error('Timezone ID is required');
      }
    },
  },
};
</script>
