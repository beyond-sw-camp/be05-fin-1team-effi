<template>
  <form @submit.prevent="handleSubmit">
    <div>
      <select v-model="timezoneId" required>
        <option v-for="timezone in timezones" :key="timezone.timezoneId" :value="timezone.timezoneId">
          {{ timezone.timezoneName }}
        </option>
      </select>
      <button class="update-button" type="submit">변경</button>
    </div>
  </form>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    initialTimezoneId: {
      type: Number,
      required: true
    },
  },
  data() {
    return {
      timezoneId: this.initialTimezoneId,
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
        this.$emit('submit', this.timezoneId);
      } else {
        console.error('Timezone ID is required');
      }
    },
  },
};
</script>

<style>
.update-button {
  background-color: #ffcc99;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
