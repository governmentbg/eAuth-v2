package bg.bulsi.egov.saml;


import javax.xml.namespace.QName;
import org.opensaml.saml.common.SAMLObject;

public interface Provider extends ResourceOID, SAMLObject {

  /**
   * Local Name of Status.
   */
  String DEFAULT_ELEMENT_LOCAL_NAME = "Provider";

  /**
   * Default element name.
   */
  QName DEFAULT_ELEMENT_NAME = new QName(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS,
      DEFAULT_ELEMENT_LOCAL_NAME,
      SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);

  /**
   * Local name of the XSI type.
   */
  String TYPE_LOCAL_NAME = "ResourceOID";

  /**
   * QName of the XSI type.
   */
  QName TYPE_NAME = new QName(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS,
      TYPE_LOCAL_NAME,
      SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX
  );
}
