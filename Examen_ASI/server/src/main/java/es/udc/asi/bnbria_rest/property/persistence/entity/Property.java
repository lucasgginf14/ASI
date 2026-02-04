package es.udc.asi.bnbria_rest.property.persistence.entity;

import es.udc.asi.bnbria_rest.availability.persistence.entity.Availability;
import es.udc.asi.bnbria_rest.booking.persistence.entity.Booking;
import es.udc.asi.bnbria_rest.propertyreview.persistence.entity.PropertyReview;
import es.udc.asi.bnbria_rest.user.persistence.entity.User;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "property")
public class Property {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "property_generator")
  @SequenceGenerator(name = "property_generator", sequenceName = "property_seq")
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @Column
  private String image;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PropertyState state;

  @ManyToOne(optional = false)
  @JoinColumn(name = "owner", nullable = false)
  private User owner;

  @Column(nullable = false)
  private Long bathrooms;

  @Column(nullable = false)
  private Long bedrooms;

  @Column(nullable = false)
  private Long maxOccupancy;

  @Column(nullable = false)
  private Long squareMeters;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PropertyType type;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

  @Column
  private String door;

  @Column(nullable = false)
  private String number;

  @Column(nullable = false)
  private String street;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String province;

  @Column(nullable = false)
  private String country;

  @Column(nullable = false)
  private Long postalCode;

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PropertyReview> reviews;

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Availability> availabilities;

  @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Booking> bookings;

  public Property() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PropertyState getState() {
    return state;
  }

  public void setState(PropertyState state) {
    this.state = state;
  }

  public Long getBathrooms() {
    return bathrooms;
  }

  public void setBathrooms(Long bathrooms) {
    this.bathrooms = bathrooms;
  }

  public Long getBedrooms() {
    return bedrooms;
  }

  public void setBedrooms(Long bedrooms) {
    this.bedrooms = bedrooms;
  }

  public Long getMaxOccupancy() {
    return maxOccupancy;
  }

  public void setMaxOccupancy(Long maxOccupancy) {
    this.maxOccupancy = maxOccupancy;
  }

  public Long getSquareMeters() {
    return squareMeters;
  }

  public void setSquareMeters(Long squareMeters) {
    this.squareMeters = squareMeters;
  }

  public PropertyType getType() {
    return type;
  }

  public void setType(PropertyType type) {
    this.type = type;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getDoor() {
    return door;
  }

  public void setDoor(String door) {
    this.door = door;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public Long getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(Long postalCode) {
    this.postalCode = postalCode;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public List<PropertyReview> getReviews() {
    return reviews;
  }

  public void setReviews(List<PropertyReview> reviews) {
    this.reviews = reviews;
  }

  public List<Availability> getAvailabilities() {
    return availabilities;
  }

  public void setAvailabilities(List<Availability> availabilities) {
    this.availabilities = availabilities;
  }

  public List<Booking> getBookings() {
    return bookings;
  }

  public void setBookings(List<Booking> bookings) {
    this.bookings = bookings;
  }
}
