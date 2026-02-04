package es.udc.asi.bnbria_rest.booking.service.dto;

import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;

import java.time.LocalDate;

public record BookingAdminRef(
  Long id,
  String propertyTitle,
  String guestName,
  String ownerName,
  BookingState state,
  LocalDate startDate,
  LocalDate endDate
) {
}
