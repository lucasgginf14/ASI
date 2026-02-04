package es.udc.asi.bnbria_rest.availability.service;

import es.udc.asi.bnbria_rest.availability.persistence.dao.AvailabilityDao;
import es.udc.asi.bnbria_rest.availability.persistence.entity.Availability;
import es.udc.asi.bnbria_rest.availability.service.dto.AvailabilityRequest;
import es.udc.asi.bnbria_rest.availability.service.dto.AvailabilityView;
import es.udc.asi.bnbria_rest.booking.persistence.dao.BookingDao;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.asi.bnbria_rest.property.persistence.dao.PropertyDao;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AvailabilityService {

  @Autowired
  private AvailabilityDao availabilityDao;

  @Autowired
  private PropertyDao propertyDao;

  @Autowired
  private BookingDao bookingDao;

  @Autowired
  private UserService userService;

  public List<AvailabilityView> getFutureAvailabilitiesByProperty(Property property) {
    return availabilityDao.getFutureAvailabilitiesByProperty(property)
      .stream().map(
        a -> new AvailabilityView(
          a.getId(),
          a.getStartDate(),
          a.getEndDate(),
          a.getPrice()
        )).toList();
  }

  public AvailabilityView create(Long propertyId, AvailabilityRequest request) {
    Property property = propertyDao.findById(propertyId);
    UserDTOPrivate owner = userService.getCurrentUserWithAuthority();

    if (!property.getOwner().getId().equals(owner.getId())) {
      throw new SecurityException("User is not the owner of the property");
    }

    if (request.endDate() == null || request.startDate() == null) {
      throw new IllegalArgumentException("Start date and end date must be provided");
    }

    if (request.price() == null || request.price() <= 0) {
      throw new IllegalArgumentException("Price must be a positive value");
    }

    if (request.startDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Start date must be in the future");
    }

    if (request.endDate().isBefore(request.startDate())) {
      throw new IllegalArgumentException("End date must be after start date");
    }

    if (!availabilityDao.isDateRangeAvailable(property, request.startDate(), request.endDate(), null)) {
      throw new IllegalArgumentException("The date range is not available");
    }

    Availability availability = new Availability();
    availability.setProperty(property);
    availability.setStartDate(request.startDate());
    availability.setEndDate(request.endDate());
    availability.setPrice(request.price());
    availability = availabilityDao.create(availability);
    return new AvailabilityView(
      availability.getId(),
      availability.getStartDate(),
      availability.getEndDate(),
      availability.getPrice()
    );
  }

  public void delete(Long propertyId, Long availabilityId) throws NotFoundException, OperationNotAllowed {
    Availability availability = availabilityDao.findById(availabilityId);
    if (availability == null) {
      throw new NotFoundException(availabilityId.toString(), Availability.class);
    }
    if (!availability.getProperty().getId().equals(propertyId)) {
      throw new NotFoundException(availabilityId.toString(), Availability.class);
    }

    UserDTOPrivate owner = userService.getCurrentUserWithAuthority();
    if (!availability.getProperty().getOwner().getId().equals(owner.getId())) {
      throw new OperationNotAllowed("User is not the owner of the property");
    }

    if (bookingDao.existsBookingInDateRange(
      availability.getProperty(),
      availability.getStartDate(),
      availability.getEndDate())) {
      throw new OperationNotAllowed("Cannot delete availability because there are bookings in the specified date range");
    }

    availabilityDao.delete(availability);
  }

  public AvailabilityView update(Long propertyId, Long availabilityId, AvailabilityRequest request) throws NotFoundException, OperationNotAllowed {

    Availability availability = availabilityDao.findById(availabilityId);
    if (availability == null) {
      throw new NotFoundException(availabilityId.toString(), Availability.class);
    }
    if (!availability.getProperty().getId().equals(propertyId)) {
      throw new NotFoundException(propertyId.toString(), Property.class);
    }

    UserDTOPrivate owner = userService.getCurrentUserWithAuthority();
    if (!availability.getProperty().getOwner().getId().equals(owner.getId())) {
      throw new OperationNotAllowed("User is not the owner of the property");
    }

    if (request.endDate() != null && request.startDate() != null) {
      LocalDate now = LocalDate.now();
      if (availability.getStartDate().isBefore(now)) {
        throw new IllegalArgumentException("Start date cannot be modified because it has already started");
      }
      if (request.startDate().isBefore(now)) {
        throw new IllegalArgumentException("Start date must be today or in the future");
      }
      if (request.endDate().isBefore(request.startDate())) {
        throw new IllegalArgumentException("End date must be after start date");
      }
      if (!availabilityDao.isDateRangeAvailable(availability.getProperty(), request.startDate(), request.endDate(), availabilityId)) {
        throw new IllegalArgumentException("The date range is not available");
      }
    } else if (request.endDate() != null) {
      LocalDate now = LocalDate.now();

      if (request.endDate().isBefore(now)) {
        throw new IllegalArgumentException("End date must be today or in the future");
      }
      if (request.endDate().isBefore(availability.getStartDate())) {
        throw new IllegalArgumentException("End date must be after start date");
      }
      if (request.endDate().isAfter(availability.getEndDate())) {
        if (!availabilityDao.isDateRangeAvailable(availability.getProperty(), availability.getStartDate(), request.endDate(), availabilityId)) {
          throw new IllegalArgumentException("The date range is not available");
        }
      }
    } else if (request.startDate() != null) {
      LocalDate now = LocalDate.now();

      if (availability.getStartDate().isBefore(now)) {
        throw new IllegalArgumentException("Start date cannot be modified because it has already started");
      }
      if (request.startDate().isBefore(now)) {
        throw new IllegalArgumentException("Start date must be today or in the future");
      }
      if (request.startDate().isAfter(availability.getEndDate())) {
        throw new IllegalArgumentException("Start date must be before end date");
      }

      if (request.startDate().isAfter(availability.getStartDate())) {
        if (!availabilityDao.isDateRangeAvailable(availability.getProperty(), request.startDate(), availability.getEndDate(), availabilityId)) {
          throw new IllegalArgumentException("The date range is not available");
        }
      } else if (request.startDate().isBefore(availability.getStartDate())) {
        if (!availabilityDao.isDateRangeAvailable(availability.getProperty(), request.startDate(), availability.getEndDate(), availabilityId)) {
          throw new IllegalArgumentException("The date range is not available");
        }
      }
    }

    LocalDate newStart = request.startDate() != null ? request.startDate() : availability.getStartDate();
    LocalDate newEnd = request.endDate() != null ? request.endDate() : availability.getEndDate();

    if (newStart.isAfter(availability.getStartDate())) {
      List<Booking> bookings = bookingDao.findBookingsInDateRange(
        availability.getProperty(),
        availability.getStartDate(),
        newStart
      ).stream().toList();
      for (Booking booking : bookings) {
        Availability coveringAvailability = availabilityDao.findAvailabilityCoveringDates(
          availability.getProperty(),
          booking.getStartDate(),
          booking.getEndDate()
        );
        if (coveringAvailability == null || (
          coveringAvailability.getId().equals(availability.getId())
            && booking.getStartDate().isBefore(newStart))) {
          throw new IllegalArgumentException("Cannot update availability because existing bookings would be excluded by the new end date");
        }
      }
    }
    if (newEnd.isBefore(availability.getEndDate())) {
      List<Booking> bookings = bookingDao.findBookingsInDateRange(availability.getProperty(), newEnd, availability.getEndDate()).stream().toList();
      for (Booking booking : bookings) {
        Availability coveringAvailability = availabilityDao.findAvailabilityCoveringDates(
          availability.getProperty(),
          booking.getStartDate(),
          booking.getEndDate()
        );

        if (coveringAvailability == null ||
          (coveringAvailability.getId().equals(availability.getId()) && booking.getEndDate().isAfter(newEnd))) {
          throw new IllegalArgumentException("Cannot update availability because existing bookings would be excluded by the new end date");
        }
      }
    }

    if (request.price() != null && request.price() <= 0) {
      throw new IllegalArgumentException("Price must be a positive value");
    }

    if (request.endDate() != null) {
      availability.setEndDate(request.endDate());
    }

    if (request.startDate() != null) {
      availability.setStartDate(request.startDate());
    }

    if (request.price() != null) {
      availability.setPrice(request.price());
    }

    availability = availabilityDao.update(availability);

    return new AvailabilityView(
      availability.getId(),
      availability.getStartDate(),
      availability.getEndDate(),
      availability.getPrice()
    );
  }
}
