<template>
  <div>
    <div v-for="search in searches" :key="search.scheduleId">
      <SearchItem :search="search" />
    </div>
  </div>
</template>

<script>
import SearchItem from './SearchItem.vue';

export default {
  props: ['searches'],
  components: {
    SearchItem
  }
};
</script>
