package bg.bulsi.egov.saml.impl;

import bg.bulsi.egov.saml.AttributeValue;

public class AttributeValueImpl extends AttributeValueTypeImpl implements AttributeValue {

  /**
   * Constructor.
   *
   * @param namespaceURI     the namespace the element is in
   * @param elementLocalName the local name of the XML element this Object represents
   * @param namespacePrefix  the prefix for the given namespace
   */
  protected AttributeValueImpl(String namespaceURI, String elementLocalName,
      String namespacePrefix) {
    super(namespaceURI, elementLocalName, namespacePrefix);
  }
}
