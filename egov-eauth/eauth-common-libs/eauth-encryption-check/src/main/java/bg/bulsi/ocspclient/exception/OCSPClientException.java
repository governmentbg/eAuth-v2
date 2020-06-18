package bg.bulsi.ocspclient.exception;

@SuppressWarnings("serial")
public class OCSPClientException extends Exception {
	

	public OCSPClientException(String message) {
		super(message);
	}
	
	
	public OCSPClientException(String message, Throwable cause) {
		super(message, cause);
	}
	
}