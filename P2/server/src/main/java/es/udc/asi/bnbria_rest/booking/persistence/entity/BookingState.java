package es.udc.asi.bnbria_rest.booking.persistence.entity;

public enum BookingState {
  PENDING,
  CONFIRMED,
  CANCELED_BY_GUEST,
  CANCELED_BY_HOST,
  CANCELED_BY_ADMIN,
  CANCELED_BY_SYSTEM,
  IN_PROGRESS,
  COMPLETED
}
