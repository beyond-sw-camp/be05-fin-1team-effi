<template>
  <v-list>
    <v-list-item v-for="group in groups" :key="group.id">
      <template v-slot:prepend>
        <v-list-item-action start>
          <v-checkbox-btn :model-value="group.selected" @click.stop="toggleGroupSelection(group)"></v-checkbox-btn>
        </v-list-item-action>
      </template>
      <div class="group-container">
        <v-list-item-title @click="navigateToParticipants(group.id)">
          {{ group.name }}
        </v-list-item-title>
        <v-list-item-action>
          <v-icon @click.stop="navigateToGroupSchedules(group.id)">mdi-calendar</v-icon>
        </v-list-item-action>
        <v-list-item-action>
          <v-icon @click.stop="removeGroup(group.id)" class="remove-group-button">mdi-close</v-icon>
        </v-list-item-action>
      </div>
    </v-list-item>
  </v-list>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axiosInstance from "@/services/axios.js";

export default {
  name: 'GroupNameList',
  emits: ['selectedGroups', 'update-groupsParticipants', 'update-groupsSchedules'],
  setup(props, { emit }) {
    const groups = ref([]);
    const router = useRouter();

    const toggleGroupSelection = (group) => {
      group.selected = !group.selected;
    };

    const removeGroup = async (groupId) => {
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
        await axiosInstance.delete(`/api/groups/${groupId}/employees`, config);
        console.log(`Group with id ${groupId} has been removed.`);
        fetchGroups();
      } catch (error) {
        console.error(`Error removing group with id ${groupId}:`, error);
        if (error.response) {
          console.error('Error details:', error.response.data);
        }
      }
    };

    const fetchGroups = async () => {
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
        const response = await axiosInstance.get(`/api/groups/find/myGroup`, config);
        const data = Array.isArray(response.data) ? response.data : response.data.data;
        if (Array.isArray(data)) {
          groups.value = data.map(group => ({
            id: group.groupId,
            name: group.groupName,
            selected: false
          }));
        } else {
          console.error('Expected an array but got:', data);
        }
      } catch (error) {
        console.error('Error fetching groups:', error);
        if (error.response) {
          console.error('Error details:', error.response.data);
        }
      }
    };

    const navigateToGroupSchedules = (groupId) => {
      router.push({ name: 'groupschedules', query: { groupId } });
      emit('update-groupsSchedules', groupId);
    };

    const navigateToParticipants = (groupId) => {
      router.push({ name: 'groupparticipants', query: { groupId } });
      emit('update-groupsParticipants', groupId);
    };

    onMounted(() => {
      fetchGroups();
    });

    watch(groups, (newGroups) => {
      const selectedGroupIds = newGroups.filter(group => group.selected).map(group => group.id);
      emit('selectedGroups', selectedGroupIds);
    }, { deep: true });

    return {
      groups,
      toggleGroupSelection,
      removeGroup,
      fetchGroups,
      navigateToGroupSchedules,
      navigateToParticipants
    };
  }
};
</script>

<style scoped>
.group-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  border-radius: 5px;
  width: 100%;
}

.group-list {
  margin-top: 10px;
  max-height: 200px;
  overflow-y: auto;
}

.group-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.group-item:hover {
  background-color: #e0e0e0;
}

.group-item input[type="checkbox"] {
  margin-right: 10px;
}

.group-item label {
  flex-grow: 1;
  cursor: pointer;
}

.remove-group-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: red;
  padding: 5px;
}
</style>
