import HTTP from "@/common/http";

export default {
  // -------------------------------------------------------------------
  // 🔴 ADMINISTRADOR (Panel de Moderación)
  // -------------------------------------------------------------------

  // Obtener reseñas de propiedades
  async getAllPropertyReviewsAdmin() {
    return (await HTTP.get("property-reviews/admin")).data;
  },

  // Obtener reseñas de usuarios (Guests)
  // Nota: En AdminReviewList usamos 'getAllGuestReviewsAdmin'
  async getAllGuestReviewsAdmin() {
    return (await HTTP.get("user-reviews/admin")).data;
  },

  // Borrar reseña de propiedad
  async deletePropertyReview(id) {
    return (await HTTP.delete(`property-reviews/admin/${id}`)).data;
  },

  // Borrar reseña de usuario
  // Nota: En AdminReviewList usamos 'deleteGuestReview'
  async deleteGuestReview(id) {
    return (await HTTP.delete(`user-reviews/admin/${id}`)).data;
  },

  // -------------------------------------------------------------------
  // 🟢 USUARIO (Lectura de pendientes)
  // -------------------------------------------------------------------

  // Ver mis pendientes de escribir sobre propiedades (Usado en PendingReviews.vue)
  async getPendingPropertyReviews() {
    return (await HTTP.get("property-reviews/pending")).data;
  },

  // Ver mis pendientes de escribir sobre usuarios (Usado en PendingReviews.vue)
  async getPendingUserReviews() {
    return (await HTTP.get("user-reviews/pending")).data;
  },

  // -------------------------------------------------------------------
  // 🔵 USUARIO (Publicar)
  // -------------------------------------------------------------------

  // Publicar reseña sobre una propiedad (Usado en PendingReviews.vue)
  async publishPropertyReview(id, payload) {
    // payload: { rating: 5, comment: "..." }
    return (await HTTP.put(`property-reviews/${id}`, payload)).data;
  },

  // Publicar reseña sobre un usuario (Usado en PendingReviews.vue)
  async publishUserReview(id, payload) {
    return (await HTTP.put(`user-reviews/${id}`, payload)).data;
  }
};
