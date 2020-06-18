package bg.bulsi.egov.security.eauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eauth.security.provider")
public class EauthProviderProperties {

    @Getter
    @Setter
    private String authenticationResponceUrl;

    @Getter
    @Setter
    private String eauthEntryPointPath;

    @Getter
    @Setter
    private String responceReceiverPath;

    @Getter
    @Setter
    private String redirect;

    @Getter
    @Setter
    private String redirectToError;

    @Getter
    @Setter
    private String idSecret;


}
