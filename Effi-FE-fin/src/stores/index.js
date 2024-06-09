import { createStore } from 'vuex';

const store = createStore({
  state: {
    calendar: {
      dialog: false,
      calendar: {
        title: '',
        content: '',
        startDate: '',
        startTime: '',
        endDate: '',
        endTime: '',
      },
    },
  },
  mutations: {
    OPEN_CALENDAR_DIALOG(state) {
      state.calendar.dialog = true;
    },
    CLOSE_CALENDAR_DIALOG(state) {
      state.calendar.dialog = false;
    },
    SET_CALENDAR(state, calendar) {
      state.calendar.calendar = calendar;
    },
  },
  actions: {
    REQUEST_ADD_EVENT({ commit }, calendar) {
      // API 호출 또는 비즈니스 로직 추가
      commit('SET_CALENDAR', calendar);
    },
  },
  getters: {
    calendarDialog: state => state.calendar.dialog,
    calendarData: state => state.calendar.calendar,
  },
});

export default store;
