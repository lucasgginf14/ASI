package es.udc.asi.bnbria_rest.userreview.service;

import es.udc.asi.bnbria_rest.booking.persistence.dao.BookingDao;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingPublicRef;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;
import es.udc.asi.bnbria_rest.userreview.persistence.dao.UserReviewDao;
import es.udc.asi.bnbria_rest.userreview.persistence.entity.UserReview;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewAdminView;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewCreateRequest;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewRef;
import es.udc.asi.bnbria_rest.userreview.service.dto.UserReviewView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserReviewService {

  @Autowired
  private UserReviewDao userReviewDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private BookingDao bookingDao;

  @Autowired
  private UserService userService;

  @PreAuthorize("hasAuthority('ADMIN')")
  public void deleteReviewById(Long id) throws NotFoundException {
    UserReview userReview = userReviewDao.findById(id);
    if (userReview == null) {
      throw new NotFoundException("UserReview with id " + id + " not found", UserReview.class);
    }
    Booking b = userReview.getBooking();
    if (b != null) {
      b.setReview(null);
      bookingDao.update(b);
    } else {
      throw new NotFoundException("Booking with id " + id + " not found", Booking.class);
    }
    userReviewDao.delete(userReview);
  }

  public UserReviewView create(Long id, UserReviewCreateRequest request) throws NotFoundException {
    UserReview userReview = userReviewDao.findById(id);

    if (userReview == null) {
      throw new NotFoundException("UserReview with id " + id + " not found", UserReview.class);
    }

    if (userReview.getDescription() != null) {
      throw new RuntimeException("UserReview with id " + id + " is actually created");
    }

    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();

    if (!userReview.getReviewer().getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("UserReview with id " + id + " not assigned to current user");
    }

    if (request.rating() < 1 || request.rating() > 5) {
      throw new RuntimeException("Rating must be between 1 and 5");
    }
    if (request.description().isEmpty()) {
      throw new RuntimeException("Description cannot be empty");
    } else if (request.description().length() < 20) {
      throw new RuntimeException("Description must be at least 20 characters long");
    }

    userReview.setDescription(request.description());
    userReview.setRating(request.rating());
    userReview.setCreationDate(new Date(System.currentTimeMillis()));
    userReviewDao.update(userReview);

    return new UserReviewView(
      userReview.getDescription(),
      userReview.getRating(),
      userReview.getCreationDate(),
      null
    );
  }

  public List<UserReviewRef> findPendingReviewsByReviewer() throws NotFoundException {
    UserDTOPrivate user = userService.getCurrentUserWithAuthority();

    if (user == null) {
      throw new NotFoundException("User not found", User.class);
    }

    List<UserReview> pendingReviews = userReviewDao.findPendingReviewsByReviewer(
      userDao.findById(user.getId())
    ).stream().toList();

    return pendingReviews.stream()
      .map(ur -> new UserReviewRef(
        ur.getId(),
        new UserPublicRef(
          ur.getBooking().getGuest().getId(),
          ur.getBooking().getGuest().getName(),
          ur.getBooking().getGuest().getSurname(),
          ur.getBooking().getGuest().getSurname_2(),
          ur.getBooking().getGuest().getCreationDate()
        ),
        ur.getBooking().getProperty().getTitle(),
        new BookingPublicRef(
          ur.getBooking().getId(),
          ur.getBooking().getStartDate(),
          ur.getBooking().getEndDate()
        )
      ))
      .toList();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public List<UserReviewAdminView> findAll() {
    return userReviewDao.findAll().stream()
      .map(ur -> new UserReviewAdminView(
        ur.getId(),
        ur.getReviewer().getEmail(),
        ur.getBooking().getGuest().getEmail(),
        ur.getDescription(),
        ur.getRating()
      )).toList();
  }
}
