package bg.bulsi.egov.saml;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSString;


public interface AttributeValueType extends XSString {

  /**
   * Local name of the XSI type.
   */
  String TYPE_LOCAL_NAME = "AttributeValueType";

  /**
   * QName of the XSI type.
   */
  QName TYPE_NAME = new QName(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS,
      TYPE_LOCAL_NAME,
      SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX
  );

}
