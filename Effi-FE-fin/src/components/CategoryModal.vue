<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
<!--      <h2>카테고리 선택</h2>-->
      <ul>
        <li @click="openModal('company')">회사</li>
        <li @click="openModal('department')">부서</li>
        <li @click="openModal('group')">그룹</li>
        <li @click="openModal('individual')">개인</li>
      </ul>
    </div>
    <DepartmentModal v-if="showDepartmentModal" @close="closeModal('department')" />
    <GroupModal v-if="showGroupModal" @close="closeModal('group')" />
  </div>
</template>

<script>
import DepartmentModal from './DeptModal.vue';
import GroupModal from './GroupModal.vue';

export default {
  components : {DepartmentModal, GroupModal},
  data() {
    return {
      showDepartmentModal: false,
      showGroupModal: false,
    };
  },
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  methods: {
    openModal(type) {
      if (type === 'department') {
        this.showDepartmentModal = true;
      } else if (type === 'group') {
        this.showGroupModal = true;
      }
    },
    closeModal(type) {
      if (type === 'department') {
        this.showDepartmentModal = false;
      } else if (type === 'group') {
        this.showGroupModal = false;
      }
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-container {
  background: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 200px;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  position: absolute;
  top: 10px;
  right: 10px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  cursor: pointer;
}
</style>
