package es.udc.asi.bnbria_rest.user.persistence.dao;

import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.persistence.entity.UserAuthority;
import jakarta.persistence.TypedQuery;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class UserDaoJpa extends GenericDaoJpa implements UserDao {

  @Override
  public Collection<User> findAll() {
    return entityManager.createQuery("from User", User.class).getResultList();
  }

  @Override
  public User findById(Long id) {
    return entityManager.find(User.class, id);
  }

  @Override
  public User findByEmail(String email) {
    TypedQuery<User> query = entityManager.createQuery("from User u where u.email = :email", User.class)
      .setParameter("email", email);
    return DataAccessUtils.singleResult(query.getResultList());
  }

  @Override
  public void create(User user) {
    entityManager.persist(user);
  }

  @Override
  public void update(User user) {
    entityManager.merge(user);
  }

  @Override
  public void delete(User user) {
    entityManager.remove(user);
  }

  @Override
  public Long countActiveUsers() {
    return entityManager.createQuery(
        "select count(u) from User u where u.active = true and u.authority = :authority", Long.class)
      .setParameter("authority", UserAuthority.USER)
      .getSingleResult();
  }
}
