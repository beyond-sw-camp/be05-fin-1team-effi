<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
      <ul>
        <li @click="openModal('company')">회사</li>
        <li @click="openModal('department')">부서</li>
        <li @click="openModal('group')">그룹</li>
        <li @click="openModal('individual')">개인</li>
      </ul>
    </div>
    <DepartmentModal
      v-if="showDepartmentModal"
      :show="showDepartmentModal"
      @close="closeModal('department')"
      @select-dept="handleSelectDept"
    />
    <GroupModal
      v-if="showGroupModal"
      :show="showGroupModal"
      @close="closeModal('group')"
      @select-group="handleSelectGroup" />
  </div>
</template>

<script>
import DepartmentModal from './DeptModal.vue';
import GroupModal from './GroupModal.vue';

export default {
  components: { DepartmentModal, GroupModal },
  data() {
    return {
      showDepartmentModal: false,
      showGroupModal: false,
      selectedDeptId: null,
      selectedGroupId: null,
      selectedOption: null
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
        this.selectedOption = 2;
      } else if (type === 'group') {
        this.showGroupModal = true;
        this.selectedOption = 3;
      } else if (type === 'individual') {
        this.selectedOption = 4;
        this.returnSelection();
      } else {
        this.selectedOption = 1;
        this.returnSelection();
      }
    },
    closeModal(type) {
      if (type === 'department') {
        this.showDepartmentModal = false;
      } else if (type === 'group') {
        this.showGroupModal = false;
      }
    },
    handleSelectDept(deptId) {
      this.selectedDeptId = deptId;
      this.showDepartmentModal = false;
      this.returnSelection();
    },
    handleSelectGroup(groupId) {
      this.selectedGroupId = groupId;
      this.showGroupModal = false;
      this.returnSelection();
    },
    handleClose() {
      this.$emit('close');
    },
    returnSelection() {
      const selectedResult = {
        selectedOption: this.selectedOption,
        selectedDeptId: this.selectedDeptId,
        selectedGroupId: this.selectedGroupId
      };
      this.$emit('select', selectedResult);
      this.handleClose();
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
