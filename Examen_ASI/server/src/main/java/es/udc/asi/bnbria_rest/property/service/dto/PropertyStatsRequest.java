package es.udc.asi.bnbria_rest.property.service.dto;

import java.util.List;

public record PropertyStatsRequest(
  List<String> cities,
  Integer year
) {}
