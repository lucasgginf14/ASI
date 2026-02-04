package es.udc.asi.bnbria_rest.booking.service.dto;

import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.property.service.dto.PropertySimpleRef;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingAdminView(
  Long id,
  BookingState state,
  LocalDate startDate,
  LocalDate endDate,
  LocalDateTime requestDate,
  Double price,
  Integer numGuests,
  String requestText,
  String cancellationReason,
  UserPublicRef owner,
  UserPublicRef guest,
  PropertySimpleRef property
) {
}
