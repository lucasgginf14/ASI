<template>
  <div v-if="note != null">
    <div v-if="auth.isAdmin()" class="card-header">Usuario: {{ note.owner }}</div>

    <div v-if="note.title">
      <span>Título: </span>
      <span>{{ note.title }}</span>
    </div>
    <div v-if="note.content">
      <span>Contenido: </span>
      <span>{{ note.content }}</span>
    </div>
    <div v-if="note.categories.length > 0">
      <span>Categorías: </span>
      <p>
        <span>{{ categoriesAsString }}</span>
      </p>
    </div>
    <div>
      <span>Fecha: </span>
      <span
        >{{ note.timestamp.toLocaleDateString() }}, a las
        {{ note.timestamp.toLocaleTimeString() }}</span
      >
    </div>
    <div>
      <button @click="$router.push('/notes')">Volver a la lista</button>
      <button v-if="!auth.isAdmin() && !note.archived" @click="toggleArchive">Archivar</button>
      <button v-if="!auth.isAdmin() && note.archived" @click="toggleArchive">Desarchivar</button>
      <button v-if="!auth.isAdmin()" @click="modificarNota">Modificar</button>
      <button v-if="!auth.isAdmin()" @click="eliminarNota">Eliminar</button>
    </div>
  </div>
</template>

<script>
import NoteRepository from "@/repositories/NoteRepository";
import NoteFunctions from "../../common/note.js";
import auth from "@/common/auth.js";

export default {
  data() {
    return {
      note: null
    };
  },
  computed: {
    auth() {
      return auth;
    },
    categoriesAsString() {
      return this.note.categories
        .map(function (cat) {
          return cat.name;
        })
        .join(", ");
    }
  },
  async mounted() {
    this.note = await NoteRepository.findOne(this.$route.params.noteId);
  },
  methods: {
    async toggleArchive() {
      try {
        await NoteFunctions.archive(this.note);
      } catch (error) {
        console.log(error);
      }
    },
    modificarNota() {
      this.$router.push(`/notes/${this.note.id}/edit`);
    },
    eliminarNota() {
      if (confirm("¿Estás seguro de que deseas eliminar esta nota?")) {
        NoteFunctions.deleteNote(this.note.id)
          .then(() => {
            this.$emit("deleted", this.note);
            this.$router.push("/notes");
          })
          .catch((error) => {
            console.error(error);
            if (error.response?.data?.message) {
              alert(error.response.data.message);
            } else {
              alert(error.message);
            }
          });
      }
    }
  }
};
</script>
