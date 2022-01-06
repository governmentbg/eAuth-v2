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
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.security.IdpPrincipal;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import bg.bulsi.egov.idp.services.TfaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExternalIdpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final IEidProviderClient providerClient;
	private final TfaService tfaService;

	private final AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	private final AuthenticationSuccessHandler successTfaHandler = new SimpleUrlAuthenticationSuccessHandler();
	private final AuthenticationSuccessHandler successButMissingProfileTfaHandler = new SimpleUrlAuthenticationSuccessHandler("/eauth/ssologin?error=404.1");

	public ExternalIdpAuthenticationSuccessHandler(String defaultTfaTargetUrl, IEidProviderClient providerClient, TfaService tfaService) {
		((SimpleUrlAuthenticationSuccessHandler) successTfaHandler).setDefaultTargetUrl(defaultTfaTargetUrl);
		this.providerClient = providerClient;
		this.tfaService = tfaService; 
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication auth) throws IOException, ServletException {

		// Проверка дали е необходима 2FA
		IdpPrincipal principal = (IdpPrincipal) auth.getPrincipal();
		log.debug("PRINCIPAL PROVIDER ID: [{}]", principal.getProviderId());
		EidProviderConfig config = providerClient.getIdentityProviderConfig(principal.getProviderId());
		log.debug("CONFIG PROVIDER ID: [{}]", config.getProviderId());
		
		// без 2FA
		if (Boolean.FALSE.equals(config.getTfaRequired())) {
			FederatedUserAuthenticationToken token = new FederatedUserAuthenticationToken(auth.getPrincipal(), auth.getCredentials(),
					AuthorityUtils.createAuthorityList("ROLE_FULL_AUTHENTICATED"));

			token.setDetails(auth.getDetails());

			SecurityContextHolder.getContext().setAuthentication(token);
			successHandler.onAuthenticationSuccess(request, response, token);
			return;
		}

		// необходима е 2FA, но първо проверяваме има ли профил в 2FA?
		SecurityContextHolder.getContext().setAuthentication(auth);
		if (Boolean.FALSE.equals(tfaService.checkIfProfileTfaExists())) {
			log.warn("Missing profile in TFA!");
			successButMissingProfileTfaHandler.onAuthenticationSuccess(request, response, auth);
		} else {
			successTfaHandler.onAuthenticationSuccess(request, response, auth);
		}

	}
}
