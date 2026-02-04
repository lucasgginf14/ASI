<template>
  <div>
    <h1>Categorías</h1>
    <button @click="createCategory">Nueva categoría</button>
    <ul>
      <li v-for="category in categories" :key="category.id" style="cursor: pointer">
        {{ category.name }}
        <button @click="onCategoryClick(category.name)">Ver notas</button>
        <button @click="modifyCategory(category.id)">Editar</button>
        <button @click="deleteCategory(category)">Eliminar</button>/
      </li>
    </ul>
  </div>
</template>

<script>
import CategoryRepository from "@/repositories/CategoryRepository";

export default {
  name: "CategoryList",
  data() {
    return {
      categories: []
    };
  },
  async mounted() {
    this.categories = await CategoryRepository.findAll();
  },
  methods: {
    onCategoryClick(category) {
      this.$router.push({ name: "NoteList", query: { category } });
    },
    createCategory() {
      this.$router.push({ name: "CategoryForm" });
    },
    modifyCategory(id) {
      this.$router.push("categories/" + id + "/edit");
    },
    deleteCategory(category) {
      if (confirm("¿Estás seguro de que deseas eliminar la categoría " + category.name + "?")) {
        CategoryRepository.delete(category.id)
          .then(() => {
            this.categories = this.categories.filter((cat) => cat.id !== category.id);
          })
          .catch((error) => {
            console.error("Error deleting category:", error);
            alert(error.response?.data?.message || error.message);
          });
      }
    }
  }
};
</script>
