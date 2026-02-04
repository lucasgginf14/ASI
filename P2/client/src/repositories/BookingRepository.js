import HTTP from "@/common/http";

export default {
  // -------------------------------------------------------------------
  // 🟢 MÉTODOS PÚBLICOS / GUEST (VIAJERO)
  // -------------------------------------------------------------------

  // Crear una nueva reserva (Desde el detalle de la propiedad)
  // Endpoint: POST /api/properties/{propertyId}/booking
  async create(propertyId, bookingData) {
    return (await HTTP.post(`properties/${propertyId}/booking`, bookingData)).data;
  },

  // Ver "Mis Viajes" (Usado en GuestBookingList.vue)
  // Endpoint: GET /api/bookings/guest/all
  async getMyBookingsAsGuest() {
    return (await HTTP.get("bookings/guest/all")).data;
  },

  // Cancelar mi propia reserva (Usado en GuestBookingList.vue)
  // Endpoint: PUT /api/bookings/{id}/cancel
  async cancelMyBooking(id, reason) {
    return (await HTTP.put(`bookings/${id}/cancel`, { reason })).data;
  },

  // -------------------------------------------------------------------
  // 🟠 MÉTODOS DE ANFITRIÓN (HOST/OWNER)
  // -------------------------------------------------------------------

  // Ver solicitudes recibidas en mis casas
  // Endpoint: GET /api/bookings/owner/pending
  async getIncomingBookings() {
    return (await HTTP.get("bookings/owner/pending")).data;
  },

  async getHostBookingDetails(id) {
    return (await HTTP.get(`bookings/owner/${id}`)).data;
  },

  // Aceptar una solicitud de reserva
  // Endpoint: PUT /api/bookings/{id}/accept
  async acceptBooking(id) {
    return (await HTTP.put(`bookings/${id}/accept`)).data;
  },

  // Rechazar una solicitud (Usa el mismo endpoint de cancelar pero con lógica de negocio)
  async rejectBooking(id, reason = "Rechazada por el anfitrión") {
    return (await HTTP.put(`bookings/${id}/cancel`, { reason })).data;
  },

  // -------------------------------------------------------------------
  // 🔴 MÉTODOS DE ADMINISTRADOR
  // -------------------------------------------------------------------

  // Ver TODAS las reservas de la plataforma (Panel Admin)
  // Endpoint: GET /api/bookings/admin (Asegúrate de tener este endpoint o ajusta la URL)
  async getAllBookingssAdmin() {
    // Si tu backend no tiene /bookings/admin específico, quizás usa otra ruta.
    // Basado en lo hablado, asumimos esta ruta:
    return (await HTTP.get("bookings/admin")).data;
  },

  // Cancelar reserva como administrador
  // Endpoint: POST /api/reservations/admin/{id}/cancel
  async cancelBooking(id, reason) {
    return (await HTTP.put(`bookings/${id}/cancel`, { reason })).data;
  },

  async getBookingDetailsAdmin(id) {
    return (await HTTP.get(`bookings/admin/${id}`)).data;
  }
};
