package bg.bulsi.egov.eauth.eid.provider.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EntityScan(basePackages = {"bg.bulsi.egov.eauth.eid.provider.model"})
@EnableJpaRepositories(basePackages = {"bg.bulsi.egov.eauth.eid.provider.model.repository"})
public class DataSourceConfig {

}
