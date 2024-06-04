// plugins/vuetify.js
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import { VCalendar } from 'vuetify/labs/VCalendar';
import dayjs from './dayjs';
import DayjsUtils from '@date-io/dayjs';

const dateAdapter = new DayjsUtils({ instance: dayjs });

export default createVuetify({
  components: {
    ...components,
    VCalendar,
  },
  directives,
  date: {
    adapter: dateAdapter,
  },
});
