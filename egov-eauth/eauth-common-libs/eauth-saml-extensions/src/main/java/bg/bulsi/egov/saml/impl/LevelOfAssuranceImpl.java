package bg.bulsi.egov.saml.impl;

import bg.bulsi.egov.saml.LevelOfAssurance;

public class LevelOfAssuranceImpl extends LevelOfAssuranceTypeImpl implements LevelOfAssurance {

  /**
   * Constructor.
   *
   * @param namespaceURI     the namespace the element is in
   * @param elementLocalName the local name of the XML element this Object represents
   * @param namespacePrefix  the prefix for the given namespace
   */
  protected LevelOfAssuranceImpl(String namespaceURI, String elementLocalName,
      String namespacePrefix) {
    super(namespaceURI, elementLocalName, namespacePrefix);
  }
}
