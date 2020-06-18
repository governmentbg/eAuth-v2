package bg.bulsi.egov.security.eauth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class EauthAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal ;

    public EauthAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }

    public EauthAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = null;
    }

    @Override
    public Object getCredentials() {
        return "USER";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
