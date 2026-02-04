<template>
  <div class="container mt-5 mb-5">
    <h2 class="fw-bold mb-4">Mis Solicitudes</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else-if="requests.length > 0" class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="booking in requests" :key="booking.id">
        <GuestBookingCard :booking="booking" @cancel="handleCancel" />
      </div>
    </div>

    <div v-else class="text-center py-5 bg-light rounded border border-dashed">
      <p class="text-muted mb-3">No tienes solicitudes.</p>
      <router-link to="/" class="btn btn-dark rounded-pill px-4">Buscar alojamiento</router-link>
    </div>
  </div>
</template>

<script>
// 👇 Importamos TU repositorio
import ReservationRepository from "@/repositories/ReservationRepository.js";
import GuestBookingCard from "./GuestBookingCard.vue";

export default {
  components: { GuestBookingCard },
  data() {
    return { bookings: [], loading: true };
  },
  computed: {
    requests() {
      return this.bookings;
    }
  },
  async mounted() {
    await this.loadBookings();
  },
  methods: {
    async loadBookings() {
      this.loading = true;
      try {
        this.bookings = await ReservationRepository.getMyBookings();
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
