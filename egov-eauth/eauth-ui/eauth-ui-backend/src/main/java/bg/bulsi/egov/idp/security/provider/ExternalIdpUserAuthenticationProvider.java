package bg.bulsi.egov.idp.security.provider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LoginRequest;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.security.IdpPrincipal;
import bg.bulsi.egov.idp.security.InvalidAuthenticationException;
import bg.bulsi.egov.idp.security.tokens.ExternalIdpUserAuthenticationToken;
import bg.bulsi.egov.idp.services.temp.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExternalIdpUserAuthenticationProvider implements 
	 org.springframework.security.authentication.AuthenticationProvider {

	private final UserService userService;

	public ExternalIdpUserAuthenticationProvider(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		LoginRequest body = (LoginRequest) authentication.getCredentials();
		String providerId = body.getProviderId();
		AuthenticationMap auth = body.getAuthMap();
		log.info("Identify against '{}' EiD provider!", providerId);
		log.debug("AuthMap: {}", auth);
		
		LoginResponse response = userService.login(providerId, auth);

		if (response == null) {
			//TODO check exception fo Cancel authentication SAMLMessageHandler
			throw new InvalidAuthenticationException("Provider: " + providerId);
		}

		IdpPrincipal principal = new IdpPrincipal(providerId, response.getLoa(),
				response.getAttributes());

		return new ExternalIdpUserAuthenticationToken(principal, response,
				AuthorityUtils.createAuthorityList("ROLE_IDP_AUTHENTICATED"));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (ExternalIdpUserAuthenticationToken.class.isAssignableFrom(authentication));

	}
}
