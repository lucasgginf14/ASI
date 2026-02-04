<template>
  <div class="container mt-5 mb-5">
    <div class="text-center mb-5 animate__animated animate__fadeIn">
      <h1 class="fw-bold display-6 mb-2">Encuentra tu próximo destino</h1>
      <p class="text-muted">Explora los mejores alojamientos de Bnbria</p>
    </div>

    <div class="container mt-5 mb-5">
      <SearchBar
        v-model:destination="searchForm.destination"
        v-model:check-in="searchForm.checkIn"
        v-model:check-out="searchForm.checkOut"
        v-model:guests="searchForm.guests"
        @search="handleSearch"
      />
    </div>
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else>
      <div v-if="topRated.length > 0" class="mb-5">
        <h4 class="fw-bold mb-4">⭐ Favoritos de los viajeros</h4>
        <div
          :class="[
            'row row-cols-1 row-cols-md-2 row-cols-lg-4 row-cols-xl-5 g-4',
            { 'justify-content-center': topRated.length >= 1 && topRated.length <= 5 }
          ]"
        >
          <div class="col" v-for="prop in topRated" :key="'rated-' + prop.id">
            <PublicPropertyCard :property="prop" />
          </div>
        </div>
      </div>

      <div v-if="topBooked.length > 0" class="mb-5">
        <h4 class="fw-bold mb-4">🔥 Tendencia ahora mismo</h4>
        <div
          :class="[
            'row row-cols-1 row-cols-md-2 row-cols-lg-4 row-cols-xl-5 g-4',
            { 'justify-content-center': topBooked.length >= 1 && topBooked.length <= 5 }
          ]"
        >
          <div class="col" v-for="prop in topBooked" :key="'booked-' + prop.id">
            <PublicPropertyCard :property="prop" />
          </div>
        </div>
      </div>

      <div
        v-if="topRated.length === 0 && topBooked.length === 0"
        class="text-center py-5 bg-light rounded-4"
      >
        <p class="text-muted mb-0">No hay propiedades destacadas disponibles.</p>
      </div>
    </div>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository";
import PublicPropertyCard from "./PublicPropertyCard.vue";

// 👇 CAMBIO 1: Importamos desde la carpeta correcta 'search'
import SearchBar from "@/components/search/SearchBar.vue";

export default {
  components: { SearchBar, PublicPropertyCard },
  data() {
    return {
      // 👇 CAMBIO 2: Datos para el formulario de búsqueda
      searchForm: {
        destination: "",
        checkIn: "",
        checkOut: "",
        guests: null
      },
      // Resto de datos intactos
      topRated: [],
      topBooked: [],
      loading: true
    };
  },
  async mounted() {
    // Lógica de carga intacta
    try {
      const [rated, booked] = await Promise.all([
        PropertyRepository.findTopRated(),
        PropertyRepository.findTopBooked()
      ]);
      this.topRated = rated ? rated.slice(0, 5) : [];
      this.topBooked = booked ? booked.slice(0, 5) : [];
    } catch (e) {
      console.error(e);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    handleSearch() {
      this.$router.push({
        path: "/search",
        query: {
          destino: this.searchForm.destination,
          llegada: this.searchForm.checkIn,
          salida: this.searchForm.checkOut,
          huespedes: this.searchForm.guests
        }
      });
    }
  }
};
</script>
