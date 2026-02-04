package es.udc.asi.bnbria_rest.booking.service.dto;

import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;

import java.time.LocalDate;

public record BookingPrivateView(
  Long id,
  BookingState state,
  Double price,
  String requestText,
  String cancellationReason,
  LocalDate startDate,
  LocalDate endDate,
  Integer numGuests
) {
}
