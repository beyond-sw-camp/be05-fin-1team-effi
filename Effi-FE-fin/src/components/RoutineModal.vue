<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="close">×</button>
      <h2>루틴 추가</h2>
      <form @submit.prevent="submitRoutine">
        <div class="form-group">
          <label for="routineStart">시작일:</label>
          <input type="date" id="routineStart" v-model="routineStart" required>
        </div>
        <div class="form-group">
          <label for="routineEnd">종료일:</label>
          <input type="date" id="routineEnd" v-model="routineEnd" required>
        </div>
        <div class="form-group">
          <label for="routineCycle">반복 주기:</label>
          <select id="routineCycle" v-model="routineCycle" required>
            <option value="daily">매일</option>
            <option value="weekly">매주</option>
            <option value="monthly">매월</option>
            <option value="yearly">매년</option>
          </select>
        </div>
        <div class="modal-footer">
          <button type="submit">확인</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/services/axios';
import { ref } from 'vue';

export default {
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  emits: ['confirm-routine'],
  setup(props, { emit }) {
    const routineStart = ref('');
    const routineEnd = ref('');
    const routineCycle = ref('daily');

    const close = () => {
      emit('close-routine');
    };

    const submitRoutine = async () => {
      try {
        const response = await axiosInstance.post('/api/routine/add', {
          routineStart: routineStart.value,
          routineEnd: routineEnd.value,
          routineCycle: routineCycle.value
        });
        console.log('Routine added:', response.data);
        emit('confirm-routine', response.data);
        close();
      } catch (error) {
        console.error('Error adding routine:', error);
      }
    };

    return {
      routineStart,
      routineEnd,
      routineCycle,
      close,
      submitRoutine
    };
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-container {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 400px;
  max-height: 90vh;
  overflow-y: auto;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  position: absolute;
  top: 10px;
  right: 10px;
}

h2 {
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input, select {
  width: calc(100% - 20px);
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
}

.modal-footer {
  text-align: right;
}

.modal-footer button {
  padding: 10px 20px;
  background-color: #FBB584;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal-footer button:hover {
  background-color: #EC971F;
}
</style>
