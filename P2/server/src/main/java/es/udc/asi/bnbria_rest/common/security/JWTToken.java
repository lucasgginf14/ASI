package es.udc.asi.bnbria_rest.common.security;

public class JWTToken {
  private String token;

  public JWTToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
