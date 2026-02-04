<template>
  <div class="container mt-5 mb-5" style="max-width: 600px">
    <h2 class="fw-bold text-center mb-4">Editar Perfil</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else class="card border-0 shadow-sm rounded-4 p-4">
      <form @submit.prevent="handleSubmit">
        <div class="mb-3">
          <label class="form-label small fw-bold text-secondary">EMAIL</label>
          <div class="form-control bg-light border-0">
            <span class="text-secondary small">{{ form.email }}</span>
          </div>
          <div class="form-text text-xs">El email no se puede modificar.</div>
        </div>

        <div class="row mb-3">
          <div class="col-12 mb-3">
            <label class="form-label small fw-bold text-secondary">NOMBRE *</label>
            <input
              v-model="form.name"
              type="text"
              class="form-control bg-light border-0"
              required
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label small fw-bold text-secondary">1º APELLIDO *</label>
            <input
              v-model="form.surname"
              type="text"
              class="form-control bg-light border-0"
              required
            />
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label small fw-bold text-secondary">2º APELLIDO</label>
            <input v-model="form.surname2" type="text" class="form-control bg-light border-0" />
          </div>
        </div>

        <div class="row mb-4">
          <div class="col-md-6 mb-3">
            <label class="form-label small fw-bold text-secondary">TELÉFONO *</label>
            <input
              v-model="form.phone"
              type="tel"
              class="form-control bg-light border-0"
              required
            />
          </div>

          <div class="col-md-6 mb-3">
            <label class="form-label small fw-bold text-secondary">NACIMIENTO</label>
            <div class="form-control bg-light border-0">
              <span class="text-secondary small">{{ formatDate(form.birthday) }}</span>
            </div>
            <div class="form-text text-xs">La fecha de nacimiento no se puede modificar.</div>
          </div>
        </div>

        <div v-if="errorMessage" class="alert alert-danger py-2 small border-0 mb-3">
          {{ errorMessage }}
        </div>

        <div class="d-flex gap-3">
          <button
            type="submit"
            class="btn btn-black w-100 py-2 fw-bold rounded-pill"
            :disabled="saving"
          >
            {{ saving ? "Guardando..." : "Guardar cambios" }}
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
      form: { email: "", name: "", surname: "", surname2: "", phone: "", birthday: "" },
      loading: true,
      saving: false,
      errorMessage: null
    };
  },
  async mounted() {
    try {
      const user = await UserRepository.getMe();
      this.form.email = user.email;
      this.form.name = user.name;
      this.form.surname = user.surname;
      this.form.surname2 = user.surname2 || "";
      this.form.phone = user.phone || "";
      this.form.birthday = user.birthday ? user.birthday.split("T")[0] : "";
    } catch (error) {
      console.error(error);
      this.errorMessage = "No se pudieron cargar los datos del perfil.";
    } finally {
      this.loading = false;
    }
  },
  methods: {
    formatDate(date) {
      if (!date) return "-";
      return new Date(date).toLocaleDateString("es-ES", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
      });
    },
    async handleSubmit() {
      console.log("Valor de surname2 antes de enviar:", this.form.surname2);
      this.saving = true;
      this.errorMessage = null;

      try {
        await UserRepository.updateMe({
          name: this.form.name,
          surname: this.form.surname,
          surname2: this.form.surname2 || null,
          phone: this.form.phone,
          birthday: this.form.birthday
        });
        this.$router.push("/profile");
      } catch (error) {
        console.error(error);
        const msg = error.response?.data?.message?.toString() || "";

        if (
          msg.includes("the_user_phone_key") ||
          (msg.toLowerCase().includes("phone") &&
            (msg.includes("already exists") ||
              msg.includes("Ya existe") ||
              msg.includes("llave duplicada")))
        ) {
          this.errorMessage = "El teléfono introducido ya está registrado.";
        } else {
          this.errorMessage = "Error al actualizar el perfil. Inténtalo de nuevo.";
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
.text-xs {
  font-size: 0.75rem;
  color: #666;
}
</style>
