package es.udc.asi.bnbria_rest.property.service.dto;

import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PropertyCreateRequest(
  @NotBlank String title,
  @NotEmpty String description,
  @NotNull Long bathrooms,
  @NotNull Long bedrooms,
  @NotNull Long maxOccupancy,
  @NotNull Long squareMeters,
  @NotNull PropertyType type,
  @NotNull String door,
  @NotNull String number,
  @NotNull String street,
  @NotNull String city,
  @NotNull String province,
  @NotNull String country,
  @NotNull Long postalCode
) {
}
