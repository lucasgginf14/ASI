<template>
  <div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold">Mis Propiedades</h2>
      <router-link to="/properties/create" class="btn btn-black rounded-pill px-4"
      >+ Crear Nueva</router-link
      >
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else-if="errorMessage" class="alert alert-danger border-0 rounded-4 text-center">
      {{ errorMessage }}
    </div>

    <div v-else-if="properties.length === 0" class="text-center py-5 bg-light rounded-4">
      <p class="text-muted mb-0">No tienes ninguna propiedad publicada. </p>
    </div>

    <div v-else class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="prop in properties" :key="prop.id">
        <PropertyCard :property="prop" @delete="handleDelete" />
      </div>
    </div>

    <div class="text-center mt-5">
      <router-link to="/properties/stats" class="btn btn-black rounded-pill px-4">
        Analizar Rendimiento
      </router-link>
    </div>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository";
import PropertyCard from "./PropertyCard.vue";

export default {
  components: { PropertyCard },
  data() {
    return {
      properties:  [],
      loading: true,
      errorMessage: null
    };
  },
  async mounted() {
    try {
      this.properties = await PropertyRepository.getMyProperties();
    } catch {
      this.errorMessage = "No se pudieron cargar tus propiedades.";
    } finally {
      this.loading = false;
    }
  },
  methods: {
    async handleDelete(id) {
      if (
        !confirm("¿Seguro que quieres eliminar esta propiedad? Esta acción no se puede deshacer.")
      )
        return;

      this.errorMessage = null;
      try {
        await PropertyRepository.delete(id);

        this.properties = this.properties.filter((p) => p.id !== id);
      } catch (e) {
        this.errorMessage = e.response?.data?.message || "Error al eliminar la propiedad.";
      }
    }
  }
};
</script>

<style scoped>
.btn-black {
  background: #000;
  color: #fff;
  transition: transform 0.2s;
}
.btn-black:hover {
  background:  #333;
  transform: translateY(-2px);
}
</style>
