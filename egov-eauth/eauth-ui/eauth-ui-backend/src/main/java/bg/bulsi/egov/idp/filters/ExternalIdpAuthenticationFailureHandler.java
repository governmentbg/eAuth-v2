package bg.bulsi.egov.idp.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExternalIdpAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private AuthenticationFailureHandler badCredentials = new SimpleUrlAuthenticationFailureHandler(
			"/eauth/ssologin?error=1");
	private AuthenticationFailureHandler missingTfaProfile = new SimpleUrlAuthenticationFailureHandler(
			"/eauth/ssologin?error=3");

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String message = exception.getMessage();
		log.info("message: [{}]", message);

		if (message.startsWith("Provider")) {
			badCredentials.onAuthenticationFailure(request, response, exception);
		} else {
			missingTfaProfile.onAuthenticationFailure(request, response, exception);
		}

	}

}
