package es.udc.asi.bnbria_rest.availability.persistence.dao;

import es.udc.asi.bnbria_rest.availability.persistence.entity.Availability;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;

import java.time.LocalDate;
import java.util.Collection;

public interface AvailabilityDao {
  Collection<Availability> getFutureAvailabilitiesByProperty(Property property);

  Availability findByPropertyAndStartDate(Property property, LocalDate date);

  boolean isDateRangeAvailable(Property property, LocalDate startDate, LocalDate endDate, Long excludeAvailabilityId);

  Availability findAvailabilityCoveringDates(Property property, LocalDate startDate, LocalDate endDate);

  Availability findById(Long id);

  Availability create(Availability availability);

  Availability update(Availability availability);

  void delete(Availability availability);
}
