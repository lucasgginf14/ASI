package es.udc.asi.bnbria_rest.userreview.persistence.entity;

import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "userReview")
public class UserReview {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_review_generator")
  @SequenceGenerator(name = "user_review_generator", sequenceName = "user_review_seq")
  private Long id;

  private String description;

  private Integer rating;

  @Column(nullable = false)
  private Date creationDate;

  @ManyToOne(optional = false)
  @JoinColumn(name = "reviewer", nullable = false)
  private User reviewer;

  @OneToOne(optional = false)
  @JoinColumn(name = "booking_review", nullable = false)
  private Booking booking;

  public UserReview() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public User getReviewer() {
    return reviewer;
  }

  public void setReviewer(User reviewer) {
    this.reviewer = reviewer;
  }

  public Booking getBooking() {
    return booking;
  }

  public void setBooking(Booking booking) {
    this.booking = booking;
  }
}
