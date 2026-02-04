package es.udc.asi.bnbria_rest.message.service.dto;

import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;

import java.time.LocalDateTime;

public record ConversationRef(
  Long bookingId,
  String propertyTitle,
  UserPublicRef user,
  Boolean hasUnreadMessages,
  LocalDateTime lastMessage
) {
}
