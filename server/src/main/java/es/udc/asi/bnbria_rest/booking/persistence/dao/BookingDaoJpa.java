package es.udc.asi.bnbria_rest.booking.persistence.dao;

import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Repository
public class BookingDaoJpa extends GenericDaoJpa implements BookingDao {

  @Override
  public Collection<Booking> getFutureBookingsByProperty(Property property) {
    LocalDate now = LocalDate.now().withDayOfMonth(1);
    return entityManager.createQuery(
        "from Booking b where b.property.id = :propertyId and b.startDate >= :now and b.state = :state", Booking.class)
      .setParameter("propertyId", property.getId())
      .setParameter("now", now)
      .setParameter("state", BookingState.CONFIRMED)
      .getResultList();
  }

  @Override
  public Boolean existsBookingInDateRange(Property property, LocalDate startDate, LocalDate endDate) {
    List<Booking> bookings = entityManager.createQuery(
        "from Booking b where b.property.id = :propertyId " +
          "and b.startDate <= :endDate and b.endDate >= :startDate and b.state in (:states)", Booking.class)
      .setParameter("propertyId", property.getId())
      .setParameter("startDate", startDate)
      .setParameter("endDate", endDate)
      .setParameter("states", Arrays.asList(BookingState.IN_PROGRESS, BookingState.CONFIRMED))
      .getResultList();
    return !bookings.isEmpty();
  }

  @Override
  public Collection<Booking> getBookingsByPropertyAndDateRange(Property property, LocalDate startDate, LocalDate endDate) {
    return entityManager.createQuery(
        "from Booking b where b.property.id = :propertyId and (" +
          "(b.startDate between :startDate and :endDate) or " +
          "(b.endDate between :startDate and :endDate) or " +
          "(b.startDate <= :startDate and b.endDate >= :endDate))", Booking.class)
      .setParameter("propertyId", property.getId())
      .setParameter("startDate", startDate)
      .setParameter("endDate", endDate)
      .getResultList();
  }

  @Override
  public Collection<Booking> getBookingsToCancelByDate(LocalDate date) {
    return entityManager.createQuery(
        "from Booking b where b.state = :state and :date >= b.startDate", Booking.class)
      .setParameter("state", BookingState.PENDING)
      .setParameter("date", date)
      .getResultList();
  }

  @Override
  public Collection<Booking> getBookingsToInProgressByDate(LocalDate date) {
    return entityManager.createQuery(
        "from Booking b where b.state = :state and :date >= b.startDate", Booking.class)
      .setParameter("state", BookingState.CONFIRMED)
      .setParameter("date", date)
      .getResultList();
  }

  @Override
  public Collection<Booking> getBookingsToCompleteByDate(LocalDate date) {
    return entityManager.createQuery(
        "from Booking b where b.state = :state and :date >= b.endDate", Booking.class)
      .setParameter("state", BookingState.IN_PROGRESS)
      .setParameter("date", date)
      .getResultList();
  }

  @Override
  public Collection<Booking> findPendingBookingsByOwner(User owner) {
    LocalDate now = LocalDate.now();
    return entityManager.createQuery(
        "from Booking b where b.property.owner.id = :ownerId and b.state = :state and b.startDate >= :now", Booking.class)
      .setParameter("ownerId", owner.getId())
      .setParameter("state", BookingState.PENDING)
      .setParameter("now", now)
      .getResultList();
  }

  @Override
  public Collection<Booking> findBookingsByOwner(User owner) {
    return entityManager.createQuery(
        "from Booking b where b.property.owner.id = :ownerId", Booking.class)
      .setParameter("ownerId", owner.getId())
      .getResultList();
  }

  @Override
  public Collection<Booking> findBookingsByGuest(User guest) {
    return entityManager.createQuery(
        "from Booking b where b.guest.id = :guestId", Booking.class)
      .setParameter("guestId", guest.getId())
      .getResultList();
  }

  @Override
  public Collection<Booking> findBookingsInDateRange(Property property, LocalDate startDate, LocalDate endDate) {
    return entityManager.createQuery(
        "from Booking b where b.property.id = :propertyId and (" +
          "(b.startDate between :startDate and :endDate) or " +
          "(b.endDate between :startDate and :endDate) or " +
          "(b.startDate <= :startDate and b.endDate >= :endDate))", Booking.class)
      .setParameter("propertyId", property.getId())
      .setParameter("startDate", startDate)
      .setParameter("endDate", endDate)
      .getResultList();
  }

  @Override
  public Collection<Booking> findAll() {
    return entityManager.createQuery(
        "from Booking b", Booking.class)
      .getResultList();
  }

  @Override
  public Booking findById(Long id) {
    return entityManager.find(Booking.class, id);
  }

  @Override
  public void create(Booking booking) {
    entityManager.persist(booking);
  }

  @Override
  public Booking update(Booking booking) {
    return entityManager.merge(booking);
  }

  @Override
  public void delete(Booking booking) {
    entityManager.remove(booking);
  }

  @Override
  public Long countTotalBookings() {
    return entityManager.createQuery(
        "select count(b) from Booking b", Long.class)
      .getSingleResult();
  }

  @Override
  public Long countCompletedBookings() {
    return entityManager.createQuery(
        "select count(b) from Booking b where b.state = :state", Long.class)
      .setParameter("state", BookingState.COMPLETED)
      .getSingleResult();
  }
}
