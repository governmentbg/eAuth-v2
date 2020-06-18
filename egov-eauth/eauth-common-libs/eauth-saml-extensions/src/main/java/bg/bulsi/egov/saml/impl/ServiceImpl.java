package bg.bulsi.egov.saml.impl;

import bg.bulsi.egov.saml.Service;

public class ServiceImpl extends ResourceOIDImpl implements Service {


  /**
   * Constructor.
   *
   * @param namespaceURI     the namespace the element is in
   * @param elementLocalName the local name of the XML element this Object represents
   * @param namespacePrefix  the prefix for the given namespace
   */
  protected ServiceImpl(String namespaceURI, String elementLocalName,
      String namespacePrefix) {
    super(namespaceURI, elementLocalName, namespacePrefix);
  }
}
