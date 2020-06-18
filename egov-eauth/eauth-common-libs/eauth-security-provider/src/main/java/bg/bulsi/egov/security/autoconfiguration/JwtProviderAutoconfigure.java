package bg.bulsi.egov.security.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"bg.bulsi.egov.security.jwt"})
@ConditionalOnProperty(prefix = "eauth.security.jwt",
        name = {"token-secret"}
)
public class JwtProviderAutoconfigure {

}
