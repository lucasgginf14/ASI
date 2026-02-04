package es.udc.asi.bnbria_rest.booking.service.dto;

import jakarta.validation.constraints.NotEmpty;

public record BookingCancelRequest(
  @NotEmpty String reason
) {
}
