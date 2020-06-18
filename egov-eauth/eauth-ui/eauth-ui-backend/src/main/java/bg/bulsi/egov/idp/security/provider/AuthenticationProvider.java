package bg.bulsi.egov.idp.security.provider;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.metadata.config.security.tokens.FederatedUserAuthenticationToken;
import bg.bulsi.egov.idp.security.InvalidAuthenticationException;

import static bg.bulsi.egov.eauth.metadata.config.api.AuthenticationMethod.ALL;

import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

public class AuthenticationProvider implements
    org.springframework.security.authentication.AuthenticationProvider {

  private final IdpConfigurationProperties idpConfiguration;

  public AuthenticationProvider(IdpConfigurationProperties idpConfiguration) {
    this.idpConfiguration = idpConfiguration;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    if (StringUtils.isEmpty(authentication.getPrincipal())) {
      throw new InvalidAuthenticationException("Principal may not be empty");
    }

    if (idpConfiguration.getAuthMethod().equals(ALL)) {
      return new FederatedUserAuthenticationToken(
          authentication.getPrincipal(),
          authentication.getCredentials(),
          Arrays.asList(
              new SimpleGrantedAuthority("ROLE_ADMIN"),
              new SimpleGrantedAuthority("ROLE_USER"))
      );
    } else {
      //need to copy or else credentials are erased for future logins
      return idpConfiguration.getUsers().stream()
          .filter(token ->
              token.getPrincipal().equals(authentication.getPrincipal())
              && token.getCredentials().equals(authentication.getCredentials()))
          .findFirst()
          .map(federatedUserAuthenticationToken -> federatedUserAuthenticationToken.clone())
          .orElseThrow(() -> new InvalidAuthenticationException("User not found or bad credentials") {});
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
