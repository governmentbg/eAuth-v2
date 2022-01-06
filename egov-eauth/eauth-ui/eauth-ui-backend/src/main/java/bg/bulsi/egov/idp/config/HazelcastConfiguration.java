package bg.bulsi.egov.idp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import bg.bulsi.egov.hazelcast.service.HazelcastService;
import bg.bulsi.egov.hazelcast.util.HazelcastUtils;

@Configuration
@EnableHazelcastHttpSession(sessionMapName = HazelcastConfiguration.SESSION_MAP_NAME)
public class HazelcastConfiguration {

	public static final String SAML_MAP = "SAMLRequestMap";

	static final String SESSION_MAP_NAME = HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME + ":idp:eauth";

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setDomainName("egov.bg");
		serializer.setCookieName("eauthIdp");
		// serializer.setUseHttpOnlyCookie(false);
		serializer.setUseSecureCookie(false);
		serializer.setCookieMaxAge(1800);
		return serializer;
	}

	@Bean
	public HazelcastService getHazelcastService() {
		return new HazelcastService();
	}

	@Bean
	public HazelcastUtils getHazelcastUtils() {
		return new HazelcastUtils();
	}
}
