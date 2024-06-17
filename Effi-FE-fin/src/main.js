import { createApp } from 'vue';
import { createPinia } from 'pinia';

import vuetify from './plugins/vuetify'; // vuetify 설정 파일을 불러옵니다

import '@vuepic/vue-datepicker/dist/main.css';
import { loadFonts } from './plugins/webfontloader';
import dayjs from './plugins/dayjs'; // dayjs 플러그인 설정을 불러옵니다

import BootstrapVue3 from 'bootstrap-vue-3';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css';
import 'bootstrap-icons/font/bootstrap-icons.css';


import moment from 'moment'; // moment를 직접 불러옵니다

import App from './App.vue';
import router from './router';
import store from './stores';

const app = createApp(App);

loadFonts();

app.use(createPinia());
app.use(router);
app.use(vuetify);
app.use(BootstrapVue3);
app.use(store);

// app.use(VueMoment); // vue-moment 제거

app.config.globalProperties.$dayjs = dayjs; // dayjs를 전역 프로퍼티로 설정
app.config.globalProperties.$moment = moment; // moment를 전역 프로퍼티로 설정

app.mount('#app');
