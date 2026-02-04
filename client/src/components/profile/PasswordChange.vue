<template>
  <div class="container mt-5 mb-5" style="max-width: 500px">
    <h2 class="fw-bold text-center mb-4">Seguridad</h2>

    <div class="card border-0 shadow-sm rounded-4 p-4">
      <form @submit.prevent="handleSubmit">
        <div class="mb-3">
          <label class="form-label small fw-bold text-secondary">CONTRASEÑA ACTUAL</label>
          <input
            v-model="form.currentPassword"
            type="password"
            class="form-control bg-light border-0"
            required
          />
        </div>

        <div class="mb-3">
          <label class="form-label small fw-bold text-secondary">NUEVA CONTRASEÑA</label>
          <input
            v-model="form.newPassword"
            type="password"
            class="form-control bg-light border-0"
            required
          />
        </div>

        <div class="mb-4">
          <label class="form-label small fw-bold text-secondary">CONFIRMAR NUEVA</label>
          <input
            v-model="form.confirmPassword"
            type="password"
            class="form-control bg-light border-0"
            required
          />
        </div>

        <div v-if="errorMsg" class="alert alert-danger py-2 small border-0 mb-3">
          {{ errorMsg }}
        </div>

        <div class="d-flex gap-3">
          <button
            type="submit"
            class="btn btn-black w-100 py-2 fw-bold rounded-pill"
            :disabled="saving"
          >
            {{ saving ? "Guardando..." : "Actualizar" }}
          </button>
          <router-link to="/profile" class="btn btn-outline-dark w-100 py-2 fw-bold rounded-pill">
            Cancelar
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import UserRepository from "@/repositories/UserRepository.js";

export default {
  data() {
    return {
      form: { currentPassword: "", newPassword: "", confirmPassword: "" },
      errorMsg: null,
      saving: false
    };
  },
  methods: {
    async handleSubmit() {
      this.errorMsg = null;

      if (this.form.currentPassword === this.form.newPassword) {
        this.errorMsg = "La nueva contraseña debe ser diferente a la actual.";
        return;
      }

      // 1. Validaciones
      if (this.form.newPassword.length < 4) {
        this.errorMsg = "La nueva contraseña debe tener al menos 4 caracteres.";
        return;
      }

      if (this.form.newPassword !== this.form.confirmPassword) {
        this.errorMsg = "Las contraseñas no coinciden.";
        return;
      }

      this.saving = true;
      try {
        await UserRepository.updatePassword({
          currentPassword: this.form.currentPassword,
          newPassword: this.form.newPassword
        });

        // Redirección inmediata a la pantalla principal (Home)
        this.$router.push("/");
      } catch (error) {
        if (error.response?.status === 400) {
          this.errorMsg = "La contraseña actual no es correcta.";
        } else {
          this.errorMsg = "Ocurrió un error al cambiar la contraseña.";
        }
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.btn-black {
  background-color: #000;
  color: #fff;
}
.btn-black:hover {
  background-color: #333;
}
.form-control:focus {
  box-shadow: none;
  border: 1px solid #000 !important;
  background-color: #fff;
}
</style>
