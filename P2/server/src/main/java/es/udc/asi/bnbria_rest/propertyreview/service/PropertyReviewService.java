package es.udc.asi.bnbria_rest.propertyreview.service;

import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.property.service.dto.PropertySimpleRef;
import es.udc.asi.bnbria_rest.propertyreview.persistence.dao.PropertyReviewDao;
import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewAdminView;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewCreateRequest;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewRef;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewView;
import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PropertyReviewService {

  @Autowired
  private PropertyReviewDao propertyReviewDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @PreAuthorize("hasAuthority('ADMIN')")
  public void deleteReviewById(Long id) throws NotFoundException {
    PropertyReview propertyReview = propertyReviewDao.findById(id);
    if (propertyReview == null) {
      throw new NotFoundException("PropertyReview with id " + id + " not found", PropertyReview.class);
    }
    propertyReviewDao.delete(propertyReview);
  }

  public PropertyReviewView create(Long id, PropertyReviewCreateRequest request) throws NotFoundException {
    PropertyReview propertyReview = propertyReviewDao.findById(id);

    if (propertyReview == null) {
      throw new NotFoundException("PropertyReview with id " + id + " not found", PropertyReview.class);
    }

    if (propertyReview.getDescription() != null) {
      throw new RuntimeException("PropertyReview with id " + id + " is actually created");
    }

    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();

    if (!propertyReview.getReviewer().getId().equals(currentUser.getId())) {
      throw new RuntimeException("PropertyReview with id " + id + " not assigned to current user");
    }

    if (request.rating() < 1 || request.rating() > 5) {
      throw new RuntimeException("Rating must be between 1 and 5");
    }
    if (request.cleanliness() < 1 || request.cleanliness() > 5) {
      throw new RuntimeException("Cleanliness must be between 1 and 5");
    }
    if (request.hospitality() < 1 || request.hospitality() > 5) {
      throw new RuntimeException("Hospitality must be between 1 and 5");
    }
    if (request.location() < 1 || request.location() > 5) {
      throw new RuntimeException("Location must be between 1 and 5");
    }
    if (request.description().isEmpty()) {
      throw new RuntimeException("Description cannot be empty");
    } else if (request.description().length() < 20) {
      throw new RuntimeException("Description must be at least 20 characters long");
    }

    propertyReview.setDescription(request.description());
    propertyReview.setRating(request.rating());
    propertyReview.setCleanliness(request.cleanliness());
    propertyReview.setHospitality(request.hospitality());
    propertyReview.setLocation(request.location());
    propertyReview.setCreationDate(new Date());

    return new PropertyReviewView(
      propertyReview.getDescription(),
      propertyReview.getRating(),
      propertyReview.getCleanliness(),
      propertyReview.getHospitality(),
      propertyReview.getLocation(),
      propertyReview.getCreationDate(),
      null
    );
  }

  public List<PropertyReviewRef> findPendingReviewsByReviewer() throws NotFoundException {
    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();
    User user = userDao.findById(currentUser.getId());

    if (user == null) {
      throw new NotFoundException("User not found", UserDTOPrivate.class);
    }

    List<PropertyReview> pendingReviews = propertyReviewDao.findPendingReviewsByReviewer
      (user).stream().toList();

    return pendingReviews.stream()
      .map(pr -> new PropertyReviewRef(
        pr.getId(),
        new PropertySimpleRef(
          pr.getProperty().getId(),
          pr.getProperty().getTitle(),
          pr.getProperty().getDoor(),
          pr.getProperty().getNumber(),
          pr.getProperty().getStreet(),
          pr.getProperty().getCity(),
          pr.getProperty().getProvince(),
          pr.getProperty().getCountry(),
          pr.getProperty().getLatitude(),
          pr.getProperty().getLongitude()
        )
      )).toList();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public List<PropertyReviewAdminView> findAll() {
    return propertyReviewDao.findAll().stream()
      .map(pr -> new PropertyReviewAdminView(
        pr.getId(),
        pr.getReviewer().getEmail(),
        pr.getProperty().getTitle(),
        pr.getDescription(),
        pr.getRating()
      )).toList();
  }
}
