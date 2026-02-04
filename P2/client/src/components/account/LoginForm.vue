<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-5 col-lg-4">
        <div class="text-center mb-4">
          <h2 class="fw-bold">Bienvenido</h2>
          <p class="text-muted small">Inicia sesión en Bnbria</p>
        </div>

        <div class="card border-0 shadow-sm p-4 rounded-4">
          <form @submit.prevent="handleLogin">
            <div class="mb-3">
              <label class="form-label small fw-bold text-secondary">EMAIL</label>
              <input
                v-model="email"
                type="email"
                class="form-control bg-light border-0 py-2"
                placeholder="correo@ejemplo.com"
                required
              />
            </div>

            <div class="mb-4">
              <label class="form-label small fw-bold text-secondary">CONTRASEÑA</label>
              <input
                v-model="password"
                type="password"
                class="form-control bg-light border-0 py-2"
                placeholder="Tu contraseña"
                required
              />
            </div>

            <div v-if="errorMessage" class="alert alert-danger py-2 small border-0">
              {{ errorMessage }}
            </div>

            <button type="submit" class="btn btn-black w-100 py-2 fw-bold rounded-pill mb-3">
              Iniciar Sesión
            </button>

            <div class="text-center small">
              ¿No tienes cuenta?
              <router-link to="/register" class="text-black fw-bold text-decoration-none"
                >Regístrate</router-link
              >
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import auth from "@/common/auth.js";

export default {
  name: "LoginForm",
  data() {
    return {
      email: "",
      password: "",
      errorMessage: null
    };
  },
  methods: {
    async handleLogin() {
      this.errorMessage = null;
      try {
        await auth.login({
          email: this.email,
          password: this.password
        });
        if (auth.isAdmin()) {
          this.$router.push("/admin");
        } else {
          this.$router.push("/");
        }
      } catch (error) {
        console.error(error);
        if (!error.response) {
          this.errorMessage = "No se puede conectar con el servidor.";
          return;
        }
        if (error.response.status === 401) {
          this.errorMessage = "Email o contraseña incorrectos.";
        } else {
          this.errorMessage = error.response.data?.message || "Ocurrió un error inesperado.";
        }
      }
    }
  }
};
</script>

<style scoped>
.btn-black {
  background-color: #000;
  color: #fff;
  transition:
    transform 0.2s,
    background-color 0.2s;
}
.btn-black:hover {
  background-color: #333;
  transform: translateY(-2px);
}
.form-control:focus {
  box-shadow: none;
  background-color: #fff;
  border: 1px solid #000 !important;
}
</style>
