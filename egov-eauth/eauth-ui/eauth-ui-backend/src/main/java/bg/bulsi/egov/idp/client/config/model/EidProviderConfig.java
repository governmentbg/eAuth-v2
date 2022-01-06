package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import bg.bulsi.egov.eauth.eid.dto.AuthProcessingType;
import bg.bulsi.egov.eauth.eid.dto.LevelOfAssurance;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class EidProviderConfig implements Serializable {

	private static final long serialVersionUID = -8399611290531449950L;

	@Getter
	@Setter
	@NotBlank
	//Vendor_ID
	private String providerId;

	@Getter
	@Setter
	@NotBlank
	private String providerApiKey;

	@Getter
	@Setter
	private Map<String, String> name;

	@Getter
	@Setter
	@NotBlank
	private LevelOfAssurance loa;

	@Getter
	@Setter
	@NotBlank
	private AuthProcessingType eidProcesss;

	@Getter
	@Setter
	private String eidCallbackUrl;
	
	@Getter
	@Setter
	@NotBlank
	private Boolean tfaRequired = false;

	@Getter
	@Setter
	@NotBlank
	private boolean active;

	@Getter
	@Setter
	@NotBlank
	private int expirationPeriod; // in sec

	@Getter
	@Setter
	private EidProviderConnection eidProviderConnection;

	@Getter
	@Setter
	/**
	 * key must generate like
	 * IdentityParam.providerId + "_" + ProviderIdSuffix.name()
	 */
	private Map<String, ProviderAuthAttribute> attributes;

}