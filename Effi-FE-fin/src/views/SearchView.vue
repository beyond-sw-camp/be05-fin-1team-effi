<!-- src/views/SearchView.vue -->
<template>
  <div>
    <HomeHeader @search-results="updateSearches" />
    <SearchList :searches="searches" />
  </div>
</template>

<script>
import HomeHeader from '../components/HomeHeader.vue';
import SearchList from '../components/SearchList.vue';
import { sortSchedulesByStartDate } from '../filters/filters';

export default {
  components: {
    HomeHeader,
    SearchList
  },
  data() {
    return {
      searches: []
    };
  },
  methods: {
    updateSearches(searches) {
      this.searches = sortSchedulesByStartDate(searches);
    }
  }
};
</script>
