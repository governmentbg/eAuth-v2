package bg.bulsi.egov.saml;


import java.util.List;

import javax.xml.namespace.QName;
import org.opensaml.saml.common.SAMLObject;

public interface RequestedAttributes extends SAMLObject {

  /**
   * Local Name of Status.
   */
  String DEFAULT_ELEMENT_LOCAL_NAME = "RequestedAttributes";

  /**
   * Default element name.
   */
  QName DEFAULT_ELEMENT_NAME = new QName(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS,
      DEFAULT_ELEMENT_LOCAL_NAME,
      SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);

  /**
   * Local name of the XSI type.
   */
  String TYPE_LOCAL_NAME = "RequestedAttributesType";

  /**
   * QName of the XSI type.
   */
  QName TYPE_NAME = new QName(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS,
      TYPE_LOCAL_NAME,
      SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX
  );

  List<RequestedAttribute> getRequestedAttributes();

  void setRequestedAttributes(List<RequestedAttribute> requestedAttributes);

}
