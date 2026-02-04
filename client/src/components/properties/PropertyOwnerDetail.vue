<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border text-dark spinner-border-sm"></div>
  </div>

  <div v-else-if="property" class="container mt-4 mb-5">
    <back-button />
    <div class="d-flex justify-content-between align-items-start mb-3">
      <div>
        <h2 class="fw-bold mb-1">{{ property.title }}</h2>
        <span
          class="badge"
          :class="
            property.state === 'APPROVED'
              ? 'bg-success'
              : property.state === 'PENDING'
                ? 'bg-warning text-dark'
                : 'bg-danger text-white'
          "
        >
          {{
            property.state === "APPROVED"
              ? "Publicada"
              : property.state === "PENDING"
                ? "Revisión Pendiente"
                : "Rechazada"
          }}
        </span>
      </div>
      <div>
        <router-link
          :to="`/my-properties/${property.id}/edit`"
          class="btn btn-outline-dark rounded-pill px-4 fw-bold"
        >
          Editar
        </router-link>

        <button
          class="btn btn-outline-danger rounded-pill px-4 fw-bold ms-2"
          :disabled="isDeleting"
          @click="handleDelete(property.id)"
          title="Eliminar"
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

    <div class="row g-3 mb-4" style="height: 400px">
      <div class="col-lg-8 h-100">
        <img
          :src="imageSrc"
          class="w-100 h-100 object-fit-cover"
          @error="setPlaceholder"
          alt="Propiedad"
        />
      </div>
      <div class="col-lg-4 h-100 d-none d-lg-block">
        <div
          class="h-100 rounded-3 overflow-hidden bg-light position-relative border shadow-sm map-wrapper"
        >
          <MapResults v-if="property.latitude && property.longitude" :properties="[property]" />
          <div
            v-else
            class="d-flex align-items-center justify-content-center h-100 text-muted small flex-column"
          >
            <i class="bi bi-geo-alt-slash fs-3 mb-2 opacity-50"></i>
            <span>Ubicación exacta no disponible</span>
          </div>
        </div>
      </div>
    </div>

    <div class="row gx-4">
      <div class="mt-4">
        <h5 class="fw-bold mb-2">Sobre este alojamiento</h5>
        <p class="text-secondary small" style="white-space: pre-line; font-size: 0.95rem">
          {{ property.description }}
        </p>
      </div>
      <div class="mb-4">
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
              🚿 {{ property.bathrooms }}
              {{ Number(property.bathrooms) === 1 ? "Baño" : "Baños" }}
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

      <div class="mt-4 pt-3 border-top">
        <AvailabilityManager
          :property-id="property.id"
          :availabilities="property.availabilities"
          :is-approved="property.state === 'APPROVED'"
          @updated="reload"
        />

        <div v-if="availableRanges.length > 0">
          <VCalendar
            borderless
            transparent
            :attributes="calendarAttributes"
            :min-date="new Date()"
            :initial-page="initialPage"
            :rows="2"
            class="w-100"
          />
          <div class="d-flex gap-3 mt-2 small">
            <span class="d-flex align-items-center gap-1"
              ><span class="dot bg-success"></span> Disponible</span
            >
            <span class="d-flex align-items-center gap-1"
              ><span class="dot bg-danger"></span> Reservado</span
            >
          </div>
        </div>

        <div v-else class="alert mt-3 alert-warning border-0 py-2 px-3 small d-inline-block">
          <i class="bi bi-calendar-x me-2"></i> No hay fechas disponibles actualmente.
        </div>
      </div>

      <div v-if="property.reviews && property.reviews.length > 0" class="mt-5 mb-4 pt-4 border-top">
        <div class="d-flex align-items-center justify-content-between mb-4">
          <h5 class="fw-bold mb-0">Reseñas</h5>
          <div class="d-flex align-items-center gap-1 text-black fw-bold">
            <span
              class="badge rounded-pill bg-white text-dark border d-inline-flex align-items-center px-2 py-1 shadow-sm"
            >
              <span class="me-2 text-warning" style="font-size: 1rem">★</span>
              <strong class="me-1">{{ Number(property.averageRating || 0).toFixed(1) }}</strong>
              <small class="text-muted">/5</small>
            </span>
          </div>
        </div>

        <div class="row row-cols-3 g-3 mb-4 pb-3 border-bottom text-center">
          <div class="col border-end">
            <div class="small text-muted text-uppercase">Limpieza</div>
            <div class="fw-bold fs-5">
              {{ property.averageCleanlinessRating?.toFixed(1) || "-" }}
            </div>
          </div>
          <div class="col border-end">
            <div class="small text-muted text-uppercase">Hospitalidad</div>
            <div class="fw-bold fs-5">
              {{ property.averageHospitalityRating?.toFixed(1) || "-" }}
            </div>
          </div>
          <div class="col">
            <div class="small text-muted text-uppercase">Ubicación</div>
            <div class="fw-bold fs-5">
              {{ property.averageLocationRating?.toFixed(1) || "-" }}
            </div>
          </div>
        </div>

        <div class="mb-2">
          <span class="text-muted small fw-normal"
            >Total de reseñas: {{ property.reviews.length }}
          </span>
        </div>

        <div class="d-flex flex-column gap-3">
          <div
            v-for="(review, index) in property.reviews.slice(0, 2)"
            :key="index"
            class="p-3 bg-light rounded-3"
          >
            <div class="d-flex justify-content-between mb-2">
              <div>
                <span class="fw-bold d-block text-dark">{{
                  review.reviewer?.name || "Usuario"
                }}</span>
                <small class="text-muted">{{ formatDate(review.creationDate) }}</small>
              </div>
              <span
                class="badge rounded-pill bg-white text-dark border d-inline-flex align-items-center px-2 py-1 shadow-sm"
              >
                <span class="me-2 text-warning" style="font-size: 1rem">★</span>
                <strong class="me-1">{{ Number(review.rating || 0) }}</strong>
                <small class="text-muted">/5</small>
              </span>
            </div>

            <div class="d-flex gap-3 mb-2 small text-secondary">
              <span title="Limpieza">✨ {{ review.cleanliness }}</span>
              <span title="Hospitalidad">🤝 {{ review.hospitality }}</span>
              <span title="Ubicación">📍 {{ review.location }}</span>
            </div>

            <p class="mb-0 text-secondary small">{{ review.description }}</p>
          </div>
        </div>

        <button
          v-if="property.reviews.length > 2"
          @click="showAllReviews = true"
          class="btn btn-outline-dark rounded-pill mt-3 btn-sm fw-bold px-4"
        >
          Mostrar todas las reseñas
        </button>
      </div>
    </div>

    <div v-if="showAllReviews" class="modal-overlay" @click.self="showAllReviews = false">
      <div class="modal-content-custom animate__animated animate__fadeInUp">
        <div
          class="modal-header border-bottom p-3 d-flex justify-content-between align-items-center sticky-top bg-white"
        >
          <h5 class="fw-bold mb-0">Todas las reseñas</h5>
          <div class="d-flex align-items-center gap-2">
            <div class="input-group input-group-sm" style="width: 190px">
              <span class="input-group-text bg-white border-0 small text-muted">Filtrar</span>
              <select
                class="form-select form-select-sm"
                v-model="$data.reviewFilter"
                aria-label="Filtrar reseñas"
              >
                <option value="all">Todas</option>

                <option v-for="n in [5, 4, 3, 2, 1]" :key="n" :value="n">
                  {{ "★".repeat(n) + "☆".repeat(5 - n) }}
                </option>
              </select>
            </div>

            <button
              @click="showAllReviews = false"
              class="btn btn-outline-secondary btn-sm rounded-pill ms-2 d-flex align-items-center justify-content-center"
              aria-label="Cerrar"
              title="Cerrar"
            >
              Cerrar
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
        </div>
        <div class="modal-body p-4" style="max-height: 70vh; overflow-y: auto">
          <div
            v-for="(review, index) in (property.reviews || []).filter(
              (r) => $data.reviewFilter === 'all' || r.rating === Number($data.reviewFilter)
            )"
            :key="'modal-' + index"
            class="mb-4 pb-3 border-bottom"
          >
            <div class="d-flex justify-content-between mb-2">
              <div>
                <h6 class="fw-bold mb-0">{{ review.reviewer?.name || "Usuario" }}</h6>
                <small class="text-muted">{{ formatDate(review.creationDate) }}</small>
              </div>
              <span
                class="badge rounded-pill bg-white text-dark border d-inline-flex align-items-center px-2 py-1 shadow-sm"
              >
                <span class="me-2 text-warning" style="font-size: 1rem">★</span>
                <strong class="me-1">{{ Number(review.rating || 0) }}</strong>
                <small class="text-muted">/5</small>
              </span>
            </div>

            <div class="d-flex gap-3 mb-2 small text-secondary">
              <span title="Limpieza">✨ {{ review.cleanliness }}</span>
              <span title="Hospitalidad">🤝 {{ review.hospitality }}</span>
              <span title="Ubicación">📍 {{ review.location }}</span>
            </div>

            <p class="text-secondary">{{ review.description }}</p>
          </div>
          <div
            v-if="
              (property.reviews || []).filter(
                (r) => $data.reviewFilter === 'all' || r.rating === Number($data.reviewFilter)
              ).length === 0
            "
            class="text-center text-muted small"
          >
            No hay reseñas con ese rating.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository.js";
import { BACKEND_URL } from "@/constants.js";
import MapResults from "@/components/search/MapResults.vue";
import BackButton from "@/common/utils/BackButton.vue";
import AvailabilityManager from "@/components/properties/AvailabilityManager.vue";

export default {
  components: { AvailabilityManager, BackButton, MapResults },
  data() {
    return {
      property: null,
      loading: true,
      processing: false,
      booking: { startDate: "", endDate: "", numGuests: 1, requestText: "" },
      errorMessage: null,
      successMessage: null,
      currentPrice: null,
      isValidSelection: false,
      showAllReviews: false,
      reviewFilter: "all",
      imageSrc: "/placeholder.png",
      isDeleting: false
    };
  },
  computed: {
    // Rangos DISPONIBLES (Verde)
    availableRanges() {
      if (!this.property?.availabilities) return [];
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return this.property.availabilities
        .filter((a) => new Date(a.endDate) >= today)
        .sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
    },

    // Rangos OCUPADOS (Gris)
    bookedRanges() {
      if (!this.property?.bookings) return [];
      // Ordenar por fecha de inicio
      const sorted = [...this.property.bookings].sort(
        (a, b) => new Date(a.startDate) - new Date(b.startDate)
      );
      const merged = [];
      let current = null;

      for (const b of sorted) {
        const start = this.parseDate(b.startDate);
        const end = this.parseDate(b.endDate);

        if (!current) {
          current = { start, end };
          continue;
        }
        // Si la siguiente reserva empieza justo después de la actual, se unen
        const nextDay = new Date(current.end);
        nextDay.setDate(nextDay.getDate() + 1);
        if (start <= nextDay) {
          if (end > current.end) current.end = end;
        } else {
          merged.push(current);
          current = { start, end };
        }
      }
      if (current) merged.push(current);
      return merged;
    },

    initialPage() {
      if (this.availableRanges.length > 0) {
        const first = this.parseDate(this.availableRanges[0].startDate);
        return { month: first.getMonth() + 1, year: first.getFullYear() };
      }
      return null;
    },

    mergedAttributes() {
      if (!this.availableRanges.length) return [];
      const ranges = [...this.availableRanges];
      const merged = [];
      let current = null;

      for (const range of ranges) {
        const rStart = this.parseDate(range.startDate);
        const rEnd = this.parseDate(range.endDate);

        if (!current) {
          current = { start: rStart, end: rEnd };
          continue;
        }

        const dayAfterCurrent = new Date(current.end);
        dayAfterCurrent.setDate(dayAfterCurrent.getDate() + 1);

        if (rStart <= dayAfterCurrent) {
          if (rEnd > current.end) current.end = rEnd;
        } else {
          merged.push(current);
          current = { start: rStart, end: rEnd };
        }
      }
      if (current) merged.push(current);
      return merged;
    },

    calendarAttributes() {
      return [
        {
          key: "booked",
          highlight: { color: "red", fillMode: "light" },
          dates: this.bookedRanges,
          popover: { label: "Reservado" },
          order: 10
        },
        {
          key: "available",
          highlight: { color: "green", fillMode: "light" },
          dates: this.mergedAttributes,
          excludeDates: this.bookedRanges,
          order: 1
        }
      ];
    }
  },
  async mounted() {
    try {
      this.property = await PropertyRepository.getMyPropertyById(this.$route.params.id);
    } catch {
      this.errorMessage = "No se pudo cargar la propiedad.";
      setTimeout(() => this.$router.push("/"), 2000);
    } finally {
      this.loading = false;
    }
    await this.fetchImage();
  },
  methods: {
    parseDate(dateStr) {
      const d = new Date(dateStr);
      return new Date(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate());
    },
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/800x400?text=Sin+Imagen";
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
    },
    formatDate(dateStr) {
      if (!dateStr) return "";
      return new Date(dateStr).toLocaleDateString("es-ES", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        timeZone: "UTC"
      });
    },

    // Traduce el tipo de propiedad enum a una etiqueta en español
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
.object-fit-cover {
  object-fit: cover;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

/* Map Wrapper */
.map-wrapper :deep() {
  height: 100% !important;
  min-height: 100%;
}

/* Modal */
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
</style>
