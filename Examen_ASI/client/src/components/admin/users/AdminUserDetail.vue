<template>
  <div class="detail-container">
    <div class="back-link">
      <back-button />
    </div>
    <div v-if="loading" class="loading">Cargando perfil...</div>

    <div v-else-if="user" class="detail-card">
      <div class="detail-header">
        <div class="avatar-large">
          {{ user.id }}
        </div>
        <div class="header-info">
          <h1 class="user-name">{{ user.name }} {{ user.surname }} {{ user.surname2 ?? "" }}</h1>
          <p class="email">{{ user.email }}</p>
          <div class="badges">
            <span class="badge role" v-if="user.authority === 'ADMIN'">Administrador</span>
            <span class="badge status" :class="user.active ? 'active' : 'inactive'">
              {{ user.active ? "Activo" : "Inactivo" }}
            </span>
          </div>
        </div>
      </div>

      <hr />

      <div class="info-section">
        <h3>Información</h3>
        <p v-if="user.phone"><strong>Teléfono: </strong> {{ user.phone }}</p>
        <p v-if="user.birthday">
          <strong>Fecha de nacimiento: </strong> {{ formatDate(user.birthday) }}
        </p>
        <p><strong>Cuenta creada el día:</strong> {{ formatDate(user.creationDate) }}</p>
      </div>

      <div class="actions-section">
        <h3>Acciones de Administrador</h3>
        <p v-if="user.active" class="warning-text">
          Al desactivar al usuario, este no podrá iniciar sesión.
        </p>
        <p v-else class="info-text">Al activar al usuario, este podrá volver a iniciar sesión.</p>

        <button v-if="user.active" @click="toggleActive(false)" class="btn-danger">
          🚫 Desactivar Usuario
        </button>

        <button v-else @click="toggleActive(true)" class="btn-success">✅ Activar Usuario</button>
      </div>

      <div v-if="user.properties && user.properties.length" class="properties-section mb-2">
        <h3>Propiedades</h3>
        <ul class="properties-list">
          <li v-for="property in user.properties" :key="property.id" class="property-item">
            <router-link :to="`/admin/properties/${property.id}`" class="property-card">
              <div class="property-left">
                <div class="property-main">
                  <strong class="property-title">{{ property.title }}</strong>
                  <div class="property-address">
                    {{ property.city }}, {{ property.province }}, {{ property.country }}
                  </div>
                </div>
              </div>

              <div class="property-right">
                <span
                  class="property-state badge small"
                  :style="{
                    background:
                      { APPROVED: '#d4edda', PENDING: '#fff3cd', REJECTED: '#f8d7da' }[
                        property.state
                      ] || '#f1f3f5',
                    color:
                      { APPROVED: '#155724', PENDING: '#856404', REJECTED: '#721c24' }[
                        property.state
                      ] || '#343a40'
                  }"
                >
                  {{
                    { APPROVED: "✅ Aceptada", PENDING: "⏳ Pendiente", REJECTED: "❌ Rechazada" }[
                      property.state
                    ] || property.state
                  }}
                </span>
                <span class="see-more">Ver detalles →</span>
              </div>
            </router-link>
          </li>
        </ul>
      </div>

      <div v-if="user.bookings && user.bookings.length" class="bookings-section">
        <h3>Reservas</h3>
        <ul class="bookings-list">
          <li v-for="booking in user.bookings" :key="booking.id" class="booking-item">
            <router-link :to="`/admin/bookings/${booking.id}`" class="booking-card">
              <div class="booking-left">
                <strong class="booking-title">{{ booking.propertyTitle }}</strong>
                <div class="booking-dates">
                  {{ formatDate(booking.startDate) }} → {{ formatDate(booking.endDate) }}
                </div>
              </div>

              <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 6px">
                <span
                  class="booking-state small"
                  :style="{
                    background:
                      {
                        PENDING: '#fff3cd',
                        CONFIRMED: '#d4edda',
                        CANCELED_BY_GUEST: '#f8d7da',
                        CANCELED_BY_HOST: '#f8d7da',
                        CANCELED_BY_ADMIN: '#f8d7da',
                        CANCELED_BY_SYSTEM: '#e2e3e5',
                        IN_PROGRESS: '#cfe2ff',
                        COMPLETED: '#d1ecf1'
                      }[booking.state] || '#f1f3f5',
                    color:
                      {
                        PENDING: '#856404',
                        CONFIRMED: '#155724',
                        CANCELED_BY_GUEST: '#721c24',
                        CANCELED_BY_HOST: '#721c24',
                        CANCELED_BY_ADMIN: '#721c24',
                        CANCELED_BY_SYSTEM: '#495057',
                        IN_PROGRESS: '#084298',
                        COMPLETED: '#0c5460'
                      }[booking.state] || '#343a40'
                  }"
                >
                  {{
                    {
                      PENDING: "⏳",
                      CONFIRMED: "✅",
                      CANCELED_BY_GUEST: "🧑‍💼",
                      CANCELED_BY_HOST: "🏠",
                      CANCELED_BY_ADMIN: "🛠️",
                      CANCELED_BY_SYSTEM: "⚙️",
                      IN_PROGRESS: "🔄",
                      COMPLETED: "🏁"
                    }[booking.state] || "ℹ️"
                  }}
                  {{
                    {
                      PENDING: "Pendiente",
                      CONFIRMED: "Confirmada",
                      CANCELED_BY_GUEST: "Cancelada (huésped)",
                      CANCELED_BY_HOST: "Cancelada (anfitrión)",
                      CANCELED_BY_ADMIN: "Cancelada (admin)",
                      CANCELED_BY_SYSTEM: "Cancelada (sistema)",
                      IN_PROGRESS: "En curso",
                      COMPLETED: "Completada"
                    }[booking.state] || booking.state
                  }}
                </span>
                <span class="see-more">Ver detalles →</span>
              </div>
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import UserRepository from "@/repositories/UserRepository";
import BackButton from "@/common/utils/BackButton.vue";

export default {
  components: { BackButton },
  data() {
    return {
      user: null,
      loading: true
    };
  },
  computed: {
    initials() {
      if (!this.user) return "U";
      const parts = `${this.user.name || ""} ${this.user.surname || ""}`.trim().split(/\s+/);
      const chars = parts.map((p) => p.charAt(0)).filter(Boolean);
      return (chars[0] || "U").toUpperCase() + (chars[1] || "").toUpperCase();
    }
  },
  async mounted() {
    await this.loadUser();
  },
  methods: {
    async loadUser() {
      this.loading = true;
      // Cogemos el ID numérico de la URL
      const id = this.$route.params.id;
      try {
        this.user = await UserRepository.getAdminUserDetail(id);
      } catch (error) {
        console.error(error);
        alert("Error al cargar el usuario");
        this.$router.push("/admin/users");
      } finally {
        this.loading = false;
      }
    },
    async toggleActive(shouldActivate) {
      if (this.user.authority === "ADMIN") {
        alert("No puedes modificar a otro administrador.");
        return;
      }

      const action = shouldActivate ? "activar" : "desactivar";
      if (!confirm(`¿Estás seguro de que quieres ${action} a este usuario?`)) {
        return;
      }

      try {
        if (shouldActivate) {
          await UserRepository.activateUser(this.user.id);
        } else {
          await UserRepository.deactivateUser(this.user.id);
        }
        // Recargar datos para ver el cambio reflejado
        await this.loadUser();
      } catch (error) {
        console.error(error);
        alert("Error al cambiar el estado");
      }
    },
    formatDate(date) {
      if (!date) return "-";
      return new Date(date).toLocaleDateString();
    }
  }
};
</script>

<style scoped>
.detail-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 0 20px;
}

.detail-card {
  background: white;
  border: 1px solid #e6eef8;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 6px 18px rgba(11, 94, 215, 0.06);
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.avatar-large {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #0d6efd, #6610f2);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
  box-shadow: 0 6px 18px rgba(13, 110, 253, 0.12);
  flex-shrink: 0;
}

.header-info {
  flex: 1;
  min-width: 0;
}
.user-name {
  margin: 0 0 5px;
  font-size: 22px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.email {
  color: #666;
  margin: 0 0 10px;
  font-size: 14px;
}

.badges {
  display: flex;
  gap: 10px;
}
.badge {
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  color: white;
}
.badge.small {
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 12px;
}
.badge.role {
  background: #6f42c1;
}
.badge.status.active {
  background: #28a745;
}
.badge.status.inactive {
  background: #dc3545;
}

.info-section,
.actions-section {
  margin: 25px 0;
}
.actions-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.btn-danger,
.btn-success {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  margin-top: 10px;
}
.btn-danger {
  background: #dc3545;
}
.btn-success {
  background: #28a745;
}
.warning-text {
  color: #856404;
}
.info-text {
  color: #0c5460;
}

.back-link {
  margin-top: 20px;
}
.back-link a {
  text-decoration: none;
  color: #007bff;
  font-weight: bold;
}

/* Eliminar puntos y espacio por defecto de las listas */
.bookings-list,
.properties-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* Ajuste de items */
.booking-item,
.property-item {
  margin: 0 0 10px 0;
}

/* Tarjetas de lista más agradables */
.property-card,
.booking-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #eaf1fb;
  color: #0b5ed7;
  text-decoration: none;
  margin-bottom: 10px;
  transition:
    box-shadow 0.18s ease,
    transform 0.12s ease;
  gap: 12px;
}

.property-card:hover,
.booking-card:hover {
  box-shadow: 0 6px 18px rgba(11, 94, 215, 0.08);
  transform: translateY(-3px);
  text-decoration: none;
}

/* Left area should shrink gracefully */
.property-left,
.booking-left {
  display: flex;
  gap: 12px;
  align-items: center;
  min-width: 0;
  flex: 1;
}

/* Right area */
.property-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  flex-shrink: 0;
  margin-left: 12px;
}

/* Títulos con ellipsis efectivos */
.property-title {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 420px;
  font-weight: 700;
  color: #0b5ed7;
  font-size: 15px;
}

.property-address {
  font-size: 12px;
  color: #6c757d;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 360px;
}

.booking-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 420px;
  font-weight: 700;
  color: #0b5ed7;
}

.booking-dates {
  font-size: 12px;
  color: #6c757d;
}

/* Badge / pill styles for states */
.property-state {
  background: #6c757d;
  color: #fff;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.booking-state {
  background: #f1f3f5;
  color: #343a40;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.see-more {
  font-size: 12px;
  color: #6c757d;
}
</style>
