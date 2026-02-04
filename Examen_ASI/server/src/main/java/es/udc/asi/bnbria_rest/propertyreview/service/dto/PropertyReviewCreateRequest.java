package es.udc.asi.bnbria_rest.propertyreview.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PropertyReviewCreateRequest(
  @NotEmpty String description,
  @NotNull Integer rating,
  @NotNull Integer cleanliness,
  @NotNull Integer hospitality,
  @NotNull Integer location
) {
}
