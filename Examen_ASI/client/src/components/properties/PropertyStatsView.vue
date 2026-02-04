<template>
  <div class="container mt-5 mb-5">
    <h2 class="fw-bold text-center mb-4">Resultados del Análisis</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else-if="errorMessage" class="alert alert-danger border-0 rounded-4 text-center">
      {{ errorMessage }}
    </div>

    <div v-else-if="stats.length === 0" class="text-center py-5 bg-light rounded-4">
      <p class="text-muted mb-0">No se encontraron resultados.</p>
    </div>

    <div v-else class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="(item, index) in sortedStats" :key="index">
        <PropertyStatsCard :stat="item" />
      </div>
    </div>

    <div class="text-center mt-5">
      <router-link to="/properties/stats" class="btn btn-outline-dark rounded-pill px-4">
        Nueva búsqueda
      </router-link>
    </div>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository";
import PropertyStatsCard from "./PropertyStatsCard.vue";

export default {
  components: { PropertyStatsCard },
  data() {
    return {
      stats: [],
      loading: true,
      errorMessage: null
    };
  },
  computed: {
    sortedStats() {
      return [...this.stats].sort((a, b) => {
        if (b.totalBookings !== a.totalBookings) {
          return b.totalBookings - a.totalBookings;
        }
        return b.totalIncome - a.totalIncome;
      });
    }
  },
  async mounted() {
    try {
      const cities = this.$route.query.cities;
      const year = this.$route.query.year;

      const citiesArray = cities ? cities.split(",") : null;
      const yearValue = year ? parseInt(year) : null;

      this.stats = await PropertyRepository.findStats(citiesArray, yearValue);
    } catch {
      this.errorMessage = "Error al cargar los datos.";
    } finally {
      this.loading = false;
    }
  }
};
</script>
