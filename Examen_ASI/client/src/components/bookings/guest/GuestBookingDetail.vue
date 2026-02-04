<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border text-dark"></div>
  </div>

  <div v-else-if="booking" class="container mt-5 mb-5" style="max-width: 900px">
    <div class="d-flex align-items-center gap-3 mb-4">
      <BackButton />

      <h2 class="fw-bold mb-0">Detalles de la Reserva</h2>
      <span class="badge ms-auto fs-6 rounded-pill" :class="badgeClass">{{ statusLabel }}</span>
    </div>

    <div class="row g-4">
      <div class="col-lg-8">
        <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-4">
          <div style="height: 300px; background-color: #eee; position: relative">
            <img
              v-if="booking.property"
              :src="getPropertyImage(booking.property.id)"
              class="w-100 h-100"
              style="object-fit: cover"
              @error="setPlaceholder"
              alt="Propiedad"
            />
            <div
              v-else
              class="w-100 h-100 d-flex align-items-center justify-content-center text-muted"
            >
              Sin imagen
            </div>
          </div>

          <div class="card-body p-4">
            <h3 class="fw-bold mb-1">{{ booking.property?.title }}</h3>
            <p class="text-muted mb-4">
              <i class="bi bi-geo-alt-fill me-1"></i>
              {{ booking.property?.city }}, {{ booking.property?.province }}
            </p>
            <router-link
              v-if="booking.property"
              :to="`/properties/${booking.property.id}`"
              class="btn btn-outline-primary rounded-pill fw-bold mb-4"
            >
              Ver detalle de la propiedad
            </router-link>

            <h5 class="fw-bold border-bottom pb-2 mb-3">Tu estancia</h5>
            <div class="row g-3">
              <div class="col-md-6">
                <small class="text-uppercase text-muted fw-bold" style="font-size: 0.75rem"
                  >LLEGADA</small
                >
                <div class="fs-5">{{ formatDate(booking.startDate) }}</div>
              </div>
              <div class="col-md-6">
                <small class="text-uppercase text-muted fw-bold" style="font-size: 0.75rem"
                  >SALIDA</small
                >
                <div class="fs-5">{{ formatDate(booking.endDate) }}</div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="booking.requestText" class="card border-0 shadow-sm rounded-4 p-4 mb-4 bg-light">
          <h6 class="fw-bold mb-2">Tu mensaje al anfitrión:</h6>
          <p class="mb-0 text-muted fst-italic">{{ booking.requestText }}</p>
        </div>
        <div
          v-if="isCancelled && booking.cancellationReason"
          class="card border-0 shadow-sm rounded-4 p-4 mb-4 bg-danger-subtle"
        >
          <h6 class="fw-bold mb-2">Motivo de cancelación:</h6>
          <p class="mb-0 text-muted fst-italic">{{ booking.cancellationReason }}</p>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card border-0 shadow rounded-4 p-4">
          <h5 class="fw-bold mb-3">Resumen de pago</h5>

          <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="text-muted">Huéspedes</span>
            <span class="fw-bold">{{ booking.numGuests }}</span>
          </div>

          <hr />

          <div class="d-flex justify-content-between align-items-center mb-4">
            <span class="fs-5 fw-bold">Total</span>
            <span class="fs-4 fw-bold">{{ booking.price?.toFixed(2) }}€</span>
          </div>

          <div class="d-grid gap-2">
            <button
              v-if="!isCancelled"
              @click="goToChat"
              class="btn btn-dark rounded-pill fw-bold py-2"
            >
              💬 Contactar Anfitrión
            </button>

            <button
              v-if="canCancel"
              @click="handleCancel"
              class="btn btn-outline-danger rounded-pill fw-bold py-2"
            >
              {{ isPending ? "Retirar Solicitud" : "Cancelar Reserva" }}
            </button>
          </div>

          <div v-if="booking.state === 'COMPLETED'" class="mt-3 text-center">
            <span class="text-success fw-bold"
              ><i class="bi bi-check-circle-fill"></i> Viaje finalizado</span
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ReservationRepository from "@/repositories/ReservationRepository";
import { BACKEND_URL } from "@/constants";
// 👇 IMPORTAMOS EL COMPONENTE DE BOTÓN ATRÁS
import BackButton from "@/common/utils/BackButton.vue";

export default {
  components: { BackButton },
  data() {
    return {
      booking: null,
      loading: true
    };
  },
  computed: {
    isPending() {
      return this.booking?.state === "PENDING";
    },
    isCancelled() {
      return [
        "CANCELLED",
        "REJECTED",
        "CANCELED_BY_GUEST",
        "CANCELED_BY_HOST",
        "CANCELED_BY_SYSTEM",
        "CANCELED_BY_ADMIN"
      ].includes(this.booking?.state);
    },
    canCancel() {
      return ["PENDING", "CONFIRMED", "ACCEPTED"].includes(this.booking?.state);
    },
    statusLabel() {
      const map = {
        PENDING: "⏳ Pendiente",
        CONFIRMED: "✅ Confirmada",
        ACCEPTED: "✅ Confirmada",
        IN_PROGRESS: "🏠 En curso",
        COMPLETED: "🏁 Finalizada",
        CANCELLED: "❌ Cancelada",
        REJECTED: "🚫 Rechazada",
        CANCELED_BY_GUEST: "❌ Cancelada por ti",
        CANCELED_BY_HOST: "🚫 Cancelada por anfitrión",
        CANCELED_BY_SYSTEM: "⚠️ Cancelada sistema",
        CANCELED_BY_ADMIN: "🛠️ Cancelada por un admin"
      };
      return map[this.booking?.state] || this.booking?.state;
    },
    badgeClass() {
      if (this.isPending) return "bg-warning text-dark";
      if (["CONFIRMED", "ACCEPTED", "IN_PROGRESS"].includes(this.booking?.state))
        return "bg-success";
      return "bg-secondary";
    }
  },
  async mounted() {
    await this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const id = this.$route.params.id;
        this.booking = await ReservationRepository.getGuestBookingDetails(id);
      } catch (e) {
        console.error(e);
        alert("No se pudo cargar la reserva.");
        this.$router.push("/my-bookings");
      } finally {
        this.loading = false;
      }
    },
    formatDate(d) {
      return d ? new Date(d).toLocaleDateString("es-ES") : "-";
    },
    getPropertyImage(id) {
      return `${BACKEND_URL}/properties/${id}/image`;
    },
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/800x400?text=Sin+Imagen";
    },

    goToChat() {
      this.$router.push({
        path: "/chats",
        query: {
          bookingId: this.booking.id,
          title: this.booking.property.title
        }
      });
    },

    async handleCancel() {
      const reason = prompt("Motivo de la cancelación:");
      if (!reason) return;
      try {
        await ReservationRepository.cancelBooking(this.booking.id, reason);
        await this.loadData();
      } catch {
        alert("Error al cancelar la reserva.");
      }
    }
  }
};
</script>
