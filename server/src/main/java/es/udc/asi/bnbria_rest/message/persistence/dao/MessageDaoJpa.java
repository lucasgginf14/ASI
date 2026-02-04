package es.udc.asi.bnbria_rest.message.persistence.dao;

import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.common.util.repository.GenericDaoJpa;
import es.udc.asi.bnbria_rest.message.persistence.entity.Message;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class MessageDaoJpa extends GenericDaoJpa implements MessageDao {

  @Override
  public void create(Message message) {
    entityManager.persist(message);
  }

  @Override
  public Integer countUnreadConversationsByUser(User user) {
    Long count = entityManager.createQuery(
        "select count(distinct m.booking) " +
          "from Message m " +
          "where (m.booking.guest = :user or m.booking.property.owner = :user) " +
          "and m.read = false " +
          "and m.writer <> :user " +
          "and m.sentAt = (select max(m2.sentAt) from Message m2 where m2.booking = m.booking)", Long.class)
      .setParameter("user", user)
      .getSingleResult();
    return count.intValue();
  }

  @Override
  public Collection<Booking> findAllConversationsByUser(User user) {
    return entityManager.createQuery(
        "select distinct m.booking " +
          "from Message m " +
          "where m.booking.guest = :user or m.booking.property.owner = :user", Booking.class)
      .setParameter("user", user)
      .getResultList();
  }

  @Override
  public Collection<Message> findAllByBooking(Booking booking) {
    return entityManager.createQuery(
        "from Message m " +
          "where m.booking = :booking " +
          "order by m.sentAt asc", Message.class)
      .setParameter("booking", booking)
      .getResultList();
  }

  @Override
  public void readMessagesByBookingAndUser(Booking booking, User user) {
    entityManager.createQuery(
        "update Message m " +
          "set m.read = true " +
          "where m.booking = :booking " +
          "and m.read = false " +
          "and m.writer <> :user")
      .setParameter("booking", booking)
      .setParameter("user", user)
      .executeUpdate();
  }

  @Override
  public Message findLastMessageByBooking(Booking booking) {
    return entityManager.createQuery(
        "from Message m " +
          "where m.booking = :booking " +
          "order by m.sentAt desc", Message.class)
      .setParameter("booking", booking)
      .setMaxResults(1)
      .getSingleResult();
  }
}
