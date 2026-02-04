<template>
  <div>
    <form @submit.prevent="registrarme">
      <div class="mb-3 mt-3">
        <label for="login">Login: </label>
        <input type="text" id="login" v-model="login" />
      </div>
      <div class="mb-3">
        <label for="pass">Password: </label>
        <input type="password" id="password" v-model="password" />
      </div>
      <div class="mb-3">
        <label for="pass">Confirm password: </label>
        <input type="password" id="confirmPassword" v-model="confirmPassword" />
      </div>
      <button type="submit">Registrarme</button>
    </form>
    <br />
    <div>
      <button @click="volver()">Volver</button>
    </div>
  </div>
</template>

<script>
import auth from "../common/auth.js";

export default {
  data() {
    return {
      login: null,
      password: null,
      confirmPassword: null
    };
  },
  methods: {
    async registrarme() {
      try {
        await auth.register(
          {
            login: this.login,
            password: this.password
          },
          this.confirmPassword
        );
        await auth.login({
          login: this.login,
          password: this.password
        });
        this.$router.push("/notes");
      } catch (e) {
        console.error(e);
        if (e.response?.data?.message) {
          alert(e.response.data.message);
        } else {
          alert(e.message);
        }
      }
    },
    volver() {
      this.$router.push("/");
    }
  }
};
</script>
