import './assets/main.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import vuetify from './plugins/vuetify'; // vuetify 설정 파일을 불러옵니다
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import { loadFonts } from './plugins/webfontloader';
import './plugins/dayjs';

import App from './App.vue'
import router from './router'

const app = createApp(App);

loadFonts();

app.use(createPinia());
app.use(router);
app.use(vuetify);
app.component('VueDatePicker', VueDatePicker);

app.mount('#app');
