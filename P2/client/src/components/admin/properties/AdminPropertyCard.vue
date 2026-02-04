<template>
  <router-link :to="`/admin/properties/${property.id}`" class="text-decoration-none text-reset">
    <div
      class="card h-100 border-0 shadow-sm overflow-hidden property-card"
      :class="statusClass"
      role="button"
    >
      <div class="position-relative" style="height: 180px; background: #eee">
        <img
          :src="imageSrc"
          class="w-100 h-100 object-fit-cover"
          @error="setPlaceholder"
          alt="Propiedad"
        />
        <div class="position-absolute top-0 end-0 m-2">
          <span class="badge" :class="badgeClass">{{ statusText }}</span>
        </div>
      </div>

      <div class="card-body p-3">
        <h6 class="fw-bold text-truncate mb-1">{{ property.title }}</h6>
        <p class="text-muted small mb-2">
          {{ property.city }}, {{ property.province }}, {{ property.country }}
        </p>
        <div class="text-muted">Creada por: {{ property.owner && property.owner.email }}</div>
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
      imageSrc: "/placeholder.png"
    };
  },
  async mounted() {
    await this.fetchImage();
  },
  computed: {
    // Colores del borde según estado
    statusClass() {
      if (this.property.state === "APPROVED") return "border-success-subtle"; // Verde
      if (this.property.state === "PENDING") return "border-warning-subtle"; // Amarillo
      return "border-danger-subtle"; // Rojo (rechazada)
    },
    // Colores del badge
    badgeClass() {
      if (this.property.state === "APPROVED") return "bg-success";
      if (this.property.state === "PENDING") return "bg-warning text-dark";
      return "bg-danger";
    },
    statusText() {
      if (this.property.state === "APPROVED") return "Aprobada";
      if (this.property.state === "PENDING") return "Pendiente";
      return "Rechazada";
    }
  },
  methods: {
    async fetchImage() {
      const token = localStorage.getItem("token"); // o como guardes el token
      const res = await fetch(`${BACKEND_URL}/properties/${this.property.id}/image`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) return;
      const blob = await res.blob();
      this.imageSrc = URL.createObjectURL(blob);
    },
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/400x300?text=Sin+Imagen";
    }
  }
};
</script>

<style scoped>
/* Borde izquierdo de color para destacar el estado */
.property-card {
  border-left: 4px solid transparent !important; /* Base */
  transition: transform 0.2s;
}

.property-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
}
.object-fit-cover {
  object-fit: cover;
}
</style>
