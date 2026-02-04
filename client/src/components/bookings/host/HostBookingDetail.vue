<template>
  <div v-if="loading" class="text-center py-5">
    <div class="spinner-border text-dark"></div>
  </div>

  <div v-else-if="booking" class="container mt-5 mb-5" style="max-width: 800px">
    <div class="d-flex align-items-center gap-3 mb-4">
      <BackButton />

      <h2 class="fw-bold mb-0">Reserva #{{ booking.id }}</h2>
      <span class="badge ms-auto fs-6 rounded-pill" :class="statusBadge">{{ statusText }}</span>
    </div>

    <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
      <div class="card-body p-4">
        <div class="d-flex align-items-center gap-3 mb-4 pb-4 border-bottom">
          <div>
            <h4 class="fw-bold mb-0">
              <span> {{ guestData.name || "Usuario" }} {{ guestData.surname || "" }} </span>
            </h4>

            <div class="text-muted small">
              Usuario desde {{ getGuestYear(guestData.creationDate) }}
            </div>
            <small class="guest-link" @click="goToProfile">Ver detalle</small>
          </div>

          <div class="ms-auto text-end">
            <button @click.stop="goToChat" class="btn btn-outline-dark rounded-pill btn-sm fw-bold">
              💬 Mensaje
            </button>
          </div>
        </div>

        <div class="row g-4 mb-4">
          <div class="col-md-8">
            <h5 class="fw-bold mb-3">Detalles del viaje</h5>
            <div class="alert alert-light border rounded-3">
              <div class="mb-2">
                <strong class="text-dark d-block">Propiedad Solicitada</strong>
                <span class="text-secondary">{{ booking.property?.title || "Propiedad" }}</span>
              </div>
              <div class="mb-2">
                <strong class="text-dark d-block">Fechas</strong>
                <span>{{ formatDate(booking.startDate) }} - {{ formatDate(booking.endDate) }}</span>
              </div>
              <div>
                <strong class="text-dark d-block">Huéspedes</strong>
                <span>{{ booking.numGuests }} personas</span>
              </div>
            </div>

            <div v-if="booking.requestText" class="mt-3">
              <h6 class="fw-bold mb-1">Mensaje del huésped:</h6>
              <p class="text-muted fst-italic">"{{ booking.requestText }}"</p>
            </div>
          </div>

          <div class="col-md-4">
            <div
              class="bg-light p-3 rounded-4 h-100 text-center d-flex flex-column justify-content-center border border-dashed"
            >
              <small class="text-uppercase text-muted fw-bold">Tus Ingresos</small>
              <div class="display-6 fw-bold text-success">{{ booking.price }}€</div>
            </div>
          </div>
        </div>

        <div v-if="booking.state === 'PENDING'" class="d-grid gap-2 pt-2 border-top">
          <div class="row g-2">
            <div class="col-6">
              <button @click="handleAccept" class="btn btn-dark w-100 rounded-pill fw-bold py-2">
                ✅ Aceptar
              </button>
            </div>
            <div class="col-6">
              <button
                @click="handleCancel"
                class="btn btn-outline-danger w-100 rounded-pill fw-bold py-2"
              >
                🚫 Rechazar
              </button>
            </div>
          </div>
        </div>

        <div v-else-if="!isCancelled" class="d-grid gap-2 pt-2 border-top">
          <button
            @click="handleCancel"
            class="btn btn-outline-danger w-100 rounded-pill fw-bold py-2"
          >
            🚫 Cancelar
          </button>
        </div>

        <div
          v-if="isCancelled && booking.cancellationReason"
          class="alert alert-danger border-0 mt-3"
        >
          <strong>Motivo de cancelación:</strong> {{ booking.cancellationReason }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BookingRepository from "@/repositories/BookingRepository";
import BackButton from "@/common/utils/BackButton.vue";
import ReservationRepository from "@/repositories/ReservationRepository.js";

export default {
  components: { BackButton },
  data() {
    return { booking: null, loading: true };
  },
  computed: {
    guestData() {
      if (!this.booking) return {};
      return this.booking.guest || this.booking.user || {};
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
    statusBadge() {
      if (!this.booking) return "";
      if (this.booking.state === "PENDING") return "bg-warning text-dark";
      if (["CONFIRMED", "ACCEPTED", "IN_PROGRESS"].includes(this.booking.state))
        return "bg-success";
      return "bg-secondary";
    },
    statusText() {
      const map = {
        PENDING: "Pendiente",
        CONFIRMED: "Confirmada",
        ACCEPTED: "Confirmada",
        IN_PROGRESS: "En Curso",
        COMPLETED: "Finalizada",
        CANCELLED: "Cancelada",
        REJECTED: "Rechazada",
        CANCELED_BY_HOST: "Rechazada por ti",
        CANCELED_BY_GUEST: "Cancelada por huésped",
        CANCELED_BY_SYSTEM: "Cancelada sistema"
      };
      return map[this.booking?.state] || this.booking?.state;
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
        this.booking = await BookingRepository.getHostBookingDetails(id);
      } catch (e) {
        console.error(e);
        alert("No se pudo cargar la reserva.");
        this.$router.push("/my-reservations-host");
      } finally {
        this.loading = false;
      }
    },

    formatDate(d) {
      if (!d) return "-";
      if (Array.isArray(d)) return new Date(d[0], d[1] - 1, d[2]).toLocaleDateString("es-ES");
      return new Date(d).toLocaleDateString("es-ES");
    },

    getGuestYear(d) {
      if (!d) return "-";
      if (Array.isArray(d)) return d[0];
      const date = new Date(d);
      if (!isNaN(date.getTime())) return date.getFullYear();
      return "-";
    },

    goToProfile() {
      const id = this.guestData.id;
      if (id) {
        this.$router.push("/users/" + id);
      }
    },

    goToChat() {
      if (!this.booking) return;

      const bookingId = this.booking.id;
      const title = this.booking.property?.title || `Reserva #${bookingId}`;
      const guestName = this.guestData.name || "Huésped";

      this.$router
        .push({
          path: "/chats",
          query: {
            bookingId: bookingId,
            title: title,
            name: guestName,
            role: "HOST"
          }
        })
        .catch((err) => {
          if (err.name !== "NavigationDuplicated") console.error(err);
        });
    },

    async handleAccept() {
      if (!confirm("¿Aceptar esta reserva?")) return;
      try {
        await BookingRepository.acceptBooking(this.booking.id);
        await this.loadData();
      } catch {
        alert("Error al aceptar");
      }
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

<style scoped>
/* Estilo para el enlace del nombre */
.guest-link {
  cursor: pointer;
  transition: color 0.2s;
}
.guest-link:hover {
  color: #0d6efd; /* Azul Bootstrap */
  text-decoration: underline;
}
</style>
