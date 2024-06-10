<template>
  <v-dialog max-width="600px" persistent v-model="dialog">
    <v-card>
      <v-card-title>
        <h3>일정 추가</h3>
      </v-card-title>
      <v-card-text>
        <v-form class="px-3" ref="form">
          <v-text-field label="일정" v-model="event.title" prepend-icon="mdi-folder-marker" :rules="inputRules"></v-text-field>
          <v-textarea label="상세설명" v-model="event.content" prepend-icon="mdi-pencil" :rules="inputRules"></v-textarea>
          <v-row>
            <v-col cols="6" class="pb-0">
              <v-menu>
                <template v-slot:activator="{ on }">
                  <v-text-field label="시작일" readonly prepend-icon="mdi-calendar-month" v-on="on" v-model="event.startDate"></v-text-field>
                </template>
                <v-date-picker v-model="event.startDate"></v-date-picker>
              </v-menu>
            </v-col>
            <v-col cols="6" class="pb-0">
              <v-menu :close-on-content-click="false" v-model="startTimer" offset-y>
                <template v-slot:activator="{ on }">
                  <v-text-field label="시작 시간" readonly prepend-icon="mdi-timer" v-on="on" v-model="event.startTime"></v-text-field>
                </template>
                <v-time-picker v-if="startTimer" v-model="event.startTime">
                  <v-btn class="mx-auto" @click="selectTime">선택</v-btn>
                </v-time-picker>
              </v-menu>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="6" class="pt-0">
              <v-menu>
                <template v-slot:activator="{ on }">
                  <v-text-field label="종료일" readonly prepend-icon="mdi-calendar-month" v-on="on" v-model="event.endDate"></v-text-field>
                </template>
                <v-date-picker v-model="event.endDate" :allowed-dates="allowedDates"></v-date-picker>
              </v-menu>
            </v-col>
            <v-col cols="6" class="pt-0">
              <v-menu :close-on-content-click="false" v-model="endTimer" offset-y>
                <template v-slot:activator="{ on }">
                  <v-text-field label="종료 시간" readonly prepend-icon="mdi-timer" v-on="on" v-model="event.endTime"></v-text-field>
                </template>
                <v-time-picker v-if="endTimer" v-model="event.endTime">
                  <v-btn class="mx-auto" @click="selectTime">선택</v-btn>
                </v-time-picker>
              </v-menu>
            </v-col>
          </v-row>
          <div class="text-center">
            <v-btn text class="primary white--text mx-2 mt-3" @click="submit">추가</v-btn>
            <v-btn text class="primary white--text mx-2 mt-3" @click="close">닫기</v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import { ref, computed } from 'vue';
import { useStore } from 'vuex';

export default {
  setup() {
    const store = useStore();
    const dialog = computed(() => store.state.dialog);
    const event = computed(() => store.state.event);
    const startTimer = ref(false);
    const endTimer = ref(false);

    const inputRules = [v => !!v || 'This field is required'];

    const submit = () => {
      if (event.value.title === '' || event.value.endDate === '') {
        store.commit('SET_SNACKBAR', { message: '제목과 종료일자를 작성해주세요.', color: 'error', position: 'top' });
      } else {
        store.dispatch('REQUEST_ADD_EVENT', event.value);
      }
    };

    const close = () => {
      store.commit('SET_DIALOG', false);
    };

    const selectTime = () => {
      endTimer.value = false;
      startTimer.value = false;
    };

    const allowedDates = (val) => {
      let endDate = val.split('-').reduce((a, b) => a + b);
      let startDate = event.value.startDate.split('-').reduce((a, b) => a + b);
      return endDate >= startDate;
    };

    return {
      dialog,
      event,
      startTimer,
      endTimer,
      inputRules,
      submit,
      close,
      selectTime,
      allowedDates,
    };
  },
};
</script>
