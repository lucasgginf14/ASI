<template>
  <div class="card border-0 shadow-sm overflow-hidden h-100 property-card" @click="goToDetail">
    <div class="row g-0 h-100">
      <div class="col-md-4 bg-light position-relative" style="min-height: 200px">
        <img
          :src="imageUrl"
          class="w-100 h-100 object-fit-cover"
          alt="Propiedad"
          @error="handleError"
        />
      </div>

      <div class="col-md-8">
        <div class="card-body d-flex flex-column h-100 p-3">
          <div class="mb-2">
            <h5 class="card-title fw-bold mb-1 text-truncate">{{ property.title }}</h5>
            <p class="card-text text-muted small text-truncate">Dirección: {{ fullAddress }}</p>
          </div>

          <div class="d-flex flex-wrap gap-3 text-secondary small mb-3">
            <span v-if="property.bedrooms"
              ><i class="bi bi-layout-bedroom"></i> {{ property.bedrooms }} hab.</span
            >
            <span v-if="property.bathrooms"
              ><i class="bi bi-droplet"></i> {{ property.bathrooms }} baños</span
            >
            <span v-if="property.maxOccupancy"
              ><i class="bi bi-people"></i> {{ property.maxOccupancy }} pers.</span
            >
            <span v-if="property.squareMeters"
              ><i class="bi bi-rulers"></i> {{ property.squareMeters }} m²</span
            >
          </div>

          <div class="mt-auto d-flex justify-content-between align-items-end border-top pt-2">
            <div>
              <h4 class="fw-bold mb-0">
                {{ formatPrice(property.lowerPrice) }}€
                <small class="fs-6 text-muted fw-normal">/noche</small>
              </h4>
            </div>

            <div class="d-flex flex-column align-items-end">
              <div class="fw-bold fs-5 d-flex align-items-center gap-1">
                {{ property.averageRating ? property.averageRating.toFixed(1) : "New" }}
                <i class="bi bi-star-fill text-dark"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { BACKEND_URL } from "@/constants";

export default {
  props: { property: Object },
  data() {
    return { imgError: false };
  },
  computed: {
    imageUrl() {
      if (this.imgError) return "https://via.placeholder.com/400x300?text=No+Image";
      return `${BACKEND_URL}/properties/${this.property.id}/image`;
    },
    // Construimos la dirección de forma segura
    fullAddress() {
      const parts = [this.property.street, this.property.number, this.property.city].filter(
        Boolean
      ); // Elimina nulos
      return parts.join(", ") || this.property.city || "Ubicación desconocida";
    }
  },
  methods: {
    goToDetail() {
      this.$router.push(`/properties/${this.property.id}`);
    },
    handleError() {
      this.imgError = true;
    },

    formatPrice(value) {
      if (!value) return "0";
      return Number(value).toFixed(0); // .toFixed(0) para quitar decimales como en tu dibujo (134€), o (2) para 134.00€
    }
  }
};
</script>

<style scoped>
.property-card {
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}
.property-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}
.object-fit-cover {
  object-fit: cover;
}
/* Ajuste para iconos de bootstrap */
.bi {
  margin-right: 4px;
}
</style>
