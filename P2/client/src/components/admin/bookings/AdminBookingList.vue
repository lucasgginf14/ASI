<template>
  <div class="container mt-5 mb-5">
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
      <h2 class="fw-bold">Gestión de Reservas</h2>

      <div class="input-group" style="max-width: 300px">
        <span class="input-group-text bg-white border-end-0">🔍</span>
        <input
          v-model="searchId"
          type="text"
          class="form-control border-start-0 ps-0"
          placeholder="Buscar por ID..."
        />
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div
      v-else-if="filteredReservations.length > 0"
      class="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4"
    >
      <div class="col" v-for="res in filteredReservations" :key="res.id">
        <AdminReservationCard :reservation="res" @cancel="handleCancel" />
      </div>
    </div>

    <div v-else class="text-center py-5 bg-light rounded-4">
      <p class="text-muted mb-0">No se encontraron reservas con estos criterios.</p>
    </div>
  </div>
</template>

<script>
import ReservationRepository from "@/repositories/ReservationRepository";
import AdminBookingCard from "./AdminBookingCard.vue";

export default {
  components: { AdminReservationCard: AdminBookingCard },
  data() {
    return {
      reservations: [],
      searchId: "",
      filter: "all",
      loading: true
    };
  },
  computed: {
    filteredReservations() {
      let result = this.reservations;

      // Buscador por ID
      if (this.searchId) {
        result = result.filter((r) => String(r.id).includes(this.searchId));
      }

      return result;
    }
  },
  async mounted() {
    await this.loadReservations();
  },
  methods: {
    async loadReservations() {
      this.loading = true;
      try {
        this.reservations = await ReservationRepository.getAllReservationsAdmin();
        // 👇 DEBUG: Mira la consola del navegador para ver qué llega realmente
        console.log("Reservas cargadas:", this.reservations);
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    },
    async handleCancel(id) {
      const reason = prompt(
        "⚠️ Acción Administrativa\nPor favor, escribe el motivo de la cancelación:"
      );

      if (reason === null) return;
      if (!reason.trim()) return alert("El motivo es obligatorio para cancelar.");

      try {
        await ReservationRepository.cancelReservationAdmin(id, reason);
        alert("Reserva cancelada correctamente.");
        await this.loadReservations();
      } catch {
        alert("Error al cancelar la reserva.");
      }
    }
  }
};
</script>
