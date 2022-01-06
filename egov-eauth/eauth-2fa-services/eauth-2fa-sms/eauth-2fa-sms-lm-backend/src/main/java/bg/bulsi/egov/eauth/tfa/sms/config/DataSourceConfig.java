package bg.bulsi.egov.eauth.tfa.sms.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EntityScan(basePackages = {"bg.bulsi.egov.eauth.tfa.sms.model"})
@EnableJpaRepositories(basePackages = {"bg.bulsi.egov.eauth.tfa.sms.data"})
public class DataSourceConfig {

}
