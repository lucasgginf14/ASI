package es.udc.asi.bnbria_rest.propertyreview.persistence.dao;

import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;

import java.util.Collection;

public interface PropertyReviewDao {

  Collection<PropertyReview> findAll();

  Collection<PropertyReview> findPendingReviewsByReviewer(User reviewer);

  PropertyReview findById(Long id);

  void create(PropertyReview propertyReview);

  PropertyReview update(PropertyReview propertyReview);

  void delete(PropertyReview propertyReview);

  Long countPropertyReviews();
}
