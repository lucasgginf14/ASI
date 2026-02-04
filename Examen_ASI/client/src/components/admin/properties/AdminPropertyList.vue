<template>
  <div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold">Gestión de Propiedades</h2>

      <div class="search-bar shadow-sm">
        <i class="bi bi-search search-icon"></i>
        <input v-model="searchId" type="text" placeholder="Buscar por id de propiedad" />
      </div>
    </div>

    <div class="d-flex gap-2 mb-4 overflow-auto pb-2">
      <button
        @click="filter = 'approved'"
        class="btn rounded-pill px-4 fw-bold"
        :class="filter === 'approved' ? 'btn-success text-white' : 'btn-light'"
      >
        ✅ Aprobadas
      </button>
      <button
        @click="filter = 'pending'"
        class="btn rounded-pill px-4 fw-bold"
        :class="filter === 'pending' ? 'btn-warning' : 'btn-light'"
      >
        ⏳ Pendientes
      </button>

      <button
        @click="filter = 'rejected'"
        class="btn rounded-pill px-4 fw-bold"
        :class="filter === 'rejected' ? 'btn-danger text-white' : 'btn-light'"
      >
        ❌ Rechazadas
      </button>
      <button
        @click="filter = 'all'"
        class="btn rounded-pill px-4 fw-bold"
        :class="filter === 'all' ? 'btn-dark' : 'btn-light'"
      >
        Todas
      </button>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div
      v-else-if="filteredProperties.length > 0"
      class="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4"
    >
      <div class="col" v-for="prop in filteredProperties" :key="prop.id">
        <AdminPropertyCard :property="prop" />
      </div>
    </div>

    <div v-else class="text-center py-5 bg-light rounded-4">
      <p class="text-muted mb-0">No se encontraron propiedades en esta categoría.</p>
    </div>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository";
import AdminPropertyCard from "./AdminPropertyCard.vue";

export default {
  components: { AdminPropertyCard },
  data() {
    return {
      properties: [],
      filter: "pending",
      searchId: "",
      loading: true
    };
  },
  computed: {
    filteredProperties() {
      let base = this.properties;
      if (this.filter === "pending") base = base.filter((p) => p.state === "PENDING");
      else if (this.filter === "approved") base = base.filter((p) => p.state === "APPROVED");
      else if (this.filter === "rejected") base = base.filter((p) => p.state === "REJECTED");
      // 'all' deja base sin cambios

      const idQuery = this.searchId && this.searchId.toString().trim();
      if (idQuery) {
        return base.filter((p) => p.id !== undefined && p.id.toString().includes(idQuery));
      }
      return base;
    }
  },
  async mounted() {
    await this.loadProperties();
  },
  methods: {
    async loadProperties() {
      this.loading = true;
      try {
        this.properties = await PropertyRepository.getAllPropertiesAdmin();
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 50px; /* Forma de píldora */
  padding: 10px 20px;
  width: 100%;
  max-width: 320px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.search-bar:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
}

.search-bar:focus-within {
  border-color: #222; /* Borde oscuro al escribir */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12) !important;
  transform: translateY(-1px);
}

.search-icon {
  font-size: 1.1rem;
  color: #666;
  margin-right: 12px;
}

.search-bar input {
  border: none;
  outline: none;
  width: 100%;
  font-size: 0.95rem;
  color: #333;
  background: transparent;
}

.search-bar input::placeholder {
  color: #999;
}
</style>
