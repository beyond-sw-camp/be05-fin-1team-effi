<template>
  <div class="left-sidebar">
    <button class="create-group-button" @click="showModal = true">
      <span class="plus-icon">+</span>
      <span class="text">group<br>create</span>
      <span class="dropdown-icon">▼</span>
    </button>
    <CreateGroupModal :show="showModal" @close="closeModal" />
  </div>
</template>

<script>
import CreateGroupModal from '@/components/CreateGroupModal.vue';

export default {
  components: { CreateGroupModal },
  data() {
    return {
      showModal: false,
    };
  },
  methods: {
    closeModal() {
      this.showModal = false;
    }
  }
}
</script>

<style scoped>
.left-sidebar {
  background-color: #FBB584;
  padding: 20px;
}

.create-group-button {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: white;
  color: #333;
  border: none;
  border-radius: 50px; /* 원형 모양 */
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
  cursor: pointer;
  width: 200px; /* 버튼의 너비 설정 */
}

.create-group-button:hover {
  background-color: #f1f1f1;
}

.plus-icon {
  font-size: 24px;
  margin-right: 10px;
}

.text {
  flex: 1;
  text-align: center;
  font-size: 16px;
  line-height: 1.2;
  font-weight: bold;
}

.dropdown-icon {
  font-size: 12px;
  margin-left: 10px;
}
</style>
