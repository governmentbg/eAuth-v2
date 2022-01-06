package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpHeaders;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class EidProviderConnection implements Serializable {
	
	private static final long serialVersionUID = -5039016892575207197L;

	@NotBlank
	@Getter
	@Setter
	private String endpoint;

	@Getter
	@Setter
	private boolean clientSslRequired;

	@Getter
	@Setter
	private KeyStoreData clientKeyStore;

	@Getter
	@Setter
	private String tlsVersion;

	@Getter
	@Setter
	private HttpHeaders customHeaders;

}