package bg.bulsi.egov.eauth.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

import bg.bulsi.egov.eauth.common.exceptions.EAuthMetadataException;
import bg.bulsi.egov.eauth.common.xml.MetadataBuilderFactoryUtil;
import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.metadata.samlobjects.SAMLMetadataCore;
import bg.bulsi.egov.eauth.metadata.samlobjects.SPType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EauthMetadataBuilder {
	// TODO add relevant properties to configuration
	// private transient Signature spSignature;
	// private transient Credential spEncryptionCredential;
	// private transient Credential spSigningCredential;
	private transient Signature idpSignature;
	private transient Credential idpEncryptionCredential;
	private transient Credential idpSigningCredential;
    public static final ImmutableMap<String, String> SIGNATURE_TO_DIGEST_ALGORITHM_MAP =
            ImmutableMap.<String, String>builder()
                    .put(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, SignatureConstants.ALGO_ID_DIGEST_SHA256)
                    .put(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384, SignatureConstants.ALGO_ID_DIGEST_SHA384)
                    .put(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512, SignatureConstants.ALGO_ID_DIGEST_SHA512)
                    // RIPEMD is allowed to verify
                    .put(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160,
                         SignatureConstants.ALGO_ID_DIGEST_RIPEMD160)
                    .put(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256, SignatureConstants.ALGO_ID_DIGEST_SHA256)
                    .put(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384, SignatureConstants.ALGO_ID_DIGEST_SHA384)
                    .put(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512, SignatureConstants.ALGO_ID_DIGEST_SHA512)
                    .put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1, SignatureConstants.ALGO_ID_DIGEST_SHA256)
                    .build();

	@Autowired
	private IdpConfigurationProperties idpConfigurationProperties;

	public Extensions generateExtensions() throws EAuthMetadataException {
		final Extensions eauthExtensions = MetadataBuilderFactoryUtil.generateMetadataExtension();

		generateEntityAttributes(eauthExtensions);
		generateSpType(eauthExtensions);
		generateDigest(eauthExtensions);
		generateSigningMethods(eauthExtensions);

		return eauthExtensions;
	}

	private void generateEntityAttributes(final Extensions eauthExtensions) throws EAuthMetadataException {
		EntityAttributes entityAttributes = (EntityAttributes) MetadataBuilderFactoryUtil
				.buildXmlObject(EntityAttributes.DEFAULT_ELEMENT_NAME);

		generateEauthProtocolVersionAttributes(entityAttributes);
		generateLoA(entityAttributes);

		if (!entityAttributes.getAttributes().isEmpty()) {
			eauthExtensions.getUnknownXMLObjects().add(entityAttributes);
		}

	}

	private void generateSpType(Extensions eauthExtensions) throws EAuthMetadataException {
		if (!StringUtils.isEmpty(idpConfigurationProperties.getSpType())) {
			final SPType spTypeObj = (SPType) MetadataBuilderFactoryUtil.buildXmlObject(SPType.DEF_ELEMENT_NAME);
			if (spTypeObj != null) {
				spTypeObj.setSPType(idpConfigurationProperties.getSpType());
				eauthExtensions.getUnknownXMLObjects().add(spTypeObj);
			} else {
				log.info("SAML METADATA EXCEPTION - error adding SPType extension");
			}
		}
	}

	private void generateDigest(final Extensions eauthExtensions) throws EAuthMetadataException {
		if (idpConfigurationProperties.getDigestMethods() != null && !idpConfigurationProperties.getDigestMethods().isEmpty()) {
			for (String digestMethod : idpConfigurationProperties.getDigestMethods()) {
				final DigestMethod dm = (DigestMethod) MetadataBuilderFactoryUtil
						.buildXmlObject(DigestMethod.DEFAULT_ELEMENT_NAME);
				if (dm != null) {
					dm.setAlgorithm(digestMethod);
					eauthExtensions.getUnknownXMLObjects().add(dm);
				} else {
					log.info("SAML METADATA EXCEPTION - error adding DigestMethod extension");
				}
			}
		}
	}

	private void generateSigningMethods(Extensions eauthExtensions) throws EAuthMetadataException {
		if (idpConfigurationProperties.getSigningMethods() != null && !idpConfigurationProperties.getSigningMethods().isEmpty()) {
			for (String signMethod : idpConfigurationProperties.getSigningMethods()) {
				final SigningMethod sm = (SigningMethod) MetadataBuilderFactoryUtil
						.buildXmlObject(SigningMethod.DEFAULT_ELEMENT_NAME);
				if (sm != null) {
					sm.setAlgorithm(signMethod);
					eauthExtensions.getUnknownXMLObjects().add(sm);
				} else {
					log.info("SAML METADATA EXCEPTION - error adding SigningMethod extension");
				}
			}
		}
	}

	private void generateEauthProtocolVersionAttributes(final EntityAttributes entityAttributes)
			throws EAuthMetadataException {
		final Namespace saml = new Namespace(SAMLConstants.SAML20_NS, SAMLConstants.SAML20_PREFIX);
		entityAttributes.getNamespaceManager().registerNamespaceDeclaration(saml);

		final Attribute eidasProtocolVersionAttribute = buildEauthProtocolVersionAttribute(
				SAMLMetadataCore.PROTOCOL_VERSION_URI.getValue(), idpConfigurationProperties.getEauthProtocolVersion());
		if (null == eidasProtocolVersionAttribute) {
			log.info("SAML METADATA EXCEPTION - eAuth Protocol Version Attribute is empty");
		} else {
			entityAttributes.getAttributes().add(eidasProtocolVersionAttribute);
		}

		final Attribute eidasApplicationIdentifierAttribute = buildEauthProtocolVersionAttribute(
				SAMLMetadataCore.APPLICATION_IDENTIFIER.getValue(), idpConfigurationProperties.getEauthProtocolVersion());
		if (null == eidasApplicationIdentifierAttribute) {
			log.info("SAML METADATA EXCEPTION - eAuth Application Identifier Attribute is empty");
		} else {
			entityAttributes.getAttributes().add(eidasApplicationIdentifierAttribute);
		}
	}

	private static Attribute buildEauthProtocolVersionAttribute(String name, String value)
			throws EAuthMetadataException {
		if (StringUtils.isNotEmpty(value)) {
			final Attribute attribute = (Attribute) MetadataBuilderFactoryUtil
					.buildXmlObject(Attribute.DEFAULT_ELEMENT_NAME);
			attribute.setNameFormat(RequestedAttribute.URI_REFERENCE);
			attribute.setName(name);

			final XSAny attributeValue = createEauthProtocolVersionAttributeValue(value);
			attribute.getAttributeValues().add(attributeValue);

			return attribute;
		} else {

			return null;
		}
	}

	private static XSAny createEauthProtocolVersionAttributeValue(String value) {
		final XSAnyBuilder builder = new XSAnyBuilder();
		final XSAny attributeValue = builder.buildObject(SAMLConstants.SAML20_NS,
				org.opensaml.saml.saml2.core.AttributeValue.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);

		final Namespace namespace = new Namespace(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
				net.shibboleth.utilities.java.support.xml.XMLConstants.XSI_PREFIX);
		attributeValue.getNamespaceManager().registerNamespaceDeclaration(namespace);

		final QName attribute_type = new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type",
				net.shibboleth.utilities.java.support.xml.XMLConstants.XSI_PREFIX);
		attributeValue.getUnknownAttributes().put(attribute_type,
				net.shibboleth.utilities.java.support.xml.XMLConstants.XSD_PREFIX + ":" + XSString.TYPE_LOCAL_NAME);

		attributeValue.setTextContent(value);
		return attributeValue;
	}

	@SuppressWarnings("squid:S2583")
	private void generateIDPSSODescriptor(final EntityDescriptor entityDescriptor,
			final X509KeyInfoGeneratorFactory keyInfoGeneratorFactory, ImmutableSortedSet<String> attributeNames)
			throws org.opensaml.security.SecurityException, IllegalAccessException, NoSuchFieldException,
			EAuthMetadataException {
		// the node has IDP role
		final String spSamlProtocol = SAMLConstants.SAML20P_NS;
		final String idpSamlProtocol = SAMLConstants.SAML20P_NS;
		IDPSSODescriptor idpSSODescriptor = MetadataBuilderFactoryUtil.buildXmlObject(IDPSSODescriptor.class);
		idpSSODescriptor.setWantAuthnRequestsSigned(true);
		if (idpSignature != null) {
			idpSSODescriptor.setSignature(idpSignature);
		}
		if (idpSigningCredential != null) {
			idpSSODescriptor.getKeyDescriptors()
					.add(getKeyDescriptor(keyInfoGeneratorFactory, idpSigningCredential, UsageType.SIGNING));
		}
		if (idpEncryptionCredential != null) {
			idpSSODescriptor.getKeyDescriptors()
					.add(getKeyDescriptor(keyInfoGeneratorFactory, idpEncryptionCredential, UsageType.ENCRYPTION));
		}
		idpSSODescriptor.addSupportedProtocol(idpSamlProtocol);
		fillNameIDFormat(idpSSODescriptor);
		idpSSODescriptor.getSingleSignOnServices().addAll(buildSingleSignOnServicesBindingLocations());
		generateSupportedAttributes(idpSSODescriptor, attributeNames);
		entityDescriptor.getRoleDescriptors().add(idpSSODescriptor);

	}

	private void fillNameIDFormat(SSODescriptor ssoDescriptor) throws EAuthMetadataException {
		NameIDFormat persistentFormat = (NameIDFormat) MetadataBuilderFactoryUtil
				.buildXmlObject(NameIDFormat.DEFAULT_ELEMENT_NAME);
		persistentFormat.setFormat(SAMLMetadataCore.SAML_NAME_ID_FORMAT_PERSISTENT.getValue());
		ssoDescriptor.getNameIDFormats().add(persistentFormat);
		NameIDFormat transientFormat = (NameIDFormat) MetadataBuilderFactoryUtil
				.buildXmlObject(NameIDFormat.DEFAULT_ELEMENT_NAME);
		transientFormat.setFormat(SAMLMetadataCore.SAML_NAME_ID_FORMAT_TRANSIENT.getValue());
		ssoDescriptor.getNameIDFormats().add(transientFormat);
		NameIDFormat unspecifiedFormat = (NameIDFormat) MetadataBuilderFactoryUtil
				.buildXmlObject(NameIDFormat.DEFAULT_ELEMENT_NAME);
		unspecifiedFormat.setFormat(SAMLMetadataCore.SAML_NAME_ID_FORMAT_UNSPECIFIED.getValue());
		ssoDescriptor.getNameIDFormats().add(unspecifiedFormat);
	}

	private void generateSupportedAttributes(IDPSSODescriptor idpssoDescriptor,
			ImmutableSortedSet<String> attributeNames) throws EAuthMetadataException {
		List<Attribute> attributes = idpssoDescriptor.getAttributes();
		for (String attributeName : attributeNames) {
			Attribute a = (Attribute) MetadataBuilderFactoryUtil.buildXmlObject(Attribute.DEFAULT_ELEMENT_NAME);
			a.setName(attributeName);
			// a.setFriendlyName(attributeDefinition.getFriendlyName());
			a.setNameFormat(Attribute.URI_REFERENCE);
			attributes.add(a);
		}
	}

	private KeyDescriptor getKeyDescriptor(X509KeyInfoGeneratorFactory keyInfoGeneratorFactory, Credential credential,
			UsageType usage)
			throws NoSuchFieldException, IllegalAccessException, EAuthMetadataException, SecurityException{
		KeyDescriptor keyDescriptor = null;
		Set<String> encryptionAlgorithms= idpConfigurationProperties.getEncryptionAlgorithms();
		if (credential != null) {
			keyDescriptor = MetadataBuilderFactoryUtil.buildXmlObject(KeyDescriptor.class);
			KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

			KeyInfo keyInfo = keyInfoGenerator.generate(credential);
			keyDescriptor.setUse(usage);
			keyDescriptor.setKeyInfo(keyInfo);
			if (usage == UsageType.ENCRYPTION && encryptionAlgorithms != null && !encryptionAlgorithms.isEmpty()) {
				for (String encryptionAlgo : encryptionAlgorithms) {
					EncryptionMethod em = (EncryptionMethod) MetadataBuilderFactoryUtil
							.buildXmlObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
					em.setAlgorithm(encryptionAlgo);
					keyDescriptor.getEncryptionMethods().add(em);
				}
			}

		}
		return keyDescriptor;
	}

	private ArrayList<SingleSignOnService> buildSingleSignOnServicesBindingLocations()
			throws NoSuchFieldException, IllegalAccessException {
		ArrayList<SingleSignOnService> singleSignOnServices = new ArrayList<>();

		HashMap<String, String> bindingLocations =idpConfigurationProperties.getProtocolBindingLocation();
		Iterator<Map.Entry<String, String>> bindLocs = bindingLocations.entrySet().iterator();
		while (bindLocs.hasNext()) {
			Map.Entry<String, String> bindingLoc = bindLocs.next();
			SingleSignOnService ssos = MetadataBuilderFactoryUtil.buildXmlObject(SingleSignOnService.class);
			ssos.setBinding(bindingLoc.getKey());
			ssos.setLocation(bindingLoc.getValue());
			singleSignOnServices.add(ssos);
		}
		return singleSignOnServices;
	}

	private void generateLoA(EntityAttributes entityAttributes) throws EAuthMetadataException {
		Attribute loaAttrib = (Attribute) MetadataBuilderFactoryUtil.buildXmlObject(Attribute.DEFAULT_ELEMENT_NAME);
		loaAttrib.setName(SAMLMetadataCore.LEVEL_OF_ASSURANCE_NAME.getValue());
		loaAttrib.setNameFormat(Attribute.URI_REFERENCE);
		/*if (idpConfigurationProperties.isHideLoaType()) {
			XSAnyBuilder builder = new XSAnyBuilder();
			XSAny stringValue = builder.buildObject(SAMLConstants.SAML20_NS,
					org.opensaml.saml.saml2.core.AttributeValue.DEFAULT_ELEMENT_LOCAL_NAME,
					SAMLConstants.SAML20_PREFIX);
			stringValue.setTextContent(levelOfAssurance);
			loaAttrib.getAttributeValues().add(stringValue);
		} else {
			XSStringBuilder stringBuilder = (XSStringBuilder) XMLObjectProviderRegistrySupport.getBuilderFactory()
					.getBuilder(XSString.TYPE_NAME);
			XSString stringValue = stringBuilder
					.buildObject(org.opensaml.saml.saml2.core.AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
			stringValue.setValue(levelOfAssurance);
			loaAttrib.getAttributeValues().add(stringValue);
		}*/
		entityAttributes.getAttributes().add(loaAttrib);
	}

}
