package bg.bulsi.egov.eauth.metadata.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@AutoConfigureBefore
@ComponentScan(basePackages = { "bg.bulsi.egov.eauth.metadata","bg.bulsi.egov.eauth.common" })
@EntityScan("bg.bulsi.egov.eauth.metadata.config.model")
public class MetadataConfig {
}
