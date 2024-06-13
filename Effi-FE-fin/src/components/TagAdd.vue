<template>
    <div class="pa-4 text-center">
      <v-dialog v-model="dialog" max-width="600">
        <template v-slot:activator="{ props: activatorProps }">
          <button
          class="update-button"
          v-bind="activatorProps"
        >태그 추가하기</button>
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
                        :style="{ backgroundColor: getRandomColor() }"
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
  
            <v-btn text="Close" variant="plain" @click="dialog = false"></v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </div>
</template>
  
<script>
  import { TrackOpTypes, ref } from 'vue';
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
      const errorMessage = ref('');
  
      const addTag = () => {
        errorMessage.value = '';
        const trimmedTag = newTag.value.trim();
        if (trimmedTag !== '' && !tags.value.includes(trimmedTag)) {
          tags.value.push(trimmedTag);
          newTag.value = '';
          emitTags();
        } else {
          errorMessage.value = '중복된 태그입니다';
          newTag.value = '';
        }
      };
        
      const removeTag = (index) => {
        tags.value.splice(index, 1);
        emitTags();
      };

      const removeTagByName = (tag) => {
        const index = tags.value.indexOf(tag);
        if (index !== -1) {
          tags.value.splice(index, 1);
          emitTags();
        }
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
  
      const message = ref(props.schedule.title + ' ' + props.schedule.content);
  
      //api/aiTags data 받아오기
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

      const getRandomFluorescentColor = () => {
        const fluorescentColors = [
          '#CCFF00', // Fluorescent Chartreuse
          '#FFFF00', // Fluorescent Yellow
          '#00FF00', // Fluorescent Green
          '#00FFFF', // Fluorescent Cyan
          '#FF00FF', // Fluorescent Magenta
          '#FF1493', // Deep Pink
          '#FF4500', // Orange Red
          '#FF6347', // Tomato
          '#FFD700', // Gold
          '#ADFF2F', // Green Yellow
          '#7CFC00', // Lawn Green
          '#32CD32', // Lime Green
          '#98FB98', // Pale Green
          '#00FF7F', // Spring Green
          '#00FA9A', // Medium Spring Green
          '#40E0D0', // Turquoise
          '#20B2AA', // Light Sea Green
          '#00CED1', // Dark Turquoise
          '#1E90FF', // Dodger Blue
          '#FF69B4', // Hot Pink
          '#FFB6C1', // Light Pink
        ];
        return fluorescentColors[Math.floor(Math.random() * fluorescentColors.length)];
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
        errorMessage,
        getRandomFluorescentColor,
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
  .update-button {
    display: inline-block;
    margin-top: 10px;
    padding: 10px;
    background-color: #FBB584;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 5px;
  }

  .error-message {
    color: red;
    margin-top: 10px;
  }
</style>
  