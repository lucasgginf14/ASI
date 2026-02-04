package es.udc.asi.bnbria_rest.userreview.persistence.dao;

import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.userreview.persistence.entity.UserReview;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class UserReviewDaoJpa extends GenericDaoJpa implements UserReviewDao {

  @Override
  public Collection<UserReview> findAll() {
    return entityManager.createQuery
        ("from UserReview ur where ur.description is not null", UserReview.class)
      .getResultList();
  }

  @Override
  public Collection<UserReview> findPendingReviewsByReviewer(User reviewer) {
    return entityManager.createQuery
        ("from UserReview ur where ur.reviewer = :reviewer and ur.description is null", UserReview.class)
      .setParameter("reviewer", reviewer)
      .getResultList();
  }

  @Override
  public UserReview findById(Long id) {
    return entityManager.find(UserReview.class, id);
  }

  @Override
  public void create(UserReview userReview) {
    entityManager.persist(userReview);
  }

  @Override
  public UserReview update(UserReview userReview) {
    return entityManager.merge(userReview);
  }

  @Override
  public void delete(UserReview userReview) {
    entityManager.remove(userReview);
  }

  @Override
  public Long countUserReviews() {
    return entityManager.createQuery(
        "select count(ur) from UserReview ur where ur.description is not null", Long.class)
      .getSingleResult();
  }
}
