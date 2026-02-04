package es.udc.asi.bnbria_rest.availability.service.dto;

import java.time.LocalDate;

public record AvailabilityView(
  Long id,
  LocalDate startDate,
  LocalDate endDate,
  Double price
) {
}
