package bg.bulsi.egov.saml.impl;

import org.opensaml.core.xml.schema.impl.XSStringImpl;

import bg.bulsi.egov.saml.ResourceOID;

public class ResourceOIDImpl extends XSStringImpl implements ResourceOID {

  /**
   * Constructor.
   *
   * @param namespaceURI     the namespace the element is in
   * @param elementLocalName the local name of the XML element this Object represents
   * @param namespacePrefix  the prefix for the given namespace
   */
  protected ResourceOIDImpl(String namespaceURI, String elementLocalName,
      String namespacePrefix) {
    super(namespaceURI, elementLocalName, namespacePrefix);
  }
}
