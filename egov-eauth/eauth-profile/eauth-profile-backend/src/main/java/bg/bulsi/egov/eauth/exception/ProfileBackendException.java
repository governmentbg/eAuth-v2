package bg.bulsi.egov.eauth.exception;

import lombok.Getter;
import lombok.Setter;

public class ProfileBackendException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private int responceCode;
	
	public ProfileBackendException(String message, int code) {
		super(message);
		responceCode = code;
	}
	
	public ProfileBackendException(String message, Throwable cause, int code) {
		super(message, cause);
		responceCode = code;
	}
}
