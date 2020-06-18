package bg.bulsi.egov.eauth.saml;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import bg.bulsi.egov.eauth.common.xml.MetadataBuilderFactoryUtil;

public class SAMLBuilder {


  public static Issuer buildIssuer(String issuingEntityName) {
    Issuer issuer = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Issuer.class);
    issuer.setValue(issuingEntityName);
    issuer.setFormat(NameIDType.ENTITY);
    return issuer;
  }

  private static Subject buildSubject(String subjectNameId, String subjectNameIdType,
                                      String recipient, String inResponseTo) {
    NameID nameID = MetadataBuilderFactoryUtil.buildXmlSAMLObject(NameID.class);
    nameID.setValue(subjectNameId);
    nameID.setFormat(subjectNameIdType);

    Subject subject = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Subject.class);
    subject.setNameID(nameID);

    SubjectConfirmation subjectConfirmation = MetadataBuilderFactoryUtil.buildXmlSAMLObject(SubjectConfirmation.class);
    subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

    SubjectConfirmationData subjectConfirmationData = MetadataBuilderFactoryUtil.buildXmlSAMLObject(SubjectConfirmationData.class);

    subjectConfirmationData.setRecipient(recipient);
    subjectConfirmationData.setInResponseTo(inResponseTo);
    subjectConfirmationData.setNotOnOrAfter(new DateTime().plusMinutes(8 * 60));

    subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

    subject.getSubjectConfirmations().add(subjectConfirmation);

    return subject;
  }

  public static Status buildStatus(String value) {
    Status status = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Status.class);
    StatusCode statusCode = MetadataBuilderFactoryUtil.buildXmlSAMLObject(StatusCode.class);
    statusCode.setValue(value);
    status.setStatusCode(statusCode);
    return status;
  }

  public static Status buildStatus(String value, String subStatus, String message) {
    Status status = buildStatus(value);

    StatusCode subStatusCode = MetadataBuilderFactoryUtil.buildXmlSAMLObject(StatusCode.class);
    subStatusCode.setValue(subStatus);
    status.getStatusCode().setStatusCode(subStatusCode);

    StatusMessage statusMessage = MetadataBuilderFactoryUtil.buildXmlSAMLObject(StatusMessage.class);
    statusMessage.setMessage(message);
    status.setStatusMessage(statusMessage);

    return status;
  }

  public static Assertion buildAssertion(SAMLPrincipal principal, Status status, String entityId) {
    Assertion assertion = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Assertion.class);

    if (status.getStatusCode().getValue().equals(StatusCode.SUCCESS) 
    		|| status.getStatusCode().getValue().equals(StatusCode.RESPONDER)) {
      // TODO: status responder logic
      Subject subject = buildSubject(principal.getNameID(), principal.getNameIDType(),
          principal.getAssertionConsumerServiceURL(), principal.getRequestID());
      assertion.setSubject(subject);
    }

    Issuer issuer = buildIssuer(entityId);

    Audience audience = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Audience.class);
    audience.setAudienceURI(principal.getServiceProviderEntityID());
    AudienceRestriction audienceRestriction = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AudienceRestriction.class);
    audienceRestriction.getAudiences().add(audience);

    Conditions conditions = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Conditions.class);
    conditions.setNotBefore(new DateTime().minusMinutes(3));
    conditions.setNotOnOrAfter(new DateTime().plusMinutes(3));
    conditions.getAudienceRestrictions().add(audienceRestriction);
    assertion.setConditions(conditions);

    AuthnStatement authnStatement = buildAuthnStatement(new DateTime(), entityId);

    assertion.setIssuer(issuer);
    assertion.getAuthnStatements().add(authnStatement);

    assertion.getAttributeStatements().add(buildAttributeStatement(principal.getAttributes()));

    assertion.setID(randomSAMLId());
    assertion.setIssueInstant(new DateTime());

    return assertion;
  }

  public static void signAssertion(
      SignableXMLObject signableXMLObject,
      Credential signingCredential)
      throws MarshallingException, SignatureException {

    Signature signature = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Signature.class);

    signature.setSigningCredential(signingCredential);
    signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);

    // TODO ---
    //Configuration.getGlobalSecurityConfiguration().getSignatureAlgorithmURI(signingCredential)
    // );

    signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

    signableXMLObject.setSignature(signature);

    XMLObjectProviderRegistrySupport.getMarshallerFactory()
        .getMarshaller(signableXMLObject).marshall(signableXMLObject);

    Signer.signObject(signature);
  }

  public static EncryptedAssertion encryptAssertion(Assertion assertion,
      Credential encryptingCredential)
      throws EncryptionException {

    DataEncryptionParameters encryptionParameters = new DataEncryptionParameters();
    encryptionParameters.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);

    KeyEncryptionParameters keyEncryptionParameters = new KeyEncryptionParameters();
    keyEncryptionParameters.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
    keyEncryptionParameters.setEncryptionCredential(encryptingCredential);

    Encrypter encrypter = new Encrypter(encryptionParameters, keyEncryptionParameters);
    encrypter.setKeyPlacement(Encrypter.KeyPlacement.INLINE);

    EncryptedAssertion encryptedAssertion = encrypter.encrypt(assertion);

    return encryptedAssertion;
  }

  public static Optional<String> getStringValueFromXMLObject(XMLObject xmlObj) {
    if (xmlObj instanceof XSString) {
      return Optional.ofNullable(((XSString) xmlObj).getValue());
    } else if (xmlObj instanceof XSAny) {
      XSAny xsAny = (XSAny) xmlObj;
      String textContent = xsAny.getTextContent();
      if (StringUtils.hasText(textContent)) {
        return Optional.of(textContent);
      }
      List<XMLObject> unknownXMLObjects = xsAny.getUnknownXMLObjects();
      if (!CollectionUtils.isEmpty(unknownXMLObjects)) {
        XMLObject xmlObject = unknownXMLObjects.get(0);
        if (xmlObject instanceof NameID) {
          NameID nameID = (NameID) xmlObject;
          return Optional.of(nameID.getValue());
        }
      }
    }
    return Optional.empty();
  }

  public static String randomSAMLId() {
    return "_" + UUID.randomUUID().toString();
  }

  private static AuthnStatement buildAuthnStatement(DateTime authnInstant, String entityID) {
    AuthnContextClassRef authnContextClassRef = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AuthnContextClassRef.class);
    authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);

    AuthenticatingAuthority authenticatingAuthority = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AuthenticatingAuthority.class);
    authenticatingAuthority.setURI(entityID);

    AuthnContext authnContext = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AuthnContext.class);
    authnContext.setAuthnContextClassRef(authnContextClassRef);
    authnContext.getAuthenticatingAuthorities().add(authenticatingAuthority);

    AuthnStatement authnStatement = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AuthnStatement.class);
    authnStatement.setAuthnContext(authnContext);

    authnStatement.setAuthnInstant(authnInstant);

    return authnStatement;

  }

  private static AttributeStatement buildAttributeStatement(List<SAMLAttribute> attributes) {
    AttributeStatement attributeStatement = MetadataBuilderFactoryUtil.buildXmlSAMLObject(AttributeStatement.class);

    attributes.forEach(entry ->
        attributeStatement.getAttributes().add(
            buildAttribute(
                entry.getName(),
                entry.getValues())));

    return attributeStatement;
  }

  private static Attribute buildAttribute(String name, List<String> values) {

    XSStringBuilder stringBuilder = (XSStringBuilder) XMLObjectProviderRegistrySupport
        .getBuilderFactory().getBuilder(XSString.TYPE_NAME);

    Attribute attribute = MetadataBuilderFactoryUtil.buildXmlSAMLObject(Attribute.class);
    attribute.setName(name);
    attribute.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
    List<XSString> xsStringList = values.stream().map(value -> {
      XSString stringValue = stringBuilder
          .buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
      stringValue.setValue(value);
      return stringValue;
    }).collect(toList());

    attribute.getAttributeValues().addAll(xsStringList);
    return attribute;
  }

}
