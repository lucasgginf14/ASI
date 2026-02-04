package es.udc.asi.bnbria_rest.booking.service.dto;

import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.property.service.dto.PropertySimpleRef;

import java.time.LocalDate;

public record BookingGuestView(
  Long id,
  PropertySimpleRef property,
  BookingState state,
  String requestText,
  String cancellationReason,
  Double price,
  LocalDate startDate,
  LocalDate endDate,
  Integer numGuests,
  Boolean hasMessages
) {
}
