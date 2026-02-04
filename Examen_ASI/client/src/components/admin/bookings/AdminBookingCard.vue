<template>
  <router-link :to="`/admin/bookings/${reservation.id}`" class="text-decoration-none text-reset">
    <div class="card h-100 border-0 shadow-sm reservation-card" :class="statusClass">
      <div
        class="card-header bg-white border-bottom-0 d-flex justify-content-between align-items-center pt-3"
      >
        <div class="d-flex align-items-center gap-2">
          <span class="badge bg-light text-dark border font-monospace">#{{ reservation.id }}</span>
        </div>
        <span :class="['badge fw-semibold', statusBadgeClass]">{{ stateLabel }}</span>
      </div>

      <div class="card-body">
        <h6 class="mb-2 text-truncate property-title" :title="reservation.propertyTitle">
          🏠 {{ reservation.propertyTitle }}
        </h6>
        <div>
          <div class="small text-muted">Huésped</div>
          <div class="mb-2 fw-bold text-dark" :title="reservation.guestName">
            {{ reservation.guestName }}
          </div>
        </div>

        <div>
          <div class="small text-muted">Anfitrión</div>
          <div class="mb-2 fw-bold text-dark" :title="reservation.ownerName">
            {{ reservation.ownerName }}
          </div>
        </div>

        <div class="p-2 bg-light rounded-3 text-center small fw-bold text-secondary mb-3">
          📅 {{ formatDate(reservation.startDate) }} ➜ {{ formatDate(reservation.endDate) }}
        </div>
      </div>
    </div>
  </router-link>
</template>

<script>
export default {
  props: { reservation: Object },

  computed: {
    stateLabel() {
      const s = this.reservation && this.reservation.state;
      switch (s) {
        case "PENDING":
          return "Pendiente";
        case "CONFIRMED":
          return "Confirmada";
        case "CANCELED_BY_GUEST":
          return "Cancelada por huésped";
        case "CANCELED_BY_HOST":
          return "Cancelada por anfitrión";
        case "CANCELED_BY_ADMIN":
          return "Cancelada por un admin";
        case "CANCELED_BY_SYSTEM":
          return "Cancelada por el sistema";
        case "IN_PROGRESS":
          return "En curso";
        case "COMPLETED":
          return "Completada";
        default:
          return s || "-";
      }
    },

    statusClass() {
      const s = this.reservation && this.reservation.state;
      switch (s) {
        case "CONFIRMED":
          return "border-success";
        case "PENDING":
          return "border-warning";
        case "IN_PROGRESS":
          return "border-info";
        case "COMPLETED":
          return "border-secondary";
        default:
          return "border-transparent";
      }
    },

    statusBadgeClass() {
      const s = this.reservation && this.reservation.state;
      switch (s) {
        case "CONFIRMED":
          return "bg-success text-white";
        case "PENDING":
          return "bg-warning text-dark";
        case "IN_PROGRESS":
          return "bg-info text-dark";
        case "COMPLETED":
          return "bg-secondary text-white";
        case "CANCELED_BY_GUEST":
        case "CANCELED_BY_HOST":
        case "CANCELED_BY_ADMIN":
        case "CANCELED_BY_SYSTEM":
          return "bg-danger text-white";
        default:
          return "bg-light text-dark";
      }
    }
  },

  methods: {
    formatDate(date) {
      if (!date) return "-";
      try {
        return new Intl.DateTimeFormat("es-ES", {
          day: "numeric",
          month: "short",
          year: "numeric"
        }).format(new Date(date));
      } catch {
        return new Date(date).toLocaleDateString();
      }
    }
  }
};
</script>

<style scoped>
.reservation-card {
  border-left: 6px solid transparent !important;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease;
  border-radius: 0.75rem;
  overflow: hidden;
}

.reservation-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.12) !important;
}

.property-title {
  font-size: 0.95rem;
}
</style>
