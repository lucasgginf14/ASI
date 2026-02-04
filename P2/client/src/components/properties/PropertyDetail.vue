<template>
  <div class="container mt-4 mb-5">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else-if="errorMessage" class="alert alert-danger text-center rounded-4">
      {{ errorMessage }}
      <div class="mt-2">
        <router-link to="/my-properties" class="btn btn-sm btn-outline-danger">Volver</router-link>
      </div>
    </div>

    <div v-else-if="property">
      <div class="d-flex justify-content-between align-items-start mb-3">
        <div>
          <h2 class="fw-bold mb-1">{{ property.title }}</h2>
          <span class="badge" :class="property.approved ? 'bg-success' : 'bg-warning text-dark'">
            {{ property.approved ? "Publicada" : "Revisión Pendiente" }}
          </span>
        </div>
        <div>
          <router-link
            :to="`/my-properties/${property.id}/edit`"
            class="btn btn-outline-dark rounded-pill px-4 fw-bold"
          >
            Editar
          </router-link>
        </div>
      </div>

      <div class="row g-3 mb-4" style="height: 400px">
        <div class="col-md-8 h-100">
          <img
            :src="imageUrl"
            class="w-100 h-100 rounded-4 object-fit-cover bg-light"
            alt="Imagen principal de la propiedad"
            @error="setPlaceholder"
          />
        </div>
        <div class="col-md-4 h-100">
          <div class="h-100 rounded-4 overflow-hidden bg-light position-relative border">
            <iframe
              v-if="mapUrl"
              :src="mapUrl"
              class="w-100 h-100 border-0"
              title="Mapa de ubicación"
            ></iframe>
            <div
              v-else
              class="d-flex align-items-center justify-content-center h-100 text-muted small"
            >
              <i class="bi bi-geo-alt me-2"></i> Ubicación no disponible en el mapa
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-lg-8">
          <div class="mb-5">
            <h4 class="fw-bold">Descripción</h4>
            <p class="text-secondary" style="white-space: pre-line">{{ property.description }}</p>

            <div class="d-flex gap-4 mt-4 py-3 border-top border-bottom text-center">
              <div>
                <span class="d-block fw-bold fs-5">{{ property.bedrooms }}</span>
                <small class="text-muted">Habitaciones</small>
              </div>
              <div>
                <span class="d-block fw-bold fs-5">{{ property.bathrooms }}</span>
                <small class="text-muted">Baños</small>
              </div>
              <div>
                <span class="d-block fw-bold fs-5">{{ property.maxOccupancy }}</span>
                <small class="text-muted">Personas</small>
              </div>
              <div>
                <span class="d-block fw-bold fs-5">{{ property.squareMeters }}</span>
                <small class="text-muted">m²</small>
              </div>
            </div>
          </div>

          <AvailabilityManager
            :property-id="property.id"
            :availabilities="property.availabilities"
            :is-approved="property.approved"
            @updated="reload"
          />

          <div v-if="property.reviews && property.reviews.length > 0" class="mt-5 pt-4 border-top">
            <div class="d-flex align-items-center justify-content-between mb-3">
              <h4 class="fw-bold mb-0">Reseñas de huéspedes</h4>
              <div class="d-flex align-items-center gap-1 text-warning fw-bold">
                <span
                  >★ {{ property.averageRating ? property.averageRating.toFixed(2) : "New" }}</span
                >
                <span class="text-muted small fw-normal">({{ property.reviews.length }})</span>
              </div>
            </div>

            <div class="d-flex flex-column gap-3">
              <div
                v-for="(review, index) in property.reviews.slice(0, 2)"
                :key="index"
                class="p-3 bg-light rounded-3"
              >
                <div class="d-flex justify-content-between mb-1">
                  <span class="fw-bold">{{ review.reviewer?.name || "Usuario" }}</span>
                  <small class="text-muted">{{ formatDate(review.creationDate) }}</small>
                </div>
                <p class="mb-0 text-secondary small">{{ review.description }}</p>
              </div>
            </div>

            <button
              v-if="property.reviews.length > 2"
              @click="showAllReviews = true"
              class="btn btn-outline-dark rounded-pill mt-3 btn-sm fw-bold px-4"
            >
              Mostrar las {{ property.reviews.length }} reseñas
            </button>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="card border-0 shadow-sm p-4 rounded-4 bg-light">
            <h5 class="fw-bold mb-3">Dirección</h5>
            <p class="mb-1 fw-bold">{{ property.street }}, {{ property.number }}</p>
            <p class="mb-1">{{ property.postalCode }} {{ property.city }}</p>
            <p class="text-muted mb-0">{{ property.province }}, {{ property.country }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showAllReviews" class="modal-overlay" @click.self="showAllReviews = false">
      <div class="modal-content-custom animate__animated animate__fadeInUp">
        <div
          class="modal-header border-bottom p-3 d-flex justify-content-between align-items-center sticky-top bg-white"
        >
          <h5 class="fw-bold mb-0">Todas las reseñas</h5>
          <button @click="showAllReviews = false" class="btn-close"></button>
        </div>
        <div class="modal-body p-4" style="max-height: 70vh; overflow-y: auto">
          <div
            v-for="(review, index) in property.reviews"
            :key="'modal-' + index"
            class="mb-4 pb-3 border-bottom"
          >
            <div class="d-flex justify-content-between mb-2">
              <h6 class="fw-bold mb-0">{{ review.reviewer?.name || "Usuario" }}</h6>
              <small class="text-muted">{{ formatDate(review.creationDate) }}</small>
            </div>
            <p class="text-secondary">{{ review.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository";
import AvailabilityManager from "./AvailabilityManager.vue";
import { BACKEND_URL } from "@/constants";

export default {
  components: { AvailabilityManager },
  data() {
    return {
      property: null,
      loading: true,
      errorMessage: null,
      showAllReviews: false // <--- NUEVO
    };
  },
  computed: {
    imageUrl() {
      return this.property ? `${BACKEND_URL}/properties/${this.property.id}/image` : "";
    },
    mapUrl() {
      if (!this.property?.latitude || this.property.latitude === 0) return null;
      const lat = this.property.latitude;
      const lon = this.property.longitude;
      return `https://www.openstreetmap.org/export/embed.html?bbox=${lon - 0.01}%2C${lat - 0.01}%2C${lon + 0.01}%2C${lat + 0.01}&layer=mapnik&marker=${lat}%2C${lon}`;
    }
  },
  async mounted() {
    await this.reload();
  },
  methods: {
    async reload() {
      this.loading = true;
      this.errorMessage = null;
      try {
        this.property = await PropertyRepository.getMyPropertyById(this.$route.params.id);
      } catch {
        this.errorMessage =
          "Error al cargar la propiedad. Puede que no exista o no tengas permisos.";
      } finally {
        this.loading = false;
      }
    },
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/800x400?text=Sin+Imagen";
    },
    formatDate(d) {
      if (!d) return "";
      return new Date(d).toLocaleDateString("es-ES");
    }
  }
};
</script>

<style scoped>
.object-fit-cover {
  object-fit: cover;
}

/* 👇 ESTILOS DEL MODAL */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}
.modal-content-custom {
  background: white;
  width: 100%;
  max-width: 600px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.btn-close {
  background: transparent;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}
.btn-close:before {
  content: "×";
}
</style>
