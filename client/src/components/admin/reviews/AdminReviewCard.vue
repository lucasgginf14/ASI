<template>
  <div class="card h-100 border border-light-subtle shadow-sm review-card">
    <div class="card-body p-3 d-flex flex-column">
      <div class="d-flex justify-content-between text-muted x-small mb-2">
        <span class="font-monospace">#{{ review.id }}</span>
      </div>

      <div class="mb-2 text-warning fw-bold d-flex align-items-center">
        <span class="me-2">{{ renderStars(review.rating) }}</span>
        <span class="text-dark x-small bg-light px-1 rounded border">{{ review.rating }}/5</span>
      </div>

      <div class="p-2 bg-light rounded-3 mb-3 border-start border-3 border-warning flex-grow-1">
        <p class="fst-italic text-secondary small mb-0">
          "{{ review.description || "Sin comentario" }}"
        </p>
      </div>

      <div class="border-top pt-2 mt-auto">
        <div class="d-flex flex-column" style="overflow: hidden">
          <span class="fw-bold small text-truncate text-dark" :title="authorName">
            Escrita por:
          </span>
          <small> {{ authorEmail }}</small>
        </div>
      </div>
    </div>

    <div class="card-footer bg-white border-top-0 pt-0 pb-3">
      <button
        @click="$emit('delete', review.id)"
        class="btn btn-sm btn-outline-danger w-100 rounded-pill fw-bold"
        style="font-size: 0.8rem"
      >
        Eliminar
      </button>
    </div>
  </div>
</template>

<script>
export default {
  props: { review: Object },
  computed: {
    authorName() {
      return (
        this.review.reviewerName ||
        this.review.reviewer?.name ||
        this.review.author ||
        "Usuario Anónimo"
      );
    },
    authorEmail() {
      return this.review.reviewerEmail || this.review.reviewer?.email || "Email oculto";
    }
  },
  methods: {
    formatDate(d) {
      return d ? new Date(d).toLocaleDateString() : "-";
    },
    renderStars(rating) {
      return "★".repeat(rating || 0) + "☆".repeat(5 - (rating || 0));
    }
  }
};
</script>

<style scoped>
.review-card {
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}
.review-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
}
.x-small {
  font-size: 0.75rem;
}
</style>
