package es.udc.asi.bnbria_rest.common.exceptions.model;

public class UserEmailExistsException extends ModelException {
  public UserEmailExistsException(String email) {
    super("User email " + email + " already exists");
  }
}
