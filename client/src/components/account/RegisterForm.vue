<template>
  <div class="container mt-5 mb-5">
    <div class="row justify-content-center">
      <div class="col-md-8 col-lg-6">
        <div class="text-center mb-4">
          <h2 class="fw-bold">Crear cuenta</h2>
          <p class="text-muted small">Únete a nuestra comunidad de viajeros</p>
        </div>

        <div class="card border-0 shadow-sm p-4 rounded-4">
          <form @submit.prevent="handleRegister" novalidate>
            <div class="row">
              <div class="col-12 mb-3">
                <label class="form-label small fw-bold text-secondary">NOMBRE *</label>
                <input
                  v-model="form.name"
                  type="text"
                  class="form-control bg-light border-0"
                  required
                />
                <div class="form-text text-danger small mt-1" v-if="submitted && !form.name">
                  Este campo es obligatorio.
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label small fw-bold text-secondary">1º APELLIDO *</label>
                <input
                  v-model="form.surname"
                  type="text"
                  class="form-control bg-light border-0"
                  required
                />
                <div class="form-text text-danger small mt-1" v-if="submitted && !form.surname">
                  Este campo es obligatorio.
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label small fw-bold text-secondary">2º APELLIDO</label>
                <input
                  v-model="form.surname_2"
                  type="text"
                  class="form-control bg-light border-0"
                />
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label small fw-bold text-secondary">EMAIL *</label>
              <input
                v-model="form.email"
                type="email"
                class="form-control bg-light border-0"
                placeholder="correo@ejemplo.com"
                required
              />
              <div class="form-text text-danger small mt-1" v-if="submitted && !form.email">
                Este campo es obligatorio.
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label small fw-bold text-secondary">TELÉFONO *</label>
                <input
                  v-model="form.phone"
                  type="tel"
                  class="form-control bg-light border-0"
                  required
                />
                <div class="form-text text-danger small mt-1" v-if="submitted && !form.phone">
                  Este campo es obligatorio.
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label small fw-bold text-secondary">FECHA NACIMIENTO *</label>
                <input
                  v-model="form.birthday"
                  type="date"
                  class="form-control bg-light border-0"
                  :max="maxBirthDate"
                  required
                />
                <div class="form-text text-danger small mt-1" v-if="submitted && !form.birthday">
                  Este campo es obligatorio.
                </div>
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label small fw-bold text-secondary">CONTRASEÑA *</label>
              <input
                v-model="form.password"
                type="password"
                class="form-control bg-light border-0"
                required
              />
              <div class="form-text text-danger small mt-1" v-if="submitted && !form.password">
                Este campo es obligatorio.
              </div>
            </div>
            <div class="mb-4">
              <label class="form-label small fw-bold text-secondary">CONFIRMAR CONTRASEÑA *</label>
              <input
                v-model="form.confirmPassword"
                type="password"
                class="form-control bg-light border-0"
                required
              />
              <div
                class="form-text text-danger small mt-1"
                v-if="submitted && !form.confirmPassword"
              >
                Este campo es obligatorio.
              </div>
            </div>

            <div v-if="errorMessage" class="alert alert-danger py-2 small border-0">
              {{ errorMessage }}
            </div>

            <button type="submit" class="btn btn-black w-100 py-2 fw-bold rounded-pill mb-3">
              Registrarse
            </button>

            <div class="text-center small">
              ¿Ya tienes cuenta?
              <router-link to="/login" class="text-black fw-bold text-decoration-none"
                >Inicia sesión</router-link
              >
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AccountRepository from "@/repositories/AccountRepository";
import auth from "@/common/auth.js";

export default {
  name: "RegisterForm",
  data() {
    return {
      form: {
        name: "",
        surname: "",
        surname_2: "",
        email: "",
        phone: "",
        birthday: null,
        password: "",
        confirmPassword: ""
      },
      errorMessage: null,
      submitted: false
    };
  },
  computed: {
    maxBirthDate() {
      const today = new Date();
      const year = today.getFullYear() - 18;
      const month = String(today.getMonth() + 1).padStart(2, "0");
      const day = String(today.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    isAdult() {
      if (!this.form.birthday) return true;
      return this.form.birthday <= this.maxBirthDate;
    }
  },
  methods: {
    async handleRegister() {
      this.errorMessage = null;

      if (
        !this.form.name ||
        !this.form.surname ||
        !this.form.email ||
        !this.form.phone ||
        !this.form.birthday ||
        !this.form.password ||
        !this.form.confirmPassword
      ) {
        this.submitted = true;
        return;
      }

      if (this.form.password !== this.form.confirmPassword) {
        this.errorMessage = "Las contraseñas no coinciden.";
        return;
      }

      if (this.form.password.length < 4) {
        this.errorMessage = "La contraseña es muy corta (mínimo 4 caracteres).";
        return;
      }

      if (!this.isAdult) {
        this.errorMessage = "Debes ser mayor de 18 años para registrarte.";
        return;
      }

      if (this.form.birthday) {
        const year = parseInt(this.form.birthday.split("-")[0], 10);
        if (Number.isNaN(year) || year <= 1900) {
          this.errorMessage = "Introduce una fecha de nacimiento válida (año posterior a 1900).";
          return;
        }
      }

      try {
        await AccountRepository.registerAccount(this.form);

        await auth.login({
          email: this.form.email,
          password: this.form.password
        });

        this.$router.push("/");
      } catch (error) {
        console.error(error);
        const msg = error.response?.data?.message?.toString() || "";

        if (msg.includes("User email") && msg.includes("already exists")) {
          this.errorMessage = "El email introducido ya está registrado.";
        } else if (
          msg.includes("the_user_phone_key") ||
          (msg.toLowerCase().includes("phone") &&
            (msg.includes("already exists") ||
              msg.includes("Ya existe") ||
              msg.includes("llave duplicada")))
        ) {
          this.errorMessage = "El teléfono introducido ya está registrado.";
        } else {
          this.errorMessage = "Ocurrió un error al crear la cuenta. Inténtalo de nuevo.";
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
  transition: transform 0.2s;
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
