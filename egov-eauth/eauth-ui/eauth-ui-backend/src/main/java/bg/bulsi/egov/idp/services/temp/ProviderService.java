package bg.bulsi.egov.idp.services.temp;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.modelmapper.ModelMapper;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Element;

import bg.bulsi.egov.hazelcast.service.HazelcastService;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;
import bg.bulsi.egov.idp.dto.AuthTimeout;
import bg.bulsi.egov.idp.dto.IdentityProvider;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.saml.Provider;
import bg.bulsi.egov.saml.RequestedService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProviderService {

	@Autowired
	private EidProvidersConfiguration config;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private HazelcastService hazelcastService;

	/*
	 * Тестови списък с IdPs заредени от конфиг. файл
	 */
	public List<IdentityProvider> list(LevelOfAssurance serviceProviderLoa) {

		List<IdentityProvider> providers = config.getProviders().values().stream()
				.filter(providerConfig -> providerConfig.isActive())
				.filter(providerConfig -> providerConfig.getLoa().ordinal() >= serviceProviderLoa.ordinal())
				.map(providerConfig -> modelMapper.map(providerConfig, IdentityProvider.class))
				// .filter(idp -> idp.getLoa().ordinal() >= serviceProviderLoa.ordinal())
				.sorted(Comparator.comparing(provider -> provider.getName().get("bg"), Comparator.naturalOrder()))
				.collect(Collectors.toList());
		for (IdentityProvider identityProvider: providers) { 
			identityProvider.setAttributes(identityProvider.getAttributes().stream()
					.sorted(Comparator.comparing(att -> att.getType(), Comparator.comparingInt(p -> p.ordinal())))
					.collect(Collectors.toList()));
		}
		
		return providers;
	}

	public AuthTimeout authenticationTimeout() {
		int totalAuthTimeInMinutes = Integer.parseInt(hazelcastService.get("egov.eauth.dyn.authentication.maxtime.minutes"));
		long timestamp = new Date().getTime() + (totalAuthTimeInMinutes * 60 * 1000);
		AuthTimeout timeout = new AuthTimeout(timestamp);
		return timeout;
	}

	public Optional<IdentityProvider> getProviderById(String id) {
		LevelOfAssurance loa = authnRequestlevelOfAssurance();
		List<IdentityProvider> providers = list(loa);
		Optional<IdentityProvider> provider = providers.stream().filter(p -> p.getId().equals(id)).findFirst();

		return provider;
	}

	public LevelOfAssurance authnRequestlevelOfAssurance() {
		LevelOfAssurance loa = null;
		try {
			AuthnRequest authnRequest = getAuthnRequest();
			loa = getLevelOfAssurance(authnRequest);
		} catch (UnmarshallingException e) {
			log.error("error: [{}]", e.getMessage());
		}

		return loa;
	}

	/*
	 * get SAML Authn Request from session parameter
	 */
	public AuthnRequest getAuthnRequest() throws UnmarshallingException {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

		HttpSession session = request.getSession(false);

		Element element = (Element) session.getAttribute("SAMLAuthRequest");
		if (element == null) {
			throw new UnmarshallingException("Missing SAML 2.0 AuthRequest");
		}

		AuthnRequest authnRequest = (AuthnRequest) Objects
				.requireNonNull(XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element))
				.unmarshall(element);

		return authnRequest;
	}

	/*
	 * LoA from SAML Authn Request
	 */
	public LevelOfAssurance getLevelOfAssurance(AuthnRequest authnRequest) {
		RequestedService requestedService = (RequestedService) authnRequest.getExtensions().getUnknownXMLObjects().get(0);
		bg.bulsi.egov.saml.LevelOfAssurance loaObject = requestedService.getLevelOfAssurance();

		LevelOfAssurance loa = LevelOfAssurance.valueOf(loaObject.getValue());
		log.info("loa: [{}]", loa);

		return loa;
	}
	
	/*
	 * Service OID from SAML Authn Request
	 */
	public String getServiceOID(AuthnRequest authnRequest) {
		RequestedService requestedService = (RequestedService) authnRequest.getExtensions().getUnknownXMLObjects().get(0);
		bg.bulsi.egov.saml.Service service = requestedService.getService();

		String serviceOID = service.getValue();
		log.info("serviceOID: [{}]", serviceOID);

		return serviceOID;
	}
	
	/*
	 * Provider OID from SAML Authn Request
	 */
	public String getProviderOID(AuthnRequest authnRequest) {
		RequestedService requestedService = (RequestedService) authnRequest.getExtensions().getUnknownXMLObjects().get(0);
		Provider provider = requestedService.getProvider();

		String providerOID = provider.getValue();
		log.info("providerOID: [{}]", providerOID);

		return providerOID;
	}
}
