package bg.bulsi.egov.idp.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = -9215531706850599491L;

	public InvalidAuthenticationException(String msg) {
		super(msg);
	}
	

	public InvalidAuthenticationException(String msg, Throwable e) {
		super(msg, e);
	}
	
}
