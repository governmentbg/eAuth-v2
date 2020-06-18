package bg.bulsi.egov.eauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @Getter
    private final Auth auth = new Auth();

    public static class Auth {

        @Getter
        @Setter
        private String tokenSecret;
        @Getter
        @Setter
        private long tokenExpirationMsec;

    }

}
