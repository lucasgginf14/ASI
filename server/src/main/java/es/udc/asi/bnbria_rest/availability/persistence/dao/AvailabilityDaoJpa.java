package es.udc.asi.bnbria_rest.availability.persistence.dao;

import es.udc.asi.bnbria_rest.availability.persistence.entity.Availability;
import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public class AvailabilityDaoJpa extends GenericDaoJpa implements AvailabilityDao {

  @Override
  public Collection<Availability> getFutureAvailabilitiesByProperty(Property property) {
    LocalDate now = LocalDate.now().withDayOfMonth(1);
    return entityManager.createQuery(
        "from Availability a where a.property.id = :propertyId and a.endDate >= :now", Availability.class)
      .setParameter("propertyId", property.getId())
      .setParameter("now", now)
      .getResultList();
  }

  @Override
  public Availability findByPropertyAndStartDate(Property property, LocalDate date) {
    List<Availability> results = entityManager.createQuery(
        "from Availability a where a.property.id = :propertyId and a.startDate <= :date order by a.startDate desc", Availability.class)
      .setParameter("propertyId", property.getId())
      .setParameter("date", date)
      .setMaxResults(1)
      .getResultList();
    return results.isEmpty() ? null : results.get(0);
  }

  @Override
  public boolean isDateRangeAvailable(Property property, LocalDate startDate, LocalDate endDate, Long excludeAvailabilityId) {
    List<Availability> results = entityManager.createQuery(
        "from Availability a where a.property.id = :propertyId and a.id <> :excludedAvailabilityId and " +
          "(:startDate between a.startDate and a.endDate or " +
          ":endDate between a.startDate and a.endDate or " +
          "a.startDate between :startDate and :endDate or " +
          "a.endDate between :startDate and :endDate)", Availability.class)
      .setParameter("excludedAvailabilityId", excludeAvailabilityId)
      .setParameter("propertyId", property.getId())
      .setParameter("startDate", startDate)
      .setParameter("endDate", endDate)
      .getResultList();
    return results.isEmpty();
  }

  @Override
  public Availability findAvailabilityCoveringDates(Property property, LocalDate startDate, LocalDate endDate) {
    return entityManager.createQuery(
        "from Availability a where a.property.id = :propertyId and a.startDate <= :startDate and a.endDate >= :endDate", Availability.class)
      .setParameter("propertyId", property.getId())
      .setParameter("startDate", startDate)
      .setParameter("endDate", endDate)
      .getSingleResult();
  }

  @Override
  public Availability findById(Long id) {
    return entityManager.find(Availability.class, id);
  }

  @Override
  public Availability create(Availability availability) {
    entityManager.persist(availability);
    return entityManager.find(Availability.class, availability.getId());
  }

  @Override
  public Availability update(Availability availability) {
    return entityManager.merge(availability);
  }

  @Override
  public void delete(Availability availability) {
    entityManager.remove(availability);
  }
}
