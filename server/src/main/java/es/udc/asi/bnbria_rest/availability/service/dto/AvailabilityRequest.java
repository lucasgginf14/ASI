package es.udc.asi.bnbria_rest.availability.service.dto;

import java.time.LocalDate;

public record AvailabilityRequest(
  LocalDate startDate,
  LocalDate endDate,
  Double price
) {
}
