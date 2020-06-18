package bg.bulsi.egov.eauth.metadata.idp;

import java.io.IOException;

import javax.net.ssl.KeyManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.ExtensionsBuilder;
import org.opensaml.saml.saml2.metadata.impl.IDPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.KeyDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.NameIDFormatBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleSignOnServiceBuilder;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.common.xml.SSOException;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

/*
 * <a href="https://medium.com/@sagarag/reloading-saml-why-do-you-need-metadata-3fbeb43320c3">info</a>
 */
@Slf4j
@Controller
@RequestMapping(path = "/saml/metadata")
public class MetadataController {

//	@Autowired
//	private KeyManager keyManager;

	@Autowired
	private IdpConfigurationProperties idpConfigurationProperties;

//	@Autowired
//	private Environment environment;

	@Value("$server.port")
	private String localPort;

	public MetadataController() throws SSOException {
		init();
	}


	private void init() throws SSOException {

		try {
			InitializationService.initialize();
		} catch (InitializationException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new SSOException("Error in bootstrapping the OpenSAML library", e);
		}

	}


	/**
	 * @return
	 * @throws SecurityException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws MarshallingException
	 * @throws org.opensaml.security.SecurityException
	 * @throws SecurityException
	 */

	@GetMapping(path = "/test", produces = "application/xml", headers = "Accept=application/xml")
	@ResponseBody
	public String test() throws SecurityException, ParserConfigurationException, TransformerException {
		return "test";
	}


	@GetMapping(path = "/idp", produces = "application/xml", headers = "Accept=application/xml")
	// public String metadata(@PathVariable("entityId") String id)
	public String metadata()
			throws TransformerException, IOException, NoSuchFieldException, IllegalAccessException, MarshallingException, InitializationException,
			org.opensaml.security.SecurityException {

		// SAMLMetadataContext sAMLMetadataContext;
		EntityDescriptor entityDescriptor = metadataGen();

		String result = writeEntityDescriptor(entityDescriptor);

		log.info("XMLresult:  {}", result);
		return result;

	}


	/*
	 * Descriptor generator
	 */
	public EntityDescriptor metadataGen() throws SecurityException, org.opensaml.core.xml.io.MarshallingException, TransformerException, org.opensaml.security.SecurityException {

		/*
		 * IDP Descriptor
		 */

		// EntityDescriptor entityDescriptorIdp = MetadataBuilderFactoryUtil.buildXmlObject(EntityDescriptor.class);
		EntityDescriptor entityDescriptorIdp = new EntityDescriptorBuilder().buildObject();
		entityDescriptorIdp.setEntityID("3rdPartyIdp");
		entityDescriptorIdp.setID("asdas");
		entityDescriptorIdp.setValidUntil(new DateTime().plusSeconds(86400));
		entityDescriptorIdp.setCacheDuration(new DateTime().plusSeconds(86400).getMillis());

		// RoleDescriptor roleDescriptor = SAMLBuilder.buildSAMLObject(RoleDescriptor.class, RoleDescriptor.DEFAULT_ELEMENT_NAME);
		// SSODescriptor ssoDescriptor= SAMLBuilder.buildSAMLObject(SSODescriptor.class, SSODescriptor.DEFAULT_ELEMENT_NAME);
		// AffiliationDescriptor affiliationDescriptor = new AffiliationDescriptorBuilder().buildObject();

		IDPSSODescriptor idpssoDescriptor = new IDPSSODescriptorBuilder().buildObject();
		idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
		KeyDescriptor encKeyDescriptor = new KeyDescriptorBuilder().buildObject();
		encKeyDescriptor.setUse(UsageType.SIGNING);

		X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
		keyInfoGeneratorFactory.setEmitEntityCertificate(true);
		KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

		Credential credential = null;
//		 Credential credential = keyManager.resolveSingle(new CriteriaSet(new EntityIDCriteria(idpConfiguration.getEntityId())));
		KeyInfo keyInfo = keyInfoGenerator.generate(credential);
		encKeyDescriptor.setKeyInfo(keyInfo);
		
		idpssoDescriptor.getKeyDescriptors().add(encKeyDescriptor);

		NameIDFormat nameIDFormat = new NameIDFormatBuilder().buildObject();
		nameIDFormat.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
		idpssoDescriptor.getNameIDFormats().add(nameIDFormat);

		SingleSignOnService singleSignOnService = new SingleSignOnServiceBuilder().buildObject();
		singleSignOnService.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		singleSignOnService.setLocation("http://localhost:" + localPort + "/SingleSignOnService");
		singleSignOnService.setResponseLocation("http://localhost:" + localPort + "/SingleSignOnService");

		idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);

		entityDescriptorIdp.getRoleDescriptors().add(idpssoDescriptor);

//		Schema sh = idpConfigurationProperties.readSchemaFromXsd(idpConfigurationProperties.getClaims());
//		Extensions extensions = new ExtensionsBuilder().buildObject();
//		extensions.setSchemaLocation(sh.getClass().getCanonicalName());
//		entityDescriptorIdp.setExtensions(extensions);
		
		return entityDescriptorIdp;
	}


	private String writeEntityDescriptor(EntityDescriptor entityDescriptor) throws MarshallingException {

		MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(entityDescriptor);
		Element element = marshaller.marshall(entityDescriptor);
		return SerializeSupport.nodeToString(element);

	}


	public static void main(String[] args) throws Exception {

		MetadataController mdc = new MetadataController();
		String result;

		EntityDescriptor entityDescriptorIdp = mdc.metadataGen();

		try {
			result = mdc.writeEntityDescriptor(entityDescriptorIdp);
			log.info("RESULT_N: {}", result);
		} catch (MarshallingException e) {
			log.info(e.getLocalizedMessage(), e);
		}

	}

}
