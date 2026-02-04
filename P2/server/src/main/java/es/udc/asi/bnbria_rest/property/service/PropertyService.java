package es.udc.asi.bnbria_rest.property.service;

import es.udc.asi.bnbria_rest.availability.persistence.dao.AvailabilityDao;
import es.udc.asi.bnbria_rest.availability.persistence.entity.Availability;
import es.udc.asi.bnbria_rest.availability.service.AvailabilityService;
import es.udc.asi.bnbria_rest.availability.service.dto.AvailabilityView;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.booking.service.BookingService;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingCancelRequest;
import es.udc.asi.bnbria_rest.common.exceptions.model.ModelException;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.common.images.ImageDto;
import es.udc.asi.bnbria_rest.common.images.ImageService;
import es.udc.asi.bnbria_rest.property.persistence.dao.PropertyDao;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyAdminRef;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyAdminView;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyRef;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyTopView;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyView;
import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewView;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserAdminRef;
import es.udc.asi.bnbria_rest.user.service.dto.UserAdminView;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PropertyService {

  @Autowired
  private PropertyDao propertyDao;

  @Autowired
  private ImageService imageService;

  @Autowired
  private AvailabilityService availabilityService;

  @Autowired
  private UserService userService;

  @Autowired
  private BookingService bookingService;

  @Autowired
  private AvailabilityDao availabilityDao;

  @PreAuthorize("hasAuthority('ADMIN')")
  public List<PropertyAdminRef> findAll() {
    return propertyDao.findAll().stream().map(p -> new PropertyAdminRef(
      p.getId(),
      p.getTitle(),
      p.getState(),
      p.getDoor(),
      p.getNumber(),
      p.getStreet(),
      p.getCity(),
      p.getProvince(),
      p.getCountry(),
      new UserAdminRef(
        p.getOwner().getId(),
        p.getOwner().getEmail(),
        p.getOwner().getCreationDate(),
        p.getOwner().getAuthority().name(),
        p.getOwner().isActive()
      )
    )).toList();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  public void changeState(Long propertyId, PropertyState state) throws NotFoundException {
    Property property = propertyDao.findById(propertyId);
    if (property == null) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    }
    if (property.getState() == state) {
      return;
    }
    if (state == PropertyState.REJECTED) {
      for (Booking booking :
        property.getBookings()
          .stream()
          .filter(b -> b.getState() == BookingState.PENDING ||
            b.getState() == BookingState.CONFIRMED)
          .toList()) {
        bookingService.cancel(booking.getId(), new BookingCancelRequest("Canceled by property rejection by admin"));
      }
    }
    property.setState(state);
    propertyDao.update(property);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @Transactional(readOnly = true)
  public PropertyAdminView privateFindById(Long propertyId) throws NotFoundException {
    Property property = propertyDao.findById(propertyId);
    if (property == null) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    }

    return new PropertyAdminView(
      property.getId(),
      property.getTitle(),
      property.getDescription(),
      property.getState(),
      property.getBathrooms(),
      property.getBedrooms(),
      property.getMaxOccupancy(),
      property.getSquareMeters(),
      property.getType(),
      property.getLatitude(),
      property.getLongitude(),
      property.getDoor(),
      property.getNumber(),
      property.getStreet(),
      property.getCity(),
      property.getProvince(),
      property.getCountry(),
      property.getPostalCode(),
      new UserAdminView(
        property.getOwner().getId(),
        property.getOwner().getEmail(),
        property.getOwner().getName(),
        property.getOwner().getSurname(),
        property.getOwner().getSurname_2(),
        property.getOwner().getPhone(),
        property.getOwner().getBirthday(),
        property.getOwner().getCreationDate(),
        property.getOwner().getAuthority().name(),
        property.getOwner().isActive(),
        null,
        null
      )
    );
  }

  @Transactional(readOnly = true)
  public List<PropertyTopView> findTopRatedProperties() {
    return propertyDao.findTop5RatedApproved().stream()
      .map(p -> new PropertyTopView(
        p.getId(),
        p.getTitle(),
        p.getType(),
        p.getCity(),
        p.getCountry(),
        p.getAvailabilities().stream()
          .mapToDouble(Availability::getPrice)
          .min().orElse(0.0)
      )).toList();
  }

  @Transactional(readOnly = true)
  public List<PropertyTopView> findTopBookedProperties() {
    return propertyDao.findTop5BookedApproved().stream()
      .map(p -> new PropertyTopView(
        p.getId(),
        p.getTitle(),
        p.getType(),
        p.getCity(),
        p.getCountry(),
        p.getAvailabilities().stream()
          .mapToDouble(Availability::getPrice)
          .min().orElse(0.0)
      )).toList();
  }

  @Transactional(readOnly = true)
  public List<PropertyRef> findByLocation(String location) throws NotFoundException {
    if (location == null || location.isBlank()) {
      throw new NotFoundException("Location cannot be null or empty", Property.class);
    }
    String q = location.trim().toLowerCase();
    return propertyDao.findAllApproved().stream()
      .filter(p -> {
        if (p.getCountry() != null && p.getCountry().toLowerCase().contains(q)) return true;
        if (p.getProvince() != null && p.getProvince().toLowerCase().contains(q)) return true;
        return p.getCity() != null && p.getCity().toLowerCase().contains(q);
      })
      .map(p -> new PropertyRef(
        p.getId(),
        p.getTitle(),
        p.getBathrooms(),
        p.getBedrooms(),
        p.getMaxOccupancy(),
        p.getSquareMeters(),
        p.getType(),
        p.getDoor(),
        p.getNumber(),
        p.getStreet(),
        p.getCity(),
        p.getProvince(),
        p.getCountry(),
        p.getLatitude(),
        p.getLongitude(),
        p.getReviews().stream()
          .filter(pr -> pr.getDescription() != null)
          .mapToInt(PropertyReview::getRating)
          .average().orElse(0.0),
        availabilityDao.getFutureAvailabilitiesByProperty(p).stream()
          .map(a -> new AvailabilityView(
            a.getId(),
            a.getStartDate(),
            a.getEndDate(),
            a.getPrice()
          )).toList(),
        p.getAvailabilities().stream().mapToDouble(Availability::getPrice).min().orElse(0.0)
      )).toList();
  }

  @Transactional(readOnly = true)
  public PropertyView findById(Long propertyId) throws NotFoundException {
    Property property = propertyDao.findById(propertyId);
    if (property == null) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    }
    if (property.getState() != PropertyState.APPROVED) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    }

    return new PropertyView(
      property.getId(),
      property.getTitle(),
      property.getDescription(),
      property.getBathrooms(),
      property.getBedrooms(),
      property.getMaxOccupancy(),
      property.getSquareMeters(),
      property.getType(),
      property.getLatitude(),
      property.getLongitude(),
      property.getDoor(),
      property.getNumber(),
      property.getStreet(),
      property.getCity(),
      property.getProvince(),
      property.getCountry(),
      property.getPostalCode(),
      new UserPublicRef(
        property.getOwner().getId(),
        property.getOwner().getName(),
        property.getOwner().getSurname(),
        property.getOwner().getSurname_2(),
        property.getOwner().getCreationDate()
      ),
      property.getReviews().stream()
        .filter(pr -> pr.getDescription() != null)
        .map(
          pr -> new PropertyReviewView(
            pr.getDescription(),
            pr.getRating(),
            pr.getCleanliness(),
            pr.getHospitality(),
            pr.getLocation(),
            pr.getCreationDate(),
            new UserPublicRef(
              pr.getReviewer().getId(),
              pr.getReviewer().getName(),
              pr.getReviewer().getSurname(),
              pr.getReviewer().getSurname_2(),
              pr.getReviewer().getCreationDate()
            )
          )).toList(),
      property.getReviews().stream()
        .filter(pr -> pr.getDescription() != null)
        .mapToInt(PropertyReview::getRating)
        .average().orElse(0.0),
      property.getReviews().stream()
        .filter(pr -> pr.getDescription() != null)
        .mapToInt(PropertyReview::getCleanliness)
        .average().orElse(0.0),
      property.getReviews().stream()
        .filter(pr -> pr.getDescription() != null)
        .mapToInt(PropertyReview::getHospitality)
        .average().orElse(0.0),
      property.getReviews().stream()
        .filter(pr -> pr.getDescription() != null)
        .mapToInt(PropertyReview::getLocation)
        .average().orElse(0.0),
      availabilityService.getFutureAvailabilitiesByProperty(property),
      bookingService.getFutureBookingsByProperty(property)
    );
  }

  @Transactional(readOnly = true)
  public ImageDto getImage(Long id) throws ModelException {
    Property property = propertyDao.findById(id);
    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();
    if (property == null) {
      throw new NotFoundException(id.toString(), Property.class);
    }

    if ((currentUser == null ||
      (!property.getOwner().getId().equals(currentUser.getId()) &&
        !currentUser.getAuthority().equals("ADMIN"))) &&
      property.getState() != PropertyState.APPROVED) {
      throw new NotFoundException(id.toString(), Property.class);
    }

    if (property.getImage() == null) {
      throw new ModelException("The property has no image");
    }

    return imageService.getImagen(id, property.getImage());
  }
}
