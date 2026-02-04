package es.udc.asi.bnbria_rest.booking.persistence.entity;

import es.udc.asi.bnbria_rest.message.persistence.entity.Message;
import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import es.udc.asi.bnbria_rest.userreview.persistence.entity.UserReview;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_generator")
  @SequenceGenerator(name = "booking_generator", sequenceName = "booking_seq")
  private Long id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BookingState state;

  @Column(nullable = false)
  private Double price;

  @Column
  private String requestText;

  @Column(nullable = false)
  private LocalDateTime requestMoment;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private Integer numGuests;

  @Column
  private String cancellationReason;

  @ManyToOne(optional = false)
  @JoinColumn(name = "guest", nullable = false)
  private User guest;

  @ManyToOne(optional = false)
  @JoinColumn(name = "property", nullable = false)
  private Property property;

  @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserReview review;

  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages;

  public Booking() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BookingState getState() {
    return state;
  }

  public void setState(BookingState state) {
    this.state = state;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getRequestText() {
    return requestText;
  }

  public void setRequestText(String requestText) {
    this.requestText = requestText;
  }

  public LocalDateTime getRequestMoment() {
    return requestMoment;
  }

  public void setRequestMoment(LocalDateTime requestMoment) {
    this.requestMoment = requestMoment;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public Integer getNumGuests() {
    return numGuests;
  }

  public void setNumGuests(Integer numGuests) {
    this.numGuests = numGuests;
  }

  public String getCancellationReason() {
    return cancellationReason;
  }

  public void setCancellationReason(String cancellationReason) {
    this.cancellationReason = cancellationReason;
  }

  public User getGuest() {
    return guest;
  }

  public void setGuest(User guest) {
    this.guest = guest;
  }

  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public UserReview getReview() {
    return review;
  }

  public void setReview(UserReview review) {
    this.review = review;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
