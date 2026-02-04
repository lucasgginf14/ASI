<template>
  <div class="container mt-5 mb-5" style="max-width: 600px">
    <h2 class="fw-bold text-center mb-4">Analizar Rendimiento</h2>

    <form @submit.prevent="handleSubmit" class="card border-0 shadow rounded-4 p-4">
      <div class="mb-3">
        <label class="form-label small fw-bold text-secondary">CIUDADES</label>
        <input
          v-model="form.cities"
          type="text"
          class="form-control bg-light border-0"
          placeholder="Madrid, Barcelona, Valencia"
        />
        <small class="text-muted">Separa las ciudades con comas (vacío = todas)</small>
      </div>

      <div class="mb-4">
        <label class="form-label small fw-bold text-secondary">AÑO</label>
        <input
          v-model.number="form.year"
          type="number"
          class="form-control bg-light border-0"
          min="2020"
          max="2030"
          placeholder="Todos los años"
        />
        <small class="text-muted">Vacío = todos los años</small>
      </div>

      <button type="submit" class="btn btn-black w-100 py-2 rounded-pill fw-bold">
        Buscar
      </button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        cities: "",
        year: null
      }
    };
  },
  methods: {
    handleSubmit() {
      const query = {};

      if (this.form.cities.trim()) {
        // Limpiamos espacios alrededor de las comas
        const cities = this.form.cities.split(",").map(c => c.trim()).filter(c => c);
        query.cities = cities.join(",");
      }

      if (this.form.year) {
        query.year = this.form.year;
      }

      this.$router.push({
        path: "/properties/stats/results",
        query
      });
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
  border: 1px solid #000 !important;
  background-color: #fff;
}
</style>
