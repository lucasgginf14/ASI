<template>
  <div class="container mt-5 mb-5">
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-5 gap-3">
      <h2 class="fw-bold mb-0 text-dark">Gestión de Usuarios</h2>

      <div class="search-bar shadow-sm">
        <i class="bi bi-search search-icon"></i>
        <input v-model="search" type="text" placeholder="Buscar por email..." />
      </div>
    </div>

    <div class="d-flex gap-2 mb-4 overflow-auto pb-2">
      <button @click="filter = 'all'" class="filter-pill" :class="{ active: filter === 'all' }">
        Todos
      </button>
      <button
        @click="filter = 'active'"
        class="filter-pill"
        :class="{ active: filter === 'active' }"
      >
        ✅ Activos
      </button>
      <button
        @click="filter = 'banned'"
        class="filter-pill"
        :class="{ active: filter === 'banned' }"
      >
        🚫 Bloqueados
      </button>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div
      v-else-if="filteredUsers.length > 0"
      class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4"
    >
      <div class="col" v-for="user in filteredUsers" :key="user.id">
        <AdminUserCard :user="user" />
      </div>
    </div>

    <div v-else class="text-center py-5 bg-light rounded-4 border border-dashed">
      <div class="py-4">
        <i class="bi bi-search display-1 text-muted opacity-25 mb-3 d-block"></i>
        <p class="text-muted mb-0 fs-5" v-if="search">
          No encontramos a nadie con "<strong>{{ search }}</strong
          >"
        </p>
        <p class="text-muted mb-0 fs-5" v-else>No hay usuarios registrados.</p>
      </div>
    </div>
  </div>
</template>

<script>
import UserRepository from "@/repositories/UserRepository";
import AdminUserCard from "./AdminUserCard.vue";

export default {
  components: { AdminUserCard },
  data() {
    return {
      users: [],
      filter: "all",
      search: "",
      loading: true
    };
  },
  computed: {
    filteredUsers() {
      let result = this.users;

      // 1. Filtro Estado
      if (this.filter === "active") {
        result = result.filter((u) => u.active);
      } else if (this.filter === "banned") {
        result = result.filter((u) => !u.active);
      }

      // 2. Filtro Buscador
      if (this.search) {
        const term = this.search.toLowerCase();
        result = result.filter((u) => u.email && u.email.toLowerCase().includes(term));
      }

      return result;
    }
  },
  async mounted() {
    await this.loadUsers();
  },
  methods: {
    async loadUsers() {
      this.loading = true;
      try {
        this.users = await UserRepository.getAllUsersAdmin();
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
/* 👇 ESTILOS DE LA BARRA DE BÚSQUEDA "PRETTY" */
.search-bar {
  display: flex;
  align-items: center;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 50px; /* Forma de píldora */
  padding: 10px 20px;
  width: 100%;
  max-width: 320px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.search-bar:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
}

.search-bar:focus-within {
  border-color: #222; /* Borde oscuro al escribir */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12) !important;
  transform: translateY(-1px);
}

.search-icon {
  font-size: 1.1rem;
  color: #666;
  margin-right: 12px;
}

.search-bar input {
  border: none;
  outline: none;
  width: 100%;
  font-size: 0.95rem;
  color: #333;
  background: transparent;
}

.search-bar input::placeholder {
  color: #999;
}

/* 👇 ESTILOS DE LOS BOTONES DE FILTRO (Pills) */
.filter-pill {
  border: 1px solid #e0e0e0;
  background: white;
  color: #555;
  padding: 8px 20px;
  border-radius: 50px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-pill:hover {
  border-color: #000;
  color: #000;
}

.filter-pill.active {
  background: #000;
  color: white;
  border-color: #000;
}
</style>
