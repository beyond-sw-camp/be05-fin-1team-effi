import { createStore } from 'vuex';
import axios from 'axios';

const initEvent = () => ({
  startDate: '',
  startTime: '',
  endDate: '',
  endTime: '',
  content: '',
  title: '',
});

const state = {
  event: initEvent(),
  events: [],
  dialog: false,
  snackbar: {
    visible: false,
    message: '',
    color: '',
    position: '',
  },
};

const mutations = {
  ADD_EVENT(state, event) {
    state.events.push(event);
    state.dialog = false;
    state.event = initEvent();
  },
  SET_DIALOG(state, value) {
    state.dialog = value;
  },
  SET_EVENT(state, event) {
    state.event = event;
  },
  SET_SNACKBAR(state, { message, color, position }) {
    state.snackbar.visible = true;
    state.snackbar.message = message;
    state.snackbar.color = color;
    state.snackbar.position = position;
  },
  RESET_SNACKBAR(state) {
    state.snackbar.visible = false;
    state.snackbar.message = '';
    state.snackbar.color = '';
    state.snackbar.position = '';
  },
};

const actions = {
  async REQUEST_ADD_EVENT({ commit }, calendar) {
    try {
      const response = await axios.post('/api/schedule/add', calendar);
      const addedEvent = makeEvent(response.data);
      commit('ADD_EVENT', addedEvent);
      commit('SET_SNACKBAR', { message: '일정이 추가 되었습니다.', color: 'info', position: 'top' });
    } catch (e) {
      console.log('일정 추가 에러', e);
      commit('SET_SNACKBAR', { message: '일정 추가에 실패했습니다.', color: 'error', position: 'top' });
    }
  },
};

const colors = ['blue', 'indigo', 'deep-purple', 'green', 'orange', 'red'];

const makeEvent = (event) => ({
  name: event.title,
  start: event.startTime,
  end: event.endTime,
  color: colors[Math.floor(Math.random() * 6)],
});

export default createStore({
  state,
  mutations,
  actions,
});
