<template>
  <router-link
    :to="`/admin/users/${user.id}`"
    class="user-card"
    :class="{ inactive: !user.active }"
    style="text-decoration: none; color: inherit; cursor: pointer"
  >
    <div class="card-header">
      <div class="avatar">
        {{ user.id ?? "U" }}
      </div>
      <div>
        <div class="status-row">
          <span class="status-dot" :class="user.active ? 'active' : 'inactive'"></span>
          <span class="status-text">{{ user.active ? "Activo" : "Inactivo" }}</span>
        </div>
        <span class="role-badge" v-if="user.authority === 'ADMIN'">ADMIN</span>
      </div>
    </div>

    <div class="card-body">
      <p class="user-email" :title="user.email">{{ user.email }}</p>

      <p class="date">Fecha de creación: {{ formatDate(user.creationDate) }}</p>
    </div>
  </router-link>
</template>

<script>
export default {
  props: {
    user: { type: Object, required: true }
  },
  methods: {
    formatDate(date) {
      if (!date) return "-";
      return new Date(date).toLocaleDateString();
    }
  }
};
</script>

<style scoped>
.user-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  overflow: hidden;
}

.user-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-card.inactive {
  background: #f9f9f9;
  opacity: 0.8;
}

.card-header {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.avatar {
  width: 45px;
  height: 45px;
  background: #007bff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
}

.role-badge {
  background: #6f42c1;
  color: white;
  font-size: 10px;
  padding: 3px 6px;
  border-radius: 4px;
  font-weight: bold;
}

.card-body {
  padding: 0 15px 15px;
  flex: 1;
}

.user-email {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 5px;
  font-size: 0.85rem;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.status-dot.active {
  background: #28a745;
}
.status-dot.inactive {
  background: #dc3545;
}

.date {
  font-size: 0.75rem;
  color: #999;
  margin-top: auto;
}
</style>
