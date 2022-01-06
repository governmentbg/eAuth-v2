package bg.bulsi.egov.saml;

import java.util.List;

import javax.xml.namespace.QName;
import org.opensaml.saml.common.SAMLObject;

public interface RequestedAttribute extends SAMLObject {

  /**
   * Local Name of Status.
   */
  String DEFAULT_ELEMENT_LOCAL_NAME = "RequestedAttribute";

  /**
   * Default element name.
   */
  QName DEFAULT_ELEMENT_NAME = new QName(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS,
      DEFAULT_ELEMENT_LOCAL_NAME,
      SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);

  /**
   * Local name of the XSI type.
   */
  String TYPE_LOCAL_NAME = "RequestedAttributeType";

  /**
   * QName of the XSI type.
   */
  QName TYPE_NAME = new QName(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS,
      TYPE_LOCAL_NAME,
      SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX
  );
  
  List<AttributeValue> getAttributeValues();
  void setAttributeValues(List<AttributeValue> attributeValues);
  
  String getName();
  void setName(String name);
  
  String getNameFormat();
  void setNameFormat(String nameFormat);
  
  String getFriendlyName();
  void setFriendlyName(String friendlyName);
  
  Boolean isRequired();
  void setRequired(Boolean isRequired);
}
