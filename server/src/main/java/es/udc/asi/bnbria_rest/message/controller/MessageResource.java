package es.udc.asi.bnbria_rest.message.controller;

import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import es.udc.asi.bnbria_rest.message.service.MessageService;
import es.udc.asi.bnbria_rest.message.service.dto.ConversationRef;
import es.udc.asi.bnbria_rest.message.service.dto.MessageRequest;
import es.udc.asi.bnbria_rest.message.service.dto.MessageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageResource {

  @Autowired
  private MessageService messageService;

  @GetMapping("/{bookingId}")
  public List<MessageView> getMessagesByBookingId(@PathVariable Long bookingId) throws NotFoundException {
    return messageService.getMessagesByBookingId(bookingId);
  }

  @PostMapping("/{bookingId}")
  public MessageView sendMessage(@PathVariable Long bookingId, @RequestBody MessageRequest request) throws NotFoundException {
    return messageService.sendMessage(bookingId, request.text());
  }

  @GetMapping
  public List<ConversationRef> getUserConversations() {
    return messageService.getUserConversations();
  }
}
