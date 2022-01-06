package bg.bulsi.egov.eauth.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import bg.bulsi.egov.eauth.saml.keystore.JKSKeyManager;
import bg.bulsi.egov.security.eauth.config.EauthProviderProperties;
import bg.bulsi.egov.security.utils.PersonalIdUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Saml2Service {
	
	@Autowired
    protected EauthProviderProperties properties;
	
	@Autowired
	private JKSKeyManager keyManager;

	
	public Response getSaml2Response(String saml2ResponseXml)
			throws ParserConfigurationException, SAXException, IOException, UnmarshallingException {
		ByteArrayInputStream is = new ByteArrayInputStream(saml2ResponseXml.getBytes(StandardCharsets.UTF_8));

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = docBuilder.parse(is);
		Element element = document.getDocumentElement();

		Response saml2Response = (Response) Objects
				.requireNonNull(XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element))
				.unmarshall(element);

		return saml2Response;
	}
	
	public String getNidFromSaml2Response(Response saml2Response) {
		String identifier = null;
		
		try {
			AttributeStatement attritbuteStatement = getAttributeStatement(saml2Response);
			List<Attribute> attritbutes = attritbuteStatement.getAttributes();
			for (Attribute attr : attritbutes) {
				if (attr.getName().contains("personIdentifier")) {
					String identValue = ((XSStringImpl) attr.getAttributeValues().get(0)).getValue();
					identifier = identValue.substring(identValue.indexOf('-') + 1);
				}
			}
		} catch (DecryptionException e) {
			log.error("error: {}", e);
		}
		
		return identifier;
	}
	
	public String encrypted(String plain) {
		String encrypted = PersonalIdUtils.encrypt(plain, properties.getIdSecret());
		return encrypted;
	}
	
	public String getNameFromSaml2Response(Response saml2Response) {
		String name = null;
		
		try {
			AttributeStatement attritbuteStatement = getAttributeStatement(saml2Response);
			List<Attribute> attritbutes = attritbuteStatement.getAttributes();
			for (Attribute attr : attritbutes) {
				if (attr.getName().contains("personName")) {
					name = ((XSStringImpl) attr.getAttributeValues().get(0)).getValue();
				}
			}
		} catch (DecryptionException e) {
			log.error("error: {}", e);
		}
		
		return name;
	}
	
	public boolean isEncryptedAssertions(Response saml2Response) {
		boolean isEncryptedAssertions = saml2Response.getEncryptedAssertions() != null && !saml2Response.getEncryptedAssertions().isEmpty();
		log.info("isEncryptedAssertions: [{}]", isEncryptedAssertions);
		return isEncryptedAssertions;
	}
	
	public AttributeStatement getAttributeStatement(Response saml2Response) throws DecryptionException {
		AttributeStatement attritbuteStatement = null;
		if (isEncryptedAssertions(saml2Response)) {
			EncryptedAssertion encryptedAssertion = saml2Response.getEncryptedAssertions().get(0);
			
			StaticKeyInfoCredentialResolver keyInfoCredentialResolver = new StaticKeyInfoCredentialResolver(keyManager.getDefaultCredential());
			 
			Decrypter decrypter = new Decrypter(null, keyInfoCredentialResolver, new InlineEncryptedKeyResolver());
			decrypter.setRootInNewDocument(true);
			 
			Assertion assertion = decrypter.decrypt(encryptedAssertion);
			attritbuteStatement = assertion.getAttributeStatements().get(0);
		} else {
			attritbuteStatement = saml2Response.getAssertions().get(0).getAttributeStatements().get(0);
		}
		
		return attritbuteStatement;
	}
}
