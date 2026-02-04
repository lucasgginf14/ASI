<template>
  <div>{{ editMode ? "Editar categoría" : "Crear Categoría" }}</div>
  <form @submit.prevent="guardarNota">
    <label for="name">Nombre:</label>
    <input id="name" v-model="name" type="text" required />
    <button type="submit">{{ editMode ? "Guardar cambios" : "Guardar" }}</button>
    <button v-if="editMode" type="button" @click="this.$router.push({ name: CategoryList })">
      Cancelar
    </button>
  </form>
</template>

<script>
import CategoryRepository from "@/repositories/CategoryRepository.js";
import CategoryList from "@/components/categories/CategoryList.vue";

export default {
  data() {
    return {
      name: null
    };
  },
  computed: {
    CategoryList() {
      return CategoryList;
    },
    editMode() {
      return !!this.$route.params.categoryId;
    }
  },
  async mounted() {
    if (this.editMode) {
      const category = await CategoryRepository.findOne(this.$route.params.categoryId);
      this.name = category.name;
    }
  },
  methods: {
    async guardarNota() {
      if (this.editMode) {
        try {
          await CategoryRepository.update(this.$route.params.categoryId, {
            id: this.$route.params.categoryId,
            name: this.name
          });
          this.$router.push("/categories");
        } catch (error) {
          console.error("Error updating category:", error);
          alert(error.response?.data?.message || error.message);
        }
      } else {
        try {
          await CategoryRepository.create({ name: this.name });
          this.$router.push("/categories");
        } catch (error) {
          console.error("Error creating category:", error);
          alert(error.response?.data?.message || error.message);
        }
      }
    }
  }
};
</script>
