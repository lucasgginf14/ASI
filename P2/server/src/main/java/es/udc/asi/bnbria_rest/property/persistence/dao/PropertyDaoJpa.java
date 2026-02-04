package es.udc.asi.bnbria_rest.property.persistence.dao;

import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class PropertyDaoJpa extends GenericDaoJpa implements PropertyDao {

  @Override
  public Collection<Property> findAll() {
    return entityManager
      .createQuery("from Property", Property.class)
      .getResultList();
  }

  @Override
  public Collection<Property> findAllApproved() {
    return entityManager
      .createQuery("from Property where state = :state", Property.class)
      .setParameter("state", PropertyState.APPROVED)
      .getResultList();
  }

  @Override
  public Collection<Property> findTop5RatedApproved() {
    return entityManager
      .createQuery
        ("from Property p " +
            "where p.state = :state " +
            "and exists (select 1 from PropertyReview pr where pr.property.id = p.id) " +
            "order by " +
            "(select avg(pr.rating) " +
            "from PropertyReview pr " +
            "where pr.property.id = p.id) desc",
          Property.class)
      .setParameter("state", PropertyState.APPROVED)
      .setMaxResults(5)
      .getResultList();
  }

  @Override
  public Collection<Property> findTop5BookedApproved() {
    return entityManager
      .createQuery
        ("from Property p " +
            "where p.state = :state " +
            "order by " +
            "(select count(b.id) " +
            "from Booking b " +
            "where b.property.id = p.id) desc",
          Property.class)
      .setParameter("state", PropertyState.APPROVED)
      .setMaxResults(5)
      .getResultList();
  }

  @Override
  public Collection<Property> findByOwner(Long ownerId) {
    return entityManager.createQuery
        ("from Property p where p.owner.id = :ownerId", Property.class)
      .setParameter("ownerId", ownerId).getResultList();
  }

  @Override
  public Property findById(Long id) {
    return entityManager.find(Property.class, id);
  }

  @Override
  public Property create(Property property) {
    entityManager.persist(property);
    return entityManager.find(Property.class, property.getId());
  }

  @Override
  public Property update(Property property) {
    return entityManager.merge(property);
  }

  @Override
  public void delete(Property property) {
    entityManager.remove(property);
  }

  @Override
  public Long countAllProperties() {
    return entityManager.createQuery(
        "select count(p) from Property p", Long.class)
      .getSingleResult();
  }

  @Override
  public Long countApprovedProperties() {
    return entityManager.createQuery(
        "select count(p) from Property p where p.state = :state", Long.class)
      .setParameter("state", PropertyState.APPROVED)
      .getSingleResult();
  }

  @Override
  public Long countPendingProperties() {
    return entityManager.createQuery(
        "select count(p) from Property p where p.state = :state", Long.class)
      .setParameter("state", PropertyState.PENDING)
      .getSingleResult();
  }
}
