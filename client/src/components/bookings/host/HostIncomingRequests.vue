<template>
  <div class="container mt-5 mb-5">
    <h2 class="fw-bold mb-4">Solicitudes Recibidas</h2>
    <div v-if="loading" class="text-center py-5"><div class="spinner-border text-dark"></div></div>

    <div
      v-else-if="requests.length === 0"
      class="text-center py-5 bg-light rounded-4 border border-dashed"
    >
      <p class="text-muted mb-0">No tienes solicitudes pendientes por responder.</p>
    </div>

    <div v-else class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="req in requests" :key="req.id">
        <HostBookingCard :booking="req" @accept="handleAccept" @reject="handleReject" />
      </div>
    </div>
  </div>
</template>

<script>
import ReservationRepository from "@/repositories/ReservationRepository";
import HostBookingCard from "./HostBookingCard.vue";

export default {
  components: { HostBookingCard },
  data() {
    return { requests: [], loading: true };
  },
  async mounted() {
    await this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        this.requests = await ReservationRepository.findIncomingRequests();
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    },
    async handleAccept(id) {
      if (!confirm("¿Aceptar reserva?")) return;
      try {
        await ReservationRepository.accept(id);
        await this.loadData();
      } catch {
        alert("Error al aceptar");
      }
    },
    async handleReject(id) {
      if (!confirm("¿Rechazar reserva?")) return;
      try {
        await ReservationRepository.cancelBooking(id, "Rechazada por anfitrión");
        await this.loadData();
      } catch {
        alert("Error al rechazar");
      }
    }
  }
};
</script>
