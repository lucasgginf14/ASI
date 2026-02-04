package es.udc.asi.bnbria_rest.message.service.dto;

import jakarta.validation.constraints.NotEmpty;

public record MessageRequest(
  @NotEmpty String text
) {
}
