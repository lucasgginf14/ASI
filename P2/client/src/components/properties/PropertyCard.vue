<template>
  <div class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden">
    <div class="position-relative" style="height: 180px; background: #f0f0f0">
      <img
        :src="imageSrc"
        class="w-100 h-100 object-fit-cover"
        @error="setPlaceholder"
        alt="Propiedad"
      />
      <div class="position-absolute top-0 end-0 m-2 p-1 rounded">
        <span
          :class="[
            'badge rounded-pill d-inline-flex align-items-center',
            property.state === 'APPROVED'
              ? 'bg-success text-white'
              : property.state === 'PENDING'
                ? 'bg-warning text-dark'
                : 'bg-danger text-white'
          ]"
          :title="
            property.state === 'APPROVED'
              ? 'Aprobada'
              : property.state === 'PENDING'
                ? 'Pendiente'
                : 'Rechazada'
          "
          role="status"
          aria-live="polite"
        >
          <i
            :class="
              property.state === 'APPROVED'
                ? 'bi bi-check-circle-fill'
                : property.state === 'PENDING'
                  ? 'bi bi-hourglass-split'
                  : 'bi bi-x-circle-fill'
            "
            aria-hidden="true"
          ></i>
          <span class="text-truncate" style="max-width: 8rem; display: inline-block">
            {{
              property.state === "APPROVED"
                ? "Aprobada"
                : property.state === "PENDING"
                  ? "Pendiente"
                  : "Rechazada"
            }}
          </span>
        </span>
      </div>
    </div>

    <div class="card-body p-3 d-flex flex-column">
      <h6 class="fw-bold text-truncate mb-1">{{ property.title }}</h6>
      <div class="d-flex justify-content-between align-items-center mb-3 gap-2">
        <p class="text-muted small mb-0 text-truncate" style="max-width: 65%">
          {{ property.city }}, {{ property.country }}
        </p>

        <div
          v-if="property.averageRating !== null && property.averageRating !== undefined"
          class="d-flex align-items-center"
        >
          <span
            class="badge text-dark"
            :title="`Valoración media: ${property.averageRating.toFixed(1)}`"
            aria-label="Valoración media"
            style="min-width: 56px; padding: 0.35rem 0.5rem; border-radius: 0.5rem"
          >
            <span class="fw-bold" style="font-size: 0.9rem">
              {{ property.averageRating.toFixed(1) }} ⭐</span
            >
          </span>
        </div>
      </div>

      <div class="d-flex gap-2 pt-3 border-top">
        <router-link
          :to="`/my-properties/${property.id}`"
          class="btn btn-sm btn-light flex-fill fw-bold text-truncate text-center"
          style="flex: 0 0 33.333%; max-width: 33.333%"
        >
          Ver
        </router-link>
        <router-link
          :to="`/my-properties/${property.id}/edit`"
          class="btn btn-sm btn-light flex-fill fw-bold text-truncate text-center"
          style="flex: 0 0 33.333%; max-width: 33.333%"
        >
          Editar
        </router-link>

        <button
          class="btn btn-sm btn-outline-danger flex-fill fw-bold text-truncate text-center"
          :disabled="isDeleting"
          @click="handleDelete(property.id)"
          title="Eliminar"
          style="flex: 0 0 33.333%; max-width: 33.333%"
        >
          <span
            v-if="isDeleting"
            class="spinner-border spinner-border-sm"
            role="status"
            aria-hidden="true"
          ></span>
          <span v-else>Eliminar</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { BACKEND_URL } from "@/constants";
import PropertyRepository from "@/repositories/PropertyRepository";

export default {
  props: { property: Object },
  data() {
    return {
      imageSrc: "/placeholder.png",
      isDeleting: false
    };
  },
  async mounted() {
    await this.fetchImage();
  },
  methods: {
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/600x400?text=Sin+Imagen";
    },
    async fetchImage() {
      const token = localStorage.getItem("token"); // o como guardes el token
      const res = await fetch(`${BACKEND_URL}/properties/${this.property.id}/image`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) return;
      const blob = await res.blob();
      this.imageSrc = URL.createObjectURL(blob);
    },
    async handleDelete(id) {
      if (!confirm("¿Eliminar propiedad? Esta acción no se puede deshacer.")) return;
      this.isDeleting = true;
      try {
        await PropertyRepository.delete(id);
        this.$emit("deleted", id);

        // Eliminar el elemento raíz del DOM para quitar la carta inmediatamente
        if (this.$el && this.$el.parentNode) {
          this.$el.parentNode.removeChild(this.$el);
        }
      } catch (err) {
        console.error(err);
        const serverMessage =
          err?.response?.data?.message || err?.data?.message || err?.message || null;

        if (serverMessage) {
          alert(serverMessage);
        } else {
          alert("No se pudo eliminar la propiedad. Intenta de nuevo.");
        }
      } finally {
        this.isDeleting = false;
      }
    }
  }
};
</script>

<style scoped>
.object-fit-cover {
  object-fit: cover;
}
.btn-light {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
}
.btn-light:hover {
  background-color: #e2e6ea;
}
</style>
