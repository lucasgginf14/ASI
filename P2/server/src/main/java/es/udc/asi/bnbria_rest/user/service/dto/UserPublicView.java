package es.udc.asi.bnbria_rest.user.service.dto;

import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewView;

import java.util.Date;
import java.util.List;

public record UserPublicView(
  Long id,
  String name,
  String surname,
  String surname2,
  Date creationDate,
  List<UserReviewView> reviews,
  Double averageRating
) {
}
