package bg.bulsi.egov.idp.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KepFailureHandlerProd implements IKepFailureHandler {
	
	private IdpConfigurationProperties idpConfiguration;
	
	public KepFailureHandlerProd(IdpConfigurationProperties idpConfiguration) {
		log.debug("Profile('PROD') IKepFailureHandler"); 
		this.idpConfiguration = idpConfiguration;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String message = exception.getMessage();
		log.info("message: [{}]", message);


		/*
		 * UI codes(error) to message
		 * 1 - Грешни данни при автентикация! 403 
		 */
		response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
		AuthenticationFailureHandler errHandler = new SimpleUrlAuthenticationFailureHandler(idpConfiguration.getBaseUrl() + idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=403");
		errHandler.onAuthenticationFailure(request, response, exception);
		
	}

}
