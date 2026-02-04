package es.udc.asi.bnbria_rest.booking.service;

import es.udc.asi.bnbria_rest.availability.persistence.dao.AvailabilityDao;
import es.udc.asi.bnbria_rest.availability.persistence.entity.Availability;
import es.udc.asi.bnbria_rest.booking.persistence.dao.BookingDao;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingAdminRef;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingAdminView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingCancelRequest;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingGuestRef;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingGuestView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingOwnerRef;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingOwnerView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingPrivateView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingPublicRef;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingRequest;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.property.persistence.dao.PropertyDao;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.property.service.dto.PropertySimpleRef;
import es.udc.asi.bnbria_rest.propertyreview.persistence.dao.PropertyReviewDao;
import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;
import es.udc.asi.bnbria_rest.userreview.persistence.dao.UserReviewDao;
import es.udc.asi.bnbria_rest.userreview.persistence.entity.UserReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BookingService {
  private static final Logger log = LoggerFactory.getLogger(BookingService.class);
  @Autowired
  private BookingDao bookingDao;

  @Autowired
  private PropertyDao propertyDao;

  @Autowired
  private AvailabilityDao availabilityDao;

  @Autowired
  private UserService userService;

  @Autowired
  private UserReviewDao userReviewDao;

  @Autowired
  private PropertyReviewDao propertyReviewDao;

  @Autowired
  private UserDao userDao;

  public List<BookingPublicRef> getFutureBookingsByProperty(Property property) {
    return bookingDao.getFutureBookingsByProperty(property)
      .stream().map(
        booking -> new BookingPublicRef(
          booking.getId(),
          booking.getStartDate(),
          booking.getEndDate()
        )
      ).toList();
  }

  private double getPrice(Property property, BookingRequest request) {
    Availability availability = availabilityDao.findByPropertyAndStartDate(property, request.startDate());
    if (availability == null || availability.getEndDate().isBefore(request.startDate())) {
      throw new IllegalArgumentException("The property is not available in the given date range");
    }

    double totalPrice = 0.0;
    LocalDate current = request.startDate();

    while (!current.isAfter(request.endDate().minusDays(1))) {
      Availability currentAvailability = availabilityDao.findByPropertyAndStartDate(property, current);
      if (currentAvailability == null || current.isBefore(currentAvailability.getStartDate()) || current.isAfter(currentAvailability.getEndDate())) {
        throw new IllegalArgumentException("No availability for the booking");
      }
      totalPrice += currentAvailability.getPrice();
      current = current.plusDays(1);
    }

    return totalPrice;
  }

  public BookingPrivateView create(Long propertyId, BookingRequest request) {
    Property property = propertyDao.findById(propertyId);
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();
    User guest = userDao.findById(user.getId());

    if (guest == null) {
      throw new IllegalArgumentException("Guest user not found");
    }

    if (user.getAuthority().equals("ADMIN")) {
      throw new IllegalArgumentException("Admins cannot create bookings");
    }

    if (property.getOwner().getId().equals(guest.getId())) {
      throw new IllegalArgumentException("Property owners cannot book their own properties");
    }

    if (request.endDate().isBefore(request.startDate())) {
      throw new IllegalArgumentException("End date cannot be before start date");
    }

    if (bookingDao.existsBookingInDateRange(property, request.startDate(), request.endDate())) {
      throw new IllegalArgumentException("There is already a booking in the given date range");
    }

    if (property.getMaxOccupancy() < request.numGuests()) {
      throw new IllegalArgumentException("Number of guests exceeds the property's maximum occupancy");
    }

    Double price = getPrice(property, request);

    Booking booking = new Booking();
    booking.setProperty(property);
    booking.setGuest(guest);
    booking.setStartDate(request.startDate());
    booking.setEndDate(request.endDate());
    booking.setRequestText(request.requestText());
    booking.setRequestMoment(LocalDateTime.now());
    booking.setNumGuests(request.numGuests());
    booking.setState(BookingState.PENDING);
    booking.setPrice(price);

    bookingDao.create(booking);

    return new BookingPrivateView(
      booking.getId(),
      booking.getState(),
      booking.getPrice(),
      booking.getRequestText(),
      booking.getCancellationReason(),
      booking.getStartDate(),
      booking.getEndDate(),
      booking.getNumGuests()
    );
  }

  public void cancel(Long bookingId, BookingCancelRequest request) throws NotFoundException {

    Booking booking = bookingDao.findById(bookingId);
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();

    if (booking == null) {
      throw new NotFoundException(bookingId.toString(), Booking.class);
    }

    if (booking.getState() != BookingState.PENDING && booking.getState() != BookingState.CONFIRMED) {
      throw new IllegalArgumentException("The booking cannot be canceled");
    }

    // TODO: Si el huésped cancela, ¿ponemos las canceladas por el sistema como disponibles?

    if (booking.getGuest().getId().equals(user.getId())) {
      booking.setState(BookingState.CANCELED_BY_GUEST);
    } else if (booking.getProperty().getOwner().getId().equals(user.getId())) {
      booking.setState(BookingState.CANCELED_BY_HOST);
    } else if (user.getAuthority().equals("ADMIN")) {
      booking.setState(BookingState.CANCELED_BY_ADMIN);
    } else {
      throw new IllegalArgumentException("Only the guest or the property owner can cancel the booking");
    }

    booking.setCancellationReason(request.reason());
    bookingDao.update(booking);
  }

  public void accept(Long bookingId) {
    Booking booking = bookingDao.findById(bookingId);
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();

    if (!booking.getProperty().getOwner().getId().equals(user.getId())) {
      throw new IllegalArgumentException("Only the property owner can approve the booking");
    }
    if (booking.getState() != BookingState.PENDING) {
      throw new IllegalArgumentException("Only pending bookings can be approved");
    }

    List<Booking> toCancelBookings = bookingDao.getBookingsByPropertyAndDateRange(
        booking.getProperty(),
        booking.getStartDate(),
        booking.getEndDate()
      ).stream()
      .filter(b -> !b.getId().equals(booking.getId()) && b.getState() == BookingState.PENDING)
      .toList();

    for (Booking b : toCancelBookings) {
      b.setState(BookingState.CANCELED_BY_SYSTEM);
      b.setCancellationReason("Owner approved another booking for this dates");
      bookingDao.update(b);
    }

    booking.setState(BookingState.CONFIRMED);
    bookingDao.update(booking);
  }

  /*
   * Se ejecuta cada 5 minutos para comprobar la funcionalidad,
   * en un entorno real sería a las 12 del mediodía
   */
  @Scheduled(cron = "0 */5 * * * *", zone = "Europe/Madrid")
  public void dailyBookingStatusCheck() {
    log.info("Ejecutando tarea programada: dailyBookingStatusCheck");
    LocalDate now = LocalDate.now();

    List<Booking> bookingsToCancel = bookingDao.getBookingsToCancelByDate(now).stream().toList();
    List<Booking> bookingsToInProgress = bookingDao.getBookingsToInProgressByDate(now).stream().toList();
    List<Booking> bookingsToComplete = bookingDao.getBookingsToCompleteByDate(now).stream().toList();

    for (Booking booking : bookingsToCancel) {
      booking.setState(BookingState.CANCELED_BY_SYSTEM);
      booking.setCancellationReason("Canceled by system due to no confirmation before start date");
      bookingDao.update(booking);
    }

    for (Booking booking : bookingsToInProgress) {
      booking.setState(BookingState.IN_PROGRESS);
      bookingDao.update(booking);
    }

    for (Booking booking : bookingsToComplete) {
      booking.setState(BookingState.COMPLETED);
      bookingDao.update(booking);

      PropertyReview propertyReview = new PropertyReview();
      propertyReview.setReviewer(booking.getGuest());
      propertyReview.setProperty(booking.getProperty());
      propertyReview.setCreationDate(new Date());
      propertyReviewDao.create(propertyReview);

      UserReview userReview = new UserReview();
      userReview.setReviewer(booking.getProperty().getOwner());
      userReview.setBooking(booking);
      userReview.setCreationDate(new Date());
      userReviewDao.create(userReview);
    }
  }

  @Transactional(readOnly = true)
  public List<BookingOwnerRef> findPendingBookingsByOwner() {
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();
    User owner = userDao.findById(user.getId());

    return bookingDao.findPendingBookingsByOwner(owner)
      .stream()
      .sorted(Comparator.comparing(Booking::getRequestMoment).reversed())
      .map(booking -> new BookingOwnerRef(
        booking.getId(),
        booking.getState(),
        booking.getStartDate(),
        booking.getEndDate(),
        booking.getRequestMoment(),
        booking.getRequestText(),
        new UserPublicRef(
          booking.getGuest().getId(),
          booking.getGuest().getName(),
          booking.getGuest().getSurname(),
          booking.getGuest().getSurname_2(),
          booking.getGuest().getCreationDate()
        )
      ))
      .toList();
  }

  @Transactional(readOnly = true)
  public List<BookingOwnerRef> findAllBookingsByOwner() {
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();
    User owner = userDao.findById(user.getId());

    return bookingDao.findBookingsByOwner(owner)
      .stream()
      .sorted(Comparator.comparing(Booking::getRequestMoment).reversed())
      .map(booking -> new BookingOwnerRef(
        booking.getId(),
        booking.getState(),
        booking.getStartDate(),
        booking.getEndDate(),
        booking.getRequestMoment(),
        booking.getRequestText(),
        new UserPublicRef(
          booking.getGuest().getId(),
          booking.getGuest().getName(),
          booking.getGuest().getSurname(),
          booking.getGuest().getSurname_2(),
          booking.getGuest().getCreationDate()
        )
      ))
      .toList();
  }

  @Transactional(readOnly = true)
  public BookingOwnerView findBookingByIdAsOwner(Long bookingId) {
    Booking booking = bookingDao.findById(bookingId);
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();
    User owner = userDao.findById(user.getId());

    if (!booking.getProperty().getOwner().getId().equals(owner.getId())) {
      throw new IllegalArgumentException("Only the property owner can view the booking details");
    }

    return new BookingOwnerView(
      booking.getId(),
      booking.getState(),
      booking.getStartDate(),
      booking.getEndDate(),
      booking.getRequestMoment(),
      booking.getPrice(),
      booking.getNumGuests(),
      booking.getRequestText(),
      booking.getCancellationReason(),
      new UserPublicRef(
        booking.getGuest().getId(),
        booking.getGuest().getName(),
        booking.getGuest().getSurname(),
        booking.getGuest().getSurname_2(),
        booking.getGuest().getCreationDate()
      ),
      new PropertySimpleRef(
        booking.getProperty().getId(),
        booking.getProperty().getTitle(),
        booking.getProperty().getDoor(),
        booking.getProperty().getNumber(),
        booking.getProperty().getStreet(),
        booking.getProperty().getCity(),
        booking.getProperty().getProvince(),
        booking.getProperty().getCountry(),
        booking.getProperty().getLatitude(),
        booking.getProperty().getLongitude()
      ),
      !booking.getMessages().isEmpty()
    );
  }

  @Transactional(readOnly = true)
  public BookingGuestView findBookingByIdAsGuest(Long bookingId) {
    Booking booking = bookingDao.findById(bookingId);
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();
    User guest = userDao.findById(user.getId());

    if (!booking.getGuest().getId().equals(guest.getId())) {
      throw new IllegalArgumentException("Only the booking guest can view the booking details");
    }

    return new BookingGuestView(
      booking.getId(),
      new PropertySimpleRef(
        booking.getProperty().getId(),
        booking.getProperty().getTitle(),
        booking.getProperty().getDoor(),
        booking.getProperty().getNumber(),
        booking.getProperty().getStreet(),
        booking.getProperty().getCity(),
        booking.getProperty().getProvince(),
        booking.getProperty().getCountry(),
        booking.getProperty().getLatitude(),
        booking.getProperty().getLongitude()
      ),
      booking.getState(),
      booking.getRequestText(),
      booking.getCancellationReason(),
      booking.getPrice(),
      booking.getStartDate(),
      booking.getEndDate(),
      booking.getNumGuests(),
      !booking.getMessages().isEmpty()
    );
  }

  @Transactional(readOnly = true)
  public List<BookingGuestRef> findAllBookingsByGuest() {
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();
    User guest = userDao.findById(user.getId());

    return bookingDao.findBookingsByGuest(guest)
      .stream()
      .sorted(Comparator.comparing(Booking::getRequestMoment).reversed())
      .map(booking -> new BookingGuestRef(
        booking.getId(),
        booking.getProperty().getTitle(),
        booking.getStartDate(),
        booking.getEndDate(),
        booking.getState()
      ))
      .toList();
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('ADMIN')")
  public List<BookingAdminRef> findAll() {
    return bookingDao.findAll()
      .stream()
      .sorted(Comparator.comparing(Booking::getRequestMoment).reversed())
      .map(booking -> new BookingAdminRef(
        booking.getId(),
        booking.getProperty().getTitle(),
        booking.getGuest().getName()
          + " " + booking.getGuest().getSurname()
          + " " + booking.getGuest().getSurname_2(),
        booking.getProperty().getOwner().getName()
          + " " + booking.getProperty().getOwner().getSurname()
          + " " + booking.getProperty().getOwner().getSurname_2(),
        booking.getState(),
        booking.getStartDate(),
        booking.getEndDate()
      ))
      .toList();
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('ADMIN')")
  public BookingAdminView findByIdAsAdmin(Long bookingId) throws NotFoundException {
    Booking booking = bookingDao.findById(bookingId);

    if (booking == null) {
      throw new NotFoundException(bookingId.toString(), Booking.class);
    }

    return new BookingAdminView(
      booking.getId(),
      booking.getState(),
      booking.getStartDate(),
      booking.getEndDate(),
      booking.getRequestMoment(),
      booking.getPrice(),
      booking.getNumGuests(),
      booking.getRequestText(),
      booking.getCancellationReason(),
      new UserPublicRef(
        booking.getProperty().getOwner().getId(),
        booking.getProperty().getOwner().getName(),
        booking.getProperty().getOwner().getSurname(),
        booking.getProperty().getOwner().getSurname_2(),
        booking.getProperty().getOwner().getCreationDate()
      ),
      new UserPublicRef(
        booking.getGuest().getId(),
        booking.getGuest().getName(),
        booking.getGuest().getSurname(),
        booking.getGuest().getSurname_2(),
        booking.getGuest().getCreationDate()
      ),
      new PropertySimpleRef(
        booking.getProperty().getId(),
        booking.getProperty().getTitle(),
        booking.getProperty().getDoor(),
        booking.getProperty().getNumber(),
        booking.getProperty().getStreet(),
        booking.getProperty().getCity(),
        booking.getProperty().getProvince(),
        booking.getProperty().getCountry(),
        booking.getProperty().getLatitude(),
        booking.getProperty().getLongitude()
      )
    );
  }
}
