<template>
  <div class="pa-4 text-center">
    <v-dialog v-model="dialog" max-width="600">
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn
          class="text-none font-weight-regular"
          text="Tags"
          variant="tonal"
          v-bind="activatorProps"
        ></v-btn>
      </template>

      <v-card>
        <v-card-text>
          <v-row>
            <v-col>
              <v-row dense>
                <v-col sm="12">
                  <v-text-field
                    v-model="newTag"
                    label="태그를 입력하세요"
                    required
                    @keyup.enter="addTag"
                    class="custom-text-field"
                  >
                    <template v-slot:append-inner>
                      <v-btn icon @click="addTag">
                        <v-icon>mdi-check</v-icon>
                      </v-btn>
                    </template>
                  </v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  <div class="chip-wrapper">
                    <v-chip
                      v-for="(tag, index) in tags"
                      :key="index"
                      close
                      @click:close="removeTag(index)"
                    >
                      {{ tag }}
                    </v-chip>
                  </div>
                </v-col>
              </v-row>
            </v-col>

            <v-divider vertical></v-divider>

            <v-col>
              <v-row dense justify="center" align="center">
                <v-btn @click="aiTags">AI 추천</v-btn>
              </v-row>
              <v-row>
                <v-col>
                  <div class="chip-wrapper">
                    <v-chip
                      v-for="(tag, index) in aiResponseTags"
                      :key="index"
                      @click="addTagFromAi(tag)"
                    >
                      {{ tag }}
                    </v-chip>
                  </div>
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn text="Close" variant="plain" @click="closeDialog"></v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { ref, watch } from 'vue';
import axiosInstance from '@/services/axios';

export default {
  props: {
    schedule: Object,
  },
  emits: ['update-schedule'],
  setup(props, { emit }) {
    const dialog = ref(false);
    const newTag = ref('');
    const tags = ref([]);
    const aiResponseTags = ref([]);
    const message = ref('');

    watch(
      () => [props.schedule.title, props.schedule.context],
      ([newTitle, newContext]) => {
        message.value = `${newTitle} ${newContext}`;
      }
    );

    const addTag = () => {
      if (newTag.value.trim() !== '') {
        tags.value.push(newTag.value.trim());
        newTag.value = '';
        emitTags();
      }
    };

    const removeTag = (index) => {
      tags.value.splice(index, 1);
      emitTags();
    };

    const addTagFromAi = (tag) => {
      if (!tags.value.includes(tag)) {
        tags.value.push(tag);
        emitTags();
      }
    };

    const emitTags = () => {
      emit('update-schedule', tags.value);
    };

    const aiTags = async () => {
      try {
        const response = await axiosInstance.post('/api/aiTags', {
          message: message.value
        });
        console.log('AI Tags:', response.data);
        aiResponseTags.value = response.data.tags;
      } catch (error) {
        console.error('Error fetching AI tags:', error);
      }
    };

    const closeDialog = () => {
      emitTags();
      dialog.value = false;
    };

    return {
      dialog,
      newTag,
      tags,
      addTag,
      removeTag,
      aiTags,
      aiResponseTags,
      addTagFromAi,
      closeDialog,
      message,
    };
  }
};
</script>

<style scoped>
.chip-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px; /* 칩 사이의 간격을 설정합니다 */
}
.chip-wrapper .v-chip {
  flex: 0 1 auto; /* 칩이 콘텐츠 크기에 맞게 설정되도록 함 */
  box-sizing: border-box;
  cursor: pointer; /* 클릭 가능한 요소로 변경 */
}
</style>
