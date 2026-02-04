package es.udc.asi.bnbria_rest.property.service.dto;

import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;

public record PropertyTopView(
  Long id,
  String title,
  PropertyType type,
  String city,
  String country,
  Double lowerPrice
) {
}
