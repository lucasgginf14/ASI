<template>
  <div class="container mt-5 mb-5">
    <h2 class="fw-bold mb-4">Mis Viajes</h2>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else-if="trips.length > 0" class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div class="col" v-for="booking in trips" :key="booking.id">
        <GuestBookingCard :booking="booking" @cancel="handleCancel" />
      </div>
    </div>

    <div v-else class="text-center py-5 bg-light rounded border border-dashed">
      <p class="text-muted mb-0">Aún no tienes viajes confirmados.</p>
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
    trips() {
      // 🔍 FILTRO: Solo las confirmadas, en curso o finalizadas
      return this.bookings.filter((b) =>
        ["CONFIRMED", "ACCEPTED", "IN_PROGRESS", "COMPLETED"].includes(b.state)
      );
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
    },
    async handleCancel(id) {
      const reason = prompt("Por favor, indica el motivo de la cancelación:");
      if (!reason) return;
      try {
        await ReservationRepository.cancelBooking(id, reason);
        await this.loadBookings();
      } catch {
        alert("Error al cancelar.");
      }
    }
  }
};
</script>
