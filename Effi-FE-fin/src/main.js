// main.js
import './assets/main.css';
import { createApp } from 'vue';
import { createPinia } from 'pinia';

import vuetify from './plugins/vuetify'; // vuetify 설정 파일을 불러옵니다
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import { loadFonts } from './plugins/webfontloader';
import './plugins/dayjs';

import BootstrapVue3 from 'bootstrap-vue-3';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css';
import App from './App.vue';
import router from './router';

const app = createApp(App);

loadFonts();

app.use(createPinia());
app.use(router);
app.use(vuetify);
app.component('VueDatePicker', VueDatePicker);

app.use(BootstrapVue3);

app.mount('#app');
