package es.udc.asi.bnbria_rest.property.service.dto;

import es.udc.asi.bnbria_rest.availability.service.dto.AvailabilityView;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;

import java.util.List;

public record PropertyRef(
  Long id,
  String title,
  Long bathrooms,
  Long bedrooms,
  Long maxOccupancy,
  Long squareMeters,
  PropertyType type,
  String door,
  String number,
  String street,
  String city,
  String province,
  String country,
  Double latitude,
  Double longitude,
  Double averageRating,
  List<AvailabilityView> availabilities,
  Double lowerPrice
) {
}
