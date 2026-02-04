package es.udc.asi.bnbria_rest.user.service.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Date;

public record UserCreateRequest(
  @NotEmpty String email,
  @NotEmpty String password,
  @NotEmpty String confirmPassword,
  @NotEmpty String name,
  @NotEmpty String surname,
  String surname_2,
  String phone,
  Date birthday
) {
}
