package bg.bulsi.egov.idp.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "tfa.login")
public class TfaLoginConfig {

	@Getter
	@Setter
	private List<String> identifiers;
}
