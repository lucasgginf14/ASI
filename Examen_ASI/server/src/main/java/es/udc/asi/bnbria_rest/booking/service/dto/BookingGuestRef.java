package es.udc.asi.bnbria_rest.booking.service.dto;

import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;

import java.time.LocalDate;

public record BookingGuestRef(
  Long id,
  String propertyTitle,
  LocalDate startDate,
  LocalDate endDate,
  BookingState state
) {
}
