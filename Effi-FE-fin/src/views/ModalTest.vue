<template>
    <div>
        <TagApp :schedule="schedule" @update-schedule="updateSchedule"/>
    </div>
</template>

<script>
import { ref } from 'vue';
import TagApp from '@/components/TagAdd.vue';
//TagAdd.vue 테스트
export default {
    components: {
        TagApp,
    },

    setup() {
        const schedule = {
            title: '오늘의 공부가 끝나면 운동하기',
            content: '권성지 장명훈과의 운동약속. 6시 개발이 끝나면 운동하러 가는것. 오늘은 등운동 하는날.',
            startDate: '2021-10-01',
            endDate: '2021-10-01',
            startTime: '10:00',
            endTime: '11:00',
            category: 'test',
            tags: [],
        };

        const updateSchedule = (tags) => {
            schedule.tags = tags;
        };

        return {
            schedule,
            updateSchedule,
        };
    },
}
</script>

<style >

</style>