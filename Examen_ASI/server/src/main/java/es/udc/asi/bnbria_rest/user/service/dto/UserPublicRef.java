package es.udc.asi.bnbria_rest.user.service.dto;

import java.util.Date;

public record UserPublicRef(
  Long id,
  String name,
  String surname,
  String surname2,
  Date creationDate
) {
}
