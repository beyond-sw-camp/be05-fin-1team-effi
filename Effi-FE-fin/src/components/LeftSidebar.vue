<template>
  <div class="left-sidebar">
    <button @click="$emit('showModal')">그룹 생성</button>
    <b-row>
      <b-col md="auto">
        <b-calendar v-model="value" @context="onContext" locale="en-US"></b-calendar>
      </b-col>
    </b-row>
  </div>
</template>

<script>
export default {
  data() {
    return {
      value: '',
      context: null
    }
  },
  methods: {
    onContext(ctx) {
      this.context = ctx
    }
  }
}
</script>

<style scoped>
.left-sidebar {
  background-color: #FBB584;
  padding: 10px;
}

button {
  padding: 10px;
  margin: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
