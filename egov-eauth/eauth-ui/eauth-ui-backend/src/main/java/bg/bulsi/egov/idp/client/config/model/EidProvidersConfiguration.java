package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Transient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import bg.bulsi.egov.eauth.eid.dto.LevelOfAssurance;
import lombok.Getter;
import lombok.Setter;

/**
 * application-idp.yaml
 */
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

	@Override
	public String toString() {
		return "EidProvidersConfiguration [providers " + providers.size() + " =" + providers + "]";
	}
	
	

}
