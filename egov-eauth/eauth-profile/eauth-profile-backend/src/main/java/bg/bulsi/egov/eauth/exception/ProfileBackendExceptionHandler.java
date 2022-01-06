package bg.bulsi.egov.eauth.exception;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import bg.bulsi.egov.eauth.profile.rest.api.ApiException;
import bg.bulsi.egov.eauth.profile.rest.api.ApiResponseMessage;
import bg.bulsi.egov.eauth.profile.rest.api.NotFoundException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class ProfileBackendExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({
            AuthenticationException.class
    })
    @ResponseBody
    public ResponseEntity<ApiResponseMessage> handleUnauthorized(AuthenticationException ex) {

        log.error(ex.toString());

        ApiResponseMessage responseMessage;
        if(ex instanceof org.springframework.security.authentication.DisabledException){
            responseMessage = new ApiResponseMessage(992, ex.getMessage());
        } else if(ex instanceof org.springframework.security.authentication.BadCredentialsException){
            responseMessage = new ApiResponseMessage(991, ex.getMessage());
        } else {
            responseMessage = new ApiResponseMessage(990, ex.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(responseMessage);
    }

    @ExceptionHandler({
            SQLException.class
    })
    @ResponseBody
    public ResponseEntity<ApiResponseMessage> handle(SQLException ex) {

        log.error(ex.toString());

        ApiResponseMessage responseMessage = new ApiResponseMessage(ex.getErrorCode(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseMessage);
    }

    @ExceptionHandler({
            ApiException.class
    })
    @ResponseBody
    public ResponseEntity<ApiResponseMessage> handle(ApiException ex) {

        log.error(ex.toString());

        ApiResponseMessage responseMessage = new ApiResponseMessage(ex.getCode(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseMessage);
    }

    @ExceptionHandler({
            NotFoundException.class
    })
    @ResponseBody
    public ResponseEntity<ApiResponseMessage> handle(NotFoundException ex) {

        log.error(ex.toString());

        ApiResponseMessage responseMessage = new ApiResponseMessage(ex.getCode(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseMessage);
    }
    
    @ExceptionHandler({ 
    	ProfileBackendException.class 
    })
    @ResponseBody
	public ResponseEntity<Object> handleProfileNotFoundException(ProfileBackendException ex) {
		log.error("handleProfileNotFoundException - code:[{}] message: [{}]", ex.getResponceCode(), ex.getMessage());
    	
    	return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex);
	}
}
