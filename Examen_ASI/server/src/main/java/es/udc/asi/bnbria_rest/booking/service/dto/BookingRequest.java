package es.udc.asi.bnbria_rest.booking.service.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(
  @NotNull LocalDate startDate,
  @NotNull LocalDate endDate,
  @NotNull Integer numGuests,
  String requestText
) {
}
