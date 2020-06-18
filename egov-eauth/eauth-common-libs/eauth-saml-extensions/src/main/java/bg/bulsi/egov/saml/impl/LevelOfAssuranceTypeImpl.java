package bg.bulsi.egov.saml.impl;

import org.opensaml.core.xml.schema.impl.XSStringImpl;

import bg.bulsi.egov.saml.LevelOfAssuranceType;

public class LevelOfAssuranceTypeImpl extends XSStringImpl implements LevelOfAssuranceType {

  /**
   * Constructor.
   *
   * @param namespaceURI     the namespace the element is in
   * @param elementLocalName the local name of the XML element this Object represents
   * @param namespacePrefix  the prefix for the given namespace
   */
  protected LevelOfAssuranceTypeImpl(String namespaceURI, String elementLocalName,
      String namespacePrefix) {
    super(namespaceURI, elementLocalName, namespacePrefix);
  }
}
