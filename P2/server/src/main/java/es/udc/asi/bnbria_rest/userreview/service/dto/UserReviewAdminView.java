package es.udc.asi.bnbria_rest.userreview.service.dto;

public record UserReviewAdminView(
  Long id,
  String reviewerEmail,
  String reviewedEmail,
  String description,
  Integer rating
) {
}
