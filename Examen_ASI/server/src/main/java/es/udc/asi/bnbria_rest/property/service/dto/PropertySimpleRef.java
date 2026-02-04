package es.udc.asi.bnbria_rest.property.service.dto;

public record PropertySimpleRef(
  Long id,
  String title,
  String door,
  String number,
  String street,
  String city,
  String province,
  String country,
  Double latitude,
  Double longitude
) {
}
