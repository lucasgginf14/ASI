package es.udc.asi.bnbria_rest.userreview.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserReviewCreateRequest(
  @NotEmpty String description,
  @NotNull Integer rating
) {

}
