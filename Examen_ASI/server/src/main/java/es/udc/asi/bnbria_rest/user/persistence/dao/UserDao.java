package es.udc.asi.bnbria_rest.user.persistence.dao;

import es.udc.asi.bnbria_rest.user.persistence.entity.User;

import java.util.Collection;

public interface UserDao {

  Collection<User> findAll();

  User findById(Long id);

  User findByEmail(String email);

  void create(User user);

  void update(User user);

  void delete(User user);

  Long countActiveUsers();
}
