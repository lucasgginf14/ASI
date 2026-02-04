package es.udc.asi.bnbria_rest.property.service.dto;

import es.udc.asi.bnbria_rest.availability.service.dto.AvailabilityView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingPublicRef;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewView;

import java.util.List;

public record PropertyOwnerView(
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
  List<PropertyReviewView> reviews,
  Double averageRating,
  Double averageCleanlinessRating,
  Double averageHospitalityRating,
  Double averageLocationRating,
  List<AvailabilityView> availabilities,
  List<BookingPublicRef> bookings
) {
}
