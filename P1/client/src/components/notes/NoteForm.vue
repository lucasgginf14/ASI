<template>
  <div class="container">
    <h1>{{ isEditMode ? "Editar nota" : "Crear nueva nota" }}</h1>
    <form @submit.prevent="guardarNota">
      <div class="mb-3">
        <label for="title" class="form-label">Título (máx. 300 caracteres)</label>
        <input
          type="text"
          class="form-control"
          id="title"
          v-model="note.title"
          maxlength="300"
          :readonly="auth.isAdmin()"
        />
      </div>
      <div class="mb-3">
        <label for="content" class="form-label">Contenido</label>
        <textarea
          class="form-control"
          id="content"
          v-model="note.content"
          rows="5"
          :readonly="auth.isAdmin()"
        ></textarea>
      </div>

      <label for="categories" class="form-label">Categorías</label>
      <div v-if="categories.length">
        <button
          v-if="!auth.isAdmin()"
          class="btn btn-secondary mb-3 dropdown-toggle"
          type="button"
          id="dropdownCategorias"
          data-bs-toggle="dropdown"
          aria-expanded="false"
        >
          Añadir categorías
        </button>
        <ul class="dropdown-menu" aria-labelledby="dropdownCategorias">
          <li v-for="cat in categories" :key="cat.id">
            <a
              class="dropdown-item"
              href="#"
              @click.prevent="toggleCategoria(cat.id)"
              :class="{ active: note.categories.some((c) => c.id === cat.id) }"
            >
              {{ cat.name }}
            </a>
          </li>
        </ul>
      </div>
      <div class="mb-2">
        <span v-if="!auth.isAdmin() && note.categories.length">Seleccionadas: </span>
        <span v-for="catObj in note.categories" :key="catObj.id" class="badge bg-primary me-1">
          {{ categories.find((c) => c.id === catObj.id)?.name }}
        </span>
      </div>

      <div v-if="auth.isAdmin()" class="mb-2">
        <label for="owner" class="form-label">Usuario</label>
        <select id="owner" class="form-select mb-2" v-model="newUser">
          <option v-for="user in users" :key="user.id" :value="user">
            {{ user.login }}
          </option>
        </select>
      </div>

      <button v-if="!auth.isAdmin()" type="submit" class="btn btn-primary">
        {{ isEditMode ? "Guardar cambios" : "Guardar" }}
      </button>
      <button v-if="auth.isAdmin()" type="button" class="btn btn-primary" @click="changeOwner">
        Guardar
      </button>
      <button type="button" class="btn btn-secondary ms-2" @click="cancelar">Cancelar</button>
    </form>
  </div>
</template>

<script>
import NoteRepository from "@/repositories/NoteRepository";
import CategoryRepository from "@/repositories/CategoryRepository";
import auth from "@/common/auth.js";
import UserRepository from "@/repositories/UserRepository";

export default {
  data() {
    return {
      note: {
        id: null,
        title: null,
        content: null,
        owner: null,
        categories: []
      },
      categories: [],
      users: [],
      newUser: null
    };
  },
  computed: {
    auth() {
      return auth;
    },
    isEditMode() {
      return !!this.$route.params.noteId;
    }
  },
  async mounted() {
    this.categories = await CategoryRepository.findAll();

    if (this.isEditMode) {
      this.note = await NoteRepository.findOne(this.$route.params.noteId);
      if (auth.isAdmin()) {
        this.users = await UserRepository.findAll();
        this.newUser = this.users.find((u) => u.login === this.note.owner);
      }
    }
  },
  methods: {
    toggleCategoria(catId) {
      const idx = this.note.categories.findIndex((c) => c.id === catId);
      if (idx === -1) {
        this.note.categories.push({ id: catId });
      } else {
        this.note.categories.splice(idx, 1);
      }
    },
    async guardarNota() {
      if (!this.note.title && !this.note.content) {
        alert("Es necesario rellenar al menos el título o el contenido");
        return;
      }

      try {
        if (this.isEditMode) {
          await NoteRepository.update(this.note);
          this.$router.push("/notes/" + this.note.id);
        } else {
          const response = await NoteRepository.create(this.note);
          this.$router.push("/notes/" + response.id);
        }
      } catch (error) {
        console.error(error);
        alert(error.response?.data?.message || error.message);
      }
    },
    async changeOwner() {
      try {
        await NoteRepository.changeOwner(this.note.id, this.newUser.id);
        this.$router.push("/notes");
      } catch (error) {
        console.error(error);
        alert(error.response?.data?.message || error.message);
      }
    },
    cancelar() {
      if (this.isEditMode) {
        if (this.auth.isAdmin()) {
          this.$router.push("/notes");
        } else {
          this.$router.push("/notes/" + this.$route.params.noteId);
        }
      } else {
        this.$router.push("/notes");
      }
    }
  }
};
</script>
