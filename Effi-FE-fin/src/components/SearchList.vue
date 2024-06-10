<template>
  <div>
    <table v-if="!grouped">
      <thead>
        <tr>
          <th>날짜</th>
          <th>카테고리</th>
          <th>상태</th>
          <th>일정</th>
          <th>태그</th>
        </tr>
      </thead>
      <tbody>
        <SearchItem v-for="search in searches" :key="search.scheduleId" :search="search" />
      </tbody>
    </table>
    <div v-else>
      <div v-if="grouped['예정됨'].length">
        <h2>예정됨</h2>
        <table>
          <tbody>
            <SearchItem v-for="search in grouped['예정됨']" :key="search.scheduleId" :search="search" />
          </tbody>
        </table>
      </div>
      <div v-if="grouped['진행중'].length">
        <h2>진행중</h2>
        <table>
          <tbody>
            <SearchItem v-for="search in grouped['진행중']" :key="search.scheduleId" :search="search" />
          </tbody>
        </table>
      </div>
      <div v-if="grouped['완료됨'].length">
        <h2>완료됨</h2>
        <table>
          <tbody>
            <SearchItem v-for="search in grouped['완료됨']" :key="search.scheduleId" :search="search" />
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import SearchItem from './SearchItem.vue';

export default {
  props: ['searches', 'grouped'],
  components: {
    SearchItem
  }
};
</script>
