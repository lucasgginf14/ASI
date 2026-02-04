package es.udc.asi.bnbria_rest.message.persistence.dao;

import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.message.persistence.entity.Message;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;

import java.util.Collection;

public interface MessageDao {

  Collection<Message> findAllByBooking(Booking booking);

  Integer countUnreadConversationsByUser(User user);

  Collection<Booking> findAllConversationsByUser(User user);

  void create(Message message);

  void readMessagesByBookingAndUser(Booking booking, User user);

  Message findLastMessageByBooking(Booking booking);
}
