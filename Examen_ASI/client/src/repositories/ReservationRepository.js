import HTTP from "@/common/http";

export default {
  // --- ADMINISTRADOR ---
  async getAllReservationsAdmin() {
    return (await HTTP.get("bookings/admin")).data;
  },
  async cancelReservationAdmin(id, reason) {
    return (await HTTP.put(`bookings/${id}/cancel`, { reason })).data;
  },

  async getMyBookings() {
    return (await HTTP.get("bookings/guest/all")).data;
  },

  async cancelBooking(id, reason) {
    return (await HTTP.put(`bookings/${id}/cancel`, { reason })).data;
  },

  async findIncomingRequests() {
    return (await HTTP.get("bookings/owner/pending")).data;
  },

  async findHostBookings() {
    return (await HTTP.get("bookings/owner/all")).data;
  },

  async accept(id) {
    return (await HTTP.put(`bookings/${id}/accept`)).data;
  },

  async getGuestBookingDetails(id) {
    return (await HTTP.get(`bookings/guest/${id}`)).data;
  },

  async getHostBookingDetails(id) {
    return (await HTTP.get(`bookings/owner/${id}`)).data;
  }
};
