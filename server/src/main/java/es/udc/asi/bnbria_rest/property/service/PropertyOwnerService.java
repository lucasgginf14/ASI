package es.udc.asi.bnbria_rest.property.service;

import es.udc.asi.bnbria_rest.availability.service.AvailabilityService;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.booking.persistence.entity.BookingState;
import es.udc.asi.bnbria_rest.booking.service.BookingService;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingCancelRequest;
import es.udc.asi.bnbria_rest.common.exceptions.model.ModelException;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.asi.bnbria_rest.common.images.ImageService;
import es.udc.asi.bnbria_rest.property.persistence.dao.PropertyDao;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyType;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyCreateRequest;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyOwnerRef;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyOwnerView;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyUpdateRequest;
import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.propertyreview.service.dto.PropertyReviewView;
import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.persistence.entity.UserAuthority;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
public class PropertyOwnerService {

  @Autowired
  private PropertyDao propertyDao;

  @Autowired
  private UserService userService;

  @Autowired
  private UserDao userDao;

  @Autowired
  private PropertySyncService propertySyncService;

  @Autowired
  private ImageService imageService;

  @Autowired
  private AvailabilityService availabilityService;

  @Autowired
  private BookingService bookingService;

  private void validatePropertyData(String title, String description, Long bathrooms,
                                    Long bedrooms, Long maxOccupancy, Long squareMeters, String street, String number,
                                    String city, String province, String country, Long postalCode) {

    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("The title is required");
    }
    if (title.trim().length() < 5) {
      throw new IllegalArgumentException("The title must have at least 5 characters");
    }
    if (title.trim().length() > 100) {
      throw new IllegalArgumentException("The title cannot exceed 100 characters");
    }

    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("The description is required");
    }
    if (description.trim().length() < 20) {
      throw new IllegalArgumentException("The description must have at least 20 characters");
    }
    if (description.trim().length() > 255) {
      throw new IllegalArgumentException("The description cannot exceed 255 characters");
    }

    if (bathrooms == null) {
      throw new IllegalArgumentException("The number of bathrooms is required");
    } else if (bathrooms < 1 || bathrooms > 10) {
      throw new IllegalArgumentException("The number of bathrooms must be between 1 and 10");
    }

    if (bedrooms == null) {
      throw new IllegalArgumentException("The number of bedrooms is required");
    } else if (bedrooms < 1 || bedrooms > 20) {
      throw new IllegalArgumentException("The number of bedrooms must be between 1 and 20");
    }

    if (maxOccupancy == null) {
      throw new IllegalArgumentException("The maximum number of guests is required");
    } else if (maxOccupancy < 1 || maxOccupancy > 50) {
      throw new IllegalArgumentException("The number of guests must be between 1 and 50");
    }

    if (squareMeters == null) {
      throw new IllegalArgumentException("The square meters are required");
    } else if (squareMeters < 10 || squareMeters > 1000) {
      throw new IllegalArgumentException("The square meters must be between 10 and 1000");
    }

    if (street == null || street.trim().isEmpty()) {
      throw new IllegalArgumentException("The street is required");
    }

    if (number == null || number.trim().isEmpty()) {
      throw new IllegalArgumentException("The number is required");
    }

    if (city == null || city.trim().isEmpty()) {
      throw new IllegalArgumentException("The city is required");
    }

    if (province == null || province.trim().isEmpty()) {
      throw new IllegalArgumentException("The province is required");
    }

    if (country == null || country.trim().isEmpty()) {
      throw new IllegalArgumentException("The country is required");
    }

    if (postalCode == null) {
      throw new IllegalArgumentException("The postal code is required");
    }
    if (String.valueOf(postalCode).length() != 5) {
      throw new IllegalArgumentException("The postal code must have 5 digits");
    }
  }

  @Transactional(readOnly = true)
  public PropertyOwnerView findMyPropertyById(Long propertyId) throws NotFoundException, OperationNotAllowed {
    Long ownerId = userService.getCurrentUserWithAuthority().getId();

    Property property = propertyDao.findById(propertyId);
    if (property == null) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    } else if (!property.getOwner().getId().equals(ownerId)) {
      throw new OperationNotAllowed("The user cannot access to the property");
    }

    return new PropertyOwnerView(
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
      property.getReviews().stream().map(
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

  public List<PropertyOwnerRef> findMyProperties() {
    Long ownerId = userService.getCurrentUserWithAuthority().getId();
    return propertyDao.findByOwner(ownerId).stream().map(p -> new PropertyOwnerRef(
      p.getId(),
      p.getTitle(),
      p.getState(),
      p.getDoor(),
      p.getNumber(),
      p.getStreet(),
      p.getCity(),
      p.getProvince(),
      p.getCountry(),
      p.getReviews().stream()
        .filter(pr -> pr.getDescription() != null)
        .mapToInt(PropertyReview::getRating)
        .average().orElse(0.0)
    )).toList();
  }

  public PropertyOwnerView update(PropertyUpdateRequest property, Long propertyId) throws NotFoundException, OperationNotAllowed {
    Property existingProperty = propertyDao.findById(propertyId);
    Long ownerId = userService.getCurrentUserWithAuthority().getId();

    if (existingProperty == null) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    } else if (!existingProperty.getOwner().getId().equals(ownerId)) {
      throw new OperationNotAllowed("The user cannot update the property");
    }

    if (!property.title().isEmpty()) {
      if (property.title().trim().length() < 5) {
        throw new IllegalArgumentException("The title must have at least 5 characters");
      }
      if (property.title().trim().length() > 100) {
        throw new IllegalArgumentException("The title cannot exceed 100 characters");
      }
      existingProperty.setTitle(property.title().trim());
    }

    if (!property.description().isEmpty()) {
      if (property.description().trim().length() < 20) {
        throw new IllegalArgumentException("The description must have at least 20 characters");
      }
      if (property.description().trim().length() > 255) {
        throw new IllegalArgumentException("The description cannot exceed 255 characters");
      }
      existingProperty.setDescription(property.description().trim());
    }

    if (property.bathrooms() != null) {
      if (property.bathrooms() < 1 || property.bathrooms() > 10) {
        throw new IllegalArgumentException("The number of bathrooms must be between 1 and 10");
      }
      existingProperty.setBathrooms(property.bathrooms());
    }

    if (property.bedrooms() != null) {
      if (property.bedrooms() < 1 || property.bedrooms() > 20) {
        throw new IllegalArgumentException("The number of bedrooms must be between 1 and 20");
      }
      existingProperty.setBedrooms(property.bedrooms());
    }

    if (property.maxOccupancy() != null) {
      if (property.maxOccupancy() < 1 || property.maxOccupancy() > 50) {
        throw new IllegalArgumentException("The number of guests must be between 1 and 50");
      }
      existingProperty.setMaxOccupancy(property.maxOccupancy());
    }

    if (property.squareMeters() != null) {
      if (property.squareMeters() < 10 || property.squareMeters() > 1000) {
        throw new IllegalArgumentException("The square meters must be between 10 and 1000");
      }
      existingProperty.setSquareMeters(property.squareMeters());
    }

    if (property.type() != null) {
      boolean valid = false;
      for (PropertyType pt : PropertyType.values()) {
        if (pt.equals(property.type())) {
          valid = true;
          break;
        }
      }
      if (!valid) {
        throw new IllegalArgumentException("Property type invalid");
      }
      existingProperty.setType(property.type());
    }

    existingProperty.setState(PropertyState.PENDING);
    for (Booking booking :
      existingProperty.getBookings()
        .stream()
        .filter(b -> b.getState() == BookingState.PENDING ||
          b.getState() == BookingState.CONFIRMED)
        .toList()) {
      bookingService.cancel(booking.getId(), new BookingCancelRequest("Canceled caused by property update"));
    }

    Property propertyUpdated = propertyDao.update(existingProperty);

    return new PropertyOwnerView(
      propertyUpdated.getId(),
      propertyUpdated.getTitle(),
      propertyUpdated.getDescription(),
      propertyUpdated.getState(),
      propertyUpdated.getBathrooms(),
      propertyUpdated.getBedrooms(),
      propertyUpdated.getMaxOccupancy(),
      propertyUpdated.getSquareMeters(),
      propertyUpdated.getType(),
      propertyUpdated.getLatitude(),
      propertyUpdated.getLongitude(),
      propertyUpdated.getDoor(),
      propertyUpdated.getNumber(),
      propertyUpdated.getStreet(),
      propertyUpdated.getCity(),
      propertyUpdated.getProvince(),
      propertyUpdated.getCountry(),
      propertyUpdated.getPostalCode(),
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }

  public PropertyOwnerView create(PropertyCreateRequest property) throws ExecutionException, NotFoundException, OperationNotAllowed {
    Long ownerId = userService.getCurrentUserWithAuthority().getId();
    User owner = userDao.findById(ownerId);

    if (owner == null) {
      throw new NotFoundException("User not exist.", User.class);
    }
    if (owner.getAuthority().equals(UserAuthority.ADMIN)) {
      throw new OperationNotAllowed("Admin users cannot create properties.");
    }

    if (property.type() == null) {
      throw new IllegalArgumentException("The property type is required");
    } else {
      boolean valid = false;
      for (PropertyType pt : PropertyType.values()) {
        if (pt.equals(property.type())) {
          valid = true;
          break;
        }
      }
      if (!valid) {
        throw new IllegalArgumentException("Property type invalid");
      }
    }

    validatePropertyData(
      property.title(),
      property.description(),
      property.bathrooms(),
      property.bedrooms(),
      property.maxOccupancy(),
      property.squareMeters(),
      property.street(),
      property.number(),
      property.city(),
      property.province(),
      property.country(),
      property.postalCode()
    );

    Property newProperty = new Property();

    try {
      PropertySyncService.Coordinates coords = propertySyncService.lookupCoordinates(
        property.street().trim(),
        property.city().trim(),
        property.province().trim(),
        property.country().trim(),
        property.postalCode()
      );
      if (coords != null) {
        newProperty.setLatitude(coords.lat());
        newProperty.setLongitude(coords.lon());
      } else {
        throw new NotFoundException("Coordinates not found by Nominatim API.", Property.class);
      }
    } catch (Exception e) {
      throw new ExecutionException(e);
    }

    newProperty.setTitle(property.title().trim());
    newProperty.setDescription(property.description().trim());
    newProperty.setState(PropertyState.PENDING);
    newProperty.setOwner(owner);
    newProperty.setBathrooms(property.bathrooms());
    newProperty.setBedrooms(property.bedrooms());
    newProperty.setMaxOccupancy(property.maxOccupancy());
    newProperty.setSquareMeters(property.squareMeters());
    newProperty.setType(property.type());
    newProperty.setDoor(property.door() != null ? property.door().trim() : null);
    newProperty.setNumber(property.number().trim());
    newProperty.setStreet(property.street().trim());
    newProperty.setCity(property.city().trim());
    newProperty.setProvince(property.province().trim());
    newProperty.setCountry(property.country().trim());
    newProperty.setPostalCode(property.postalCode());

    propertyDao.create(newProperty);

    return new PropertyOwnerView(
      newProperty.getId(),
      newProperty.getTitle(),
      newProperty.getDescription(),
      newProperty.getState(),
      newProperty.getBathrooms(),
      newProperty.getBedrooms(),
      newProperty.getMaxOccupancy(),
      newProperty.getSquareMeters(),
      newProperty.getType(),
      newProperty.getLatitude(),
      newProperty.getLongitude(),
      newProperty.getDoor(),
      newProperty.getNumber(),
      newProperty.getStreet(),
      newProperty.getCity(),
      newProperty.getProvince(),
      newProperty.getCountry(),
      newProperty.getPostalCode(),
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }

  public void delete(Long propertyId) throws ModelException {
    Property property = propertyDao.findById(propertyId);
    if (property == null) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    }

    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();

    if (!currentUser.getId().equals(property.getOwner().getId())
      && !currentUser.getAuthority().equals(UserAuthority.ADMIN.toString())) {
      throw new OperationNotAllowed("The user cannot remove the property");
    }

    if (!currentUser.getAuthority().equals(UserAuthority.ADMIN.toString())) {
      if (property.getBookings().stream().
        anyMatch(b -> b.getState() == BookingState.CONFIRMED
          || b.getState() == BookingState.IN_PROGRESS)) {
        throw new OperationNotAllowed("The property cannot be removed because it has active bookings");
      }
    }

    imageService.deleteImage(property.getId(), property.getImage());
    propertyDao.delete(property);
  }

  @Transactional(rollbackFor = Exception.class)
  public void saveImage(Long id, MultipartFile file) throws ModelException {
    Property property = propertyDao.findById(id);
    if (property == null) {
      throw new NotFoundException(id.toString(), Property.class);
    }
    if (!property.getOwner().getId().equals(userService.getCurrentUserWithAuthority().getId())) {
      throw new OperationNotAllowed("The user cannot update the property");
    }

    if (file.isEmpty()) {
      throw new ModelException("Empty file");
    }

    String nombreImagenOriginal = imageService.saveImage(file, id);
    property.setImage(nombreImagenOriginal);
    property.setState(PropertyState.PENDING);
    propertyDao.update(property);
  }
}
