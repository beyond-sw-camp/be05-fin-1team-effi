// src/filters/filters.js

// 스케줄을 시작 날짜별로 정렬하는 함수
export function sortSchedulesByStartDate(schedules) {
  return schedules.sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
}
