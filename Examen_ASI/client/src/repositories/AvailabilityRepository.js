import HTTP from "@/common/http";

export default {
  // Crear disponibilidad
  // CORRECCIÓN: Añadido '/my-properties' para coincidir con el PropertyController
  async create(propertyId, availability) {
    return (await HTTP.post(`properties/my-properties/${propertyId}/availabilities`, availability))
      .data;
  },

  // Editar disponibilidad
  // CORRECCIÓN: Añadido '/my-properties'
  async update(propertyId, availabilityId, availability) {
    return (
      await HTTP.put(
        `properties/my-properties/${propertyId}/availabilities/${availabilityId}`,
        availability
      )
    ).data;
  },

  // Eliminar disponibilidad
  // CORRECCIÓN: Añadido '/my-properties'
  async delete(propertyId, availabilityId) {
    return (
      await HTTP.delete(`properties/my-properties/${propertyId}/availabilities/${availabilityId}`)
    ).data;
  }
};
