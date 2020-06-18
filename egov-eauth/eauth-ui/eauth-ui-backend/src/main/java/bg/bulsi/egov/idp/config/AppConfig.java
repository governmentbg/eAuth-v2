package bg.bulsi.egov.idp.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import bg.bulsi.egov.eauth.config.ProfileModelConfig;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.keystore.JKSKeyManager;
import bg.bulsi.egov.eauth.saml.keystore.KeyManager;
import bg.bulsi.egov.eauth.saml.keystore.KeyStoreLocator;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig.ProviderAuthAttribute;
import bg.bulsi.egov.idp.dto.AuthenticationAttribute;
import bg.bulsi.egov.idp.dto.IdentityProvider;
import bg.bulsi.egov.idp.saml.SAMLMessageHandler;
import bg.bulsi.egov.idp.services.temp.ProviderService;
import bg.bulsi.egov.idp.services.validator.AuthnRequestValidator;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Import(ProfileModelConfig.class)
@Slf4j
public class AppConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		modelMapper.createTypeMap(EidProviderConfig.class, IdentityProvider.class).addMappings(mapping -> {
			mapping.using((Converter<Map<String, ProviderAuthAttribute>, List<AuthenticationAttribute>>) context -> {

				List<AuthenticationAttribute> destination = new ArrayList<>();

				Map<String, ProviderAuthAttribute> source = context.getSource();
				if (source != null) {
					destination.addAll(source.values());
				}

				return destination;
			}).map(EidProviderConfig::getAttributes, IdentityProvider::setAttributes);

		});

		return modelMapper;
	}
	
	@Bean
	@Autowired
	public SAMLMessageHandler samlMessageHandler(
			KeyManager keyManager,
//			Collection<SAMLMessageDecoder> decoders,
//			SAMLMessageEncoder encoder,
//			SecurityPolicyResolver securityPolicyResolver,
			IdpConfigurationProperties idpConfiguration,
			ApplicationEventPublisher applicationEventPublisher,
			AuthnRequestValidator authnValidator,
			ProviderService providerService
			) {

		/*
		 * BasicSecurityPolicy securityPolicy = new BasicSecurityPolicy();
		 * securityPolicy.getPolicyRules().addAll( Collections.singletonList(new
		 * IssueInstantRule(clockSkew, expires)) );
		 */

		// Message decoders
		// HTTPRedirectDeflateDecoder httpRedirectDeflateDecoder = new
		// HTTPRedirectDeflateDecoder();
		// HTTPPostDecoder httpPostDecoder = new HTTPPostDecoder();

		// SAMLMessageEncoder encoder = new HTTPPostEncoder();

		return new SAMLMessageHandler(keyManager, idpConfiguration, applicationEventPublisher, authnValidator, providerService);
	}

	@Autowired
	@Bean
	public JKSKeyManager keyManager(IdpConfigurationProperties idpConfiguration) throws IOException, GeneralSecurityException {
		
	    log.debug("IdpConfigurationProperties: {}", idpConfiguration.toString());
		KeyStore keyStore = KeyStoreLocator.createKeyStore(idpConfiguration.getPassphrase());

		String pk = idpConfiguration.readFromPem(idpConfiguration.getPrivateKey());
		log.info("APP_CONFIG:PK: {}",pk);
		String cert = idpConfiguration.readFromPem(idpConfiguration.getCertificate());
		log.info("APP_CONFIG:CERT: {}",cert);
		
		KeyStoreLocator.addPrivateKey(keyStore, idpConfiguration.getEntityId(), pk, cert, idpConfiguration.getPassphrase());

		return new JKSKeyManager(keyStore,
				Collections.singletonMap(idpConfiguration.getEntityId(), idpConfiguration.getPassphrase()),
				idpConfiguration.getEntityId());
	}
	
}
