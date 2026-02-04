package es.udc.asi.bnbria_rest.userreview.service.dto;

import es.udc.asi.bnbria_rest.booking.service.dto.BookingPublicRef;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;

public record UserReviewRef(
  Long id,
  UserPublicRef reviewed,
  String propertyTitle,
  BookingPublicRef booking
) {
}
