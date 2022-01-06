package bg.bulsi.egov.saml.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

import bg.bulsi.egov.saml.SAMLeAuthConstants;
import bg.bulsi.egov.saml.Service;

public class ServiceBuilder extends AbstractSAMLObjectBuilder<Service> {

  @Nonnull
  @Override
  public Service buildObject() {
    return this.buildObject(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS, Service.TYPE_LOCAL_NAME,
        SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);
  }

  @Nonnull
  @Override
  public Service buildObject(@Nullable String namespaceURI, @Nonnull String localName,
      @Nullable String namespacePrefix) {
    return new ServiceImpl(namespaceURI, localName, namespacePrefix);
  }
}
