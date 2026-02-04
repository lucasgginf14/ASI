package es.udc.asi.bnbria_rest.userreview.persistence.dao;

import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.userreview.persistence.entity.UserReview;

import java.util.Collection;

public interface UserReviewDao {

  Collection<UserReview> findAll();

  Collection<UserReview> findPendingReviewsByReviewer(User reviewer);

  UserReview findById(Long id);

  void create(UserReview userReview);

  UserReview update(UserReview userReview);

  void delete(UserReview userReview);

  Long countUserReviews();
}
