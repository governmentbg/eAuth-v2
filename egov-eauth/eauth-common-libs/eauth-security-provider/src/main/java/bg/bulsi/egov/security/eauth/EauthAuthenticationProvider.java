package bg.bulsi.egov.security.eauth;

import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class EauthAuthenticationProvider implements AuthenticationProvider, InitializingBean {


    @Getter
    private final UserDetailsService userDetailsService;

    public EauthAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * @param authentication CustomAuthenticationToken
     * @return Authentication
     * @throws AuthenticationException -
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        EauthAuthenticationToken token = (EauthAuthenticationToken) authentication;

        // TODO
        UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(token.getName());

        token.setAuthenticated(true);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EauthAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
