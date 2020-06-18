package bg.bulsi.egov.saml.impl;


import bg.bulsi.egov.saml.LevelOfAssurance;
import bg.bulsi.egov.saml.Provider;
import bg.bulsi.egov.saml.RequestedService;
import bg.bulsi.egov.saml.Service;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;

public class RequestedServiceUnmarshaller extends AbstractSAMLObjectUnmarshaller {


  protected void processChildElement(final XMLObject parentSAMLObject,
      final XMLObject childSAMLObject)
      throws UnmarshallingException {
    RequestedService requestedService = (RequestedService) parentSAMLObject;

    if (childSAMLObject instanceof Provider) {
      requestedService.setProvider((Provider) childSAMLObject);
    } else if (childSAMLObject instanceof Service) {
      requestedService.setService((Service) childSAMLObject);
    } else if (childSAMLObject instanceof LevelOfAssurance) {
        requestedService.setLevelOfAssurance((LevelOfAssurance) childSAMLObject);
    } else {
      super.processChildElement(parentSAMLObject, childSAMLObject);
    }

  }
}
