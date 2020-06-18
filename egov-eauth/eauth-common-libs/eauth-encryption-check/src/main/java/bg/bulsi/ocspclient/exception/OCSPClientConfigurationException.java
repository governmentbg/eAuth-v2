package bg.bulsi.ocspclient.exception;

@SuppressWarnings("serial")
public class OCSPClientConfigurationException extends Exception {


	public OCSPClientConfigurationException(String message) {
		super(message);
	}
	
	
	public OCSPClientConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
