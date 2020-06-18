package bg.bulsi.egov.saml.config;

import org.opensaml.core.xml.config.AbstractXMLObjectProviderInitializer;

public class XMLObjectProviderInitializer extends AbstractXMLObjectProviderInitializer {

  /**
   * Config resources.
   */
  private static String[] configs = {
      "/eauth-config.xml"
  };

  @Override
  protected String[] getConfigResources() {
    return configs;
  }
}
