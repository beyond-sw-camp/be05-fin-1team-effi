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
            title: '',
            content: '',
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