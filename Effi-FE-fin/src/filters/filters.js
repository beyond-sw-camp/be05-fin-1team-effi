// src/filters/filters.js

// 스케줄을 시작 날짜별로 정렬하는 함수
export function sortSchedulesByStartDate(schedules) {
  return schedules.sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
}

// 주 단위로 스케줄을 필터링하는 함수
export function filterSchedulesByWeek(schedules, currentWeek) {
  const startOfWeek = new Date(currentWeek);
  startOfWeek.setHours(0, 0, 0, 0);
  const endOfWeek = new Date(startOfWeek);
  endOfWeek.setDate(endOfWeek.getDate() + 7);

  return schedules.filter(schedule => {
    const scheduleDate = new Date(schedule.startTime);
    return scheduleDate >= startOfWeek && scheduleDate < endOfWeek;
  });
}

// 스케줄을 상태별로 정렬하는 함수
export function sortSchedulesByStatus(schedules) {
  const statusOrder = {
    '예정됨': 1,
    '진행중': 2,
    '완료됨': 3
  };
  return schedules.sort((a, b) => statusOrder[a.status] - statusOrder[b.status]);
}
