package bg.bulsi.egov.idp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.idp.filters.ExternalIdpAuthenticationFailureHandlerProd;
import bg.bulsi.egov.idp.filters.IExternalIdpAuthenticationFailureHandler;
import bg.bulsi.egov.idp.filters.IKepFailureHandler;
import bg.bulsi.egov.idp.filters.KepFailureHandlerProd;

@Configuration
@Profile("prod")
public class ProfileProConfig {

	@Autowired
	private IdpConfigurationProperties idpConfigurationProperties; 

	@Bean
	public IExternalIdpAuthenticationFailureHandler externalIdpAuthenticationFailureHandler(){
		return new ExternalIdpAuthenticationFailureHandlerProd(idpConfigurationProperties);
	}

	@Bean
	public IKepFailureHandler kepFailureHandler(){
		return new KepFailureHandlerProd(idpConfigurationProperties);
	}
	
}
