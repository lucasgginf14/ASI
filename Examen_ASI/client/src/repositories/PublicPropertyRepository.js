import HTTP from "@/common/http";

export default {
  // Buscar propiedades por ubicación (Coincide con tu PropertyResource.java)
  // Endpoint: GET /api/properties/search/{location}
  async searchByLocation(location) {
    if (!location) return [];
    // Codificamos la ubicación para evitar errores con espacios o acentos
    const encodedLocation = encodeURIComponent(location);
    return (await HTTP.get(`properties/search/${encodedLocation}`)).data;
  },

  // Obtener detalle de una propiedad
  // Endpoint: GET /api/properties/{id}
  async getById(id) {
    return (await HTTP.get(`properties/${id}`)).data;
  }
};
