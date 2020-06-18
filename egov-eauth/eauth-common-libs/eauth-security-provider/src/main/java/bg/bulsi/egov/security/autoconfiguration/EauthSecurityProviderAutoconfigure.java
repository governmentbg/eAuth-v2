package bg.bulsi.egov.security.autoconfiguration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"bg.bulsi.egov.security.eauth"})
public class EauthSecurityProviderAutoconfigure {
}
