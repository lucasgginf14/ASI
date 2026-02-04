<template>
  <div class="container mt-5 mb-5">
    <div
      class="container mt-5 mb-5"
      style="
        background: linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%);
        border-radius: 2rem;
        box-shadow: 0 4px 32px #6366f140;
      "
    >
      <h2 class="fw-bold mb-2" style="color: #312e81">Reseñas Pendientes</h2>
      <p class="text-muted" style="font-size: 1.1rem">¡Tu opinión ayuda a la comunidad!</p>

      <div class="mb-4 d-flex gap-2 justify-content-center">
        <button
          class="btn shadow-sm"
          :class="filter === 'ALL' ? 'btn-gradient-dark' : 'btn-outline-gradient-dark'"
          @click="filter = 'ALL'"
        >
          Todos <span class="badge bg-gradient-secondary">{{ items.length }}</span>
        </button>
        <button
          class="btn shadow-sm"
          :class="filter === 'PROPERTY' ? 'btn-gradient-primary' : 'btn-outline-gradient-primary'"
          @click="filter = 'PROPERTY'"
        >
          Propiedades
          <span class="badge bg-gradient-primary">{{ propertyCount }}</span>
        </button>
        <button
          class="btn shadow-sm"
          :class="filter === 'USER' ? 'btn-gradient-info text-dark' : 'btn-outline-gradient-info'"
          @click="filter = 'USER'"
        >
          Usuarios
          <span class="badge bg-gradient-info text-dark">{{ userCount }}</span>
        </button>
      </div>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-dark"></div>
      </div>

      <div
        v-else-if="items.length === 0"
        class="text-center py-5"
        style="background: linear-gradient(135deg, #e0e7ff 0%, #f1f5f9 100%); border-radius: 1.5rem"
      >
        <h5 style="color: #2563eb">¡Todo al día! 🎉</h5>
        <p class="text-muted">No tienes reseñas pendientes de escribir.</p>
      </div>

      <div v-else class="row g-4">
        <div class="col-md-6 d-flex" v-for="item in filteredItems" :key="item.uniqueId">
          <div
            class="card border-0 shadow-lg w-100"
            style="
              background: linear-gradient(120deg, #f1f5f9 60%, #e0e7ff 100%);
              border-radius: 1.5rem;
              position: relative;
              min-height: 250px;
              margin: 0.5rem 0;
              padding-bottom: 2.5rem;
              display: flex;
              flex-direction: column;
              justify-content: space-between;
            "
          >
            <div class="card-body p-4 d-flex flex-column">
              <span
                class="badge mb-2 align-self-start"
                :class="
                  item.type === 'PROPERTY' ? 'bg-gradient-primary' : 'bg-gradient-info text-dark'
                "
                style="font-size: 0.95rem; letter-spacing: 0.5px"
              >
                {{ item.type === "PROPERTY" ? "Alojamiento" : "Usuario" }}
              </span>

              <h5 class="fw-bold mt-2 mb-1" style="color: #1e293b">{{ item.title }}</h5>
              <small class="text-muted">{{ item.subtitle }}</small>

              <div v-if="item.isWriting" class="animate__animated animate__fadeIn">
                <div class="d-flex align-items-center mt-3">
                  <label
                    class="form-label small fw-bold mb-0 me-3"
                    style="color: #312e81; min-width: 100px; font-size: 0.95rem"
                  >
                    Puntuación
                  </label>
                  <div
                    class="fs-3 user-select-none"
                    style="cursor: pointer; color: #fbbf24"
                    @mouseleave="item.hoverRating = 0"
                  >
                    <span
                      v-for="n in 5"
                      :key="n"
                      @click="item.rating = n"
                      @mouseover="item.hoverRating = n"
                      style="transition: color 0.2s"
                    >
                      {{ n <= (item.hoverRating || item.rating) ? "★" : "☆" }}
                    </span>
                  </div>
                </div>

                <div v-if="item.type === 'PROPERTY'">
                  <div class="d-flex align-items-center mt-2">
                    <label
                      class="form-label small fw-bold mb-0 me-3"
                      style="color: #312e81; min-width: 100px; font-size: 0.95rem"
                    >
                      Limpieza
                    </label>
                    <div class="fs-3" style="color: #fbbf24">
                      <span
                        v-for="n in 5"
                        :key="'cleanliness-' + n"
                        @click="item.cleanliness = n"
                        @mouseover="item.hoverCleanliness = n"
                        @mouseleave="item.hoverCleanliness = 0"
                        style="cursor: pointer; transition: color 0.2s"
                      >
                        {{ n <= (item.hoverCleanliness || item.cleanliness || 0) ? "★" : "☆" }}
                      </span>
                    </div>
                  </div>
                  <div class="d-flex align-items-center mt-2">
                    <label
                      class="form-label small fw-bold mb-0 me-3"
                      style="color: #312e81; min-width: 100px; font-size: 0.95rem"
                    >
                      Hospitalidad
                    </label>
                    <div class="fs-3" style="color: #fbbf24">
                      <span
                        v-for="n in 5"
                        :key="'hospitality-' + n"
                        @click="item.hospitality = n"
                        @mouseover="item.hoverHospitality = n"
                        @mouseleave="item.hoverHospitality = 0"
                        style="cursor: pointer; transition: color 0.2s"
                      >
                        {{ n <= (item.hoverHospitality || item.hospitality || 0) ? "★" : "☆" }}
                      </span>
                    </div>
                  </div>
                  <div class="d-flex align-items-center mt-2">
                    <label
                      class="form-label small fw-bold mb-0 me-3"
                      style="color: #312e81; min-width: 100px; font-size: 0.95rem"
                    >
                      Ubicación
                    </label>
                    <div class="fs-3" style="color: #fbbf24">
                      <span
                        v-for="n in 5"
                        :key="'location-' + n"
                        @click="item.location = n"
                        @mouseover="item.hoverLocation = n"
                        @mouseleave="item.hoverLocation = 0"
                        style="cursor: pointer; transition: color 0.2s"
                      >
                        {{ n <= (item.hoverLocation || item.location || 0) ? "★" : "☆" }}
                      </span>
                    </div>
                  </div>
                </div>

                <div class="mb-3" />

                <textarea
                  v-model="item.comment"
                  class="form-control mb-4"
                  rows="3"
                  minlength="20"
                  style="background: #f8fafc; border-radius: 1rem; border: 1px solid #c7d2fe"
                  placeholder="Escribe tu experiencia (mínimo 20 caracteres)..."
                ></textarea>

                <div class="d-flex gap-2">
                  <button
                    @click="submitReview(item)"
                    class="btn btn-gradient-dark btn-sm rounded-pill px-4 fw-bold"
                  >
                    Enviar
                  </button>
                  <button
                    @click="item.isWriting = false"
                    class="btn btn-light btn-sm rounded-pill px-3"
                    style="border: 1px solid #cbd5e1"
                  >
                    Cancelar
                  </button>
                </div>
              </div>

              <button
                v-else
                @click="item.isWriting = true"
                class="btn btn-outline-gradient-dark rounded-pill"
                style="
                  font-weight: 600;
                  position: absolute;
                  left: 1rem;
                  right: 1rem;
                  bottom: 1.5rem;
                  margin: 0;
                "
              >
                ⭐ Escribir Reseña
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ReviewRepository from "@/repositories/ReviewRepository";

export default {
  data() {
    return {
      items: [],
      loading: true,
      filter: "ALL"
    };
  },
  async mounted() {
    await this.loadPending();
  },
  computed: {
    filteredItems() {
      if (this.filter === "ALL") return this.items;
      return this.items.filter((i) => i.type === this.filter);
    },
    propertyCount() {
      return this.items.filter((i) => i.type === "PROPERTY").length;
    },
    userCount() {
      return this.items.filter((i) => i.type === "USER").length;
    }
  },
  methods: {
    async loadPending() {
      this.loading = true;
      this.items = [];
      try {
        // 1. Cargar pendientes de PROPIEDADES
        const properties = await ReviewRepository.getPendingPropertyReviews();
        // Mapeamos para unificar formato
        const propItems = properties.map((p) => ({
          id: p.id,
          type: "PROPERTY",
          title: `Tu estancia en ${p.property.title}`,
          rating: 0,
          cleanliness: 0,
          hospitality: 0,
          location: 0,
          comment: "",
          isWriting: false,
          uniqueId: `p-${p.id}`,
          hoverRating: 0,
          hoverCleanliness: 0,
          hoverHospitality: 0,
          hoverLocation: 0
        }));

        // 2. Cargar pendientes de USUARIOS
        const users = await ReviewRepository.getPendingUserReviews();
        const userItems = users.map((u) => ({
          id: u.id,
          type: "USER",
          title: `Experiencia con ${u.reviewed.name} ${u.reviewed.surname} en ${u.propertyTitle}`,
          subtitle:
            "Desde el día " +
            new Date(u.booking.startDate).toLocaleDateString() +
            " hasta el " +
            new Date(u.booking.endDate).toLocaleDateString(),
          rating: 0,
          comment: "",
          isWriting: false,
          uniqueId: `u-${u.id}`,
          hoverRating: 0
        }));

        // Unimos ambas listas
        this.items = [...propItems, ...userItems];
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    },

    async submitReview(item) {
      if (item.rating === 0) return alert("Por favor, selecciona una puntuación.");
      if (item.type === "PROPERTY") {
        if (item.cleanliness === 0)
          return alert("Por favor, selecciona una puntuación para la limpieza.");
        if (item.hospitality === 0)
          return alert("Por favor, selecciona una puntuación para la hospitalidad.");
        if (item.location === 0)
          return alert("Por favor, selecciona una puntuación para la ubicación.");
      }
      if (!item.comment || item.comment.trim().length < 20) {
        return alert("Por favor, escribe un comentario de al menos 20 caracteres.");
      }

      let payload;
      if (item.type === "PROPERTY") {
        payload = {
          description: item.comment,
          rating: item.rating,
          cleanliness: item.cleanliness,
          hospitality: item.hospitality,
          location: item.location
        };
      } else {
        payload = {
          description: item.comment,
          rating: item.rating
        };
      }

      try {
        if (item.type === "PROPERTY") {
          await ReviewRepository.publishPropertyReview(item.id, payload);
        } else {
          await ReviewRepository.publishUserReview(item.id, payload);
        }

        alert("¡Reseña publicada! Gracias.");
        // Quitamos el elemento de la lista visualmente
        this.items = this.items.filter((i) => i.uniqueId !== item.uniqueId);
      } catch {
        alert("Error al enviar la reseña.");
      }
    }
  }
};
</script>

<style scoped>
.btn-gradient-dark {
  background: linear-gradient(90deg, #312e81 0%, #6366f1 100%);
  color: #fff;
  border: none;
}
.btn-outline-gradient-dark {
  background: transparent;
  border: 2px solid #6366f1;
  color: #312e81;
}
.btn-gradient-primary {
  background: linear-gradient(90deg, #2563eb 0%, #60a5fa 100%);
  color: #fff;
  border: none;
}
.btn-outline-gradient-primary {
  background: transparent;
  border: 2px solid #60a5fa;
  color: #2563eb;
}
.btn-gradient-info {
  background: linear-gradient(90deg, #38bdf8 0%, #a5f3fc 100%);
  color: #0c4a6e;
  border: none;
}
.btn-outline-gradient-info {
  background: transparent;
  border: 2px solid #38bdf8;
  color: #0c4a6e;
}
.bg-gradient-secondary {
  background: linear-gradient(90deg, #64748b 0%, #cbd5e1 100%) !important;
  color: #fff !important;
}
.bg-gradient-primary {
  background: linear-gradient(90deg, #2563eb 0%, #60a5fa 100%) !important;
  color: #fff !important;
}
.bg-gradient-info {
  background: linear-gradient(90deg, #38bdf8 0%, #a5f3fc 100%) !important;
  color: #0c4a6e !important;
}
.card {
  border-radius: 1.5rem !important;
}
</style>
