package bg.bulsi.egov.eauth.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories( basePackages = {"bg.bulsi.egov.eauth.repository"})
@EnableTransactionManagement
public class DataSourceConfig {


}
