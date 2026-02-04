<template>
  <div v-if="category != null">
    <div>
      <span>Título: </span>
      <span>{{ category.name }}</span>
    </div>
  </div>
</template>

<script>
import CategoryRepository from "@/repositories/CategoryRepository";

export default {
  data() {
    return {
      category: null
    };
  },
  async mounted() {
    this.category = await CategoryRepository.findOne(this.$route.params.categoryId);
  }
};
</script>
