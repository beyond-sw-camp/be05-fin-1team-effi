<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-container">
      <button class="close-button" @click="$emit('close')">×</button>
      <div class="align-center">
        <h2>새 그룹 만들기</h2>
        <p>그룹장 : {{ groupLeader }}</p>
      </div>
      <div class="input-group">
        <input type="text" placeholder="그룹 이름을 입력하세요" v-model="groupName" class="group-input" />
      </div>
      <div class="input-group">
        <input type="text" placeholder="추가할 사원을 검색하세요" v-model="searchQuery" class="group-input" />
        <button class="search-button" @click="searchEmployees">🔍</button>
      </div>
      <ul v-if="searchResults.length" class="search-results">
        <li v-for="employee in searchResults" :key="employee.id" class="search-result-item" @click="selectEmployee(employee)">
          {{ employee.name }}/{{ employee.deptName }}/{{ employee.rank }}
        </li>
      </ul>
      <div v-if="selectedEmployees.length" class="selected-employees">
        <p>선택된 사원:</p>
        <ul>
          <li v-for="employee in selectedEmployees" :key="employee.empNo">
            {{ employee.name }}/{{ employee.deptName }}/{{ employee.rank }}
            <button @click="removeEmployee(employee.empNo)" class="remove-button">×</button>
          </li>
        </ul>
      </div>
      <button @click="createGroup" class="create-button">그룹 생성</button>
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/services/axios';
import { useRouter } from 'vue-router';

export default {
  props: {
    show: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      groupLeader: '',
      groupName: '',
      searchQuery: '',
      searchResults: [],
      selectedEmployees: []
    };
  },
  created() {
    this.fetchGroupLeaderName();
  },
  setup() {
    const router = useRouter();
    return { router };
  },
  methods: {
    async createGroup() {
      const token = sessionStorage.getItem('accessToken');
      if (!token) {
        console.error('No token found');
        return;
      }
      const config = {
        headers: {
          Authorization: `Bearer ${token}`
        }
      };
      
      const myEmpNo = Number(sessionStorage.getItem('empNo'));
      const firstEmpNos = this.selectedEmployees.map(emp => emp.empNo);
      const empNos = firstEmpNos.filter(empNo => empNo !== myEmpNo);

      // 데이터 확인용 콘솔 로그
      console.log('Creating group with data:', {
        groupName: this.groupName,
        empNos: empNos
      });

      if (empNos.length === 0) {
        console.error('No employees selected');
        return;
      }

      try {
        const response = await axiosInstance.post('http://localhost:8080/api/groups', {
          groupName: this.groupName,
          employeeIds: empNos
        }, config);
        
        const groupId = response.data.data.groupId; // groupId를 제대로 받아옴
        console.log('그룹 생성 성공:', response.data);

        // 그룹 생성이 성공하면 모달을 닫고 홈으로 이동
        this.$emit('close');
        this.router.push('/');

        // 이메일 전송 시도
        try {
          const emailResponse = await axiosInstance.post(`http://localhost:8080/api/auth/send/group/add/${groupId}`, {
            groupId: groupId
          }, config);
          console.log('그룹 생성 메일 전송 성공:', emailResponse.data);
        } catch (emailError) {
          console.error('그룹 생성 메일 전송 오류:', emailError.response ? emailError.response.data : emailError.message);
        }
      } catch (error) {
        console.error('그룹 생성 오류:', error.response ? error.response.data : error.message);
      }
    },

    fetchGroupLeaderName() {
      const name = sessionStorage.getItem('name');
      if (name) {
        this.groupLeader = name;
      } else {
        console.error('No name found in session storage');
      }
    },
    async searchEmployees() {
      const token = sessionStorage.getItem('accessToken');
      if (!token) {
        console.error('No token found');
        return;
      }
      const config = {
        headers: {
          Authorization: `Bearer ${token}`
        }
      };
      try {
        const response = await axiosInstance.get(`/api/groups/search?name=${this.searchQuery}`, config);
        const employees = response.data;
        for (let employee of employees) {
          const deptResponse = await axiosInstance.get(`/api/search/dept/${employee.deptId}`, config);
          employee.deptName = deptResponse.data;
        }
        this.searchResults = employees;
      } catch (error) {
        console.error('Error searching employees:', error.response ? error.response.data : error.message);
      }
    },
    selectEmployee(employee) {
      if (!this.selectedEmployees.some(emp => emp.empNo === employee.empNo)) {
        this.selectedEmployees.push(employee);
        console.log('Selected employees:', this.selectedEmployees); // 선택된 사원 목록 로그 출력
      }
    },
    removeEmployee(empNo) {
      this.selectedEmployees = this.selectedEmployees.filter(emp => emp.empNo !== empNo);
      console.log('Selected employees after removal:', this.selectedEmployees); // 제거 후 선택된 사원 목록 로그 출력
    }
  }
};
</script>

<style scoped>
.align-center {
  text-align: center;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10001;
}

.modal-container {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: absolute;
  width: 300px;
  z-index: 10001;
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

.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.group-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
}

.search-button {
  padding-left: 5px;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  margin-left: 5px;
}

.create-button {
  width: 100%;
  background-color: #ffcc99;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.search-results {
  list-style: none;
  padding: 0;
  margin: 10px 0;
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.search-result-item {
  padding: 10px;
  border-bottom: 1px solid #ccc;
  cursor: pointer;
}

.search-result-item:last-child {
  border-bottom: none;
}

.selected-employees {
  margin: 10px 0;
}

.selected-employees ul {
  list-style: none;
  padding: 0;
}

.selected-employees li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 0;
}

.remove-button {
  background: none;
  border: none;
  font-size: 14px;
  cursor: pointer;
  color: red;
}
</style>
