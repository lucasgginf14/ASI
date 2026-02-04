package es.udc.asi.bnbria_rest.user.service.dto;

import java.util.Date;

public record UserUpdateRequest(
  String name,
  String surname,
  String surname2,
  String phone,
  Date birthday
) {
}
