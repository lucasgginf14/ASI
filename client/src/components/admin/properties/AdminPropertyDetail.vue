<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border text-dark"></div>
  </div>

  <div v-else-if="property" class="container mt-4 mb-5">
    <back-button />
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div>
        <h2 class="fw-bold mb-1">{{ property.title }}</h2>
      </div>
      <span class="badge" :class="statusBadgeClass">{{ statusText }}</span>
    </div>

    <div class="card border-0 shadow-sm mb-4 bg-light">
      <div class="card-body p-4">
        <h5 class="fw-bold mb-3 border-bottom pb-2">⚙️ Acciones de Administración</h5>

        <div
          v-if="property.state === 'PENDING'"
          class="d-flex flex-column flex-md-row gap-3 align-items-start"
        >
          <button @click="handleApprove" class="btn btn-success fw-bold text-white px-4 order-md-1">
            ✅ Aprobar
          </button>

          <button @click="handleDelete" class="btn btn-danger text-white px-4 fw-bold order-md-2">
            🗑️ Eliminar
          </button>

          <button
            @click="
              showRejectInput = true;
              $nextTick(() => $refs.rejectInput && $refs.rejectInput.focus());
            "
            class="btn btn-warning fw-bold text-dark px-4 no-active-style order-md-3"
            type="button"
          >
            🚫 Rechazar
          </button>

          <div
            class="w-100 order-md-4 d-flex gap-2 align-items-stretch align-self-stretch"
            style="min-width: 220px"
          >
            <template v-if="showRejectInput">
              <input
                ref="rejectInput"
                v-model="rejectReason"
                type="text"
                class="form-control flex-grow-1 py-2 h-100"
                placeholder="Motivo del rechazo..."
              />

              <button
                @click="handleReject"
                class="btn btn-warning fw-bold text-dark px-4 py-2 h-100"
                :disabled="!rejectReason"
                type="button"
              >
                Confirmar
              </button>
            </template>
          </div>
        </div>

        <div
          v-else-if="property.state === 'REJECTED'"
          class="d-flex flex-column flex-md-row gap-3 align-items-start"
        >
          <button @click="handleApprove" class="btn btn-success fw-bold text-white px-4 order-md-1">
            ✅ Aprobar
          </button>

          <button @click="handleDelete" class="btn btn-danger text-white px-4 fw-bold order-md-2">
            🗑️ Eliminar
          </button>
        </div>

        <div v-else class="d-flex flex-column flex-md-row gap-3 align-items-start">
          <button @click="handleDelete" class="btn btn-danger text-white px-4 fw-bold">
            🗑️ Eliminar
          </button>

          <button
            @click="
              showRejectInput = true;
              $nextTick(() => $refs.rejectInput && $refs.rejectInput.focus());
            "
            class="btn btn-warning fw-bold text-dark px-4 no-active-style order-md-3"
            type="button"
          >
            🚫 Rechazar
          </button>

          <div
            class="w-100 order-md-4 d-flex gap-2 align-items-stretch align-self-stretch"
            style="min-width: 220px"
          >
            <template v-if="showRejectInput">
              <input
                ref="rejectInput"
                v-model="rejectReason"
                type="text"
                class="form-control flex-grow-1 py-2 h-100"
                placeholder="Motivo del rechazo..."
              />

              <button
                @click="handleReject"
                class="btn btn-warning fw-bold text-dark px-4 py-2 h-100"
                :disabled="!rejectReason"
                type="button"
              >
                Confirmar
              </button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-5" style="height: 350px">
      <div class="col-md-8 h-100">
        <img
          :src="imageSrc"
          class="w-100 h-100 rounded-4 object-fit-cover bg-light"
          @error="setPlaceholder"
          alt="🏠"
        />
      </div>
      <div class="col-md-4 h-100">
        <div class="h-100 rounded-4 overflow-hidden bg-light border position-relative">
          <iframe v-if="mapUrl" :src="mapUrl" class="w-100 h-100 border-0"></iframe>
          <div v-else class="d-flex align-items-center justify-content-center h-100 text-muted">
            Sin mapa
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-lg-8">
        <h4 class="fw-bold mb-1">Descripción</h4>
        <p class="text-secondary mb-2" style="white-space: pre-line">
          {{ property.description }}
        </p>
        <template v-if="property.owner?.id">
          <router-link
            :to="`/admin/users/${property.owner.id}`"
            class="d-block text-decoration-none text-reset btn btn-light p-3 rounded-4 text-start position-relative no-hover"
            role="button"
          >
            <div class="card bg-transparent border-0 p-0 mt-3">
              <div class="bg-light p-3 rounded-3">
                <h6 class="fw-bold mb-2">👤 Datos del Anfitrión</h6>

                <p class="mb-1">
                  <strong>Nombre:</strong>
                  {{ property.owner?.name }}
                  {{ property.owner?.surname }}
                  {{ property.owner?.surname2 ?? "" }}
                </p>

                <p class="mb-1">
                  <strong>Email:</strong>
                  {{ property.owner?.email }}
                </p>

                <p class="mb-0">
                  <strong>Usuario desde:</strong>
                  {{ formatDate(property.owner?.creationDate) }}
                </p>
              </div>
            </div>

            <div
              class="position-absolute bottom-0 end-0 m-3 small text-muted d-flex align-items-center"
            >
              <span>Ver detalle</span>
              <span class="ms-2">➡️</span>
            </div>
          </router-link>
        </template>
      </div>

      <div class="col-lg-4">
        <div class="card border-0 shadow-sm p-4 rounded-4">
          <h5 class="fw-bold mb-3">Detalles</h5>
          <ul class="list-unstyled mb-0">
            <li class="mb-2">
              📍 {{ property.street }}, {{ property.number }} {{ property.door ?? "" }}
            </li>
            <li class="mb-2">
              🏙️ {{ property.city }} ({{ property.postalCode }}), {{ property.province }},
              {{ property.country }}
            </li>
            <li class="mb-2">
              🛏️ {{ property.bedrooms }}
              {{ Number(property.bedrooms) === 1 ? "Habitación" : "Habitaciones" }}
            </li>
            <li class="mb-2">
              🚿 {{ property.bathrooms }} {{ Number(property.bathrooms) === 1 ? "Baño" : "Baños" }}
            </li>
            <li class="mb-2">
              👥 {{ property.maxOccupancy }}
              {{ Number(property.maxOccupancy) === 1 ? "Persona" : "Personas" }}
            </li>
            <li class="mb-2">📐 {{ property.squareMeters }} m²</li>
            <li class="mb-2">🏘️ {{ translatePropertyType(property.type) }}</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository";
import { BACKEND_URL } from "@/constants";
import BackButton from "@/common/utils/BackButton.vue";

export default {
  components: { BackButton },
  data() {
    return {
      property: null,
      loading: true,
      imageSrc: "/placeholder.png",
      rejectReason: "",
      showRejectInput: false
    };
  },
  computed: {
    statusBadgeClass() {
      if (this.property.state === "APPROVED") return "bg-success";
      if (this.property.state === "PENDING") return "bg-warning text-dark";
      return "bg-danger";
    },
    statusText() {
      if (this.property.state === "APPROVED") return "Publicada";
      if (this.property.state === "PENDING") return "Revisión Pendiente";
      return "Rechazada";
    },
    mapUrl() {
      if (!this.property?.latitude) return null;
      return `https://www.openstreetmap.org/export/embed.html?bbox=${this.property.longitude - 0.01}%2C${this.property.latitude - 0.01}%2C${this.property.longitude + 0.01}%2C${this.property.latitude + 0.01}&layer=mapnik&marker=${this.property.latitude}%2C${this.property.longitude}`;
    }
  },
  async mounted() {
    await this.loadProperty();
    await this.fetchImage();
  },
  methods: {
    formatDate(date) {
      if (!date) return "-";
      return new Date(date).toLocaleDateString();
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
    async loadProperty() {
      this.loading = true;
      try {
        const id = this.$route.params.id;
        this.property = await PropertyRepository.getAdminPropertyById(id);
        this.imageSrc = `${BACKEND_URL}/properties/${id}/image`;
      } catch {
        alert("Error al cargar la propiedad");
        this.$router.push("/admin/properties");
      } finally {
        this.loading = false;
      }
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
    },
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/800x400?text=Sin+Imagen";
    },
    async handleApprove() {
      if (!confirm("¿Seguro que quieres aprobar esta propiedad?")) return;
      try {
        await PropertyRepository.changeStatus(this.property.id, "APPROVED");
        this.property.approved = true;
        alert("¡Propiedad aprobada!");
        this.$router.push("/admin/properties");
      } catch (e) {
        console.error(e);
        alert("Error al aprobar. Mira la consola para más detalles.");
      }
    },
    async handleReject() {
      if (!confirm("¿Rechazar esta propiedad?")) return;
      try {
        await PropertyRepository.changeStatus(this.property.id, "REJECTED", this.rejectReason);
        alert("Propiedad rechazada.");
        this.$router.push("/admin/properties");
      } catch (e) {
        console.error(e);
        alert("Error al rechazar. Mira la consola para más detalles.");
      }
    },
    async handleDelete() {
      if (!confirm("PELIGRO: ¿Borrar permanentemente?")) return;
      try {
        await PropertyRepository.deleteAdmin(this.property.id);
        alert("Propiedad eliminada.");
        this.$router.push("/admin/properties");
      } catch {
        alert("Error al eliminar");
      }
    }
  }
};
</script>
<style scoped>
.object-fit-cover {
  object-fit: cover;
}
</style>
