<template>
  <v-dialog v-model="internalDialog" max-width="500px">
    <v-card>
      <v-card-title>
        <span class="text-h5">Add Schedule</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form">
          <v-text-field v-model="schedule.title" label="Title" required></v-text-field>
          <v-textarea v-model="schedule.context" label="Context" required></v-textarea>
          <v-menu
            v-model="startMenu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="schedule.startTime"
                label="Start Time"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker v-model="schedule.startTime" @input="startMenu = false"></v-date-picker>
          </v-menu>
          <v-menu
            v-model="endMenu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="schedule.endTime"
                label="End Time"
                prepend-icon="mdi-calendar"
                readonly
                v-bind="attrs"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker v-model="schedule.endTime" @input="endMenu = false"></v-date-picker>
          </v-menu>
          <v-select
            v-model="schedule.status"
            :items="statuses"
            label="Status"
            required
          ></v-select>
          <v-switch
            v-model="schedule.notificationYn"
            label="Notification"
          ></v-switch>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
        <v-btn color="blue darken-1" text @click="saveSchedule">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { ref, watch } from 'vue';
import axios from 'axios';

export default {
  props: {
    dialog: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['update:dialog', 'schedule-saved'],
  setup(props, { emit }) {
    const internalDialog = ref(props.dialog);
    const schedule = ref({
      title: '',
      context: '',
      startTime: '',
      endTime: '',
      status: 1,
      notificationYn: false,
    });

    const startMenu = ref(false);
    const endMenu = ref(false);
    const statuses = ref([
      { text: 'Pending', value: 1 },
      { text: 'Confirmed', value: 2 },
      { text: 'Cancelled', value: 3 },
    ]);

    watch(() => props.dialog, (newVal) => {
      internalDialog.value = newVal;
    });

    watch(internalDialog, (newVal) => {
      emit('update:dialog', newVal);
    });

    const saveSchedule = async () => {
      try {
        const scheduleData = {
          ...schedule.value,
          startTime: new Date(schedule.value.startTime).toISOString(),
          endTime: new Date(schedule.value.endTime).toISOString(),
        };
        await axios.post('/api/schedule/add', scheduleData);
        emit('schedule-saved');
        internalDialog.value = false;
      } catch (error) {
        console.error('Failed to save schedule:', error);
      }
    };

    const close = () => {
      internalDialog.value = false;
    };

    return {
      internalDialog,
      schedule,
      startMenu,
      endMenu,
      statuses,
      saveSchedule,
      close,
    };
  },
};
</script>
