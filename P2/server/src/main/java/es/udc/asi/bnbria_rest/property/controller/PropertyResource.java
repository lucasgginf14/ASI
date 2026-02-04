package es.udc.asi.bnbria_rest.property.controller;

import es.udc.asi.bnbria_rest.availability.service.AvailabilityService;
import es.udc.asi.bnbria_rest.availability.service.dto.AvailabilityRequest;
import es.udc.asi.bnbria_rest.availability.service.dto.AvailabilityView;
import es.udc.asi.bnbria_rest.booking.service.BookingService;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingPrivateView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingRequest;
import es.udc.asi.bnbria_rest.common.exceptions.model.ModelException;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.asi.bnbria_rest.common.images.ImageDto;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import es.udc.asi.bnbria_rest.property.service.PropertyOwnerService;
import es.udc.asi.bnbria_rest.property.service.PropertyService;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyAdminRef;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyAdminView;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyCreateRequest;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyOwnerRef;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyOwnerView;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyRef;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyTopView;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyUpdateRequest;
import es.udc.asi.bnbria_rest.property.service.dto.PropertyView;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyResource {

  @Autowired
  private PropertyService propertyService;

  @Autowired
  private PropertyOwnerService propertyOwnerService;

  @Autowired
  private AvailabilityService availabilityService;

  @Autowired
  private BookingService bookingService;

  @GetMapping("/admin")
  public List<PropertyAdminRef> findAll() {
    return propertyService.findAll();
  }

  @GetMapping("/admin/{propertyId}")
  public PropertyAdminView findByIdAdmin(@PathVariable Long propertyId) throws NotFoundException {
    return propertyService.privateFindById(propertyId);
  }

  @PutMapping("/admin/{propertyId}/approve")
  public void approveProperty(@PathVariable Long propertyId) throws NotFoundException {
    propertyService.changeState(propertyId, PropertyState.APPROVED);
  }

  @DeleteMapping("/admin/{propertyId}/unapprove")
  public void rejectProperty(@PathVariable Long propertyId) throws NotFoundException {
    propertyService.changeState(propertyId, PropertyState.REJECTED);
  }

  @DeleteMapping("/admin/{propertyId}")
  public void deletePropertyAdmin(@PathVariable Long propertyId) throws ModelException {
    propertyOwnerService.delete(propertyId);
  }

  @GetMapping("/top-rated")
  public List<PropertyTopView> findTopRatedProperties() {
    return propertyService.findTopRatedProperties();
  }

  @GetMapping("/top-booked")
  public List<PropertyTopView> findTopBookedProperties() {
    return propertyService.findTopBookedProperties();
  }

  @GetMapping("/search/{location}")
  public List<PropertyRef> findByLocation(
    @PathVariable String location
  ) throws NotFoundException {
    return propertyService.findByLocation(location);
  }

  @GetMapping("/{propertyId}")
  public PropertyView findByIdPublic(@PathVariable Long propertyId) throws NotFoundException {
    return propertyService.findById(propertyId);
  }

  @PostMapping("/{propertyId}/booking")
  public BookingPrivateView createBooking(
    @PathVariable Long propertyId,
    @RequestBody BookingRequest request
  ) {
    return bookingService.create(propertyId, request);
  }

  @GetMapping("/{propertyId}/image")
  public void getImage(@PathVariable Long propertyId, HttpServletResponse response) throws ModelException {
    ImageDto image = propertyService.getImage(propertyId);

    try {
      response.setHeader("Content-disposition", "filename=" + image.filename());
      response.setContentType(image.mimeType());
      IOUtils.copy(image.content(), response.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @GetMapping("/my-properties")
  public List<PropertyOwnerRef> findByOwner() {
    return propertyOwnerService.findMyProperties();
  }

  @GetMapping("/my-properties/{propertyId}")
  public PropertyOwnerView findMyPropertyById(@PathVariable Long propertyId) throws NotFoundException, OperationNotAllowed {
    return propertyOwnerService.findMyPropertyById(propertyId);
  }

  @PostMapping("/my-properties")
  public PropertyOwnerView createProperty(@RequestBody PropertyCreateRequest request) throws Exception {
    return propertyOwnerService.create(request);
  }

  @PutMapping("/my-properties/{propertyId}")
  public PropertyOwnerView updateProperty(@PathVariable Long propertyId, @RequestBody PropertyUpdateRequest request) throws ModelException {
    return propertyOwnerService.update(request, propertyId);
  }

  @DeleteMapping("/my-properties/{propertyId}")
  public void deleteProperty(@PathVariable Long propertyId) throws ModelException {
    propertyOwnerService.delete(propertyId);
  }

  @PostMapping("/my-properties/{propertyId}/image")
  public void saveImage(@PathVariable Long propertyId, @RequestParam MultipartFile file) throws ModelException {
    propertyOwnerService.saveImage(propertyId, file);
  }

  @PostMapping("/my-properties/{propertyId}/availabilities")
  public AvailabilityView createAvailability(
    @PathVariable Long propertyId,
    @RequestBody AvailabilityRequest request
  ) {
    return availabilityService.create(propertyId, request);
  }

  @DeleteMapping("/my-properties/{propertyId}/availabilities/{availabilityId}")
  public void deleteAvailability(
    @PathVariable Long availabilityId,
    @PathVariable Long propertyId
  ) throws NotFoundException, OperationNotAllowed {
    availabilityService.delete(propertyId, availabilityId);
  }

  @PutMapping("/my-properties/{propertyId}/availabilities/{availabilityId}")
  public AvailabilityView updateAvailability(
    @PathVariable Long availabilityId,
    @PathVariable Long propertyId,
    @RequestBody AvailabilityRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return availabilityService.update(propertyId, availabilityId, request);
  }
}
