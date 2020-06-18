package bg.bulsi.egov.idp.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import bg.bulsi.egov.eauth.metadata.config.security.tokens.FederatedUserAuthenticationToken;
import bg.bulsi.egov.idp.security.IdpPrincipal;
import bg.bulsi.egov.idp.services.EidProviderClientImpl;
import lombok.var;

public class ExternalIdpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final EidProviderClientImpl providerClient;

	private final AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	private final AuthenticationSuccessHandler successTfaHandler = new SimpleUrlAuthenticationSuccessHandler();

	public ExternalIdpAuthenticationSuccessHandler(String defaultTfaTargetUrl, EidProviderClientImpl providerClient) {
		((SimpleUrlAuthenticationSuccessHandler) successTfaHandler).setDefaultTargetUrl(defaultTfaTargetUrl);
		this.providerClient = providerClient;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException, ServletException {

		// Проверка дали е необходима 2FA
		var loginResponse = (IdpPrincipal) authResult.getPrincipal();
		var config = providerClient.getIdentityProviderConfig(loginResponse.getProviderId());

		// old logic: loginResponse.getLoa() == LevelOfAssurance.LOW
		// без 2FA
		if (Boolean.FALSE.equals(config.getTfaRequired())) {
			var token = new FederatedUserAuthenticationToken(authResult.getPrincipal(), authResult.getCredentials(),
					AuthorityUtils.createAuthorityList("ROLE_FULL_AUTHENTICATED"));

			token.setDetails(authResult.getDetails());

			SecurityContextHolder.getContext().setAuthentication(token);
			successHandler.onAuthenticationSuccess(request, response, token);
			return;
		}

		// необходима е 2FA
		SecurityContextHolder.getContext().setAuthentication(authResult);
		successTfaHandler.onAuthenticationSuccess(request, response, authResult);

	}
}
