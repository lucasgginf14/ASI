package es.udc.asi.bnbria_rest.user.service;

import es.udc.asi.bnbria_rest.booking.persistence.dao.BookingDao;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingGuestRef;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.asi.bnbria_rest.common.exceptions.model.UserEmailExistsException;
import es.udc.asi.bnbria_rest.common.security.SecurityUtils;
import es.udc.asi.bnbria_rest.message.persistence.dao.MessageDao;
import es.udc.asi.bnbria_rest.property.persistence.dao.PropertyDao;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyAdminRef;
import es.udc.asi.bnbria_rest.propertyreview.persistence.dao.PropertyReviewDao;
import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.persistence.entity.UserAuthority;
import es.udc.asi.bnbria_rest.user.service.dto.StatsAdminView;
import es.udc.asi.bnbria_rest.user.service.dto.UserAdminRef;
import es.udc.asi.bnbria_rest.user.service.dto.UserAdminView;
import es.udc.asi.bnbria_rest.user.service.dto.UserCreateRequest;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPublic;
import es.udc.asi.bnbria_rest.user.service.dto.UserInfoView;
import es.udc.asi.bnbria_rest.user.service.dto.UserOwnerView;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicView;
import es.udc.asi.bnbria_rest.user.service.dto.UserUpdatePasswordRequest;
import es.udc.asi.bnbria_rest.user.service.dto.UserUpdateRequest;
import es.udc.asi.bnbria_rest.userreview.persistence.dao.UserReviewDao;
import es.udc.asi.bnbria_rest.userreview.persistence.entity.UserReview;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MessageDao messageDao;

  @Autowired
  private UserReviewDao userReviewDao;

  @Autowired
  private PropertyReviewDao propertyReviewDao;

  @Autowired
  private PropertyDao propertyDao;

  @Autowired
  private BookingDao bookingDao;

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = true)
  public List<UserAdminRef> findAll() {
    return userDao.findAll().stream()
      .map(user -> new UserAdminRef(
        user.getId(),
        user.getEmail(),
        user.getCreationDate(),
        user.getAuthority().name(),
        user.isActive()
      ))
      .collect(Collectors.toList());
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = true)
  public UserAdminView findByEmail(String email) throws NotFoundException {
    User user = userDao.findByEmail(email);

    if (user == null) {
      throw new NotFoundException(email, User.class);
    }

    return new UserAdminView(
      user.getId(),
      user.getEmail(),
      user.getName(),
      user.getSurname(),
      user.getSurname_2(),
      user.getPhone(),
      user.getBirthday(),
      user.getCreationDate(),
      user.getAuthority().name(),
      user.isActive(),
      user.getProperties().stream()
        .map(property -> new PropertyAdminRef(
          property.getId(),
          property.getTitle(),
          property.getState(),
          property.getDoor(),
          property.getNumber(),
          property.getStreet(),
          property.getCity(),
          property.getProvince(),
          property.getCountry(),
          null
        ))
        .collect(Collectors.toList()),
      user.getBookings().stream()
        .map(booking -> new BookingGuestRef(
          booking.getId(),
          booking.getProperty().getTitle(),
          booking.getStartDate(),
          booking.getEndDate(),
          booking.getState()
        ))
        .collect(Collectors.toList())
    );
  }

  @Transactional(readOnly = true)
  public UserPublicView findById(Long id) throws NotFoundException {
    User user = userDao.findById(id);
    if (user == null) {
      throw new NotFoundException(id.toString(), User.class);
    }
    if (!user.isActive() && !SecurityUtils.getCurrentUserIsAdmin()) {
      throw new NotFoundException(id.toString(), User.class);
    }
    if (user.getAuthority().equals(UserAuthority.ADMIN)) {
      throw new NotFoundException(id.toString(), User.class);
    }

    return new UserPublicView(
      user.getId(),
      user.getName(),
      user.getSurname(),
      user.getSurname_2(),
      user.getCreationDate(),
      user.getBookings().stream()
        .map(Booking::getReview)
        .filter(review -> review != null && review.getDescription() != null)
        .map(review -> new UserReviewView(
          review.getDescription(),
          review.getRating(),
          review.getCreationDate(),
          new UserPublicRef(
            review.getReviewer().getId(),
            review.getReviewer().getName(),
            review.getReviewer().getSurname(),
            review.getReviewer().getSurname_2(),
            review.getReviewer().getCreationDate()
          )
        ))
        .collect(Collectors.toList()),
      user.getBookings().stream()
        .filter(booking -> booking.getState() == BookingState.COMPLETED)
        .map(Booking::getReview)
        .filter(review -> review != null && review.getRating() != null)
        .mapToDouble(UserReview::getRating)
        .average()
        .orElse(0.0)
    );
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = true)
  public UserAdminView privateFindById(Long id) throws NotFoundException {
    User user = userDao.findById(id);
    if (user == null) {
      throw new NotFoundException(id.toString(), User.class);
    }
    return new UserAdminView(
      user.getId(),
      user.getEmail(),
      user.getName(),
      user.getSurname(),
      user.getSurname_2(),
      user.getPhone(),
      user.getBirthday(),
      user.getCreationDate(),
      user.getAuthority().name(),
      user.isActive(),
      user.getProperties().stream()
        .map(property -> new PropertyAdminRef(
          property.getId(),
          property.getTitle(),
          property.getState(),
          property.getDoor(),
          property.getNumber(),
          property.getStreet(),
          property.getCity(),
          property.getProvince(),
          property.getCountry(),
          null
        ))
        .collect(Collectors.toList()),
      user.getBookings().stream()
        .map(booking -> new BookingGuestRef(
          booking.getId(),
          booking.getProperty().getTitle(),
          booking.getStartDate(),
          booking.getEndDate(),
          booking.getState()
        ))
        .collect(Collectors.toList())
    );
  }

  @Transactional(readOnly = true)
  public UserOwnerView findMe() throws NotFoundException {
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser == null) {
      throw new NotFoundException("Current user", User.class);
    }
    User user = userDao.findById(currentUser.getId());
    return new UserOwnerView(
      user.getId(),
      user.getEmail(),
      user.getName(),
      user.getSurname(),
      user.getSurname_2(),
      user.getPhone(),
      user.getBirthday(),
      user.getCreationDate(),
      user.getBookings().stream()
        .map(Booking::getReview)
        .filter(review -> review.getRating() != null)
        .map(review -> new UserReviewView(
          review.getDescription(),
          review.getRating(),
          review.getCreationDate(),
          new UserPublicRef(
            review.getReviewer().getId(),
            review.getReviewer().getName(),
            review.getReviewer().getSurname(),
            review.getReviewer().getSurname_2(),
            review.getReviewer().getCreationDate()
          )
        )).collect(Collectors.toList()),
      user.getBookings().stream()
        .map(Booking::getReview)
        .filter(review -> review.getRating() != null)
        .mapToDouble(UserReview::getRating).average()
        .orElse(0.0)
    );
  }

  public UserInfoView getUserInfo() throws NotFoundException {
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    User user = userDao.findById(currentUser.getId());
    if (user == null) {
      throw new NotFoundException("Current user", User.class);
    }

    Integer pendingMessages = messageDao.countUnreadConversationsByUser(user);

    Integer pendingUserReviews = userReviewDao.findPendingReviewsByReviewer(user).size();

    Integer pendingPropertyReviews = propertyReviewDao.findPendingReviewsByReviewer(user).size();

    Integer pendingBookings = bookingDao.findPendingBookingsByOwner(user).size();

    return new UserInfoView(
      user.getName(),
      user.getSurname(),
      user.getSurname_2(),
      user.getCreationDate(),
      user.getProperties() != null && !user.getProperties().isEmpty(),
      pendingMessages,
      pendingUserReviews,
      pendingPropertyReviews,
      pendingBookings
    );
  }

  public UserOwnerView updateMe(UserUpdateRequest request) throws NotFoundException {
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser == null) {
      throw new NotFoundException("Current user", User.class);
    }
    User user = userDao.findById(currentUser.getId());

    if (request.name() != null && !request.name().isEmpty()) {
      user.setName(request.name().trim());
    }
    if (request.surname() != null && !request.surname().isEmpty()) {
      user.setSurname(request.surname().trim());
    }
    if (request.surname2() != null && !request.surname2().isEmpty()) {
      user.setSurname_2(request.surname2().trim());
    } else if (request.surname2() == null && user.getSurname_2() != null) {
      user.setSurname_2(null);
    }
    if (request.phone() != null && !request.phone().isEmpty()) {
      user.setPhone(request.phone().trim());
    }
    if (request.birthday() != null) {
      LocalDate birthday = request.birthday().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
      LocalDate today = LocalDate.now();
      int age = Period.between(birthday, today).getYears();
      if (birthday.isAfter(today)) {
        throw new IllegalArgumentException("The birthday cannot be in the future");
      }
      if (age < 18) {
        throw new IllegalArgumentException("User must be at least 18 years old");
      }
      user.setBirthday(request.birthday());
    }
    userDao.update(user);
    return new UserOwnerView(
      user.getId(),
      user.getEmail(),
      user.getName(),
      user.getSurname(),
      user.getSurname_2(),
      user.getPhone(),
      user.getBirthday(),
      user.getCreationDate(),
      null,
      null
    );
  }

  public void updateMyPassword(UserUpdatePasswordRequest request) throws NotFoundException, OperationNotAllowed {
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser == null) {
      throw new NotFoundException("Current user", User.class);
    }
    User user = userDao.findById(currentUser.getId());
    if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
      throw new OperationNotAllowed("Current password is incorrect");
    }
    if (request.newPassword().equals(request.currentPassword())) {
      throw new IllegalArgumentException("The new password must be different from the current password");
    }
    user.setPassword(passwordEncoder.encode(request.newPassword()));
    userDao.update(user);
  }

  public void registerUser(UserCreateRequest request) throws UserEmailExistsException {
    if (userDao.findByEmail(request.email()) != null) {
      throw new UserEmailExistsException(request.email());
    }
    if (!request.password().equals(request.confirmPassword())) {
      throw new IllegalArgumentException("Password and confirm password do not match");
    }

    if (request.name() == null || request.name().trim().isEmpty()) {
      throw new IllegalArgumentException("The name is obligatory");
    }

    if (request.surname() == null || request.surname().trim().isEmpty()) {
      throw new IllegalArgumentException("The first surname is obligatory");
    }

    if (request.email() == null) {
      throw new IllegalArgumentException("The email is obligatory");
    } else if (!request.email().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
      throw new IllegalArgumentException("The email format is not valid");
    }

    if (request.phone() == null) {
      throw new IllegalArgumentException("The phone number is obligatory");
    }

    if (request.birthday() == null) {
      throw new IllegalArgumentException("The birthday is obligatory");
    } else {
      LocalDate birthday = request.birthday().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
      LocalDate today = LocalDate.now();
      int age = Period.between(birthday, today).getYears();
      if (birthday.isAfter(today)) {
        throw new IllegalArgumentException("The birthday cannot be in the future");
      }
      if (age < 18) {
        throw new IllegalArgumentException("User must be at least 18 years old");
      }
      if (age > 120) {
        throw new IllegalArgumentException("The birthday is not valid");
      }
    }

    User user = new User();

    user.setName(request.name().trim());
    user.setSurname(request.surname().trim());
    user.setSurname_2(request.surname_2().trim());
    user.setEmail(request.email().trim());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setPhone(request.phone().trim());
    user.setBirthday(request.birthday());
    user.setActive(true);
    user.setAuthority(UserAuthority.USER);
    user.setCreationDate(new Date(System.currentTimeMillis()));
    userDao.create(user);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public UserDTOPublic updateActive(Long id, boolean active) throws NotFoundException, OperationNotAllowed {
    User user = userDao.findById(id);
    if (user == null) {
      throw new NotFoundException(id.toString(), User.class);
    }

    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser.getId().equals(user.getId())) {
      throw new OperationNotAllowed("The user cannot activate/deactive itself");
    }

    user.setActive(active);
    userDao.update(user);
    return new UserDTOPublic(user);
  }

  @Transactional(readOnly = true)
  public UserDTOPrivate getCurrentUserWithAuthority() {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();
    if (currentUserLogin != null) {
      return new UserDTOPrivate(userDao.findByEmail(currentUserLogin));
    }
    return null;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public void deleteById(Long id) throws NotFoundException, OperationNotAllowed {
    User theUser = userDao.findById(id);
    if (theUser == null) {
      throw new NotFoundException(id.toString(), User.class);
    }
    UserDTOPrivate currentUser = getCurrentUserWithAuthority();
    if (currentUser.getId().equals(theUser.getId())) {
      throw new OperationNotAllowed("The user cannot remove itself");
    }

    userDao.delete(theUser);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public StatsAdminView getAppStats() {
    return new StatsAdminView(
      userDao.countActiveUsers(),
      propertyDao.countAllProperties(),
      propertyDao.countApprovedProperties(),
      propertyDao.countPendingProperties(),
      bookingDao.countTotalBookings(),
      bookingDao.countCompletedBookings(),
      userReviewDao.countUserReviews(),
      propertyReviewDao.countPropertyReviews()
    );
  }
}
