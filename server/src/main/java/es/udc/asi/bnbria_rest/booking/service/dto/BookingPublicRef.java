package es.udc.asi.bnbria_rest.booking.service.dto;

import java.time.LocalDate;

public record BookingPublicRef(
  Long id,
  LocalDate startDate,
  LocalDate endDate
) {
}
