<template>
  <div class="container mt-5 mb-5" style="max-width: 900px">
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark" role="status"></div>
    </div>

    <div v-else-if="user" class="card border-0 shadow-sm rounded-4 p-4">
      <div class="d-flex align-items-center gap-4 mb-4">
        <div
          class="avatar bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center"
        >
          <template v-if="user.avatarUrl">
            <img :src="user.avatarUrl" alt="avatar" class="avatar-img rounded-circle" />
          </template>
          <template v-else>
            <div class="avatar-initials">
              {{
                [user.name, user.surname, user.surname2]
                  .filter(Boolean)
                  .map((n) => n.charAt(0).toUpperCase())
                  .join("")
              }}
            </div>
          </template>
        </div>

        <div class="flex-grow-1">
          <h3 class="mb-0 fw-bold">
            {{ [user.name, user.surname, user.surname2].filter(Boolean).join(" ") }}
          </h3>
          <div class="text-muted small">{{ user.email }}</div>
          <div class="text-muted small">Miembro desde {{ formatDate(user.creationDate) }}</div>
        </div>

        <div class="ms-auto d-flex flex-column gap-2">
          <router-link to="/profile/edit" class="btn btn-black rounded-pill px-4 fw-bold">
            Editar perfil
          </router-link>
          <router-link
            to="/profile/password"
            class="btn btn-outline-dark rounded-pill px-4 fw-bold"
          >
            Cambiar contraseña
          </router-link>
        </div>
      </div>

      <div class="row g-3 profile-data">
        <div class="col-12 col-md-6">
          <div class="py-3 border-bottom d-flex justify-content-between align-items-center">
            <span class="fw-bold text-secondary small">NOMBRE</span>
            <span class="text-dark">{{ user.name }}</span>
          </div>
        </div>

        <div class="col-12 col-md-6">
          <div class="py-3 border-bottom d-flex justify-content-between align-items-center">
            <span class="fw-bold text-secondary small">EMAIL</span>
            <span class="text-dark">{{ user.email }}</span>
          </div>
        </div>

        <div class="col-12 col-md-6">
          <div class="py-3 border-bottom d-flex justify-content-between align-items-center">
            <span class="fw-bold text-secondary small">PRIMER APELLIDO</span>
            <span class="text-dark">{{ user.surname }}</span>
          </div>
        </div>

        <div class="col-12 col-md-6">
          <div class="py-3 border-bottom d-flex justify-content-between align-items-center">
            <span class="fw-bold text-secondary small">SEGUNDO APELLIDO</span>
            <span class="text-dark">{{ user.surname2 || "-" }}</span>
          </div>
        </div>

        <div class="col-12 col-md-6">
          <div class="py-3 border-bottom d-flex justify-content-between align-items-center">
            <span class="fw-bold text-secondary small">TELÉFONO</span>
            <span class="text-dark">{{ user.phone || "-" }}</span>
          </div>
        </div>

        <div class="col-12 col-md-6">
          <div class="py-3 border-bottom d-flex justify-content-between align-items-center">
            <span class="fw-bold text-secondary small">FECHA DE NACIMIENTO</span>
            <span class="text-dark">{{ formatDate(user.birthday) }}</span>
          </div>
        </div>
      </div>

      <div v-if="store.state.user.authority !== 'ADMIN'" class="mt-4">
        <div class="row align-items-center mb-3">
          <div class="col-auto">
            <div class="rating-circle d-flex align-items-center justify-content-center fw-bold">
              {{ user.averageRating ? user.averageRating.toFixed(1) : "-" }}
            </div>
          </div>
          <div class="col">
            <div class="fw-bold small text-secondary">VALORACIÓN MEDIA</div>
            <div class="text-muted small">
              {{
                user.reviews && user.reviews.length
                  ? user.reviews.length + " valoraciones"
                  : "Sin valoraciones"
              }}
            </div>
          </div>
        </div>

        <div class="card reviews-card border-0">
          <div class="card-body p-0">
            <div v-if="user.reviews && user.reviews.length" class="reviews-list">
              <div v-for="(review, idx) in user.reviews" :key="idx" class="py-3 border-bottom">
                <div class="d-flex gap-3 align-items-start">
                  <div class="flex-grow-1">
                    <div class="d-flex justify-content-between">
                      <div>
                        <div class="fw-bold small">
                          {{
                            review.reviewer && (review.reviewer.name || review.reviewer.surname)
                              ? [
                                  review.reviewer.name,
                                  review.reviewer.surname,
                                  review.reviewer.surname2
                                ]
                                  .filter(Boolean)
                                  .join(" ")
                              : "Anónimo"
                          }}
                        </div>
                        <div class="text-muted small text-truncate" style="max-width: 520px">
                          {{ review.description }}
                        </div>
                      </div>

                      <div class="text-end ms-3" style="min-width: 95px">
                        <div class="fw-bold mb-1">
                          <span
                            v-for="n in 5"
                            :key="n"
                            class="star text-warning"
                            :class="{ filled: n <= (review.rating || 0) }"
                          >
                            {{ n <= (review.rating || 0) ? "★" : "☆" }}
                          </span>
                        </div>
                        <div class="text-muted small">{{ formatDate(review.creationDate) }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="py-2 text-muted small">Sin valoraciones</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserRepository from "@/repositories/UserRepository.js";
import { getStore } from "@/common/store.js";

export default {
  data() {
    return {
      user: null,
      loading: true,
      store: getStore()
    };
  },
  async mounted() {
    try {
      this.user = await UserRepository.getMe();
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    formatDate(date) {
      if (!date) return "-";
      return new Date(date).toLocaleDateString("es-ES", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
      });
    }
  }
};
</script>

<style scoped>
.btn-black {
  background-color: #000;
  color: #fff;
  transition: transform 0.2s;
}
.btn-black:hover {
  background-color: #333;
  transform: translateY(-2px);
}

.avatar {
  width: 80px;
  height: 80px;
  flex: 0 0 80px;
  overflow: hidden;
  position: relative;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-initials {
  font-weight: 700;
  font-size: 1.25rem;
  letter-spacing: 1px;
}
.profile-data .border-bottom {
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

.rating-circle {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffe082, #ffc107);
  color: #000;
}

.reviews-card {
  background: transparent;
}

.star {
  font-size: 1rem;
  line-height: 1;
  display: inline-block;
  font-weight: 300;
}
.star.filled {
  font-weight: 700;
}
</style>
