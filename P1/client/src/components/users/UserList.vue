<template>
  <div class="user-list-container">
    <div class="user-list-header">
      <h1>Lista de usuarios</h1>
    </div>
    <div class="user-list-grid">
      <div class="user-card-col" v-for="user in users" :key="user.id">
        <UserCard :user="user" />
        <div class="user-notes-total">
          <span class="notes-label">Total notas:</span>
          <span class="notes-badge">
            {{ notes.filter((note) => note.owner === user.login).length }}
          </span>
        </div>
        <button @click="deleteUser(user.id)">Borrar</button>
        <button v-if="user.active" @click="changeActive(user)">Desactivar</button>
        <button v-else @click="changeActive(user)">Activar</button>
      </div>
    </div>
  </div>
</template>

<style>
.user-list-container {
  background: #f9f9f9;
  padding: 32px;
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(66, 185, 131, 0.08);
  max-width: 900px;
  margin: 32px auto;
}

.user-list-header h1 {
  color: #2c3e50;
  font-size: 2.2em;
  margin-bottom: 24px;
  letter-spacing: 1px;
}

.user-list-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: flex-start;
}

.user-card-col {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(44, 62, 80, 0.07);
  padding: 20px 18px 14px 18px;
  min-width: 260px;
  max-width: 300px;
  flex: 1 1 260px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: box-shadow 0.2s;
}
.user-card-col:hover {
  box-shadow: 0 6px 24px rgba(66, 185, 131, 0.18);
}

.user-notes-total {
  margin-top: 12px;
  display: flex;
  align-items: center;
  background: #eafaf3;
  border-radius: 8px;
  padding: 4px 10px;
}

.notes-label {
  font-weight: 600;
  margin-right: 6px;
  color: #2c3e50;
}

.notes-badge {
  background: #42b983;
  color: white;
  border-radius: 12px;
  padding: 2px 10px;
  font-size: 1em;
  font-weight: bold;
  box-shadow: 0 1px 4px rgba(66, 185, 131, 0.15);
}
</style>

<script>
import UserCard from "@/components/users/UserCard.vue";
import UserRepository from "@/repositories/UserRepository.js";
import NoteRepository from "@/repositories/NoteRepository.js";

export default {
  components: { UserCard },
  data() {
    return {
      users: [],
      notes: []
    };
  },
  async mounted() {
    this.users = await UserRepository.findAll();
    this.notes = await NoteRepository.findAll();
  },
  methods: {
    async deleteUser(userId) {
      if (confirm("Are you sure you want to delete this user?")) {
        await UserRepository.delete(userId);
        this.users = this.users.filter((user) => user.id !== userId);
      }
    },
    async changeActive(user) {
      if (user.active) {
        await UserRepository.deactivate(user.id);
      } else {
        await UserRepository.activate(user.id);
      }
      user.active = !user.active;
    }
  }
};
</script>
