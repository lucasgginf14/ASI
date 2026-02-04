package es.udc.asi.bnbria_rest.user.service.dto;

import es.udc.asi.bnbria_rest.user.persistence.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTOPrivate {
  private Long id;

  @NotEmpty
  @Size(min = 4)
  private String email;

  @NotEmpty
  @Size(min = 4)
  private String password;
  private String authority;

  public UserDTOPrivate() {
  }

  public UserDTOPrivate(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.authority = user.getAuthority().name();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }
}
