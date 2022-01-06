package bg.bulsi.egov.eauth.eid.client.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiClientExceptionHandler extends ResponseEntityExceptionHandler{

/*
 *  -- CUSTOM Handlers--
 */

	@ExceptionHandler(value = { ApiClientException.class })
	public ResponseEntity<Object> handleException(ApiClientException ex, WebRequest request) {
		HttpStatus httpStat= HttpStatus.valueOf(ex.getResponceCode());

		 return handleExceptionInternal(ex, ex.getIntermediateStatus(), 
		          new HttpHeaders(), httpStat, request);
	}


/*	@ExceptionHandler
//	(EidException.class)
	public ResponseEntity<ResponseStatus> handleException(EidProviderException ex) {
		ResponseStatus error = new ResponseStatus();
		error.setFailure(true);
		error.setStatusCode(String.valueOf(HttpStatus.valueOf(ex.code)));
		error.setStatusMessage(ex.getMessage());
		error.setSubStatusCode("eid-custom");

		return new ResponseEntity<>(error, HttpStatus.valueOf(ex.code));
	}


 *  -- System Handlers-- 
 
	
	@ExceptionHandler
//	(NotFoundException.class)
	public ResponseEntity<ResponseStatus> handleException(ApiException ex) {
		ResponseStatus error = new ResponseStatus();
		error.setFailure(true);
		error.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
		error.setStatusMessage(ex.getMessage());
		error.setSubStatusCode("common-custom");
		
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
//	@ExceptionHandler
//	(Exception.class)
	public ResponseEntity<ResponseStatus> handleException(Exception ex) {
		ResponseStatus error = new ResponseStatus();
		error.setFailure(true);
		error.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
		error.setStatusMessage(ex.getMessage());
		error.setSubStatusCode("common-custom");
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}*/

}
