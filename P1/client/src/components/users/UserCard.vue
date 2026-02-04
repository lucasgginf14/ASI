<template>
  <div class="card-body">
    <h5 class="card-title text-primary" @click="onLoginClick(user.login)" style="cursor: pointer">
      {{ user.login }}
    </h5>
  </div>
</template>

<script>
export default {
  props: {
    user: {
      type: Object,
      required: true
    }
  },
  methods: {
    onLoginClick(login) {
      this.$router.push({ name: "NoteList", query: { login } });
      this.$emit("login-selected", login);
    }
  }
};
</script>
