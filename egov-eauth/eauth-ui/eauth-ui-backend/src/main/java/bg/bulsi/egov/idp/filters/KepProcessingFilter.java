package bg.bulsi.egov.idp.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.context.request.RequestContextHolder;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.audit.util.HttpReqRespUtils;
import bg.bulsi.egov.eauth.metadata.config.security.tokens.FederatedUserAuthenticationToken;
import bg.bulsi.egov.idp.dto.IdentityAttributes;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.security.IdpPrincipal;
import bg.bulsi.egov.idp.security.InvalidAuthenticationException;
import bg.bulsi.egov.idp.services.temp.UserService;
import bg.bulsi.egov.idp.utils.Streams;
import lombok.Setter;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

/*

SSL_CLIENT_S_DN: C=BG,CN=Krassimir Blagoev Antonov,serialNumber=PNOBG-6901136926,GN=Krassimir,SN=Antonov,emailAddress=krassi2m@gmail.com
SSL_CLIENT_I_DN: CN=B-Trust Operational Qualified CA,OU=B-Trust,O=BORICA AD,organizationIdentifier=NTRBG-201230426,C=BG
SSL_CLIENT_S_DN_x509:
SSL_CLIENT_VERIFY: SUCCESS
SSL_CLIENT_M_SERIAL: 2CB9BB3A277BA30D
SSL_CLIENT_S_DN_C: BG
SSL_CLIENT_S_DN_CN: Krassimir Blagoev Antonov
SSL_CLIENT_S_DN_G: Krassimir
SSL_CLIENT_S_DN_S: Antonov
SSL_CLIENT_S_DN_Email: krassi2m@gmail.com
SSL_CLIENT_CERT_RFC4523_CEA: { serialNumber 3222812866805211917, issuer rdnSequence:"CN=B-Trust Operational Qualified CA,OU=B-Trust,O=BORICA AD,organizationIdentifier=NTRBG-201230426,C=BG" }
SSL_CLIENT_I_DN_UID: (null)

*/
@Slf4j
public class KepProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private ApplicationEventPublisher applicationEventPublisher;

	private static final String HEADER_PREFFIX = "SSL_CLIENT_";
	private static final String SSL_CLIENT_VERIFY = "VERIFY";

	@Setter
	private UserService userService;


	public KepProcessingFilter(String defaultFilterProcessesUrl, ApplicationEventPublisher applicationEventPublisher) {
		super(defaultFilterProcessesUrl);
		this.applicationEventPublisher = applicationEventPublisher;
	}


	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		Map<String, List<String>> certificateProperties = Streams.enumerationAsStream(request.getHeaderNames())
				.filter(s -> s.toUpperCase().startsWith(HEADER_PREFFIX))
				.collect(Collectors.toMap(s -> s.toUpperCase().replace(HEADER_PREFFIX, ""), o -> Streams.enumerationAsStream(request.getHeaders(o)).collect(Collectors.toList())));

		log.info("certificateProperties: {}", certificateProperties);

		if (certificateProperties.isEmpty()) {
			throw new InvalidAuthenticationException("Missing certificate properties");
		}
		if (!checkRequiredAttributes(certificateProperties)) {
			throw new InvalidAuthenticationException("Missing certificate required properties");
		}

		if (!(certificateProperties.containsKey(SSL_CLIENT_VERIFY) &&
				certificateProperties.get(SSL_CLIENT_VERIFY).size() > 0 &&
				"SUCCESS".equals(certificateProperties.get(SSL_CLIENT_VERIFY).get(0).toUpperCase()))) {
			throw new InvalidAuthenticationException("No QES provided");
		}

		if (!certificateProperties.containsKey("S_DN")) {
			throw new InvalidAuthenticationException("Missing SSL_CLIENT_S_DN header key");
		}

		// SSL_CLIENT_S_DN: C=BG,CN=Krassimir Blagoev Antonov,serialNumber=PNOBG-6901136926,GN=Krassimir,SN=Antonov,emailAddress=krassi2m@gmail.com
		Map<String, String> certificateDn = Stream.of(certificateProperties.get("S_DN").stream()
				.findFirst()
				.orElseThrow(() -> new InvalidAuthenticationException("Missing Missing SSL_CLIENT_S_DN header key data"))
				.split(","))
				.map(s -> s.trim().split("="))
				.collect(Collectors.toMap(k -> k[0].trim(), v -> v[1].trim()));

		List<IdentityAttributes> attributes = this.userService
				.loadAttributes(certificateDn.get("CN"), certificateDn.get("serialNumber"));

		var principal = new IdpPrincipal("eAuth_Certificate", LevelOfAssurance.HIGH, attributes);

		var token = new FederatedUserAuthenticationToken(principal, null, AuthorityUtils.createAuthorityList("ROLE_FULL_AUTHENTICATED"));

		/*
		 * AuditEvent
		 */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.principal(((Principal) principal).getName())
				.type(EventTypes.QEP_AUTHENTICATION)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);
		
		return token;
	}


	private boolean checkRequiredAttributes(Map<String, List<String>> certificateProperties) {
		return true;
	}

}
