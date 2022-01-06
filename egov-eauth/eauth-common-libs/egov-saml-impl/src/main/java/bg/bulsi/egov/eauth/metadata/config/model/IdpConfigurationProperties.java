package bg.bulsi.egov.eauth.metadata.config.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import bg.bulsi.egov.eauth.metadata.config.api.AuthenticationMethod;
import bg.bulsi.egov.eauth.metadata.config.security.tokens.FederatedUserAuthenticationToken;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * application-idp-config.yml 
 */
@Component
@Data
@ConfigurationProperties(prefix = "idp")
@ToString(includeFieldNames = true)
@Slf4j
public class IdpConfigurationProperties {

	// # EntityID
	private String entityId;
	// # Path file to Private key used to sign the SAML response
	private String privateKey;
	// # Path file to Public certificate to verify the signature of the SAML
	// response
	private String certificate;
	// # Passphrase of the keystore
	private String passphrase;
	// #
	private AuthenticationMethod authMethod; // = AuthenticationMethod.ALL;
	// # base url
	private String baseUrl;
	// # backend context for failure handlers url
	private String pathPrefix;
	// # The number of seconds before a lower time bound, or after an upper time
	// bound, to consider still acceptable
	private Integer clockSkew;
	// # Number of seconds after a message issue instant after which the message is
	// considered expired
	private Integer expires;
	// # Are endpoints compared. If so then pay notice to the base_url when behind a
	// load balancer
	private Boolean compareEndpoints;
	// #Organization INFO
	private OrganizationData organizationData;
	
	private ContactData supportContact;
	
	private ContactData technicalContact;
	// # Path to Claims XSD
	private String claims;

	private Boolean needsSigning;
	
	private String defaultSignatureAlgorithm; // = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;
	
	private String signatureAlgorithm;
	
	private String spType;
	
	private Set<String> digestMethods;
	
	private Set<String> signingMethods;
	
	private Set<String> encryptionAlgorithms;
	
	private transient HashMap<String, String> protocolBindingLocation; // = new HashMap<>();
	
	private List<String> levelOfAssurance;
	
	private Boolean hideLoaType;
	
	private String eauthProtocolVersion;

	private List<String> eauthAssertionAttributes;
	
	private Map<String, List<String>> attributes; // = new TreeMap<>();
	// # !Missing default constructor
	private List<FederatedUserAuthenticationToken> users; // = new ArrayList<>();
	
	private String acsEndpoint;

	@Autowired
	public IdpConfigurationProperties() {
		reset();
	}

	// -----------------------------------------------------------------------------------------------

	// private AuthenticationMethod authenticationMethod;

	public void reset() {
		/*
		 * setEntityId(defaultEntityId); resetAttributes();
		 * resetKeyStore(defaultEntityId, privateKey, certificate);
		 * 
		 * resetUsers();
		 * 
		 * setAcsEndpoint(null);
		 * setAuthenticationMethod(this.defaultAuthenticationMethod);
		 */

		setSignatureAlgorithm(getDefaultSignatureAlgorithm());
	}

	private void resetUsers() {
		users.clear();

		// TODO  да се прехмахне в един момент !!!

		users.addAll(Arrays.asList(
				new FederatedUserAuthenticationToken("admin", "secret",
						Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"),
								new SimpleGrantedAuthority("ROLE_ADMIN"))),
				new FederatedUserAuthenticationToken("user", "secret",
						Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))));
	}

	private void resetAttributes() {
		attributes.clear();
		// TODO - да се премахне в един момент !!!

		putAttribute("urn:mace:dir:attribute-def:uid", "john.doe");
		putAttribute("urn:mace:dir:attribute-def:cn", "John Doe");
		putAttribute("urn:mace:dir:attribute-def:givenName", "John");
		putAttribute("urn:mace:dir:attribute-def:sn", "Doe");
		putAttribute("urn:mace:dir:attribute-def:displayName", "John Doe");
		putAttribute("urn:mace:dir:attribute-def:mail", "j.doe@example.com");
		putAttribute("urn:mace:terena.org:attribute-def:schacHomeOrganization", "example.com");
		putAttribute("urn:mace:dir:attribute-def:eduPersonPrincipalName", "j.doe@example.com");
	}

	private void putAttribute(String key, String... values) {
		this.attributes.put(key, Arrays.asList(values));
	}

	// -----------------------------------------------------------------------------------------------
	// TODO -- ??? ---
	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;

		/*
		 * ((BasicSecurityConfiguration) Configuration.getGlobalSecurityConfiguration())
		 * .registerSignatureAlgorithmURI("RSA", signatureAlgorithm);
		 */
	}

	@SuppressWarnings("deprecation")
	public String readFromPem(String classPath) throws IOException {

		try (InputStream stream = new ClassPathResource(classPath).getInputStream()) {
			return IOUtils.toString(stream);
		} catch (IOException e) {
			throw new IOException(e);
		}

	}

	@SuppressWarnings("deprecation")
	public String readFromXsd(String classPath) throws IOException {

		try (InputStream stream = new ClassPathResource(classPath).getInputStream()) {
			return IOUtils.toString(stream);
		} catch (IOException e) {
			throw new IOException(e);
		}

	}
	

	public Schema readSchemaFromXsd(String filePath) {
		File xsdFile =  new File(filePath);

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(xsdFile);
		} catch (SAXException e) {
			log.error(e.getMessage(), e);
		}
		
		return schema;

	}
	
}
