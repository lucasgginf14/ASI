package es.udc.asi.bnbria_rest.propertyreview.service.dto;

import es.udc.asi.bnbria_rest.property.service.dto.PropertySimpleRef;

public record PropertyReviewRef(
  Long id,
  PropertySimpleRef property
) {
}
