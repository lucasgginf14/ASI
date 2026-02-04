package es.udc.asi.bnbria_rest.booking.persistence.dao;

import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;

import java.time.LocalDate;
import java.util.Collection;

public interface BookingDao {

  Collection<Booking> getFutureBookingsByProperty(Property property);

  Boolean existsBookingInDateRange(Property property, LocalDate startDate, LocalDate endDate);

  Collection<Booking> getBookingsByPropertyAndDateRange(Property property, LocalDate startDate, LocalDate endDate);

  Collection<Booking> getBookingsToCancelByDate(LocalDate date);

  Collection<Booking> getBookingsToInProgressByDate(LocalDate date);

  Collection<Booking> getBookingsToCompleteByDate(LocalDate date);

  Collection<Booking> findPendingBookingsByOwner(User owner);

  Collection<Booking> findBookingsByOwner(User owner);

  Collection<Booking> findBookingsByGuest(User guest);

  Collection<Booking> findBookingsInDateRange(Property property, LocalDate startDate, LocalDate endDate);

  Collection<Booking> findAll();

  Booking findById(Long id);

  void create(Booking booking);

  Booking update(Booking booking);

  void delete(Booking booking);

  Long countTotalBookings();

  Long countCompletedBookings();
}
