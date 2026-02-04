package es.udc.asi.bnbria_rest.user.service.dto;


import java.util.Date;

public record UserAdminRef(
  Long id,
  String email,
  Date creationDate,
  String authority,
  boolean active
) {
}
