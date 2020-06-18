package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import bg.bulsi.egov.eauth.eid.dto.AuthProcessingType;
import bg.bulsi.egov.eauth.eid.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.dto.AuthenticationAttribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@ConfigurationProperties("idp.3rd.party")
public class EidProvidersConfiguration implements Serializable {

	private static final long serialVersionUID = 2932785183929463064L;

	@Getter
	@Setter
	private Map<String, EidProviderConfig> providers;

	/**
	 *
	 * @param loa
	 * @return
	 * 
	 * @see bg.bulsi.egov.idp.services.temp.ProviderServer#list(LevelOfAssurance serviceProviderLoa) list(LevelOfAssurance serviceProviderLoa)
	 */
	@Deprecated
	@Transient
	public Map<String, EidProviderConfig> getListByLoa(LevelOfAssurance loa) {
		Map<String, EidProviderConfig> res = new HashMap<>();

		Stream<Entry<String, EidProviderConfig>> sLow = this.getProviders().entrySet().stream().filter(e -> LevelOfAssurance.LOW.name().equals(e.getValue().getLoa().name()));
		Stream<Entry<String, EidProviderConfig>> sSubs = this.getProviders().entrySet().stream().filter(e -> LevelOfAssurance.SUBSTANTIAL.name().equals(e.getValue().getLoa().name()));
		Stream<Entry<String, EidProviderConfig>> sHigh = this.getProviders().entrySet().stream().filter(e -> LevelOfAssurance.HIGH.name().equals(e.getValue().getLoa().name()));

		switch (loa) {
			case LOW:
				res.putAll(sLow.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
				// collect next
			case SUBSTANTIAL:
				res.putAll(sSubs.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
				// collect next
			case HIGH:
				res.putAll(sHigh.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
			default:
				break;
		}
		
		return res;
	}

	@ToString
	public static class EidProviderConfig implements Serializable {

		private static final long serialVersionUID = -8399611290531449950L;
		
		public enum ProviderIdSuffix {
			USERNAME,
			PASSWORD,
			EGN,
			PIK,
			PUK
		}


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
		@NotBlank
		private Boolean tfaRequired = false;

		@Getter
		@Setter
		@NotBlank
		private String endpoint;

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
		/**
		 * key must generate like
		 * IdentityParam.providerId + "_" + ProviderIdSuffix.name()
		 */
		private Map<String, ProviderAuthAttribute> attributes;


		@EqualsAndHashCode(callSuper = true)
		public static class ProviderAuthAttribute extends AuthenticationAttribute implements Serializable {

			private static final long serialVersionUID = -7304199837524898345L;


			public enum Eid {
				IDENTITY,
				PASSWORD,
				ADDITIONAL;
			}

			@Getter
			@Setter
			@NotBlank
			private Eid eId;

		}

	}

}
