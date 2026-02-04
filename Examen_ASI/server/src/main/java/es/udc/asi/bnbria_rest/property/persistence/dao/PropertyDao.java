package es.udc.asi.bnbria_rest.property.persistence.dao;

import es.udc.asi.bnbria_rest.property.persistence.entity.Property;

import java.util.List;
import java.util.Collection;

public interface PropertyDao {
  Collection<Property> findAll();

  Collection<Property> findAllApproved();

  Collection<Property> findTop5RatedApproved();

  Collection<Property> findTop5BookedApproved();

  Collection<Property> findByOwner(Long ownerId);

  Property findById(Long id);

  Property create(Property property);

  Property update(Property property);

  void delete(Property property);

  Long countAllProperties();

  Long countApprovedProperties();

  Long countPendingProperties();


  Collection<Object[]> findPropertyStatsByCitiesAndYear(List<String> cities, Integer year);
}
