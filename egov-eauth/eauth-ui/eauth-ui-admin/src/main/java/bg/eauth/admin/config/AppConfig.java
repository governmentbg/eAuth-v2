package bg.eauth.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import bg.bulsi.egov.hazelcast.service.HazelcastService;

@Configuration
public class AppConfig {

	@Bean
	public HazelcastService getHazelcastService() {
		return new HazelcastService();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
