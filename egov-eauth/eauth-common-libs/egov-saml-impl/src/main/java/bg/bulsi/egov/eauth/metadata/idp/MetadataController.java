package bg.bulsi.egov.eauth.metadata.idp;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.IDPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.KeyDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.NameIDFormatBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleSignOnServiceBuilder;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.common.exceptions.EAuthMetadataException;
import bg.bulsi.egov.eauth.common.xml.SSOException;
import bg.bulsi.egov.eauth.metadata.EauthMetadataBuilder;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.keystore.KeyManager;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

/*
 * <a href="https://medium.com/@sagarag/reloading-saml-why-do-you-need-metadata-3fbeb43320c3">info</a>
 */
/**
 * @author strahil
 *
 */
@Slf4j
@Controller
@RequestMapping(path = "/saml/metadata")
public class MetadataController {

	@Autowired
	private KeyManager keyManager;

	@Autowired
	private IdpConfigurationProperties idpConfigurationProperties;
	

	@Autowired
	private EauthMetadataBuilder metaBuilder;
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


	/**
	 * @return
	 * @throws TransformerException
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws MarshallingException
	 * @throws InitializationException
	 * @throws org.opensaml.security.SecurityException
	 * @throws SignatureException 
	 */
	@GetMapping(path = "/idp", produces = "application/xml", headers = "Accept=application/xml")
	// public String metadata(@PathVariable("entityId") String id)
	@ResponseBody
	public String metadata()
			throws TransformerException, IOException, NoSuchFieldException, IllegalAccessException, MarshallingException, InitializationException,
			org.opensaml.security.SecurityException, SignatureException {

		EntityDescriptor entityDescriptor = null;
		try {
			entityDescriptor = metadataGen();
		} catch (SecurityException | ResolverException | EAuthMetadataException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		String result = writeEntityDescriptor(entityDescriptor);
		// log.debug("XMLresult: {}", result);
		return result;

	}


	/**
	 * Descriptor generator
	 * @return generated EntityDescriptor element
	 * @throws SecurityException
	 * @throws org.opensaml.core.xml.io.MarshallingException
	 * @throws TransformerException
	 * @throws org.opensaml.security.SecurityException
	 * @throws ResolverException 
	 * @throws EAuthMetadataException 
	 */
	public EntityDescriptor metadataGen() throws SecurityException, org.opensaml.core.xml.io.MarshallingException, TransformerException, org.opensaml.security.SecurityException, ResolverException, EAuthMetadataException {

		/*
		 * IDP Descriptor
		 */

		// EntityDescriptor entityDescriptorIdp = MetadataBuilderFactoryUtil.buildXmlObject(EntityDescriptor.class);
		EntityDescriptor entityDescriptorIdp = new EntityDescriptorBuilder().buildObject();
		entityDescriptorIdp.setEntityID(idpConfigurationProperties.getEntityId()); //"3rdPartyIdp"
		entityDescriptorIdp.setID(idpConfigurationProperties.getEntityId()); //?
		entityDescriptorIdp.setValidUntil(new DateTime().plusSeconds(idpConfigurationProperties.getExpires()));
		entityDescriptorIdp.setCacheDuration(new DateTime().plusSeconds(idpConfigurationProperties.getExpires()).getMillis());
		entityDescriptorIdp.setOrganization(metaBuilder.buildOrganization());
		entityDescriptorIdp.getContactPersons().add(metaBuilder.buildContact(ContactPersonTypeEnumeration.SUPPORT));
		entityDescriptorIdp.getContactPersons().add(metaBuilder.buildContact(ContactPersonTypeEnumeration.TECHNICAL));		// RoleDescriptor roleDescriptor = SAMLBuilder.buildSAMLObject(RoleDescriptor.class, RoleDescriptor.DEFAULT_ELEMENT_NAME);
		// SSODescriptor ssoDescriptor= SAMLBuilder.buildSAMLObject(SSODescriptor.class, SSODescriptor.DEFAULT_ELEMENT_NAME);
		// AffiliationDescriptor affiliationDescriptor = new AffiliationDescriptorBuilder().buildObject();
		
		IDPSSODescriptor idpssoDescriptor = new IDPSSODescriptorBuilder().buildObject();
		idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
		KeyDescriptor encKeyDescriptor = new KeyDescriptorBuilder().buildObject();
		encKeyDescriptor.setUse(UsageType.SIGNING);
		
		X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
		keyInfoGeneratorFactory.setEmitEntityCertificate(true);
		KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

		Credential credential = keyManager.resolveSingle(new CriteriaSet(new EntityIdCriterion(idpConfigurationProperties.getEntityId())));
		entityDescriptorIdp.setSignature(metaBuilder.buildSignature(credential));
		KeyInfo keyInfo = keyInfoGenerator.generate(credential);
		encKeyDescriptor.setKeyInfo(keyInfo);
		idpssoDescriptor.getKeyDescriptors().add(encKeyDescriptor);

		NameIDFormat nameIDFormat = new NameIDFormatBuilder().buildObject();
		nameIDFormat.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
		idpssoDescriptor.getNameIDFormats().add(nameIDFormat);

		SingleSignOnService singleSignOnService = new SingleSignOnServiceBuilder().buildObject();
		singleSignOnService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		singleSignOnService.setLocation(idpConfigurationProperties.getBaseUrl() + "/SingleSignOnService");
		//singleSignOnService.setResponseLocation(idpConfigurationProperties.getBaseUrl() + "/SingleSignOnService");

		idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);

		entityDescriptorIdp.getRoleDescriptors().add(idpssoDescriptor);
		
        Extensions e = metaBuilder.generateExtensions();
        if (!e.getUnknownXMLObjects().isEmpty()) {
        	entityDescriptorIdp.setExtensions(e);
        }

//		Schema sh = idpConfigurationProperties.readSchemaFromXsd(idpConfigurationProperties.getClaims());
//		Extensions extensions = new ExtensionsBuilder().buildObject();
//		extensions.setSchemaLocation(sh.getClass().getCanonicalName());
//		entityDescriptorIdp.setExtensions(extensions);
		
		return entityDescriptorIdp;
	}


	private String writeEntityDescriptor(EntityDescriptor entityDescriptor) throws MarshallingException, SignatureException {

		MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(entityDescriptor);
		Element element = marshaller.marshall(entityDescriptor);
		Signer.signObject(entityDescriptor.getSignature());
		
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
