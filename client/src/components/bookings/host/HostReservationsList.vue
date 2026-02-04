<template>
  <div class="container mt-5 mb-5">
    <h2 class="fw-bold mb-4">Reservas de mis Propiedades</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div
      v-else-if="reservations.length === 0"
      class="text-center py-5 bg-light rounded-4 border border-dashed"
    >
      <p class="text-muted mb-0">Aún no tienes reservas en tu historial.</p>
    </div>

    <div v-else class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="res in reservations" :key="res.id">
        <HostBookingCard :booking="res" />
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
    return { reservations: [], loading: true };
  },
  async mounted() {
    await this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        // Llamamos al endpoint "ALL" del anfitrión
        this.reservations = await ReservationRepository.findHostBookings();
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
