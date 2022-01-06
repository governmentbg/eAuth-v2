package bg.bulsi.egov.idp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.idp.filters.ExternalIdpAuthenticationFailureHandlerTest;
import bg.bulsi.egov.idp.filters.IExternalIdpAuthenticationFailureHandler;
import bg.bulsi.egov.idp.filters.IKepFailureHandler;
import bg.bulsi.egov.idp.filters.KepFailureHandlerTest;

@Configuration
@Profile("dev")
public class ProfileTestConfig {

	@Autowired
	private IdpConfigurationProperties idpConfigurationProperties; 

	@Bean
	public IExternalIdpAuthenticationFailureHandler externalIdpAuthenticationFailureHandler(){
		return new ExternalIdpAuthenticationFailureHandlerTest(idpConfigurationProperties);
	}

	@Bean
	public IKepFailureHandler kepFailureHandler(){
		return new KepFailureHandlerTest(idpConfigurationProperties);
	}
	
}
