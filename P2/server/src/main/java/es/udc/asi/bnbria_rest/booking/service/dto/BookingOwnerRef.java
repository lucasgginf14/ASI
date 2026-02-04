package es.udc.asi.bnbria_rest.booking.service.dto;

import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingOwnerRef(
  Long id,
  BookingState state,
  LocalDate startDate,
  LocalDate endDate,
  LocalDateTime requestDate,
  String requestText,
  UserPublicRef user
) {
}
