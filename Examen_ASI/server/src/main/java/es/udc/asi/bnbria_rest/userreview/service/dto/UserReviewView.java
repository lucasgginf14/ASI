package es.udc.asi.bnbria_rest.userreview.service.dto;

import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;

import java.util.Date;

public record UserReviewView(
  String description,
  Integer rating,
  Date creationDate,
  UserPublicRef reviewer
) {
}
