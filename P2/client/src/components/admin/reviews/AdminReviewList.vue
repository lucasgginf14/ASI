<template>
  <div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="fw-bold">Moderación de Reseñas</h2>
      <div class="input-group w-25 shadow-sm rounded-pill overflow-hidden">
        <span class="input-group-text bg-white border-0 ps-3" aria-hidden="true">🔎</span>
        <input
          v-model="searchId"
          type="search"
          class="form-control border-0"
          placeholder="Buscar por ID de reseña"
          aria-label="Buscar por ID de reseña"
          style="outline: none; box-shadow: none"
        />
      </div>
    </div>

    <div class="d-flex gap-2 mb-4">
      <button
        @click="switchType('property')"
        class="btn rounded-pill px-4 fw-bold"
        :class="type === 'property' ? 'btn-dark' : 'btn-light'"
      >
        🏠 Por Propiedad
      </button>
      <button
        @click="switchType('user')"
        class="btn rounded-pill px-4 fw-bold"
        :class="type === 'user' ? 'btn-dark' : 'btn-light'"
      >
        👤 Por Usuario
      </button>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else-if="groupedReviews.length > 0" class="d-flex flex-column gap-4">
      <div
        v-for="group in groupedReviews"
        :key="group.id"
        class="card border-0 shadow-sm overflow-hidden"
      >
        <details>
          <summary class="d-flex align-items-center justify-content-between p-3 bg-white">
            <div class="d-flex align-items-center gap-3">
              <div>
                <h5 class="mb-0 fw-semibold">{{ group.title }}</h5>
                <small class="text-muted">{{ group.reviews.length }} reseñas</small>
              </div>
            </div>
            <div class="text-end">
              <div class="d-flex align-items-center gap-2">
                <span
                  class="badge bg-dark text-white fs-6"
                  :title="
                    group.reviews.length
                      ? (group.totalRating / group.reviews.length).toFixed(1)
                      : '—'
                  "
                  aria-label="Promedio de reseñas"
                >
                  {{
                    group.reviews.length
                      ? (group.totalRating / group.reviews.length).toFixed(1)
                      : "—"
                  }}
                </span>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="18"
                  height="18"
                  fill="currentColor"
                  class="bi bi-star-fill text-warning"
                  viewBox="0 0 16 16"
                  aria-hidden="true"
                >
                  <path
                    d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.32-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.63.283.95l-3.523 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
                  />
                </svg>
              </div>
            </div>
          </summary>

          <div class="card-body bg-light p-3">
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-3">
              <div class="col" v-for="review in group.reviews" :key="review.id">
                <AdminReviewCard :review="review" @delete="handleDelete" />
              </div>
            </div>
          </div>
        </details>
      </div>
    </div>

    <div v-else class="text-center py-5 bg-light rounded-4">
      <p class="text-muted mb-0">No hay reseñas registradas en esta categoría.</p>
    </div>
  </div>
</template>

<script>
import ReviewRepository from "@/repositories/ReviewRepository";
import AdminReviewCard from "./AdminReviewCard.vue";

export default {
  components: { AdminReviewCard },
  data() {
    return {
      reviews: [],
      type: "property",
      loading: true,
      searchId: ""
    };
  },
  computed: {
    // Lógica de agrupación
    groupedReviews() {
      const groups = {};
      const query = String(this.searchId || "").trim();
      const source = query
        ? this.reviews.filter((r) => String(r.id).includes(query))
        : this.reviews;

      source.forEach((review) => {
        let groupId, groupTitle;

        if (this.type === "property") {
          groupId = review.propertyTitle || "Propiedad Desconocida";
          groupTitle = review.propertyTitle || "Propiedad Desconocida";
        } else {
          groupId = review.reviewedEmail || "Usuario Desconocido";
          groupTitle = review.reviewedEmail || "Usuario Desconocido";
        }

        if (!groups[groupId]) {
          groups[groupId] = {
            id: groupId,
            title: groupTitle,
            reviews: [],
            totalRating: 0
          };
        }

        groups[groupId].reviews.push(review);
        groups[groupId].totalRating += review.rating || 0;
      });

      return Object.values(groups);
    }
  },
  async mounted() {
    await this.loadReviews();
  },
  methods: {
    async switchType(newType) {
      this.type = newType;
      await this.loadReviews();
    },
    async loadReviews() {
      this.loading = true;
      this.reviews = [];
      try {
        if (this.type === "property") {
          this.reviews = await ReviewRepository.getAllPropertyReviewsAdmin();
        } else {
          this.reviews = await ReviewRepository.getAllGuestReviewsAdmin();
        }
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    },
    async handleDelete(id) {
      if (!confirm("¿Eliminar esta reseña permanentemente?")) return;
      try {
        if (this.type === "property") {
          await ReviewRepository.deletePropertyReview(id);
        } else {
          await ReviewRepository.deleteGuestReview(id);
        }
        alert("Reseña eliminada.");
        await this.loadReviews();
      } catch {
        alert("Error al eliminar.");
      }
    }
  }
};
</script>
