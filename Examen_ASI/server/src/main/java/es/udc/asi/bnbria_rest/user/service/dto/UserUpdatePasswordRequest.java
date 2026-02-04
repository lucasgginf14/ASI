package es.udc.asi.bnbria_rest.user.service.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserUpdatePasswordRequest(
  @NotEmpty String currentPassword,
  @NotEmpty String newPassword
) {
}
