<template>
  <div
    class="card h-100 border-0 shadow-sm"
    :class="borderClass"
    role="link"
    tabindex="0"
    style="
      cursor: pointer;
      transition:
        transform 0.15s ease,
        box-shadow 0.15s ease;
    "
    @click="$router.push({ path: '/my-bookings/' + booking.id })"
    @keydown.enter="$router.push({ path: '/my-bookings/' + booking.id })"
    onmouseover="this.style.transform='translateY(-4px)'; this.style.boxShadow='0 .5rem 1rem rgba(0,0,0,.12)';"
    onmouseout="this.style.transform=''; this.style.boxShadow='';"
    onfocus="this.style.transform='translateY(-4px)'; this.style.boxShadow='0 .5rem 1rem rgba(0,0,0,.12)';"
    onblur="this.style.transform=''; this.style.boxShadow='';"
  >
    <div class="card-body d-flex flex-column">
      <div class="d-flex justify-content-between align-items-start mb-3">
        <span class="badge rounded-pill" :class="badgeClass">
          {{ statusLabel }}
        </span>
      </div>

      <h5 class="fw-bold mb-2 text-truncate" :title="booking.propertyTitle">
        {{ booking.propertyTitle }}
      </h5>

      <div class="p-3 bg-light rounded text-center mt-2 mb-3">
        <small class="text-muted text-uppercase fw-bold" style="font-size: 0.7rem">Fechas</small>
        <div class="fw-bold text-dark">
          {{ formatDate(booking.startDate) }} ➜ {{ formatDate(booking.endDate) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    booking: { type: Object, required: true }
  },
  computed: {
    isPending() {
      return this.booking.state === "PENDING";
    },
    // Para no mostrar el chat si ya se canceló la reserva
    isCancelledOrRejected() {
      return [
        "CANCELLED",
        "REJECTED",
        "CANCELED_BY_GUEST",
        "CANCELED_BY_HOST",
        "CANCELED_BY_SYSTEM"
      ].includes(this.booking.state);
    },
    canCancel() {
      return ["PENDING", "CONFIRMED", "ACCEPTED"].includes(this.booking.state);
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
        CANCELED_BY_SYSTEM: "⚠️ Cancelada por el sistema",
        CANCELED_BY_ADMIN: "️🛠️ Cancelada por admin"
      };
      return map[this.booking.state] || this.booking.state;
    },
    badgeClass() {
      if (this.isPending) return "bg-warning text-dark";
      if (["CONFIRMED", "ACCEPTED", "IN_PROGRESS"].includes(this.booking.state))
        return "bg-success";
      return "bg-secondary";
    },
    borderClass() {
      if (this.isPending) return "border-start border-4 border-warning";
      if (["CONFIRMED", "ACCEPTED", "IN_PROGRESS"].includes(this.booking.state))
        return "border-start border-4 border-success";
      return "border-start border-4 border-secondary";
    }
  },
  methods: {
    formatDate(d) {
      if (!d) return "-";
      return new Date(d).toLocaleDateString("es-ES");
    },

    goToChat() {
      this.$router.push({
        path: "/chats",
        query: {
          bookingId: this.booking.id,
          title: this.booking.propertyTitle
          // Nota: No pasamos 'name' del anfitrión porque el backend no lo envía en esta lista,
          // pero el chat cargará la conversación correctamente con el bookingId.
        }
      });
    }
  }
};
</script>
