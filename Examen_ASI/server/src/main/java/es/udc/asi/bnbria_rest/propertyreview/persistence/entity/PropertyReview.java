package es.udc.asi.bnbria_rest.propertyreview.persistence.entity;

import es.udc.asi.bnbria_rest.property.persistence.entity.Property;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "propertyReview")
public class PropertyReview {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "property_review_generator")
  @SequenceGenerator(name = "property_review_generator", sequenceName = "property_review_seq")
  private Long id;

  private String description;

  private Integer rating;

  private Integer cleanliness;

  private Integer hospitality;

  private Integer location;

  @Column(nullable = false)
  private Date creationDate;

  @ManyToOne(optional = false)
  @JoinColumn(name = "property", nullable = false)
  private Property property;

  @ManyToOne(optional = false)
  @JoinColumn(name = "reviewer", nullable = false)
  private User reviewer;

  public PropertyReview() {
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

  public Integer getCleanliness() {
    return cleanliness;
  }

  public void setCleanliness(Integer cleanliness) {
    this.cleanliness = cleanliness;
  }

  public Integer getHospitality() {
    return hospitality;
  }

  public void setHospitality(Integer hospitality) {
    this.hospitality = hospitality;
  }

  public Integer getLocation() {
    return location;
  }

  public void setLocation(Integer location) {
    this.location = location;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public User getReviewer() {
    return reviewer;
  }

  public void setReviewer(User reviewer) {
    this.reviewer = reviewer;
  }
}
