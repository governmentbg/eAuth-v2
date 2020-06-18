package bg.bulsi.egov.idp.services.temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.model.User;
import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.idp.config.TfaLoginConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.IdentityAttributes;
import bg.bulsi.egov.idp.dto.IdentityProvider;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.security.InvalidAuthenticationException;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import bg.bulsi.egov.security.eauth.config.EauthProviderProperties;
import bg.bulsi.egov.security.utils.PersonalIdUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private static final String DEFAULT_USERNAME = "admin";
	// "admin" -> hashed:
	private static final String DEFAULT_PASS_ENCODED = "$2a$10$nuubUH89MKyvIcX0QN9jHuJneXofQs51Is4QDaNUPUi8WAwPYu1t6";

	private static final String DEFAULT_PROVIDER_ID = "test";

	private static final String TFA_PROVIDER_ID = "noi";
	private static final String TFA_EGN = "1010101010";
	
	@Getter
	private Map<String, String> credentials;
	
	@Getter
	@Autowired
	private TfaLoginConfig loginConfig;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	@Qualifier("eidProviderClientImpl")
	private IEidProviderClient client;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
    protected EauthProviderProperties properties;

	private final PasswordEncoder passwordEncoder;

	private final ProviderService providerService;

	public UserService(PasswordEncoder passwordEncoder, ProviderService providerService) {
		this.passwordEncoder = passwordEncoder;
		this.providerService = providerService;
		init();
	}
	
	private void init() {
		log.info("initialize credentials!!!");
		credentials = new HashMap<>();
		credentials.put(DEFAULT_USERNAME, DEFAULT_PASS_ENCODED);
		
		credentials.put("user1", "$2a$10$O25PQQabyvoBuC6Y8PGQGu/HtbPULLixJmPqnzUxSorIV4lVoiBk6");
		credentials.put("user2", "$2a$10$cKYps9J4S1Re/PJbTuv0QOBtyH3Ka0X04jXpPps3qFDrL0OE8tLkm");
		credentials.put("user3", "$2a$10$FQtRF0lmUupwP0V31NTj1uw91W.KD.O0jrZXLN2lUsyvXVrpf3VvO");
		credentials.put("user4", "$2a$10$T7rrsq38EifMtsP.gC00tuWdFe9UZIgr4zK2AYVkV3D8skR9GZiPa");
		credentials.put("user5", "$2a$10$bBDL67kHdKiTdJLKFJxK6OPzkHhJOg.lH4JxIIPzIMvdBUYzNLqo2");
		credentials.put("user6", "$2a$10$dayWMQHbuAvfOYqRP6ycCO7a/ubYFbMq/7hkNOiH3Fw73jEfBRx5q");
		credentials.put("user7", "$2a$10$5Ybdqvr4XYyR79XAGxnjNOroEqKhRSETvYZTm/5PwBSbvFnnZyGjy");
		credentials.put("user8", "$2a$10$kdMDZbspFtxBC1Jy9Sy2zO.d6jtYgD.OeQQrnEnSZdLUddl0QeVa.");
		credentials.put("user9", "$2a$10$TFGQjMJJkLqIDoepKPaopuJq8ChNUznjLTuMgCFgGvlJFqPAmoxga");
		credentials.put("user10", "$2a$10$8U5Z04OZOBevyW2nto.9S.2jNkPCoNwzoICiYJICGPHki1FQZxKI2");
		credentials.put("user11", "$2a$10$AIbFcYYoXOWiuHJE68dxhOGIKAy8mSwX2FL7WD6LWskbpH/twPxG6");
		credentials.put("user12", "$2a$10$O.Wlknp.DUOzEyfKRoM.6.TX85J5pxCrNYB5qwuNIF3PIuCThvNz6");
	}

	public LoginResponse login(String providerId, AuthenticationMap auth) {
		
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();
		
		LoginResponse response = null;
		
		// client.makeAuthInquiry(identityProviderConfig, authMap)

		String username = auth.get("username");
		String password = auth.get("password");
		String egn = auth.get("egn");
		
		if ((DEFAULT_PROVIDER_ID.equals(providerId) && credentials.containsKey(username)
				&& passwordEncoder.matches(password, credentials.get(username)))
				|| (TFA_PROVIDER_ID.equals(providerId) && loginConfig.getIdentifiers().contains(egn))) {
			response = new LoginResponse();
			Optional<IdentityProvider> identityProviderOpt = providerService.getProviderById(providerId);
			if (identityProviderOpt.isPresent()) {
				IdentityProvider identityProvider = identityProviderOpt.get();
				response.setProviderId(identityProvider.getId());
				response.setLoa(identityProvider.getLoa());
				response.setRelayState("relayState");
				response.setInResponseTid("transactionId-" + System.currentTimeMillis());
			}
			
			List<IdentityAttributes> attributes = null;
			if (DEFAULT_PROVIDER_ID.equals(providerId)) {
				attributes = loadAttributes(null, null);
			} else {
				// TFA
				String name = userProfileName(egn);
				if (name == null) {
					// missing profile in 2FA!
					throw new InvalidAuthenticationException("No profile in 2FA: " + egn);
				}
				attributes = loadAttributes(name, egn);
			}
			response.setAttributes(attributes);
		}
		
		/*
		 * AuditEvent
		 */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.principal(username)
				.type(EventTypes.EXT_IDP_AUTHENTICATION)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);

		return response;
	}

	private String userProfileName(String nid) {
		String encryptedNid = PersonalIdUtils.encrypt(nid, properties.getIdSecret());
		Optional<User> user = userRepository.findByPersonID(encryptedNid);
		if (user.isPresent()) {
			return user.get().getName();
		}
		log.error("nid: [{}] encrypted: [{}] not found in user profiles!", nid, encryptedNid);
		return null;
	}

	public List<IdentityAttributes> loadAttributes(String name, String identifier) {
		List<IdentityAttributes> attributes = new ArrayList<>();

		IdentityAttributes attr1 = new IdentityAttributes();
		attr1.setOid("1.1.1.1.1.1");
		attr1.setUrn("urn:egov:bg:eauth:2.0:attributes:personName");
		if (name == null) {
			name = "Петър Иванов Петров";
		}
		attr1.setValue(name);

		/*
		 * 1) PNO for identification based on (national) personal number (national civic
		 * registration number) 2) 2 character ISO country code; 3) hyphen-minus
		 * "-"(0x2D (ASCII), U+002D (UTF-8)); and 4) identifier (according to country
		 * and identity type reference).
		 */
		IdentityAttributes attr2 = new IdentityAttributes();
		attr2.setOid("2.2.2.2.2.2");
		attr2.setUrn("urn:egov:bg:eauth:2.0:attributes:personIdentifier");
		if (identifier == null) {
			identifier = TFA_EGN;
		}
		if (!identifier.contains("-")) {
			identifier = "PNOBG-" + identifier;
		}
		attr2.setValue(identifier);

		attributes.add(attr1);
		attributes.add(attr2);

		return attributes;
	}

}
