package es.udc.asi.bnbria_rest.user.service.dto;

import java.util.Date;

public record UserInfoView(
  String name,
  String surname,
  String surname2,
  Date creationDate,
  Boolean hasProperties,
  Integer pendingMessages,
  Integer pendingUserReviews,
  Integer pendingPropertyReviews,
  Integer pendingBookings
) {
}
