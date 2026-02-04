package es.udc.asi.bnbria_rest.booking.controller;

import es.udc.asi.bnbria_rest.booking.service.BookingService;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingAdminRef;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingAdminView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingCancelRequest;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingGuestRef;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingGuestView;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingOwnerRef;
import es.udc.asi.bnbria_rest.booking.service.dto.BookingOwnerView;
import es.udc.asi.bnbria_rest.common.exceptions.model.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingResource {

  @Autowired
  private BookingService bookingService;

  @PutMapping("/{bookingId}/accept")
  public void accept(@PathVariable Long bookingId) {
    bookingService.accept(bookingId);
  }

  @PutMapping("/{bookingId}/cancel")
  public void cancel(
    @PathVariable Long bookingId,
    @RequestBody BookingCancelRequest request
  ) throws NotFoundException {
    bookingService.cancel(bookingId, request);
  }

  @GetMapping("/owner/pending")
  public List<BookingOwnerRef> findBookingsByOwner() {
    return bookingService.findPendingBookingsByOwner();
  }

  @GetMapping("/owner/all")
  public List<BookingOwnerRef> findAllBookingsByOwner() {
    return bookingService.findAllBookingsByOwner();
  }

  @GetMapping("/owner/{bookingId}")
  public BookingOwnerView findBookingByIdAsOwner(@PathVariable Long bookingId) {
    return bookingService.findBookingByIdAsOwner(bookingId);
  }

  @GetMapping("/guest/all")
  public List<BookingGuestRef> findAllBookingsByGuest() {
    return bookingService.findAllBookingsByGuest();
  }

  @GetMapping("/guest/{bookingId}")
  public BookingGuestView findBookingByIdAsGuest(@PathVariable Long bookingId) {
    return bookingService.findBookingByIdAsGuest(bookingId);
  }

  @GetMapping("/admin")
  public List<BookingAdminRef> findAllAdmin() {
    return bookingService.findAll();
  }

  @GetMapping("/admin/{bookingId}")
  public BookingAdminView findByIdAdmin(@PathVariable Long bookingId) throws NotFoundException {
    return bookingService.findByIdAsAdmin(bookingId);
  }
}
