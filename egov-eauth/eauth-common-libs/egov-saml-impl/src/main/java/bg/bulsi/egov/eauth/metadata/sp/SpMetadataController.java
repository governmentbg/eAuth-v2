package bg.bulsi.egov.eauth.metadata.sp;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.joda.time.DateTime;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.common.xml.MetadataBuilderFactoryUtil;
import bg.bulsi.egov.eauth.metadata.EauthMetadataBuilder;
import bg.bulsi.egov.eauth.saml.SAMLBuilder;
import bg.bulsi.egov.eauth.saml.keystore.JKSKeyManager;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

@Slf4j
@Controller
@RequestMapping("/saml2") // saml
public class SpMetadataController {
	
	public static final String SP_METADATA_PATH = "mock-idp"; // sp.xml
	
	public static final String XML_LANG = "en";
	public static final String ORGANIZATION_NAME = "BulSI Ltd";
	public static final String ORGANIZATION_URL = "http://bul-si.bg/";
	
	@Autowired
	private JKSKeyManager keyManager;
	
	@Autowired
	private EauthMetadataBuilder metaBuilder;

	/**
     *
     * SPSSODescriptor
     * трябва задължително да има :
     *
     * 1  the public key(s) used by the IdP for authentication and encryption
     * 2 endpoints of various types for communicating with it
     * 3 explicitly supported identifier formats, if any
     * 4 explicitly supported attributes, if any
     *
     *
     * @return
     * @throws SecurityException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws MarshallingException
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 * @throws SignatureException 
	 * @throws ResolverException 
	 * @throws org.opensaml.security.SecurityException 
     */

	// metadata/{path}
    @GetMapping(path = "/service-provider-metadata/{path}", produces = "application/xml")
    @ResponseBody
    public String metadata(@PathVariable String path) throws MarshallingException, NoSuchFieldException, IllegalAccessException, ResolverException, org.opensaml.security.SecurityException, SignatureException {

    	log.debug("metadata path: [{}]", path);
    	
    	if (!path.equals(SP_METADATA_PATH)) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Metadata resource not found at [" + path + "]");
    	}
    	
        EntityDescriptor entityDescriptor = MetadataBuilderFactoryUtil.buildXmlSAMLObject(EntityDescriptor.class);
        
        String entityId = ORGANIZATION_URL + "sp"; // sp-config.yml: entity_id
        log.debug("entityId: [{}]", entityId);
        
        entityDescriptor.setEntityID(entityId);
        entityDescriptor.setID(SAMLBuilder.randomSAMLId());
        entityDescriptor.setValidUntil(new DateTime().plusMillis(86400000));
        
        SPSSODescriptor spssoDescriptor = MetadataBuilderFactoryUtil.buildXmlObject(SPSSODescriptor.class);
        
        spssoDescriptor.setAuthnRequestsSigned(Boolean.FALSE);
        spssoDescriptor.setWantAssertionsSigned(Boolean.FALSE);

        NameIDFormat nameIDFormat = MetadataBuilderFactoryUtil.buildXmlSAMLObject(NameIDFormat.class);
        nameIDFormat.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        spssoDescriptor.getNameIDFormats().add(nameIDFormat);

        spssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
       
        X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
        keyInfoGeneratorFactory.setEmitEntityCertificate(true);
        KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

        Credential credential = keyManager.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityId)));
		KeyInfo keyInfo = keyInfoGenerator.generate(credential);
        
		Signature signature = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Signature.class);
		signature.setSigningCredential(credential);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        
        entityDescriptor.setSignature(signature);
        
        KeyDescriptor keyDescriptorSigning = MetadataBuilderFactoryUtil.buildXmlSAMLObject(KeyDescriptor.class);
        keyDescriptorSigning.setUse(UsageType.SIGNING);
        keyDescriptorSigning.setKeyInfo(keyInfo);

        spssoDescriptor.getKeyDescriptors().add(keyDescriptorSigning);
        
        KeyDescriptor keyDescriptorEncryption = MetadataBuilderFactoryUtil.buildXmlSAMLObject(KeyDescriptor.class);
        keyDescriptorEncryption.setUse(UsageType.ENCRYPTION);
        KeyInfo keyInfoEncryption = keyInfoGenerator.generate(credential);
        keyDescriptorEncryption.setKeyInfo(keyInfoEncryption);

        spssoDescriptor.getKeyDescriptors().add(keyDescriptorEncryption);
        
        Organization organization = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Organization.class);
        
		OrganizationName organizationName = MetadataBuilderFactoryUtil.buildXmlSAMLObject(OrganizationName.class);
		organizationName.setXMLLang(XML_LANG);
		organizationName.setValue(ORGANIZATION_NAME);
		organization.getOrganizationNames().add(organizationName);
        
		OrganizationDisplayName displayName = MetadataBuilderFactoryUtil.buildXmlSAMLObject(OrganizationDisplayName.class);;
		displayName.setXMLLang(XML_LANG);
		displayName.setValue(ORGANIZATION_NAME);
		organization.getDisplayNames().add(displayName);
        
		OrganizationURL organizationUrl = MetadataBuilderFactoryUtil.buildXmlSAMLObject(OrganizationURL.class);
		organizationUrl.setXMLLang(XML_LANG);
		organizationUrl.setValue(ORGANIZATION_URL);
		organization.getURLs().add(organizationUrl);
       
		entityDescriptor.setOrganization(organization);

		entityDescriptor.getContactPersons().add(metaBuilder.buildContact(ContactPersonTypeEnumeration.SUPPORT));
		entityDescriptor.getContactPersons().add(metaBuilder.buildContact(ContactPersonTypeEnumeration.TECHNICAL));	
		
        // List<Endpoint> allEndponts = spssoDescriptor.getEndpoints();

        entityDescriptor.getRoleDescriptors().add(spssoDescriptor);
        
        entityDescriptor.getAdditionalMetadataLocations();
        
        return writeEntityDescriptor(entityDescriptor);

    }

    private String writeEntityDescriptor(EntityDescriptor entityDescriptor) throws MarshallingException, SignatureException {

		MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(entityDescriptor);
		Element element = marshaller.marshall(entityDescriptor);
		Signer.signObject(entityDescriptor.getSignature());

		return SerializeSupport.nodeToString(element);
	}

}
