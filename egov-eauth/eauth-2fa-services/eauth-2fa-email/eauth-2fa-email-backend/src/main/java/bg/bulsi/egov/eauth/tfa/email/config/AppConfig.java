package bg.bulsi.egov.eauth.tfa.email.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.hazelcast.core.HazelcastInstance;

import bg.bulsi.egov.eauth.tfa.email.services.TokenService;
import bg.bulsi.egov.hazelcast.service.HazelcastService;

@Configuration
@EnableAsync
public class AppConfig {
	
	@Bean
	public HazelcastService getHazelcastService() {
		return new HazelcastService();
	}

    @Bean
    public TokenService tokenService(@Qualifier("hazelcastInstance") HazelcastInstance instance, HazelcastService hazelcastService) {
    	return new TokenService(instance, hazelcastService);
    }

}
