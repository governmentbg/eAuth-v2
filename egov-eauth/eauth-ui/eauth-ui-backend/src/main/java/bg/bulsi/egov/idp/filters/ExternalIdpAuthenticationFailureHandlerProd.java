package bg.bulsi.egov.idp.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExternalIdpAuthenticationFailureHandlerProd implements IExternalIdpAuthenticationFailureHandler {
	
	private IdpConfigurationProperties idpConfiguration;
	
	/*
	 * UI code(error) to message
	 * 1->403 - Грешни данни при автентикация! 
	 * 2->400 - Грешен код за верификация! 
	 * 3->404 - Липса на профил за двуфакторна автентикация!
	 * 4->408 - Времето за автентикация изтече! 408
	 * 5->499 - Отказахте се от двуфакторна автентикация! 499
	 * ->440 Session expired
	 * ->500 Server/Service error
	 * ->504 Gateway Timeot
	 * 
	 * CancelAuthFilter code(error) to message
	 * 
	 */
	//TODO add &errmgs=""
	private AuthenticationFailureHandler badCredentials;
	private AuthenticationFailureHandler missingTfaProfile;
	private AuthenticationFailureHandler missingIdentity;
	private AuthenticationFailureHandler missingMessage;
	private AuthenticationFailureHandler responceTimeout;
	private AuthenticationFailureHandler sessionExpired;
	private AuthenticationFailureHandler cancelTfa;
	private AuthenticationFailureHandler serverErrorAuth;
	private AuthenticationFailureHandler gatewayTimeout;
	
	public ExternalIdpAuthenticationFailureHandlerProd(IdpConfigurationProperties idpConfiguration) {
		log.debug("Profile('PROD') IExternalIdpAuthenticationFailureHandler");
		this.idpConfiguration = idpConfiguration;
		
		this.badCredentials = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=403");
		this.missingTfaProfile = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=404.1");
		this.missingIdentity = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=404.2");
		this.missingMessage = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=408");
		this.responceTimeout = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=408");
		this.sessionExpired = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=440");
		this.cancelTfa = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=499");
		this.serverErrorAuth = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=500");
		this.gatewayTimeout = new SimpleUrlAuthenticationFailureHandler(this.idpConfiguration.getPathPrefix() + "/api/idp/cancel-auth?error=504");
	}
	
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String message = exception.getMessage();
		log.info("message: [{}]", message);

		response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
		if (StringUtils.isBlank(message)) {
			missingMessage.onAuthenticationFailure(request, response, exception);
		} else if (message.startsWith("Missing identity")) { // || exception instanceof InvalidAuthenticationException
			missingIdentity.onAuthenticationFailure(request, response, exception);
		} else if (message.startsWith("ResponceTimeout")) {
			responceTimeout.onAuthenticationFailure(request, response, exception);
		} else if (message.startsWith("GatewayTimeout")) {
			gatewayTimeout.onAuthenticationFailure(request, response, exception);
		} else if (message.startsWith("Provider")) {
			badCredentials.onAuthenticationFailure(request, response, exception);
		} else {
			//TODO Specify and change
			missingTfaProfile.onAuthenticationFailure(request, response, exception);
		}

	}

}
