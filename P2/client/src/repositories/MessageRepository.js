import HTTP from "@/common/http";

export default {
  // HU71: Obtener la lista de conversaciones (GET /api/messages)
  async getConversations() {
    return (await HTTP.get("messages")).data;
  },

  // HU72: Obtener los mensajes de una reserva concreta (GET /api/messages/{bookingId})
  async getMessages(bookingId) {
    return (await HTTP.get(`messages/${bookingId}`)).data;
  },

  // HU72: Enviar mensaje (POST /api/messages/{bookingId})
  async sendMessage(bookingId, text) {
    return (await HTTP.post(`messages/${bookingId}`, { text })).data;
  }
};
