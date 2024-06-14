<template>
  <div class="pa-4 text-center">
    <v-dialog v-model="dialog" max-width="600">
      <template v-slot:activator="{ props: activatorProps }">
        <v-btn
          id="add-tag-button"
          class="text-none font-weight-regular"
          text="태그 추가하기"
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
              <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
              <v-row>
                <v-col>
                  <div class="chip-wrapper">
                    <v-chip
                      v-for="(tag, index) in tags"
                      :key="index"
                      :style="{ backgroundColor: getTagColor(tag) }"
                      close
                      @click="removeTag(index)"
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
                      :style="{ backgroundColor: getTagColor(tag) }"
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
import { ref, reactive, watch } from 'vue';
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
    const tagColors = reactive({});
    const message = ref('');
    const errorMessage = ref('');


    watch(
      () => [props.schedule.title, props.schedule.context],
      ([newTitle, newContext]) => {
        message.value = `${newTitle} ${newContext}`;
      }
    );

    const addTag = () => {
      errorMessage.value = '';
      if (newTag.value.trim() !== '') {
        const tag = newTag.value.trim();
        if (!tags.value.includes(tag)) {
          tags.value.push(tag);
          if (!tagColors[tag]) {
            tagColors[tag] = getRandomColor();
          }
          newTag.value = '';
          emitTags();
        } else {
          errorMessage.value = '중복된 태그입니다';
          newTag.value = '';
        }
      }
    };

    const removeTag = (index) => {
      tags.value.splice(index, 1);
      emitTags();
    };

    const addTagFromAi = (tag) => {
      if (!tags.value.includes(tag)) {
        tags.value.push(tag);
        if (!tagColors[tag]) {
          tagColors[tag] = getRandomColor();
        }
        emitTags();
      }
    };

    const emitTags = () => {
      emit('update-schedule', { tags: tags.value, tagColors: { ...tagColors } });
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

    const getRandomColor = () => {
      const colors = ['#FFB6C1', '#FF69B4', '#FF1493', '#DB7093', '#C71585'];
      return colors[Math.floor(Math.random() * colors.length)];
    };

    const getTagColor = (tag) => {
      if (!tagColors[tag]) {
        tagColors[tag] = getRandomColor();
      }
      return tagColors[tag];
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
      getTagColor,
      errorMessage
    };
  }
};
</script>

<style scoped>
button{
  display: inline-block;
  margin-top: 10px;
  padding: 10px;
  background-color: #FBB584;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;

}
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
.error-message {
    color: red;
    margin-top: 10px;
    font-size: 12px;
  }
</style>
