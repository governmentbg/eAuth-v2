package bg.bulsi.egov.idp;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import bg.bulsi.egov.eauth.metadata.config.MetadataConfig;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.metadata.config.model.SpProvidersConfiguration;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;

@SpringBootApplication
@ComponentScan(basePackages = { "bg.bulsi.egov.eauth.metadata","bg.bulsi.egov.eauth.common", "bg.bulsi.egov.idp" })
@Import({
	EidProvidersConfiguration.class, 
	IdpConfigurationProperties.class, 
	SpProvidersConfiguration.class,
	MetadataConfig.class})
public class IdpApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdpApplication.class, args);
	}
	
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("Eauth2Lookup-");
		executor.initialize();
		return executor;
	}
}
