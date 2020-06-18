package bg.bulsi.egov.idp.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationException extends AuthenticationException {

  public InvalidAuthenticationException(String msg) {
    super(msg);
  }
}
