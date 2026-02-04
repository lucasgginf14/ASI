package es.udc.asi.bnbria_rest.property.service.dto;

import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;

public record PropertyOwnerRef(
  Long id,
  String title,
  PropertyState state,
  String door,
  String number,
  String street,
  String city,
  String province,
  String country,
  Double averageRating
) {
}
