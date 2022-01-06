package bg.bulsi.egov.eauth.eid.provider.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity(debug = true)
//@ComponentScan("bg.bulsi.egov.eauth.eid.provider")
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Order(1)
@Slf4j
public class EidSecurityConfig extends WebSecurityConfigurerAdapter {
	

    @Value("${egov.eauth.sys.eid.client.auth.token.header.name}")
    private String principalRequestHeader;

    @Value("${egov.eauth.sys.eid.client.auth.token}")
    private String principalRequestValue;

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
        APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);
        log.info("Use APIkey name '{}', and value '{}'", principalRequestHeader, principalRequestValue);
        filter.setAuthenticationManager(new AuthenticationManager() {
        
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String principal = (String) authentication.getPrincipal();
                if (!principalRequestValue.equals(principal))
                {
                    throw new BadCredentialsException("The API key was not found or not the expected value.");
                }
                authentication.setAuthenticated(true);
                return authentication;
            }
        });
        
        http
		.antMatcher("/exIdent/**")
		.csrf()
        	.disable()
        .sessionManagement()
        	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        	.addFilter(filter)
        	.authorizeRequests()
	    		.antMatchers("/exIdent/signed/result/*").authenticated()
	    		.antMatchers("/exIdent/signed/addAtt/*").authenticated()
	    		.antMatchers("/exIdent/inquiry").authenticated()
	        	.anyRequest().denyAll();		
	
	}
	
}
