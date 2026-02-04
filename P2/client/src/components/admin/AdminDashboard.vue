<template>
  <div class="container mt-5">
    <div class="text-center mb-5">
      <h1 class="fw-bold">Panel de Administración</h1>
      <p class="text-muted fs-5">Bienvenido al centro de control de Bnbria.</p>
    </div>

    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
      <div class="col">
        <router-link to="/admin/users" class="text-decoration-none">
          <div class="card h-100 border-0 shadow-sm text-center p-4 hover-card">
            <div class="fs-1 mb-3">👥</div>
            <h5 class="fw-bold text-dark">Usuarios</h5>
            <p class="text-muted small">Gestionar cuentas, activar y desactivar usuarios.</p>

            <div v-if="stats" class="stats-group mt-3 justify-content-center">
              <span class="badge stats-badge bg-primary"
                >Usuarios activos: {{ stats.totalUsers }}</span
              >
            </div>
          </div>
        </router-link>
      </div>

      <div class="col">
        <router-link to="/admin/properties" class="text-decoration-none">
          <div class="card h-100 border-0 shadow-sm text-center p-4 hover-card">
            <div class="fs-1 mb-3">🏠</div>
            <h5 class="fw-bold text-dark">Propiedades</h5>
            <p class="text-muted small">Aprobar inmuebles pendientes y revisar listado.</p>

            <div v-if="stats" class="stats-group mt-3 justify-content-center">
              <span class="badge stats-badge bg-primary">Total: {{ stats.totalProperties }}</span>
              <span class="badge stats-badge bg-success"
                >Aprobadas: {{ stats.approvedProperties }}</span
              >
              <span class="badge stats-badge bg-secondary"
                >Pendientes: {{ stats.pendingProperties }}</span
              >
            </div>
          </div>
        </router-link>
      </div>

      <div class="col">
        <router-link to="/admin/bookings" class="text-decoration-none">
          <div class="card h-100 border-0 shadow-sm text-center p-4 hover-card">
            <div class="fs-1 mb-3">📅</div>
            <h5 class="fw-bold text-dark">Reservas</h5>
            <p class="text-muted small">Consultar reservas activas y cancelaciones.</p>

            <div v-if="stats" class="stats-group mt-3 justify-content-center">
              <span class="badge stats-badge bg-primary">Total: {{ stats.totalBookings }}</span>
              <span class="badge stats-badge bg-success"
                >Completadas: {{ stats.completedBookings }}</span
              >
            </div>
          </div>
        </router-link>
      </div>

      <div class="col">
        <router-link to="/admin/reviews" class="text-decoration-none">
          <div class="card h-100 border-0 shadow-sm text-center p-4 hover-card">
            <div class="fs-1 mb-3">⭐</div>
            <h5 class="fw-bold text-dark">Reseñas</h5>
            <p class="text-muted small">Moderar comentarios de usuarios y propiedades.</p>

            <div v-if="stats" class="stats-group mt-3 justify-content-center">
              <span class="badge stats-badge bg-primary"
                >Reseñas de usuarios: {{ stats.totalUserReviews }}</span
              >
              <span class="badge stats-badge bg-primary"
                >Reseñas de propiedades: {{ stats.totalPropertyReviews }}</span
              >
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import auth from "@/common/auth.js";

export default {
  name: "AdminDashboard",
  data() {
    return {
      stats: null
    };
  },
  async mounted() {
    try {
      this.stats = await auth.getAppStats();
    } catch (error) {
      console.error("Error al obtener estadísticas de la aplicación:", error);
    }
  }
};
</script>

<style scoped>
/* Efecto al pasar el ratón */
.hover-card {
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease,
    border-color 0.25s ease,
    border-width 0.25s ease;
  cursor: pointer;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.hover-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.12);
  border: 2px solid rgba(13, 110, 253, 0.9);
}

/* Estadísticas: ancho, espaciado y tamaño de badge */
.stats-group {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  align-items: center;
}

.stats-badge {
  font-size: 0.85rem;
  padding: 0.45rem 0.65rem;
  border-radius: 999px;
}
</style>
