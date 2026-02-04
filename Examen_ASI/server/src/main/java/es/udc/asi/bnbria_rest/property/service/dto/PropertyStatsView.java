package es.udc.asi.bnbria_rest.property.service.dto;

public record PropertyStatsView(
  String title,
  String city,
  Long totalBookings,
  Double totalIncome
) {}
