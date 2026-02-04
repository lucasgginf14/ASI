package es.udc.asi.bnbria_rest.property.service.dto;

import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;

public record PropertyUpdateRequest(
  String title,
  String description,
  Long bathrooms,
  Long bedrooms,
  Long maxOccupancy,
  Long squareMeters,
  PropertyType type
) {
}
