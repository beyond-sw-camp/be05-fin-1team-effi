<template>
  <div class="chart-container">
    <h3>Tag 사용 비율</h3>
    <Pie v-if="chartData" :data="chartData" :options="chartOptions" />
    <p v-else>Loading...</p>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import { Pie } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from 'chart.js';
import DataLabelsPlugin from 'chartjs-plugin-datalabels';
import axiosInstance from '@/services/axios';

// Chart.js 플러그인 등록
ChartJS.register(Title, Tooltip, Legend, ArcElement, DataLabelsPlugin);

export default defineComponent({
  name: 'TagAnalyze',
  components: {
    Pie
  },
  setup() {
    const chartData = ref(null);
    const chartOptions = ref({
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        datalabels: {
          color: '#000',
          font:{
            weight: 'bold',
          },
          align: 'center',
          anchor: 'center',
          formatter: (value, context) => {
            const label = context.chart.data.labels[context.dataIndex];
            return `${label}: ${value}%`;
          }
        },
        legend: {
          position: 'top',
          labels: {
            usePointStyle: true,
          },
          padding : 0
        }
        // ,
        // title: {
        //   display: true,
        //   text: 'Tag 사용 비율',
        //   font: {
        //     size: 24,
        //     weight: 'bold',
        //     family: 'Arial, sans-serif' // Use the same font family as h3
        //   },
        //   padding: {
        //     top: 20,
        //     bottom: 10
        //   },
        //   color: '#4A4A4A' // Match the color to the h3 color if needed
        // }
      },
      layout: {
        padding: {
          top: 0,
          bottom: 0,
          left: 0,
          right: 0
        }
      }
    });

    const fetchTagRatio = async () => {
      try {
        const response = await axiosInstance.get('/api/tag/find/tagRatio');
        const data = response.data;

        // 데이터가 올바른지 콘솔에 출력하여 확인
        console.log('Fetched data:', data);

        // 응답 데이터를 처리하여 차트 데이터 형식으로 변환
        const labels = data.map(item => Object.keys(item)[0]);
        const values = data.map(item => Object.values(item)[0]);

        chartData.value = {
          labels: labels,
          datasets: [
            {
              backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'],
              data: values
            }
          ]
        };
      } catch (error) {
        console.error('Error fetching tag ratio:', error);
      }
    };

    onMounted(() => {
      fetchTagRatio();
    });

    return {
      chartData,
      chartOptions
    };
  }
});
</script>

<style scoped>
.chart-container {
  position: relative;
  width: 400px;
  height: 500px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-container h3 {
  margin-bottom: 10px;
  text-align: center;
}

</style>
