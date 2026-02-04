package es.udc.asi.bnbria_rest.user.service.dto;

import es.udc.asi.bnbria_rest.booking.service.dto.BookingGuestRef;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyAdminRef;

import java.util.Date;
import java.util.List;

public record UserAdminView(
  Long id,
  String email,
  String name,
  String surname,
  String surname2,
  String phone,
  Date birthday,
  Date creationDate,
  String authority,
  boolean active,
  List<PropertyAdminRef> properties,
  List<BookingGuestRef> bookings
) {
}
