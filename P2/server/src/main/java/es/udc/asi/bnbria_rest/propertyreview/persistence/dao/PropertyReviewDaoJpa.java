package es.udc.asi.bnbria_rest.propertyreview.persistence.dao;

import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class PropertyReviewDaoJpa extends GenericDaoJpa implements PropertyReviewDao {

  @Override
  public Collection<PropertyReview> findAll() {
    return entityManager.createQuery
        ("from PropertyReview pr where pr.description is not null", PropertyReview.class)
      .getResultList();
  }

  @Override
  public Collection<PropertyReview> findPendingReviewsByReviewer(User reviewer) {
    return entityManager.createQuery
        ("from PropertyReview pr where pr.reviewer = :reviewer and pr.description is null", PropertyReview.class)
      .setParameter("reviewer", reviewer)
      .getResultList();
  }

  @Override
  public PropertyReview findById(Long id) {
    return entityManager.find(PropertyReview.class, id);
  }

  @Override
  public void create(PropertyReview propertyReview) {
    entityManager.persist(propertyReview);
  }

  @Override
  public PropertyReview update(PropertyReview propertyReview) {
    return entityManager.merge(propertyReview);
  }

  @Override
  public void delete(PropertyReview propertyReview) {
    entityManager.remove(propertyReview);
  }

  @Override
  public Long countPropertyReviews() {
    return entityManager.createQuery(
        "select count(pr) from PropertyReview pr where pr.description is not null", Long.class)
      .getSingleResult();
  }
}
