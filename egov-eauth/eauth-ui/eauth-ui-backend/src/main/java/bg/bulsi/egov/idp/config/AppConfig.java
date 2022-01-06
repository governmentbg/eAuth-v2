package bg.bulsi.egov.idp.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

import bg.bulsi.egov.eauth.config.ProfileModelConfig;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.keystore.JKSKeyManager;
import bg.bulsi.egov.eauth.saml.keystore.KeyManager;
import bg.bulsi.egov.eauth.saml.keystore.KeyStoreLocator;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;
import bg.bulsi.egov.idp.client.config.model.ProviderAuthAttribute;
import bg.bulsi.egov.idp.dto.AuthenticationAttribute;
import bg.bulsi.egov.idp.dto.IdentityProvider;
import bg.bulsi.egov.idp.saml.SAMLMessageHandler;
import bg.bulsi.egov.idp.services.temp.ProviderService;
import bg.bulsi.egov.idp.services.validator.AuthnRequestValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Import(ProfileModelConfig.class)
public class AppConfig {

	@Value("${server.ssl.trust-store-type}")
	private String trustStoreType;

	@Value("${server.ssl.trust-store}")
	private String trustStore;

	@Value("${server.ssl.trust-store-password}")
	private String trustStorePass;

	@Autowired
	private ResourceLoader resourceLoader;

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

	/*
	 * @Bean
	 * 
	 * @Autowired public EauthMetadataBuilder metadataBuilder(KeyManager
	 * keyManager,IdpConfigurationProperties idpConfigurationProperties) {
	 * Credential idpEncryptionCredential = keyManager.getDefaultCredential();
	 * Credential idpSigningCredential= keyManager.getDefaultCredential(); return
	 * new EauthMetadataBuilder(idpEncryptionCredential, idpSigningCredential,
	 * idpConfigurationProperties); }
	 */

	@Bean
	@Autowired
	public SAMLMessageHandler samlMessageHandler(KeyManager keyManager,
			// Collection<SAMLMessageDecoder> decoders,
			// SAMLMessageEncoder encoder,
			// SecurityPolicyResolver securityPolicyResolver,
			IdpConfigurationProperties idpConfiguration, ApplicationEventPublisher applicationEventPublisher,
			AuthnRequestValidator authnValidator, ProviderService providerService, RestTemplate restTempalte,
			Environment environment) {

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

		return new SAMLMessageHandler(keyManager, idpConfiguration, applicationEventPublisher, authnValidator,
				providerService, restTempalte, environment);
	}

	@Autowired
	@Bean
	public JKSKeyManager keyManager(IdpConfigurationProperties idpConfiguration)
			throws IOException, GeneralSecurityException {

		log.debug("IdpConfigurationProperties: {}", idpConfiguration.toString());
		KeyStore keyStore = KeyStoreLocator.createKeyStore(idpConfiguration.getPassphrase());

		String pk = idpConfiguration.readFromPem(idpConfiguration.getPrivateKey());
		log.info("APP_CONFIG:PK: {}", pk);
		String cert = idpConfiguration.readFromPem(idpConfiguration.getCertificate());
		log.info("APP_CONFIG:CERT: {}", cert);

		KeyStoreLocator.addPrivateKey(keyStore, idpConfiguration.getEntityId(), pk, cert,
				idpConfiguration.getPassphrase());

		return new JKSKeyManager(keyStore,
				Collections.singletonMap(idpConfiguration.getEntityId(), idpConfiguration.getPassphrase()),
				idpConfiguration.getEntityId());
	}

	@Autowired
	@Bean(name = "trustStoreIdp")
	public KeyStore trustStoreIdp() throws IOException, GeneralSecurityException {

		if (StringUtils.isBlank(trustStoreType)) {
			trustStoreType = "JKS";
		}

		KeyStore keyStore;
		keyStore = KeyStore.getInstance(trustStoreType);

		InputStream tio = null;
		Resource resource = resourceLoader.getResource(trustStore);
		log.debug("TrustStore resource '{}'", resource.getURI().toString());
		tio = resource.getInputStream();
		keyStore.load(tio, trustStorePass.toCharArray());

		return keyStore;
	}

	@Bean(name = "keyStoreIdp")
	@Autowired
	public KeyStore keyStoreIdp(EidProvidersConfiguration eidProviderConfiguration)
			throws IOException, GeneralSecurityException {

		String keyStoreType = null;
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(null,null);
		log.info("GET all keystore for providers from {}", eidProviderConfiguration.toString());
		for (Entry<String, EidProviderConfig> eidProviderConfig : eidProviderConfiguration.getProviders().entrySet()) {

			if (eidProviderConfig.getValue().getEidProviderConnection().isClientSslRequired()) {
				String keyStorePath = eidProviderConfig.getValue().getEidProviderConnection().getClientKeyStore()
						.getPath();
				String keyStorePass = eidProviderConfig.getValue().getEidProviderConnection().getClientKeyStore()
						.getPass();
				String keyStoreAlias = eidProviderConfig.getValue().getEidProviderConnection().getClientKeyStore()
						.getAlias();
				keyStoreType = eidProviderConfig.getValue().getEidProviderConnection().getClientKeyStore().getType();
				if (keyStoreType == null || keyStoreType.isEmpty()) {
					keyStoreType = "JKS";
				}
				KeyStore currKeyStore = KeyStore.getInstance(keyStoreType);
				log.debug("Create keystore with type {}", keyStoreType);

				InputStream kio = null;
				Resource resource = resourceLoader.getResource(keyStorePath);
				log.debug("KeyStore resource: '{}', type: '{}', pass: '{}'", resource.getURI().toString(), keyStoreType,
						keyStorePass);
				kio = resource.getInputStream();

				currKeyStore.load(kio, keyStorePass.toCharArray());
				kio.close();
				keyStore.setCertificateEntry(keyStoreAlias, currKeyStore.getCertificate(keyStoreAlias));
				keyStore.setKeyEntry(keyStoreAlias, currKeyStore.getKey(keyStoreAlias, keyStorePass.toCharArray()),
						keyStorePass.toCharArray(), currKeyStore.getCertificateChain(keyStoreAlias));
			}
		}

		if (keyStore == null) {
			log.debug("Create empty EID keystore!");

		}
		Enumeration<String> aliases = keyStore.aliases();
		while (aliases.hasMoreElements()) {
			log.debug("KeyStoreAlias: [{}]", aliases.nextElement());

		}

		return keyStore;
	}

}
