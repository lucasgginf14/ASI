<template>
  <div class="card" style="width: 18rem">
    <div
      v-if="auth.isAdmin()"
      class="card-header"
      @click="onLoginClick(note.owner)"
      style="cursor: pointer"
    >
      {{ note.owner }}
    </div>
    <div class="card-body">
      <h5 class="card-title">
        {{ note.title }}
      </h5>
      <h6 class="card-subtitle mb-2 text-muted">
        <router-link
          :to="{ name: 'NoteDetail', params: { noteId: note.id } }"
          style="cursor: pointer"
        >
          {{ note.timestamp.toLocaleDateString() }} - {{ note.timestamp.toLocaleTimeString() }}
        </router-link>
      </h6>
      <p class="card-text">
        {{ note.content }}
      </p>
      <p v-if="auth.isAdmin()" class="card-text">
        {{ note.archived ? "Archivada" : "No archivada" }}
      </p>
      <p></p>
    </div>
    <div v-if="categoriesAsString" class="card-footer">
      <span
        v-for="cat in note.categories"
        :key="cat.id"
        class="category"
        style="cursor: pointer; margin-right: 5px"
        @click="onCategoryClick(cat.name)"
      >
        {{ cat.name }}
      </span>
    </div>

    <button v-if="!auth.isAdmin() && !note.archived" @click="toggleArchive">Archivar</button>
    <button v-if="!auth.isAdmin() && note.archived" @click="toggleArchive">Desarchivar</button>
    <button v-if="!auth.isAdmin()" @click="modificarNota">Modificar</button>
    <button v-if="!auth.isAdmin()" @click="eliminarNota">Eliminar</button>
    <button v-if="auth.isAdmin()" @click="reasignarNota">Reasignar nota</button>
  </div>
</template>

<script>
import NoteFunctions from "../../common/note.js";
import auth from "@/common/auth.js";

export default {
  props: {
    note: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      localNote: { ...this.note },
      users: []
    };
  },
  computed: {
    auth() {
      return auth;
    },
    categoriesAsString() {
      return this.note?.categories.map((t) => t.name).join(", a las ");
    }
  },
  methods: {
    onCategoryClick(category) {
      this.$emit("category-selected", category);
    },
    onLoginClick(login) {
      this.$emit("login-selected", login);
    },
    async toggleArchive() {
      try {
        await NoteFunctions.archive(this.note);
        if (this.note.archived) {
          this.$emit("archived", this.note);
        }
      } catch (error) {
        console.log(error);
      }
    },
    modificarNota() {
      this.$router.push("notes/" + this.note.id + "/edit");
    },
    eliminarNota() {
      if (confirm("¿Estás seguro de que deseas eliminar esta nota?")) {
        NoteFunctions.deleteNote(this.note.id)
          .then(() => {
            this.$emit("deleted", this.note);
            this.$router.push("/notes");
          })
          .catch((error) => {
            console.log(error);
          });
      }
    },
    reasignarNota() {
      this.$router.push("/notes/" + this.note.id + "/edit");
    }
  }
};
</script>
