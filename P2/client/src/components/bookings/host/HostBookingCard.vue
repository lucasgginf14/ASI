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
    @click="goToDetail"
    @keydown.enter="goToDetail"
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

      <h4 class="fw-bold mb-3 text-truncate" :title="guestData.name">
        <span class="d-flex align-items-center gap-2">
          <i class="bi bi-person-circle text-secondary fs-5"></i>
          {{ guestData.name || "Usuario" }} {{ guestData.surname || "" }}
        </span>
      </h4>

      <div class="p-3 bg-light rounded text-center mb-3">
        <small class="text-muted text-uppercase fw-bold" style="font-size: 0.7rem">Estancia</small>
        <div class="fw-bold text-dark">
          {{ formatDate(booking.startDate) }} ➜ {{ formatDate(booking.endDate) }}
        </div>
      </div>

      <div v-if="booking.requestText" class="alert alert-secondary p-2 mb-3 fst-italic small">
        "{{ booking.requestText }}"
      </div>

      <div class="mt-auto text-end pt-2 border-top">
        <span class="small fw-bold text-primary"
          >Gestionar reserva <i class="bi bi-arrow-right"></i
        ></span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: { booking: Object },
  computed: {
    // 👇 SOLUCIÓN: Unificamos datos por si el back envía 'user' en vez de 'guest'
    guestData() {
      if (!this.booking) return {};
      return this.booking.guest || this.booking.user || {};
    },
    isPending() {
      return this.booking.state === "PENDING";
    },
    statusLabel() {
      const map = {
        PENDING: "⏳ Pendiente",
        CONFIRMED: "✅ Confirmada",
        ACCEPTED: "✅ Confirmada",
        IN_PROGRESS: "🏠 En curso",
        COMPLETED: "🏁 Finalizada",
        CANCELED_BY_GUEST: "❌ Cancelada por huésped",
        CANCELED_BY_HOST: "🚫 Rechazada por ti",
        CANCELED_BY_SYSTEM: "⚠️ Cancelada sistema",
        CANCELED_BY_ADMIN: "️🛠️ Cancelada admin",
        REJECTED: "🚫 Rechazada"
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
    goToDetail() {
      this.$router.push("/my-reservations-host/" + this.booking.id);
    },
    // Ir al perfil del usuario
    goToProfile() {
      const id = this.guestData.id;
      if (id) {
        this.$router.push("/users/" + id);
      }
    }
  }
};
</script>
