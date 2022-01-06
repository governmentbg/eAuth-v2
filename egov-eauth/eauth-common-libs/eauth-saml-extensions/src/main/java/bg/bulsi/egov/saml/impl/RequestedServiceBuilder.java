package bg.bulsi.egov.saml.impl;


import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

import bg.bulsi.egov.saml.RequestedService;
import bg.bulsi.egov.saml.SAMLeAuthConstants;

public class RequestedServiceBuilder extends AbstractSAMLObjectBuilder<RequestedService> {

  @Override
  public RequestedService buildObject() {
    return this.buildObject(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS, RequestedService.TYPE_LOCAL_NAME,
        SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);
  }

  @Override
  public RequestedService buildObject(String namespaceURI, String localName,
      String namespacePrefix) {
    return new RequestedServiceImpl(namespaceURI, localName, namespacePrefix);
  }
}
