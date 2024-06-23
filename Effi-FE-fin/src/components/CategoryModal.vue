<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="handleClose">×</button>
      <!-- List of categories -->
      <ul>
        <li @click="selectCategory(1)">회사</li>
        <li @click="openModal('department')">부서</li>
        <li @click="openModal('group')">그룹</li>
        <li @click="selectCategory(4)">개인</li>
      </ul>
      <!-- Display previously selected category and allow modification -->
      <div v-if="selectedOption">
        <p>선택된 카테고리:</p>
        <p>{{ getCategoryName(selectedOption) }}</p>
        <button @click="clearSelection">변경하기</button>
      </div>
    </div>
    <!-- DepartmentModal component for selecting departments -->
    <DepartmentModal
      v-if="showDepartmentModal"
      :show="showDepartmentModal"
      @close="closeModal('department')"
      @select-dept="handleSelectDept"
    />
    <!-- GroupModal component for selecting groups -->
    <GroupModal
      v-if="showGroupModal"
      :show="showGroupModal"
      @close="closeModal('group')"
      @select-group="handleSelectGroup"
    />
  </div>
</template>

<script>
import axios from 'axios';
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
      selectedOption: null,  // Track selected category type (1: company, 2: department, 3: group, 4: individual)
      categoryNo: null,
      categoryName: '',
    };
  },
  props: {
    show: {
      type: Boolean,
      required: true
    },
    scheduleId: {
      type: Number,
      required: false,
      default: null
    }
  },
  watch: {
    show: {
      handler(newVal) {
        if (newVal && this.scheduleId) {
          this.fetchCategory();
        }
      },
      immediate: true
    }
  },
  methods: {
    async fetchCategory() {
      if (!this.scheduleId) return;
      
      try {
        const response = await axios.get(`/api/schedule/find/${this.scheduleId}`);
        const categoryNo = response.data.categoryNo;
        this.selectedOption = categoryNo;
        this.categoryName = this.getCategoryName(categoryNo);
      } catch (error) {
        console.error('Error fetching category:', error);
      }
    },
    openModal(type) {
      if (type === 'department') {
        this.showDepartmentModal = true;
        this.selectedOption = 2; // Department
      } else if (type === 'group') {
        this.showGroupModal = true;
        this.selectedOption = 3; // Group
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
    clearSelection() {
      this.selectedDeptId = null;
      this.selectedGroupId = null;
      this.selectedOption = null;
      this.categoryName = '';
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
    },
    getCategoryName(option) {
      switch (option) {
        case 1: return '회사';
        case 2: return '부서';
        case 3: return '그룹';
        case 4: return '개인';
        default: return '';
      }
    },
    selectCategory(option) {
      this.selectedOption = option;
      this.returnSelection();
    }
  }
};
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
  width: 300px;
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

