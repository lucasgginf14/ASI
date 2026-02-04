<template>
  <div class="note-list-container">
    <div class="note-list-header">
      <h1>Lista de notas</h1>
      <button v-if="!auth.isAdmin()" class="btn-create" @click="crearNota">Crear nueva nota</button>
      <div class="note-list-info">
        <h2>
          Notas mostradas: <span class="note-count">{{ notes.length }}</span>
        </h2>
        <h2>
          Filtro categoría:
          <span class="category-filter" v-if="category === null">Ninguno</span>
          <span class="category-filter" v-else>{{ category }}</span>
        </h2>
        <h2>
          Filtro login:
          <span class="login-filter" v-if="login === null">Ninguno</span>
          <span class="login-filter" v-else>{{ login }}</span>
        </h2>
      </div>
      <div class="note-list-actions">
        <button
          class="btn-filter"
          @click="
            category = null;
            this.$router.replace({ query: {} });
          "
        >
          Borrar filtro categoría
        </button>
        <button
          class="btn-filter"
          @click="
            login = null;
            this.$router.replace({ query: {} });
          "
        >
          Borrar filtro login
        </button>
        <button
          class="btn-archived"
          v-if="!auth.isAdmin() && !showArchived"
          @click="showArchived = true"
        >
          Mostrar archivadas
        </button>
        <button
          class="btn-archived"
          v-if="!auth.isAdmin() && showArchived"
          @click="showArchived = false"
        >
          Ocultar archivadas
        </button>
      </div>
      <div class="note-list-actions" style="flex-wrap: wrap">
        <div v-if="auth.isAdmin()" style="display: flex; gap: 0.5rem; align-items: center">
          <label>Desde: <input type="date" v-model="dateFrom" /></label>
          <label>Hasta: <input type="date" v-model="dateTo" /></label>
          <button class="btn-filter" @click="filtrarPorFechas">Filtrar por fechas</button>
          <button class="btn-filter" @click="limpiarFechas">Limpiar fechas</button>
        </div>
      </div>
    </div>
    <div class="note-list-grid">
      <div class="note-card-col" v-for="note in notes" :key="note.id">
        <NoteCard
          :key="note.id"
          :note="note"
          @category-selected="filtrarPorCategoria"
          @login-selected="filtrarPorUsuario"
          @archived="archivarNota"
          @deleted="borrarNota"
        ></NoteCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.note-list-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 2rem;
  background: #f8f9fa;
  border-radius: 16px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.08);
}

.note-list-header {
  margin-bottom: 2rem;
  text-align: center;
}

.btn-create {
  background: #007bff;
  color: #fff;
  border: none;
  padding: 0.7rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-create:hover {
  background: #0056b3;
}

.note-list-info {
  margin-bottom: 1rem;
}
.note-count {
  font-weight: bold;
  color: #007bff;
}
.category-filter {
  font-weight: bold;
  color: #28a745;
}

.note-list-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
}
.btn-filter,
.btn-archived {
  background: #e2e6ea;
  color: #333;
  border: none;
  padding: 0.5rem 1.2rem;
  border-radius: 6px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-filter:hover,
.btn-archived:hover {
  background: #ced4da;
}

.note-list-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 1.5rem;
}
.note-card-col {
  display: flex;
  justify-content: center;
}
</style>

<script>
import NoteRepository from "@/repositories/NoteRepository";
import NoteCard from "./NoteCard.vue";
import auth from "@/common/auth.js";

export default {
  computed: {
    auth() {
      return auth;
    }
  },
  data() {
    return {
      notes: [],
      category: null,
      login: null,
      showArchived: false,
      dateFrom: null,
      dateTo: null
    };
  },
  components: { NoteCard },
  async mounted() {
    if (this.auth.isAdmin()) {
      this.showArchived = true;
    }

    if (this.$route.query.login) {
      this.login = this.$route.query.login;
    }
    if (this.$route.query.category) {
      this.category = this.$route.query.category;
    }

    this.notes = await NoteRepository.findAll();
    this.notes = this.notes.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
    if (!auth.isAdmin()) {
      this.notes = this.notes.filter((note) => !note.archived);
    }
    if (auth.isAdmin() && this.$route.query.login) {
      this.notes = this.notes.filter((nota) => nota.owner === this.login);
    }
    if (!auth.isAdmin() && this.$route.query.category) {
      this.notes = this.notes.filter((nota) =>
        nota.categories.some((cat) => cat.name === this.category)
      );
    }
  },
  watch: {
    async category(newCategory) {
      if (newCategory != null) {
        this.notes = (await NoteRepository.findAll()).filter((nota) =>
          nota.categories.some((cat) => cat.name === newCategory)
        );
      } else {
        this.notes = await NoteRepository.findAll();
      }
      if (!this.showArchived) {
        this.notes = this.notes.filter((nota) => !nota.archived);
      }
      this.notes = this.notes.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
      this.filtrarPorFechas();
    },
    async login(newLogin) {
      if (newLogin != null) {
        this.notes = (await NoteRepository.findAll()).filter((nota) => nota.owner === newLogin);
      } else {
        this.notes = await NoteRepository.findAll();
      }
      if (!this.showArchived) {
        this.notes = this.notes.filter((nota) => !nota.archived);
      }
      this.notes = this.notes.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
      this.filtrarPorFechas();
    },
    async showArchived(newArchived) {
      if (newArchived) {
        this.notes = await NoteRepository.findAll();
      } else {
        this.notes = (await NoteRepository.findAll()).filter((nota) => !nota.archived);
      }
      if (this.category != null) {
        this.notes = this.notes.filter((nota) =>
          nota.categories.some((cat) => cat.name === this.category)
        );
      }
      this.notes = this.notes.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
      this.filtrarPorFechas();
    }
  },
  methods: {
    crearNota() {
      this.$router.push("notes/create");
    },
    filtrarPorCategoria(categoria) {
      this.category = categoria;
    },
    filtrarPorUsuario(login) {
      this.login = login;
    },
    archivarNota(nota) {
      if (!this.showArchived) {
        this.notes = this.notes.filter((n) => n.id !== nota.id);
      }
    },
    borrarNota(nota) {
      this.notes = this.notes.filter((n) => n.id !== nota.id);
    },
    filtrarPorFechas() {
      this.filtrarNotasPorFechas();
    },
    limpiarFechas() {
      this.dateFrom = null;
      this.dateTo = null;
      this.filtrarNotasPorFechas();
    },
    async filtrarNotasPorFechas() {
      let notas = await NoteRepository.findAll();
      if (this.dateFrom) {
        const from = new Date(this.dateFrom);
        notas = notas.filter((n) => new Date(n.timestamp) >= from);
      }
      if (this.dateTo) {
        const to = new Date(this.dateTo);
        notas = notas.filter((n) => new Date(n.timestamp) <= to);
      }
      if (this.category != null) {
        notas = notas.filter((nota) => nota.categories.some((cat) => cat.name === this.category));
      }
      if (this.login != null) {
        notas = notas.filter((nota) => nota.owner === this.login);
      }
      if (!this.showArchived) {
        notas = notas.filter((nota) => !nota.archived);
      }
      this.notes = notas.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
    }
  }
};
</script>
