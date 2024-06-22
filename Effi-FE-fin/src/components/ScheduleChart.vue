<template>
  <div class="chart-container">
    <h3>7일간의 Schedule</h3>
    <canvas v-if="canvasReady" ref="canvas"></canvas>
    <p v-else>Loading...</p>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axiosInstance from '@/services/axios';
import { Chart as ChartJS, Title, Tooltip, Legend, BarController, BarElement, CategoryScale, LinearScale } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarController, BarElement, CategoryScale, LinearScale);

const canvas = ref(null);
const canvasReady = ref(false);
let chart;

const fetchScheduleData = async () => {
  try {
    const response = await axiosInstance.get('/api/schedule/find/7days');
    console.log('Schedule data:', response.data); // 데이터 확인을 위한 로그
    return response.data;
  } catch (error) {
    console.error('Error fetching schedule data:', error);
    return {};
  }
};

const renderChart = async () => {
  const data = await fetchScheduleData();

  if (Object.keys(data).length === 0) {
    console.error('No data available for the chart');
    return;
  }

  // 날짜를 이른 순서로 정렬
  const sortedDates = Object.keys(data).sort();
  const sortedData = sortedDates.map(date => data[date]);

  const chartData = {
    labels: sortedDates,
    datasets: [
      {
        label: 'Schedule Count',
        backgroundColor: '#f87979',
        data: sortedData
      }
    ]
  };

  canvasReady.value = true;

  if (chart) {
    chart.destroy();
  }

  // canvasReady를 true로 설정한 후에 chart를 생성하도록 순서 변경
  await nextTick(() => {
    if (canvas.value) {
      chart = new ChartJS(canvas.value, {
        type: 'bar',
        data: chartData,
        options: {
          responsive: true,
          maintainAspectRatio: false
        }
      });
    }
  });
};

onMounted(renderChart);
</script>

<style scoped>
.chart-container {
  padding: 20px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 600px; /* 폭을 늘림 */
  margin: 0 auto;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-container h3 {
  margin-bottom: 10px;
  text-align: center;
}

canvas {
  display: block; /* 추가된 스타일 */
  width: 100% !important; /* 차트 너비를 강제로 설정 */
  height: 700px !important; /* 차트 높이를 강제로 설정 */
}
</style>