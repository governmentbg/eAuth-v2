package bg.bulsi.egov.eauth.metadata.config.model;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Component
@Data
@ConfigurationProperties("sp.3rd.party")
public class SpProvidersConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String, SpProviderConfig> providers;

	@Transient
	public Map<String, SpProviderConfig> getList() {
		return providers;
	}

	@Data
	@ToString
	public static class SpProviderConfig implements Serializable {
		
		private static final long serialVersionUID = 1L;

		@NotBlank
		private String entityId;

		private Map<String, String> name;

		@NotBlank
		private String bindingUrl;

	}

}
