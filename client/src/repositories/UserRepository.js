import HTTP from "@/common/http";

export default {
  // --- MÉTODOS DE USUARIO NORMAL ---
  async getMe() {
    return (await HTTP.get("users/me")).data;
  },

  async updateMe(data) {
    return (await HTTP.put("users/me", data)).data;
  },

  async updatePassword(data) {
    return (await HTTP.put("users/me/password", data)).data;
  },

  // --- MÉTODOS PARA ADMINISTRADOR ---

  // 1. Obtener lista (CORREGIDO: faltaba "/admin")
  async getAllUsersAdmin() {
    return (await HTTP.get("users/admin")).data;
  },

  // 2. Obtener detalle por ID (Usamos ID numérico, no email)
  async getAdminUserDetail(id) {
    return (await HTTP.get(`users/admin/${id}`)).data;
  },

  // 3. Activar usuario
  async activateUser(id) {
    return (await HTTP.put(`users/admin/${id}/active`)).data;
  },

  // 4. Desactivar usuario
  async deactivateUser(id) {
    return (await HTTP.delete(`users/admin/${id}/active`)).data;
  },

  // --- MÉTODOS DE PERFIL PÚBLICO ---
  async getPublicProfile(id) {
    return (await HTTP.get(`users/${id}/public`)).data;
  }
};
