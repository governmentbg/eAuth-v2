package bg.bulsi.egov.eauth.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.keystore.JKSKeyManager;
import bg.bulsi.egov.eauth.saml.keystore.KeyStoreLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ProfileMetadataConfig {

	@Autowired
	@Bean
	public JKSKeyManager keyManager(IdpConfigurationProperties idpConfiguration) throws IOException, GeneralSecurityException {

		log.info("IdpConfigurationProperties: {}", idpConfiguration.toString());
		KeyStore keyStore = KeyStoreLocator.createKeyStore(idpConfiguration.getPassphrase());

		String pk = idpConfiguration.readFromPem(idpConfiguration.getPrivateKey());
		log.info("APP_CONFIG:PK: {}", pk);
		String cert = idpConfiguration.readFromPem(idpConfiguration.getCertificate());
		log.info("APP_CONFIG:CERT: {}", cert);

		KeyStoreLocator.addPrivateKey(keyStore, idpConfiguration.getEntityId(), pk, cert, idpConfiguration.getPassphrase());

		return new JKSKeyManager(	keyStore,
									Collections.singletonMap(idpConfiguration.getEntityId(), idpConfiguration.getPassphrase()),
									idpConfiguration.getEntityId());
	}

}
