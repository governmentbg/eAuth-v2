package bg.bulsi.egov.security.jwt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eauth.security.jwt")
public class JwtProviderProperties {

    @Getter
    @Setter
    private String tokenSecret;

    @Getter
    @Setter
    private int tokenExpirationMsec = 1800000;
}
