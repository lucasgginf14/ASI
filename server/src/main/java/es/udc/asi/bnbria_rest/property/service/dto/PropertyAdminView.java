package es.udc.asi.bnbria_rest.property.service.dto;

import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;
import es.udc.asi.bnbria_rest.user.service.dto.UserAdminView;

public record PropertyAdminView(
  Long id,
  String title,
  String description,
  PropertyState state,
  Long bathrooms,
  Long bedrooms,
  Long maxOccupancy,
  Long squareMeters,
  PropertyType type,
  Double latitude,
  Double longitude,
  String door,
  String number,
  String street,
  String city,
  String province,
  String country,
  Long postalCode,
  UserAdminView owner
) {
}
