package es.udc.asi.bnbria_rest.message.service;

import es.udc.asi.bnbria_rest.booking.persistence.dao.BookingDao;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.message.persistence.dao.MessageDao;
import es.udc.asi.bnbria_rest.message.persistence.entity.Message;
import es.udc.asi.bnbria_rest.message.service.dto.ConversationRef;
import es.udc.asi.bnbria_rest.message.service.dto.MessageView;
import es.udc.asi.bnbria_rest.user.persistence.dao.UserDao;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.user.service.UserService;
import es.udc.asi.bnbria_rest.user.service.dto.UserDTOPrivate;
import es.udc.asi.bnbria_rest.user.service.dto.UserPublicRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MessageService {

  @Autowired
  private MessageDao messageDao;

  @Autowired
  private BookingDao bookingDao;

  @Autowired
  private UserService userService;

  @Autowired
  private UserDao userDao;
  
  public List<MessageView> getMessagesByBookingId(Long bookingId) throws NotFoundException {
    Booking booking = bookingDao.findById(bookingId);
    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();
    User user = userDao.findById(currentUser.getId());
    if (booking == null) {
      throw new NotFoundException("Booking with id " + bookingId + " not found", Booking.class);
    }
    if (booking.getGuest() == user || booking.getProperty().getOwner() == user) {
      messageDao.readMessagesByBookingAndUser(booking, user);
      return messageDao.findAllByBooking(booking).stream()
        .map(message -> new MessageView(
          new UserPublicRef(
            message.getWriter().getId(),
            message.getWriter().getName(),
            message.getWriter().getSurname(),
            message.getWriter().getSurname_2(),
            message.getWriter().getCreationDate()
          ),
          message.getText(),
          message.getRead(),
          message.getSentAt()
        ))
        .toList();
    } else {
      return null;
    }
  }

  public MessageView sendMessage(Long bookingId, String text) throws NotFoundException {
    Booking booking = bookingDao.findById(bookingId);
    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();
    User user = userDao.findById(currentUser.getId());
    if (booking == null) {
      throw new NotFoundException("Booking with id " + bookingId + " not found", Booking.class);
    }
    if (booking.getGuest() == user || booking.getProperty().getOwner() == user) {
      Message message =
        new Message();
      message.setBooking(booking);
      message.setWriter(user);
      message.setText(text);
      message.setRead(false);
      message.setSentAt(LocalDateTime.now());
      messageDao.create(message);

      return new MessageView(
        new UserPublicRef(
          user.getId(),
          user.getName(),
          user.getSurname(),
          user.getSurname_2(),
          user.getCreationDate()
        ),
        message.getText(),
        message.getRead(),
        message.getSentAt()
      );
    } else {
      throw new RuntimeException("User not authorized to send messages for booking with id " + bookingId);
    }
  }

  @Transactional(readOnly = true)
  public List<ConversationRef> getUserConversations() {
    UserDTOPrivate currentUser = userService.getCurrentUserWithAuthority();
    User user = userDao.findById(currentUser.getId());
    List<Booking> bookings = messageDao
      .findAllConversationsByUser(user)
      .stream().toList();

    List<ConversationRef> conversationRefs = new ArrayList<>();

    for (Booking booking : bookings) {
      UserPublicRef otherUser;
      if (booking.getGuest() == user) {
        otherUser = new UserPublicRef(
          booking.getProperty().getOwner().getId(),
          booking.getProperty().getOwner().getName(),
          booking.getProperty().getOwner().getSurname(),
          booking.getProperty().getOwner().getSurname_2(),
          booking.getProperty().getOwner().getCreationDate()
        );
      } else {
        otherUser = new UserPublicRef(
          booking.getGuest().getId(),
          booking.getGuest().getName(),
          booking.getGuest().getSurname(),
          booking.getGuest().getSurname_2(),
          booking.getGuest().getCreationDate()
        );
      }
      Message lastMessage = messageDao.findLastMessageByBooking(booking);
      conversationRefs.add(new ConversationRef(
        booking.getId(),
        booking.getProperty().getTitle(),
        otherUser,
        (lastMessage.getWriter() != user && !lastMessage.getRead()),
        lastMessage.getSentAt()
      ));
    }

    return conversationRefs;
  }
}
