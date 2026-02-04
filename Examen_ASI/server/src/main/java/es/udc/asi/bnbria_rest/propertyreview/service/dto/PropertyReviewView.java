package es.udc.asi.bnbria_rest.propertyreview.service.dto;

import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;

import java.util.Date;

public record PropertyReviewView(
  String description,
  Integer rating,
  Integer cleanliness,
  Integer hospitality,
  Integer location,
  Date creationDate,
  UserPublicRef reviewer
) {
}
