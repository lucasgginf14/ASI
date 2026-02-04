package es.udc.asi.bnbria_rest.common.exceptions.web;

public class CredentialsAreNotValidException extends ResourceException {

  public CredentialsAreNotValidException(String errorMsg) {
    super(errorMsg);
  }
}
