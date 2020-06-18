package bg.bulsi.egov.eauth.test.sp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml2.provider.service.authentication.AuthenticationRequestFactory;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestFactory;

@Configuration
public class SpApplicationConfig {

	@Bean
	public Saml2AuthenticationRequestFactory saml2AuthenticationRequestFactory() {
		return new AuthenticationRequestFactory();
	}
}
