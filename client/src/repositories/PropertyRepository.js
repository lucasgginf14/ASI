import HTTP from "@/common/http";

export default {
  // --- MÉTODOS PÚBLICOS (Cualquiera puede verlos) ---

  async findTopRated() {
    return (await HTTP.get("properties/top-rated")).data;
  },

  async findTopBooked() {
    return (await HTTP.get("properties/top-booked")).data;
  },

  async findByLocation(location) {
    return (await HTTP.get(`properties/search/${location}`)).data;
  },

  // Detalle público (para la vista de reserva, etc.)
  async findById(id) {
    return (await HTTP.get(`properties/${id}`)).data;
  },

  // --- MÉTODOS DE GESTIÓN (Anfitrión - Requieren Autenticación) ---
  // IMPORTANTE: Todas estas rutas en tu Controller empiezan por "properties/my-properties"

  // Listar MIS propiedades
  async getMyProperties() {
    // CORRECCIÓN: La ruta en el controller es @GetMapping("/my-properties") dentro de @RequestMapping("/api/properties")
    return (await HTTP.get("properties/my-properties")).data;
  },

  // Obtener detalle de MI propiedad (para editar)
  async getMyPropertyById(id) {
    // CORRECCIÓN: Usamos el endpoint específico de propietario para asegurar permisos
    return (await HTTP.get(`properties/my-properties/${id}`)).data;
  },

  // Crear propiedad
  async create(propertyData) {
    // CORRECCIÓN: Ruta POST /api/properties/my-properties
    return (await HTTP.post("properties/my-properties", propertyData)).data;
  },

  // Actualizar propiedad
  async update(id, propertyData) {
    // CORRECCIÓN: Ruta PUT /api/properties/my-properties/{id}
    return (await HTTP.put(`properties/my-properties/${id}`, propertyData)).data;
  },

  // Eliminar propiedad
  async delete(id) {
    // CORRECCIÓN: Ruta DELETE /api/properties/my-properties/{id}
    return (await HTTP.delete(`properties/my-properties/${id}`)).data;
  },

  // Subir imagen
  async saveImage(propertyId, imageFile) {
    const formData = new FormData();
    formData.append("file", imageFile);
    // CORRECCIÓN: Ruta POST /api/properties/my-properties/{id}/image
    return (
      await HTTP.post(`properties/my-properties/${propertyId}/image`, formData, {
        headers: { "Content-Type": "multipart/form-data" }
      })
    ).data;
  },

  async getAllPropertiesAdmin() {
    return (await HTTP.get("properties/admin")).data;
  },

  // 2. Ver el detalle completo de una propiedad (Vista Admin)
  async getAdminPropertyById(id) {
    return (await HTTP.get(`properties/admin/${id}`)).data;
  },

  // 3. Cambiar estado: Aprobar ("APPROVED") o Rechazar ("REJECTED")
  async changeStatus(id, status) {
    if (status === "APPROVED") {
      return (await HTTP.put(`properties/admin/${id}/approve`)).data;
    } else {
      return (await HTTP.delete(`properties/admin/${id}/unapprove`)).data;
    }
  },

  // 4. Eliminar propiedad como administrador
  async deleteAdmin(id) {
    return (await HTTP.delete(`properties/admin/${id}`)).data;
  }
};
