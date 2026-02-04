package es.udc.asi.bnbria_rest.property.persistence.dao;

import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.property.persistence.entity.PropertyState;
import org.springframework.stereotype.Repository;

import java.util.List;
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

  @Override
  public Collection<Object[]> findPropertyStatsByCitiesAndYear(List<String> cities, Integer year) {
    StringBuilder query = new StringBuilder(
      "select p.title, p.city, count(b), coalesce(sum(b.price), 0) " +
        "from Property p left join p.bookings b " +
        "where p.state = :state"
    );

    // LÓGICA DE BÚSQUEDA PARCIAL
    if (cities != null && !cities.isEmpty()) {
      query.append(" and (");
      for (int i = 0; i < cities.size(); i++) {
        if (i > 0) query.append(" or ");
        // Usamos LIKE :param
        query.append("lower(p.city) like :city").append(i);
      }
      query.append(")");
    }

    if (year != null) {
      query.append(" and year(b.startDate) = :year");
    }

    query.append(" group by p.id, p.title, p.city");

    var q = entityManager.createQuery(query.toString(), Object[].class)
      .setParameter("state", PropertyState.APPROVED);

    if (cities != null && !cities.isEmpty()) {
      for (int i = 0; i < cities.size(); i++) {
        // Convertimos a minúsculas y añadimos % AQUÍ en Java
        String term = "%" + cities.get(i).toLowerCase() + "%";
        q.setParameter("city" + i, term);
      }
    }

    if (year != null) {
      q.setParameter("year", year);
    }

    return q.getResultList();
  }
}
