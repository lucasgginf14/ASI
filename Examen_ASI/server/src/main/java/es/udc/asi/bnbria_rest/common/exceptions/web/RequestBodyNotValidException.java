package es.udc.asi.bnbria_rest.common.exceptions.web;

import org.springframework.validation.Errors;

import java.util.stream.Collectors;

public class RequestBodyNotValidException extends ResourceException {
  public RequestBodyNotValidException(Errors errors) {
    super(errors.getFieldErrors().stream()
        .map(fe -> String.format("%s.%s %s", fe.getObjectName(), fe.getField(), fe.getDefaultMessage()))
        .collect(Collectors.joining("; ")));
  }
}
