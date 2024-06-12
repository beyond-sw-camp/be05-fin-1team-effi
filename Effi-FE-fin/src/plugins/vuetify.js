// plugins/vuetify.js
import { createVuetify } from 'vuetify';
import '@mdi/font/css/materialdesignicons.css'; // MDI 아이콘 CSS 포함
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import { VCalendar } from 'vuetify/labs/VCalendar';
import { VTimePicker } from 'vuetify/labs/VTimePicker'
import { aliases, mdi } from 'vuetify/iconsets/mdi'; // MDI 아이콘 세트를 가져옵니다.


import dayjs from './dayjs';
import DayjsUtils from '@date-io/dayjs';
import DayJsAdapter from '@date-io/dayjs'
import 'vuetify/styles';

// dayjs 확장 추가
import isToday from 'dayjs/plugin/isToday';
import localizedFormat from 'dayjs/plugin/localizedFormat';
import isBetween from 'dayjs/plugin/isBetween';
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import weekOfYear from 'dayjs/plugin/weekOfYear';
import advancedFormat from 'dayjs/plugin/advancedFormat';

dayjs.extend(isToday);
dayjs.extend(localizedFormat);
dayjs.extend(isBetween);
dayjs.extend(isSameOrAfter);
dayjs.extend(isSameOrBefore);
dayjs.extend(customParseFormat);
dayjs.extend(weekOfYear);
dayjs.extend(advancedFormat);

const dateAdapter = new DayjsUtils({ instance: dayjs });

export default createVuetify({
  components: {
    ...components,
    VCalendar,
    VTimePicker
  },
  directives,
  icons: {
    defaultSet: 'mdi', // MDI 아이콘 세트 사용
    aliases,
    sets: {
      mdi,
    },
  },
  date: {
    adapter: DayJsAdapter, dateAdapter
  },
  
});