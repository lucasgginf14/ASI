<template>
  <router-link :to="`/properties/${property.id}`" class="text-decoration-none text-dark">
    <div class="card h-100 border-0 shadow-sm property-card">
      <div class="position-relative" style="height: 200px; background: #f0f0f0">
        <img
          :src="imageSrc"
          class="w-100 h-100 object-fit-cover"
          alt="Imagen propiedad"
          @error="setPlaceholder"
        />

        <span
          v-if="property.lowerPrice"
          class="position-absolute bottom-0 end-0 m-2 badge bg-black fs-6"
        >
          {{ property.lowerPrice }}€ <span class="fw-normal x-small">/noche</span>
        </span>
      </div>

      <div class="card-body p-3">
        <div class="d-flex justify-content-between align-items-start mb-1">
          <span class="badge bg-light text-dark border text-uppercase x-small">
            {{ translatePropertyType(property.type) }}
          </span>

          <div class="small fw-bold text-warning" v-if="property.averageRating > 0">
            ★ {{ property.averageRating.toFixed(1) }}
          </div>
        </div>

        <h6 class="card-title fw-bold text-truncate mb-1" :title="property.title">
          {{ property.title }}
        </h6>

        <p class="card-text text-muted small text-truncate">
          <i class="bi bi-geo-alt-fill me-1"></i>
          {{ property.city }}, {{ property.country }}
        </p>
      </div>
    </div>
  </router-link>
</template>

<script>
import { BACKEND_URL } from "@/constants";

export default {
  props: { property: Object },
  data() {
    return {
      imageSrc: this.property ? `${BACKEND_URL}/properties/${this.property.id}/image` : ""
    };
  },
  methods: {
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/400x300?text=Sin+Imagen";
    },
    translatePropertyType(type) {
      if (!type) return "—";
      const map = {
        APARTMENT: "Apartamento",
        CHALET: "Chalet",
        TOWNHOUSE: "Adosado",
        PENTHOUSE: "Ático",
        STUDIO: "Estudio",
        DUPLEX: "Dúplex",
        COUNTRY_HOUSE: "Casa rural",
        VILLA: "Villa",
        BUNGALOW: "Bungalow",
        CABIN: "Cabaña",
        ESTATE: "Finca",
        MANSION: "Mansión",
        HOUSE: "Casa",
        LOFT: "Loft",
        HOTEL_ROOM: "Habitación de hotel"
      };
      return map[type] || type;
    }
  }
};
</script>

<style scoped>
.property-card {
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  overflow: hidden;
  border-radius: 12px;
  cursor: pointer;
}
.property-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
}
.object-fit-cover {
  object-fit: cover;
}
.x-small {
  font-size: 0.75rem;
}
</style>
