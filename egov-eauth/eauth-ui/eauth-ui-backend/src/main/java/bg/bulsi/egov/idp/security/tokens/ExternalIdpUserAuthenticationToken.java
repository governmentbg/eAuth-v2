package bg.bulsi.egov.idp.security.tokens;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class ExternalIdpUserAuthenticationToken extends UsernamePasswordAuthenticationToken {

  public ExternalIdpUserAuthenticationToken(Object principal, Object credentials,
      Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }
}
