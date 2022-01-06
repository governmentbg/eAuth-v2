package bg.bulsi.egov.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import bg.bulsi.egov.eauth.metadata.config.MetadataConfig;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.metadata.config.model.SpProvidersConfiguration;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;

@SpringBootApplication
@ComponentScan(basePackages = { 
		"bg.bulsi.egov.eauth.metadata", 
		"bg.bulsi.egov.eauth.common", 
		"bg.bulsi.egov.idp", 
		"bg.bulsi.egov.client", 
		"bg.bulsi.egov.eauth.eid", 
		"bg.bulsi.egov.idp.services" 
})
@Import({
	EidProvidersConfiguration.class, 
	IdpConfigurationProperties.class, 
	SpProvidersConfiguration.class,
	MetadataConfig.class
})
@EnableConfigurationProperties
public class IdpApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdpApplication.class, args);
	}
	
}
