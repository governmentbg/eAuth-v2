package bg.bulsi.egov.eauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import bg.bulsi.egov.eauth.metadata.EauthMetadataBuilder;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.metadata.sp.SpMetadataController;

@SpringBootApplication
@ComponentScan(excludeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "bg.bulsi.egov.eauth.metadata.*") })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@Import({ SpMetadataController.class, IdpConfigurationProperties.class, EauthMetadataBuilder.class })
public class EauthProfileApplication {

	public static void main(String[] args) {
		SpringApplication.run(EauthProfileApplication.class, args);
	}

}
