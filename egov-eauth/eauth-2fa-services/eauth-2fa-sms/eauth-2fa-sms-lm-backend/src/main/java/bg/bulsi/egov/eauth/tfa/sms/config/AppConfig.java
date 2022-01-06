package bg.bulsi.egov.eauth.tfa.sms.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.core.HazelcastInstance;

import bg.bulsi.egov.eauth.tfa.sms.service.SmsStoreService;
import bg.bulsi.egov.hazelcast.service.HazelcastService;

@Configuration
public class AppConfig {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public SmsStoreService getStore(@Qualifier("hazelcastInstance") HazelcastInstance instance) {
		return new SmsStoreService(instance);
	}
	
	@Bean
	public HazelcastService getHazelcastService() {
		return new HazelcastService();
	}

}
