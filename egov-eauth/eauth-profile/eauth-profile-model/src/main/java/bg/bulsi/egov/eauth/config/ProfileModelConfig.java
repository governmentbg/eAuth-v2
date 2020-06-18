package bg.bulsi.egov.eauth.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("bg.bulsi.egov.eauth.model")
@EnableJpaRepositories("bg.bulsi.egov.eauth.model")
public class ProfileModelConfig {
}
