package es.udc.asi.bnbria_rest.message.service.dto;

import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;

import java.time.LocalDateTime;

public record MessageView(
  UserPublicRef writer,
  String text,
  Boolean read,
  LocalDateTime sentAt
) {
}
