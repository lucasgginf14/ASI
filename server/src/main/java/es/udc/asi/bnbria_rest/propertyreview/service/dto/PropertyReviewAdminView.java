package es.udc.asi.bnbria_rest.propertyreview.service.dto;

public record PropertyReviewAdminView(
  Long id,
  String reviewerEmail,
  String propertyTitle,
  String description,
  Integer rating
) {
}
