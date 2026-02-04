package es.udc.asi.bnbria_rest.user.service.dto;

import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewView;

import java.util.Date;
import java.util.List;

public record UserOwnerView(
  Long id,
  String email,
  String name,
  String surname,
  String surname2,
  String phone,
  Date birthday,
  Date creationDate,
  List<UserReviewView> reviews,
  Double averageRating
) {
}
