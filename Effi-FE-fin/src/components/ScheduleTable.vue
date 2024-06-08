<template>
  <div>
    <table class="schedule-table">
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
      <tr v-for="item in schedule" :key="item.date">
        <td>{{ item.date }}</td>
        <td>
          <span :class="['category-dot', item.categoryColor]"></span>
          {{ item.category }}
        </td>
        <td>{{ item.status }}</td>
        <td>{{ item.task }}</td>
        <td>
          <span v-for="tag in item.tags" :key="tag" :class="['tag', tagColor(tag)]">{{ tag }}</span>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      schedule: [
        {
          date: '2024-05-16',
          category: '진행중',
          categoryColor: 'green',
          status: '화면 설계도 완성하기',
          task: '화면 설계도 완성하기',
          tags: ['#프로젝트', '#figma']
        },
        {
          date: '2024-05-16',
          category: '예정됨',
          categoryColor: 'green',
          status: '한화시스템 파이널 2차 멘토링',
          task: '한화시스템 파이널 2차 멘토링',
          tags: ['#프로젝트']
        },
        {
          date: '2024-05-17',
          category: '예정됨',
          categoryColor: 'green',
          status: '한화시스템 파이널 1차 산출물 제출',
          task: '한화시스템 파이널 1차 산출물 제출',
          tags: ['#프로젝트']
        },
        {
          date: '2024-05-17',
          category: '예정됨',
          categoryColor: 'blue',
          status: '채원이랑 사당역',
          task: '채원이랑 사당역',
          tags: ['#저녁약속']
        }
      ]
    };
  },
  methods: {
    tagColor(tag) {
      if (tag === '#프로젝트') return 'pink';
      if (tag === '#figma') return 'orange';
      if (tag === '#저녁약속') return 'blue';
      return '';
    }
  }
};
</script>

<style>
.schedule-table {
  width: 100%;
  border-collapse: collapse;
}
.schedule-table th, .schedule-table td {
  border: 1px solid #ddd;
  padding: 8px;
}
.schedule-table th {
  background-color: #f2f2f2;
  text-align: left;
}
.category-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 8px;
}
.green {
  background-color: green;
}
.blue {
  background-color: blue;
}
.tag {
  display: inline-block;
  margin-right: 4px;
  padding: 2px 4px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
}
.pink {
  background-color: pink;
}
.orange {
  background-color: orange;
}
.blue {
  background-color: blue;
}
</style>
